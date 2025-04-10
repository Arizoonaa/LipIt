package com.ssafy.lipit_app.ui.screens.report.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.lipit_app.R
import com.ssafy.lipit_app.data.model.response_dto.report.ReportListResponse
import com.ssafy.lipit_app.util.CommonUtils.formatDate
import com.ssafy.lipit_app.util.CommonUtils.formatSeconds

// 카드 앞면
@Composable
fun ReportFront(
    report: ReportListResponse, onReportItemClick: (Long) -> Unit, onMeasuredHeight: (Int) -> Unit
) {


    Column(modifier = Modifier
        .fillMaxWidth()
        .onGloballyPositioned { layoutCoordinates ->
            onMeasuredHeight(layoutCoordinates.size.height)
        }
        .paint(
            painter = painterResource(id = R.drawable.report_background),
            contentScale = ContentScale.FillBounds
        )
        .padding(horizontal = 15.dp, vertical = 40.dp)) {

        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = formatDate(report.createdAt), style = TextStyle(
                        fontSize = 20.sp,
                        lineHeight = 30.sp,
                        fontWeight = FontWeight(700),
                        color = Color(0xFFFFFFFF),
                    ), modifier = Modifier.padding(start = 15.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable {
                                onReportItemClick(report.reportId)
                            }
                            .padding(bottom = 5.dp)) {

                        Text(
                            "상세보기 ",
                            color = Color.White.copy(0.6f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Light
                        )

                        Image(
                            painter = painterResource(id = R.drawable.btn_report_detail),
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

            }

            Text(
                "착신 통화 ${formatSeconds(report.callDuration)}",
                style = TextStyle(
                    fontSize = 15.sp,
                    lineHeight = 30.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFFFFFFFF),
                ),
                modifier = Modifier.padding(horizontal = 15.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier.padding(horizontal = 15.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xffFDF8FF), shape = RoundedCornerShape(18.dp))
                    .padding(horizontal = 42.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .offset(x = -12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {
                    Text(
                        text = "${report.wordCount}개", style = TextStyle(
                            fontSize = 15.sp,
                            lineHeight = 15.sp,
                            fontWeight = FontWeight(400),
                            color = Color(0xFF000000),
                            textAlign = TextAlign.Center
                        )
                    )

                    Text(
                        text = "말한 단어 수", style = TextStyle(
                            fontSize = 12.sp,
                            lineHeight = 15.sp,
                            fontWeight = FontWeight(700),
                            color = Color(0xFF000000),
                            textAlign = TextAlign.Center,
                        )
                    )
                }

                VerticalDivider(
                    modifier = Modifier
                        .height(40.dp)
                        .width(2.dp), color = Color(0xffD3D3D3)
                )


                Column(
                    modifier = Modifier
                        .weight(1f)
                        .offset(x = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${report.sentenceCount}개", style = TextStyle(
                            fontSize = 15.sp,
                            lineHeight = 15.sp,
                            fontWeight = FontWeight(400),
                            color = Color(0xFF000000),
                            textAlign = TextAlign.Center
                        )
                    )
                    Text(
                        text = "말한 문장 수", style = TextStyle(
                            fontSize = 12.sp,
                            lineHeight = 15.sp,
                            fontWeight = FontWeight(700),
                            color = Color(0xFF000000),
                            textAlign = TextAlign.Center,
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 요약 영역
        Column(
            modifier = Modifier.padding(horizontal = 15.dp)
        ) {
            Text(
                "대화 내용 요약",
                style = TextStyle(
                    fontSize = 18.sp,
                    lineHeight = 30.sp,
                    fontWeight = FontWeight(700),
                    color = Color(0xFFFFFFFF),
                )
            )


            Spacer(modifier = Modifier.height(7.dp))

            Text(
                text = report.communicationSummary, style = TextStyle(
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFFFFFFFF),
                )
            )
        }

        Spacer(modifier = Modifier.height(15.dp))

        Column(
            modifier = Modifier.padding(horizontal = 15.dp)
        ) {
            Text(
                "AI 피드백 요약", style = TextStyle(
                    fontSize = 18.sp,
                    lineHeight = 30.sp,
                    fontWeight = FontWeight(700),
                    color = Color(0xFFFFFFFF),
                )
            )

            Spacer(modifier = Modifier.height(7.dp))

            Text(
                text = report.feedbackSummary, style = TextStyle(
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFFFFFFFF),
                )

            )
        }
    }
}