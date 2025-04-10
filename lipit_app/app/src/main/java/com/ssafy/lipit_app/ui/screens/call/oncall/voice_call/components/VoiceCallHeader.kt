package com.ssafy.lipit_app.ui.screens.call.oncall.voice_call.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.lipit_app.ui.screens.call.oncall.voice_call.VoiceCallViewModel

// 대화하는 AI 정보 (이름, 남은 시간)
@Composable
fun VoiceCallHeader(leftTime: String, viewModel: VoiceCallViewModel, voiceName: String) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .padding(top = 45.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 보이스 이름
        Text(
            text = if (voiceName.isNotBlank()) voiceName else "로딩 중...",
            style = TextStyle(
                fontSize = 40.sp,
                lineHeight = 15.sp,
                fontWeight = FontWeight(700),
                color = Color(0xFFFDF8FF),
                textAlign = TextAlign.Center,
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 남은 통화 시간
        Text(
            text = state.leftTime,
            style = TextStyle(
                fontSize = 20.sp,
                lineHeight = 15.sp,
                fontWeight = FontWeight(400),
                color = Color(0xFFFDF8FF),
                textAlign = TextAlign.Center,
            )
        )
    }
}