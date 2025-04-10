# app/service/call.py
import asyncio
import logging

from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import text
from app.graph.call_graph import (
    build_add_message_graph,
    build_meaningful_messages_graph,
    build_start_call_graph,
)
from app.graph.node.memory import convert_to_lc_message, safe_convert_message_to_dict
from app.rag.store import store_meaningful_messages
from app.service.report import generate_report
from app.util.datetime_utils import now_kst, to_kst, to_kst_isoformat
from app.util.message_utils import append_messages_to_call
from app.exception.custom_exceptions import APIException
from app.exception.error_code import Error
from app.crud.call import get_call_by_id, save_call
from app.crud.news import get_random_news
from app.crud.member import get_member_by_id
from app.model.call import Call
from app.schema.call import (
    AIMessageResponse,
    StartCallRequest,
    StartCallResponse,
    Message,
    UserMessageRequest,
    EndCallResponse,
)

logger = logging.getLogger(__name__)

start_call_graph = build_start_call_graph()
add_message_graph = build_add_message_graph()
get_build_meaningful_messages_graph = build_meaningful_messages_graph()


async def start_call(
    db: AsyncSession,
    request: StartCallRequest,
    member_id: int,
    voice_name: str,
    type: str,
) -> StartCallResponse:
    # member 정보 조회
    member = await get_member_by_id(db, member_id)
    if not member:
        raise APIException(404, Error.MEMBER_NOT_FOUND)

    # 자유 주제(topic = None)인 경우 뉴스/날씨 데이터로 topic 추출
    topic = request.topic
    if not topic:
        news = await get_random_news(db, category="날씨")  # 사회, 생활, 날씨, 경제, IT
        if news:
            # 뉴스 제목과 내용을 함께 제공해서 AI가 대화하기 좋은 주제를 선택할 수 있게 함
            topic = f"뉴스 제목: {news.title}\n\n뉴스 내용: {news.content}"
        else:
            topic = "일상 생활과 취미"  # 뉴스가 없는 경우 간단한 주제 사용

    state = {
        "call_id": -1,
        "member_id": member_id,
        "topic": topic,
        "messages": [],
        "voice_name": voice_name,
        "voice_type": type,
        "member_interest": member.interest,
    }

    try:
        result = await start_call_graph.ainvoke(state)
    except Exception:
        raise APIException(500, Error.CALL_AI_FAILED)

    ai_message = result["messages"][-1]
    new_call = Call(
        member_id=request.memberId,
        messages=[safe_convert_message_to_dict(ai_message)],
        start_time=now_kst(),
        end_time=None,
    )

    await save_call(db, new_call)

    return StartCallResponse(
        calld=new_call.call_id,
        startTime=to_kst_isoformat(new_call.start_time),
        aiMessage=result.get("ai_response"),
        aiMessageKor=result.get("ai_response_kor"),
        aiAudioUrl=result.get("ai_audio_url"),
    )


async def add_message_to_call(
    db: AsyncSession,
    call_id: int,
    request: UserMessageRequest,
    member_id: int,
    voice_name: str,
    type: str,
) -> AIMessageResponse:
    call_record = await get_call_by_id(db, call_id)
    if not call_record:
        raise APIException(404, Error.CALL_NOT_FOUND)
    if call_record.end_time:
        raise APIException(400, Error.CALL_ALREADY_ENDED)

    # member 정보 조회
    member = await get_member_by_id(db, member_id)
    if not member:
        raise APIException(404, Error.MEMBER_NOT_FOUND)

    # 시간 초과 여부 확인
    duration = int((now_kst() - to_kst(call_record.start_time)).total_seconds())
    is_timeout = duration >= 300  # 5분

    state = {
        "call_id": call_record.call_id,
        "member_id": member_id,
        "input": request.userMessage,
        "messages": [convert_to_lc_message(Message(**m)) for m in call_record.messages],
        "is_timeout": is_timeout,
        "voice_name": voice_name,
        "voice_type": type,
        "member_interest": member.interest,
    }

    try:
        result = await add_message_graph.ainvoke(state)
    except Exception:
        raise APIException(500, Error.CALL_INTERNAL_ERROR)

    append_messages_to_call(
        call_record, [safe_convert_message_to_dict(m) for m in result["messages"][-2:]]
    )

    # 통화 종료 조건이면 end_time 처리
    should_end = result.get("should_end_call") or is_timeout

    print(f"📌 is_timeout = {is_timeout}")
    print(f"📌 should_end_call = {should_end}")

    if should_end:
        end_time = now_kst()
        call_record.duration = duration
        call_record.end_time = end_time
        call_record.updated_at = end_time

        await save_call(db, call_record)

        # "human" 메시지의 content 길이 합산
        total_human_content_length = sum(
            len(m.get("content", ""))
            for m in call_record.messages
            if m.get("type") == "human"
        )

        # 조건에 따라 reportCreated 설정
        reportCreated = total_human_content_length > 10

        # member의 통화 시간 업데이트 - raw SQL 사용
        try:
            # total_report_count는 reportCreated가 true일 때만 증가
            update_sql = """
                UPDATE member 
                SET total_call_duration = COALESCE(total_call_duration, 0) + :duration
                WHERE member_id = :member_id
            """

            # reportCreated가 true인 경우에만 total_report_count 증가 포함
            if reportCreated:
                update_sql = """
                    UPDATE member 
                    SET total_report_count = COALESCE(total_report_count, 0) + 1,
                        total_call_duration = COALESCE(total_call_duration, 0) + :duration
                    WHERE member_id = :member_id
                """

            await db.execute(
                text(update_sql),
                {"member_id": call_record.member_id, "duration": duration},
            )
            await db.commit()

            # 로그 메시지 조건부 변경
            if reportCreated:
                logger.info(
                    f"Member {call_record.member_id}의 total_report_count를 증가시키고 total_call_duration에 {duration}을 추가했습니다."
                )
            else:
                logger.info(
                    f"Member {call_record.member_id}의 total_call_duration에 {duration}을 추가했습니다."
                )
        except Exception as e:
            logger.error(f"멤버 통계 업데이트 실패: {str(e)}")
            # 이 오류로 인해 전체 흐름이 중단되지 않도록 pass
            pass

        # 의미 있는 메시지 + 태그 벡터 DB 저장
        try:
            # LangGraph 그래프 실행을 비동기 태스크로 실행
            task = asyncio.create_task(
                get_build_meaningful_messages_graph.ainvoke(state)
            )

            # 나중에 await하지 않고 백그라운드 실행되도록 처리
            async def handle_background():
                try:
                    result_meaningful_messages = await task
                    meaningful_messages = result_meaningful_messages.get(
                        "meaningful_messages", []
                    )

                    print(f"⭐ meaningful_messages = {meaningful_messages}")

                    if meaningful_messages:
                        await store_meaningful_messages(
                            call_id=call_id,
                            member_id=call_record.member_id,
                            messages=meaningful_messages,
                        )
                except Exception as e:
                    print("⚠️ Background error in meaningful message saving:", e)

            asyncio.create_task(handle_background())

        except Exception:
            raise APIException(500, Error.CALL_INTERNAL_ERROR)

        if reportCreated:
            # 리포트 생성
            asyncio.create_task(
                generate_report(
                    db=db,
                    member_id=call_record.member_id,
                    call_id=call_record.call_id,
                    duration=duration,
                    messages=[Message(**m) for m in call_record.messages],
                )
            )

        return AIMessageResponse(
            aiMessage=result["ai_response"],
            aiMessageKor=result["ai_response_kor"],
            endTime=to_kst_isoformat(end_time),
            duration=duration,
            reportCreated=reportCreated,
        )

    # 일반 응답
    await save_call(db, call_record)
    return AIMessageResponse(
        aiMessage=result["ai_response"], aiMessageKor=result["ai_response_kor"]
    )


async def end_call(db: AsyncSession, call_id: int) -> EndCallResponse:
    call_record = await get_call_by_id(db, call_id)

    if not call_record:
        raise APIException(404, Error.CALL_NOT_FOUND)

    if call_record.end_time:
        raise APIException(400, Error.CALL_ALREADY_ENDED)

    end_time = now_kst()
    duration = int((end_time - to_kst(call_record.start_time)).total_seconds())

    call_record.duration = duration
    call_record.end_time = end_time
    call_record.updated_at = end_time

    await save_call(db, call_record)

    # "human" 메시지의 content 길이 합산
    total_human_content_length = sum(
        len(m.get("content", ""))
        for m in call_record.messages
        if m.get("type") == "human"
    )

    # 조건에 따라 reportCreated 설정
    reportCreated = total_human_content_length > 10

    # member의 통화 시간 업데이트 - raw SQL 사용
    try:
        # total_report_count는 reportCreated가 true일 때만 증가
        update_sql = """
            UPDATE member 
            SET total_call_duration = COALESCE(total_call_duration, 0) + :duration
            WHERE member_id = :member_id
        """

        # reportCreated가 true인 경우에만 total_report_count 증가 포함
        if reportCreated:
            update_sql = """
                UPDATE member 
                SET total_report_count = COALESCE(total_report_count, 0) + 1,
                    total_call_duration = COALESCE(total_call_duration, 0) + :duration
                WHERE member_id = :member_id
            """

        await db.execute(
            text(update_sql),
            {"member_id": call_record.member_id, "duration": duration},
        )
        await db.commit()

        # 로그 메시지 조건부 변경
        if reportCreated:
            logger.info(
                f"Member {call_record.member_id}의 total_report_count를 증가시키고 total_call_duration에 {duration}을 추가했습니다."
            )
        else:
            logger.info(
                f"Member {call_record.member_id}의 total_call_duration에 {duration}을 추가했습니다."
            )
    except Exception as e:
        logger.error(f"멤버 통계 업데이트 실패: {str(e)}")
        # 이 오류로 인해 전체 흐름이 중단되지 않도록 pass
        pass

    # 의미 있는 메시지 + 태그 벡터 DB 저장
    state = {
        "call_id": call_record.call_id,
        "member_id": call_record.member_id,
        "messages": [convert_to_lc_message(Message(**m)) for m in call_record.messages],
    }

    try:
        # LangGraph 그래프 실행을 비동기 태스크로 실행
        task = asyncio.create_task(get_build_meaningful_messages_graph.ainvoke(state))

        # 나중에 await하지 않고 백그라운드 실행되도록 처리
        async def handle_background():
            try:
                result_meaningful_messages = await task
                meaningful_messages = result_meaningful_messages.get(
                    "meaningful_messages", []
                )

                print(f"⭐ meaningful_messages = {meaningful_messages}")

                if meaningful_messages:
                    await store_meaningful_messages(
                        call_id=call_id,
                        member_id=call_record.member_id,
                        messages=meaningful_messages,
                    )
            except Exception as e:
                print("⚠️ Background error in meaningful message saving:", e)

        asyncio.create_task(handle_background())

    except Exception:
        raise APIException(500, Error.CALL_INTERNAL_ERROR)

    if reportCreated:
        # 리포트 생성
        asyncio.create_task(
            generate_report(
                db=db,
                member_id=call_record.member_id,
                call_id=call_record.call_id,
                duration=duration,
                messages=[Message(**m) for m in call_record.messages],
            )
        )

    return EndCallResponse(
        endTime=to_kst_isoformat(end_time),
        duration=duration,
        reportCreated=reportCreated,
    )
