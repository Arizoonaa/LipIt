package com.ssafy.lipit_app.data.remote

import com.ssafy.lipit_app.data.model.request_dto.custom_voice.S3UploadRequest
import com.ssafy.lipit_app.data.model.response_dto.BaseResponse
import com.ssafy.lipit_app.data.model.response_dto.custom_vocie.S3UploadResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface S3PresignService {

    @POST("s3/presign")
    suspend fun getPresignedUrl(@Body request: S3UploadRequest): Response<BaseResponse<S3UploadResponse>>

}