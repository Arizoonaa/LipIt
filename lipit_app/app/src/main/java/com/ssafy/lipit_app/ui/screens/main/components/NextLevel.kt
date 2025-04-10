package com.ssafy.lipit_app.ui.screens.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.lipit_app.R

// 레벨업 보드 파트
@Composable
fun NextLevel(reportPercentage: Int, callTimePercentage: Int) {
    // 레벨 1~5 (기준: 리포트 수, 통화 누적 시간)

    var showLevelUpDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(top = 23.dp, bottom = 15.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            // 제목
            Text(
                text = "Next Level", style = TextStyle(
                    fontSize = 23.sp,
                    lineHeight = 45.sp,
                    fontWeight = FontWeight(590),
                    color = Color(0xFF3D3D3D),
                )
            )


            Spacer(modifier = Modifier.width(3.dp))

            // 물음표 버튼 - 레벨업 관련 안내

            Icon(
                painterResource(id = R.drawable.main_question_icon),
                contentDescription = "?",
                tint = Color(0xFFCBCBCB),
                modifier = Modifier
                    .size(18.dp)
                    .offset(y = -1.dp)
                    .clip(CircleShape)
                    .clickable {
                        // 레벨 설명 팝업 띄우기
                        showLevelUpDialog = true
                    }
            )


            if (showLevelUpDialog) {
                LevelUpDialog(
                    onDismissRequest = { showLevelUpDialog = false }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 다이어그램
        Column(
            modifier = Modifier
                .shadow(
                    elevation = 30.dp,
                    spotColor = Color(0x0D000000),
                    ambientColor = Color(0x0D000000),
                    clip = true
                )
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = Color(0xFFF3E7F9),
                    shape = RoundedCornerShape(size = 15.dp)
                )
                .background(color = Color(0xB2F3E7F9), shape = RoundedCornerShape(size = 15.dp))
        ) {
            Column(
                modifier = Modifier
                    .padding(top = 15.dp, start = 15.dp, end = 15.dp, bottom = 20.dp)
            ) {
                // 리포트 개수


             ProgressBarWithLabel(
                    label = "Number of Reports",
                    percent = reportPercentage,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )

                // 통화 누적 시간
                ProgressBarWithLabel(
                    label = "Call Time",
                    percent = callTimePercentage,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

        }
    }
}

@Composable
fun ProgressBarWithLabel(
    label: String,
    percent: Int, // 0~100
    modifier: Modifier = Modifier
) {
    val progress = percent.coerceIn(0, 100)

    Column(modifier = modifier) {
        // 텍스트 제목
        Text(
            text = label,
            style = TextStyle(
                fontSize = 13.sp,
                lineHeight = 13.sp,
                fontWeight = FontWeight(400),
                color = Color(0xFF515151),
            )
        )

        Spacer(modifier = Modifier.height(5.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(18.dp)
                .background(color = Color(0xFFFDF8FF), shape = RoundedCornerShape(50.dp)) // 배경 바
        ) {
            // 채워진 바
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress / 100f)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF5C3C84),
                                Color(0xFFA37BBD)
                            )
                        ),
                        shape = RoundedCornerShape(50.dp)
                    )
            ) {
                // 퍼센트 텍스트 (채워진 바 위에 정렬)
                Text(
                    text = if (progress == 100) "MAX" else "$progress%",
                    style = TextStyle(
                        fontSize = 12.sp,
                        lineHeight = 13.sp,
                        fontWeight = FontWeight(500),
                        color = Color(0xFFFFFFFF),
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 12.dp)
                )
            }

        }
    }
}
