/**
 * CallNotificationHelper.kt
 * 통화 알림을 관리하는 유틸리티 클래스
 */
package com.ssafy.lipit_app.ui.screens.call.alarm

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.graphics.drawable.IconCompat
import com.ssafy.lipit_app.MainActivity
import com.ssafy.lipit_app.R

/**
 * 통화 알림 관련 기능을 제공하는 싱글톤 객체
 */
object CallNotificationHelper {
    // 상수 정의
    private const val TAG = "CallNotification"
    private const val CALL_CHANNEL_ID = "call_channel"
    const val CALL_NOTIFICATION_ID = 1001
    private const val MISSED_CALL_NOTIFICATION_ID = 1002

    // 진동 패턴: 0ms 대기, 500ms 진동, 500ms 대기, 500ms 진동 (반복)
    private val VIBRATION_PATTERN = longArrayOf(0, 500, 500, 500)

    // 상태 관리 변수
    private var vibrator: Vibrator? = null
    private var applicationContext: Context? = null
    private var isVibrating = false

    /**
     * 통화 알림 채널 생성 - 앱 시작 시 한 번만 호출
     */
    fun createCallNotificationChannel(context: Context) {
        if (applicationContext == null) {
            applicationContext = context.applicationContext
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CALL_CHANNEL_ID,
                "전화 알림",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "수신 전화 알림을 표시합니다"
                setShowBadge(true)
                enableVibration(true)
                vibrationPattern = VIBRATION_PATTERN
            }

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            Log.d(TAG, "통화 알림 채널 생성됨")
        }
    }

    /**
     * 진동 시작
     */
    @SuppressLint("ServiceCast")
    fun startVibration(context: Context) {
        if (isVibrating) {
            Log.d(TAG, "이미 진동 중입니다")
            return
        }

        try {
            vibrator = vibrator ?: getVibrator(context)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val vibrationEffect = VibrationEffect.createWaveform(VIBRATION_PATTERN, 0)
                vibrator?.vibrate(vibrationEffect)
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(VIBRATION_PATTERN, 0)
            }

            isVibrating = true
            Log.d(TAG, "진동 시작")
        } catch (e: Exception) {
            Log.e(TAG, "진동 시작 중 오류: ${e.message}")
        }
    }

    /**
     * 진동 중지
     */
    fun stopVibration(context: Context) {
        if (!isVibrating) {
            return
        }

        try {
            val vibratorToUse = vibrator ?: getVibrator(context)
            vibratorToUse?.cancel()

            isVibrating = false
            Log.d(TAG, "진동 중지")
        } catch (e: Exception) {
            Log.e(TAG, "진동 중지 중 오류: ${e.message}")
        }
    }

    /**
     * Vibrator 서비스 가져오기
     */
    private fun getVibrator(context: Context): Vibrator? {
        try {
            val ctx = applicationContext ?: context.applicationContext
            applicationContext = ctx

            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = ctx.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                ctx.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
        } catch (e: Exception) {
            Log.e(TAG, "Vibrator 서비스 가져오기 실패: ${e.message}")
            return null
        }
    }

    /**
     * 수신 통화 알림 표시
     */
    fun showCallNotification(
        context: Context,
        callerName: String,
        callerPhotoUri: Uri? = null,
        declineIntent: PendingIntent? = null
    ) {
        // 수락 인텐트
        val acceptIntent = PendingIntent.getBroadcast(
            context,
            0,
            Intent(context, CallActionReceiver::class.java).apply {
                action = CallActionReceiver.ACTION_ACCEPT_CALL
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 거절 인텐트 (외부에서 제공되지 않은 경우 기본값 사용)
        val finalDeclineIntent = declineIntent ?: PendingIntent.getBroadcast(
            context,
            1,
            Intent(context, CallActionReceiver::class.java).apply {
                action = CallActionReceiver.ACTION_DECLINE_CALL
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 알림 탭 시 실행될 인텐트
        val contentIntent = PendingIntent.getActivity(
            context,
            2,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("NAVIGATION_DESTINATION", "inComingCall")
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 발신자 Person 객체 생성
        val caller = Person.Builder()
            .setName(callerName)
            .apply {
                if (callerPhotoUri != null) {
                    setIcon(IconCompat.createWithContentUri(callerPhotoUri))
                } else {
                    setIcon(IconCompat.createWithResource(context, R.drawable.img_add_image))
                }
            }
            .build()

        // 알림 빌더 생성 (Android 버전에 따라 다른 스타일 적용)
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android 12 이상: CallStyle 지원
            NotificationCompat.Builder(context, CALL_CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Lip It")
                .setContentText("${callerName}님으로부터 전화가 왔습니다")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setFullScreenIntent(contentIntent, true)
                .setStyle(
                    NotificationCompat.CallStyle.forIncomingCall(
                        caller,
                        finalDeclineIntent,
                        acceptIntent
                    )
                )
                .setAutoCancel(true)
        } else {
            // Android 11 이하: 일반 알림 + 액션 버튼
            NotificationCompat.Builder(context, CALL_CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Lip It")
                .setContentText("${callerName}님으로부터 전화가 왔습니다")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setFullScreenIntent(contentIntent, true)
                .addAction(R.drawable.incoming_call_decline, "거절", finalDeclineIntent)
                .addAction(R.drawable.incoming_call_accept, "수락", acceptIntent)
                .setAutoCancel(true)
        }

        // 진동 시작
        startVibration(context)

        // 알림 표시 (권한 확인)
        val notificationManager = NotificationManagerCompat.from(context)
        if (hasNotificationPermission(context)) {
            notificationManager.notify(CALL_NOTIFICATION_ID, builder.build())
            Log.d(TAG, "통화 알림 표시됨: $callerName")
        } else {
            Log.w(TAG, "알림 권한 없음")
        }
    }

    /**
     * 알림 권한 확인
     */
    private fun hasNotificationPermission(context: Context): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
    }

    /**
     * 통화 알림 취소
     */
    fun cancelCallNotification(context: Context) {
        val ctx = applicationContext ?: context.applicationContext
        stopVibration(ctx)

        val notificationManager = NotificationManagerCompat.from(ctx)
        notificationManager.cancel(CALL_NOTIFICATION_ID)
        Log.d(TAG, "통화 알림 취소됨")
    }

    /**
     * 알림이 활성 상태인지 확인
     */
    fun isNotificationActive(context: Context, notificationId: Int): Boolean {
        val notificationManager = NotificationManagerCompat.from(context)
        val activeNotifications = notificationManager.activeNotifications
        return activeNotifications.any { it.id == notificationId }
    }

    /**
     * 부재중 전화 알림 표시
     */
    fun showMissedCallNotification(context: Context, callerName: String, retryCount: Int) {
        // 알림 탭 시 실행될 인텐트
        val contentIntent = PendingIntent.getActivity(
            context,
            2,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("NAVIGATION_DESTINATION", "missedCalls")
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 부재중 전화 횟수에 따른 텍스트
        val missedCallText = if (retryCount == 0)
            "${callerName}님으로부터 부재중 전화"
        else
            "${callerName}님으로부터 부재중 전화(${retryCount + 1})"

        // 알림 생성
        val builder = NotificationCompat.Builder(context, CALL_CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle("부재중 전화")
            .setContentText(missedCallText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setContentIntent(contentIntent)
            .setAutoCancel(true)

        // 알림 표시 (권한 확인)
        val notificationManager = NotificationManagerCompat.from(context)
        if (hasNotificationPermission(context)) {
            notificationManager.notify(MISSED_CALL_NOTIFICATION_ID, builder.build())
            Log.d(TAG, "부재중 전화 알림 표시됨: $callerName (재시도: $retryCount)")
        }
    }
}