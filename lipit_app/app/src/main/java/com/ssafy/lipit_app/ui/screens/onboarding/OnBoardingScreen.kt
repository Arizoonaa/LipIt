package com.ssafy.lipit_app.ui.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ssafy.lipit_app.ui.screens.onboarding.components.OnBoardingFifth
import com.ssafy.lipit_app.ui.screens.onboarding.components.OnBoardingFirst
import com.ssafy.lipit_app.ui.screens.onboarding.components.OnBoardingFourth
import com.ssafy.lipit_app.ui.screens.onboarding.components.OnBoardingSecond
import com.ssafy.lipit_app.ui.screens.onboarding.components.OnBoardingSixth
import com.ssafy.lipit_app.ui.screens.onboarding.components.OnBoardingThird


@Composable
fun OnBoardingScreen(onFinish: () -> Unit = {}) {

    var currentStep by remember { mutableIntStateOf(1) }
    val progressStep = 4
    val totalSteps = 6
    val purpleColor = Color(0xff603981)

    val systemUiController = rememberSystemUiController()
    DisposableEffect(systemUiController) {
        systemUiController.setNavigationBarColor(
            color = purpleColor,
            darkIcons = false
        )
        systemUiController.setStatusBarColor(
            color = purpleColor,
            darkIcons = false
        )
        onDispose {}
    }

    val onNext: () -> Unit = {
        when {
            currentStep < progressStep -> currentStep++
            currentStep < totalSteps -> currentStep++
            else -> onFinish()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(purpleColor)
    ) {
        when (currentStep) {
            1 -> OnBoardingFirst(currentStep, progressStep, onNext)
            2 -> OnBoardingSecond(currentStep, progressStep, onNext)
            3 -> OnBoardingThird(currentStep, progressStep, onNext)
            4 -> OnBoardingFourth(currentStep, progressStep, onNext)
            5 -> OnBoardingFifth(onNext)
            6 -> OnBoardingSixth(onNext)
        }
    }

}


@Preview(showBackground = true)
@Composable
fun OnBoardingPreview() {
    OnBoardingScreen()
}