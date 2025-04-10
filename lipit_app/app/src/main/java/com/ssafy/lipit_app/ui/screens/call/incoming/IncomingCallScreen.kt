package com.ssafy.lipit_app.ui.screens.call.incoming

import android.app.Activity
import android.util.Log
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ssafy.lipit_app.R
import com.ssafy.lipit_app.ui.screens.call.alarm.CallNotificationHelper
import com.ssafy.lipit_app.util.SharedPreferenceUtils

@Composable
fun IncomingCallScreen(
    onIntent: (IncomingCallIntent) -> Unit,
    viewModel: IncomingCallViewModel,
    navController: NavController
) {

    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val memberId = SharedPreferenceUtils.getMemberId()
        viewModel.fetchSelectedVoiceName(memberId)
    }


    LaunchedEffect(state.callDeclined) {
        if (state.callDeclined) {

            CallNotificationHelper.stopVibration(context)

            val activity = (context as? Activity)
            activity?.finishAffinity() // 앱 종료
        }
    }

    LaunchedEffect(state.callAccepted) {
        if (state.callAccepted) {
            Log.d("IncomingCall", "✅ 수락됨 - 네비게이션 이동 시도")

            navController.navigate("call_screen") {
                popUpTo("incoming_call") { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painterResource(
                id = R.drawable.incoming_call_background
            ),
            contentDescription = "배경",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
        )

        Column(
            modifier = Modifier
                .padding(top = 150.dp, start = 20.dp, end = 20.dp, bottom = 40.dp)
                .align(Alignment.TopCenter)
        ) {
            // 보이스 이름 출력 영역
            Text(
                text = state.voiceName,
                style = TextStyle(
                    fontSize = 40.sp,
                    lineHeight = 15.sp,
                    fontWeight = FontWeight(700),
                    color = Color(0xFFFDF8FF),
                    textAlign = TextAlign.Center,
                )
            )

            Spacer(modifier = Modifier.height(7.dp))

            // 앱 이름 출력 영역
            // : 전화와 혼동 방지를 위한 요소
            Box(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Lip It!",
                    style = TextStyle(
                        fontSize = 18.sp,
                        lineHeight = 15.sp,
                        fontWeight = FontWeight(400),
                        color = Color(0xFFFDF8FF),
                        textAlign = TextAlign.Center,
                    )
                )

            }
        }

        // 하단 Accept / Decline 버튼 영역
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(start = 50.dp, end = 50.dp, bottom = 70.dp),
            horizontalArrangement = Arrangement.SpaceBetween, // 양쪽 끝 정렬
            verticalAlignment = Alignment.CenterVertically

        ) {
            // 전화 받기 버튼
            AcceptCallBtn(onClick = {
                CallNotificationHelper.stopVibration(context)
                onIntent(IncomingCallIntent.Accept)
            })

            // 전화 거절 버튼
            DeclineCallBtn(onClick = {
                CallNotificationHelper.stopVibration(context)
                onIntent(IncomingCallIntent.Decline)
            })
        }
    }
}

@Composable
fun DeclineCallBtn(
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Box(
            modifier = Modifier
                .width(90.dp)
                .height(90.dp)
                .clip(CircleShape)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            RippleEffect()
            Box(
                modifier = Modifier
                    .width(69.dp)
                    .height(69.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFDF8FF))
                    .clickable { onClick() },
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.incoming_call_decline),
                    contentDescription = "거절 버튼",
                    modifier = Modifier
                        .width(70.dp)
                        .height(60.dp)
                        .align(Alignment.Center),
                    tint = Color(0xFFFE3B31),
                )
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        Text(
            text = "Decline",
            style = TextStyle(
                fontSize = 15.sp,
                lineHeight = 21.sp,
                fontWeight = FontWeight(510),
                color = Color(0xFFFDF8FF),
            ),
            textAlign = TextAlign.Center
        )
    }
}


// 버튼 ripple 효과(pulse)
@Composable
private fun RippleEffect(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()
    val animation = infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    Box(
        modifier = modifier
            .scale(animation.value)
            .size(120.dp) // 버튼보다 크게!
            .clip(CircleShape)
            .background(Color(0x33FDF8FF)) // 살짝 투명하게
    )
}

// 전화 받기
@Composable
fun AcceptCallBtn(
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(90.dp)
                .height(90.dp)
                .clip(CircleShape)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {

            RippleEffect()

            Box(
                modifier = Modifier
                    .width(69.dp)
                    .height(69.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFDF8FF))
                    .clickable {
                        onClick()
                    },
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.incoming_call_accept),
                    contentDescription = "수신 버튼",
                    modifier = Modifier
                        .width(38.dp)
                        .height(44.dp)
                        .align(Alignment.Center),
                    tint = Color(0xFF00C853),
                )
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        Text(
            text = "Accept",
            style = TextStyle(
                fontSize = 15.sp,
                lineHeight = 21.sp,
                fontWeight = FontWeight(510),
                color = Color(0xFFFDF8FF),
            ),
            textAlign = TextAlign.Center
        )
    }

}
