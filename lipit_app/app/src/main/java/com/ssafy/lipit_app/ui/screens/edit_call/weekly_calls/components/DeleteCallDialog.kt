package com.ssafy.lipit_app.ui.screens.edit_call.weekly_calls.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun DeleteCallDialog(
    scheduleId: Long,
    onDismiss: () -> Unit,
    onConfirm: (Long) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .background(Color(0xFFFDF8FF), shape = RoundedCornerShape(20.dp))
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .fillMaxWidth()
        ) {
            Column(
            ) {
                // 제목
                Text(
                    text = "Call 예약 삭제하기",
                    style = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 16.sp,
                        fontWeight = FontWeight(700),
                        color = Color(0xFF222124),
                        textAlign = TextAlign.Center,
                    )
                )

                // 설명
                Text(
                    text = "선택한 Call 예약을 스케줄에서 삭제할까요?",
                    style = TextStyle(
                        fontSize = 12.sp,
                        lineHeight = 15.sp,
                        fontWeight = FontWeight(400),
                        color = Color(0xFF6F6F6F),
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 버튼 영역
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
                ) {
                    // 취소 버튼
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFD7D8DA))
                            .clickable {
                                onDismiss()
                                       },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "취소",
                            style = TextStyle(
                                fontSize = 13.sp,
                                lineHeight = 15.sp,
                                fontWeight = FontWeight(510),
                                color = Color(0xFF6F6F6F),
                            )
                        )
                    }

                    // 삭제하기 버튼
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFA37BBD))
                            .clickable {
                                onConfirm(scheduleId)
                                       },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "삭제하기",
                            style = TextStyle(
                                fontSize = 13.sp,
                                lineHeight = 15.sp,
                                fontWeight = FontWeight(510),
                                color = Color(0xFFFDF8FF),
                            )
                        )
                    }
                }
            }
        }
    }
}
