package com.ssafy.lipit_app.ui.screens.onboarding.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ssafy.lipit_app.R
import com.ssafy.lipit_app.ui.screens.onboarding.OnBoardingIntent
import com.ssafy.lipit_app.ui.screens.onboarding.OnboardingViewModel
import com.ssafy.lipit_app.util.calculateFontSize


@Composable
fun OnBoardingFifth(
    onNext: () -> Unit,
    onboardingViewModel: OnboardingViewModel = viewModel()
) {
    // 화면 높이와 너비 가져오기
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    // 상대적인 크기 계산
    val titleFontSize = calculateFontSize(screenHeight, 0.03f)
    val subtitleFontSize = calculateFontSize(screenHeight, 0.016f)
    val buttonFontSize = calculateFontSize(screenHeight, 0.024f)
    val hintFontSize = calculateFontSize(screenHeight, 0.016f)

    // 상대적인 여백 계산
    val topSpacerHeight = screenHeight * 0.15f
    val titleTopSpacerHeight = screenHeight * 0.03f
    val subtitleTopSpacerHeight = screenHeight * 0.015f
    val inputBoxTopSpacerHeight = screenHeight * 0.04f
    val buttonHeight = screenHeight * 0.1f

    // 입력 상자 크기 계산
    val inputBoxMinHeight = screenHeight * 0.3f
    val inputBoxMaxHeight = screenHeight * 0.4f
    val inputBoxPadding = screenHeight * 0.02f

    val state by onboardingViewModel.state.collectAsState()
    var interest by remember { mutableStateOf("") }

    LaunchedEffect(state.navigateToNext) {
        if (state.navigateToNext) {
            onNext()
            onboardingViewModel.onIntent(OnBoardingIntent.NavigationComplete)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(id = R.drawable.bg_onboarding),
                contentScale = ContentScale.FillBounds
            )
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .windowInsetsPadding(WindowInsets.ime)
            .imePadding()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = screenWidth * 0.08f)
                .windowInsetsPadding(WindowInsets.safeDrawing),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(topSpacerHeight))

            Text(
                text = "본인에 대한 추가 정보를\n제공해주세요",
                color = Color.White,
                fontSize = titleFontSize,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Start,
                lineHeight = titleFontSize * 1.6f
            )

            Spacer(modifier = Modifier.height(subtitleTopSpacerHeight))

            Text(
                text = "AI가 더욱 실감나는 대화를 할 수 있어요!",
                color = Color(0xffC494D9),
                fontSize = subtitleFontSize,
                fontWeight = FontWeight.Light
            )

            Spacer(modifier = Modifier.height(inputBoxTopSpacerHeight))

            ExpandableTextInputBox(
                value = interest,
                onValueChange = { interest = it },
                hintFontSize = hintFontSize,
                minHeight = inputBoxMinHeight,
                maxHeight = inputBoxMaxHeight,
                padding = inputBoxPadding
            )
        }

        // 하단 버튼 - 박스 맨 아래에 배치
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(buttonHeight)
                .align(Alignment.BottomCenter)
                .background(Color(0xff603981))
                .clickable(onClick = {
                    Log.d("TAG", "OnBoardingFifth: 다음 버튼 clicked")
                    onboardingViewModel.onIntent(OnBoardingIntent.UpdateInterest(interest))
                    onboardingViewModel.onIntent(OnBoardingIntent.SaveInterest)
                    onNext()
                }),
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

@Composable
fun ExpandableTextInputBox(
    value: String,
    onValueChange: (String) -> Unit,
    hintFontSize: TextUnit,
    minHeight: Dp,
    maxHeight: Dp,
    padding: Dp
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = minHeight, max = maxHeight)
            .background(Color.White.copy(0.3f), shape = RoundedCornerShape(20.dp))
            .border(1.dp, color = Color.White, shape = RoundedCornerShape(20.dp))
            .padding(padding)
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth(),
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text(
                        text = "이 곳에 입력해주세요.",
                        fontSize = hintFontSize,
                        color = Color.White.copy(0.5f)
                    )
                }
                innerTextField()
            }
        )
    }
}