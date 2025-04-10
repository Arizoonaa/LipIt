package com.ssafy.lipit_app.ui.screens.edit_call.add_voice

import android.net.Uri

// AddVoiceIntent.kt
sealed class AddVoiceIntent {
    // 녹음 시작 Intent
    object StartRecording : AddVoiceIntent()

    // 녹음 중지 Intent
    object StopRecording : AddVoiceIntent()

    // 다음 문장으로 이동 Intent
    object NextSentence : AddVoiceIntent()

    // 음성 이름 설정 Intent - 사용자가 텍스트 필드에 입력할 때마다 발생
    data class SetVoiceName(val name: String) : AddVoiceIntent()
    data class SetVoiceImage(val uri: Uri) : AddVoiceIntent()

    // 음성 제출(저장) Intent
    object SubmitVoice : AddVoiceIntent()

    // 메인 화면으로 이동 Intent
//    object NavigateToMain : AddVoiceIntent()
    object NavigateBackToMyVoices : AddVoiceIntent()

    // Error 처리용 팝업 Intent
    object DismissErrorDialog: AddVoiceIntent()


}
