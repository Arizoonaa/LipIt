package com.ssafy.lipit_app.data.model.response_dto.onboarding

data class OnBoardingResponse(
    val memberId: Long,
    val email: String,
    val name: String,
    val gender: String,
    val interest: String,
    val selectedVoiceId: Int,
    val levelId: Long? = null,
    val fcmToken: String
)
