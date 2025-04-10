from sqlalchemy import Column, BigInteger, DateTime, Integer, Text, func
from sqlalchemy.dialects.postgresql import JSONB
from app.db.session import Base
from app.util.datetime_utils import now_kst


class Call(Base):
    __tablename__ = "call"
    call_id = Column(BigInteger, primary_key=True, autoincrement=True)
    member_id = Column(BigInteger, nullable=False)
    messages = Column(JSONB, nullable=True)  # 대화 메시지 (JSONB 형식)
    start_time = Column(DateTime(timezone=True), default=now_kst, nullable=False)
    end_time = Column(DateTime(timezone=True), nullable=True)
    duration = Column(Integer, default=0, nullable=True)
    custom_audio_url = Column(Text, nullable=True)
    updated_at = Column(
        DateTime(timezone=True),
        default=now_kst,
        onupdate=now_kst,
        nullable=False,
    )  # 수정 시간
    created_at = Column(
        DateTime(timezone=True), default=now_kst, nullable=False
    )  # 생성 시간
