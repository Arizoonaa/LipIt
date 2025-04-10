package com.ssafy.lipit_app.ui.screens.report.components

import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.ssafy.lipit_app.R

@OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun VideoPlayer(
    videoUrl: String?,
    modifier: Modifier = Modifier,
    isLooping: Boolean = false,
    isVisible: Boolean = true
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
            repeatMode = if (isLooping) ExoPlayer.REPEAT_MODE_ALL else ExoPlayer.REPEAT_MODE_OFF

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
            exoPlayer.volume = 1f
        } else {
            exoPlayer.playWhenReady = false
            exoPlayer.volume = 0f
        }
    }

    // 화면에서 벗어날 때 플레이어 해제
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    Box(
        modifier = modifier
//            .background(Color.Black) // 검은색 배경 추가
            .clip(RoundedCornerShape(25.dp))
    ) {
        if (isError) {
            // 오류 발생 시 대체 UI 표시
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.avatar_3d), // 기본 이미지
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize(0.8f)
                )

                Spacer(modifier = Modifier.height(8.dp))

            }
        } else {
            AndroidView(
                factory = { ctx ->
                    androidx.media3.ui.PlayerView(ctx).apply {
                        player = exoPlayer
                        useController = false
                        resizeMode = androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIT

                        setBackgroundColor(android.graphics.Color.BLACK)
                        setShutterBackgroundColor(android.graphics.Color.BLACK)
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(25.dp))
            )
        }
    }
}