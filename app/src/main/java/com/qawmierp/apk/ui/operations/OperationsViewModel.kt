package com.qawmierp.apk.ui.operations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qawmierp.apk.core.network.ApiResult
import com.qawmierp.apk.core.network.Phase3ApiService
import com.qawmierp.apk.core.network.dto.*
import com.qawmierp.apk.core.network.safeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

data class OperationsUiState(
    val isLoading: Boolean = false,
    val meals: List<BoardingMealDto> = emptyList(),
    val bazar: List<BoardingBazarDto> = emptyList(),
    val books: List<LibraryBookDto> = emptyList(),
    val bookIssues: List<BookIssueDto> = emptyList(),
    val inventory: List<InventoryItemDto> = emptyList(),
    val transport: List<TransportDto> = emptyList(),
    val notices: List<NoticeDto> = emptyList(),
    val smsLogs: List<SmsLogDto> = emptyList(),
    val badges: List<TarbiyahBadgeDto> = emptyList(),
    val childSafety: List<ChildSafetyDto> = emptyList(),
    val biometricDevices: List<BiometricDeviceDto> = emptyList(),
    val auditLogs: List<AuditLogDto> = emptyList(),
    val statusMessage: String? = null
)

@HiltViewModel
class OperationsViewModel @Inject constructor(
    private val api: Phase3ApiService,
    private val json: Json
) : ViewModel() {

    private val _uiState = MutableStateFlow(OperationsUiState())
    val uiState: StateFlow<OperationsUiState> = _uiState.asStateFlow()

    init {
        loadAll()
    }

    fun loadAll() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val meals = (safeApiCall(json) { api.getBoardingMeals() } as? ApiResult.Success)?.data ?: emptyList()
            val bazar = (safeApiCall(json) { api.getBoardingBazar() } as? ApiResult.Success)?.data ?: emptyList()
            val books = (safeApiCall(json) { api.getLibraryBooks() } as? ApiResult.Success)?.data ?: emptyList()
            val issues = (safeApiCall(json) { api.getBookIssues() } as? ApiResult.Success)?.data ?: emptyList()
            val inventory = (safeApiCall(json) { api.getInventory() } as? ApiResult.Success)?.data ?: emptyList()
            val transport = (safeApiCall(json) { api.getTransport() } as? ApiResult.Success)?.data ?: emptyList()
            val notices = (safeApiCall(json) { api.getNotices() } as? ApiResult.Success)?.data ?: emptyList()
            val smsLogs = (safeApiCall(json) { api.getSmsLogs() } as? ApiResult.Success)?.data ?: emptyList()
            val badges = (safeApiCall(json) { api.getTarbiyahBadges() } as? ApiResult.Success)?.data ?: emptyList()
            val safety = (safeApiCall(json) { api.getChildSafety() } as? ApiResult.Success)?.data ?: emptyList()
            val biometric = (safeApiCall(json) { api.getBiometricDevices() } as? ApiResult.Success)?.data ?: emptyList()
            val audit = (safeApiCall(json) { api.getAuditLogs() } as? ApiResult.Success)?.data ?: emptyList()

            _uiState.value = OperationsUiState(
                isLoading = false,
                meals = meals,
                bazar = bazar,
                books = books,
                bookIssues = issues,
                inventory = inventory,
                transport = transport,
                notices = notices,
                smsLogs = smsLogs,
                badges = badges,
                childSafety = safety,
                biometricDevices = biometric,
                auditLogs = audit
            )
        }
    }

    fun publishNotice(title: String, content: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, statusMessage = null)
            when (val res = safeApiCall(json) { api.createNotice(CreateNoticeRequest(title, content)) }) {
                is ApiResult.Success -> {
                    _uiState.value = _uiState.value.copy(statusMessage = "নোটিশ প্রকাশিত হয়েছে।")
                    loadAll()
                }
                is ApiResult.Error -> _uiState.value = _uiState.value.copy(isLoading = false, statusMessage = res.message)
            }
        }
    }
}
