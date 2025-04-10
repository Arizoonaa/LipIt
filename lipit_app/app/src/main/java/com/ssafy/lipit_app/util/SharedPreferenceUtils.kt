package com.ssafy.lipit_app.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log


/**
 * 멤버 ID를 관리하는 유틸리티 클래스
 */

object SharedPreferenceUtils {

    private const val PREF_NAME = "member_pref"
    private const val KEY_MEMBER_ID = "member_id"
    const val PREF_ALARM_REGISTERED_PREFIX = "alarm_registered_"
    const val PREF_ALARM_TIMESTAMP_PREFIX = "alarm_timestamp_"

    private var preferences: SharedPreferences? = null

    fun init(context: Context) {
        if (preferences == null) {
            preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        }
    }

    /**
     * 멤버 ID 저장
     */
    fun saveMemberId(memberId: Long) {
        if (preferences == null) {
            Log.e("MemberIdManager", "MemberIdManager가 초기화되지 않았습니다. init(context)를 먼저 호출하세요.")
            return
        }

        preferences?.edit()?.apply {
            putLong(KEY_MEMBER_ID, memberId)
            apply()
        }
        Log.d("MemberIdManager", "멤버 ID가 저장되었습니다: $memberId")
    }

    /**
     * 멤버 ID 가져오기
     * 저장된 ID가 없으면 기본값 1L 반환
     */
    fun getMemberId(): Long {
        if (preferences == null) {
            Log.e("MemberIdManager", "MemberIdManager가 초기화되지 않았습니다. init(context)를 먼저 호출하세요.")
            return 1L
        }

        val memberId = preferences?.getLong(KEY_MEMBER_ID, 1L) ?: 1L
        Log.d("MemberIdManager", "멤버 ID 조회: $memberId")
        return memberId
    }

    /**
     * 멤버 ID 삭제
     * 로그아웃 시 제거
     */
    fun clearMemberId() {
        if (preferences == null) {
            Log.e("MemberIdManager", "MemberIdManager가 초기화되지 않았습니다. init(context)를 먼저 호출하세요.")
            return
        }

        preferences?.edit()?.apply {
            remove(KEY_MEMBER_ID)
            apply()
        }
        Log.d("MemberIdManager", "멤버 ID가 삭제되었습니다.")
    }

    fun saveUserName(name: String) {
        preferences?.edit()?.putString("USER_NAME", name)?.apply()
    }

    fun getUserName(): String {
        return preferences?.getString("USER_NAME", "익명") ?: "익명"
    }

    fun saveSelectedVoiceName(voiceName: String) {
        preferences?.edit()?.putString("selected_voice_name", voiceName)?.apply()
    }

    fun getSelectedVoiceName(): String {
        return preferences?.getString("selected_voice_name", "Sarang") ?: "Sarang"
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return preferences?.getBoolean(key, defaultValue) ?: defaultValue
    }

    fun saveBoolean(key: String, value: Boolean) {

        preferences?.edit()?.apply {
            putBoolean(key, value)
            apply()
        }
    }

    fun saveString(key: String, value: String) {
        preferences?.edit()?.apply {
            putString(key, value)
            apply()
        }
    }

    fun getString(key: String, defaultValue: String): String {
        return preferences?.getString(key, defaultValue) ?: defaultValue
    }

    fun remove(key: String) {
        preferences?.edit()?.apply {
            remove(key)
            apply()
        }
    }
}