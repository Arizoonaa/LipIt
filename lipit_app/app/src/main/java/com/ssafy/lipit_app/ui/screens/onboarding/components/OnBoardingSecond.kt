package com.ssafy.lipit_app.ui.screens.onboarding.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.lipit_app.R
import com.ssafy.lipit_app.util.calculateFontSize


@Composable
fun OnBoardingSecond(currentStep: Int, progressStep: Int, onNext: () -> Unit) {
    // 화면 높이와 너비 가져오기
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    // 상대적인 크기 계산
    val titleFontSize = calculateFontSize(screenHeight, 0.035f)
    val subtitleFontSize = calculateFontSize(screenHeight, 0.016f)
    val buttonFontSize = calculateFontSize(screenHeight, 0.024f)

    // 상대적인 여백 계산
    val topSpacerHeight = screenHeight * 0.03f
    val progressBarTopPadding = screenHeight * 0.02f
    val titleTopSpacerHeight = screenHeight * 0.07f
    val subtitleTopSpacerHeight = screenHeight * 0.02f
    val contentBottomSpacerHeight = screenHeight * 0.06f
    val buttonHeight = screenHeight * 0.1f

    // 이미지 오프셋 계산
    val imageOffsetY = buttonHeight * 0.8f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(id = R.drawable.bg_onboarding),
                contentScale = ContentScale.FillBounds
            )
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(topSpacerHeight))

            StepProgressBar(
                currentStep = currentStep,
                totalSteps = progressStep,
                modifier = Modifier.padding(progressBarTopPadding)
            )

            Spacer(modifier = Modifier.height(titleTopSpacerHeight))

            Text(
                text = "AI가 정리해주는\n나만의 영어 리포트",
                color = Color.White,
                fontSize = titleFontSize,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = titleFontSize * 1.3f
            )

            Spacer(modifier = Modifier.height(subtitleTopSpacerHeight))

            Text(
                text = "말한 내용부터 부족한 표현까지,\n한 눈에 보는 나의 말하기 습관",
                color = Color.White.copy(0.8f),
                fontSize = subtitleFontSize,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center,
                lineHeight = subtitleFontSize * 2.0f
            )

            // 나머지 공간을 채우는 유연한 스페이서
            Spacer(modifier = Modifier.weight(1f))
        }

        // 이미지는 상대적인 위치에 배치
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = imageOffsetY)
                .fillMaxWidth(0.9f) // 이미지 너비를 화면의 90%로 제한
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_onboarding2),
                contentDescription = "Onboarding Second Image",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = contentBottomSpacerHeight)
            )
        }

        // 하단 버튼 - 박스 맨 아래에 배치
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(buttonHeight)
                .align(Alignment.BottomCenter)
                .background(Color(0xff603981))
                .clickable(onClick = onNext),
            contentAlignment = Alignment.TopCenter
        ) {
            Text(
                text = "다음",
                color = Color.White,
                fontSize = buttonFontSize,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = buttonHeight * 0.3f)
            )
        }
    }
}
