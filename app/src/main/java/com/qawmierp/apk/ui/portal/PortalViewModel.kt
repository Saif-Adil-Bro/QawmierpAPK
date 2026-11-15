package com.qawmierp.apk.ui.portal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qawmierp.apk.core.network.ApiResult
import com.qawmierp.apk.core.network.QawmiApiService
import com.qawmierp.apk.core.network.dto.StudentPortalDto
import com.qawmierp.apk.core.network.dto.TeacherPortalDto
import com.qawmierp.apk.core.network.safeApiCall
import com.qawmierp.apk.core.storage.PortalCacheDao
import com.qawmierp.apk.core.storage.PortalCacheEntity
import com.qawmierp.apk.core.storage.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

data class StudentPortalUiState(
    val isLoading: Boolean = true,
    val data: StudentPortalDto? = null,
    val isOfflineCache: Boolean = false,
    val error: String? = null,
    val userFullName: String = "অভিভাবক"
)

data class TeacherPortalUiState(
    val isLoading: Boolean = true,
    val data: TeacherPortalDto? = null,
    val isOfflineCache: Boolean = false,
    val error: String? = null,
    val userFullName: String = "মুহতারাম উস্তাদ"
)

@HiltViewModel
class PortalViewModel @Inject constructor(
    private val apiService: QawmiApiService,
    private val portalCacheDao: PortalCacheDao,
    private val sessionManager: SessionManager,
    private val json: Json
) : ViewModel() {

    private val initialUserName: String =
        sessionManager.getUser()?.full_name?.takeIf { it.isNotBlank() } ?: ""

    private val _studentState = MutableStateFlow(
        StudentPortalUiState(userFullName = initialUserName.ifBlank { "অভিভাবক" })
    )
    val studentState: StateFlow<StudentPortalUiState> = _studentState.asStateFlow()

    private val _teacherState = MutableStateFlow(
        TeacherPortalUiState(userFullName = initialUserName.ifBlank { "মুহতারাম উস্তাদ" })
    )
    val teacherState: StateFlow<TeacherPortalUiState> = _teacherState.asStateFlow()

    fun loadStudentPortal(studentId: String? = null) {
        viewModelScope.launch {
            _studentState.value = _studentState.value.copy(isLoading = true, error = null)
            val cacheKey = "student_portal_${studentId ?: "default"}"
            when (val result = safeApiCall(json) { apiService.getStudentPortal(studentId) }) {
                is ApiResult.Success -> {
                    portalCacheDao.upsert(PortalCacheEntity(cacheKey, json.encodeToString(result.data)))
                    _studentState.value = _studentState.value.copy(
                        isLoading = false,
                        data = result.data,
                        isOfflineCache = false,
                        error = null
                    )
                }
                is ApiResult.Error -> {
                    val cached = portalCacheDao.get(cacheKey)?.let {
                        runCatching { json.decodeFromString<StudentPortalDto>(it.payloadJson) }.getOrNull()
                    }
                    _studentState.value = _studentState.value.copy(
                        isLoading = false,
                        data = cached,
                        isOfflineCache = cached != null,
                        error = if (cached == null) result.message else null
                    )
                }
            }
        }
    }

    fun loadTeacherPortal() {
        viewModelScope.launch {
            _teacherState.value = _teacherState.value.copy(isLoading = true, error = null)
            val cacheKey = "teacher_portal"
            when (val result = safeApiCall(json) { apiService.getTeacherPortal() }) {
                is ApiResult.Success -> {
                    portalCacheDao.upsert(PortalCacheEntity(cacheKey, json.encodeToString(result.data)))
                    _teacherState.value = _teacherState.value.copy(
                        isLoading = false,
                        data = result.data,
                        isOfflineCache = false,
                        error = null
                    )
                }
                is ApiResult.Error -> {
                    val cached = portalCacheDao.get(cacheKey)?.let {
                        runCatching { json.decodeFromString<TeacherPortalDto>(it.payloadJson) }.getOrNull()
                    }
                    _teacherState.value = _teacherState.value.copy(
                        isLoading = false,
                        data = cached,
                        isOfflineCache = cached != null,
                        error = if (cached == null) result.message else null
                    )
                }
            }
        }
    }
}
