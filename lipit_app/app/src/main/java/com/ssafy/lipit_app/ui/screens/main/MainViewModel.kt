package com.ssafy.lipit_app.ui.screens.main

import android.content.Context
import android.util.Log
import androidx.collection.arrayMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.lipit_app.data.model.request_dto.auth.LogoutRequest
import com.ssafy.lipit_app.data.model.response_dto.schedule.ScheduleResponse
import com.ssafy.lipit_app.data.model.response_dto.schedule.TopicCategory
import com.ssafy.lipit_app.domain.repository.MyVoiceRepository
import com.ssafy.lipit_app.domain.repository.ScheduleRepository
import com.ssafy.lipit_app.ui.screens.call.alarm.AlarmScheduler
import com.ssafy.lipit_app.ui.screens.edit_call.weekly_calls.CallSchedule
import com.ssafy.lipit_app.ui.screens.edit_call.weekly_calls.WeeklyCallsState
import com.ssafy.lipit_app.ui.screens.main.components.DailySentenceManager
import com.ssafy.lipit_app.ui.screens.main.components.dayFullToShort
import com.ssafy.lipit_app.util.SharedPreferenceUtils
import com.ssafy.lipit_app.util.SharedPreferenceUtils.PREF_ALARM_REGISTERED_PREFIX
import com.ssafy.lipit_app.util.SharedPreferenceUtils.PREF_ALARM_TIMESTAMP_PREFIX
import com.ssafy.lipit_app.util.sortSchedulesByDay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters

class MainViewModel(
    private val context: Context
) : ViewModel() {
    private val _state = MutableStateFlow(MainState())
    val state: StateFlow<MainState> = _state

    private val scheduleRepository by lazy { ScheduleRepository() }
    private val voiceRepository by lazy { MyVoiceRepository() }

    private val alarmScheduler by lazy { AlarmScheduler(context) }

    // 멤버 ID 가져오기
    private val memberId: Long by lazy {
        SharedPreferenceUtils.getMemberId()
    }

    init {
        loadDailySentence()
        val userName = SharedPreferenceUtils.getUserName()
        _state.value = _state.value.copy(userName = userName)

        clearAllAlarms()
        loadInitialData()
    }

    private fun loadDailySentence() {
        val sentenceOriginal = DailySentenceManager.getOriginal().ifBlank {
            "With your talent and hard work, sky’s the limit!"
        }
        val sentenceTranslated = DailySentenceManager.getTranslated().ifBlank {
            "너의 재능과 노력이라면, 한계란 없지!"
        }

        Log.d("FCM", "getOriginal: ${DailySentenceManager.getOriginal()}")

        _state.value = _state.value.copy(
            sentenceOriginal = sentenceOriginal,
            sentenceTranslated = sentenceTranslated
        )
    }

    fun onIntent(intent: MainIntent) {
        when (intent) {
            is MainIntent.OnDaySelected -> {
                _state.update {
                    it.copy(selectedDay = intent.day)
                }
            }

            //todo: call 기능 구현 시 수정할 것
            is MainIntent.OnCallClick -> TODO()

            // 로그아웃
            is MainIntent.OnLogoutClicked -> {
                logout(context)
            }

            is MainIntent.OnLogoutHandled -> {
                _state.value = _state.value.copy(isLogoutSuccess = false)
            }

            // NavGraph에서 처리
            is MainIntent.NavigateToReports,
            is MainIntent.NavigateToMyVoices,
            is MainIntent.NavigateToCallScreen,
            is MainIntent.NavigateToAddVoice -> {
                // 네비게이션 관련 상태 업데이트
            }

            // BottomSheet: 상태관리
            is MainIntent.OnSettingsClicked -> {
                Log.d("TAG", "onIntent: 바텀 상태관리이벤트 ${memberId}")
//                scheduleRepository.getWeeklyCallsSchedule(memberId)
                getWeeklyCallsSchedule()
            }

            is MainIntent.OnCloseSettingsSheet -> {
                _state.update { it.copy(isSettingsSheetVisible = false) }
            }

            is MainIntent.ResetBottomSheetContent -> {
                _state.update {
                    it.copy(bottomSheetContent = BottomSheetContent.WEEKLY_CALLS)
                }
            }

            // BottomSheet: 종류 변경
            is MainIntent.ShowWeeklyCallsScreen -> {
                _state.update { it.copy(bottomSheetContent = BottomSheetContent.WEEKLY_CALLS) }
            }
            // 스케줄 변경할 아이템 선택
            is MainIntent.SelectSchedule -> {
                _state.update {
                    it.copy(selectedSchedule = intent.schedule)
                }
            }

            is MainIntent.ShowRescheduleScreen -> {
                _state.update {
                    it.copy(
                        bottomSheetContent = BottomSheetContent.RESCHEDULE_CALL,
                        selectedSchedule = intent.schedule
                    )
                }
            }

            is MainIntent.ShowMyVoicesScreen -> {
                _state.update { it.copy(bottomSheetContent = BottomSheetContent.MY_VOICES) }
            }

            is MainIntent.DeleteSchedule -> {
                // 삭제 전에 알람 취소
                val scheduleToDelete =
                    _state.value.weeklyCallsState.callSchedules.find { it.callScheduleId == intent.scheduleId }
                cancelScheduleAlarm(scheduleToDelete, isDelete = true)

                // 스케줄 내역 삭제
                deleteScheduleAndReload(intent.scheduleId)
            }

            // 음성 정보 업데이트 후 메인 화면 갱신
            is MainIntent.RefreshAfterVoiceChange -> {
                loadInitialData()
            }


        }
    }

    private fun logout(context: Context) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLogoutClicked = true)

            // SecureDataStore에서 refreshToken 가져오기
            val refreshToken = com.ssafy.lipit_app.base.SecureDataStore
                .getInstance(context)
                .getRefreshToken()

            Log.d("auth", "logout: refreshToken - $refreshToken")

            // 로그아웃 요청 생성하기
            val request = LogoutRequest(refreshToken.toString())

            // 서버에 로그아웃 요청하기
            val result = com.ssafy.lipit_app.domain.repository.AuthRepository().logout(request)

            if (result.isSuccess) {
                // 성공 시 사용자 정보 삭제
                com.ssafy.lipit_app.base.SecureDataStore.getInstance(context).clearUserInfo()

                _state.value = _state.value.copy(isLogoutSuccess = true)

                // 사용자 id 정보 삭제
                SharedPreferenceUtils.clearMemberId()
            } else {
                _state.value = _state.value.copy(isLogoutSuccess = true)

            }
        }
    }

    // 사용자 레벨 아이콘 가져오기
    fun fetchUserLevel(memberId: Long) {
        viewModelScope.launch {
            val result =
                com.ssafy.lipit_app.domain.repository.AuthRepository().getMemberLevel(memberId)
            result.onSuccess { levelData ->
                Log.d(
                    "UserLevel",
                    "등급 정보: ${levelData.level}, 전화 누적 시간 퍼센트: ${levelData.totalCallDurationPercentage}, 리포트 개수 퍼센트: ${levelData.totalReportCountPercentage}"
                )
                _state.update {
                    it.copy(
                        level = levelData.level,
                        callPercent = levelData.totalCallDurationPercentage,
                        reportPercent = levelData.totalReportCountPercentage
                    )
                }
            }.onFailure { e ->
                Log.e("UserLevel", "회원 등급 조회 실패", e)

            }
        }
    }

    // 사용자의 일주일 스케줄 조회하기 - Main 화면
    fun fetchWeeklySchedule(memberId: Long) {
        Log.d("TAG", "fetchWeeklySchedule: 사용자 일주일스케줄 조회")
        viewModelScope.launch {
            val result = scheduleRepository.getWeeklyCallsSchedule(memberId)

            result.onSuccess { schedules ->
                val currentState = _state.value

                val callItems = schedules.map { schedule ->

                    // 알림 스케줄은 처음 한번만 등록
                    val alarmId = schedule.callScheduleId.toInt()
                    val isAlarmRegistered = isAlarmAlreadyRegistered(alarmId)

                    if (!isAlarmRegistered) {
                        Log.d(
                            "MainViewModel",
                            "알람 최초 등록: ${schedule.scheduledDay} ${schedule.scheduledTime}"
                        )
                        registerScheduleAlarm(schedule, currentState.callItem_name)
                        markAlarmAsRegistered(alarmId)
                    }

                    CallItem(
                        id = schedule.callScheduleId,
                        name = currentState.callItem_name,
                        topic = schedule.topicCategory?.let {
                            TopicCategory.fromEnglish(it)?.koreanName
                        } ?: "자유주제",
                        time = schedule.scheduledTime,
                        imageUrl = currentState.imageUrl,
                        scheduleDay = dayFullToShort(schedule.scheduledDay)
                    )
                }

                // 추가 BottomSheet 참조 내용도 업데이트
                _state.update {
                    it.copy(
                        callItems = callItems,
                        callItem_name = currentState.callItem_name,
                        imageUrl = currentState.imageUrl,
                        weeklyCallsState = it.weeklyCallsState.copy(
                            voiceName = currentState.callItem_name,
                            voiceImageUrl = currentState.imageUrl
                        )
                    )
                }

                _state.update { it.copy(callItems = callItems) }

            }.onFailure { e ->
                Log.e("schedule", "스케줄 불러오기 실패", e)
            }
        }
    }


    /**
     * 특정 알람이 이미 등록되었는지 확인
     */
    private fun isAlarmAlreadyRegistered(alarmId: Int): Boolean {
        val key = PREF_ALARM_REGISTERED_PREFIX + alarmId
        return SharedPreferenceUtils.getBoolean(key, false)
    }

    /**
     * 알람을 등록된 상태로 표시
     */
    private fun markAlarmAsRegistered(alarmId: Int) {
        val key = PREF_ALARM_REGISTERED_PREFIX + alarmId
        SharedPreferenceUtils.saveBoolean(key, true)
    }


    private fun registerScheduleAlarm(schedule: ScheduleResponse, callerName: String) {
        val scheduledDateTime = convertToLocalDateTime(schedule)
        val alarmId = schedule.callScheduleId.toInt()

        val existingTimestamp = getAlarmTimestamp(alarmId)
        if (existingTimestamp != null && existingTimestamp == scheduledDateTime) {
            Log.d("Alarm", "이미 동일한 시간에 알람이 등록되어 있음: $scheduledDateTime")
            return
        }

        alarmScheduler.cancelAlarm(alarmId)

        val now = LocalDateTime.now()
        if (scheduledDateTime.isAfter(now)) {
            // 알람 예약 시도
            val success = alarmScheduler.scheduleCallAlarm(
                time = scheduledDateTime,
                callerName = callerName,
                alarmId = alarmId,
                retryCount = 0
            )

            if (success) {
                saveAlarmTimestamp(alarmId, scheduledDateTime)
                markAlarmAsRegistered(alarmId)
                Log.d("Alarm", "알람 새로 등록: ID=$alarmId, 시간=${scheduledDateTime}")
            } else {
                Log.e("Alarm", "알람 등록 실패: ID=$alarmId")
            }
        } else {
            Log.d("Alarm", "현재 시간보다 이전이라 알람 등록하지 않음: $scheduledDateTime")
        }
    }

    private fun convertToLocalDateTime(schedule: ScheduleResponse): LocalDateTime {

        val scheduleDay = schedule.scheduledDay
        val scheduleTime = schedule.scheduledTime

        val today = LocalDate.now()
        val targetDate = today.with(TemporalAdjusters.nextOrSame(convertDayOfWeek(scheduleDay)))

        // 시간 파싱
        val timeParts = scheduleTime.split(":")
        val hour = timeParts[0].toInt()
        val minute = timeParts[1].toInt()

        val now = LocalDateTime.now()
        Log.d("TAG", "today: $today, targetDate: $targetDate, 시간= $hour:$minute ")

        // LocalDateTime 생성
        var scheduledDateTime = LocalDateTime.of(
            targetDate.year,
            targetDate.monthValue,
            targetDate.dayOfMonth,
            hour,
            minute
        )

        if (scheduledDateTime.isBefore(now)) {
            scheduledDateTime = scheduledDateTime.plusWeeks(1)
        }

        Log.d("TAG", "원래 시간: $targetDate $hour:$minute, 조정된 시간: $scheduledDateTime")

        return scheduledDateTime
    }

    // 알람 등록 시간 저장
    private fun saveAlarmTimestamp(alarmId: Int, timestamp: LocalDateTime) {
        val key = PREF_ALARM_TIMESTAMP_PREFIX + alarmId
        SharedPreferenceUtils.saveString(key, timestamp.toString())
    }

    // 저장된 알람 시간 가져오기
    private fun getAlarmTimestamp(alarmId: Int): LocalDateTime? {
        val key = PREF_ALARM_TIMESTAMP_PREFIX + alarmId
        val timestampStr = SharedPreferenceUtils.getString(key, "")
        return if (timestampStr.isNotEmpty()) {
            LocalDateTime.parse(timestampStr)
        } else null
    }

    private fun clearAllAlarms() {
        // 모든 등록된 알람 ID 가져오기
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val registeredAlarmIds = prefs.all.keys
            .filter { it.startsWith(PREF_ALARM_REGISTERED_PREFIX) }
            .map { it.removePrefix(PREF_ALARM_REGISTERED_PREFIX).toInt() }

        // 모든 알람 취소
        for (alarmId in registeredAlarmIds) {
            alarmScheduler.cancelAlarm(alarmId)

            // SharedPreferences에서 알람 정보 삭제
            val regKey = PREF_ALARM_REGISTERED_PREFIX + alarmId
            val timestampKey = PREF_ALARM_TIMESTAMP_PREFIX + alarmId
            SharedPreferenceUtils.remove(regKey)
            SharedPreferenceUtils.remove(timestampKey)

            Log.d("Alarm", "알람 초기화: ID=$alarmId")
        }

        Log.d("Alarm", "모든 알람 초기화 완료")
    }

    // Weekly calls - 현재 선택된 Voice 받아오기
    private fun loadInitialData() {
        Log.d("schedule", "로그 호출")
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true) }

                // 1. 현재 선택된 음성 정보 가져오기
                val selectedVoiceResult = voiceRepository.getVoice(memberId)
                Log.d("MyVoiceViewModel", "API 응답: $selectedVoiceResult")

                selectedVoiceResult.onSuccess { voice ->


                    val newVoiceName = voice[0].voiceName
                    val currentVoiceName = _state.value.callItem_name

                    // 음성 이름이 변경되었는지 확인
                    if (currentVoiceName.isNotEmpty() && currentVoiceName != newVoiceName) {
                        // 음성 이름이 변경된 경우, 모든 알림 업데이트
                        Log.d(
                            "MainViewModel",
                            "음성 이름 변경 감지: $currentVoiceName -> $newVoiceName"
                        )
                        updateAllScheduleAlarms(newVoiceName)
                    }

                    // 2. 선택된 음성 정보 저장
                    _state.update { currentState ->

                        val updatedCallItems = currentState.callItems.map { callItem ->
                            callItem.copy(
                                name = voice[0].voiceName,
                                imageUrl = voice[0].customImageUrl
                            )
                        }

                        SharedPreferenceUtils.saveSelectedVoiceName(voice[0].voiceName)
                        Log.d("MainViewModel", "선택된 음성 이름 저장: ${voice[0].voiceName}")

                        // 현재 선택된 Vocie 정보를 불러오면 BottomSheet 참조 내용 추가 업데이트
                        currentState.copy(
                            // 콜 아이템 업데이트
                            callItems = updatedCallItems,
                            // 메인 상태 업데이트
                            callItem_name = voice[0].voiceName,
                            imageUrl = voice[0].customImageUrl,
                            // 바텀 시트 상태 업데이트
                            weeklyCallsState = currentState.weeklyCallsState.copy(
                                voiceName = voice[0].voiceName,
                                voiceImageUrl = voice[0].customImageUrl
                            )
                        )
                    }

                    // Mian 에서 보여지는 일주일치 데이터는 해당 함수 내에서 얻어올 수 있음
                    fetchWeeklySchedule(memberId)
                }

                _state.update { it.copy(isLoading = false) }

            } catch (e: Exception) {
                println("예외 발생: ${e.message}")
            }
        }
    }


    // [Weekly Calls: BottomSheet] 요일별 스케쥴 리스트
    private fun getWeeklyCallsSchedule() {
        Log.d("TAG", "getWeeklyCallsSchedule: 이벤트 발생")
        val memberId = SharedPreferenceUtils.getMemberId()

        viewModelScope.launch {
            try {
                val voiceResult = voiceRepository.getVoice(memberId)
                val response = scheduleRepository.getWeeklyCallsSchedule(memberId = memberId)

                response.onSuccess { scheduleList ->
                    // 1. 최신 음성 정보 먼저 확인
                    var voiceName = _state.value.callItem_name
                    var voiceImageUrl = _state.value.imageUrl

                    // 새 음성 정보 있으면 업데이트
                    voiceResult.onSuccess { voiceList ->
                        if (voiceList.isNotEmpty()) {
                            voiceName = voiceList[0].voiceName
                            voiceImageUrl = voiceList[0].customImageUrl
                        }
                    }

                    // 2. ScheduleResponse → CallSchedule로 변환
                    val convertedSchedules = scheduleList.map { schedule ->
                        CallSchedule(
                            callScheduleId = schedule.callScheduleId,
                            memberId = memberId,
                            scheduleDay = schedule.scheduledDay,
                            scheduledTime = schedule.scheduledTime,
                            topicCategory = schedule.topicCategory?.let {
                                TopicCategory.fromEnglish(it)?.koreanName
                            } ?: "자유주제"
                        )
                    }

                    // 2. 요일 정렬 유틸 적용
                    val sortedSchedules = sortSchedulesByDay(convertedSchedules)

                    // 3. 상태 업데이트
                    _state.update { it ->
                        it.copy(
                            callItem_name = voiceName,
                            imageUrl = voiceImageUrl,
                            isSettingsSheetVisible = true,
                            weeklyCallsState = WeeklyCallsState(
                                voiceName = voiceName,
                                voiceImageUrl = voiceImageUrl,
                                callSchedules = sortedSchedules
                            )
                        )
                    }
                }.onFailure {
                    println("스케줄 조회 실패: ${it.message}")
                }

            } catch (e: Exception) {
                println("예외 발생: ${e.message}")
            }
        }
    }

    private fun deleteScheduleAndReload(scheduleId: Long) {
        val memberId = SharedPreferenceUtils.getMemberId()

        viewModelScope.launch {
            val result = scheduleRepository.deleteSchedule(
                callScheduleId = scheduleId,
                memberId = memberId
            )

            if (result.isSuccess) {
                getWeeklyCallsSchedule() // 삭제 후 다시 조회
            } else {
                Log.e("MainViewModel", "삭제 실패: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    // 음성 이름이 변경되었을 때, 기존 알림 스케쥴 모두 업데이트
    private fun updateAllScheduleAlarms(newVoiceName: String) {
        viewModelScope.launch {
            try {
                // 현재 등록된 스케쥴 모두 조회
                val scheduleResult = scheduleRepository.getWeeklyCallsSchedule(memberId)

                scheduleResult.onSuccess { schedules ->
                    schedules.forEach  { schedule ->
                        val alarmId = schedule.callScheduleId.toInt()

                        // 기존 알람 취소
                        alarmScheduler.cancelAlarm(alarmId)

                        // 알람이 한 번이라도 등록된 적이 있는지 확인
                        val wasRegistered = isAlarmAlreadyRegistered(alarmId)

                        if (wasRegistered) {
                            // 새 음성 이름으로 재등록
                            val scheduleDateTime = convertToLocalDateTime(schedule)
                            alarmScheduler.scheduleCallAlarm(
                                time = scheduleDateTime,
                                callerName = newVoiceName,
                                alarmId = alarmId,
                                retryCount = 0
                            )
                            Log.d(
                                "MainViewModel",
                                "알림 업데이트: ${schedule.scheduledDay} ${schedule.scheduledTime}, " +
                                        "새 음성 이름: $newVoiceName"
                            )
                        }
                    }
                }.onFailure { e ->
                    Log.e("MainViewModel", "알림 업데이트 실패: ${e.message}", e)
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "알림 업데이트 중 예외 발생: ${e.message}", e)
            }
        }
    }

    // 알람 매니저 : 알림 삭제 구현
    private fun cancelScheduleAlarm(schedule: CallSchedule?, isDelete: Boolean = false) {
        // isDelete : 삭제여부 (true) 로 들어오면 삭제 이벤트
        // schedule : 스케쥴 내역 (요일, 시간, 카테고리) => 해당 데이터를 기반으로 알람 설정 진행
        Log.d("TAG", "updateSchedule: Step 3")

        schedule?.let {
            val alarmScheduler = AlarmScheduler(context)

            if (isDelete) {
                alarmScheduler.cancelAlarm(schedule.callScheduleId.toInt())
                Log.d("Alarm", "알람 취소: ${schedule.callScheduleId} ${schedule.scheduleDay}")
            }
        }

        Log.d("Alarm", "Alarm _---------- 알람 정보 업데이트: $schedule, 삭제여부: $isDelete")
    }

    private fun convertDayOfWeek(day: String): java.time.DayOfWeek {
        return when (day.uppercase()) {
            "MONDAY" -> java.time.DayOfWeek.MONDAY
            "TUESDAY" -> java.time.DayOfWeek.TUESDAY
            "WEDNESDAY" -> java.time.DayOfWeek.WEDNESDAY
            "THURSDAY" -> java.time.DayOfWeek.THURSDAY
            "FRIDAY" -> java.time.DayOfWeek.FRIDAY
            "SATURDAY" -> java.time.DayOfWeek.SATURDAY
            "SUNDAY" -> java.time.DayOfWeek.SUNDAY
            else -> throw IllegalArgumentException("유효하지 않은 요일: $day")
        }
    }

}