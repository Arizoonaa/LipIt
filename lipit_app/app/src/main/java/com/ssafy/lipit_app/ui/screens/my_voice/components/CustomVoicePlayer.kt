package com.ssafy.lipit_app.ui.screens.my_voice.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

@Composable
fun CustomVoicePlayer(
    videoUrl: String?,
    isLooping: Boolean = false,
    isVisible: Boolean = true,
    onPlayStateChanged: (Boolean) -> Unit = {},
    onPlaybackReady: () -> Unit = {}
) {
    val context = LocalContext.current

    // 오류 상태 추적
    var isError by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }

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
                    onPlayStateChanged(false)
                }

                override fun onIsPlayingChanged(playing: Boolean) {
                    super.onIsPlayingChanged(playing)
                    isPlaying = playing
                    onPlayStateChanged(playing)
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
                    if (playbackState == Player.STATE_ENDED) {
                        onPlayStateChanged(false)
                    }
                    if (playbackState == Player.STATE_READY) {
                        onPlaybackReady()
                    }
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
}