# app/service/report.py
import re
import logging
from app.crud.report import create_report
from app.graph import report_graph
from app.rag.store import store_meaningful_messages
from app.schema.call import Message
from app.schema.report import CreateReportRequest
from app.service.native_expression import save_native_expressions
from sqlalchemy import text
from app.service.voice import get_voice_by_call_id

logger = logging.getLogger(__name__)

create_report_graph = report_graph.build_report_graph()


async def generate_report(
    db,
    member_id: int,
    call_id: int,
    duration: int,
    messages: list[Message],
):
    try:
        # 사용자 이름 조회
        try:
            result = await db.execute(
                text(
                    """
                    SELECT name FROM member 
                    WHERE member_id = :member_id
                """
                ),
                {"member_id": member_id},
            )
            member_name = result.scalar() or "사용자"
            logger.info(f"Member {member_id}의 이름을 조회했습니다: {member_name}")
        except Exception as e:
            logger.error(f"사용자 이름 조회 실패: {str(e)}")
            member_name = "사용자"  # 기본값 설정

        # 통화에 사용된 음성 정보 조회
        voice = None
        voice_id = None
        voice_name = "English Tutor"  # 기본값 설정
        try:
            voice = await get_voice_by_call_id(db, call_id)
            voice_id = voice.voice_id
            voice_name = voice.voice_name
            logger.info(
                f"Call {call_id}의 음성 정보를 조회했습니다: ID={voice_id}, 이름={voice_name}"
            )
        except Exception as e:
            logger.error(f"음성 정보 조회 실패: {str(e)}")

        # 상태 구성
        state = {
            "member_id": member_id,
            "member_name": member_name,  # 사용자 이름 추가
            "voice_name": voice_name,  # 음성 이름 추가
            "voice_id": voice_id,  # 음성 ID 추가
            "call_id": call_id,
            "duration": duration,
            "messages": messages,
        }

        # LangGraph 실행
        result = await create_report_graph.ainvoke(state)

        # member_name이 result에 없으면 추가 (그래프 처리 과정에서 유실 방지)
        if "member_name" not in result:
            result["member_name"] = member_name
            logger.info(f"📌 result에 member_name({member_name})을 추가했습니다.")

        # voice_name이 result에 없으면 추가
        if "voice_name" not in result:
            result["voice_name"] = voice_name
            logger.info(f"📌 result에 voice_name({voice_name})을 추가했습니다.")

        # voice_id가 result에 없으면 추가
        if "voice_id" not in result:
            result["voice_id"] = voice_id
            logger.info(f"📌 result에 voice_id({voice_id})을 추가했습니다.")

        # 사용자 메시지 기반 단어 수
        word_count = sum(
            len(m.content.split()) for m in messages if m.type == "human" and m.content
        )

        # 사용자 메시지 기반 문장 수 ('.', '!', '?' 기준으로 분리)
        sentence_count = sum(
            len(re.findall(r"[.!?]", m.content))
            for m in messages
            if m.type == "human" and m.content
        )

        # 리포트 결과에서 사용자 이름이 제대로 사용되었는지 확인
        summary = result.get("summary", "")
        feedback = result.get("feedback", "")

        # 결과에 "사용자님"이 있는지 확인
        if "사용자님" in summary or "사용자님" in feedback:
            logger.warning(
                f"⚠️ 리포트에 '사용자님'이 포함되어 있습니다. 수정이 필요합니다."
            )
            # 사용자님을 member_name님으로 교체
            result["summary"] = summary.replace("사용자님", f"{member_name}님")
            result["feedback"] = feedback.replace("사용자님", f"{member_name}님")
            logger.info(
                f"🔄 리포트에서 '사용자님'을 '{member_name}님'으로 교체했습니다."
            )

        # 결과에 "English Tutor"나 "Tutor"가 있는지 확인
        if (
            "English Tutor" in summary
            or "English Tutor" in feedback
            or "Tutor" in summary
            or "Tutor" in feedback
        ):
            logger.warning(
                f"⚠️ 리포트에 'English Tutor' 또는 'Tutor'가 포함되어 있습니다. 수정이 필요합니다."
            )
            # "English Tutor"와 "Tutor"를 voice_name으로 교체
            result["summary"] = summary.replace("English Tutor", voice_name).replace(
                "Tutor", voice_name
            )
            result["feedback"] = feedback.replace("English Tutor", voice_name).replace(
                "Tutor", voice_name
            )
            logger.info(
                f"🔄 리포트에서 'English Tutor'/'Tutor'를 '{voice_name}'으로 교체했습니다."
            )

        # 결과 저장 처리
        await save_report_result(db, result, word_count, sentence_count)

        # # 의미 있는 메시지 + 태그 벡터 DB 저장
        # meaningful_messages = result.get("meaningful_messages", [])

        # print(f"⭐ meaningful_messages = {meaningful_messages}")

        # if meaningful_messages:
        #     await store_meaningful_messages(
        #         call_id=call_id,
        #         member_id=member_id,
        #         messages=meaningful_messages,
        #     )

    except Exception as e:
        logger.error(f"📉 리포트 생성 실패: {e}")


async def save_report_result(
    db, state: dict, word_count: int = 0, sentence_count: int = 0
):
    # 사용자 이름 확인 (디버깅용)
    member_name = state.get("member_name", "사용자")
    voice_name = state.get("voice_name", "English Tutor")

    logger.info(
        f"💾 리포트 저장 시 사용자 이름: '{member_name}', 음성 이름: '{voice_name}'"
    )

    # 혹시 남아있는 {member_name} 템플릿 문자열 처리
    summary = state.get("summary", "요약 없음")
    feedback = state.get("feedback", "피드백 없음")

    # 영어 레벨 가져오기 (parse_report에서 추출된 값)
    english_level = state.get("english_level", "중")  # 기본값은 중
    logger.info(f"🎯 영어 레벨: {english_level}")

    # voice_id 가져오기 (generate_report에서 이미 조회)
    voice_id = state.get("voice_id")
    logger.info(f"🎤 Voice ID: {voice_id}")

    # 셀럽 비디오 URL 설정
    celeb_video_url = None
    if voice_id and english_level:
        level_mapping = {"상": "advanced", "중": "intermediate", "하": "low"}
        level_code = level_mapping.get(english_level)
        if level_code:
            celeb_video_url = f"https://dlxayir1dj7sa.cloudfront.net/celeb-video/{voice_id}_{level_code}.mp4"
            logger.info(f"🎬 셀럽 비디오 URL 설정: {celeb_video_url}")

    # 템플릿 변수가 남아 있으면 실제 이름으로 교체
    if "{member_name}" in summary:
        summary = summary.replace("{member_name}", member_name)
    if "{member_name}" in feedback:
        feedback = feedback.replace("{member_name}", member_name)

    # 템플릿 변수 {voice_name}이 남아 있으면 실제 이름으로 교체
    if "{voice_name}" in summary:
        summary = summary.replace("{voice_name}", voice_name)
    if "{voice_name}" in feedback:
        feedback = feedback.replace("{voice_name}", voice_name)

    report_request = CreateReportRequest(
        memberId=state["member_id"],
        callId=state["call_id"],
        callDuration=state["duration"],
        celebVideoUrl=celeb_video_url,
        wordCount=word_count,
        sentenceCount=sentence_count,
        communicationSummary=summary,
        feedbackSummary=feedback,
    )

    # DB에 리포트 저장
    new_report = await create_report(db=db, request=report_request)

    # 원어민 표현 DB 저장 - 새로운 데이터베이스 세션을 사용하여 충돌 방지
    native_expressions = state.get("native_expressions", [])
    await save_native_expressions(
        db=db, report_id=new_report.reportId, expressions=native_expressions
    )
