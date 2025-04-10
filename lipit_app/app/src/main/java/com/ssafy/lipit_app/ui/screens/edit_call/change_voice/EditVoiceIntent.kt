package com.ssafy.lipit_app.ui.screens.edit_call.change_voice

import com.ssafy.lipit_app.ui.screens.my_voice.MyVoiceIntent

sealed interface EditVoiceIntent {

    object LoadCelebrityVoices : EditVoiceIntent
    object LoadCustomVoices : EditVoiceIntent

    data class SelectVoice(
        val voiceId: Long,
        val voiceName: String,
        val voiceUrl: String
    ) : EditVoiceIntent

    data class ChangeVoice(val voiceId: Long) : EditVoiceIntent

    object NavigateToAddVoice : EditVoiceIntent
}