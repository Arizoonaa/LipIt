//package com.ssafy.lipit_app.ui.screens.edit_call.add_voice.components
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.size
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.CheckCircle
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.Icon
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.TextStyle
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.ssafy.lipit_app.ui.screens.edit_call.add_voice.AddVoiceIntent
//
//@Composable
//fun UploadSuccessScreen(
//    onIntent: (AddVoiceIntent) -> Unit
//) {
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xFFFDF8FF)),
//        contentAlignment = Alignment.Center
//    ) {
//        Column(
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Icon(
//                imageVector = Icons.Default.CheckCircle,
//                contentDescription = "성공",
//                tint = Color(0xFF4CAF50),
//                modifier = Modifier.size(64.dp)
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Text(
//                text = "음성이 성공적으로 저장되었습니다!",
//                style = TextStyle(
//                    fontSize = 20.sp,
//                    fontWeight = FontWeight.Bold,
//                    color = Color(0xFF222124),
//                    textAlign = TextAlign.Center
//                )
//            )
//
//            Spacer(modifier = Modifier.height(32.dp))
//
//            Button(
//                onClick = { onIntent(AddVoiceIntent.NavigateToMain) },
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = Color(0xFF9C27B0)
//                )
//            ) {
//                Text("홈으로 돌아가기")
//            }
//        }
//    }
//}