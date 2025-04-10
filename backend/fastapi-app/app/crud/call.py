# app/crud/call.py
from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession
from app.model.call import Call


async def get_call_by_id(db: AsyncSession, call_id: int) -> Call | None:
    result = await db.execute(select(Call).where(Call.call_id == call_id))
    return result.scalars().first()


async def save_call(db: AsyncSession, call: Call):
    db.add(call)
    await db.commit()
    await db.refresh(call)


async def get_member_id_by_call_id(db: AsyncSession, call_id: int) -> int | None:
    result = await db.execute(select(Call.member_id).where(Call.call_id == call_id))
    return result.scalar_one_or_none()
