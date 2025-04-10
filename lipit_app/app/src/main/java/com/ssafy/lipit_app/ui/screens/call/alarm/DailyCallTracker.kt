package com.ssafy.lipit_app.ui.screens.call.alarm

import android.content.Context
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DailyCallTracker {

    private const val PREF_NAME = "daily_call_tracker"
    private const val KEY_LAST_CALL_DATE = "last_call_Date"

    /**
     * 오늘 통화를 완료한 것으로 표시
     */
    fun markTodayCallCompleted(context: Context) {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LAST_CALL_DATE, today).apply()
    }

    /**
     * 오늘 통화를 완료했는지 확인
     * @return 오늘 통화 완료 여부
     */
    fun isCallCompletedForToday(context: Context): Boolean {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val lastCallDate = prefs.getString(KEY_LAST_CALL_DATE, "") ?: ""
        return lastCallDate == today
    }

    /**
     * 통화 완료 상태 초기화 (테스트용 또는 관리자 기능)
     */
    fun resetCallCompletionStatus(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_LAST_CALL_DATE).apply()
    }

}