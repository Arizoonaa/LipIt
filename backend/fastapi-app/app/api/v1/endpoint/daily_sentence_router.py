from fastapi import APIRouter, Depends
from sqlalchemy.ext.asyncio import AsyncSession
from app.api.v1.endpoint.daily_sentence import (
    create_daily_sentence_endpoint,
    create_daily_sentences_batch_endpoint,
)
from app.schema.daily_sentence import (
    DailySentenceResponse,
    CreateDailySentenceRequest,
    CreateDailySentenceBatchRequest,
    DailySentenceBatchResponse,
)
from app.schema.common import APIResponse
from app.exception.error_code import Error
from app.db.session import get_db

# 표준 APIRouter 사용
router = APIRouter(prefix="/api", tags=["오늘의 문장"])


@router.post(
    "/daily-sentence",
    response_model=APIResponse[DailySentenceResponse],
    summary="✨ 오늘의 문장 개별 등록",
    description="새로운 영어 학습 문장을 데이터베이스에 등록합니다.",
    responses={
        201: {"model": APIResponse[DailySentenceResponse]},
        500: {
            "description": Error.DAILY_SENTENCE_INTERNAL_ERROR.message,
        },
    },
)
async def create_daily_sentence(
    request: CreateDailySentenceRequest, db: AsyncSession = Depends(get_db)
):
    return await create_daily_sentence_endpoint(request, db)


@router.post(
    "/daily-sentences",
    response_model=APIResponse[DailySentenceBatchResponse],
    summary="📚 오늘의 문장 일괄 등록",
    description="다수의 영어 학습 문장을 한 번에 데이터베이스에 등록합니다. (최대 10개)",
    responses={
        201: {"model": APIResponse[DailySentenceBatchResponse]},
        400: {
            "description": "최대 10개까지의 문장만 처리할 수 있습니다.",
        },
        500: {
            "description": Error.DAILY_SENTENCE_INTERNAL_ERROR.message,
        },
    },
)
async def create_daily_sentences_batch(
    request: CreateDailySentenceBatchRequest, db: AsyncSession = Depends(get_db)
):
    # 최대 10개로 제한
    if len(request.sentences) > 10:
        raise APIException(
            400, Error("DAILY-003", "최대 10개까지의 문장만 처리할 수 있습니다.")
        )

    return await create_daily_sentences_batch_endpoint(request, db)
