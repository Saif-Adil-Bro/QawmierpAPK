package com.qawmierp.apk.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qawmierp.apk.core.network.ApiResult
import com.qawmierp.apk.core.network.QawmiApiService
import com.qawmierp.apk.core.network.dto.DashboardDto
import com.qawmierp.apk.core.network.dto.UniversalSearchDto
import com.qawmierp.apk.core.network.safeApiCall
import com.qawmierp.apk.core.storage.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

data class DashboardUiState(
    val isLoading: Boolean = true,
    val dashboard: DashboardDto? = null,
    val searchResults: UniversalSearchDto? = null,
    val error: String? = null,
    val madrasaName: String = "আপনার মাদরাসা",
    val userFullName: String = "মুহতামিম",
    val userRole: String = "super_admin"
)

@HiltViewModel
class AdminDashboardViewModel @Inject constructor(
    private val apiService: QawmiApiService,
    private val sessionManager: SessionManager,
    private val json: Json
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        DashboardUiState(
            madrasaName = sessionManager.getMadrasa()?.name ?: "আপনার মাদরাসা",
            userFullName = sessionManager.getUser()?.full_name?.takeIf { it.isNotBlank() } ?: "মুহতামিম",
            userRole = sessionManager.getUser()?.role ?: "super_admin"
        )
    )
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = safeApiCall(json) { apiService.getDashboard() }) {
                is ApiResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        dashboard = result.data
                    )
                }
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }

    fun search(query: String) {
        if (query.isBlank()) {
            _uiState.value = _uiState.value.copy(searchResults = null)
            return
        }
        viewModelScope.launch {
            when (val result = safeApiCall(json) { apiService.search(query.trim()) }) {
                is ApiResult.Success -> _uiState.value = _uiState.value.copy(searchResults = result.data)
                is ApiResult.Error -> Unit
            }
        }
    }
}
