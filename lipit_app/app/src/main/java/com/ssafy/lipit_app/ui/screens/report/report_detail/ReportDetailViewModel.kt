package com.ssafy.lipit_app.ui.screens.report.report_detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.lipit_app.domain.repository.ReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "ReportDetailViewModel"
class ReportDetailViewModel(reportId: Long) : ViewModel() {

    private val _state = MutableStateFlow(ReportDetailState())
    val state: StateFlow<ReportDetailState> = _state.asStateFlow()

    private val reportRepository = ReportRepository()

    init {
        loadReportSummary(reportId)
        loadReportScript(reportId)
        loadNativeExpression(reportId)
    }

    fun onIntent(intent: ReportDetailIntent) {
        when (intent) {
            is ReportDetailIntent.LoadReportSummary -> loadReportSummary(intent.reportId)
            is ReportDetailIntent.LoadReportScript -> loadReportScript(intent.reportId)
            is ReportDetailIntent.LoadNativeExpression -> loadNativeExpression(intent.reportId)
            is ReportDetailIntent.SelectTab -> selectTab(intent.index)
        }
    }

    private fun loadReportSummary(reportId: Long) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true, error = null) }

                val result = reportRepository.getReportSummary(reportId)
                result.onSuccess { summary ->
                    _state.update { currentState ->
                        currentState.copy(
                            reportSummary = summary,
                            isLoading = false,
                            error = null
                        )
                    }
                }.onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "요약 내용을 불러오던 중 오류 발생"
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "요약 내용을 불러오던 중 오류 발생"
                    )
                }
            }
        }
    }


    // 스크립트 불러오기
    private fun loadReportScript(reportId: Long) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true, error = null) }

                val result = reportRepository.getReportScript(reportId)
                Log.d(TAG, "loadReportScript: $result")
                result.onSuccess { data ->
                    _state.update { currentState ->
                        currentState.copy(
                            reportScript = data.script,
                            isLoading = false,
                            error = null
                        )
                    }
                }.onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "스크립트 목록을 불러오던 중 오류 발생"
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "스크립트 목록을 불러오던 중 오류 발생"
                    )
                }
            }
        }
    }

    // 원어민 표현
    private fun loadNativeExpression(reportId: Long) {

        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true, error = null) }

                val result = reportRepository.getNativeExpressions(reportId)
                Log.d(TAG, "loadNativeExpression: $result")
                result.onSuccess { data ->
                    _state.update { currentState ->
                        currentState.copy(
                            nativeExpression = data.nativeExpressions,
                            isLoading = false,
                            error = null
                        )
                    }
                }.onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "원어민 표현을 불러오던 중 오류 발생"
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "원어민 표현을 불러오던 중 오류 발생"
                    )
                }
            }
        }

    }

    private fun selectTab(index: Int) {
        _state.update { it.copy(selectedTabIndex = index) }
    }

}