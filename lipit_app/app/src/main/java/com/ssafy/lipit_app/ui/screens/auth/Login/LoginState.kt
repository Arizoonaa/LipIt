package com.ssafy.lipit_app.ui.screens.auth.Login

data class LoginState(
    var id: String = "",
    var pw: String = "",
    var fcmToken: String = "",

    val isLoginClicked: Boolean = false,
    val isLoginSuccess: Boolean = false,
    val errorMessage: String? = null,
    var isPasswordVisible: Boolean = false,

    )