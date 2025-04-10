package com.ssafy.lipit_app.ui.screens.edit_call.weekly_calls

import com.ssafy.lipit_app.ui.screens.my_voice.MyVoiceIntent

sealed interface WeeklyCallsIntent {
    // WeeklyCallsScreen 관련
    data class OnDaySelected(val day: String): WeeklyCallsIntent
    data class OnCallSelected(val callId: Int): WeeklyCallsIntent

    data class OnEditSchedule(val schedule: CallSchedule) : WeeklyCallsIntent
    // EditCallScreen 관련
    data class SelectFreeMode(val isSelected: Boolean): WeeklyCallsIntent
    data class SelectCategory(val category: String): WeeklyCallsIntent

    // Voice 변경 바텀시트 요청
    data object OnChangeVoice: WeeklyCallsIntent
}

