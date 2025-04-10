package com.ssafy.lipit_app.ui.screens.onboarding

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.lipit_app.base.SecureDataStore
import com.ssafy.lipit_app.data.model.request_dto.onboarding.OnBoardingRequest
import com.ssafy.lipit_app.domain.repository.OnBoardingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OnboardingViewModel(
    context: Context
) : ViewModel() {

    private val secureDataStore = SecureDataStore.getInstance(context)
    private val onBoardingRepository: OnBoardingRepository = OnBoardingRepository()

    private val _state = MutableStateFlow(OnBoardingState())
    val state: StateFlow<OnBoardingState> = _state.asStateFlow()

    // 네비게이션 상태를 관리할 수 있는 StateFlow 추가
    private val _navigateToMain = MutableStateFlow(false)
    val navigateToMain = _navigateToMain.asStateFlow()

    fun onIntent(intent: OnBoardingIntent) {
        when (intent) {
            is OnBoardingIntent.SaveInterest -> {
                saveInterest()
            }

            is OnBoardingIntent.CompleteOnboarding -> {
                completeOnboarding()
            }

            is OnBoardingIntent.NavigationComplete -> {
                _state.update {
                    it.copy(
                        navigateToNext = false,
                        navigateToMain = false,
                        error = null
                    )
                }
                _navigateToMain.value = false
            }

            is OnBoardingIntent.UpdateInterest -> {
                val request = OnBoardingRequest(interest = intent.interest)
                _state.update { it.copy(request = request) }
                Log.d("OnboardingViewModel", "관심사 업데이트: ${intent.interest}")
            }

        }
    }

    // Fix for saveInterest method to handle null request
    private fun saveInterest() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val request = _state.value.request
            if (request == null) {
                // Handle the case where request is null
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "관심사가 설정되지 않았습니다."
                    )
                }
                Log.d("OnboardingViewModel", "관심사 저장 실패: request is null")
                return@launch
            }

            val result = onBoardingRepository.saveInterest(request)
            Log.d("TAG", "저장된 흥미 사항: ${request.interest}")

            result.fold(
                onSuccess = { response ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            navigateToNext = true
                        )
                    }
                    Log.d("OnboardingViewModel", "관심사 저장 성공, navigateToNext = true")
                    Log.d("OnboardingViewModel", "saveInterest: ${response.interest}")
                },
                onFailure = { exception ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message
                        )
                    }
                    Log.d("OnboardingViewModel", "관심사 저장 실패: ${exception.message}")
                }
            )
        }
    }

    private fun completeOnboarding() {
        viewModelScope.launch {
            secureDataStore.setOnboardingCompleted(true)
            _state.update { it.copy(navigateToMain = true) }
        }
    }
}