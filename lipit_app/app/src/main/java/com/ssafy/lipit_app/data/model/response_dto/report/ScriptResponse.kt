package com.ssafy.lipit_app.data.model.response_dto.report

import com.ssafy.lipit_app.data.model.request_dto.report.NativeExpression
import com.ssafy.lipit_app.data.model.request_dto.report.ReportScript

data class ScriptResponse(
    val script: List<ReportScript>
)
