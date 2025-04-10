package com.ssafy.lipit_app.data.model.request_dto.report

data class ReportScript(
    val isAI: Boolean, // true면 진한 보라색, false면 연한 보라색
    val content: String = "",
    val contentKor: String = "",
    val timestamp: String
)