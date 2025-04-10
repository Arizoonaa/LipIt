package com.ssafy.lipit_app.ui.screens.report

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.lipit_app.R
import com.ssafy.lipit_app.ui.screens.report.components.Report

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ReportScreen(
    state: ReportState,
    onIntent: (ReportIntent) -> Unit,
    shouldRefresh: Boolean
) {

    // pull & refresh 기능 상태 관리
    val isRefreshing = state.isLoading
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            Log.d("ReportScreen", "🔄 당겨서 새로고침 동작!")
            onIntent(ReportIntent.LoadReportList)
        }
    )

    // call에서 넘어올때 강제 새로고침 시행
    LaunchedEffect(shouldRefresh) {
        if (shouldRefresh) {
            onIntent(ReportIntent.LoadReportList)
            Log.d("ReportScreen", "강제 새로고침 실행")
        }
    }

    LaunchedEffect(Unit) {
        onIntent(ReportIntent.LoadReportList)
        Log.d("ReportScreen", "리포트 개수: ${state.totalReportList.size}")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
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
                    painter = painterResource(id = R.drawable.bg_myvoice),
                    contentScale = ContentScale.FillBounds
                )
                .padding(top = 24.dp, start = 24.dp, end = 24.dp)
        ) {
            Text(
                text = "Reports",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                modifier = Modifier.padding(top = 46.dp)
            )

            Spacer(modifier = Modifier.height(26.dp))

            when {
                state.isLoading -> {
                    LoadingView()
                }

                state.totalReportList.isNotEmpty() -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        items(state.totalReportList) { reports ->
                            Report(
                                report = reports,
                                onReportItemClick = { reportId ->
                                    onIntent(ReportIntent.NavigateToReportDetail(reportId))
                                }
                            )
                        }
                    }
                }

                else -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Top
                    ) {
                        Spacer(modifier = Modifier.height(40.dp))

                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Text(
                                "아직 리포트가 없어요! 🐣",
                                style = TextStyle(fontSize = 14.sp, color = Color.White)
                            )
                        }

                        Spacer(modifier = Modifier.weight(3f))
                    }
                }
            }
        }

        // 새로고침 인디케이터 추가
        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}


@Composable
fun LoadingView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 4.dp,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "리포트 불러오는 중...",
                color = Color.White,
                fontSize = 16.sp
            )
        }
    }
}
