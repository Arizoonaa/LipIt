package com.ssafy.lipit_app.ui.screens.call.incoming

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.lipit_app.domain.repository.MyVoiceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class IncomingCallViewModel : ViewModel() {

    private val _state = MutableStateFlow(IncomingCallState())
    val state: StateFlow<IncomingCallState> = _state


    fun onIntent(intent: IncomingCallIntent) {
        when (intent) {
            is IncomingCallIntent.Accept -> {
                _state.value = _state.value.copy(callAccepted = true)
            }

            is IncomingCallIntent.Decline -> {
                _state.value = _state.value.copy(callDeclined = true)
            }
        }
    }

    fun fetchSelectedVoiceName(memberId: Long) {
        viewModelScope.launch {
            val result = MyVoiceRepository().getVoiceName(memberId)
            result.onSuccess { name ->
                _state.update { it.copy(voiceName = name) }
            }.onFailure {
                Log.e("IncomingCall", "❌ 음성 이름 로드 실패: ${it.message}")
            }
        }
    }

}