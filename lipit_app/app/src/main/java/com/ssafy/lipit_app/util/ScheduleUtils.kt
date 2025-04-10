package com.ssafy.lipit_app.util

import com.ssafy.lipit_app.ui.screens.edit_call.weekly_calls.CallSchedule

//fun sortSchedulesByDay(scheduleList: List<CallSchedule>): List<CallSchedule> {
//    val dayOrder = listOf(
//        "MONDAY", "TUESDAY", "WEDNESDAY",
//        "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"
//    )
//    return scheduleList.sortedBy { dayOrder.indexOf(it.scheduleDay) }
//}

fun sortSchedulesByDay(scheduleList: List<CallSchedule>): List<CallSchedule> {
    val dayOrder = listOf(
        "MONDAY", "TUESDAY", "WEDNESDAY",
        "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"
    )

    val grouped = scheduleList.groupBy { it.scheduleDay }

    return dayOrder.flatMap { day ->
        grouped[day]?.takeIf { it.isNotEmpty() } ?: listOf(
            CallSchedule(
                callScheduleId = -1, // placeholder
                memberId = -1,
                scheduleDay = day,
                scheduledTime = "", // or "00:00:00"
                topicCategory = null // UI에서 처리용
            )
        )
    }
}
