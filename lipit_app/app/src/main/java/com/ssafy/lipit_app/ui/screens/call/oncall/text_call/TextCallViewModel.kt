package com.ssafy.lipit_app.ui.screens.call.oncall.text_call

import android.util.Log
import androidx.lifecycle.ViewModel
import com.ssafy.lipit_app.data.model.ChatMessageText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class TextCallViewModel : ViewModel() {
    private val _state = MutableStateFlow(TextCallState())
    val state: StateFlow<TextCallState> = _state

    fun toggleMode() {
        _state.update { it.copy(currentMode = if (it.currentMode == "Text") "Voice" else "Text") }

    }

    fun setReportFailed() {
        _state.update { it.copy(reportFailed = true) }
    }


    fun addMessage(message: ChatMessageText) {
        if (state.value.messages.any { it.text == message.text && !it.isFromUser }) {
            Log.d("TextCall", "❗ 중복 메시지 감지, 추가 생략: ${message.text}")
            return
        }

        _state.update { current ->
            current.copy(messages = current.messages + message)
        }
    }

    fun getMessages(): List<ChatMessageText> {
        return state.value.messages
    }


    fun onIntent(intent: TextCallIntent, onSendToServer: (String) -> Unit = {}) {
        when (intent) {
            // 번역 켜고 끄기
            is TextCallIntent.ToggleTranslation -> {
                _state.update { it.copy(showTranslation = intent.show) }
            }

            // 사용자 입력 반영
            is TextCallIntent.UpdateInputText -> {
                _state.update { it.copy(inputText = intent.text) }
            }

            // 입력된 메시지 보내기
            is TextCallIntent.SendMessage -> {
                val currentText = _state.value.inputText
                if (currentText.isNotBlank()) {
                    val newMessage = ChatMessageText(
                        text = currentText,
                        isFromUser = true
                    )

                    Log.d("TextCallVM", "🧍‍♂️ 사용자 메시지 추가됨: $newMessage")

                    _state.update {
                        it.copy(
                            messages = it.messages + newMessage,
                            inputText = "" // 전송 후 텍스트 초기화
                        )
                    }

                    onSendToServer(currentText)

                }
            }

        }
    }

    // 보이스 -> 텍스트 넘어갈 때 초기 대화 내역 설정
    fun setInitialMessages(initialMessages: List<ChatMessageText>) {
        _state.update { it.copy(messages = initialMessages) }
    }

    // 사용자 텍스트 입력 관련
    fun onTextInputChanged(newInput: String) {
        _state.update { it.copy(inputText = newInput) }
    }

}