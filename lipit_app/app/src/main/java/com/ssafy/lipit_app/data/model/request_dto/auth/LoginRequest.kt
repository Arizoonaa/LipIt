package com.ssafy.lipit_app.data.model.request_dto.auth

data class LoginRequest(
    val email: String,
    val password: String,
    val fcmToken: String
)