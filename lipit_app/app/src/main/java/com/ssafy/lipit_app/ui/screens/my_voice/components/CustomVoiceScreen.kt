package com.ssafy.lipit_app.ui.screens.my_voice.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import coil.compose.rememberAsyncImagePainter
import com.ssafy.lipit_app.R
import com.ssafy.lipit_app.data.model.response_dto.myvoice.CustomResponse

@Composable
fun CustomVoiceScreen(
    customVoices: List<CustomResponse> = emptyList(),
    selectedVoiceName: String,
    onVoiceChange: (Long) -> Unit
) {
    // 현재 재생 중인 음성의 ID를 상태로 관리
    var currentlyPlayingId by remember { mutableStateOf<Long?>(null) }
    val context = LocalContext.current

    // 하나의 ExoPlayer 인스턴스를 생성하고 관리
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_ENDED) {
                        currentlyPlayingId = null
                    }
                }
            })
        }
    }

    // 컴포넌트가 파괴될 때 ExoPlayer 해제
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        // 커스텀 음성이 없는 경우
        if (customVoices.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "No Custom voices",
                    color = Color.White,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(70.dp))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 10.dp, top = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 커스텀 음성 목록 표시
                items(customVoices) { voice ->
                    Log.d("TAG", "CustomVoiceScreen: ${selectedVoiceName} ${voice.voiceName}")

                    val isCurrentlyPlaying = currentlyPlayingId == voice.voiceId

                    CustomColumn(
                        voices = voice,
                        imageUrl = voice.customImageUrl,
                        voiceName = voice.voiceName,
                        isSelected = selectedVoiceName == voice.voiceName,
                        isPlaying = isCurrentlyPlaying,
                        onVoiceChange = { onVoiceChange(voice.voiceId) },
                        onPlayToggle = { shouldPlay ->
                            if (shouldPlay) {
                                // 다른 음성이 재생 중이면 중지
                                if (currentlyPlayingId != null && currentlyPlayingId != voice.voiceId) {
                                    exoPlayer.stop()
                                }

                                // 새 음성 재생 시작
                                voice.audioUrl?.let { audioUrl ->
                                    exoPlayer.setMediaItem(MediaItem.fromUri(audioUrl))
                                    exoPlayer.prepare()
                                    exoPlayer.play()
                                    currentlyPlayingId = voice.voiceId
                                }
                            } else {
                                // 현재 음성 정지
                                if (currentlyPlayingId == voice.voiceId) {
                                    exoPlayer.pause()
                                    currentlyPlayingId = null
                                }
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                }
            }
        }
    }
}

@Composable
fun CustomColumn(
    voices: CustomResponse,
    imageUrl: String,
    voiceName: String,
    onVoiceChange: (Long) -> Unit,
    onPlayToggle: (Boolean) -> Unit,
    isSelected: Boolean = false,
    isPlaying: Boolean = false
) {
    val rememberedSelected = rememberUpdatedState(isSelected).value

    Log.d("CustomColumn", "이미지 URL: $imageUrl, 비어있음: ${imageUrl.isEmpty()}")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                clip = false // 그림자 잘리지 않게 설정!
            }
            .shadow(
                elevation = if (rememberedSelected) 8.dp else 2.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = if (rememberedSelected) Color(0xFFA37BBD) else Color(0xFFCCCCCC),
                spotColor = if (rememberedSelected) Color(0xFFD372FF) else Color(0xFFCCCCCC)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(
                color = if (rememberedSelected) Color(0xFFF9F0FF) else Color.White,
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = if (rememberedSelected) 2.dp else 0.dp,
                color = Color(0xFFD372FF),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onVoiceChange(voices.voiceId) }
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val painter = rememberAsyncImagePainter(
                model = imageUrl.ifEmpty { R.drawable.img_add_image }
            )

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            ) {
                // 이미지 로딩 성공 시 표시
                Image(
                    painter = painter,
                    contentDescription = "프로필 이미지",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Text(
                text = voiceName,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }

        Icon(
            painter = painterResource(
                id = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
            ),
            contentDescription = null,
            tint = Color(0xffD09FE6),
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null  // 클릭 효과(리플 효과) 제거
            ) {
                // 재생 상태 토글하고 부모 컴포넌트에 알림
                onPlayToggle(!isPlaying)
            }
        )
    }
}
