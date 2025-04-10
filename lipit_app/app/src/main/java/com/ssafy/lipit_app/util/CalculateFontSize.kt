package com.ssafy.lipit_app.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit

// 화면 높이에 따라 비율적으로 폰트 크기 계산하는 함수
@Composable
fun calculateFontSize(screenHeight: Dp, ratio: Float): TextUnit {
    val density = LocalDensity.current
    return with(density) {
        (screenHeight.toPx() * ratio).toSp()
    }
}