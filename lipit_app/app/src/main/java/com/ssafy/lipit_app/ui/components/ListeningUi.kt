package com.ssafy.lipit_app.ui.components


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.ssafy.lipit_app.R

@Composable
fun ListeningUi() {
    // 텍스트 알파 애니메이션
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.audio_wave)
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .offset(y = (90).dp)
    ) {
        if (composition != null) {
            LottieAnimation(
                composition = composition,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier
                    .width(300.dp)
            )
        }

        Text(
            text = "듣고 있어요...",
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 4.dp),
            color = Color.White,
            fontSize = 12.sp
        )

    }
}
