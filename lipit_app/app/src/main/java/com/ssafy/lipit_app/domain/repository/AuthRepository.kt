package com.ssafy.lipit_app.domain.repository

import android.util.Log
import com.ssafy.lipit_app.data.model.request_dto.auth.LoginRequest
import com.ssafy.lipit_app.data.model.request_dto.auth.LogoutRequest
import com.ssafy.lipit_app.data.model.request_dto.auth.RefreshAccessTokenRequest
import com.ssafy.lipit_app.data.model.request_dto.auth.SignUpRequest
import com.ssafy.lipit_app.data.model.response_dto.auth.LevelResponse
import com.ssafy.lipit_app.data.model.response_dto.auth.LoginResponse
import com.ssafy.lipit_app.data.model.response_dto.auth.LogoutResponse
import com.ssafy.lipit_app.data.model.response_dto.auth.RefreshAccessTokenResponse
import com.ssafy.lipit_app.data.model.response_dto.auth.SignupResponse
import com.ssafy.lipit_app.data.remote.RetrofitUtil
import handleResponse

class AuthRepository {
    // 로그인
    suspend fun login(loginRequest: LoginRequest): Result<LoginResponse> {
        return try {
            val response = RetrofitUtil.authService.login(loginRequest)
            Log.d("AuthRepository", "요청 성공 여부: ${response.isSuccessful}")
            Log.d("AuthRepository", "HTTP 코드: ${response.code()}")
            Log.d("AuthRepository", "응답 메시지: ${response.message()}")

            if (response.isSuccessful) {
                Log.d("AuthRepository", "로그인 성공: ${response.body()}")
            } else {
                Log.e("AuthRepository", "로그인 실패")
                Log.e("AuthRepository", "에러 바디: ${response.errorBody()?.string()}")
            }
            handleResponse(response)
        } catch (e: Exception) {
            Log.e("AuthRepository", "예외 발생: ${e.message}", e)
            Result.failure(e)
        }
    }

    // 회원가입
    suspend fun signup(signUpRequest: SignUpRequest): Result<SignupResponse> {
        return try {
            val response = RetrofitUtil.authService.signUp(signUpRequest)
            handleResponse(response)
        } catch (e: Exception) {
            Log.e("AuthRepository", "예외 발생: ${e.message}", e)
            Result.failure(e)
        }
    }

    // 로그아웃
    suspend fun logout(logoutRequest: LogoutRequest): Result<LogoutResponse> {
        return try {
            val response = RetrofitUtil.authService.logout(logoutRequest)
            handleResponse(response)
        } catch (e: Exception) {
            Log.e("AuthRepository", "예외 발생: ${e.message}", e)
            Result.failure(e)
        }
    }

    // Access Token 재발급
    suspend fun refreshAccessToken(tokenRequest: RefreshAccessTokenRequest): Result<RefreshAccessTokenResponse> {
        return try {
            val response = RetrofitUtil.authService.refreshAccessToken(tokenRequest)
            handleResponse(response)
        } catch (e: Exception) {
            Log.e("AuthRepository", "예외 발생: ${e.message}", e)
            Result.failure(e)
        }
    }

    // 회원 등급 조회
    suspend fun getMemberLevel(memberId: Long): Result<LevelResponse> {
        return try {
            val response = RetrofitUtil.authService.getMemberLevel(memberId)
            Log.d("UserLevel", "서버로 전송할 memberId: $memberId")

            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                val code = response.code()
                val errorBody = response.errorBody()?.string()
                Log.e("UserLevel", "API 실패 - code: $code, errorBody: $errorBody")
                throw Exception("서버 응답 실패 - code: $code")
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 다음 레벨까지 달성률 (Next Level) 조회
//    suspend fun getNextLevelProgress(memberId: Long): Result<LevelResponse> {
//        return try {
//            val response = RetrofitUtil.authService.getMemberLevel(memberId)
//            if(response.isSuccessful){
//                val body =
//            }
//        }
//    }

}