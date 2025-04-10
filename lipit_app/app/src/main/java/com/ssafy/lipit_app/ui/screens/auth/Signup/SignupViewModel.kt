package com.ssafy.lipit_app.ui.screens.auth.Signup

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.ssafy.lipit_app.data.model.request_dto.auth.SignUpRequest
import com.ssafy.lipit_app.domain.repository.AuthRepository
import com.ssafy.lipit_app.ui.screens.auth.Signup.components.validateSignupInput
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignupViewModel : ViewModel() {
    private val _state = MutableStateFlow(SignupState())
    val state: StateFlow<SignupState> = _state

    private val authRepository by lazy { AuthRepository() }

    fun onIntent(intent: SignupIntent) {
        when (intent) {
            // id 변경되었을 때
            is SignupIntent.OnIdChanged -> {
                // 불변성 유지 + compose recompositon 유도를 위해 copy 사용
                _state.value = _state.value.copy(id = intent.id)
            }

            // pw 변경되었을 때
            is SignupIntent.OnPwChanged -> {
                _state.value = _state.value.copy(pw = intent.pw)
            }

            // 재입력 pw 변경되었을 때
            is SignupIntent.OnPwConfirmChanged -> {
                _state.value = _state.value.copy(pwConfirm = intent.pwConfirm)
            }

            // 영어 이름 변경되었을 때
            is SignupIntent.OnEnglishNameChanged -> {
                _state.value = _state.value.copy(englishName = intent.englishName)
            }

            // gender 변경되었을 때
            is SignupIntent.OnSelectedGenderChanged -> {
                _state.value = _state.value.copy(selectedGender = intent.selectedGender)
            }

            // ======================================

            // 비밀번호 visible 변경 check
            is SignupIntent.OnisPasswordVisible1Changed -> {
                val current = _state.value.isPasswordVisible_1
                _state.value = _state.value.copy(isPasswordVisible_1 = !current)

            }

            is SignupIntent.OnisPasswordVisible2Changed -> {
                val current = _state.value.isPasswordVisible_2
                _state.value = _state.value.copy(isPasswordVisible_2 = !current)

            }

            is SignupIntent.OnExpandedChanged -> {
                val current = _state.value.expanded
                _state.value = _state.value.copy(expanded = !current)
            }

            // ======================================

            // API 연동 관련
            // 회원가입 눌렀을 때
            is SignupIntent.OnSignupClicked -> {
                val error = validateSignupInput(
                    id = _state.value.id,
                    password = _state.value.pw,
                    passwordConfirm = _state.value.pwConfirm,
                    name = _state.value.englishName,
                    selectedGender = _state.value.selectedGender
                )

                if (error != null) { // 제대로 입력이 되지 않았을 경우
                    _state.value = _state.value.copy(errorMessage = error)

                } else {
                    // API 요청 또는 가입 성공 처리
                    signup()
                    _state.value = _state.value.copy(signupSuccess = true, errorMessage = null)
                }
            }
        }
    }


    private fun signup() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            val request = SignUpRequest(
                email = _state.value.id,
                password1 = _state.value.pw,
                password2 = _state.value.pwConfirm,
                name = _state.value.englishName,
                gender = _state.value.selectedGender.uppercase()
            )

            Log.d("SIGNUP_JSON", Gson().toJson(request))

            val result = authRepository.signup(request)

            _state.value = if (result.isSuccess) {
                _state.value.copy(isLoading = false, signupSuccess = true)
            } else {
                _state.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message
                )
            }

        }
    }
}