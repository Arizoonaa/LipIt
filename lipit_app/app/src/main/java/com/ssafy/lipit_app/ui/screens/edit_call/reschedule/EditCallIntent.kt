package com.ssafy.lipit_app.ui.screens.edit_call.reschedule

import com.ssafy.lipit_app.ui.screens.edit_call.weekly_calls.CallSchedule

sealed interface EditCallIntent {
    data class SelectFreeMode(val isSelected: Boolean) : EditCallIntent
    data class SelectCategory(val category: String) : EditCallIntent

    // 해당 이벤트는 Main 쪽으로 넘어가서 처리한다. (스케줄 리스트 갱신을 위해)
    data class CreateSchedule(val schedule: CallSchedule) : EditCallIntent
    data class UpdateSchedule(val schedule: CallSchedule) : EditCallIntent
}
