from fastapi import Depends, Query
from sqlalchemy.ext.asyncio import AsyncSession

from app.db.session import get_db
from app.schema.common import APIResponse
from app.crud.report import (
    get_reports_by_member_id,
    get_report_summary,
    get_report_script,
    get_report_expressions,
)
from app.exception.custom_exceptions import APIException
from app.exception.error_code import Error


# 리포트 목록 조회
async def get_reports_endpoint(
    member_id: int = Query(..., description="회원 ID"),
    db: AsyncSession = Depends(get_db),
):
    try:
        report_responses = await get_reports_by_member_id(db, member_id)

        return APIResponse(
            status=200,
            message="요청이 성공적으로 처리되었습니다.",
            data=report_responses,
        )
    except Exception:
        raise APIException(500, Error.REPORT_INTERNAL_ERROR)


# 리포트 요약 조회
async def get_report_summary_endpoint(
    report_id: int, db: AsyncSession = Depends(get_db)
):
    try:
        report_summary = await get_report_summary(db, report_id)

        return APIResponse(
            status=200, message="요약이 성공적으로 조회되었습니다.", data=report_summary
        )
    except APIException as e:
        raise e
    except Exception:
        raise APIException(500, Error.REPORT_INTERNAL_ERROR)


# 리포트 스크립트 조회
async def get_report_script_endpoint(
    report_id: int, db: AsyncSession = Depends(get_db)
):
    try:
        report_script = await get_report_script(db, report_id)

        return APIResponse(
            status=200,
            message="스크립트가 성공적으로 조회되었습니다.",
            data=report_script,
        )
    except APIException as e:
        raise e
    except Exception:
        raise APIException(500, Error.REPORT_INTERNAL_ERROR)


# 리포트 원어민 표현 조회
async def get_report_expressions_endpoint(
    report_id: int, db: AsyncSession = Depends(get_db)
):
    try:
        expressions_response = await get_report_expressions(db, report_id)

        return APIResponse(
            status=200,
            message="원어민 표현이 성공적으로 조회되었습니다.",
            data=expressions_response,
        )
    except APIException as e:
        raise e
    except Exception:
        raise APIException(500, Error.REPORT_INTERNAL_ERROR)
