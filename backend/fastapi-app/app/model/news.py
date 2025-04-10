from sqlalchemy import Column, BigInteger, String, DateTime, Text, func
from sqlalchemy.dialects.postgresql import JSONB
from app.db.session import Base
from app.util.datetime_utils import now_kst


class News(Base):
    __tablename__ = "news"

    news_id = Column(BigInteger, primary_key=True, autoincrement=True)
    title = Column(String(500), nullable=False)
    url = Column(String(1000), nullable=False)
    content = Column(Text, nullable=True)
    category = Column(String(50), nullable=True)
    updated_at = Column(
        DateTime(timezone=True),
        default=now_kst,
        onupdate=now_kst,
        nullable=False,
    )
    created_at = Column(DateTime(timezone=True), default=now_kst, nullable=False)
