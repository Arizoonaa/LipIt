package com.ssafy.lipit_app.data.model

/* 사용자/AI 메시지 저장해서 채팅처렁 보여주는 용도 */
data class ChatMessage(
    val type: String, // "user" or "ai"
    val message: String,
    val messageKor: String? = null
)
