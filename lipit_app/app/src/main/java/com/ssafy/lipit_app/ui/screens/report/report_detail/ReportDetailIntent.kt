package com.ssafy.lipit_app.ui.screens.report.report_detail

sealed class ReportDetailIntent {
    data class LoadReportSummary(val reportId: Long) : ReportDetailIntent()  // 리포트 요약
    data class LoadReportScript(val reportId: Long) : ReportDetailIntent() // 리포트 스크립트 목록
    data class LoadNativeExpression(val reportId: Long) : ReportDetailIntent() // 원어민 표현

    data class SelectTab(val index: Int) : ReportDetailIntent()
}
