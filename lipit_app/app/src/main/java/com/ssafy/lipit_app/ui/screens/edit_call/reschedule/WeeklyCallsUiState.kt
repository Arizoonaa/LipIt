package com.ssafy.lipit_app.ui.screens.edit_call.reschedule

import com.ssafy.lipit_app.ui.screens.edit_call.change_voice.EditVoiceState
import com.ssafy.lipit_app.ui.screens.edit_call.weekly_calls.WeeklyCallsState

data class WeeklyCallsUiState(
    val weeklyState: WeeklyCallsState = WeeklyCallsState(), // 기존 목록 상태
    val editState: EditCallState = EditCallState(),
    val voiceState: EditVoiceState = EditVoiceState()
)
