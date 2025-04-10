from sqlalchemy import BigInteger, Column, String

from app.db.session import Base


class Voice(Base):
    __tablename__ = "voice"

    voice_id = Column(BigInteger, primary_key=True, index=True)
    voice_name = Column(String, nullable=False)
    type = Column(String, nullable=False, default="CUSTOM")  # DEFAULT, CELEB 등
    audio_url = Column(String, nullable=True)
