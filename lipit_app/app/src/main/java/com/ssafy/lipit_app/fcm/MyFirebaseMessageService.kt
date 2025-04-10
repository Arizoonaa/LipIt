package com.ssafy.lipit_app.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ssafy.lipit_app.R
import com.ssafy.lipit_app.ui.screens.main.components.DailySentenceManager

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "새 토큰: $token")
        // 서버에 토큰 전달 필요 시 여기에 로직 작성
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val intent = Intent("DAILY_SENTENCE_UPDATED")
        sendBroadcast(intent)

        Log.d("FCM", "알림 도착: ${remoteMessage.data}")

        // 데이터 기반 알림 처리
        val type = remoteMessage.data["type"]

        when (type) {
            "DAILY_SENTENCE" -> {
                val original_daily_sentance = remoteMessage.data["title"] ?: ""
                val fullBody = remoteMessage.data["body"] ?: ""
                val translated_daily_sentance = fullBody.removePrefix("오늘의 문장").trim()

                // 내부 SharedPreferences에 저장
                // 흐름: 새로운 문장 수신 -> 저장 -> 브로드 캐스트로 전송
                DailySentenceManager.save(original_daily_sentance, translated_daily_sentance)
                Log.d(
                    "FCM data",
                    "data 확인용 - type: $type, original: $original_daily_sentance, translated: $translated_daily_sentance"
                )

                DailySentenceManager.getOriginal()

                // 실시간 UI 반영을 위한 브로드캐스트
                val intent = Intent("DAILY_SENTENCE_UPDATED")
                sendBroadcast(intent)
            }

            // 추후 다른 알림 구현 시 추가하기!
//            CALL_REMINDER: 예약 전화 10분 전 알림
//            CALL_START: 전화 알림
//            MISSED_CALL: 부재중 알림
//            REPORT_COMPLETE: 리포트 발행 알림
//            DAILY_SENTENCE: 오늘의 문장 알림
        }

    }

    private fun showNotification(message: String) {
        // NotificationManager로 알림 띄우기
        val builder = NotificationCompat.Builder(this, "default")
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle("Lipit 알림")
            .setContentText(message)
            .setAutoCancel(true)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            "default", "기본 알림",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)

        notificationManager.notify(0, builder.build())
    }
}
