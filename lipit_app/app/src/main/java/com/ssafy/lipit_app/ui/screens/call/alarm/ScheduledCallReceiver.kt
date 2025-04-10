/**
 * ScheduledCallReceiver.kt
 * 예약된 시간에 통화 알림을 표시하는 BroadcastReceiver
 */
package com.ssafy.lipit_app.ui.screens.call.alarm

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.ssafy.lipit_app.util.SharedPreferenceUtils

/**
 * 예약된 시간에 호출되어 통화 알림을 표시하는 BroadcastReceiver
 */
class ScheduledCallReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "ScheduledCallReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "예약된 통화 알림 시간이 되었습니다")

        // 인텐트에서 정보 추출
        val callerName = intent.getStringExtra("CALLER_NAME") ?: "SARANG"
        val alarmId = intent.getIntExtra("ALARM_ID", 0)
        val retryCount = intent.getIntExtra(CallActionReceiver.EXTRA_RETRY_COUNT, 0)

        Log.d(TAG, "발신자: $callerName, 알람 ID: $alarmId, 재시도: $retryCount")

        // 알람 데이터 정리
        clearAlarmData(alarmId)

        // 통화 알림 표시
        try {
            // 거절 인텐트 생성 (재시도 정보 포함)
            val notificationIntent = Intent(context, CallActionReceiver::class.java).apply {
                action = CallActionReceiver.ACTION_DECLINE_CALL
                putExtra(CallActionReceiver.EXTRA_RETRY_COUNT, retryCount)
                putExtra("CALLER_NAME", callerName)
                putExtra("ALARM_ID", alarmId)
            }

            val declineIntent = PendingIntent.getBroadcast(
                context,
                1,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // 통화 알림 표시
            CallNotificationHelper.showCallNotification(
                context = context,
                callerName = callerName,
                declineIntent = declineIntent
            )

            // 타임아웃 설정 (응답 없을 경우 부재중 처리)
            setupMissedCallTimeout(context, callerName, alarmId, retryCount)

            Log.d(TAG, "통화 알림 표시 성공")
        } catch (e: Exception) {
            CallNotificationHelper.stopVibration(context)
            Log.e(TAG, "통화 알림 표시 실패: ${e.message}", e)
        }
    }

    /**
     * 부재중 전화 타임아웃 설정
     */
    private fun setupMissedCallTimeout(context: Context, callerName: String, alarmId: Int, retryCount: Int) {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            // 알림이 아직 활성 상태인지 확인
            if (CallNotificationHelper.isNotificationActive(
                    context,
                    CallNotificationHelper.CALL_NOTIFICATION_ID
                )
            ) {
                // 부재중 처리 인텐트 생성
                val missCallIntent = Intent(context, CallActionReceiver::class.java).apply {
                    action = CallActionReceiver.ACTION_MISSED_CALL
                    putExtra("CALLER_NAME", callerName)
                    putExtra("ALARM_ID", alarmId)
                    putExtra(CallActionReceiver.EXTRA_RETRY_COUNT, retryCount)
                }

                // 부재중 처리 브로드캐스트 전송
                context.sendBroadcast(missCallIntent)

                // 통화 알림 취소
                CallNotificationHelper.cancelCallNotification(context)

                // 부재중 알림 표시
                CallNotificationHelper.showMissedCallNotification(context, callerName, retryCount)
                Log.d(TAG, "통화 응답 없음 - 부재중 처리")
            }
        }, CallActionReceiver.MISSED_CALL_TIMEOUT_SECONDS * 1000L)
    }

    /**
     * 알람 데이터 정리
     */
    private fun clearAlarmData(alarmId: Int) {
        // SharedPreference에서 알람 데이터 제거
        val registeredKey = SharedPreferenceUtils.PREF_ALARM_REGISTERED_PREFIX + alarmId
        val timestampKey = SharedPreferenceUtils.PREF_ALARM_TIMESTAMP_PREFIX + alarmId

        SharedPreferenceUtils.remove(registeredKey)
        SharedPreferenceUtils.remove(timestampKey)

        Log.d(TAG, "알람 데이터 정리 완료: ID=$alarmId")
    }
}