package com.ssafy.lipit_app.data.model.response_dto.auth

data class SignupResponse(
    val userId: Long,
    val email: String,
    val username: String,
    val createdAt: String
)