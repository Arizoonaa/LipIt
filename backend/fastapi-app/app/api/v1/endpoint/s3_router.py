# app/api/v1/endpoint/audio_router.py:
from app.api.v1.endpoint.s3 import generate_presigned_url_view
from app.core.base_router import BaseRouter
from app.schema.common import APIResponse
from app.exception.error_code import Error
from pydantic import BaseModel

from app.schema.s3 import PresignRequest, PresignedURLResponse

router = BaseRouter(prefix="/api/s3", tags=["S3"])

# Presigned URL 발급 API
router.api_doc(
    path="/presign",
    endpoint=generate_presigned_url_view,
    methods=["POST"],
    request_model=PresignRequest,
    response_model=APIResponse[PresignedURLResponse],
    success_model=PresignedURLResponse,
    request_example={"filename": "voice-audio/1_1.wav"},
    success_example={
        "url": "https://s3.amazonaws.com/your-bucket/voice-audio/1_1.wav?AWSAccessKeyId=...",
        "cdnUrl": "https://d123abc456.cloudfront.net/voice-audio/1_1.wav",
    },
    errors={
        500: {
            "message": Error.S3_PRESIGNED_ERROR.message,
            "code": Error.S3_PRESIGNED_ERROR.code,
        }
    },
    summary="🔐 S3 Presigned URL 발급",
    description="""
S3에 파일을 직접 업로드할 수 있는 임시 URL을 발급합니다. 클라이언트는 해당 URL로 PUT 요청을 보내면 됩니다.

---

## filename(파일 이름)
- 형식: `{폴더 이름}`/`{파일 이름}`.`{확장자}`
    - 폴더 이름
        - 음성 이미지: `voice-image`
        - 음성 파일: `voice-audio`
    - 파일 이름
        - 음성 이미지: `{memberId}`_`{timestamp}`
        - 음성 파일: `{memberId}`_`{timestamp}`

- 예시: `voice-audio/1_1743742614.wav`


** `cdnUrl`를 통해 업로드한 파일에 접근 가능하며 이 URL을 DB에 저장합니다.
    """,
)


# router.api_doc(
#     path="/upload",
#     endpoint=upload_file,
#     methods=["POST"],
#     request_model=None,
#     response_model=APIResponse[AudioUploadResponse],
#     success_model=AudioUploadResponse,
#     success_example={"url": "https://dlxayir1dj7sa.cloudfront.net/test/abc123.mp4"},
#     errors={
#         500: {
#             "message": Error.AUDIO_UPLOAD_ERROR.message,
#             "code": Error.AUDIO_UPLOAD_ERROR.code,
#         }
#     },
#     summary="🔼 음성 파일 업로드",
#     description="사용자가 업로드한 음성 파일(wav, mp4 등)을 S3에 저장하고, CloudFront로 접근 가능한 URL을 반환합니다.",
# )
