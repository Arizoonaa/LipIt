package com.ssafy.lipit_app.data.remote

import com.ssafy.lipit_app.data.model.request_dto.report.ReportSummary
import com.ssafy.lipit_app.data.model.response_dto.BaseResponse
import com.ssafy.lipit_app.data.model.response_dto.report.NativeResponse
import com.ssafy.lipit_app.data.model.response_dto.report.ReportListResponse
import com.ssafy.lipit_app.data.model.response_dto.report.ScriptResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ReportService {

    // 리포트 목록 조회
    @GET("reports")
    suspend fun getReportList(
        @Query("member_id") memberId: Long
    ): Response<BaseResponse<List<ReportListResponse>>>

    // 리포트 요약 조회
    @GET("reports/{report_id}/summary")
    suspend fun getReportSummary(
        @Path("report_id") reportId: Long
    ): Response<BaseResponse<ReportSummary>>

    // 리포트 스크립트 조회
    @GET("reports/{report_id}/script")
    suspend fun getReportScript(
        @Path("report_id") reportId: Long
    ) : Response<BaseResponse<ScriptResponse>>

    // 리포트 원어민 표현 조회
    @GET("reports/{report_id}/expressions")
    suspend fun getNativeExpressions(
        @Path("report_id") reportId: Long
    ) : Response<BaseResponse<NativeResponse>>

}