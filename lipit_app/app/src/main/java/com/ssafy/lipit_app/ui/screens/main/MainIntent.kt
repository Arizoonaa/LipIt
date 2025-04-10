package com.ssafy.lipit_app.ui.screens.main
import com.ssafy.lipit_app.ui.screens.edit_call.weekly_calls.CallSchedule
import com.ssafy.lipit_app.ui.screens.edit_call.weekly_calls.WeeklyCallsIntent


sealed class MainIntent {
    data class OnCallClick(val id: Int) : MainIntent() // 전화 걸기 버튼 클릭
    data class OnDaySelected(val day: String) : MainIntent() // Weekly Calls에서 특정 요일 클릭

    // 네비게이션 인텐트 추가
    object NavigateToReports : MainIntent()
    object NavigateToMyVoices : MainIntent()
    object NavigateToCallScreen : MainIntent()
    object NavigateToAddVoice: MainIntent()

    // 로그아웃 관련
    object OnLogoutClicked : MainIntent()
    object OnLogoutHandled : MainIntent()

    // [Weekly Calls] Bottom Sheet: 일주일 일정 활/비활성화
    object OnSettingsClicked : MainIntent()
    object OnCloseSettingsSheet : MainIntent()
    object ResetBottomSheetContent : MainIntent()

    // BottomSheet 종류 변경
    object ShowWeeklyCallsScreen : MainIntent()
    data class ShowRescheduleScreen(val schedule: CallSchedule) : MainIntent()
    object ShowMyVoicesScreen: MainIntent()

    data class SelectSchedule(val schedule: CallSchedule) : MainIntent()

    // 스케줄 삭제 후, 리스트 갱신 이벤트를 받기 위해 정의
    data class DeleteSchedule(val scheduleId: Long) : MainIntent()

    // BottomSheet 에서 뒤로가기, 변경사항 이벤트를 감지하기 위한 기능
    object RefreshAfterVoiceChange : MainIntent()


}