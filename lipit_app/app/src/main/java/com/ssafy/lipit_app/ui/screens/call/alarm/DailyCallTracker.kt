package com.ssafy.lipit_app.ui.screens.call.alarm

import android.content.Context
import android.util.Log
import com.ssafy.lipit_app.util.SharedPreferenceUtils
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DailyCallTracker {

    private const val PREF_NAME = "daily_call_tracker"
    private const val KEY_LAST_CALL_DATE_PREFIX = "last_call_date_member_"
    private const val KEY_COMPLETED_ALARM_PREFIX = "completed_alarm_"


    /**
     * 현재 로그인한 사용자의 memberId 가져오기
     */
    private fun getCurrentMemberId(): Long {
        return SharedPreferenceUtils.getMemberId()
    }

    /**
     * memberId에 따른 키 생성
     */
    private fun getKeyForMember(memberId: Long): String {
        return KEY_LAST_CALL_DATE_PREFIX + memberId
    }

    /**
     * 기본 알람 ID 추출 (재시도 번호 제거)
     */
    private fun getBaseAlarmId(alarmId: Int): Int {
        // 알람 ID가 재시도로 인해 변경된 경우 (1000 단위로 증가)
        // 원래 알람 ID로 되돌린다
        val MAX_RETRY_COUNT = 2 // CallActionReceiver와 일치

        return if (alarmId > 1000) {
            val retryCountInId = (alarmId % 1000)
            if (retryCountInId in 1..MAX_RETRY_COUNT) {
                alarmId - 1000 - retryCountInId
            } else {
                alarmId
            }
        } else {
            alarmId
        }
    }

    /**
     * 알람 ID에 따른 키 생성
     */
    private fun getKeyForAlarm(alarmId: Int): String {
        // 알람 ID의 기본 ID만 사용 (재시도 정보 제외)
        val baseAlarmId = getBaseAlarmId(alarmId)
        return KEY_COMPLETED_ALARM_PREFIX + baseAlarmId
    }

    /**
     * 오늘 통화를 완료한 것으로 표시 (현재 로그인한 사용자)
     */
    fun markTodayCallCompleted(context: Context) {
        val memberId = getCurrentMemberId()
        markTodayCallCompletedForMember(context, memberId)
    }

    /**
     * 특정 알람에 대해서만 통화 완료로 표시
     */
    fun markAlarmCompleted(context: Context, alarmId: Int) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val alarmKey = getKeyForAlarm(alarmId)

        prefs.edit().putString(alarmKey, today).apply()

        Log.d("TAG", "알람 ID: ${alarmId}에 대한 통화 완료 표시: $today")
    }

    /**
     * 특정 사용자(memberId)의 오늘 통화를 완료한 것으로 표시
     */
    private fun markTodayCallCompletedForMember(context: Context, memberId: Long) {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(getKeyForMember(memberId), today).apply()

        Log.d("TAG", "사용자(memberId: $memberId)의 오늘 통화 완료 표시: $today")
    }

    /**
     * 오늘 통화를 완료했는지 확인 (현재 로그인한 사용자)
     * @return 오늘 통화 완료 여부
     */
    fun isCallCompletedForToday(context: Context): Boolean {
        val memberId = getCurrentMemberId()
        return isCallCompletedForTodayByMember(context, memberId)
    }

    /**
     * 특정 사용자(memberId)의 오늘 통화를 완료했는지 확인
     * @return 오늘 통화 완료 여부
     */
    fun isCallCompletedForTodayByMember(context: Context, memberId: Long): Boolean {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val lastCallDate = prefs.getString(getKeyForMember(memberId), "") ?: ""

        val isCompleted = lastCallDate == today
        Log.d(
            "TAG",
            "사용자(memberId: $memberId)의 오늘 통화 완료 여부: $isCompleted (마지막 통화일: $lastCallDate, 오늘: $today)"
        )

        return isCompleted
    }

    /**
     * 특정 알람이 오늘 완료되었는지 확인
     * @return 알람 완료 여부
     */
    fun isAlarmCompletedForToday(context: Context, alarmId: Int): Boolean {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val alarmKey = getKeyForAlarm(alarmId)
        val lastCompletedDate = prefs.getString(alarmKey, "") ?: ""

        val isCompleted = lastCompletedDate == today
        Log.d(
            "TAG",
            "알람 ID: $alarmId 완료 여부: $isCompleted (완료일: $lastCompletedDate, 오늘: $today)"
        )

        return isCompleted
    }

    /**
     * 통화 완료 상태 초기화 (현재 로그인한 사용자)
     */
    fun resetCallCompletionStatus(context: Context) {
        val memberId = getCurrentMemberId()
        resetCallCompletionStatusForMember(context, memberId)
    }

    /**
     * 특정 사용자(memberId)의 통화 완료 상태 초기화
     */
    private fun resetCallCompletionStatusForMember(context: Context, memberId: Long) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(getKeyForMember(memberId)).apply()

        Log.d("TAG", "사용자(memberId: $memberId)의 통화 완료 상태가 초기화되었습니다.")
    }

    /**
     * 특정 알람의 완료 상태 초기화
     */
    fun resetAlarmCompletionStatus(context: Context, alarmId: Int) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val alarmKey = getKeyForAlarm(alarmId)
        prefs.edit().remove(alarmKey).apply()

        Log.d("TAG", "알람 ID: ${alarmId}의 완료 상태가 초기화되었습니다.")
    }

    /**
     * memberId로 검색하여 마지막 통화 일자 조회 (디버깅용)
     */
    fun getLastCallDateForMember(context: Context, memberId: Long): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(getKeyForMember(memberId), "") ?: ""
    }

    /**
     * 모든 사용자 통화 데이터 목록 조회 (디버깅용)
     */
    fun getAllCallRecords(context: Context): Map<String, String> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val result = mutableMapOf<String, String>()

        prefs.all.forEach { (key, value) ->
            if (key.startsWith(KEY_LAST_CALL_DATE_PREFIX) && value is String) {
                val memberIdStr = key.removePrefix(KEY_LAST_CALL_DATE_PREFIX)
                result[memberIdStr] = value
            }
        }


        return result

    }
}