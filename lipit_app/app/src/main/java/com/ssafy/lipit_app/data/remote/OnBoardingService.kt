package com.ssafy.lipit_app.data.remote


import com.ssafy.lipit_app.data.model.request_dto.onboarding.OnBoardingRequest
import com.ssafy.lipit_app.data.model.response_dto.BaseResponse
import com.ssafy.lipit_app.data.model.response_dto.onboarding.OnBoardingResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface OnBoardingService {

    // 사용자 관심사항 저장
    @POST("onboarding/interesting")
    suspend fun saveInterest(
        @Body request: OnBoardingRequest
    ): Response<BaseResponse<OnBoardingResponse>>

}
