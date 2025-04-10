package com.ssafy.lipit_app.data.model.request_dto.auth

data class SignUpRequest(
    val email: String,
    val password1: String,
    val password2: String,
    val name: String,
    val gender: String? = null
)