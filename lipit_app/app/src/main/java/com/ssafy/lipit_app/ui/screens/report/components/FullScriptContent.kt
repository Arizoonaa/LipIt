package com.ssafy.lipit_app.ui.screens.report.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.lipit_app.data.model.request_dto.report.ReportScript


@Composable
fun FullScriptContent(
    reportScripts:
    List<ReportScript>
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(reportScripts) { script ->
            ChatBubble(reportScript = script)
        }
    }
}

@Composable
fun ChatBubble(reportScript: ReportScript) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = if (reportScript.isAI) Alignment.Start else Alignment.End
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = if (reportScript.isAI)
                        Color(0xFF483D56) // 진한 보라색
                    else
                        Color(0xFFB19CD9), // 연한 보라색
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(fraction = 0.8f)
            ) {
                Text(
                    text = reportScript.content,
                    color = Color.White,
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                )

                if (reportScript.contentKor.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = reportScript.contentKor,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}
