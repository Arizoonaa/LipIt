package com.ssafy.lipit_app.ui.screens.my_voice

sealed class MyVoiceIntent {
    // Select voice
    data class SelectVoice(val voiceName: String, val voiceUrl: String) : MyVoiceIntent()

    // Load voices
    object LoadCelebrityVoices : MyVoiceIntent()
    object LoadCustomVoices : MyVoiceIntent()


    data class SelectTab(val tabName: String) : MyVoiceIntent()
    data class ChangeVoice(val voiceId: Long) : MyVoiceIntent()

    object NavigateToAddVoice : MyVoiceIntent()
}