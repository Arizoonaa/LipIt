from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession
from typing import List, Optional

from app.model.native_expression import NativeExpression
from app.schema.report import NativeExpressionItem


async def create_native_expression(
    db: AsyncSession,
    report_id: int,
    my_sentence: str,
    ai_sentence: str,
    keyword: str,
    keyword_korean: str
) -> NativeExpression:
    """
    원어민 표현을 생성하고 데이터베이스에 저장합니다.
    """
    db_native_expression = NativeExpression(
        report_id=report_id,
        my_sentence=my_sentence,
        AI_sentence=ai_sentence,
        keyword=keyword,
        keyword_korean=keyword_korean
    )
    db.add(db_native_expression)
    await db.commit()
    await db.refresh(db_native_expression)
    return db_native_expression


async def get_native_expressions_by_report_id(
    db: AsyncSession, report_id: int
) -> List[NativeExpression]:
    """
    보고서 ID로 저장된 원어민 표현 목록을 조회합니다.
    """
    result = await db.execute(
        select(NativeExpression).where(NativeExpression.report_id == report_id)
    )
    return result.scalars().all()


def convert_to_native_expression_item(
    native_expression: NativeExpression, id_counter: int = None
) -> NativeExpressionItem:
    """
    DB 모델을 API 응답 DTO로 변환합니다.
    """
    return NativeExpressionItem(
        nativeExpressionId=id_counter if id_counter is not None else native_expression.native_expression_id,
        mySentence=native_expression.my_sentence,
        AISentence=native_expression.AI_sentence,
        keyword=native_expression.keyword,
        keywordKorean=native_expression.keyword_korean
    ) 