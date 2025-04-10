package com.ssafy.lipit_app.ui.screens.edit_call.add_voice

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.lipit_app.R
import com.ssafy.lipit_app.ui.screens.edit_call.add_voice.components.ErrorDialog
import com.ssafy.lipit_app.ui.screens.edit_call.add_voice.components.SuccessDialog
import com.ssafy.lipit_app.ui.screens.edit_call.add_voice.components.VoiceNameInputScreen

@Composable
fun AddVoiceScreen(
    state: AddVoiceState,
    onIntent: (AddVoiceIntent) -> Unit,
) {
    // 기본 화면은 항상 보여줌 : 녹음하는 화면 or 녹음 완료 화면
    when {
        state.isAllSentencesRecorded -> {
            VoiceNameInputScreen(state, onIntent)
        }
        else -> {
            RecordingScreen(state, onIntent)
        }
    }

    // 커스텀보이스 생성후 다이얼로그는 화면 위에 얹는 형태로 조건부 표시
    if (state.uploadSuccess) {
        SuccessDialog(
            onDismiss = { onIntent(AddVoiceIntent.NavigateBackToMyVoices) }, // 변경
            onConfirm = { onIntent(AddVoiceIntent.NavigateBackToMyVoices) }  // 변경
        )
    }

    if (state.showErrorPopup && state.errorMessage != null) {
        ErrorDialog(
            errorMessage = state.errorMessage,
            onDismiss = { onIntent(AddVoiceIntent.DismissErrorDialog) }
        )
    }
}


// AddVoiceScreen.kt 파일의 RecordingScreen 함수 수정
// 분석 중 상태와 실패 상태에 대한 UI 처리 추가
@Composable
fun RecordingScreen(
    state: AddVoiceState,
    onIntent: (AddVoiceIntent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDF8FF)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(0.5f))

        // 제목
        Text(
            text = "Create Custom Voice",
            style = TextStyle(
                fontSize = 30.sp,
                lineHeight = 30.sp,
                fontWeight = FontWeight(700),
                color = Color(0xFF222124),
                textAlign = TextAlign.Center,
            )
        )

        Spacer(modifier = Modifier.height(5.dp))

        // 안내 문구
        Text(
            text = "조용한 환경에서 녹음해 주세요.",
            style = TextStyle(
                fontSize = 15.sp,
                lineHeight = 30.sp,
                fontWeight = FontWeight(400),
                color = Color(0xFF8A8A8A),
                textAlign = TextAlign.Center,
            )
        )

        Spacer(modifier = Modifier.weight(0.3f))

        // 녹음 상태
        Text(
            text = when (state.recordingStatus) {
                RecordingStatus.WAITING -> "녹음 대기중"
                RecordingStatus.RECORDING -> "녹음중..."
                RecordingStatus.ANALYZING -> "음성 분석 중..." // 새로운 상태 처리
                RecordingStatus.COMPLETED -> "녹음 완료"
                RecordingStatus.FAILED -> "녹음 분석 실패" // 새로운 상태 처리
            },
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight(700),
                color = when (state.recordingStatus) {
                    RecordingStatus.WAITING -> Color.Gray
                    RecordingStatus.RECORDING -> Color(0xFFE94F52)
                    RecordingStatus.ANALYZING -> Color(0xFF2196F3) // 분석 중일 때 파란색
                    RecordingStatus.COMPLETED -> Color(0xFF4CAF50)
                    RecordingStatus.FAILED -> Color(0xFFE94F52) // 실패 시 빨간색
                }
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 녹음 문장
        Text(
            text = state.sentenceList.getOrNull(state.currentSentenceIndex) ?: "문장을 불러오는 중 ..",
            style = TextStyle(
                fontSize = 20.sp,
                lineHeight = 30.sp,
                fontWeight = FontWeight(400),
                color = Color(0xFF222124),
                textAlign = TextAlign.Center,
            ),
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        // 인식된 텍스트 표시 (녹음 완료 또는 실패 시)
        if ((state.recordingStatus == RecordingStatus.COMPLETED || state.recordingStatus == RecordingStatus.FAILED)
            && state.recognizedText.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "인식된 내용: ${state.recognizedText}",
                style = TextStyle(
                    fontSize = 16.sp,
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Normal
                ),
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            // 정확도 표시
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = "정확도: ${(state.accuracy * 100).toInt()}%",
                style = TextStyle(
                    fontSize = 16.sp,
                    color = if (state.accuracy >= 0.8f) Color(0xFF4CAF50) else Color(0xFFE94F52),
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 문장 진행률
        Text(
            text = "${state.currentSentenceIndex + 1} / ${state.sentenceList.size}",
            style = TextStyle(
                fontSize = 14.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Light
            )
        )

        Spacer(modifier = Modifier.weight(0.3f))

        // 마이크 그림
        Image(
            painterResource(id = R.drawable.add_voice_mic_img),
            contentDescription = "마이크",
            modifier = Modifier
                .width(180.dp)
                .height(381.dp)
        )

        Spacer(modifier = Modifier.weight(0.4f))

        // 녹음 상태에 따른 버튼 UI 변경
        when (state.recordingStatus) {
            RecordingStatus.WAITING -> {
                // 녹음 시작 버튼
                Image(
                    painterResource(id = R.drawable.add_voice_start_icon),
                    contentDescription = "녹음 시작",
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .clickable { onIntent(AddVoiceIntent.StartRecording) }
                )
            }
            RecordingStatus.RECORDING -> {
                // 녹음 중지 버튼
                Image(
                    painterResource(id = R.drawable.add_voice_stop_icon),
                    contentDescription = "녹음 중지",
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .clickable { onIntent(AddVoiceIntent.StopRecording) }
                )
            }
            RecordingStatus.ANALYZING -> {
                // 분석 중일 때는 로딩 인디케이터 표시
                CircularProgressIndicator(
                    color = Color(0xFF2196F3),
                    modifier = Modifier.size(70.dp)
                )
            }
            RecordingStatus.COMPLETED -> {
                // 녹음 완료 후 버튼 표시 (정확도에 따라 다른 버튼 표시)
                Row(
                    modifier = Modifier.padding(bottom = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // 정확도가 낮을 경우에도 다시 녹음 버튼 표시
                    if (state.accuracy < 0.8f) {
                        Button(
                            onClick = { onIntent(AddVoiceIntent.StartRecording) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE57373)
                            )
                        ) {
                            Text("다시 녹음")
                        }
                    } else {
                        // 정확도가 높을 경우 다음 버튼 표시
                        Button(
                            onClick = { onIntent(AddVoiceIntent.NextSentence) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF9C27B0)
                            )
                        ) {
                            Text("다음")
                        }
                    }
                }
            }
            RecordingStatus.FAILED -> {
                // 녹음 실패 시 다시 녹음 버튼
                Button(
                    onClick = { onIntent(AddVoiceIntent.StartRecording) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE57373)
                    ),
                    modifier = Modifier.padding(bottom = 20.dp)
                ) {
                    Text("다시 녹음")
                }
            }
        }

        Spacer(modifier = Modifier.weight(0.5f))
    }
}// ...RecordingScreen()


@Preview(showBackground = true)
@Composable
fun AddVoiceScreenPreview() {
    val sampleSentences = listOf(
        "Hi, I'm glad to meet you today.",
        "Can you hear me clearly?",
        "Let's get started with our conversation.",
        "The weather is really nice today, isn't it?",
        "I enjoy reading books and watching movies.",
        "What are your hobbies?",
        "Please repeat after me, slowly and clearly.",
        "This is how I usually talk every day.",
        "You can speak naturally and confidently.",
        "Thank you for listening to my voice!"
    )

    val dummyState = AddVoiceState(
        voiceName = "MyVoice",
        selectedImageUri = null,
        isUploading = false,
        currentSentenceIndex = 0,
        sentenceList = sampleSentences,
        recordingStatus = RecordingStatus.WAITING,
        recognizedText = "",
        accuracy = 0.0f,
        showErrorPopup = false,
        errorMessage = null,
        isAllSentencesRecorded = false,
        uploadSuccess = false
    )

    AddVoiceScreen(
        state = dummyState,
        onIntent = {}
    )
}