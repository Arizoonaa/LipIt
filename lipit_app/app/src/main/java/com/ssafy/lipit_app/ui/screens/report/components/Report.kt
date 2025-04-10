package com.ssafy.lipit_app.ui.screens.report.components

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.ssafy.lipit_app.data.model.response_dto.report.ReportListResponse

// 현재 뒤집힌 카드의 ID를 저장하는 객체
object FlippedCardTracker {
    private var _currentFlippedCardId: Long? = null

    // 리스너 목록
    private val listeners = mutableListOf<(Long?) -> Unit>()

    var currentFlippedCardId: Long?
        get() = _currentFlippedCardId
        set(value) {
            if (_currentFlippedCardId != value) {
                _currentFlippedCardId = value
                // 모든 리스너에게 변경 알림
                listeners.forEach { it(value) }
            }
        }

    // 리스너 등록
    fun addListener(listener: (Long?) -> Unit) {
        listeners.add(listener)
    }

    // 리스너 제거
    fun removeListener(listener: (Long?) -> Unit) {
        listeners.remove(listener)
    }
}

@Composable
fun Report(report: ReportListResponse, onReportItemClick: (Long) -> Unit) {
    val cardHeight = remember { mutableStateOf(0) }

    // 카드가 뒤집혔는지 상태 저장
    var isFlipped by remember { mutableStateOf(false) }

    // 현재 카드가 선택된 카드인지 확인하는 함수
    val listener: (Long?) -> Unit = remember {
        { newCardId ->
            Log.d("Report", "Listener triggered: newCardId=$newCardId, reportId=${report.reportId}")
            if (newCardId != null && newCardId != report.reportId && isFlipped) {
                Log.d("Report", "Flipping card ${report.reportId} back to front")
                isFlipped = false
            }
        }
    }

    // 컴포넌트가 구성될 때 리스너 등록, 해제될 때 리스너 제거
    DisposableEffect(Unit) {
        Log.d("Report", "Adding listener for card ${report.reportId}")
        FlippedCardTracker.addListener(listener)
        onDispose {
            Log.d("Report", "Removing listener for card ${report.reportId}")
            FlippedCardTracker.removeListener(listener)
        }
    }

    // 회전 애니메이션 값 계산
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "rotationAnimation"
    )

    val isFrontVisible = rotation <= 90f
    val isBackVisible = rotation > 90f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(25.dp))
            .background(Color.Transparent, shape = RoundedCornerShape(25.dp))
            .clickable(indication = null,
                interactionSource = remember { MutableInteractionSource() }) {
                if (!isFlipped) {
                    // 앞면 -> 뒷면으로 뒤집기: 현재 카드 ID 설정
                    Log.d("Report", "Flipping card ${report.reportId} to back, setting as current")
                    FlippedCardTracker.currentFlippedCardId = report.reportId
                } else {
                    // 뒷면 -> 앞면으로 뒤집기: 현재 카드가 아니게 됨
                    Log.d("Report", "Flipping card ${report.reportId} to front, clearing current")
                    if (FlippedCardTracker.currentFlippedCardId == report.reportId) {
                        FlippedCardTracker.currentFlippedCardId = null
                    }
                }
                isFlipped = !isFlipped
            }
    ) {

        // 카드 앞면 (뒤집혔을 때 숨김)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(25.dp))
                .background(Color.Transparent, shape = RoundedCornerShape(25.dp))
                .graphicsLayer {
                    rotationY = rotation
                    cameraDistance = 12f * density
                    alpha = if (isFrontVisible) 1f else 0f
                }
        ) {
            ReportFront(
                report = report,
                onReportItemClick = onReportItemClick,
                onMeasuredHeight = { height -> cardHeight.value = height }
            )
        }

        // 카드 뒷면 (앞면이 보일 때 숨김)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(25.dp))
                .background(Color.Transparent, shape = RoundedCornerShape(25.dp))
                .graphicsLayer {
                    rotationY = rotation - 180f
                    cameraDistance = 12f * density
                    alpha = if (isBackVisible) 1f else 0f
                }
        ) {
            ReportBack(
                report, isVisible = isBackVisible, height = cardHeight.value
            )
        }
    }
}