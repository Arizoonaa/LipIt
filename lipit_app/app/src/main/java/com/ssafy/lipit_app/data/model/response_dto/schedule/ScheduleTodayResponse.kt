package com.ssafy.lipit_app.data.model.response_dto.schedule


data class ScheduleTodayResponse(
    val status: Int,
    val message: String,
    val data: ScheduleResponse
)