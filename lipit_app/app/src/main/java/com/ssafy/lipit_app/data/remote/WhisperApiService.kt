package com.ssafy.lipit_app.data.remote

import com.ssafy.lipit_app.data.model.response_dto.custom_vocie.WhisperResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface WhisperApiService {

    // OpenAI Whisper API (음성 인식)
    @Multipart
    @POST("audio/transcriptions")
    suspend fun transcribeAudio(
        @Part file: MultipartBody.Part,
        @Part("model") model: RequestBody,
        @Part("language") language: RequestBody,
        @Part("response_format") responseFormat: RequestBody,
        @Header("Authorization") authorization: String
    ): Response<WhisperResponse>

}