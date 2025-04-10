package com.ssafy.lipit_app.data.model.response_dto.myvoice

data class VoiceTakeResponse(
    val memberId: Long,
    val selectedVoiceId: Long,
    val voiceName: String,
    val type: String
)