package com.ssafy.lipit_app.data.model.response_dto.myvoice

data class VoiceResponse(
    val memberId: Long,
    val memberVoiceId: Long,
    val voiceId: Long,
    val voiceName: String,
    val type: String,
    val imageUrl: String,
    val audioUrl: String
)
