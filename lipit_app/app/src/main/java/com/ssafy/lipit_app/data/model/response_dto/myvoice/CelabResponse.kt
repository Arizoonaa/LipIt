package com.ssafy.lipit_app.data.model.response_dto.myvoice

data class CelabResponse(
    val voiceId: Long,
    val voiceName: String,
    val customImageUrl: String,
    val audioUrl: String,
    val activated: Boolean
)
