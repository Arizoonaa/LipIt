package com.ssafy.lipit_app.ui.screens.report.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.lipit_app.data.model.request_dto.report.ReportSummary
import com.ssafy.lipit_app.util.CommonUtils.formatDate
import com.ssafy.lipit_app.util.CommonUtils.formatSeconds


@Composable
fun SummaryContent(reportSummary: ReportSummary?) {

    if (reportSummary == null) {
        EmptyContent(message = "요약 정보가 없습니다.")
    } else {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            ReportContent(reportSummary)
        }
    }

}


@Composable
fun ReportContent(summary: ReportSummary) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                BorderStroke(1.dp, color = Color.White),
                shape = RoundedCornerShape(25.dp)
            )
            .background(Color.Transparent, shape = RoundedCornerShape(25.dp))
            .padding(horizontal = 30.dp, vertical = 23.dp)
    ) {

        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = formatDate(summary.createdAt),
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )

                    }

                    Text(
                        "착신 통화 ${formatSeconds(summary.callDuration)}",
                        color = Color.White.copy(0.6f),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Light
                    )
                }


                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xffFDF8FF), shape = RoundedCornerShape(18.dp))
                        .padding(horizontal = 32.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${summary.wordCount}개",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Light
                        )
                        Text(
                            text = "말한 단어 수",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    VerticalDivider(
                        modifier = Modifier
                            .height(40.dp)
                            .width(2.dp),
                        color = Color(0xffD3D3D3)
                    )


                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${summary.sentenceCount}개",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Light
                        )
                        Text(
                            text = "말한 문장 수",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 요약 영역
                Column {
                    Text(
                        "대화 내용 요약",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        summary.communicationSummary,
                        color = Color.White.copy(0.6f),
                        fontSize = 15.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.Light
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Column {
                    Text(
                        "AI 피드백 요약",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        summary.feedbackSummary,
                        color = Color.White.copy(0.6f),
                        fontSize = 15.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.Light
                    )
                }
            }
        }
    }
}


@Composable
fun EmptyContent(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}