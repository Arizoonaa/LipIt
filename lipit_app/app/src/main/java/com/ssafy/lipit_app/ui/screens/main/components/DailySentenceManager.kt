package com.ssafy.lipit_app.ui.screens.main.components

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

// FCM으로 받아온 메시지를 앱에서 내부적으로 저장
object DailySentenceManager {
    private const val PREFS_NAME = "daily_sentence"
    private const val ORIGINAL_KEY = "original_daily_sentance"
    private const val TRANSLATED_KEY = "translated_daily_sentance"


    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // 오늘의 문장 저장
    fun save(original: String, translated: String) {
        sharedPreferences.edit().putString(ORIGINAL_KEY, original)
            .putString(TRANSLATED_KEY, translated).apply()
        Log.d("DailySentenceManager", "문장 저장 완료")
    }

    // 원본 텍스트 가져오기
    fun getOriginal(): String {
        return sharedPreferences.getString(ORIGINAL_KEY, "")
            ?: ""
    }

    // 번역 텍스트 가져오기
    fun getTranslated(): String {
        return sharedPreferences.getString(TRANSLATED_KEY, "") ?: ""
    }

    // 지우기
    fun clear() {
        sharedPreferences.edit().clear().apply()
    }
}