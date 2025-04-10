package com.ssafy.lipit_app.ui.screens.main.components

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ssafy.lipit_app.R
import com.ssafy.lipit_app.ui.screens.main.CallItem
import com.ssafy.lipit_app.ui.screens.main.MainIntent
import com.ssafy.lipit_app.ui.screens.main.MainViewModel
import kotlin.math.log

// 요일별 call 카드뷰
@Composable
fun DailyCallSchedule(
    callItems: List<CallItem>,
    viewModel: MainViewModel,
    onIntent: (MainIntent) -> Unit
) {
    val state by viewModel.state.collectAsState() // 고정된 값이 아닌 상태 관찰 -> 실시간 UI 반영

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .shadow(
                elevation = 8.dp,
                spotColor = Color(0xFFD09FE6),
                ambientColor = Color(0xFFD09FE6),
                shape = RoundedCornerShape(15.dp)
            )
            .width(300.dp)
            .height(70.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onIntent(MainIntent.OnSettingsClicked)
            }
            .background(color = Color(0xFFFFFFFF), shape = RoundedCornerShape(size = 15.dp)),
        contentAlignment = Alignment.Center
    ) {

        // 내용
        Row(
            modifier = Modifier
                .padding(start = 20.dp, end = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // url을 통해 이미지 받아오기
            if (state.imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = state.imageUrl,
                    contentDescription = "voice 프로필 사진",
                    modifier = Modifier
                        .width(55.dp)
                        .height(55.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.img_add_image),
                    error = painterResource(id = R.drawable.img_add_image)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.img_add_image),
                    contentDescription = "기본 프로필",
                    modifier = Modifier
                        .width(55.dp)
                        .height(55.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
            Log.d("ImageCheck", "URL: ${callItems.getOrNull(0)?.imageUrl}")


            Column(
                modifier = Modifier
                    .padding(
                        start = 15.dp,
                    )
                    .align(Alignment.CenterVertically)
            ) {
                // 보이스 이름
                Text(
                    text = state.callItem_name,
                    style = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 15.sp,
                        fontWeight = FontWeight(590),
                        color = Color(0xFF000000)
                    )
                )

                // 대화 주제 (토픽)
                Text(
                    text = callItems[0].topic,
                    style = TextStyle(
                        fontSize = 13.sp,
                        lineHeight = 15.sp,
                        fontWeight = FontWeight(400),
                        color = Color(0xFF5F5F61),
                    )
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // 정해진 call 시간
            Box(
                modifier = Modifier
                    .align(Alignment.Bottom)
                    .padding(bottom = 12.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Text(
                    text = formatCallTimeTo12Hour(callItems[0].time),
                    style = TextStyle(
                        fontSize = 15.sp,
                        lineHeight = 15.sp,
                        fontWeight = FontWeight(600),
                        color = Color(0xFFA37BBD),
                    ),
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Clip
                )
            }


        }
    }
}

@SuppressLint("DefaultLocale")
fun formatCallTimeTo12Hour(time: String): String {
    val parts = time.split(":")
    if (parts.size < 2) return time // 잘못된 입력은 그대로 반환

    val hour = parts[0].toIntOrNull() ?: return time
    val minute = parts[1].toIntOrNull() ?: return time

    val isAM = hour < 12
    val displayHour = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }

    val amPm = if (isAM) "AM" else "PM"

    return String.format("%d:%02d %s", displayHour, minute, amPm)
}

