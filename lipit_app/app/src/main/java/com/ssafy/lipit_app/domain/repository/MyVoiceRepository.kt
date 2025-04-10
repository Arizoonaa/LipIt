package com.ssafy.lipit_app.domain.repository

import android.util.Log
import com.ssafy.lipit_app.data.model.request_dto.myvoice.VoiceRequest
import com.ssafy.lipit_app.data.model.response_dto.myvoice.CelabResponse
import com.ssafy.lipit_app.data.model.response_dto.myvoice.CustomResponse
import com.ssafy.lipit_app.data.model.response_dto.myvoice.VoiceResponse
import com.ssafy.lipit_app.data.model.response_dto.myvoice.VoiceTakeResponse
import com.ssafy.lipit_app.data.remote.RetrofitUtil
import com.ssafy.lipit_app.util.SharedPreferenceUtils
import handleResponse

class MyVoiceRepository {

    // 셀럽 음성 목록 조회
    suspend fun getCelebVoices(memberId: Long)
            : Result<List<CelabResponse>> {
        return try {
            val response = RetrofitUtil.myVoiceService.getCelebVoices(memberId)
            if (response.isSuccessful) {
                Log.d("MyVoiceRepository", "셀럽 음성 목록 조회: ${response.body()}")
            }
            handleResponse(response)
        } catch (e: Exception) {
            Log.e("MyVoiceRepository", "예외 발생: ${e.message}", e)
            Result.failure(e)
        }
    }

    // 커스텀 음성 목록 조회
    suspend fun getCustomVoices(memberId: Long)
            : Result<List<CustomResponse>> {
        return try {
            val response = RetrofitUtil.myVoiceService.getCustomVoices(memberId)
            if (response.isSuccessful) {

                Log.d("MyVoiceRepository", "커스텀 음성 목록 조회: ${response.body()}")
            }
            handleResponse(response)
        } catch (e: Exception) {
            Log.e("MyVoiceRepository", "예외 발생: ${e.message}", e)
            Result.failure(e)
        }
    }

    // 선택한 음성 조회
    suspend fun getVoice(memberId: Long)
            : Result<List<CustomResponse>> {
        return try {
            val response = RetrofitUtil.myVoiceService.getVoice(memberId)
            if (response.isSuccessful) {
                val voiceName =
                    response.body()?.data?.firstOrNull()?.voiceName ?: "SSAFY"
                Result.success(voiceName)

                Log.d("MyVoiceRepository", "선택한 음성 조회: ${response.body()}")

            }
            handleResponse(response)
        } catch (e: Exception) {
            Log.e("MyVoiceRepository", "예외 발생: ${e.message}", e)
            Result.failure(e)
        }
    }

    // 선택한 음성 이름 조회
    suspend fun getVoiceName(memberId: Long)
            : Result<String> {
        Log.d("MyVoiceRepository", "🟩 getVoiceName 진입 - memberId: $memberId")

        return try {
            val response = RetrofitUtil.myVoiceService.getVoice(memberId)
            Log.d("MyVoiceRepository", "📦 응답 수신: isSuccessful=${response.isSuccessful}, code=${response.code()}")

            if (response.isSuccessful) {
                val voiceName = response.body()?.data?.firstOrNull()?.voiceName ?: "Sarang"
                SharedPreferenceUtils.saveSelectedVoiceName(voiceName)
                Log.d("MyVoiceRepository", "음성 이름: $voiceName")
                return Result.success(voiceName)
            }
            Result.failure(Exception("음성 조회 실패"))
        } catch (e: Exception) {
            Log.e("MyVoiceRepository", "예외 발생: ${e.message}", e)
            Result.failure(e)
        }
    }

    // 기본 음성 선택
    suspend fun changeVoice(memberId: Long, voiceId: Long)
            : Result<VoiceTakeResponse> {
        return try {
            val response = RetrofitUtil.myVoiceService.changeVoice(memberId, voiceId)
            if (response.isSuccessful) {
                Log.d("MyVoiceRepository", "기본 음성 선택: ${response.body()}")
            }
            handleResponse(response)
        } catch (e: Exception) {
            Log.e("MyVoiceRepository", "예외 발생: ${e.message}", e)
            Result.failure(e)
        }
    }

}
