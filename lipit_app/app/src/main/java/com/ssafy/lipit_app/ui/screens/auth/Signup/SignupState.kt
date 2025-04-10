package com.ssafy.lipit_app.ui.screens.auth.Signup

data class SignupState(
    var id: String = "",
    var pw: String = "",
    var pwConfirm: String = "",
    var englishName: String = "",
    var selectedGender: String = "",

    var isPasswordVisible_1: Boolean = false,
    var isPasswordVisible_2: Boolean = false,
    var expanded: Boolean = false,

    val isLoading: Boolean = false,
    val signupSuccess: Boolean = false,
    val errorMessage: String? = null,
)