# app/schema/common.py
from typing import Generic, TypeVar, Optional
from pydantic import BaseModel

T = TypeVar("T")


class APIResponse(BaseModel, Generic[T]):
    status: int
    message: str
    data: Optional[T] = None  # 성공 응답일 때만 포함


# Swagger 명시용 에러 응답 모델 정의
class ErrorResponse(BaseModel):
    status: int
    message: str
    errorCode: str
