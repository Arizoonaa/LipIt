from sqlalchemy.ext.asyncio import AsyncSession
from app.model.daily_sentence import DailySentence
from app.schema.daily_sentence import DailySentenceResponse, CreateDailySentenceRequest, CreateDailySentenceBatchRequest
from app.exception.custom_exceptions import APIException
from app.exception.error_code import Error
from app.util.datetime_utils import to_kst_date_str
from typing import List


# 오늘의 문장 생성
async def create_daily_sentence(
    db: AsyncSession, request: CreateDailySentenceRequest
) -> DailySentenceResponse:
    try:
        sentence = DailySentence(
            content=request.content,
            content_korean=request.contentKorean,
        )

        db.add(sentence)
        await db.commit()
        await db.refresh(sentence)

        return DailySentenceResponse(
            dailySentenceId=sentence.daily_sentence_id,
            content=sentence.content,
            contentKorean=sentence.content_korean,
            createdAt=to_kst_date_str(sentence.created_at),
        )
    except Exception as e:
        await db.rollback()
        raise APIException(500, Error.DAILY_SENTENCE_INTERNAL_ERROR)


# 오늘의 문장 배치 생성
async def create_daily_sentences_batch(
    db: AsyncSession, request: CreateDailySentenceBatchRequest
) -> List[DailySentenceResponse]:
    try:
        result = []
        
        for item in request.sentences:
            sentence = DailySentence(
                content=item.content,
                content_korean=item.contentKorean,
            )
            
            db.add(sentence)
            # 개별 commit은 하지 않고 모두 추가
            
            # 나중에 ID와 생성일자를 얻기 위해 임시 저장
            result.append(sentence)
        
        # 한 번에 모든 레코드 커밋
        await db.commit()
        
        # 결과 생성
        response = []
        for sentence in result:
            await db.refresh(sentence)
            response.append(DailySentenceResponse(
                dailySentenceId=sentence.daily_sentence_id,
                content=sentence.content,
                contentKorean=sentence.content_korean,
                createdAt=to_kst_date_str(sentence.created_at),
            ))
            
        return response
        
    except Exception as e:
        await db.rollback()
        raise APIException(500, Error.DAILY_SENTENCE_INTERNAL_ERROR) 