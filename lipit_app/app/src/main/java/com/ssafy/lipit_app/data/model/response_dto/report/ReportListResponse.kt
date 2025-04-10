package com.ssafy.lipit_app.data.model.response_dto.report

data class ReportListResponse(
    val reportId: Long,
    val callDuration: Int,             // 통화 시간
    val celebVideoUrl: String? = null,         // 영상 url
    val wordCount: Int,                // 단어 수
    val sentenceCount: Int,            // 문장 수
    val communicationSummary: String,  // 대화 요약
    val feedbackSummary: String,       // 피드백 요약
    val createdAt: String              // 생성일
)
