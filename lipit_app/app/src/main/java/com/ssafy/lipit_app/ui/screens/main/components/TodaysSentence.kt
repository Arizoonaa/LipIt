package com.ssafy.lipit_app.ui.screens.main.components

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.lipit_app.R
import com.ssafy.lipit_app.ui.screens.main.MainViewModel

// 오늘의 문장
@Composable
fun TodaysSentence(viewModel: MainViewModel, context: Context) {
    val state by viewModel.state.collectAsState()
    val cleanedTranslated = state.sentenceTranslated.trim()

    val parts = cleanedTranslated.split("\n", limit = 2)
    val englishText = if (parts.isNotEmpty()) parts[0].trim() else ""
    val koreanText = if (parts.size > 1) parts[1].trim() else ""

    // 보라색 그라데이션을 위한 색상 정의
    val purpleLight = Color(0xFFC494D9)  // 밝은 보라색
    val purpleLight2 = Color(0xFF9A75B5) // 중간 밝은 보라색
    val purpleDark = Color(0xFF634C86)   // 중간 어두운 보라색
    val purpleDark2 = Color(0xFF2B2256)  // 어두운 보라색

    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(purpleLight, purpleLight2, purpleDark, purpleDark2)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 15.dp)
    ) {
        // 그라데이션 배경 적용
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(gradientBrush)
        ) {
            // 내용 배치
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 오늘의 문장 원문 + 번역 텍스트
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(
                        text = englishText,
                        style = TextStyle(
                            fontSize = 14.sp,
                            lineHeight = 18.sp,
                            fontWeight = FontWeight(600),
                            color = Color(0xFFFFFFFF),
                        ),
                        modifier = Modifier
                            .padding(bottom = 5.dp) // 이미지와의 간격
                    )

                    Text(
                        text = "$koreanText  ✦˚",
                        style = TextStyle(
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            fontWeight = FontWeight.Light,
                            color = Color(0xFFFFFFFF).copy(0.7f),
                        ),
                        modifier = Modifier
                            .padding(end = 8.dp) // 이미지와의 간격
                    )

                }

                // 이미지
                Image(
                    painterResource(id = R.drawable.main_todays_sentance_img),
                    contentDescription = "오늘의 문장 이미지",
                    modifier = Modifier
                        .width(80.dp)
                        .height(80.dp)
                )
            }
        }
    }
}

@Composable
@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
fun TodaysSentencePreview() {
    val context = LocalContext.current
    TodaysSentence(viewModel = MainViewModel(context), context = context)
}