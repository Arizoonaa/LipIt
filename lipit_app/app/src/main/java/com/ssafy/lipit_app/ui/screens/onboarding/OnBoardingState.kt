package com.ssafy.lipit_app.ui.screens.onboarding

import com.ssafy.lipit_app.data.model.request_dto.onboarding.OnBoardingRequest

data class OnBoardingState(
    val request: OnBoardingRequest? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val navigateToNext: Boolean = false,
    val navigateToMain: Boolean = false
)
