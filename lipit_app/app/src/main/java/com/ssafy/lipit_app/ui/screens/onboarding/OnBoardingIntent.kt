package com.ssafy.lipit_app.ui.screens.onboarding

sealed class OnBoardingIntent {

    data class UpdateInterest(val interest: String) : OnBoardingIntent()
    object SaveInterest : OnBoardingIntent()
    object CompleteOnboarding : OnBoardingIntent()
    object NavigationComplete : OnBoardingIntent()
}