//package com.ssafy.lipit_app.ui.screens.edit_call.add_voice.before
//
//import android.annotation.SuppressLint
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.CheckCircle
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.Icon
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.TextStyle
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.ssafy.lipit_app.R
//@Composable
//fun AddVoiceScreen(
//    state: AddVoiceState,
//    onIntent: (AddVoiceIntent) -> Unit,
//) {
//
//    // 모든 문장 녹음 완료 후 이름 입력 화면
//    if (state.isAllSentencesRecorded) {
//        VoiceNameInputScreen(state, onIntent)
//    }
//    // 업로드 성공 시 완료 팝업
//    else if (state.uploadSuccess) {
//        UploadSuccessScreen(onIntent)
//    }
//    // 녹음 화면
//    else {
//        RecordingScreen(state, onIntent)
//    }
//}
//
//
//// AddVoiceScreen.kt 파일의 RecordingScreen 함수 수정
//// 분석 중 상태와 실패 상태에 대한 UI 처리 추가
//@Composable
//fun RecordingScreen(
//    state: AddVoiceState,
//    onIntent: (AddVoiceIntent) -> Unit
//) {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xFFFDF8FF)),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Spacer(modifier = Modifier.weight(0.5f))
//
//        // 제목
//        Text(
//            text = "Create Custom Voice",
//            style = TextStyle(
//                fontSize = 30.sp,
//                lineHeight = 30.sp,
//                fontWeight = FontWeight(700),
//                color = Color(0xFF222124),
//                textAlign = TextAlign.Center,
//            )
//        )
//
//        Spacer(modifier = Modifier.height(5.dp))
//
//        // 안내 문구
//        Text(
//            text = "조용한 환경에서 녹음해 주세요.",
//            style = TextStyle(
//                fontSize = 15.sp,
//                lineHeight = 30.sp,
//                fontWeight = FontWeight(400),
//                color = Color(0xFF8A8A8A),
//                textAlign = TextAlign.Center,
//            )
//        )
//
//        Spacer(modifier = Modifier.weight(0.3f))
//
//        if (state.errorMessage != null) {
//            Text(
//                text = state.errorMessage,
//                color = Color.Red,
//                fontSize = 14.sp,
//                modifier = Modifier.padding(horizontal = 24.dp)
//            )
//        }
//
//        // 녹음 상태
//        Text(
//            text = when (state.recordingStatus) {
//                RecordingStatus.WAITING -> "녹음 대기중"
//                RecordingStatus.RECORDING -> "녹음중..."
//                RecordingStatus.ANALYZING -> "음성 분석 중..." // 새로운 상태 처리
//                RecordingStatus.COMPLETED -> "녹음 완료"
//                RecordingStatus.FAILED -> "녹음 분석 실패" // 새로운 상태 처리
//            },
//            style = TextStyle(
//                fontSize = 24.sp,
//                fontWeight = FontWeight(700),
//                color = when (state.recordingStatus) {
//                    RecordingStatus.WAITING -> Color.Gray
//                    RecordingStatus.RECORDING -> Color(0xFFE94F52)
//                    RecordingStatus.ANALYZING -> Color(0xFF2196F3) // 분석 중일 때 파란색
//                    RecordingStatus.COMPLETED -> Color(0xFF4CAF50)
//                    RecordingStatus.FAILED -> Color(0xFFE94F52) // 실패 시 빨간색
//                }
//            )
//        )
//
//        Spacer(modifier = Modifier.height(20.dp))
//
//        // 녹음 문장
//        Text(
//            text = state.sentenceList.getOrNull(state.currentSentenceIndex) ?: "문장을 불러오는 중 ..",
//            style = TextStyle(
//                fontSize = 20.sp,
//                lineHeight = 30.sp,
//                fontWeight = FontWeight(400),
//                color = Color(0xFF222124),
//                textAlign = TextAlign.Center,
//            ),
//            modifier = Modifier.padding(horizontal = 24.dp)
//        )
//
//        // 인식된 텍스트 표시 (녹음 완료 또는 실패 시)
//        if ((state.recordingStatus == RecordingStatus.COMPLETED || state.recordingStatus == RecordingStatus.FAILED)
//            && state.recognizedText.isNotEmpty()) {
//            Spacer(modifier = Modifier.height(10.dp))
//            Text(
//                text = "인식된 내용: ${state.recognizedText}",
//                style = TextStyle(
//                    fontSize = 16.sp,
//                    color = Color.DarkGray,
//                    fontWeight = FontWeight.Normal
//                ),
//                modifier = Modifier.padding(horizontal = 24.dp)
//            )
//
//            // 정확도 표시
//            Spacer(modifier = Modifier.height(5.dp))
//            Text(
//                text = "정확도: ${(state.accuracy * 100).toInt()}%",
//                style = TextStyle(
//                    fontSize = 16.sp,
//                    color = if (state.accuracy >= 0.8f) Color(0xFF4CAF50) else Color(0xFFE94F52),
//                    fontWeight = FontWeight.Bold
//                )
//            )
//        }
//
//        Spacer(modifier = Modifier.height(10.dp))
//
//        // 문장 진행률
//        Text(
//            text = "${state.currentSentenceIndex + 1} / ${state.sentenceList.size}",
//            style = TextStyle(
//                fontSize = 14.sp,
//                color = Color.Gray,
//                fontWeight = FontWeight.Light
//            )
//        )
//
//        Spacer(modifier = Modifier.weight(0.3f))
//
//        // 마이크 그림
//        Image(
//            painterResource(id = R.drawable.add_voice_mic_img),
//            contentDescription = "마이크",
//            modifier = Modifier
//                .width(180.dp)
//                .height(381.dp)
//        )
//
//        Spacer(modifier = Modifier.weight(0.4f))
//
//        // 녹음 상태에 따른 버튼 UI 변경
//        when (state.recordingStatus) {
//            RecordingStatus.WAITING -> {
//                // 녹음 시작 버튼
//                Image(
//                    painterResource(id = R.drawable.add_voice_start_icon),
//                    contentDescription = "녹음 시작",
//                    modifier = Modifier
//                        .size(70.dp)
//                        .clickable { onIntent(AddVoiceIntent.StartRecording) }
//                )
//            }
//            RecordingStatus.RECORDING -> {
//                // 녹음 중지 버튼
//                Image(
//                    painterResource(id = R.drawable.add_voice_stop_icon),
//                    contentDescription = "녹음 중지",
//                    modifier = Modifier
//                        .size(70.dp)
//                        .clickable { onIntent(AddVoiceIntent.StopRecording) }
//                )
//            }
//            RecordingStatus.ANALYZING -> {
//                // 분석 중일 때는 로딩 인디케이터 표시
//                CircularProgressIndicator(
//                    color = Color(0xFF2196F3),
//                    modifier = Modifier.size(70.dp)
//                )
//            }
//            RecordingStatus.COMPLETED -> {
//                // 녹음 완료 후 버튼 표시 (정확도에 따라 다른 버튼 표시)
//                Row(
//                    modifier = Modifier.padding(bottom = 20.dp),
//                    horizontalArrangement = Arrangement.spacedBy(24.dp)
//                ) {
//                    // 정확도가 낮을 경우에도 다시 녹음 버튼 표시
//                    if (state.accuracy < 0.8f) {
//                        Button(
//                            onClick = { onIntent(AddVoiceIntent.StartRecording) },
//                            colors = ButtonDefaults.buttonColors(
//                                containerColor = Color(0xFFE57373)
//                            )
//                        ) {
//                            Text("다시 녹음")
//                        }
//                    } else {
//                        // 정확도가 높을 경우 다음 버튼 표시
//                        Button(
//                            onClick = { onIntent(AddVoiceIntent.NextSentence) },
//                            colors = ButtonDefaults.buttonColors(
//                                containerColor = Color(0xFF9C27B0)
//                            )
//                        ) {
//                            Text("다음")
//                        }
//                    }
//                }
//            }
//            RecordingStatus.FAILED -> {
//                // 녹음 실패 시 다시 녹음 버튼
//                Button(
//                    onClick = { onIntent(AddVoiceIntent.StartRecording) },
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = Color(0xFFE57373)
//                    ),
//                    modifier = Modifier.padding(bottom = 20.dp)
//                ) {
//                    Text("다시 녹음")
//                }
//            }
//        }
//
//        Spacer(modifier = Modifier.weight(0.5f))
//    }
//}
//
//@Composable
//fun VoiceNameInputScreen(
//    state: AddVoiceState,
//    onIntent: (AddVoiceIntent) -> Unit
//) {
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xFFFDF8FF))
//            .padding(24.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        Text(
//            text = "음성에 이름을 붙여주세요",
//            style = TextStyle(
//                fontSize = 24.sp,
//                fontWeight = FontWeight.Bold,
//                color = Color(0xFF222124)
//            )
//        )
//
//        Spacer(modifier = Modifier.height(32.dp))
//
////        var voiceName by remember { mutableStateOf(state.voiceName) }
//        OutlinedTextField(
//            value = state.voiceName,
//            onValueChange = {
////                voiceName = it
//                onIntent(AddVoiceIntent.SetVoiceName(it))
//            },
//            label = { Text("음성 이름") },
//            singleLine = true,
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        Spacer(modifier = Modifier.height(32.dp))
//
//        Button(
//            onClick = { onIntent(AddVoiceIntent.SubmitVoice) },
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(50.dp),
//            colors = ButtonDefaults.buttonColors(
//                containerColor = Color(0xFF9C27B0)
//            ),
//            enabled = !state.isUploading && state.voiceName.isNotBlank()
//        ) {
//            if (state.isUploading) {
//                CircularProgressIndicator(
//                    color = Color.White,
//                    modifier = Modifier.size(24.dp)
//                )
//            } else {
//                Text("저장하기")
//            }
//        }
//
//        // 오류 메시지 표시
//        state.errorMessage?.let { error ->
//            Spacer(modifier = Modifier.height(16.dp))
//            Text(
//                text = error,
//                color = Color.Red,
//                fontSize = 14.sp
//            )
//        }
//    }
//}
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
//
//@Preview(showBackground = true)
//@Composable
//fun AddVoiceScreenPreview() {
//    val sampleSentences = listOf(
//        "Hi, I'm glad to meet you today.",
//        "Can you hear me clearly?",
//        "Let's get started with our conversation.",
//        "The weather is really nice today, isn't it?",
//        "I enjoy reading books and watching movies.",
//        "What are your hobbies?",
//        "Please repeat after me, slowly and clearly.",
//        "This is how I usually talk every day.",
//        "You can speak naturally and confidently.",
//        "Thank you for listening to my voice!"
//    )
//
//
////    AddVoiceScreen(
////        state = AddVoiceState(
////            secondsRemaining = 30,
////            isRecording = false,
////            currentSentenceIndex = 0,
////            sentenceList = sampleSentences
////        ),
////        onIntent = {}
////    )
//}