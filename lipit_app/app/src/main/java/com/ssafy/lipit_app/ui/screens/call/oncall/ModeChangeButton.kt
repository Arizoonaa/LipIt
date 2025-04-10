package com.ssafy.lipit_app.ui.screens.call.oncall

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.lipit_app.R

@Composable
fun ModeChangeButton(currentMode: String, onToggle: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.CenterEnd
    ) {
        Row(
            modifier = Modifier
                .width(81.dp)
                .height(32.dp)
                .fillMaxWidth()
                .background(
                    color = Color(0x66000000),
                    shape = RoundedCornerShape(size = 15.dp)
                )
                .clickable { onToggle() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // 아이콘
            Icon(
                painterResource(id = R.drawable.oncall_mode_switch_icon),
                contentDescription = "모드 전환 아이콘",
                tint = Color(0xFFF3E7F9),
            )

            Spacer(modifier = Modifier.width(3.dp))

            // 텍스트
            Text(
                // Voice 모드 -> 버튼 텍스트 Text 출력 (버튼에는 변경될 모드를 출력)
                // Text 모드 -> 버튼 텍스트 Voice 출력
                text = if (currentMode == "Text") "Voice" else "Text",
                style = TextStyle(
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFFFDF8FF),
                    textAlign = TextAlign.Center,
                )
            )
        }
    }
}