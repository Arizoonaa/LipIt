package com.ssafy.lipit_app.ui.screens.main.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 커스텀 탭 레이아웃
@Composable
fun DaySelector(
    onDaySelected: (String) -> Unit,
    selectedDay: String
) {
    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    // 현재 선택된 요일의 인덱스
    var currentIndex by remember {
        mutableIntStateOf(days.indexOf(selectedDay).coerceIn(0, days.size - 1))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp)
            .padding(horizontal = 3.dp)
    ) {
        days.forEachIndexed { index, day ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .height(26.dp)
                    .padding(horizontal = 4.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(
                        // 선택된 요일의 배경은 여기서 직접 처리
                        if (day == selectedDay) Color(0xFFA37BBD) else Color.Transparent,
                        shape = RoundedCornerShape(size = 50.dp)
                    )
                    .clickable {
                        onDaySelected(day)
                    }
                    .align(Alignment.CenterVertically),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day,
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontSize = 13.sp,
                        lineHeight = 15.sp,
                        fontWeight = if (day == selectedDay) FontWeight(700) else FontWeight(500),
                        color = if (day == selectedDay) Color(0xFFFFFFFF) else Color(0xFFA7A7A7),
                        textAlign = TextAlign.Center,
                    )
                )
            }
        }
    }
}