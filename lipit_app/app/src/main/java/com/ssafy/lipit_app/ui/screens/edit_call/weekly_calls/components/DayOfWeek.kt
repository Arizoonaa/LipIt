package com.ssafy.lipit_app.ui.screens.edit_call.weekly_calls.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun DayOfWeek(dayList: List<String>) {
    val today = LocalDate.now()
    val dayOfWeek: DayOfWeek = today.dayOfWeek
    val koreanDayName: String = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN)
    val dayOfMonth: String = today.dayOfMonth.toString()

    Column(
        modifier = Modifier
            .background(Color(0xB2F3E7F9),
                shape = RoundedCornerShape(15.dp)
            )
            .padding(horizontal = 17.dp, vertical = 31.dp)
            .height(400.dp)
            .width(20.dp),
        verticalArrangement = Arrangement.SpaceBetween, // 간격 동일하게 설정
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 요일 리스트 (오늘 자동 선택 하이라이트)
        for(i in dayList){
            val isToday = i == koreanDayName

            Text(
                text = i,
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = if(isToday) 18.sp else 16.sp,
                    lineHeight = 15.sp,
                    fontWeight = if(isToday) FontWeight(700) else FontWeight(400),
                    color = if(isToday) Color(0xFF8055A6) else Color(0xFF000000),
                    textAlign = TextAlign.Center,
                )
            )
        }


    }
}