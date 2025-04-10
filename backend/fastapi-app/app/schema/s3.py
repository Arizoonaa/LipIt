# Response 모델
from pydantic import BaseModel


class PresignedURLResponse(BaseModel):
    url: str  # S3 업로드용
    cdnUrl: str  # CloudFront로 접근할 URL


# Request 모델
class PresignRequest(BaseModel):
    filename: str


class AudioUploadResponse(BaseModel):
    url: str
