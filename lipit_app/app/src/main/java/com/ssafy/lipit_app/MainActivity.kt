package com.ssafy.lipit_app

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.rememberNavController
import com.ssafy.lipit_app.base.ApplicationClass
import com.ssafy.lipit_app.navigation.NavGraph
import com.ssafy.lipit_app.theme.LipItTheme
import com.ssafy.lipit_app.ui.screens.call.alarm.AlarmScheduler
import com.ssafy.lipit_app.ui.screens.call.alarm.CallNotificationHelper

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {

    // 알람 스케줄러 인스턴스
    private lateinit var alarmScheduler: AlarmScheduler

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ApplicationClass.init(applicationContext)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }

        // 아이콘 색상 지정 (true = 검정 아이콘, false = 흰색 아이콘)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightStatusBars = true
        controller.isAppearanceLightNavigationBars = true

        // 알림 채널 생성
        CallNotificationHelper.createCallNotificationChannel(this)
        // 알람 스케줄러 초기화
        alarmScheduler = AlarmScheduler(this)

        val initialDestination = intent.getStringExtra("NAVIGATION_DESTINATION")
        Log.d(TAG, "Initial destination: $initialDestination")

        setContent {

            val navController = rememberNavController()

            LipItTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                        .navigationBarsPadding(),
                    color = Color.Transparent
                ) {
                    NavGraph(
                        navController = navController,
                        initialDestination = initialDestination
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)

        val newDestination = intent.getStringExtra("NAVIGATION_DESTINATION")
        if (newDestination != null) {
            Log.d(TAG, "New intent의 destination: $newDestination")

            val restartIntent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("NAVIGATION_DESTINATION", newDestination)
            }
            startActivity(restartIntent)
            finish()
        }
    }

}