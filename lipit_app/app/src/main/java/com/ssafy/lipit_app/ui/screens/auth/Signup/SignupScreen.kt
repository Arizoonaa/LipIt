package com.ssafy.lipit_app.ui.screens.auth.Signup

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ssafy.lipit_app.R
import com.ssafy.lipit_app.ui.components.SpacerHeight
import com.ssafy.lipit_app.ui.screens.auth.Signup.components.InputForm
import kotlinx.coroutines.delay

// 회워가입 메인 화면 구성
@Composable
fun SignupScreen(
    state: SignupState,
    onIntent: (SignupIntent) -> Unit,
    onSuccess: () -> Unit
) {

    val scrollState = rememberScrollState()

    // 입력 필드 포커스 상태 추적
    val isAnyFieldFocused by remember { mutableStateOf(false) }

    LaunchedEffect(isAnyFieldFocused) {
        if (isAnyFieldFocused) {
            // 약간의 지연 후 스크롤 (키보드가 완전히 열린 후)
            delay(300)
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // 배경은 나중에 이미지로
    ) {
        // 배경 이미지
        Image(
            painter = painterResource(id = R.drawable.bg_login),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding() // 키보드가 나타날 때 화면이 올라가도록 함
                .verticalScroll(rememberScrollState()), //
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SpacerHeight(44)

            // Title
            Image(
                painter = painterResource(id = R.drawable.img_title),
                contentDescription = "타이틀 로고",
                modifier = Modifier
                    .width(325.dp)
                    .wrapContentHeight(),
                contentScale = ContentScale.FillWidth
            )

            // 입력 폼
            InputForm(state, onSuccess, onIntent)

        }
    }
}



@Preview(showBackground = true)
@Composable
fun SignupScreenPreview() {
    SignupScreen(
        state = SignupState(
            id = "",
            pw="",
            pwConfirm = "",
            englishName = "",
            selectedGender = "",
            isPasswordVisible_1 = false,
            isPasswordVisible_2 = false,
            expanded = false
        ),
        onIntent = {},
        onSuccess = {}
    )
}
