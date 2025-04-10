from sqlalchemy import Column, BigInteger, Integer, String, DateTime, func
from app.db.session import Base
from app.util.datetime_utils import now_kst


class Report(Base):
    __tablename__ = "report"

    report_id = Column(BigInteger, primary_key=True, autoincrement=True)
    member_id = Column(BigInteger, nullable=False)
    call_id = Column(BigInteger, nullable=False)
    call_duration = Column(Integer, nullable=False)
    celeb_video_url = Column(String, nullable=True)
    word_count = Column(Integer, nullable=True)
    sentence_count = Column(Integer, nullable=True)
    communication_summary = Column(String(500), nullable=True)
    feedback_summary = Column(String(500), nullable=True)
    updated_at = Column(
        DateTime(timezone=True),
        default=now_kst,
        onupdate=now_kst,
        nullable=False,
    )
    created_at = Column(DateTime(timezone=True), default=now_kst, nullable=False)
