package com.ssafy.lipit_app.ui.screens.auth.Login

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ssafy.lipit_app.R
import com.ssafy.lipit_app.ui.screens.auth.components.CustomFilledButton
import kotlinx.coroutines.delay


@Composable
fun LoginScreen(
    state: LoginState,
    onIntent: (LoginIntent) -> Unit,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    // 입력 필드 포커스 상태 추적
    var isIdFocused by remember { mutableStateOf(false) }
    var isPwFocused by remember { mutableStateOf(false) }

    LaunchedEffect(isIdFocused, isPwFocused) {
        if (isIdFocused || isPwFocused) {
            // 약간의 지연 후 스크롤 (키보드가 완전히 열린 후)
            delay(300)
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    LaunchedEffect(state.isLoginSuccess) {
        if (state.isLoginSuccess) {
            Log.d("auth", "LaunchedEffect: onSuccess 호출됨")
            onSuccess()
            onIntent(LoginIntent.OnLoginHandled)
        }
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            Toast.makeText(context, "아이디 정보를 다시 확인해주세요", Toast.LENGTH_SHORT).show()
        }
    }


    // ID
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
                .imePadding()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.SpaceBetween, // 위–아래 간격 자동 분배
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 상단 여백
            Spacer(modifier = Modifier.height(44.dp))

            // 1. Title
            Image(
                painter = painterResource(id = R.drawable.img_title),
                contentDescription = "타이틀 로고",
                modifier = Modifier
                    .width(325.dp)
                    .wrapContentHeight(),
                contentScale = ContentScale.FillWidth
            )

            // 2. 3D 로고 영여
            Image(
                painter = painterResource(id = R.drawable.img_3d_crop),
                contentDescription = "타이틀 로고",
                modifier = Modifier
                    .fillMaxWidth() // 어떤 화면에서도 가로 꽉 채우기
                    .wrapContentHeight(),
                contentScale = ContentScale.FillWidth
            )

            // Input: ID, PWD
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                // Input: ID
                OutlinedTextField(
                    value = state.id,
                    onValueChange = { onIntent(LoginIntent.onIdChanged(it)) },
                    placeholder = { Text("ID", color = Color(0xFFE2C7FF)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFFE2C7FF),
                        unfocusedBorderColor = Color(0xFFE2C7FF),
                        textColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Input: PW
                OutlinedTextField(
                    value = state.pw,
                    onValueChange = { onIntent(LoginIntent.onPwChanged(it)) },
                    placeholder = { Text("PW", color = Color(0xFFE2C7FF)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFFE2C7FF),
                        unfocusedBorderColor = Color(0xFFE2C7FF),
                        textColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = {
                            onIntent(
                                LoginIntent.OnisPasswordVisibleChanged(
                                    state.isPasswordVisible
                                )
                            )
                        }) {
                            Icon(
                                painter = painterResource(
                                    id = if (state.isPasswordVisible)
                                        R.drawable.ic_pw_closed2
                                    else
                                        R.drawable.ic_pw_show
                                ),
                                contentDescription = if (state.isPasswordVisible) "비밀번호 숨기기" else "비밀번호 보기",
                                tint = Color(0xFFE2C7FF)
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(50.dp))

                // 로그인 버튼
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,

                    ) {

//                    if (state.isLoginClicked) {
//                        // 로딩 인디케이터 표시
//                        CircularProgressIndicator(
//                            color = Color(0xFFE2C7FF),
//                            modifier = Modifier.padding(8.dp)
//                        )
//                    } else {
                        CustomFilledButton(text = "LOGIN", context, state, onClick = {
                            onIntent(LoginIntent.OnLoginClicked)
                        })
//                    }
                }

                Spacer(modifier = Modifier.height(94.dp))
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(
        state = LoginState(
            id = "",
            pw = "",
            isLoginClicked = false,
            isLoginSuccess = false,
            errorMessage = ""
        ),
        onIntent = {},
        onSuccess = {}
    )
}
