from typing import List
from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession
from app.model.report import Report
from app.model.call import Call
from app.schema.report import (
    ReportResponse,
    CreateReportRequest,
    ReportSummaryResponse,
    ReportScriptResponse,
    ScriptItem,
    ReportExpressionsResponse,
)
from app.exception.custom_exceptions import APIException
from app.exception.error_code import Error
from app.crud import native_expression
from app.util.datetime_utils import to_kst_date_str


# 회원 ID로 리포트 목록 조회
async def get_reports_by_member_id(
    db: AsyncSession, member_id: int
) -> List[ReportResponse]:
    query = (
        select(Report)
        .where(Report.member_id == member_id)
        .order_by(Report.created_at.desc())
    )
    result = await db.execute(query)
    reports = result.scalars().all()

    # 리포트 응답 객체 리스트로 변환
    report_responses = []
    for report in reports:
        report_responses.append(
            ReportResponse(
                reportId=report.report_id,
                callDuration=report.call_duration,
                celebVideoUrl=report.celeb_video_url,
                wordCount=report.word_count,
                sentenceCount=report.sentence_count,
                communicationSummary=report.communication_summary,
                feedbackSummary=report.feedback_summary,
                createdAt=to_kst_date_str(report.created_at),
            )
        )

    return report_responses


# 새 리포트 생성
async def create_report(
    db: AsyncSession, request: CreateReportRequest
) -> ReportResponse:
    report = Report(
        member_id=request.memberId,
        call_id=request.callId,
        call_duration=request.callDuration,
        celeb_video_url=request.celebVideoUrl,
        word_count=request.wordCount,
        sentence_count=request.sentenceCount,
        communication_summary=request.communicationSummary,
        feedback_summary=request.feedbackSummary,
    )

    db.add(report)
    await db.commit()
    await db.refresh(report)

    return ReportResponse(
        reportId=report.report_id,
        callDuration=report.call_duration,
        celebVideoUrl=report.celeb_video_url,
        wordCount=report.word_count,
        sentenceCount=report.sentence_count,
        communicationSummary=report.communication_summary,
        feedbackSummary=report.feedback_summary,
        createdAt=to_kst_date_str(report.created_at),
    )


# 리포트 요약 조회
async def get_report_summary(db: AsyncSession, report_id: int) -> ReportSummaryResponse:
    report = await db.get(Report, report_id)

    if not report:
        raise APIException(404, Error.REPORT_NOT_FOUND)

    return ReportSummaryResponse(
        callDuration=report.call_duration,
        wordCount=report.word_count,
        sentenceCount=report.sentence_count,
        communicationSummary=report.communication_summary,
        feedbackSummary=report.feedback_summary,
        createdAt=to_kst_date_str(report.created_at),
    )


# 리포트 스크립트 조회
async def get_report_script(db: AsyncSession, report_id: int) -> ReportScriptResponse:
    # 리포트 조회
    report = await db.get(Report, report_id)

    if not report:
        raise APIException(404, Error.REPORT_NOT_FOUND)

    # 해당 리포트와 연결된 통화 조회
    call_id = report.call_id
    result = await db.execute(select(Call).where(Call.call_id == call_id))
    call = result.scalars().first()

    if not call or not call.messages:
        return ReportScriptResponse(script=[])

    # 통화 메시지를 스크립트 아이템으로 변환
    script_items = []
    for message in call.messages:
        is_ai = message.get("type") == "ai"
        content_kor = message.get("content_kor")
        if content_kor is None:
            content_kor = ""

        script_items.append(
            ScriptItem(
                isAI=is_ai,
                content=message.get("content", ""),
                contentKor=content_kor,
                timestamp=message.get("timestamp", ""),
            )
        )

    return ReportScriptResponse(script=script_items)


# 리포트 원어민 표현 조회
async def get_report_expressions(
    db: AsyncSession, report_id: int
) -> ReportExpressionsResponse:
    # 리포트 조회
    report = await db.get(Report, report_id)

    if not report:
        raise APIException(404, Error.REPORT_NOT_FOUND)

    # DB에서 원어민 표현 조회
    expressions_db = await native_expression.get_native_expressions_by_report_id(
        db=db, report_id=report_id
    )

    # DTO로 변환
    expressions = []
    for i, expr in enumerate(expressions_db, 1):
        expressions.append(native_expression.convert_to_native_expression_item(expr, i))

    # 저장된 표현이 없으면 빈 리스트 반환
    return ReportExpressionsResponse(nativeExpressions=expressions)
