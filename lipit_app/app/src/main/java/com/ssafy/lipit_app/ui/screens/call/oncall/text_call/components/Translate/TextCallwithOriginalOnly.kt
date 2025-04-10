package com.ssafy.lipit_app.ui.screens.call.oncall.text_call.components.Translate

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.lipit_app.ui.screens.call.oncall.text_call.TextCallState

@Composable
fun TextCallwithOriginalOnly(state: TextCallState) {
    val chatMessages = state.messages
    val scrollState = rememberScrollState()

    Log.d("TextCall", "🖼️ 렌더링 대상 메시지 수: ${chatMessages.size}")
    chatMessages.forEachIndexed { i, msg ->
        Log.d("TextCall", "🗨️ [$i] ${if (msg.isFromUser) "나" else "AI"} → ${msg.text}")
    }
    Log.d("TextCallFooter", "📨 SendMessage 클릭됨")

    // 스크롤 상태 변화 시 맨 아래로 자동 스크롤
    LaunchedEffect(chatMessages.size) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }
    
    // 일단 리스트로 구현했으나 백 연동 시 다시 고려
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(top = 24.dp),
        verticalArrangement = Arrangement.spacedBy(17.dp)
    ) {
        chatMessages.forEach { message  ->
            Column(
                modifier = Modifier
                    .fillMaxWidth(),

                // 누가 보낸 메시지인지에 따라 가로 위치 변경
                horizontalAlignment = if (message.isFromUser) Alignment.End else Alignment.Start
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            color = if (message.isFromUser) Color(0x99C494D9) else Color(
                                0x66000000
                            ),
                            shape = RoundedCornerShape(size = 15.dp)
                        )
                        .padding(14.dp)
                        .widthIn(max = 260.dp) // 최대 가로 길이 제한
                ) {
                    Column {
                        Text(
                            text = message.text,
                            style = TextStyle(
                                fontSize = 20.sp,
                                lineHeight = 30.sp,
                                fontWeight = FontWeight(590),
                                color = Color(0xFFFDF8FF)
                            )
                        )
                    }

                }
            }

        }
    }
}