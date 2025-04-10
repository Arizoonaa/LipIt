package com.ssafy.lipit_app.data.model.request_dto.custom_voice

// 기존: PresignRequest
// AWS S3 에 데이터를 올리기 위해 파일명을 넘기고(해당 저장소에 폴더 생성), 저장위치 URL 을 받기 위한 Request
data class S3UploadRequest(
    val filename: String
)