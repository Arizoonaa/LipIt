package com.ssafy.lipit_app.ui.screens.report

import com.ssafy.lipit_app.data.model.response_dto.report.ReportListResponse

data class ReportState(
    // 전체 리포트 목록
    val totalReportList: List<ReportListResponse> = emptyList(),

    // 로딩 상태
    val isLoading: Boolean = false,
    // 에러 메시지
    val error: String? = null

)
