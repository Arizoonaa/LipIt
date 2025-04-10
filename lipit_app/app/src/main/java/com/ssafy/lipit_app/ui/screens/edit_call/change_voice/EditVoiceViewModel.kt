package com.ssafy.lipit_app.ui.screens.edit_call.change_voice

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.lipit_app.domain.repository.MyVoiceRepository
import com.ssafy.lipit_app.util.SharedPreferenceUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditVoiceViewModel: ViewModel() {

    private val _state = MutableStateFlow(EditVoiceState())
    val state: StateFlow<EditVoiceState> = _state.asStateFlow()
    private val voiceRepository = MyVoiceRepository()

    private val memberId: Long by lazy {
        SharedPreferenceUtils.getMemberId()
    }

    init {
        loadInitialData()
    }


    fun onIntent(intent: EditVoiceIntent) {
        when (intent) {
            is EditVoiceIntent.LoadCelebrityVoices -> loadCelebrityVoices()
            is EditVoiceIntent.LoadCustomVoices -> loadCustomVoices()
            is EditVoiceIntent.SelectVoice -> selectVoice(intent)
            is EditVoiceIntent.NavigateToAddVoice -> {}
            is EditVoiceIntent.ChangeVoice -> changeVoice(intent.voiceId)
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // 현재 선택된 보이스 정보 로드
            val selectedVoiceResult = voiceRepository.getVoice(memberId)
            selectedVoiceResult.onSuccess { voices ->
                Log.d("EditVoiceViewModel", "loadInitialData: ${voices.size}")
                if (voices.isNotEmpty()) {
                    _state.update {
                        it.copy(
                            selectedVoiceName = voices[0].voiceName,
                            selectedVoiceUrl = voices[0].customImageUrl
                        )
                    }
                    SharedPreferenceUtils.saveSelectedVoiceName(voices[0].voiceName)
                }
            }

            // 초기 데이터 로드
            loadCelebrityVoices()
            loadCustomVoices()
        }
    }

    private fun loadCelebrityVoices() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val result = voiceRepository.getCelebVoices(memberId)
            result.onSuccess { celebList ->
                _state.update {
                    it.copy(
                        celebrityVoices = celebList,
                        isLoading = false
                    )
                }
            }.onFailure { error ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "셀럽 목소리를 불러오는 중 오류가 발생했습니다."
                    )
                }
            }
        }
    }

    private fun loadCustomVoices() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val result = voiceRepository.getCustomVoices(memberId)
            result.onSuccess { customList ->
                _state.update {
                    it.copy(
                        customVoices = customList,
                        isLoading = false
                    )
                }
            }.onFailure { error ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "커스텀 목소리를 불러오는 중 오류가 발생했습니다."
                    )
                }
            }
        }
    }

    private fun selectVoice(intent: EditVoiceIntent.SelectVoice) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    selectedVoiceName = intent.voiceName,
                    selectedVoiceUrl = intent.voiceUrl
                )
            }
        }
    }


    private fun changeVoice(voiceId: Long) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true, errorMessage = null) }

                // 1. 음성 변경 API 호출
                val result = voiceRepository.changeVoice(memberId, voiceId)
                Log.d("MyVoiceViewModel", "changeVoice API 응답: $result")

                result.onSuccess { response ->
                    Log.d("MyVoiceViewModel", "음성 변경 성공, 데이터 다시 로드")

                    loadInitialData()

                    val currentVoiceName = _state.value.selectedVoiceName

                    if (currentVoiceName.isNotEmpty()) {
                        SharedPreferenceUtils.saveSelectedVoiceName(currentVoiceName)
                        Log.d("MyVoiceViewModel", "선택된 음성 이름 저장: $currentVoiceName")
                    } else {
                        SharedPreferenceUtils.saveSelectedVoiceName("Sarang")
                        Log.d("MyVoiceViewModel", "선택된 음성 이름이 없어 기본값 저장")
                    }


                }.onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "음성 변경 중 오류가 발생했습니다."
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "음성 변경 중 오류가 발생했습니다."
                    )
                }
            }
        }

    }
}