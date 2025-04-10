package com.ssafy.lipit_app.data.model.request_dto.schedule

data class ScheduleCreateRequest(
    val scheduledDay: String,
    val scheduledTime: String,
    val topicCategory: String? = null
)