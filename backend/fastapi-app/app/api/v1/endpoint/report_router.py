from fastapi import APIRouter, Query, Depends
from sqlalchemy.ext.asyncio import AsyncSession
from app.api.v1.endpoint.report import (
    get_reports_endpoint,
    get_report_summary_endpoint,
    get_report_script_endpoint,
    get_report_expressions_endpoint,
)
from app.schema.report import (
    ReportResponse,
    ReportSummaryResponse,
    ReportScriptResponse,
    ReportExpressionsResponse,
)
from app.schema.common import APIResponse
from app.exception.error_code import Error
from app.db.session import get_db
from typing import List
from app.core.base_router import BaseRouter

router = BaseRouter(prefix="/api/reports", tags=["리포트"])

router.api_doc(
    path="",
    endpoint=get_reports_endpoint,
    request_model=None,
    response_model=APIResponse[List[ReportResponse]],
    success_model=APIResponse[List[ReportResponse]],
    errors={
        404: {
            "message": Error.REPORT_NOT_FOUND.message,
            "code": Error.REPORT_NOT_FOUND.code,
        },
        500: {
            "message": Error.REPORT_INTERNAL_ERROR.message,
            "code": Error.REPORT_INTERNAL_ERROR.code,
        },
    },
    summary="📊 리포트 목록 조회",
    description="사용자 ID를 기반으로 해당 사용자의 모든 리포트 목록을 조회합니다.",
)


async def get_reports(
    member_id: int = Query(..., description="회원 ID"),
    db: AsyncSession = Depends(get_db),
):
    return await get_reports_endpoint(member_id, db)


router.api_doc(
    path="/{report_id}/summary",
    endpoint=get_report_summary_endpoint,
    request_model=None,
    response_model=APIResponse[ReportSummaryResponse],
    success_model=APIResponse[ReportSummaryResponse],
    errors={
        404: {
            "message": Error.REPORT_NOT_FOUND.message,
            "code": Error.REPORT_NOT_FOUND.code,
        },
        500: {
            "message": Error.REPORT_INTERNAL_ERROR.message,
            "code": Error.REPORT_INTERNAL_ERROR.code,
        },
    },
    summary="📝 리포트 요약 조회",
    description="특정 리포트의 요약 정보를 조회합니다.",
)


async def get_report_summary(report_id: int, db: AsyncSession = Depends(get_db)):
    return await get_report_summary_endpoint(report_id, db)


router.api_doc(
    path="/{report_id}/script",
    endpoint=get_report_script_endpoint,
    request_model=None,
    response_model=APIResponse[ReportScriptResponse],
    success_model=APIResponse[ReportScriptResponse],
    errors={
        404: {
            "message": Error.REPORT_NOT_FOUND.message,
            "code": Error.REPORT_NOT_FOUND.code,
        },
        500: {
            "message": Error.REPORT_INTERNAL_ERROR.message,
            "code": Error.REPORT_INTERNAL_ERROR.code,
        },
    },
    summary="📝 리포트 스크립트 조회",
    description="특정 리포트의 통화 스크립트를 조회합니다.",
)


async def get_report_script(report_id: int, db: AsyncSession = Depends(get_db)):
    return await get_report_script_endpoint(report_id, db)


router.api_doc(
    path="/{report_id}/expressions",
    endpoint=get_report_expressions_endpoint,
    request_model=None,
    response_model=APIResponse[ReportExpressionsResponse],
    success_model=APIResponse[ReportExpressionsResponse],
    errors={
        404: {
            "message": Error.REPORT_NOT_FOUND.message,
            "code": Error.REPORT_NOT_FOUND.code,
        },
        500: {
            "message": Error.REPORT_INTERNAL_ERROR.message,
            "code": Error.REPORT_INTERNAL_ERROR.code,
        },
    },
    summary="🗣️ 리포트 원어민 표현 조회",
    description="리포트에 포함된 사용자 대화를 기반으로 원어민 표현을 추천합니다.",
)


async def get_report_expressions(report_id: int, db: AsyncSession = Depends(get_db)):
    return await get_report_expressions_endpoint(report_id, db)
