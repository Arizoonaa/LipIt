package com.ssafy.lipit_app.data.model.response_dto

// 공통 응답 wrapper
data class BaseResponse<T>(
    val statusCode: Int,
    val message: String,
    val data: T? = null  // API마다 다른 데이터를 포함하기 위해 Generic 사용
)