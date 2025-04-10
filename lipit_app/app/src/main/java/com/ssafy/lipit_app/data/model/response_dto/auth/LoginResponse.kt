package com.ssafy.lipit_app.data.model.response_dto.auth

data class LoginResponse(
    val memberId: Long,
    val email: String,
    val name: String,
    val accessToken: String,
    val refreshToken: String,
    val fcmToken: String
)