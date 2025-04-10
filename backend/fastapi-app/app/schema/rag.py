from pydantic import BaseModel
from typing import List


class RAGTestRequest(BaseModel):
    memberId: int
    query: str


class RAGMatch(BaseModel):
    chunkId: str
    content: str  # 검색된 원문
    score: float  # 유사도 점수


class RAGTestResponse(BaseModel):
    relatedCalls: List[RAGMatch]
