package com.ssafy.lipit_app.base

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ssafy.lipit_app.data.model.response_dto.auth.LoginResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

private val Context.dataStore by preferencesDataStore(name = "secure_prefs")

// 로그인 상태 유지 & 사용자 인증에 필요한 데이터
class SecureDataStore(private val context: Context) {
    companion object {

        @Volatile
        private var instance: SecureDataStore? = null

        // 싱글톤 인스턴스 가져오기
        fun getInstance(context: Context): SecureDataStore {
            return instance ?: synchronized(this) {
                instance ?: SecureDataStore(context.applicationContext).also { instance = it }
            }
        }

        // 키 정의
        val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token") // 로그인 후 발급된 JWT 엑세스 토큰
        val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token") // 액세스 토큰 만료 시 재발급용 토큰
        val MEMBER_ID_KEY = longPreferencesKey("member_id")
        val EMAIL_KEY = stringPreferencesKey("user_email")
        val USER_NAME_KEY = stringPreferencesKey("user_name")
        val FCM_TOKEN_KEY = stringPreferencesKey("fcm_token") //fcm 토큰 추가
        val ONBOARDING_COMPLETED_KEY = booleanPreferencesKey("onboarding_completed")
    }

    // JWT Access Token 가져오기
    fun getAccessToken(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[ACCESS_TOKEN_KEY]
        }
    }

    // JWT Access Token 저장
    suspend fun saveUserInfo(response: LoginResponse) {
        Log.d("SecureDataStore", "사용자 정보 저장: $response")
        context.dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN_KEY] = response.accessToken
            prefs[REFRESH_TOKEN_KEY] = response.refreshToken
            prefs[MEMBER_ID_KEY] = response.memberId
            prefs[USER_NAME_KEY] = response.name
            prefs[EMAIL_KEY] = response.email
            prefs[FCM_TOKEN_KEY] = response.fcmToken ?: ""
        }
    }

    // refresh token 가져오기
    fun getRefreshToken(): Flow<String?> {
        return context.dataStore.data.map { prefs ->
            prefs[REFRESH_TOKEN_KEY]
        }
    }

    // 로그아웃 시 DataStore 초기화
    suspend fun clearUserInfo() {
        Log.d("SecureDataStore", "사용자 정보 삭제")

        val isOnboardingCompleted =
            context.dataStore.data.first()[ONBOARDING_COMPLETED_KEY] ?: false

        context.dataStore.edit { prefs ->
            prefs.remove(ACCESS_TOKEN_KEY)
            prefs.remove(REFRESH_TOKEN_KEY)
            prefs.remove(MEMBER_ID_KEY)
            prefs.remove(EMAIL_KEY)
            prefs.remove(USER_NAME_KEY)
            prefs.remove(FCM_TOKEN_KEY)

            // 온보딩 상태는 그대로 유지
            prefs[ONBOARDING_COMPLETED_KEY] = isOnboardingCompleted
        }

        Log.d("SecureDataStore", "로그아웃 후 온보딩 상태 유지: $isOnboardingCompleted")
    }

    // 토큰이 있는지 동기적으로 확인하는 함수 (NavGraph 등에서 사용)
    fun hasAccessTokenSync(): Boolean {
        return runBlocking {
            try {
                val token = getAccessToken().firstOrNull()
                val hasToken = !token.isNullOrEmpty()
                Log.d("SecureDataStore", "토큰 존재 여부(동기): $hasToken")
                hasToken
            } catch (e: Exception) {
                Log.e("SecureDataStore", "토큰 확인 중 오류", e)
                false
            }
        }
    }

    // 온보딩 완료 상태를 저장하는 메서드
    suspend fun setOnboardingCompleted(completed: Boolean) {
        // 현재 로그인한 사용자의 아이디 가져오기
        val memberId = context.dataStore.data.first()[MEMBER_ID_KEY]

        if (memberId != null) {
            // 사용자별 온보딩 키 생성
            val userOnboardingKey = booleanPreferencesKey("onboarding_completed_$memberId")

            context.dataStore.edit { preferences ->
                preferences[userOnboardingKey] = completed
            }

            Log.d("SecureDataStore", "사용자($memberId)의 온보딩 상태 설정: $completed")
        } else {
            // 로그인되지 않은 경우 처리
            Log.e("SecureDataStore", "사용자 ID가 없어 온보딩 상태를 저장할 수 없습니다")
        }
    }


    // 온보딩 완료 상태를 확인하는 메서드
    fun isOnboardingCompletedSync(): Boolean {
        return runBlocking {
            try {
                val memberId = context.dataStore.data.first()[MEMBER_ID_KEY]

                if (memberId != null) {
                    // 사용자별 온보딩 키로 상태 확인
                    val userOnboardingKey = booleanPreferencesKey("onboarding_completed_$memberId")
                    val completed = context.dataStore.data.first()[userOnboardingKey] ?: false

                    Log.d("SecureDataStore", "사용자($memberId)의 온보딩 상태: $completed")
                    completed
                } else {
                    // 로그인되지 않은 경우 온보딩이 필요하다고 판단
                    Log.d("SecureDataStore", "로그인되지 않아 온보딩 필요")
                    false
                }
            } catch (e: Exception) {
                Log.e("SecureDataStore", "온보딩 상태 확인 오류", e)
                false
            }
        }
    }
}