from typing import List
from pydantic import BaseModel
from app.schema.common import APIResponse


# 오늘의 문장 요청 DTO
class CreateDailySentenceRequest(BaseModel):
    content: str
    contentKorean: str


# 오늘의 문장 배치 요청 DTO
class CreateDailySentenceBatchRequest(BaseModel):
    sentences: List[CreateDailySentenceRequest]


# 오늘의 문장 응답 DTO
class DailySentenceResponse(BaseModel):
    dailySentenceId: int
    content: str
    contentKorean: str
    createdAt: str


# 오늘의 문장 배치 응답 DTO
class DailySentenceBatchResponse(BaseModel):
    count: int
    sentences: List[DailySentenceResponse] 