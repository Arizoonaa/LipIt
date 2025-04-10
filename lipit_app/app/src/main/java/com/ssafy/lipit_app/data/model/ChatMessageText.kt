package com.ssafy.lipit_app.data.model

data class ChatMessageText (
    val text: String,
    val translatedText: String = "",
    val isFromUser: Boolean = false
)