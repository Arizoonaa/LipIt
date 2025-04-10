from sqlalchemy import Column, BigInteger, String
from sqlalchemy.orm import declarative_base

Base = declarative_base()


class Member(Base):
    __tablename__ = "member"

    member_id = Column(BigInteger, primary_key=True, index=True)
    selected_voice_id = Column(BigInteger, nullable=True)
    interest = Column(String, nullable=True)
    # 필요한 경우 다른 필드도 추가
