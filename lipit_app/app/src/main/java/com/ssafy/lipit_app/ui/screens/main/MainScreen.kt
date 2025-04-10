package com.ssafy.lipit_app.ui.screens.main

import android.app.Activity
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.lipit_app.R
import com.ssafy.lipit_app.ui.screens.auth.components.MypagePopup
import com.ssafy.lipit_app.ui.screens.call.alarm.AlarmScheduler
import com.ssafy.lipit_app.ui.screens.call.alarm.CallActionReceiver
import com.ssafy.lipit_app.ui.screens.call.alarm.DailyCallTracker
import com.ssafy.lipit_app.ui.screens.edit_call.change_voice.EditVoiceScreen
import com.ssafy.lipit_app.ui.screens.edit_call.change_voice.EditVoiceState
import com.ssafy.lipit_app.ui.screens.edit_call.reschedule.EditCallScreen
import com.ssafy.lipit_app.ui.screens.edit_call.weekly_calls.WeeklyCallsIntent
import com.ssafy.lipit_app.ui.screens.edit_call.weekly_calls.WeeklyCallsScreen
import com.ssafy.lipit_app.ui.screens.main.components.DailySentenceManager
import com.ssafy.lipit_app.ui.screens.main.components.NextLevel
import com.ssafy.lipit_app.ui.screens.main.components.ReportAndVoiceBtn
import com.ssafy.lipit_app.ui.screens.main.components.TodaysSentence
import com.ssafy.lipit_app.ui.screens.main.components.WeeklyCallsSection
import com.ssafy.lipit_app.util.SharedPreferenceUtils
import java.time.LocalDate


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun MainScreen(
    onIntent: (MainIntent) -> Unit,
    viewModel: MainViewModel,
    onSuccess: () -> Unit
) {
    val state by viewModel.state.collectAsState() // 고정된 값이 아닌 상태 관찰 -> 실시간 UI 반영
    val context = LocalContext.current


    // ***** Bottom Sheet 관리 : show/hide 처리
    val bottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    LaunchedEffect(state.isSettingsSheetVisible) {
        if (state.isSettingsSheetVisible) {
            bottomSheetState.show()
        } else {
            bottomSheetState.hide()
        }
    }
    LaunchedEffect(bottomSheetState.isVisible) {
        if (!bottomSheetState.isVisible) {
            onIntent(MainIntent.OnCloseSettingsSheet)
            onIntent(MainIntent.ResetBottomSheetContent)

            onIntent(MainIntent.RefreshAfterVoiceChange) // 바텀 시트 닫히면 새로고침
        }
    }



    LaunchedEffect(Unit) {
        val memberId = SharedPreferenceUtils.getMemberId()
        viewModel.fetchUserLevel(memberId)
        viewModel.fetchWeeklySchedule(memberId)

        DailySentenceManager.init(context)
    }


    // ***** 뒤로가기 핸들링
    // BottomSheet 가 있으면 닫기 / 아무것도 없을 경우 두번 빠르게 눌러 앱 종료
    BackHandler(enabled = bottomSheetState.isVisible) {
        // 바텀시트가 열려있을 때 → 닫기
        onIntent(MainIntent.OnCloseSettingsSheet)
        onIntent(MainIntent.ResetBottomSheetContent)
    }

    var backPressedTime by remember { mutableStateOf(0L) }
    BackHandler(enabled = !bottomSheetState.isVisible) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - backPressedTime < 2000) {
            // 앱 종료
            (context as? Activity)?.finish()
        } else {
            backPressedTime = currentTime
            Toast.makeText(context, "한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
        }
    }

    // ***** BottomSheet 분기를위한 필드
    val editVoiceState: EditVoiceState = EditVoiceState()


    // 로그아웃 관련
    LaunchedEffect(key1 = state.isLogoutSuccess) {
        if (state.isLogoutSuccess) {
            Log.d("auth", "MainScreen: LaunchedEffect onSuccess 호출")
            onSuccess()
            onIntent(MainIntent.OnLogoutHandled)
        }
    }

    // 회원 등급 및 weekly call 스케줄 관련
    LaunchedEffect(Unit) {
        val memberId = SharedPreferenceUtils.getMemberId()
        viewModel.fetchUserLevel(memberId)
        //viewModel.fetchWeeklySchedule(memberId)
    }


    //val state by viewModel.state.collectAsState()
    // 1. (default: hide) BottomSheet 3가지 종류 : 일주일 스케줄, 수정, 보유 음성
    // 2. (Base) MainScreen
    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetBackgroundColor = Color.Transparent,
        sheetContent = {
            Surface(
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                color = Color(0xFFFDF8FF),
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.9f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 10.dp)
                ) {
                    // 핸들바
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(vertical = 8.dp)
                            .size(width = 40.dp, height = 4.dp)
                            .background(Color.LightGray, RoundedCornerShape(2.dp))
                    )

                    // 상태에 따라 바텀시트 내용 분기
                    when (state.bottomSheetContent) {
                        BottomSheetContent.WEEKLY_CALLS -> {
                            WeeklyCallsScreen(
                                state = state.weeklyCallsState,
                                onIntent = { intent ->
                                    when (intent) {
                                        is WeeklyCallsIntent.OnEditSchedule -> {
                                            onIntent(MainIntent.SelectSchedule(intent.schedule))
                                            onIntent(MainIntent.ShowRescheduleScreen(intent.schedule))
                                        }

                                        is WeeklyCallsIntent.OnChangeVoice -> {
                                            onIntent(MainIntent.ShowMyVoicesScreen)
                                        }

                                        else -> {}
                                    }
                                },
                                onMainIntent = onIntent
                            )
                        }

                        BottomSheetContent.RESCHEDULE_CALL -> {
                            val schedule = state.selectedSchedule
                            EditCallScreen(
                                schedule = schedule!!,
                                onBack = {
                                    onIntent(MainIntent.ShowWeeklyCallsScreen)
                                },
                                onSuccess = { updatedSchedule, isEditMode ->
                                    // 알람 수정, 삭제 작업이 성공적으로 완료되면 이쪽으로 onSuccess 응답이 온다.
                                    Log.d(
                                        "MainScreen",
                                        "Plan ${if (isEditMode) "수정" else "추가"} OK: $updatedSchedule"
                                    )


                                    // 먼저 데이터 갱신
                                    onIntent(MainIntent.RefreshAfterVoiceChange)  // 메인 화면 데이터 갱신
                                    onIntent(MainIntent.OnSettingsClicked)        // 바텀시트 데이터 갱신
                                    onIntent(MainIntent.ShowWeeklyCallsScreen)    // 바텀시트 화면 전환

                                }
                            )
                        }

                        BottomSheetContent.MY_VOICES -> {
                            EditVoiceScreen(
                                onBack = {
                                    onIntent(MainIntent.RefreshAfterVoiceChange)
                                    onIntent(MainIntent.OnCloseSettingsSheet)
                                },
                                onNavigateToAddVoice = {
                                    onIntent(MainIntent.NavigateToAddVoice)
                                }
                            )
                        }
                    }
                }
            }
        }
    ) {
        // ***** 기존 MainScreen UI
        var selectedDay by remember { mutableStateOf(state.selectedDay) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFDF8FF))
                .padding(start = 20.dp, end = 20.dp, top = 70.dp),

            ) {

            // 스크롤 버전 영역
            Column(
                modifier = Modifier
                    .weight(0.88f)
                    .verticalScroll(rememberScrollState())
            ) {
                UserInfoSection(state.userName, state, onIntent, state.level) // 상단의 유저 이름, 등급 부분
                TodaysSentence(viewModel, context) // 오늘의 문장

                WeeklyCallsSection(
                    selectedDay = selectedDay,
                    callItems = state.callItems,
                    onIntent = {
                        Log.d("TAG", "MainScreen: ${state.callItems}")
                        if (it is MainIntent.OnDaySelected) {
                            selectedDay = it.day
                        } else {
                            onIntent(it) // 나머지 이벤트 넘기기 (예: OnSettingsClicked)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 리포트 & 마이 보이스로 넘어가는 버튼들
                ReportAndVoiceBtn(onIntent)

                // 레벨업 파트
                NextLevel(
                    reportPercentage = state.reportPercent,
                    callTimePercentage = state.callPercent
                )
            }


            // 전화 걸기 버튼
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.12f),
                contentAlignment = Alignment.Center
            ) {
                CallButton(onIntent)
            }

            Spacer(modifier = Modifier.weight(0.06f))

        }
    }
}


// 전화 걸기 버튼
@Composable
fun CallButton(onIntent: (MainIntent) -> Unit) {

    val context = LocalContext.current
    Image(
        painterResource(id = R.drawable.main_call_icon),
        contentDescription = "전화 걸기",
        modifier = Modifier
            .size(70.dp)
            .clip(CircleShape)
            .clickable {
                // 오늘 통화 완료로 표시
                DailyCallTracker.markTodayCallCompleted(context)

                // 모든 예약된 알람 취소 (당일것만)
                val alarmScheduler = AlarmScheduler(context)
                val baseAlarmId = LocalDate.now().dayOfYear // 오늘 날짜 기반 알람 ID
                alarmScheduler.cancelAllTodayAlarms(baseAlarmId, CallActionReceiver.MAX_RETRY_COUNT)

                // 화면 이동
                onIntent(MainIntent.NavigateToCallScreen)
            }
    )
}


// 사용자 정보 (이름 & 등급)
@Composable
fun UserInfoSection(
    userName: String,
    state: MainState,
    onIntent: (MainIntent) -> Unit,
    level: Int
) {
    var showPopup by remember { mutableStateOf(false) } // 로그아웃 팝업 관련

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                // 누르면 팝업으로 로그아웃 버튼 (추후 다른 버튼도 추가하던지..)
                .clickable(
                    indication = null,
                    interactionSource = remember {
                        MutableInteractionSource()
                    }
                ) {
                    showPopup = true
                }
        ) {
            //  사용자 이름
            Text(
                text = "Hello, $userName",
                style = TextStyle(
                    fontSize = 24.sp,
                    lineHeight = 50.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF000000),
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            // 사용자 등급
            Image(
                painter = painterResource(id = getLevelIcon(level)),
                contentDescription = "사용자 등급",
                modifier = Modifier.size(26.dp)
            )

            // 로그아웃 팝업 관련
            if (showPopup) {
                MypagePopup(
                    onDismissRequest = { showPopup = false },
                    onConfirmation = {
                        showPopup = false
                        // 로그아웃 로직 처리
                        onIntent(MainIntent.OnLogoutClicked)
                    },
                    dialogTitle = "로그아웃 하시겠습니까?",
                    dialogText = "로그아웃하고 앱에서 나가기"
                )
            }
        }

        Image(
            painter = painterResource(id = R.drawable.ic_logout),
            contentDescription = null,
            modifier = Modifier
                .size(22.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember {
                        MutableInteractionSource()
                    }
                ) {
                    showPopup = true
                }
        )
    }
}


// 레벨 등급에 따른 아이콘 매핑
@Composable
fun getLevelIcon(level: Int): Int {
    return when (level) {
        1 -> R.drawable.user_level_1
        2 -> R.drawable.user_level_2
        3 -> R.drawable.user_level_3
        4 -> R.drawable.user_level_4
        5 -> R.drawable.user_level_5
        else -> R.drawable.user_level_1 // 기본값
    }
}


