package com.ssafy.lipit_app.ui.screens.auth.components

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.lipit_app.R

@Composable
fun AuthStartScreen(
    onLoginClick: () -> Unit,
    onSignupClick: () -> Unit = {}
) {
    val context = LocalContext.current

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
                .fillMaxSize(),
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
                    .fillMaxWidth() // ✅ 어떤 화면에서도 가로 꽉 채우기
                    .wrapContentHeight(),
                contentScale = ContentScale.FillWidth
            )

            // 3. 로그인 & 회원가입 영역
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                // 왼쪽 정렬 텍스트
                Text(
                    text = "A new conversation awaits.\nReady to connect?",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(30.dp))

                // 가운데 정렬 버튼 + 회원가입 텍스트
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CustomOutlinedButton(text = "Get Started") {
//                        Toast.makeText(context, "회원가입 화면 이동", Toast.LENGTH_SHORT).show()
                        onSignupClick()
                    }

                    Spacer(modifier = Modifier.height(15.dp))

                    Text(
                        text = "I already have an account",
                        color = Color.White.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
//                            Toast.makeText(context, "로그인 화면 이동", Toast.LENGTH_SHORT).show()
                            onLoginClick()
                        },
                        fontSize = 16.sp
                    )
                }

                // 하단 여백
                Spacer(modifier = Modifier.height(90.dp))
            } // ...Column()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AuthStartPreview() {
    AuthStartScreen(
        onLoginClick = {},
        onSignupClick = {}
    )
}
