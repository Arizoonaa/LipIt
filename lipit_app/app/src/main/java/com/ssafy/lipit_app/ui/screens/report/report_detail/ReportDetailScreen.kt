package com.ssafy.lipit_app.ui.screens.report.report_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.lipit_app.R
import com.ssafy.lipit_app.ui.screens.report.components.FullScriptContent
import com.ssafy.lipit_app.ui.screens.report.components.NativeSpeakerContent
import com.ssafy.lipit_app.ui.screens.report.components.SummaryContent
import kotlinx.coroutines.launch

@Composable
fun ReportDetailScreen(
    reportId: Long,
    state: ReportDetailState,
    onIntent: (ReportDetailIntent) -> Unit
) {

    val tabTitles = listOf("요약", "원어민 표현", "전체 스크립트")
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        pageCount = { tabTitles.size },
        initialPageOffsetFraction = 0f,
        initialPage = 0
    )

    LaunchedEffect(pagerState.currentPage) {
        onIntent(ReportDetailIntent.SelectTab(pagerState.currentPage))
    }

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

        Text(
            text = "Reports",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            modifier = Modifier.padding(start = 24.dp, top = 46.dp)
        )

        Spacer(modifier = Modifier.height(18.dp))

        // 탭 뷰
        TabRow(
            selectedTabIndex = state.selectedTabIndex,
            backgroundColor = Color.Transparent,
            contentColor = Color.White,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[state.selectedTabIndex]),
                    color = Color.White
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = state.selectedTabIndex == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                        onIntent(ReportDetailIntent.SelectTab(index))
                    },
                    modifier = Modifier.weight(1f),
                    text = {
                        Text(
                            text = title,
                            color = if (state.selectedTabIndex == index) Color.White else Color.White.copy(
                                alpha = 0.6f
                            ),
                            fontSize = 16.sp,
                            maxLines = 1,
                            fontWeight = if (state.selectedTabIndex == index) FontWeight.Bold else FontWeight.Light,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                )
            }
        }


        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Transparent)
                    ),
                    shape = RectangleShape
                )
                .padding(start = 20.dp, end = 20.dp, top = 24.dp)
        ) {
            HorizontalPager(
                state = pagerState,
                userScrollEnabled = true
            ) { page ->
                // 탭 화면
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp)
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    when (page) {
                        0 -> SummaryContent(state.reportSummary)
                        1 -> NativeSpeakerContent(state.nativeExpression)
                        2 -> FullScriptContent(state.reportScript)
                    }
                }
            }
        }

    }
}
