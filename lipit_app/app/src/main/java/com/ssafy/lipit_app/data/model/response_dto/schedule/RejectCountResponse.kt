package com.ssafy.lipit_app.data.model.response_dto.schedule

data class RejectCountResponse(
    val isCalled: Boolean,
    val missedCount: Int
)