package com.ssafy.lipit_app.ui.screens.edit_call.weekly_calls.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ssafy.lipit_app.ui.screens.edit_call.weekly_calls.CallSchedule

// 스케줄 출력 영역
@Composable
fun WeeklySchedule(
    callSchedules: List<CallSchedule>,
    onTapSchedule: (CallSchedule) -> Unit,
    onDeleteSchedule: (Long) -> Unit)
{
    Row(
        modifier = Modifier.fillMaxSize()
    ){
        val dayList = listOf("월", "화", "수", "목", "금", "토", "일")

        // 좌측 요일 출력 - 오늘 날짜 하이라이트
        DayOfWeek(dayList)

        // 우측 요일별 스케줄 출력
        ThisWeekSchedules(
            dayList = dayList,
            callSchedules = callSchedules,
            onTapSchedule = onTapSchedule,
            onDeleteSchedule = onDeleteSchedule
        )

    }
}