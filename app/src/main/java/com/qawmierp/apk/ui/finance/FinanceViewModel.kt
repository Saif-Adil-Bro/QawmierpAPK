package com.qawmierp.apk.ui.finance

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

data class FinanceUiState(
    val isLoading: Boolean = false,
    val expenses: List<ExpenseDto> = emptyList(),
    val funds: List<FundDto> = emptyList(),
    val donors: List<DonorDto> = emptyList(),
    val donations: List<DonationDto> = emptyList(),
    val fundraising: List<FundraisingDto> = emptyList(),
    val statusMessage: String? = null
)

@HiltViewModel
class FinanceViewModel @Inject constructor(
    private val api: Phase2ApiService,
    private val json: Json
) : ViewModel() {

    private val _uiState = MutableStateFlow(FinanceUiState())
    val uiState: StateFlow<FinanceUiState> = _uiState.asStateFlow()

    init {
        loadAll()
    }

    fun loadAll() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val expenses = (safeApiCall(json) { api.getExpenses() } as? ApiResult.Success)?.data ?: emptyList()
            val funds = (safeApiCall(json) { api.getFunds() } as? ApiResult.Success)?.data ?: emptyList()
            val donors = (safeApiCall(json) { api.getDonors() } as? ApiResult.Success)?.data ?: emptyList()
            val donations = (safeApiCall(json) { api.getDonations() } as? ApiResult.Success)?.data ?: emptyList()
            val fundraising = (safeApiCall(json) { api.getFundraising() } as? ApiResult.Success)?.data ?: emptyList()

            _uiState.value = FinanceUiState(
                isLoading = false,
                expenses = expenses,
                funds = funds,
                donors = donors,
                donations = donations,
                fundraising = fundraising
            )
        }
    }

    fun addExpense(title: String, amount: Double, date: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, statusMessage = null)
            when (val res = safeApiCall(json) { api.createExpense(CreateExpenseRequest(title, "general", amount, date)) }) {
                is ApiResult.Success -> {
                    _uiState.value = _uiState.value.copy(statusMessage = "ব্যয় এন্ট্রি সংরক্ষিত হয়েছে।")
                    loadAll()
                }
                is ApiResult.Error -> _uiState.value = _uiState.value.copy(isLoading = false, statusMessage = res.message)
            }
        }
    }

    fun addDonation(donorName: String, amount: Double, date: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, statusMessage = null)
            when (val res = safeApiCall(json) { api.createDonation(CreateDonationRequest(donorName, amount, date)) }) {
                is ApiResult.Success -> {
                    _uiState.value = _uiState.value.copy(statusMessage = "অনুদান রসিদ সংরক্ষিত হয়েছে।")
                    loadAll()
                }
                is ApiResult.Error -> _uiState.value = _uiState.value.copy(isLoading = false, statusMessage = res.message)
            }
        }
    }
}
