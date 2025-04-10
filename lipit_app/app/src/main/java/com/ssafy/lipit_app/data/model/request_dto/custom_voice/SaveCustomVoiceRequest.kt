package com.ssafy.lipit_app.data.model.request_dto.custom_voice

data class SaveCustomVoiceRequest(
    val voiceName: String,
    val audioUrl: String,
    val imageUrl: String = ""
)