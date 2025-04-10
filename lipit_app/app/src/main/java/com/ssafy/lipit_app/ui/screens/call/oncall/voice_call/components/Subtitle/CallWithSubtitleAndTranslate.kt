package com.ssafy.lipit_app.ui.screens.call.oncall.voice_call.components.Subtitle

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.lipit_app.ui.screens.call.oncall.voice_call.VoiceCallState

// 자막과 번역 둘 다 나오는 버전
@Composable
fun CallWithSubtitleAndTranslate(state: VoiceCallState) {
    Column {
        Text(
            text = state.AIMessageOriginal,
            style = TextStyle(
                fontSize = 20.sp,
                lineHeight = 30.sp,
                fontWeight = FontWeight(590),
                color = Color(0xFFFDF8FF),
                textAlign = TextAlign.Center,
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = state.AIMessageTranslate,
            style = TextStyle(
                fontSize = 15.sp,
                lineHeight = 30.sp,
                fontWeight = FontWeight(274),
                color = Color(0xB2FDF8FF),
                textAlign = TextAlign.Center,
            )
        )
    }
}
