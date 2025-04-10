package com.ssafy.lipit_app.ui.screens.edit_call.weekly_calls

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.lipit_app.data.model.response_dto.schedule.TopicCategory
import com.ssafy.lipit_app.domain.repository.MyVoiceRepository
import com.ssafy.lipit_app.domain.repository.ScheduleRepository
import com.ssafy.lipit_app.ui.screens.edit_call.reschedule.WeeklyCallsUiState
import com.ssafy.lipit_app.util.sortSchedulesByDay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WeeklyCallsViewModel : ViewModel() {
    private val _state = MutableStateFlow(WeeklyCallsUiState())
    val state: StateFlow<WeeklyCallsUiState> = _state

    fun onIntent(intent: WeeklyCallsIntent) {
        when (intent) {
            is WeeklyCallsIntent.OnDaySelected -> {
                // 요일 선택 처리
            }

            is WeeklyCallsIntent.OnCallSelected -> {
                // 개별 call 선택 처리
            }

            is WeeklyCallsIntent.OnEditSchedule -> {
                // 바텀시트 내부에서 edit 모드로 전환 트리거 (MainScreen의 isEditMode 전환을 호출해야 함)
            }

            // Delete 는 Main 에서 진행. WeeklyCallScreen 에서는 이벤트 전달만
//            is WeeklyCallsIntent.OnDeleteSchedule -> {}

            is WeeklyCallsIntent.OnChangeVoice -> {

            }

            is WeeklyCallsIntent.SelectFreeMode -> {
                _state.update {
                    it.copy(
                        editState = it.editState.copy(
                            isFreeModeSelected = intent.isSelected,
                            isCategoryModeSelected = !intent.isSelected
                        )
                    )
                }
            }

            is WeeklyCallsIntent.SelectCategory -> {
                _state.update {
                    it.copy(editState = it.editState.copy(
                        selectedCategory = intent.category
                    ))
                }
            }

        }
    }

}