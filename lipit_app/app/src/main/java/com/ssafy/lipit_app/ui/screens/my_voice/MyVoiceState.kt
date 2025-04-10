package com.ssafy.lipit_app.ui.screens.my_voice

import com.ssafy.lipit_app.data.model.response_dto.myvoice.CelabResponse
import com.ssafy.lipit_app.data.model.response_dto.myvoice.CustomResponse

data class MyVoiceState(
    //  현재 선택된 목소리 관련
    val selectedVoiceName: String = "",
    val selectedVoiceUrl: String = "",

    // 셀럽 보이스 목록
    val myCelebrityVoiceList: List<CelabResponse> = emptyList(),
    // 커스텀 보이스 목록
    val myCustomVoiceList: List<CustomResponse> = emptyList(),

    // 탭 상태
    val selectedTab: String = "Celebrity", // "Celebrity" 또는 "Custom"
    val pageCount: Int = 0,

    // 로딩 상태
    val isLoading: Boolean = false,
    // 오류 메시지
    val errorMessage: String? = null

)
