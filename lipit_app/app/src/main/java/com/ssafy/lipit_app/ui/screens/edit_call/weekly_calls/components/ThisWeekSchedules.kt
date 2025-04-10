package com.ssafy.lipit_app.ui.screens.edit_call.weekly_calls.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ssafy.lipit_app.R
import com.ssafy.lipit_app.ui.screens.edit_call.weekly_calls.CallSchedule
import com.ssafy.lipit_app.ui.screens.main.MainViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun ThisWeekSchedules(
    dayList: List<String>,
    callSchedules: List<CallSchedule>,
    onTapSchedule: (CallSchedule) -> Unit,
    onDeleteSchedule: (Long) -> Unit
) {
    val today = LocalDate.now()
    val dayOfWeek: DayOfWeek = today.dayOfWeek
    val koreanDayName: String = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(start = 12.dp, bottom = 70.dp, top = 10.dp),
        verticalArrangement = Arrangement.SpaceBetween // 아이템 간격 일정하게 조정
    ) {

        dayList.zip(callSchedules).forEach { (day, schedule) ->
            val isToday = day == koreanDayName
            var showDeletePopup by remember { mutableStateOf(false) } // 삭제 팝업

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(57.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = {
                                showDeletePopup = true // 롱프레스 시 팝업 띄우기
                            },
                            onTap = {
                                //전화 개별 상세 편집 화면으로 넘어감
//                                onTapSchedule()
                                onTapSchedule(schedule)
                            }
                        )
                    }
                    .border(
                        width = if (isToday) 1.5.dp else 1.dp,
                        color = if (isToday) Color(0xB28055A6) else Color(0xFFF3E7F9),
                        shape = RoundedCornerShape(size = 15.dp)
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 스케줄 데이터가 없는 경우에는 Text, Category 아무것도 표시하지 않음
                if (schedule.callScheduleId != -1L) {

                    // 12 시간표현
                    val hourMinute = schedule.scheduledTime.substringBeforeLast(":")
                    val hour = hourMinute.substringBefore(":").toInt()
                    val minute = hourMinute.substringAfter(":").padStart(2, '0')
                    val amPm = if (hour < 12) "AM" else "PM"
                    val hour12 = when (hour) {
                        0 -> 12  // 0시는 12 AM
                        in 1..12 -> hour  // 1시~12시는 그대로
                        else -> hour - 12  // 13시~23시는 -12
                    }

                    Text(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 19.dp),
                        text = "$hour12:$minute $amPm",
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 18.sp,
                            lineHeight = 15.sp,
                            fontWeight = FontWeight(400),
                            color = Color(0xFF5F5F61)
                        )
                    )


                    Spacer(modifier = Modifier.weight(1f))

                    // 카테고리
                    Box(
                        modifier = Modifier
                            .height(25.dp)
                            .background(
                                color = Color(0xFFF3E7F9),
                                shape = RoundedCornerShape(size = 15.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = schedule.topicCategory ?: "자유주제",
                            style = androidx.compose.ui.text.TextStyle(
                                fontSize = 12.sp,
                                lineHeight = 15.sp,
                                fontWeight = FontWeight(400),
                                color = Color(0xFF5F5F61),
                                textAlign = TextAlign.Center,
                            ),
                            modifier = Modifier.padding(horizontal = 15.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(5.dp))

                    // 아이콘
                    Icon(
                        painterResource(id = R.drawable.menu_icon),
                        contentDescription = "이동버튼",
                        modifier = Modifier
                            .width(20.dp)
                            .height(20.dp),
                        tint = Color(0xFF8055A6)
                    )

                    Spacer(modifier = Modifier.width(12.dp))
                } else {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Add, contentDescription = "add",
                            tint = Color.LightGray
                        )
                    }
                }

                // LongClickEvent: 꾹 눌러 삭제
                if (showDeletePopup && schedule.callScheduleId != -1L) {
                    DeleteCallDialog(
                        scheduleId = schedule.callScheduleId,
                        onDismiss = { showDeletePopup = false },
                        onConfirm = { id -> onDeleteSchedule(id) }
                    )
                }

            }
        }
    }
}

