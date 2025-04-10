package com.ssafy.lipit_app.ui.screens.edit_call.weekly_calls

data class WeeklyCallsState (

    // Weekly 스케줄 정보 - 임시
    val callSchedules: List<CallSchedule> = emptyList(),

    val voiceName: String = "",
    val voiceImageUrl: String = "",
)

// 임시
data class CallSchedule(
    val callScheduleId: Long,
    val memberId: Long,
    val scheduleDay:String,
    val scheduledTime: String,
    val topicCategory: String?
)