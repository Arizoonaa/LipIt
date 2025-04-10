package com.ssafy.lipit_app.domain.repository

import android.util.Log
import com.ssafy.lipit_app.data.model.request_dto.report.NativeExpression
import com.ssafy.lipit_app.data.model.request_dto.report.ReportScript
import com.ssafy.lipit_app.data.model.request_dto.report.ReportSummary
import com.ssafy.lipit_app.data.model.response_dto.report.NativeResponse
import com.ssafy.lipit_app.data.model.response_dto.report.ReportListResponse
import com.ssafy.lipit_app.data.model.response_dto.report.ScriptResponse
import com.ssafy.lipit_app.data.remote.RetrofitUtil
import handleResponse

private const val TAG = "ReportRepository"

class ReportRepository {

    // 전체 리포트 조회
    suspend fun getReportList(memberId: Long)
            : Result<List<ReportListResponse>> {
        return try {
            val response = RetrofitUtil.reportService.getReportList(memberId)
            if (response.isSuccessful) {
                Log.d(TAG, "전체 리포트 목록 조회 :  $response")
            }
            handleResponse(response)
        } catch (e: Exception) {
            Log.e(TAG, "getReportList: 예외 발생 ${e.message}")
            Result.failure(e)
        }
    }

    // 리포트 요약 조회
    suspend fun getReportSummary(reportId: Long)
            : Result<ReportSummary> {
        return try {
            val response = RetrofitUtil.reportService.getReportSummary(reportId)
            if (response.isSuccessful) {
                Log.d(TAG, "리포트 요약 정보 : $response")
            }
            handleResponse(response)
        } catch (e: Exception) {
            Log.e(TAG, "getReportSummary: 예외 발생 ${e.message}")
            Result.failure(e)
        }
    }

    // 리포트 스크립트 조회
    suspend fun getReportScript(reportId: Long)
            : Result<ScriptResponse> {
        return try {
            val response = RetrofitUtil.reportService.getReportScript(reportId)
            if (response.isSuccessful) {
                Log.d(TAG, "리포트 스크립트 내용 조회 : $response")
            }
            handleResponse(response)
        } catch (e: Exception) {
            Log.e(TAG, "getReportScript: 예외 발생 ${e.message}")
            Result.failure(e)
        }
    }

    // 리포트 원어민 표현 조회
    suspend fun getNativeExpressions(reportId: Long)
            : Result<NativeResponse> {
        return try {
            val response = RetrofitUtil.reportService.getNativeExpressions(reportId)
            if (response.isSuccessful) {
                Log.d(TAG, "원어민 표현 조회 : $response")
            }
            handleResponse(response)
        } catch (e: Exception) {
            Log.e(TAG, "getNativeExpressions: 예외 발생 ${e.message}")
            Result.failure(e)
        }
    }
}