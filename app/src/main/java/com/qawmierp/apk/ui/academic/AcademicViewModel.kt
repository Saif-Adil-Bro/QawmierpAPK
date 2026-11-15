package com.qawmierp.apk.ui.academic

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.qawmierp.apk.core.network.ApiResult
import com.qawmierp.apk.core.network.QawmiApiService
import com.qawmierp.apk.core.network.dto.*
import com.qawmierp.apk.core.network.safeApiCall
import com.qawmierp.apk.core.storage.AttendanceSyncWorker
import com.qawmierp.apk.core.storage.PendingAttendanceDao
import com.qawmierp.apk.core.storage.PendingAttendanceEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

data class AcademicUiState(
    val isLoading: Boolean = false,
    val students: List<StudentDto> = emptyList(),
    val hifzLogs: List<HifzLogDto> = emptyList(),
    val kitabLogs: List<KitabLogDto> = emptyList(),
    val statusMessage: String? = null,
    val error: String? = null
)

@HiltViewModel
class AcademicViewModel @Inject constructor(
    private val apiService: QawmiApiService,
    private val pendingAttendanceDao: PendingAttendanceDao,
    @ApplicationContext private val appContext: Context,
    private val json: Json
) : ViewModel() {

    private val _uiState = MutableStateFlow(AcademicUiState())
    val uiState: StateFlow<AcademicUiState> = _uiState.asStateFlow()

    fun loadStudents(search: String? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val res = safeApiCall(json) { apiService.getStudents(search = search) }) {
                is ApiResult.Success -> _uiState.value = _uiState.value.copy(isLoading = false, students = res.data)
                is ApiResult.Error -> _uiState.value = _uiState.value.copy(isLoading = false, error = res.message)
            }
        }
    }

    fun submitBulkAttendance(date: String, records: List<BulkAttendanceRecordDto>) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, statusMessage = null, error = null)
            val req = BulkAttendanceRequest(date, records)
            when (val res = safeApiCall(json) { apiService.submitBulkAttendance(req) }) {
                is ApiResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        statusMessage = "হাজিরা সফলভাবে সার্ভারে সংরক্ষিত হয়েছে।"
                    )
                }
                is ApiResult.Error -> {
                    // Save offline to Room and schedule WorkManager sync
                    pendingAttendanceDao.insert(
                        PendingAttendanceEntity(date = date, payloadJson = json.encodeToString(req))
                    )
                    val workReq = OneTimeWorkRequestBuilder<AttendanceSyncWorker>()
                        .setConstraints(
                            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                        )
                        .build()
                    WorkManager.getInstance(appContext).enqueueUniqueWork(
                        "sync_attendance",
                        ExistingWorkPolicy.APPEND_OR_REPLACE,
                        workReq
                    )
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        statusMessage = "অফলাইনে সংরক্ষিত হয়েছে। ইন্টারনেট পেলে স্বয়ংক্রিয়ভাবে সিঙ্ক হবে।"
                    )
                }
            }
        }
    }

    fun loadHifzAndKitab() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val hifz = safeApiCall(json) { apiService.getHifzLogs() }
            val kitab = safeApiCall(json) { apiService.getKitabLogs() }
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                hifzLogs = (hifz as? ApiResult.Success)?.data ?: emptyList(),
                kitabLogs = (kitab as? ApiResult.Success)?.data ?: emptyList()
            )
        }
    }
}
