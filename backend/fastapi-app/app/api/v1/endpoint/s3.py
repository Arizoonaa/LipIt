# app/api/v1/endpoint/audio_router.py
from fastapi import Body, Query, UploadFile, File
from app.core.base_router import BaseRouter
from app.service.s3 import generate_presigned_url, upload_file_to_s3
from app.schema.common import APIResponse
from app.exception.error_code import Error
from pydantic import BaseModel

from app.schema.s3 import AudioUploadResponse, PresignRequest, PresignedURLResponse


async def generate_presigned_url_view(req: PresignRequest = Body(...)):
    try:
        result = generate_presigned_url(req.filename)
        return APIResponse(
            status=200,
            message="Presigned URL 발급에 성공했습니다.",
            data=PresignedURLResponse(**result),
        )
    except Exception as e:
        raise APIResponse(500, Error.S3_PRESIGNED_ERROR)


# async def upload_file(file: UploadFile = File(...)):
#     try:
#         url = upload_file_to_s3(file)
#         return APIResponse(
#             status=200,
#             message="파일 업로드에 성공했습니다.",
#             data=AudioUploadResponse(url=url),
#         )
#     except Exception as e:
#         raise APIResponse(500, Error.AUDIO_UPLOAD_ERROR)
