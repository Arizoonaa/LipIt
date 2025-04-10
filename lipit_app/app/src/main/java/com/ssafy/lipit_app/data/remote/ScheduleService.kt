package com.ssafy.lipit_app.data.remote

import com.ssafy.lipit_app.data.model.request_dto.auth.LoginRequest
import com.ssafy.lipit_app.data.model.request_dto.auth.LogoutRequest
import com.ssafy.lipit_app.data.model.request_dto.auth.RefreshAccessTokenRequest
import com.ssafy.lipit_app.data.model.request_dto.auth.SignUpRequest
import com.ssafy.lipit_app.data.model.request_dto.schedule.ScheduleCreateRequest
import com.ssafy.lipit_app.data.model.response_dto.BaseResponse
import com.ssafy.lipit_app.data.model.response_dto.auth.LevelResponse
import com.ssafy.lipit_app.data.model.response_dto.auth.LoginResponse
import com.ssafy.lipit_app.data.model.response_dto.auth.LogoutResponse
import com.ssafy.lipit_app.data.model.response_dto.auth.RefreshAccessTokenResponse
import com.ssafy.lipit_app.data.model.response_dto.auth.SignupResponse
import com.ssafy.lipit_app.data.model.response_dto.schedule.RejectCountResponse
import com.ssafy.lipit_app.data.model.response_dto.schedule.ScheduleResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ScheduleService {

    // 일정 조회
    @GET("schedule")
    suspend fun getScheduleList(
        @Query("memberId") memberId: Long
    ): Response<BaseResponse<List<ScheduleResponse>>>

    // 부재중 개수 조회
    @GET("schedule/{callScheduleId}/reject")
    suspend fun getRejectCount(
        @Path("callScheduleId") callScheduleId: Long
    ): Response<BaseResponse<RejectCountResponse>>

    // 오늘의 일정 조회
    @GET("schedule/today")
    suspend fun getTodaySchedule(
        @Query("memberId") memberId: Long,
        @Query("callScheduleDay") callScheduleDay: String
    ): Response<BaseResponse<ScheduleResponse>>

    // 일정 생성
    @POST("schedule")
    suspend fun createSchedule(
        @Query("memberId") memberId: Long,
        @Body request: ScheduleCreateRequest
    ): Response<BaseResponse<ScheduleResponse>>

    // 일정 수정
    @PATCH("schedule/{callScheduleId}")
    suspend fun updateSchedule(
        @Path("callScheduleId") callScheduleId: Long,
        @Query("memberId") memberId: Long,
        @Body request: ScheduleCreateRequest
    ): Response<Void>

    // 전화 수신 거절 : Front에서 처리
//    @PATCH("/schedule/{callScheduleId}/reject")
//    suspend fun rejectSchedule(
//        @Path("callScheduleId") callScheduleId: Long,
//        @Body request: RejectRequest
//    ): Response<Void>

    // 일정 삭제 : Response 값이 Logout 이랑 동일해서 재사용
    @DELETE("schedule/{callScheduleId}")
    suspend fun deleteSchedule(
        @Path("callScheduleId") callScheduleId: Long,
        @Query("memberId") memberId: Long
    ): Response<BaseResponse<LogoutResponse>>
}