package com.ssafy.lipit_app.ui.screens.edit_call.weekly_calls

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.lipit_app.ui.screens.edit_call.weekly_calls.components.SelectedVoiceCard
import com.ssafy.lipit_app.ui.screens.edit_call.weekly_calls.components.WeeklySchedule
import com.ssafy.lipit_app.ui.screens.main.MainIntent
import kotlin.math.log

// Main > Settings 누르면 보여지는 BottomSheet
private const val TAG = "WeeklyCallsScreen"
@Composable
fun WeeklyCallsScreen(
    state: WeeklyCallsState,
    onIntent: (WeeklyCallsIntent) -> Unit,
    onMainIntent: (MainIntent) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(top = 30.dp, start = 20.dp, end = 20.dp)
//            .background(Color(0xFFFDF8FF))
            .fillMaxWidth()
            .height(650.dp)
            .fillMaxHeight(0.7f)
    ) {
        // 제목
        Text(
            text = "Weekly Calls",
            style = TextStyle(
                fontSize = 25.sp,
                lineHeight = 30.sp,
                fontWeight = FontWeight(700),
                color = Color(0xFF222124),
            )
        )

        // 현재 선택된 보이스
        Log.d(TAG, "WeeklyCallsScreen: ${state.voiceName}")
        SelectedVoiceCard(state.voiceName, state.voiceImageUrl, onIntent = onIntent)

        // 일주일 스케줄 일정
        WeeklySchedule(
            callSchedules = state.callSchedules,
            onTapSchedule = { schedule ->
                onIntent(WeeklyCallsIntent.OnEditSchedule(schedule))
            },
            onDeleteSchedule = { scheduleId ->
                onMainIntent(MainIntent.DeleteSchedule(scheduleId))
            }
        )

    }
}


@Preview(showBackground = true)
@Composable
fun EditWeeklyCallsPreview() {
    // 테스트용 스케줄 정보
    val callSchedules: List<CallSchedule> = listOf(
        CallSchedule(
            callScheduleId = 1,
            memberId = 1,
            scheduleDay = "월",
            scheduledTime = "08:00:00",
            topicCategory = "자유주제"
        ),
        CallSchedule(
            callScheduleId = 2,
            memberId = 1,
            scheduleDay = "화",
            scheduledTime = "09:30:00",
            topicCategory = "자유주제"
        ),
        CallSchedule(
            callScheduleId = 3,
            memberId = 1,
            scheduleDay = "수",
            scheduledTime = "10:00:00",
            topicCategory = "자유주제"
        ),
        CallSchedule(
            callScheduleId = 4,
            memberId = 1,
            scheduleDay = "목",
            scheduledTime = "14:00:00",
            topicCategory = "여행"
        ),
        CallSchedule(
            callScheduleId = 5,
            memberId = 1,
            scheduleDay = "금",
            scheduledTime = "16:30:00",
            topicCategory = "음식"
        ),
        CallSchedule(
            callScheduleId = 6,
            memberId = 1,
            scheduleDay = "토",
            scheduledTime = "11:00:00",
            topicCategory = "취미"
        ),
        CallSchedule(
            callScheduleId = 7,
            memberId = 1,
            scheduleDay = "일",
            scheduledTime = "13:00:00",
            topicCategory = "문화"
        )
    )

    WeeklyCallsScreen(
        state = WeeklyCallsState(
            voiceName = "Harry Potter",
            voiceImageUrl = "https://file.notion.so/f/f/87d6e907-21b3-47d8-98dc-55005c285cce/7a38e4c0-9789-42d0-b8a0-2e3d8c421433/image.png?table=block&id=1c0fd4f4-17d0-80ed-9fa9-caa1056dc3f9&spaceId=87d6e907-21b3-47d8-98dc-55005c285cce&expirationTimestamp=1742824800000&signature=3tw9F7cAaX__HcAYxwEFal6KBsvDg2Gt0kd7VnZ4LcY&downloadName=image.png",
            callSchedules = callSchedules
        ),
        onIntent = {},
        onMainIntent = {}
    )
}
