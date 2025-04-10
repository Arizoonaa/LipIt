package com.ssafy.lipit_app.ui.screens.call.oncall.text_call.components.Translate

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyListState
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
fun TextCallWithTranslate(
    state: TextCallState,
    listState: LazyListState
) {
    val chatMessages = state.messages
    val scrollState = rememberScrollState()

    // 새 메시지 생기면 맨 아래로 스크롤
    LaunchedEffect(chatMessages.size) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(17.dp)
    ) {
        chatMessages.forEach { message ->
            val isUser = message.isFromUser

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            color = if (isUser) Color(0x99C494D9) else Color(0x66000000),
                            shape = RoundedCornerShape(15.dp)
                        )
                        .padding(17.dp)
                        .widthIn(max = 260.dp)
                ) {
                    Column {
                        Text(
                            text = message.text,
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight(590),
                                color = Color(0xFFFDF8FF),
                                lineHeight = 30.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(5.dp))

                        if (!isUser && message.translatedText.isNotBlank()) {
                            Text(
                                text = message.translatedText,
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight(274),
                                    color = Color(0xB2FDF8FF),
                                    lineHeight = 30.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}