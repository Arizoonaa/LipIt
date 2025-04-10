package com.ssafy.lipit_app.data.model.response_dto.custom_vocie

// S3 업로드 응답 모델
data class S3UploadResponse(
    val url: String,
    val key: String,
    val cdnUrl: String
)