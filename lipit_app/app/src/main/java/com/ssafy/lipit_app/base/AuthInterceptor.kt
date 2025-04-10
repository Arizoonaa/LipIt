package com.ssafy.lipit_app.base

import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

// API 요청에 자동으로 토큰 붙여주기
// 401 응답 -> Refresh token으로 새 access token을 받아옴

class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        // 로그인 요청 시, token을 헤더에 추가x
        val originalRequest = chain.request()

        // URL 로깅
        val url = originalRequest.url.toString()
        Log.d("AuthInterceptor", "요청 URL: $url")

        // 로그인/회원가입 API 요청인 경우 `Authorization` 헤더를 추가하지 않음
        if (
            url.contains("/auth/login") ||
            url.contains("/auth/signup") ||
            url.contains("/auth/refresh") ||
            url.contains("api.openai.com") ||
            url.contains("amazonaws.com")
        ) {
            Log.d("AuthInterceptor", "인증이 필요하지 않은 요청: $url")
            return chain.proceed(originalRequest)
        }


        // 로그인 요청이 아닌 경우, 토큰을 가져와서 헤더에 추가
        var token: String? = null
        try {
            token = runBlocking { SecureDataStore.getInstance(context).getAccessToken().first() }.toString()
            Log.d("AuthInterceptor", "토큰 가져오기: ${token}")
        } catch (e: Exception) {
            Log.e("AuthInterceptor", "토큰 가져오기 실패", e)
        }

        // 토큰이 null이거나 비어있을 경우 처리
        if (token.isNullOrEmpty()) {
            Log.d("AuthInterceptor", "토큰이 없습니다. 원래 요청으로 진행합니다.")
            return chain.proceed(originalRequest)
        }


        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")
            .build()

        Log.d("AuthInterceptor", "보내는 토큰: Bearer $token")

        val response = chain.proceed(request)

        // 401 Unauthorized → Access Token이 만료됨 → Refresh Token을 사용하여 새 토큰 요청
        if (response.code == 401) {
            runBlocking {
                val refreshToken = SecureDataStore.getInstance(context).getRefreshToken()
                refreshToken.collect { storedToken ->
                    if (!storedToken.isNullOrEmpty()) {
                        val newToken = refreshAccessToken(storedToken) // 서버에서 새 Access Token 받기
                        if (!newToken.isNullOrEmpty()) {
//                            SecureDataStore.saveUserInfo()
                            chain.proceed(
                                request.newBuilder()
                                    .header("Authorization", "Bearer $newToken")
                                    .build()
                            )
                        }
                    }
                }
            }
        }

        return response
    }

    private fun refreshAccessToken(refreshToken: String): String? {
        // 서버에 Refresh Token을 보내서 새 Access Token을 받아오는 로직 추가
        // return myAuthApi.refreshToken(refreshToken).accessToken
        return null
    }
}
