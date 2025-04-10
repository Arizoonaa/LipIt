package com.ssafy.lipit_app.data.model.response_dto.custom_vocie

data class WhisperResponse(
    val text: String,
    val language: String? = null,
    val duration: Float? = null
)
