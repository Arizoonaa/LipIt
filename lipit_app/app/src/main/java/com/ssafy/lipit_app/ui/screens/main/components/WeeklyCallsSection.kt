package com.ssafy.lipit_app.ui.screens.main.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ssafy.lipit_app.R
import com.ssafy.lipit_app.ui.screens.main.CallItem
import com.ssafy.lipit_app.ui.screens.main.MainIntent
import com.ssafy.lipit_app.ui.screens.main.MainViewModel

// 주간 전화 일정 한 눈에 보기
@Composable
fun WeeklyCallsSection(
    selectedDay: String,
    callItems: List<CallItem>,
    onIntent: (MainIntent) -> Unit
) {
    val context = LocalContext.current
    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val initialPage = days.indexOf(selectedDay).coerceAtLeast(0)
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { days.size }
    )

    // pager 변경 감지해서 요일 업데이트
    LaunchedEffect(pagerState.currentPage) {
        val newDay = days[pagerState.currentPage]
        if (newDay != selectedDay) {
            onIntent(MainIntent.OnDaySelected(newDay))
        }
    }

    LaunchedEffect(selectedDay) {
        val newIndex = days.indexOf(selectedDay)
        if (newIndex >= 0 && newIndex != pagerState.currentPage) {
//            pagerState.animateScrollToPage(newIndex)
            pagerState.scrollToPage(newIndex)
        }
    }

    Column(
        modifier = Modifier.padding(top = 25.dp)
    ) {
        // 제목 + 설정 버튼 Row
        Row(
            Modifier
                .padding(bottom = 14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "Weekly Calls",
                style = TextStyle(
                    fontSize = 23.sp,
                    lineHeight = 30.sp,
                    fontWeight = FontWeight(700),
                    color = Color(0xFF3D3D3D),
                )
            )

            // 편집 버튼
            Text(
                text = "Settings",
                style = TextStyle(
                    fontSize = 15.sp,
                    lineHeight = 15.sp,
                    fontWeight = FontWeight(600),
                    color = Color(0xFFA37BBD),
                    textAlign = TextAlign.End,
                ),
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onIntent(MainIntent.OnSettingsClicked)
                    }
            )
        }


        Column(
            modifier = Modifier
                .background(
                    color = Color(0xB2F3E7F9),
                    shape = RoundedCornerShape(size = 20.dp)
                )
                .padding(top = 10.dp, start = 12.dp, end = 12.dp, bottom = 12.dp)
        ) {
            // DaySelector 탭
            DaySelector(
                onDaySelected = { day ->
                    onIntent(MainIntent.OnDaySelected(day))
                },
                selectedDay
            )

            Spacer(modifier = Modifier.height(5.dp))

            // 🧭 Pager로 전화 스케줄 보여주기
            HorizontalPager(
                state = pagerState,
                pageSpacing = 12.dp,
                modifier = Modifier.fillMaxWidth()
            ) { page ->
                val day = days[page]
                val filteredItems = callItems.filter { it.scheduleDay == day }

                if (filteredItems.isNotEmpty()) {
                    Log.d("TAG", "WeeklyCallsSection: 데이터찾기 ${filteredItems}")
                    DailyCallSchedule(
                        filteredItems,
                        viewModel = MainViewModel(context),
                        onIntent = onIntent
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                            .shadow(
                                elevation = 8.dp,
                                spotColor = Color(0xFFD09FE6),
                                ambientColor = Color(0xFFD09FE6),
                                shape = RoundedCornerShape(15.dp)
                            )
                            .width(300.dp)
                            .height(70.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                onIntent(MainIntent.OnSettingsClicked)
                            }
                            .background(
                                color = Color(0xFFFFFFFF),
                                shape = RoundedCornerShape(size = 15.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {

                        // 내용
                        Row(
                            modifier = Modifier
                                .padding(start = 20.dp, end = 20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(text = "스케쥴이 없어요! 🥲", fontWeight = FontWeight.Light)

                        }
                    }
                }
            }

        }
    }
}


// api에서 제공하는 형식이랑 맞지 않아 추가함
fun dayFullToShort(day: String): String {
    return when (day.uppercase()) {
        "MONDAY" -> "Mon"
        "TUESDAY" -> "Tue"
        "WEDNESDAY" -> "Wed"
        "THURSDAY" -> "Thu"
        "FRIDAY" -> "Fri"
        "SATURDAY" -> "Sat"
        "SUNDAY" -> "Sun"
        else -> ""
    }
}
