package com.ssafy.lipit_app.data.model.request_dto.report

data class ReportSummary(
    val reportId: Long = -1,
    val callDuration: Int,  // 통화 시간(초)
    val celebVideoUrl: String = "",  // 셀럽 영상
    val wordCount: Int,  // 단어 수
    val sentenceCount: Int,  // 문장 수
    val communicationSummary: String,  // 대화 요약
    val feedbackSummary: String,  // 피드백 요약
    val createdAt: String  // 생성일
)
