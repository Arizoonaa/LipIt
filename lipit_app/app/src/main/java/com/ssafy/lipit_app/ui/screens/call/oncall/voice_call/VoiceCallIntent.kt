package com.ssafy.lipit_app.ui.screens.call.oncall.voice_call

sealed interface VoiceCallIntent {

    //자막 이벤트
    data class SubtitleOn(val showSubtitle: Boolean) : VoiceCallIntent
    data class SubtitleOff(val showSubtitle: Boolean) : VoiceCallIntent
    data class UpdateSubtitle(val message: String) : VoiceCallIntent

    //번역 이벤트
    data class TranslationOn(val showTranslation: Boolean) : VoiceCallIntent
    data class TranslationOff(val showTranslation: Boolean) : VoiceCallIntent
    data class UpdateTranslation(val translatedMessage: String) : VoiceCallIntent

    //타이머 종료
    object timerIsOver : VoiceCallIntent

}