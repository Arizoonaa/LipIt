package com.ssafy.lipit_app.ui.screens.auth.Login

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import com.ssafy.lipit_app.base.SecureDataStore
import com.ssafy.lipit_app.data.model.request_dto.auth.LoginRequest
import com.ssafy.lipit_app.domain.repository.AuthRepository
import com.ssafy.lipit_app.util.SharedPreferenceUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException

class LoginViewModel(private val context: Context) : ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state

    private val authRepository by lazy { AuthRepository() }

    init {
        loadFcmToken()
    }

    private fun loadFcmToken() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    Log.d("FCM", "디바이스 토큰: $token")
                    _state.value = _state.value.copy(fcmToken = token)
                } else {
                    Log.e("FCM", "토큰 가져오기 실패", task.exception)
                }
            }
    }


    fun onIntent(intent: LoginIntent) {
        when (intent) {
            // id 변경
            is LoginIntent.onIdChanged -> {
                _state.value = _state.value.copy(id = intent.id)
            }

            // pw 변경
            is LoginIntent.onPwChanged -> {
                _state.value = _state.value.copy(pw = intent.pw)
            }

            // login 버튼 눌렸을 경우
            is LoginIntent.OnLoginClicked -> {
                login()
            }

            is LoginIntent.OnisPasswordVisibleChanged -> {
                val current = _state.value.isPasswordVisible
                _state.value = _state.value.copy(isPasswordVisible = !current)

            }

            is LoginIntent.OnLoginHandled -> {
                _state.value = _state.value.copy(isLoginSuccess = false, errorMessage = null)
            }
        }

    }

    private fun login() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoginClicked = true)

            // 입력 유효성 검사
            if (_state.value.id.isEmpty() || _state.value.pw.isEmpty()) {
                _state.value = _state.value.copy(
                    errorMessage = "아이디와 비밀번호를 모두 입력해주세요.",
                    isLoginClicked = false
                )
                return@launch
            }

            val request = LoginRequest(
                email = _state.value.id,
                password = _state.value.pw,
                fcmToken = _state.value.fcmToken
            )


            val result = authRepository.login(request)

            if (result.isSuccess) {
                val rawJson = result.toString()
                Log.d("LOGIN_JSON", "응답 내용: $rawJson")


                val loginData = result.getOrNull()

                loginData?.let { data ->

                    Log.d(
                        "LoginViewModel",
                        "로그인 성공: ${data.email}, 토큰: ${data.accessToken.take(15)}, fcm 디바이스 토큰: ${data.fcmToken}"
                    )
                    SecureDataStore.getInstance(context).saveUserInfo(data)
                    SharedPreferenceUtils.saveMemberId(data.memberId)

                    // 사용자 이름 저장 -> 따로 api가 없음
                    SharedPreferenceUtils.saveUserName(data.name)

                    SecureDataStore.getInstance(context).saveUserInfo(data)
                    SharedPreferenceUtils.saveMemberId(data.memberId)

                    // 로그인 성공 상태로 업데이트
                    _state.value = _state.value.copy(isLoginSuccess = true, isLoginClicked = false)
                } ?: run {
                    Log.e("LoginViewModel", "로그인 응답 데이터가 null입니다")
                    _state.value = _state.value.copy(
                        errorMessage = "로그인 데이터를 받지 못했습니다",
                        isLoginClicked = false
                    )
                }
            } else {
                val exception = result.exceptionOrNull()
                val errorMsg = if (exception is HttpException) {
                    val errorBody = exception.response()?.errorBody()?.string()
                    extractErrorMessages(errorBody)
                } else {
                    exception?.message ?: "알 수 없는 오류가 발생했어요."
                }
                _state.value = _state.value.copy(errorMessage = errorMsg)
            }

        }

    }

    fun extractErrorMessages(errorBody: String?): String {
        if (errorBody.isNullOrEmpty()) return "알 수 없는 오류가 발생했어요."

        return try {
            val json = JSONObject(errorBody)
            val errorsArray = json.optJSONArray("errors")
            if (errorsArray != null && errorsArray.length() > 0) {
                // errors 안에 있는 모든 메시지를 줄바꿈으로 연결
                buildString {
                    for (i in 0 until errorsArray.length()) {
                        val obj = errorsArray.getJSONObject(i)
                        appendLine(obj.optString("message"))
                    }
                }.trim()
            } else {
                json.optString("message", "오류가 발생했습니다.")
            }
        } catch (e: Exception) {
            "에러 메시지를 파싱하는 데 실패했어요."
        }

    }


}