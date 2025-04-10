package com.ssafy.lipit_app.data.model.response_dto.auth

data class MemberIdResponse(
    val level: Int,
    val badgeIcon: String,
    val totalCallDurationPercentage: Int,
    val totalReportCountPercentage: Int
)