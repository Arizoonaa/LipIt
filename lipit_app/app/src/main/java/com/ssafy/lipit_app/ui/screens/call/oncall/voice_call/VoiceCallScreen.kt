package com.ssafy.lipit_app.ui.screens.call.oncall.voice_call

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.ssafy.lipit_app.R
import com.ssafy.lipit_app.data.model.ChatMessage
import com.ssafy.lipit_app.data.model.ChatMessageText
import com.ssafy.lipit_app.ui.components.ListeningUi
import com.ssafy.lipit_app.ui.components.TestLottieLoadingScreen
import com.ssafy.lipit_app.ui.screens.call.alarm.AlarmScheduler
import com.ssafy.lipit_app.ui.screens.call.alarm.CallActionReceiver
import com.ssafy.lipit_app.ui.screens.call.oncall.ModeChangeButton
import com.ssafy.lipit_app.ui.screens.call.oncall.text_call.TextCallScreen
import com.ssafy.lipit_app.ui.screens.call.oncall.text_call.TextCallViewModel
import com.ssafy.lipit_app.ui.screens.call.oncall.voice_call.components.CallActionButtons
import com.ssafy.lipit_app.ui.screens.call.oncall.voice_call.components.Subtitle.CallWithSubtitleAndTranslate
import com.ssafy.lipit_app.ui.screens.call.oncall.voice_call.components.Subtitle.CallWithSubtitleOriginalOnly
import com.ssafy.lipit_app.ui.screens.call.oncall.voice_call.components.Subtitle.CallWithoutSubtitle
import com.ssafy.lipit_app.ui.screens.call.oncall.voice_call.components.VoiceCallHeader
import com.ssafy.lipit_app.util.SharedPreferenceUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun VoiceCallScreen(
    onIntent: (VoiceCallIntent) -> Unit,
    viewModel: VoiceCallViewModel,
    navController: NavController,
    textCallViewModel: TextCallViewModel
) {
    val context = LocalContext.current
    val textState = remember { mutableStateOf("") }
    val chatMessages = viewModel.chatMessages
    val state by viewModel.state.collectAsState()
    val toastMessage = remember { mutableStateOf<String?>(null) }
    val isAudioLoading by viewModel.isAudioLoading.collectAsState()

    // 서버 연결 에러 날 때 다이얼로그 띄우기
    if (viewModel.connectionError.value && !viewModel.state.value.isReportCreated) {
        AlertDialog(
            onDismissRequest = { viewModel.connectionError.value = false },
            title = { Text("⚠️ 서버 연결 실패") },
            text = { Text("서버와의 연결에 실패했습니다. 인터넷을 확인하거나 서버 상태를 확인해주세요.") },
            confirmButton = {
                Text(
                    "확인",
                    modifier = Modifier.clickable {
                        viewModel.connectionError.value = false
                        navController.navigate("main") {
                            popUpTo("call_screen") { inclusive = true }
                        }
                    }
                )
            }
        )
    }

    if (isAudioLoading) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loader))

        if (composition != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                LottieAnimation(
                    composition = composition,
                    iterations = LottieConstants.IterateForever,
                    modifier = Modifier
                        .width(64.dp)
                        .height(64.dp)
                )
            }
        }
    }


    // 가장 먼저 Player 초기화
    LaunchedEffect(Unit) {
        viewModel.fetchTodayTopicAndStartCall()

        viewModel.initPlayerIfNeeded(context)
        textCallViewModel.setInitialMessages(viewModel.convertToTextMessages())

        if (!viewModel.isCountdownRunning()) {
            viewModel.startCountdown(context)
        }

        viewModel.getLastAiMessage()?.let { lastAi ->
            onIntent(VoiceCallIntent.UpdateSubtitle(lastAi.text))
            onIntent(VoiceCallIntent.UpdateTranslation(lastAi.translatedText))
        }

        cancelAllTodayAlarms(context)
    }


    // 토스트 메시지 표시
    LaunchedEffect(toastMessage.value) {
        toastMessage.value?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            toastMessage.value = null
        }
    }

    // VoiceName 상태 변경 로그 출력
    LaunchedEffect(state.voiceName) {
        Log.d("VoiceCallScreen", "📣 state.voiceName 변경됨: ${state.voiceName}")
    }

    // 퍼미션 체크
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (context is Activity) {
                ActivityCompat.requestPermissions(
                    context,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    1000
                )
            }
        }
    }

    // 시스템 메시지 수신 시 UI에 반영
    LaunchedEffect(viewModel.systemMessage.value) {
        viewModel.systemMessage.value?.let { msg ->
            chatMessages.add(ChatMessage("system", msg))
            viewModel.clearSystemMessage()
        }
    }

    // 초기화 로직 수행
    LaunchedEffect(Unit) {
        viewModel.loadVoiceName(memberId = SharedPreferenceUtils.getMemberId())
    }

    // AI 응답 수신 처리
    LaunchedEffect(viewModel.aiMessage) {
        if (viewModel.aiMessage.isNotBlank()) {
            viewModel.onReceiveAIMessage(viewModel.aiMessage)

            viewModel.addAiMessage(viewModel.aiMessage, viewModel.aiMessageKor)

            Log.d("VoiceCallScreen", "🤖 AI: ${viewModel.aiMessage}")
            Log.d("VoiceCallScreen", "🤖 currentMode: ${state.currentMode}")


            // 자막용 업뎃
            onIntent(VoiceCallIntent.UpdateSubtitle(viewModel.aiMessage))
            onIntent(VoiceCallIntent.UpdateTranslation(viewModel.aiMessageKor))

            // 텍스트 모드일 때 텍스트 뷰모델에도 반영
            if (viewModel.state.value.currentMode == "Text") {
                textCallViewModel.addMessage(
                    ChatMessageText(
                        text = viewModel.aiMessage,
                        translatedText = viewModel.aiMessageKor,
                        isFromUser = false
                    )
                )
            }

            viewModel.clearAiMessage()
        }
    }

    // 유저 음성 인식 결과 로그 출력
    LaunchedEffect(viewModel.latestSpeechResult) {
        if (viewModel.latestSpeechResult.isNotBlank()) {
            Log.d("VoiceCallScreen", "🗣️ User(STT): ${viewModel.latestSpeechResult}")
            viewModel.clearLatestSpeechResult()
        }
    }

    // 통화 종료 후 이동
    // 로딩 화면 보여주기
    if (state.isLoading) {
        TestLottieLoadingScreen("리포트 생성 중...")
    }


    LaunchedEffect(
        key1 = state.isCallEnded,
        key2 = state.isReportCreated
    ) {
        if (state.isCallEnded && state.isReportCreated) {
            Log.d("VoiceCallScreen", "📍 종료됨 + 리포트 생성됨 → 이동")
            viewModel._state.update { it.copy(isLoading = true) }

            delay(7000L) // 리포트 로딩 보여주는 시간

            viewModel._state.update { it.copy(isLoading = false) }

            navController.navigate("reports?refresh=true") {
                popUpTo("main") { inclusive = false }
                launchSingleTop = true
            }
        }

        // 통화 종료는 됐지만 리포트 생성 실패한 경우 처리
        if (state.isCallEnded && !state.isReportCreated) {
            Log.d("TextCallScreen", "❗ 종료됨 + 리포트 생성 실패 → 다이얼로그 표시")
            viewModel._state.update { it.copy(reportFailed = true) }
        }
    }


    if (viewModel.state.value.reportFailed && !viewModel.state.value.isReportCreated) {
        AlertDialog(
            onDismissRequest = {
                viewModel.resetCall()
                navController.navigate("main") {
                    popUpTo("call_screen") { inclusive = true }
                }
            },
            title = { Text("조금 더 대화해주세요🥲", fontWeight = FontWeight.Bold) },
            text = { Text("사용 글자 수가 100자 이하인 경우, 리포트가 생성되지 않습니다.") },
            confirmButton = {
                Text(
                    "확인",
                    modifier = Modifier.clickable {
                        viewModel.resetCall()
                        navController.navigate("main") {
                            popUpTo("call_screen") { inclusive = true }
                        }
                    }
                )
            }
        )
    }


    // 전체 레이아웃 구성
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        // 배경 이미지
        Image(
            painter = painterResource(id = R.drawable.incoming_call_background),
            contentDescription = "배경",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 상단 영역: 헤더 + 자막
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 55.dp, start = 20.dp, end = 20.dp)
        ) {
            // 상단 콘텐츠 영역 (weight로 남은 공간 모두 차지)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                // 여기에 LazyColumn을 배치하여 스크롤 가능하게 만듦
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // 모드 변경 버튼
                    item {
                        ModeChangeButton(
                            currentMode = state.currentMode,
                            onToggle = {
                                viewModel.toggleMode()
                            }
                        )
                    }

                    // 헤더
                    item {
                        VoiceCallHeader(state.leftTime, viewModel, state.voiceName)
                        Spacer(modifier = Modifier.height(28.dp))
                    }

                    // 자막 영역
                    item {
                        VoiceVersionCall(state, onIntent)
                    }

                    // 필요에 따라 여기에 더 많은 item 추가 가능
                    // 스크롤에 의해 자동으로 처리됨
                }

                // 음성 인식 중 표시 (화면 하단 가운데 위치)
                if (viewModel.isListening) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter),
                        contentAlignment = Alignment.Center
                    ) {
                        ListeningUi()
                    }
                }

                // 오디오 로딩 애니메이션
                if (isAudioLoading) {
                    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.voice_loading))
                    if (composition != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            LottieAnimation(
                                composition = composition,
                                iterations = LottieConstants.IterateForever,
                                modifier = Modifier
                                    .width(80.dp)
                                    .height(80.dp)
                            )
                        }
                    }
                }
            }

            // 하단 버튼 영역 (고정 높이)
            CallActionButtons(
                state = state,
                onIntent = onIntent,
                viewModel = viewModel,
                navController = navController,
                textState = textState,
                textCallViewModel = textCallViewModel
            )
        }
    }

}

@Composable
fun VoiceVersionCall(state: VoiceCallState, onIntent: (VoiceCallIntent) -> Unit) {
    when {
        state.showSubtitle && state.showTranslation -> CallWithSubtitleAndTranslate(state)
        state.showSubtitle -> CallWithSubtitleOriginalOnly(state)
        else -> CallWithoutSubtitle()
    }
}

@Composable
fun CallScreen(voiceViewModel: VoiceCallViewModel, navController: NavController) {
    val currentMode by voiceViewModel.state.collectAsState()
    val textViewModel: TextCallViewModel = viewModel()
    Log.d("DEBUG", "CallScreen recomposed - currentMode: ${currentMode.currentMode}")

    when (currentMode.currentMode) {
        "Voice" -> VoiceCallScreen(
            onIntent = { voiceViewModel.onIntent(it) },
            viewModel = voiceViewModel,
            navController = navController,
            textCallViewModel = textViewModel
        )

        "Text" -> {
            LaunchedEffect(Unit) {
                textViewModel.setInitialMessages(voiceViewModel.convertToTextMessages())
            }

            TextCallScreen(
                viewModel = textViewModel,
                navController = navController,
                onModeToggle = { voiceViewModel.toggleMode() },
                onIntent = { intent ->
                    textViewModel.onIntent(intent) { userText ->
                        voiceViewModel.sendText(userText)
                    }
                },
                voiceCallViewModel = voiceViewModel
            )

        }
    }
}

private fun cancelAllTodayAlarms(context: Context) {
    val alarmScheduler = AlarmScheduler(context)
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    // 등록된 알람 ID 찾기
    val registeredAlarmIds = prefs.all.keys
        .filter { it.startsWith(SharedPreferenceUtils.PREF_ALARM_REGISTERED_PREFIX) }
        .map { it.removePrefix(SharedPreferenceUtils.PREF_ALARM_REGISTERED_PREFIX).toInt() }

    // 각 알람 취소
    for (alarmId in registeredAlarmIds) {
        // 기본 알람 취소
        alarmScheduler.cancelAlarm(alarmId)

        // 재시도 알람도 모두 취소 (최대 재시도 횟수만큼)
        for (i in 1..CallActionReceiver.MAX_RETRY_COUNT) {
            val retryAlarmId = alarmId + 1000 + i
            alarmScheduler.cancelAlarm(retryAlarmId)

            // SharedPreferences에서도 삭제
            val registeredKey = SharedPreferenceUtils.PREF_ALARM_REGISTERED_PREFIX + retryAlarmId
            val timestampKey = SharedPreferenceUtils.PREF_ALARM_TIMESTAMP_PREFIX + retryAlarmId
            SharedPreferenceUtils.remove(registeredKey)
            SharedPreferenceUtils.remove(timestampKey)
        }

        // 원래 알람 정보도 삭제
        val registeredKey = SharedPreferenceUtils.PREF_ALARM_REGISTERED_PREFIX + alarmId
        val timestampKey = SharedPreferenceUtils.PREF_ALARM_TIMESTAMP_PREFIX + alarmId
        SharedPreferenceUtils.remove(registeredKey)
        SharedPreferenceUtils.remove(timestampKey)

        Log.d("VoiceCallScreen", "오늘의 모든 알람 취소: ID=$alarmId 및 관련 재시도 알람")
    }

    Log.d("VoiceCallScreen", "오늘 예정된 모든 알람 취소 완료")
}

@SuppressLint("StateFlowValueCalledInComposition")
@Preview(showBackground = true)
@Composable
fun VoiceCallScreenPreview() {
    // 미리보기용 더미 ViewModel 생성
    val previewViewModel = VoiceCallViewModel()
    val previewTextViewModel = TextCallViewModel()
    val previewNavController = rememberNavController()

    // 더미 상태 설정
    previewViewModel._state.value = VoiceCallState(
        currentMode = "Voice",
        voiceName = "Sarah",
        leftTime = "04:30",
        showSubtitle = true,
        showTranslation = true,
        isCallEnded = false,
        isReportCreated = false
    )

    // 더미 메시지 추가
    previewViewModel.addAiMessage(
        "I'm doing well, thank you! How about you?",
        "잘 지내고 있어요, 감사합니다! 당신은 어떠세요?"
    )

    // 미리보기 렌더링
    VoiceCallScreen(
        onIntent = {},
        viewModel = previewViewModel,
        navController = previewNavController,
        textCallViewModel = previewTextViewModel
    )
}

@SuppressLint("StateFlowValueCalledInComposition")
@Preview(showBackground = true)
@Composable
fun CallScreenPreview() {
    // 미리보기용 더미 ViewModel 생성
    val previewViewModel = VoiceCallViewModel()
    val previewNavController = rememberNavController()

    // 더미 상태 설정 (Voice 모드)
    previewViewModel._state.value = VoiceCallState(
        currentMode = "Voice",
        voiceName = "Sarah",
        leftTime = "04:30",
        showSubtitle = true,
        showTranslation = true,
        isCallEnded = false,
        isReportCreated = false
    )

    // 더미 메시지 추가
    previewViewModel.addAiMessage(
        "I'm doing well, thank you! How about you?",
        "잘 지내고 있어요, 감사합니다! 당신은 어떠세요?"
    )

    // 미리보기 렌더링
    CallScreen(
        voiceViewModel = previewViewModel,
        navController = previewNavController
    )
}

// NavController를 미리보기에서 사용하기 위한 도우미 함수
@Composable
fun rememberNavController(): NavController {
    val context = LocalContext.current
    return remember { NavController(context) }
}