package com.ssafy.lipit_app.ui.screens.edit_call.reschedule

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.lipit_app.data.model.request_dto.schedule.ScheduleCreateRequest
import com.ssafy.lipit_app.data.model.response_dto.schedule.TopicCategory
import com.ssafy.lipit_app.domain.repository.ScheduleRepository
import com.ssafy.lipit_app.ui.screens.call.alarm.AlarmScheduler
import com.ssafy.lipit_app.ui.screens.edit_call.weekly_calls.CallSchedule
import com.ssafy.lipit_app.util.SharedPreferenceUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.TemporalAdjusters

class EditCallViewModel(
    private val context: Context? = null
) : ViewModel() {

    private val _state = MutableStateFlow(EditCallState())
    val state: StateFlow<EditCallState> = _state

    private val scheduleRepository by lazy { ScheduleRepository() }


    fun onIntent(intent: EditCallIntent, onSuccess: () -> Unit = {}) {
        when (intent) {
            is EditCallIntent.SelectCategory -> {}
            is EditCallIntent.SelectFreeMode -> {}

            is EditCallIntent.CreateSchedule -> {
                createSchedule(intent.schedule, onSuccess)  // 일정 추가
            }

            is EditCallIntent.UpdateSchedule -> {
                Log.d("TAG", "EditCallViewModel Change: EditCallITnetn ${intent.schedule}")
                updateSchedule(intent.schedule, onSuccess)  // 일정 수정
            }

        }
    }

    private fun createSchedule(schedule: CallSchedule, onSuccess: () -> Unit) {
        viewModelScope.launch {

            val memberId = SharedPreferenceUtils.getMemberId()
            val day = schedule.scheduleDay
            val time = schedule.scheduledTime
            val topic = schedule.topicCategory

            // topicCategory가 null이거나 "자유주제"인 경우 카테고리 없이 요청
            val request = if (topic.isNullOrBlank() || topic == "자유주제") {
                ScheduleCreateRequest(
                    scheduledDay = day,
                    scheduledTime = time
                )
            } else {
                // 카테고리 이름을 영어로 변환하여 요청
                val englishTopic = TopicCategory.fromKorean(topic)?.name
                ScheduleCreateRequest(
                    scheduledDay = day,
                    scheduledTime = time,
                    topicCategory = englishTopic
                )
            }

            val result = scheduleRepository.createSchedule(memberId, request)
            if (result.isSuccess) {
                setScheduleAlarm(schedule)  // 알림 추가
                onSuccess()
            } else {
                println("❌ 일정 추가 실패: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    private fun updateSchedule(schedule: CallSchedule, onSuccess: () -> Unit) {
        viewModelScope.launch {

            val topic = schedule.topicCategory
            // "자유주제"면 null, 그 외엔 영어 enum으로 변환
            val englishTopic = if (topic.isNullOrBlank() || topic == "자유주제") {
                null
            } else {
                TopicCategory.fromKorean(topic)?.name // "스포츠" → "SPORTS"
            }


            val request = ScheduleCreateRequest(
                scheduledDay = schedule.scheduleDay,
                scheduledTime = schedule.scheduledTime,
                topicCategory = englishTopic
            )

            Log.d("TAG", "updateSchedule: Send Server Data ${request}")


            // 알람(스케줄) 내역 업데이트 API 실행
            val result = scheduleRepository.updateSchedule(
                callScheduleId = schedule.callScheduleId,
                memberId = schedule.memberId,
                request = request
            )

            if (result.isSuccess) {
                Log.d("TAG", "updateSchedule: Step 1")
                setScheduleAlarm(schedule)  // 알림 수정
                onSuccess()
            } else {
                println("❌ 일정 수정 실패: ${result.exceptionOrNull()?.message}")
            }
        }
    }


    /**
     *
     * TODO AlarmManager : Add, Update == Set
     * AlarmManager 수정/추가 작업 진행할 부분
     */
    private fun setScheduleAlarm(schedule: CallSchedule) {
        context?.let { context ->
            val alarmScheduler = AlarmScheduler(context)

            val scheduledDay = when (schedule.scheduleDay) {
                "MONDAY" -> DayOfWeek.MONDAY
                "TUESDAY" -> DayOfWeek.TUESDAY
                "WEDNESDAY" -> DayOfWeek.WEDNESDAY
                "THURSDAY" -> DayOfWeek.THURSDAY
                "FRIDAY" -> DayOfWeek.FRIDAY
                "SATURDAY" -> DayOfWeek.SATURDAY
                "SUNDAY" -> DayOfWeek.SUNDAY
                else -> DayOfWeek.MONDAY
            }

            val scheduledTime = LocalTime.parse(schedule.scheduledTime)
            val currentDateTime = LocalDateTime.now()
            val currentDayOfWeek = currentDateTime.dayOfWeek
            val currentVoiceName = SharedPreferenceUtils.getSelectedVoiceName()

            Log.d(
                "AlarmScheduler",
                "currentDatetime: $currentDateTime, dayofweek: $currentDayOfWeek"
            )
            val nextScheduledDate = if (currentDayOfWeek == scheduledDay) {
                // 현재 요일과 스케줄된 요일이 같으면 오늘 시간으로 설정
                currentDateTime.with(scheduledTime)
            } else {
                // 다른 요일이면 다음 해당 요일로 설정
                currentDateTime
                    .with(TemporalAdjusters.nextOrSame(scheduledDay))
                    .with(scheduledTime)
            }

            alarmScheduler.scheduleCallAlarm(
                time = nextScheduledDate,
                callerName = currentVoiceName,  // TODO:: 보이스 설정이름으로
                alarmId = schedule.callScheduleId.toInt(),
                retryCount = 0
            )

            Log.d("Alarm", "Alarm 설정: $schedule")
        }
    }

    private fun DayOfWeek.toDayOfWeek(): DayOfWeek {
        return when (this) {
            DayOfWeek.MONDAY -> DayOfWeek.MONDAY
            DayOfWeek.TUESDAY -> DayOfWeek.TUESDAY
            DayOfWeek.WEDNESDAY -> DayOfWeek.WEDNESDAY
            DayOfWeek.THURSDAY -> DayOfWeek.THURSDAY
            DayOfWeek.FRIDAY -> DayOfWeek.FRIDAY
            DayOfWeek.SATURDAY -> DayOfWeek.SATURDAY
            DayOfWeek.SUNDAY -> DayOfWeek.SUNDAY
        }
    }

}