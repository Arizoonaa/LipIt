from typing import List, Optional
from pydantic import BaseModel
from app.schema.common import APIResponse


# 리포트 생성 요청 DTO
class CreateReportRequest(BaseModel):
    memberId: int
    callId: int
    callDuration: int
    celebVideoUrl: Optional[str] = None
    wordCount: Optional[int] = None
    sentenceCount: Optional[int] = None
    communicationSummary: Optional[str] = None
    feedbackSummary: Optional[str] = None


# 리포트 응답 DTO
class ReportResponse(BaseModel):
    reportId: int
    callDuration: int
    celebVideoUrl: Optional[str] = None
    wordCount: Optional[int] = None
    sentenceCount: Optional[int] = None
    communicationSummary: Optional[str] = None
    feedbackSummary: Optional[str] = None
    createdAt: str


# 리포트 목록 응답 DTO
class ReportListResponse(APIResponse):
    data: List[ReportResponse] = []


# 리포트 요약 응답 DTO
class ReportSummaryResponse(BaseModel):
    callDuration: int
    wordCount: int
    sentenceCount: int
    communicationSummary: str
    feedbackSummary: str
    createdAt: str


# 스크립트 메시지 항목 DTO
class ScriptItem(BaseModel):
    isAI: bool
    content: str
    contentKor: Optional[str] = None
    timestamp: str


# 리포트 스크립트 응답 DTO
class ReportScriptResponse(BaseModel):
    script: List[ScriptItem] = []


# 원어민 표현 항목 DTO
class NativeExpressionItem(BaseModel):
    nativeExpressionId: int
    mySentence: str  # 내가 말한 문장
    AISentence: str  # AI 추천 문장
    keyword: str  # 키워드
    keywordKorean: str  # 키워드 한글 번역


# 리포트 원어민 표현 응답 DTO
class ReportExpressionsResponse(BaseModel):
    nativeExpressions: List[NativeExpressionItem] = []
