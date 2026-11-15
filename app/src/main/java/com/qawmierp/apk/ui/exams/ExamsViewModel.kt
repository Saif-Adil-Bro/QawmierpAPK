package com.qawmierp.apk.ui.exams

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qawmierp.apk.core.network.ApiResult
import com.qawmierp.apk.core.network.Phase2ApiService
import com.qawmierp.apk.core.network.dto.*
import com.qawmierp.apk.core.network.safeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

data class ExamsUiState(
    val isLoading: Boolean = false,
    val exams: List<ExamDto> = emptyList(),
    val results: List<ExamResultDto> = emptyList(),
    val routines: List<ExamRoutineDto> = emptyList(),
    val leaves: List<LeaveDto> = emptyList(),
    val holidays: List<HolidayDto> = emptyList(),
    val admissions: List<AdmissionDto> = emptyList(),
    val statusMessage: String? = null
)

@HiltViewModel
class ExamsViewModel @Inject constructor(
    private val api: Phase2ApiService,
    private val json: Json
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExamsUiState())
    val uiState: StateFlow<ExamsUiState> = _uiState.asStateFlow()

    init {
        loadAll()
    }

    fun loadAll() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val exams = (safeApiCall(json) { api.getExams() } as? ApiResult.Success)?.data ?: emptyList()
            val results = (safeApiCall(json) { api.getExamResults() } as? ApiResult.Success)?.data ?: emptyList()
            val routines = (safeApiCall(json) { api.getExamRoutines() } as? ApiResult.Success)?.data ?: emptyList()
            val leaves = (safeApiCall(json) { api.getLeaves() } as? ApiResult.Success)?.data ?: emptyList()
            val holidays = (safeApiCall(json) { api.getHolidays() } as? ApiResult.Success)?.data ?: emptyList()
            val admissions = (safeApiCall(json) { api.getAdmissions() } as? ApiResult.Success)?.data ?: emptyList()

            _uiState.value = ExamsUiState(
                isLoading = false,
                exams = exams,
                results = results,
                routines = routines,
                leaves = leaves,
                holidays = holidays,
                admissions = admissions
            )
        }
    }

    fun applyLeave(reason: String, startDate: String, endDate: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, statusMessage = null)
            when (val res = safeApiCall(json) { api.createLeave(CreateLeaveRequest(reason, startDate, endDate)) }) {
                is ApiResult.Success -> {
                    _uiState.value = _uiState.value.copy(statusMessage = "ছুটির আবেদন জমা হয়েছে।")
                    loadAll()
                }
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, statusMessage = res.message)
                }
            }
        }
    }
}
