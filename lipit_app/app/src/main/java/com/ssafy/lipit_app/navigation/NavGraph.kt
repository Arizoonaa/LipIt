package com.ssafy.lipit_app.navigation

import android.os.Build
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ssafy.lipit_app.base.SecureDataStore
import com.ssafy.lipit_app.ui.components.TestLottieLoadingScreen
import com.ssafy.lipit_app.ui.screens.auth.Login.LoginScreen
import com.ssafy.lipit_app.ui.screens.auth.Login.LoginViewModel
import com.ssafy.lipit_app.ui.screens.auth.Signup.SignupScreen
import com.ssafy.lipit_app.ui.screens.auth.Signup.SignupViewModel
import com.ssafy.lipit_app.ui.screens.auth.components.AuthStartScreen
import com.ssafy.lipit_app.ui.screens.call.incoming.IncomingCallScreen
import com.ssafy.lipit_app.ui.screens.call.incoming.IncomingCallViewModel
import com.ssafy.lipit_app.ui.screens.call.oncall.text_call.TextCallScreen
import com.ssafy.lipit_app.ui.screens.call.oncall.text_call.TextCallViewModel
import com.ssafy.lipit_app.ui.screens.call.oncall.voice_call.CallScreen
import com.ssafy.lipit_app.ui.screens.call.oncall.voice_call.VoiceCallViewModel
import com.ssafy.lipit_app.ui.screens.edit_call.add_voice.AddVoiceIntent
import com.ssafy.lipit_app.ui.screens.edit_call.add_voice.AddVoiceScreen
import com.ssafy.lipit_app.ui.screens.edit_call.add_voice.AddVoiceViewModel
import com.ssafy.lipit_app.ui.screens.edit_call.change_voice.EditVoiceScreen
import com.ssafy.lipit_app.ui.screens.edit_call.weekly_calls.WeeklyCallsScreen
import com.ssafy.lipit_app.ui.screens.edit_call.weekly_calls.WeeklyCallsViewModel
import com.ssafy.lipit_app.ui.screens.main.MainIntent
import com.ssafy.lipit_app.ui.screens.main.MainScreen
import com.ssafy.lipit_app.ui.screens.main.MainViewModel
import com.ssafy.lipit_app.ui.screens.my_voice.MyVoiceIntent
import com.ssafy.lipit_app.ui.screens.my_voice.MyVoiceScreen
import com.ssafy.lipit_app.ui.screens.my_voice.MyVoiceViewModel
import com.ssafy.lipit_app.ui.screens.onboarding.OnBoardingIntent
import com.ssafy.lipit_app.ui.screens.onboarding.OnBoardingScreen
import com.ssafy.lipit_app.ui.screens.onboarding.OnboardingViewModel
import com.ssafy.lipit_app.ui.screens.report.ReportIntent
import com.ssafy.lipit_app.ui.screens.report.ReportScreen
import com.ssafy.lipit_app.ui.screens.report.ReportViewModel
import com.ssafy.lipit_app.ui.screens.report.report_detail.ReportDetailScreen
import com.ssafy.lipit_app.ui.screens.report.report_detail.ReportDetailViewModel
import kotlinx.coroutines.launch

private const val TAG = "NavGraph"

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun NavGraph(
    navController: NavHostController,
    initialDestination: String? = null
) {

    val context = LocalContext.current
    val secureDataStore = SecureDataStore.getInstance(context)

    val isInitialized = remember { mutableStateOf(false) }
    // NavGraph 초기화 (한 번만 실행되도록)
    if (!isInitialized.value) {
        Log.d(
            TAG,
            "NavGraph 초기화: initialDestination=$initialDestination, hasToken=${secureDataStore.hasAccessTokenSync()}"
        )
        isInitialized.value = true
    }

    val startDestination = when {
        initialDestination == "onVoiceCall" -> "onVoiceCall"
        initialDestination == "inComingCall" -> "inComingCall"
        !secureDataStore.hasAccessTokenSync() -> "auth_start"
        !secureDataStore.isOnboardingCompletedSync() -> "onboarding" // 온보딩 완료 안 됐을 때
        else -> "main" // 토큰 있고 온보딩 완료됐을 때
    }

    DisposableEffect(navController) {
        val callback = NavController.OnDestinationChangedListener { controller, destination, _ ->
            val route = destination.route
            Log.d(TAG, "네비게이션 변경: $route (기대 시작 화면: $startDestination)")

            if (route == "main" && startDestination == "onVoiceCall") {
                Log.d(TAG, "main으로 자동 이동 감지, onVoiceCall로 다시 이동")
                controller.navigate("onVoiceCall") {
                    popUpTo("main") { inclusive = true }
                    launchSingleTop = true
                }
            }
        }

        navController.addOnDestinationChangedListener(callback)

        onDispose {
            navController.removeOnDestinationChangedListener(callback)
        }
    }


    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        composable("auth_start") {
            AuthStartScreen(
                onLoginClick = {
                    Log.d(TAG, "로그인 화면으로 이동 요청")
                    navController.navigate("login")
                },
                onSignupClick = {
                    Log.d(TAG, "회원가입 화면으로 이동 요청")
                    navController.navigate("join")
                }
            )
        }

        composable("test_loader") {
            TestLottieLoadingScreen()
        }


        composable("login") {
            Log.d(TAG, "login 화면 구성")
            val viewModel: LoginViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return LoginViewModel(context) as T
                    }
                }
            )
            val state by viewModel.state.collectAsState()
            LoginScreen(
                state = state,
                onIntent = { viewModel.onIntent(it) },
                onSuccess = {
                    // 로그인 성공 시 온보딩 체크
                    if (!secureDataStore.isOnboardingCompletedSync()) {
                        navController.navigate("onboarding") {
                            popUpTo("login") { inclusive = true }
                            launchSingleTop = true
                        }
                    } else {
                        navController.navigate("main") {
                            popUpTo("login") { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }

        composable("onboarding") {
            Log.d(TAG, "onboarding 화면 구성")
            val viewModel: OnboardingViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return OnboardingViewModel(context) as T
                    }
                }
            )

            // 상태 관찰
            val state by viewModel.state.collectAsState()
            val shouldNavigateToMain by viewModel.navigateToMain.collectAsState()

            // 메인 화면으로 네비게이션
            LaunchedEffect(shouldNavigateToMain) {
                if (shouldNavigateToMain) {
                    navController.navigate("main") {
                        popUpTo("auth_start") { inclusive = true }
                        launchSingleTop = true
                    }
                    viewModel.onIntent(OnBoardingIntent.NavigationComplete)
                }
            }

            // 화면 간 네비게이션 (state에서 navigateToMain 체크)
            LaunchedEffect(state.navigateToMain) {
                if (state.navigateToMain) {
                    navController.navigate("main") {
                        popUpTo("auth_start") { inclusive = true }
                        launchSingleTop = true
                    }
                    viewModel.onIntent(OnBoardingIntent.NavigationComplete)
                }
            }

            OnBoardingScreen(
                onFinish = {
                    // 마지막 단계에서 온보딩 완료 처리 (MVI 방식으로 Intent 전달)
                    viewModel.onIntent(OnBoardingIntent.CompleteOnboarding)
                }
            )
        }

        composable("join") {
            Log.d(TAG, "join 화면 구성")
            val viewModel = viewModel<SignupViewModel>()
            val state by viewModel.state.collectAsState()
            SignupScreen(
                state = state,
                onIntent = { viewModel.onIntent(it) },
                onSuccess = {
                    Log.d(TAG, "회원가입 성공: 로그인 화면으로 이동 요청")
                    navController.navigate("login")
                }
            )
        }


        composable("main") {
            Log.d(TAG, "main 화면 구성")
            val viewModel: MainViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return MainViewModel(context) as T
                    }
                }
            )

            MainScreen(
                onIntent = { intent ->
                    viewModel.viewModelScope.launch {
                        viewModel.onIntent(intent)
                    }

                    // Intent 유형에 따라 네비게이션 처리
                    when (intent) {
                        is MainIntent.NavigateToReports -> {
                            Log.d(TAG, "메인에서 레포트 화면으로 이동 요청")
                            navController.navigate("reports")
                        }

                        is MainIntent.NavigateToMyVoices -> {
                            Log.d(TAG, "메인에서 내 목소리 화면으로 이동 요청")
                            navController.navigate("my_voices")
                        }

                        is MainIntent.NavigateToCallScreen -> {
                            Log.d(TAG, "메인에서 통화 화면으로 이동 요청:  onVoiceCall")
                            navController.navigate("onVoiceCall")
                        }

                        is MainIntent.NavigateToAddVoice -> {
                            navController.navigate("add_voice")
                        }

                        else -> {
                        }
                    }
                },
                viewModel = viewModel,
                onSuccess = {
                    Log.d(TAG, "로그아웃: 시작 화면으로 이동 요청")
                    navController.navigate("auth_start") {
                        popUpTo("main") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable("call_screen") {
            val viewModel = viewModel<VoiceCallViewModel>()
            CallScreen(voiceViewModel = viewModel, navController = navController)
        }



        composable("my_voices") {
            Log.d(TAG, "my_voices 화면 구성")
            val viewModel = viewModel<MyVoiceViewModel>()
            val state by viewModel.state.collectAsState()

            MyVoiceScreen(
                state = state,
                onIntent = { intent ->
                    when (intent) {
                        is MyVoiceIntent.NavigateToAddVoice -> {
                            Log.d(TAG, "목소리 추가 화면으로 이동 요청")
                            navController.navigate("add_voice")
                        }

                        else -> {
                            Log.d(TAG, "MyVoice 인텐트 처리: $intent")
                            viewModel.onIntent(intent)
                        }
                    }
                }
            )
        }

        composable("editWeeklyCalls") {
            Log.d(TAG, "editWeeklyCalls 화면 구성")
            val viewModel = viewModel<WeeklyCallsViewModel>()
            val state by viewModel.state.collectAsState()

            WeeklyCallsScreen(
                state = state.weeklyState,
                onIntent = {
                    Log.d(TAG, "WeeklyCalls 인텐트 처리")
                    viewModel.onIntent(it)
                },
                onMainIntent = {}
            )
        }

        composable("inComingCall") {

            val viewModel = viewModel<IncomingCallViewModel>()
            val state by viewModel.state.collectAsState()

            IncomingCallScreen(
                onIntent = { viewModel.onIntent(it) },
                viewModel = viewModel,
                navController = navController
            )
        }

        composable("onVoiceCall") {
            Log.d(TAG, "onVoiceCall 화면 구성")

            val viewModel = viewModel<VoiceCallViewModel>()
            val state by viewModel.state.collectAsState()

            CallScreen(
                voiceViewModel = viewModel,
                navController = navController
            )
        }

        composable("onTextCall") {
            Log.d(TAG, "onTextCall 화면 구성")

            val viewModel = viewModel<TextCallViewModel>()

            TextCallScreen(
                viewModel = viewModel,
                onIntent = {
                    Log.d(TAG, "TextCall 인텐트 처리: $it")
                    viewModel.onIntent(it)
                },
                navController = navController,
                onModeToggle = {
                    navController.navigate("call_screen") {
                        popUpTo("onTextCall") { inclusive = true } // 이전 스택 제거 (선택사항)
                        launchSingleTop = true
                    }
                },
                voiceCallViewModel = VoiceCallViewModel()
            )

        }


        composable("add_voice") {
            val context = LocalContext.current
            val viewModel: AddVoiceViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return AddVoiceViewModel(context) as T
                    }
                }
            )

            // LaunchedEffect로 Context 초기화하기
            LaunchedEffect(key1 = Unit) {
                viewModel.setContext(context)
            }

            AddVoiceScreen(
                state = viewModel.state.collectAsState().value,
                onIntent = { intent ->
                    Log.d(TAG, "AddVoice 인텐트 처리: $intent")
                    when (intent) {
                        is AddVoiceIntent.NavigateBackToMyVoices -> {
                            // 메인으로 돌아가는 네비게이션 처리
                            navController.navigate("main") {
                                popUpTo("add_voice") { inclusive = true }
                            }
                        }

                        else -> viewModel.onIntent(intent)
                    }
                }
            )
        }

        composable(route = "edit_voice") {
            EditVoiceScreen(
                onBack = { navController.popBackStack() },
                onNavigateToAddVoice = {
                    Log.d(TAG, "add_voice로 네비게이션 시도")
                    navController.navigate("add_voice") {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        // 레포트 관련 화면들
        composable(route = "reports?refresh={refresh}",
            arguments = listOf(
                navArgument("refresh") {
                    defaultValue = "false"
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val shouldRefresh = backStackEntry.arguments?.getString("refresh")?.toBoolean() ?: false
            val viewModel = viewModel<ReportViewModel>()

            LaunchedEffect(shouldRefresh) {
                if (shouldRefresh) {
                    viewModel.refreshReportList()  // 새로고침 강제 트리거
                }
            }

            // 뒤로가기 처리 추가
            BackHandler {
                // 뒤로가기 시 메인으로 직접 이동
                navController.navigate("main") {
                    popUpTo("main") { inclusive = false }
                    launchSingleTop = true
                }
            }

            ReportScreen(
                state = viewModel.state.collectAsState().value,
                onIntent = { intent ->
                    when (intent) {
                        is ReportIntent.NavigateToReportDetail -> {
                            navController.navigate("report_detail_screen/${intent.reportId}")
                        }
                        else -> viewModel.onIntent(intent)
                    }
                },
                shouldRefresh = shouldRefresh
            )
        }


        composable(
            route = "report_detail_screen/{reportId}",
            arguments = listOf(navArgument("reportId") { type = NavType.LongType })
        ) { backStackEntry ->
            Log.d(TAG, "report_detail_screen 화면 구성")
            val reportId = backStackEntry.arguments?.getLong("reportId") ?: -1L
            Log.d(TAG, "레포트 상세 화면 ID: $reportId")

            val viewModel: ReportDetailViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return ReportDetailViewModel(reportId) as T
                    }
                }
            )

            ReportDetailScreen(
                reportId = reportId,
                state = viewModel.state.collectAsState().value,
                onIntent = {
                    Log.d(TAG, "ReportDetail 인텐트 처리: $it")
                    viewModel.onIntent(it)
                }
            )
        }
    }
}
