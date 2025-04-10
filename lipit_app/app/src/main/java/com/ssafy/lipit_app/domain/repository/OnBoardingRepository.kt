package com.ssafy.lipit_app.domain.repository

import android.util.Log
import com.ssafy.lipit_app.data.model.request_dto.onboarding.OnBoardingRequest
import com.ssafy.lipit_app.data.model.response_dto.onboarding.OnBoardingResponse
import com.ssafy.lipit_app.data.remote.RetrofitUtil
import handleResponse

class OnBoardingRepository {

    // 사용자 관심사항 저장
    suspend fun saveInterest(request: OnBoardingRequest)
            : Result<OnBoardingResponse> {
        return try {
            val response = RetrofitUtil.onBoardingService.saveInterest(request)
            if (response.isSuccessful) {
                Result.success(response.body()?.data!!)
            } else {
                Result.failure(Exception("Failed to save interest"))
            }
            handleResponse(response)
        } catch (e: Exception) {
            Log.e("OnBoardingRepository", "예외 발생: ${e.message}", e)
            Result.failure(e)
        }
    }

}