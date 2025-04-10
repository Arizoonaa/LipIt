package com.ssafy.lipit_app.data.remote

import com.ssafy.lipit_app.data.model.request_dto.auth.LoginRequest
import com.ssafy.lipit_app.data.model.request_dto.auth.LogoutRequest
import com.ssafy.lipit_app.data.model.request_dto.auth.RefreshAccessTokenRequest
import com.ssafy.lipit_app.data.model.request_dto.auth.SignUpRequest
import com.ssafy.lipit_app.data.model.response_dto.BaseResponse
import com.ssafy.lipit_app.data.model.response_dto.auth.LevelResponse
import com.ssafy.lipit_app.data.model.response_dto.auth.LoginResponse
import com.ssafy.lipit_app.data.model.response_dto.auth.LogoutResponse
import com.ssafy.lipit_app.data.model.response_dto.auth.RefreshAccessTokenResponse
import com.ssafy.lipit_app.data.model.response_dto.auth.SignupResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthService {
    // 회원가입
    @POST("auth/signup")
    suspend fun signUp(@Body request: SignUpRequest): Response<BaseResponse<SignupResponse>>

    // 로그인
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<BaseResponse<LoginResponse>>

    // 로그아웃
    @POST("auth/logout")
    suspend fun logout(@Body request: LogoutRequest): Response<BaseResponse<LogoutResponse>>

    // 엑세스 토큰 재발급
    @POST("auth/token")
    suspend fun refreshAccessToken(@Body request: RefreshAccessTokenRequest): Response<BaseResponse<RefreshAccessTokenResponse>>

    // 회원 등급 조회 및 레벨 관련 정보 조회
    @POST("members/{memberId}/level")
    suspend fun getMemberLevel(@Path("memberId") memberId: Long): Response<BaseResponse<LevelResponse>>
}