package com.ssafy.lipit_app.data.model.response_dto.custom_vocie

data class SaveCustomVoiceResponse(
    val voiceId: Long,
    val voiceName: String,
    val audioUrl: String,
    val imageUrl: String,
    val memberId: Long,
    val memberVoiceId: Long,
    val type: String
)
