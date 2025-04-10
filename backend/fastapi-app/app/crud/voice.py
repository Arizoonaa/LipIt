from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select
from app.model.voice import Voice


async def get_voice_by_id(db: AsyncSession, voice_id: int) -> Voice | None:
    result = await db.execute(select(Voice).where(Voice.voice_id == voice_id))
    return result.scalar_one_or_none()
