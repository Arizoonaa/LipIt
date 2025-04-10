/**
 * AlarmScheduler.kt
 * 예약된 시간에 통화 알림을 스케줄링하는 클래스
 */
package com.ssafy.lipit_app.ui.screens.call.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.ssafy.lipit_app.util.SharedPreferenceUtils
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * 예약된 시간에 통화 알림을 표시하기 위한 스케줄러
 */
class AlarmScheduler(private val context: Context) {
    companion object {
        private const val TAG = "AlarmScheduler"
    }

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    /**
     * 지정된 시간에 통화 알림을 예약
     *
     * @param time 알림이 표시될 시간
     * @param callerName 발신자 이름
     * @param alarmId 고유 알람 ID
     * @param retryCount 재시도 횟수
     * @return 예약 성공 여부
     */
    fun scheduleCallAlarm(
        time: LocalDateTime,
        callerName: String,
        alarmId: Int = 0,
        retryCount: Int = 0
    ): Boolean {
        try {

            // 이미 오늘 통화를 완료했는지 확인
            if (DailyCallTracker.isCallCompletedForToday(context)) {
                Log.d(TAG, "오늘은 이미 통화를 완료했습니다. 알람 예약을 건너뜁니다.")
                return false
            }


            // 알람이 울릴 때 실행될 인텐트 준비
            val intent = Intent(context, ScheduledCallReceiver::class.java).apply {
                putExtra("CALLER_NAME", callerName)
                putExtra("ALARM_ID", alarmId)
                putExtra(CallActionReceiver.EXTRA_RETRY_COUNT, retryCount)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                alarmId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // LocalDateTime을 밀리초로 변환
            val triggerTimeMillis = time
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()

            // 현재 시간보다 이전인지 확인
            val now = System.currentTimeMillis()
            if (triggerTimeMillis <= now) {
                Log.e(TAG, "알람 시간이 현재보다 이전입니다: $time")
                return false
            }

            // Android 버전에 따라 알람 설정
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Android 6.0(M) 이상: Doze 모드에서도 작동
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTimeMillis,
                    pendingIntent
                )
            } else {
                // Android 6.0 미만
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerTimeMillis,
                    pendingIntent
                )
            }

            Log.d(TAG, "통화 알림 예약: $callerName, 시간: $time, ID: $alarmId, 재시도: $retryCount")
            return true
        } catch (e: Exception) {
            Log.e(TAG, "알람 예약 실패: ${e.message}", e)
            return false
        }
    }

    /**
     * 지정된 ID의 알람을 취소
     */
    fun cancelAlarm(alarmId: Int) {
        try {
            val intent = Intent(context, ScheduledCallReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                alarmId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()

            Log.d(TAG, "알람 취소: ID $alarmId")
        } catch (e: Exception) {
            Log.e(TAG, "알람 취소 실패: ${e.message}", e)
        }
    }

    /**
     * 오늘의 모든 알람 취소
     */
    fun cancelAllTodayAlarms(baseAlarmId: Int, maxRetryCount: Int) {
        // 현재 알람 취소
        cancelAlarm(baseAlarmId)

        // 재시도 알람 모두 취소
        for (i in 1..maxRetryCount) {
            val retryAlarmId = baseAlarmId + 1000 + i
            cancelAlarm(retryAlarmId)

            // SharedPreference에서 알람 데이터 제거
            val registeredKey = SharedPreferenceUtils.PREF_ALARM_REGISTERED_PREFIX + retryAlarmId
            val timestampKey = SharedPreferenceUtils.PREF_ALARM_TIMESTAMP_PREFIX + retryAlarmId
            SharedPreferenceUtils.remove(registeredKey)
            SharedPreferenceUtils.remove(timestampKey)
        }

        Log.d(TAG, "오늘의 모든 알람 취소 완료")
    }
}