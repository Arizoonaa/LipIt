package com.ssafy.lipit_app.data.model

data class VoiceList(
    val voiceName: String,
    val voiceUrl: String,
    val activated: Boolean,  // 내가 획득한
    val isSelected: Boolean,  // 지금 내가 선택한
)
