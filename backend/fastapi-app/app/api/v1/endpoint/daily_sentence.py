from fastapi import Depends
from sqlalchemy.ext.asyncio import AsyncSession

from app.db.session import get_db
from app.schema.common import APIResponse
from app.crud.daily_sentence import create_daily_sentence, create_daily_sentences_batch
from app.schema.daily_sentence import CreateDailySentenceRequest, CreateDailySentenceBatchRequest, DailySentenceBatchResponse
from app.exception.custom_exceptions import APIException
from app.exception.error_code import Error


# 오늘의 문장 생성
async def create_daily_sentence_endpoint(
    request: CreateDailySentenceRequest, db: AsyncSession = Depends(get_db)
):
    try:
        sentence = await create_daily_sentence(db, request)
        
        return APIResponse(
            status=201,
            message="오늘의 문장이 성공적으로 생성되었습니다.",
            data=sentence
        )
    except APIException as e:
        raise e
    except Exception:
        raise APIException(500, Error.DAILY_SENTENCE_INTERNAL_ERROR)

# 오늘의 문장 배치 생성
async def create_daily_sentences_batch_endpoint(
    request: CreateDailySentenceBatchRequest, db: AsyncSession = Depends(get_db)
):
    try:
        sentences = await create_daily_sentences_batch(db, request)
        
        return APIResponse(
            status=201,
            message=f"{len(sentences)}개의 오늘의 문장이 성공적으로 생성되었습니다.",
            data=DailySentenceBatchResponse(
                count=len(sentences),
                sentences=sentences
            )
        )
    except APIException as e:
        raise e
    except Exception:
        raise APIException(500, Error.DAILY_SENTENCE_INTERNAL_ERROR) 