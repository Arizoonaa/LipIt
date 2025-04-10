package com.ssafy.lipit_app.ui.screens.call.oncall.voice_call

import com.ssafy.lipit_app.data.model.ChatMessage

data class VoiceCallState(
    val voiceName: String = "",
    val leftTime: String = "05:00",

    // 모드 변경 버튼 - Voice / Text mode
    val currentMode: String = "Voice",

    // 대화 내용 - 보이스 모드
    val AIMessageOriginal: String = "",
    val AIMessageTranslate: String = "",

    // 자막&번역 출력 여부
    val showSubtitle: Boolean = false,
    val showTranslation: Boolean = false,

    // 타이머 관련
    var isLoading: Boolean = false,
    val isFinished: Boolean = false,

    val chatMessages: List<ChatMessage> = emptyList(),
    val reportFailed: Boolean = false,

    val isCallEnded: Boolean = false,

    val isReportCreated: Boolean = false,
    val reportFailReason: String? = null,

)