package com.ssafy.lipit_app.ui.screens.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun LevelUpDialog(onDismissRequest: () -> Unit) {
    Dialog(onDismissRequest = onDismissRequest) {
        BoxWithConstraints {
            val dialogWidth = maxWidth * 0.95f // 💡 화면의 95% 차지
            Box(
                modifier = Modifier
                    .width(dialogWidth)
                    .padding(horizontal = 12.dp, vertical = 12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            Color(0xFFF7F0FB),
                            shape = RoundedCornerShape(15.dp)
                        )
                        .padding(24.dp)
                ) {
                    // 제목
                    Text(
                        text = "🏅 Level Up !",
                        style = TextStyle(
                            fontSize = 21.sp,
                            fontWeight = FontWeight(590),
                            color = Color(0xFF3D3D3D),
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "레벨은 아래 두 항목의 달성률에 따라 결정돼요.",
                        style = TextStyle(
                            fontSize = 13.sp,
                            fontWeight = FontWeight(400),
                            color = Color(0xFF000000),
                        )
                    )
                    Spacer(modifier = Modifier.height(15.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = Color(0xFFD3A9FF),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .background(Color.White, shape = RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "🧵 Number of Reports",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF000000),
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "레벨업을 위해 작성한 리포트 수",
                            style = TextStyle(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Light,
                                color = Color(0xFF000000),
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "📞 Call Time",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF000000),
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "누적 통화 시간",
                            style = TextStyle(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Light,
                                color = Color(0xFF000000),
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(15.dp))

                    // 하단 텍스트 영역
                    Text(
                        text = "더 많은 리포트, 더 긴 통화!\n" +
                                "조금씩 쌓이는 노력은 레벨 업으로 이어져요.\n" +
                                "오늘도 한 걸음 성장해보세요 \uD83C\uDF31",
                        style = TextStyle(
                            fontSize = 13.sp,
                            fontWeight = FontWeight(400),
                            color = Color(0xFF000000),
                        )
                    )
                }
            }
        }
    }
}