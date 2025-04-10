package com.ssafy.lipit_app.ui.screens.edit_call.change_voice

import com.ssafy.lipit_app.data.model.response_dto.myvoice.CelabResponse
import com.ssafy.lipit_app.data.model.response_dto.myvoice.CustomResponse

data class EditVoiceState(
    val selectedVoiceName: String = "",
    val selectedVoiceUrl: String = "",

    val celebrityVoices: List<CelabResponse> = emptyList(),
    val customVoices: List<CustomResponse> = emptyList(),

    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

