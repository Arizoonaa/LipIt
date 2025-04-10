package com.ssafy.lipit_app.ui.screens.my_voice.components

import androidx.annotation.OptIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.ssafy.lipit_app.R
import com.ssafy.lipit_app.data.model.response_dto.myvoice.CelabResponse

@OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun CelabVoicePlayer(
    videoUrl: String?,
    voice: CelabResponse,
    selectedVoiceName: String,
    modifier: Modifier = Modifier,
    isLooping: Boolean = false,
    isVisible: Boolean = true,
    onVoiceChange: (Long) -> Unit
) {
    val context = LocalContext.current

    // 오류 상태 추적
    var isError by remember { mutableStateOf(false) }

    // ExoPlayer 인스턴스 생성
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = videoUrl?.let { MediaItem.fromUri(it) }
            if (mediaItem != null) {
                setMediaItem(mediaItem)
            }
            //repeatMode = if (isLooping) ExoPlayer.REPEAT_MODE_ALL else ExoPlayer.REPEAT_MODE_OFF
            repeatMode = ExoPlayer.REPEAT_MODE_OFF

            // 오류 리스너 추가
            addListener(object : Player.Listener {
                override fun onPlayerError(error: PlaybackException) {
                    super.onPlayerError(error)
                    isError = true
                }
            })

            prepare()
        }
    }

    LaunchedEffect(isVisible) {
        if (isVisible) {
            exoPlayer.seekTo(0)
            exoPlayer.playWhenReady = true
        } else {
            exoPlayer.playWhenReady = false
        }
    }

    // 화면에서 벗어날 때 플레이어 해제
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    Box(modifier = modifier) {
        if (!isError) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp)
            ) {
                val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.voice_celeb_loading))
                val progress by animateLottieCompositionAsState(
                    composition,
                    iterations = LottieConstants.IterateForever
                )
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier.size(150.dp)
                )

                if (voice.activated) {
                    Button(
                        onClick = { onVoiceChange(voice.voiceId) },
                        enabled = selectedVoiceName != voice.voiceName,
                        colors = ButtonDefaults.buttonColors( if(selectedVoiceName != voice.voiceName) Color.Transparent else Color.Gray),
                        border = BorderStroke(
                            width = 1.dp,
                            color = Color.White
                        ),
                        shape = RoundedCornerShape(20.dp),
                        elevation = ButtonDefaults.elevation(0.dp),
                    ) {
                        Text("선택", color = if(selectedVoiceName != voice.voiceName) Color.Black else Color.White)
                    }
                }

            }
        }
    }
}