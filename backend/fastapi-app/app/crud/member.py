from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.future import select
from app.model.member import Member


async def get_member_by_id(db: AsyncSession, member_id: int) -> Member | None:
    result = await db.execute(select(Member).where(Member.member_id == member_id))
    return result.scalar_one_or_none()
