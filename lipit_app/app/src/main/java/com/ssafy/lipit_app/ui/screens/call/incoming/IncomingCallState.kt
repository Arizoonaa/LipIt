package com.ssafy.lipit_app.ui.screens.call.incoming

data class IncomingCallState (
    val callAccepted: Boolean = false,
    val callDeclined: Boolean = false,
    val voiceName: String = "SARANG"  // 기본 음성 이름
)