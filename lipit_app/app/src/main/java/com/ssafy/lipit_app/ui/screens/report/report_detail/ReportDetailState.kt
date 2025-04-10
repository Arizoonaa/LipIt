package com.ssafy.lipit_app.ui.screens.report.report_detail

import com.ssafy.lipit_app.data.model.request_dto.report.NativeExpression
import com.ssafy.lipit_app.data.model.request_dto.report.ReportScript
import com.ssafy.lipit_app.data.model.request_dto.report.ReportSummary

data class ReportDetailState(
    // 리포트 요약
    val reportSummary: ReportSummary? = null,
    // 리포트 스크립트 내용
    val reportScript: List<ReportScript> = emptyList(),
    // 원어민 표현
    val nativeExpression: List<NativeExpression> = emptyList(),

    var selectedTabIndex: Int = 0,

    // 로딩 상태
    val isLoading: Boolean = false,
    // 에러 메시지
    val error: String? = null
)
