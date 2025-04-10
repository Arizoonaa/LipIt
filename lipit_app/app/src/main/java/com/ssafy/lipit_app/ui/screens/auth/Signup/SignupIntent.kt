package com.ssafy.lipit_app.ui.screens.auth.Signup

//  Intent → ViewModel → State 업데이트

sealed class SignupIntent {
    // UI 데이터 업데이트 관련
    data class OnIdChanged(val id: String) : SignupIntent()
    data class OnPwChanged(val pw: String) : SignupIntent()
    data class OnPwConfirmChanged(val pwConfirm: String) : SignupIntent()
    data class OnEnglishNameChanged(val englishName: String) : SignupIntent()
    data class OnSelectedGenderChanged(val selectedGender: String) : SignupIntent()

    data class OnisPasswordVisible1Changed(val isPasswordVisible_1: Boolean) : SignupIntent()
    data class OnisPasswordVisible2Changed(val isPasswordVisible_2: Boolean) : SignupIntent()

    data class OnExpandedChanged(val expanded: Boolean) : SignupIntent()

    // API 연동 관련
    object OnSignupClicked : SignupIntent()
}