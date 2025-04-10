from sqlalchemy.ext.asyncio import AsyncSession, create_async_engine
from sqlalchemy.orm import sessionmaker, declarative_base

from app.core.config import DATABASE_URL

# 비동기 엔진 생성
engine = create_async_engine(DATABASE_URL, echo=True, future=True)

# 세션 팩토리 생성
SessionLocal = sessionmaker(bind=engine, class_=AsyncSession, expire_on_commit=False)

# Base 클래스 (ORM 테이블 정의에 필요)
Base = declarative_base()


# 비동기 세션 생성 의존성 (FastAPI의 `Depends(get_db)`에서 사용)
async def get_db():
    async with SessionLocal() as session:
        yield session


# 데이터베이스 초기화 (테이블 생성)
async def init_db():
    async with engine.begin() as conn:
        await conn.run_sync(Base.metadata.create_all)
