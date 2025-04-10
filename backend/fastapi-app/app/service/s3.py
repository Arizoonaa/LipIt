# app/crud/audio.py
from io import BytesIO
import mimetypes
from uuid import uuid4
from fastapi import UploadFile
import boto3

from app.core.config import (
    AWS_ACCESS_KEY_ID,
    AWS_SECRET_ACCESS_KEY,
    AWS_REGION,
    BUCKET_NAME,
    CLOUDFRONT_DOMAIN,
)


s3_client = boto3.client(
    "s3",
    aws_access_key_id=AWS_ACCESS_KEY_ID,
    aws_secret_access_key=AWS_SECRET_ACCESS_KEY,
    region_name=AWS_REGION,
)


def generate_presigned_url(filename: str) -> dict:
    key = f"{filename}"
    content_type = guess_content_type(filename)

    url = s3_client.generate_presigned_url(
        ClientMethod="put_object",
        Params={
            "Bucket": BUCKET_NAME,
            "Key": key,
            "ContentType": content_type,
        },
        ExpiresIn=3600,
    )

    cdn_url = f"https://{CLOUDFRONT_DOMAIN}/{key}"

    return {"url": url, "cdnUrl": cdn_url}


def upload_file_to_s3(file: UploadFile) -> str:
    try:
        fileName = file.filename.split(".")[0]
        ext = file.filename.split(".")[-1]
        key = f"test/{fileName}.{ext}"

        s3_client.upload_fileobj(
            file.file, BUCKET_NAME, key, ExtraArgs={"ContentType": file.content_type}
        )

        return f"https://{CLOUDFRONT_DOMAIN}/{key}"
    except Exception as e:
        raise RuntimeError(f"S3 업로드 실패: {str(e)}")


def upload_bytes_to_s3(data: bytes, content_type: str = "audio/mpeg") -> str:
    key = f"audio/{uuid4()}.mp3"

    s3_client.upload_fileobj(
        Fileobj=BytesIO(data),
        Bucket=BUCKET_NAME,
        Key=key,
        ExtraArgs={"ContentType": content_type},
    )

    return f"https://{CLOUDFRONT_DOMAIN}/{key}"


def guess_content_type(filename: str) -> str:
    content_type, _ = mimetypes.guess_type(filename)
    if not content_type:
        raise ValueError(f"Unsupported file type for: {filename}")
    return content_type
