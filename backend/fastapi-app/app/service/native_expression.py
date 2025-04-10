# app/service/native_expression.py
from app.crud.native_expression import create_native_expression
from sqlalchemy.ext.asyncio import AsyncSession
import logging

logger = logging.getLogger(__name__)


async def save_native_expressions(
    db: AsyncSession, report_id: int, expressions: list[dict]
):
    """
    LangGraph에서 생성된 native_expressions 리스트를 DB에 저장
    """
    for expr in expressions:
        try:
            await create_native_expression(
                db=db,
                report_id=report_id,
                my_sentence=expr.get("my_sentence", ""),
                ai_sentence=expr.get("native_sentence", ""),
                keyword=expr.get("keyword", ""),
                keyword_korean=expr.get("keyword_kor", ""),
            )
        except Exception as e:
            logger.warning(f"⚠️ 원어민 표현 저장 실패: {e}")
            continue
