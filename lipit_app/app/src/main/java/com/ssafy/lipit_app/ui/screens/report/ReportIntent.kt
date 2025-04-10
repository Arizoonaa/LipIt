package com.ssafy.lipit_app.ui.screens.report

sealed class ReportIntent {

    data object LoadReportList: ReportIntent()  // 전체 리포트 목록

    data class ReportItemClicked(val reportId: Long) : ReportIntent() // 리포트 클릭

    data class NavigateToReportDetail(val reportId: Long) : ReportIntent()
}