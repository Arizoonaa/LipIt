from sqlalchemy import Column, BigInteger, Text, DateTime
from app.db.session import Base
from app.util.datetime_utils import now_kst


class DailySentence(Base):
    __tablename__ = "daily_sentence"

    daily_sentence_id = Column(BigInteger, primary_key=True, autoincrement=True)
    content = Column(Text, nullable=False)
    content_korean = Column(Text, nullable=False)
    created_at = Column(DateTime(timezone=True), default=now_kst, nullable=False)
    updated_at = Column(
        DateTime(timezone=True),
        default=now_kst,
        onupdate=now_kst,
        nullable=False,
    )