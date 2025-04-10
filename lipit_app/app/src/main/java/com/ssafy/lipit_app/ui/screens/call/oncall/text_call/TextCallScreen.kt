package com.ssafy.lipit_app.ui.screens.call.oncall.text_call

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ssafy.lipit_app.R
import com.ssafy.lipit_app.data.model.ChatMessageText
import com.ssafy.lipit_app.ui.components.TestLottieLoadingScreen
import com.ssafy.lipit_app.ui.screens.call.oncall.ModeChangeButton
import com.ssafy.lipit_app.ui.screens.call.oncall.text_call.components.TextCallFooter
import com.ssafy.lipit_app.ui.screens.call.oncall.text_call.components.TextCallHeader
import com.ssafy.lipit_app.ui.screens.call.oncall.text_call.components.Translate.TextCallWithTranslate
import com.ssafy.lipit_app.ui.screens.call.oncall.text_call.components.Translate.TextCallwithOriginalOnly
import com.ssafy.lipit_app.ui.screens.call.oncall.voice_call.VoiceCallViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun TextCallScreen(
    viewModel: TextCallViewModel,
    onIntent: (TextCallIntent) -> Unit,
    navController: NavController,
    onModeToggle: () -> Unit,
    voiceCallViewModel: VoiceCallViewModel
) {
    val listState = rememberLazyListState()
    val context = LocalContext.current
    val voiceCallState by voiceCallViewModel.state.collectAsState()
    val isKeyboardOpen = isKeyboardOpen()

    val state = viewModel.state.collectAsState().value
    Log.d("TextCall", "📦 메시지 수: ${state.messages.size}")

    // 로딩 화면 보여주기
    if (voiceCallState.isLoading) {
        TestLottieLoadingScreen("리포트 생성 중...")
    }

    LaunchedEffect(
        key1 = voiceCallState.isCallEnded,
        key2 = voiceCallState.isReportCreated
    ) {
        if (voiceCallState.isCallEnded && voiceCallState.isReportCreated) {
            Log.d("TextCallScreen", "📍 종료됨 + 리포트 생성됨 → 이동")
            voiceCallViewModel._state.update { it.copy(isLoading = true) }

            delay(15000L) // 로딩 보여주는 시간

            voiceCallViewModel._state.update { it.copy(isLoading = false) }

            navController.navigate("reports?refresh=true") {
                popUpTo("main") { inclusive = false }
                launchSingleTop = true
            }

        }


        // 통화 종료는 됐지만 리포트 생성 실패한 경우 처리
        if (voiceCallState.isCallEnded && !voiceCallState.isReportCreated) {
            Log.d("TextCallScreen", "❗ 종료됨 + 리포트 생성 실패 → 다이얼로그 표시")
            voiceCallViewModel._state.update { it.copy(reportFailed = true) }
        }
    }

    LaunchedEffect(voiceCallViewModel.aiMessage) {
        if (voiceCallViewModel.aiMessage.isNotBlank() &&
            voiceCallViewModel.state.value.currentMode == "Text"
        ) {
            Log.d("TextCallScreen", "🤖 AI 응답 감지됨 → TextCallViewModel에 추가")

            viewModel.addMessage(
                ChatMessageText(
                    text = voiceCallViewModel.aiMessage,
                    translatedText = voiceCallViewModel.aiMessageKor,
                    isFromUser = false
                )
            )

            voiceCallViewModel.clearAiMessage()
        }
    }



    if (voiceCallViewModel.state.value.reportFailed && !voiceCallViewModel.state.value.isReportCreated) {
        AlertDialog(
            onDismissRequest = {
                voiceCallViewModel.resetCall()
                navController.navigate("main") {
                    popUpTo("call_screen") { inclusive = true }
                }
            },
            title = { Text("Report 생성 실패", fontWeight = FontWeight.Bold) },
            text = { Text("사용 글자 수가 100자 이하인 경우, 리포트가 생성되지 않습니다.") },
            confirmButton = {
                Text(
                    "확인",
                    modifier = Modifier.clickable {
                        voiceCallViewModel.resetCall()
                        navController.navigate("main") {
                            popUpTo("call_screen") { inclusive = true }
                        }
                    }
                )
            }
        )
    }

    // 대화 내역이 바뀌면 마지막으로 스크롤
    LaunchedEffect(state.messages.size) {
        listState.animateScrollToItem(state.messages.size)
    }

    // 키보드가 열리면 마지막 메시지로 스크롤
    LaunchedEffect(isKeyboardOpen) {
        if (isKeyboardOpen && state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.size - 1)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth(),
        //contentAlignment = Alignment.TopCenter,
    ) {
        // 배경
        Image(
            painterResource(
                id = R.drawable.incoming_call_background
            ),
            contentDescription = "배경",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 20.dp, end = 20.dp)
            ) {
                // 상단 여백
                Spacer(modifier = Modifier.height(55.dp))


                // 모드 변경
                ModeChangeButton(
                    currentMode = state.currentMode,
                    onToggle = {
                        // 모드 전환: 텍스트 → 보이스로 바꾸는 시점이면 chatMessages 동기화
                        voiceCallViewModel.syncFromTextMessages(viewModel.getMessages())
                        onModeToggle()
                    }
                )

                // 헤더 (VoiceName, 남은 시간, 끊기 버튼)
                TextCallHeader(
                    voiceName = voiceCallState.voiceName,
                    leftTime = voiceCallState.leftTime,
                    voiceCallViewModel = voiceCallViewModel
                )


                // 대화 내역(채팅 ver.)
                Box(
                    modifier = Modifier.weight(1f)
                        .fillMaxWidth()
                ) {
                    TextVersionCall(state, onIntent, listState)
                }

                // 하단 영역 (텍스트 입력 공간, 번역 여부 및 텍스트 보내기 버튼)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {

                    Spacer(modifier = Modifier.height(18.dp))
                    TextCallFooter(state.inputText, state.showTranslation, onIntent = onIntent)

                    if (isKeyboardOpen) {
                        Spacer(modifier = Modifier.height(30.dp)) // 키보드 열렸을 때만 여백 줌
                    } else {
                        Spacer(modifier = Modifier.height(5.dp))
                    }
                }

            }
        }
    }
}

@Composable
fun TextVersionCall(
    state: TextCallState,
    onIntent: (TextCallIntent) -> Unit,
    listState: LazyListState
) {
    // 번역 여부에 따라 UI 달라짐
    when {
        state.showTranslation -> TextCallWithTranslate(state, listState)
        else -> TextCallwithOriginalOnly(state)
    }
}

@Composable
fun isKeyboardOpen(): Boolean {
    val ime = androidx.compose.foundation.layout.WindowInsets.ime
    return ime.getBottom(androidx.compose.ui.platform.LocalDensity.current) > 0
}

@Preview(showBackground = true)
@Composable
fun TextCallScreenPreview() {
    // 미리보기용 더미 ViewModel 생성
    val previewTextCallViewModel = TextCallViewModel()
    val previewVoiceCallViewModel = VoiceCallViewModel()

    // 더미 데이터 추가
    previewTextCallViewModel.addMessage(
        ChatMessageText(
            text = "Hello, how are you?",
            translatedText = "안녕하세요, 어떻게 지내세요?",
            isFromUser = true
        )
    )

    previewTextCallViewModel.addMessage(
        ChatMessageText(
            text = "I'm doing well, thank you! How about you?",
            translatedText = "잘 지내고 있어요, 감사합니다! 당신은 어떠세요?",
            isFromUser = false
        )
    )

    // 더미 상태 설정
    previewVoiceCallViewModel._state.value = previewVoiceCallViewModel._state.value.copy(
        voiceName = "Sarah",
        leftTime = "04:30",
        currentMode = "Text"
    )

    // 미리보기 렌더링
    TextCallScreen(
        viewModel = previewTextCallViewModel,
        onIntent = {},
        navController = rememberNavController(),
        onModeToggle = {},
        voiceCallViewModel = previewVoiceCallViewModel
    )
}

// 클래스 외부에 미리보기를 위한 보조 함수 추가
@Composable
fun rememberNavController(): NavController {
    val context = LocalContext.current
    return remember { NavController(context) }
}