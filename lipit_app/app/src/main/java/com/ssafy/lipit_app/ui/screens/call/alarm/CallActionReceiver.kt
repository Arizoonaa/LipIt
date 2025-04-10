/**
 * CallActionReceiver.kt
 * 통화 수락/거절 등의 액션을 처리하는 BroadcastReceiver
 */
package com.ssafy.lipit_app.ui.screens.call.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.ssafy.lipit_app.MainActivity
import com.ssafy.lipit_app.util.SharedPreferenceUtils
import java.time.LocalDateTime

/**
 * 통화 액션(수락/거절/부재중)을 처리하는 BroadcastReceiver
 */
class CallActionReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "CallActionReceiver"

        // 액션 상수
        const val ACTION_ACCEPT_CALL = "com.ssafy.lipit_app.ACTION_ACCEPT_CALL"
        const val ACTION_DECLINE_CALL = "com.ssafy.lipit_app.ACTION_DECLINE_CALL"
        const val ACTION_MISSED_CALL = "com.ssafy.lipit_app.ACTION_MISSED_CALL"

        // 설정 상수
        const val MAX_RETRY_COUNT = 2             // 최대 재시도 횟수
        const val RETRY_INTERVAL_MINUTES = 5      // 재시도 간격(분)
        const val MISSED_CALL_TIMEOUT_SECONDS = 15 // 부재중 처리 타임아웃(초)

        // 인텐트 Extra 키
        const val EXTRA_RETRY_COUNT = "RETRY_COUNT"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val callerName = intent.getStringExtra("CALLER_NAME") ?: "SARANG"
        val alarmId = intent.getIntExtra("ALARM_ID", 0)
        val retryCount = intent.getIntExtra(EXTRA_RETRY_COUNT, 0)

        when (intent.action) {
            ACTION_ACCEPT_CALL -> handleAcceptCall(context, alarmId)
            ACTION_DECLINE_CALL -> handleDeclineCall(context, callerName, alarmId, retryCount)
            ACTION_MISSED_CALL -> handleMissedCall(context, callerName, alarmId, retryCount)
        }
    }

    /**
     * 통화 수락 처리
     */
    private fun handleAcceptCall(context: Context, alarmId: Int) {
        Log.d(TAG, "전화 수락됨 (알람 ID: $alarmId)")

        // 오늘 통화 완료로 표시
        DailyCallTracker.markTodayCallCompleted(context)


        // VoiceCall 화면으로 이동
        val acceptIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("NAVIGATION_DESTINATION", "onVoiceCall")
        }
        context.startActivity(acceptIntent)

        // 알림 취소 및 진동 중지
        CallNotificationHelper.stopVibration(context)
        CallNotificationHelper.cancelCallNotification(context)

        // 모든 관련 재시도 알람 취소
        cancelAllRetryAlarms(context, alarmId)
    }

    /**
     * 통화 거절 처리
     */
    private fun handleDeclineCall(context: Context, callerName: String, alarmId: Int, retryCount: Int) {
        Log.d(TAG, "전화 거절됨 (알람 ID: $alarmId, 재시도: $retryCount)")

        // 최대 재시도 횟수 미만이면 다음 알람 예약
        if (retryCount < MAX_RETRY_COUNT) {
            val nextAlarmId = alarmId + 1000 + (retryCount + 1)
            val nextAlarmTime = LocalDateTime.now().plusMinutes(RETRY_INTERVAL_MINUTES.toLong())

            // 새 알람 예약
            val alarmScheduler = AlarmScheduler(context)
            alarmScheduler.scheduleCallAlarm(
                time = nextAlarmTime,
                callerName = callerName,
                alarmId = nextAlarmId,
                retryCount = retryCount + 1
            )

            Log.d(TAG, "재시도 알람 설정: #${retryCount + 1}, 시간: $nextAlarmTime, ID: $nextAlarmId")
        } else {
            Log.d(TAG, "최대 재시도 횟수($MAX_RETRY_COUNT)에 도달")
        }

        // 알림 취소 및 진동 중지
        CallNotificationHelper.stopVibration(context)
        CallNotificationHelper.cancelCallNotification(context)
    }

    /**
     * 부재중 전화 처리
     */
    private fun handleMissedCall(context: Context, callerName: String, alarmId: Int, retryCount: Int) {
        Log.d(TAG, "전화 부재중 처리 (알람 ID: $alarmId, 재시도: $retryCount)")

        // 최대 재시도 횟수 미만이면 다음 알람 예약
        if (retryCount < MAX_RETRY_COUNT) {
            val nextAlarmId = alarmId + 1000 + (retryCount + 1)
            val nextAlarmTime = LocalDateTime.now().plusMinutes(RETRY_INTERVAL_MINUTES.toLong())

            // 새 알람 예약
            val alarmScheduler = AlarmScheduler(context)
            alarmScheduler.scheduleCallAlarm(
                time = nextAlarmTime,
                callerName = callerName,
                alarmId = nextAlarmId,
                retryCount = retryCount + 1
            )

            Log.d(TAG, "부재중 재시도 알람 설정: #${retryCount + 1}, 시간: $nextAlarmTime, ID: $nextAlarmId")
        } else {
            Log.d(TAG, "최대 재시도 횟수($MAX_RETRY_COUNT)에 도달")
        }

        // 진동 중지
        CallNotificationHelper.stopVibration(context)
    }

    /**
     * 모든 관련 재시도 알람 취소
     */
    private fun cancelAllRetryAlarms(context: Context, baseAlarmId: Int) {
        val alarmScheduler = AlarmScheduler(context)

        // 현재 알람 취소
        alarmScheduler.cancelAlarm(baseAlarmId)

        // 모든 재시도 알람 취소
        for (i in 1..MAX_RETRY_COUNT) {
            val retryAlarmId = baseAlarmId + 1000 + i
            alarmScheduler.cancelAlarm(retryAlarmId)

            // SharedPreference에서 알람 데이터 제거
            val registeredKey = SharedPreferenceUtils.PREF_ALARM_REGISTERED_PREFIX + retryAlarmId
            val timestampKey = SharedPreferenceUtils.PREF_ALARM_TIMESTAMP_PREFIX + retryAlarmId
            SharedPreferenceUtils.remove(registeredKey)
            SharedPreferenceUtils.remove(timestampKey)

            Log.d(TAG, "재시도 알람 취소: ID=$retryAlarmId")
        }

        Log.d(TAG, "모든 재시도 알람 취소 완료")
    }
}