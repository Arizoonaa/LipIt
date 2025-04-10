package com.ssafy.lipit_app.ui.screens.call.oncall.text_call

import com.ssafy.lipit_app.data.model.ChatMessageText

data class TextCallState(
    val voiceName: String = "Harry Potter",
    val leftTime: String = "04:50",

    // 모드 변경 버튼 - Voice / Text mode
    val currentMode: String = "Text",

    // 대화 내용 - 텍스트 모드
    val messages: List<ChatMessageText> = emptyList(),

    // 사용자가 입력 중인 메시지
    val inputText: String = "",

    // 번역 출력 여부
    val showTranslation: Boolean = false,
    val isReportCreated: Boolean = false,
    val reportFailed: Boolean = false

)