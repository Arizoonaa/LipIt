package com.ssafy.lipit_app.ui.screens.my_voice

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.paint
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ssafy.lipit_app.R
import com.ssafy.lipit_app.ui.screens.my_voice.components.CelebVoiceScreen
import com.ssafy.lipit_app.ui.screens.my_voice.components.CustomVoiceScreen
import mx.platacard.pagerindicator.PagerIndicator


@Composable
fun MyVoiceScreen(
    state: MyVoiceState,
    onIntent: (MyVoiceIntent) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color.Transparent)
                ),
                shape = RectangleShape
            )
            .paint(
                painter = painterResource(id = R.drawable.bg_without_logo),
                contentScale = ContentScale.FillBounds
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "My Voices",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                modifier = Modifier.padding(top = 46.dp)
            )

            Spacer(modifier = Modifier.height(26.dp))

            // 선택한 음성
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        shadowElevation = 8f
                        shape = RoundedCornerShape(20.dp)
                        clip = false
                    }
                    .drawBehind {
                        // 여러 개의 겹친 흰색 테두리로 홀리 그림자 효과 만들기
                        for (i in 1..13) {
                            drawRoundRect(
                                color = Color.White.copy(alpha = 0.1f),
                                style = Stroke(width = (i * 2).toFloat()),
                                cornerRadius = CornerRadius(20.dp.toPx(), 20.dp.toPx())
                            )
                        }
                    },
                shape = RoundedCornerShape(20.dp),
                backgroundColor = Color.White,
                elevation = 0.dp
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(12.dp)
                ) {

                    if (state.selectedVoiceUrl.isNotEmpty()) {
                        AsyncImage(
                            model = state.selectedVoiceUrl,
                            contentDescription = "profile image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(Color.Gray)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.img_add_image), // 프로필 이미지
                            contentDescription = "Profile Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(Color.Gray)
                        )

                    }

                    Spacer(modifier = Modifier.width(20.dp))
                    // 선택 음성 부분
                    Column {
                        Text(
                            text = state.selectedVoiceName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.Black
                        )
                    }
                }
            }


            Spacer(modifier = Modifier.height(30.dp))

            // Celebrity Custom
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Row {
                    Text(
                        text = "Celebrity",
                        fontSize = 20.sp,
                        color = if (state.selectedTab == "Celebrity") Color.White else Color.White.copy(
                            0.4f
                        ),
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                onIntent(MyVoiceIntent.SelectTab("Celebrity"))
                            }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Custom",
                        fontSize = 20.sp,
                        color = if (state.selectedTab == "Custom") Color.White else Color.White.copy(
                            0.4f
                        ),
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                onIntent(MyVoiceIntent.SelectTab("Custom"))
                            }
                    )
                }

                // Custom 탭이 선택된 경우에만 플러스 버튼 표시
                if (state.selectedTab == "Custom") {
                    Button(
                        onClick = {
                            onIntent(MyVoiceIntent.NavigateToAddVoice)
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFD7B7EC)),
                        modifier = Modifier.size(25.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Custom Voice",
                            tint = Color(0xFF431768),
                            modifier = Modifier.size(25.dp)
                        )
                    }
                }
            }
        }


        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            when (state.selectedTab) {
                "Celebrity" -> {
                    if (state.myCelebrityVoiceList.isNotEmpty()) {
                        val pagerState =
                            rememberPagerState(pageCount = { state.myCelebrityVoiceList.size })
                        val fling = PagerDefaults.flingBehavior(
                            state = pagerState,
                            snapPositionalThreshold = 0.3f // 더 낮으면 쉽게 넘어감
                        )

                        HorizontalPager(
                            state = pagerState,
                            contentPadding = PaddingValues(horizontal = 50.dp),
                            modifier = Modifier.fillMaxWidth(),
                            pageSpacing = (-25).dp,
                            flingBehavior = fling
                        ) { page ->
                            CelebVoiceScreen(
                                pagerState = pagerState,
                                page = page,
                                selectedVoiceName = state.selectedVoiceName,
                                voice = state.myCelebrityVoiceList[page],
                                onVoiceChange = { voiceId ->
                                    onIntent(MyVoiceIntent.ChangeVoice(voiceId))
                                }
                            )
                        }

                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            PagerIndicator(
                                pagerState = pagerState,
                                activeDotColor = Color(0xff503D75),
                                dotColor = Color.LightGray,
                                dotCount = 5,
                                activeDotSize = 8.dp
                            )
                        }
                    } else {
                        // 셀럽 목소리가 없을 때
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No celebrity voices",
                                color = Color.White,
                                fontSize = 16.sp
                            )

                            Spacer(modifier = Modifier.height(70.dp))
                        }
                    }
                }

                "Custom" -> {
                    // Custom 음성
                    Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                        CustomVoiceScreen(
                            customVoices = state.myCustomVoiceList,
                            selectedVoiceName = state.selectedVoiceName,
                            onVoiceChange = { voiceId ->
                                onIntent(MyVoiceIntent.ChangeVoice(voiceId))
                            }
                        )
                    }
                }
            }
        }
    }

}
