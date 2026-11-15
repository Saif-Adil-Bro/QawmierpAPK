package com.qawmierp.apk.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qawmierp.apk.core.network.ApiResult
import com.qawmierp.apk.core.network.QawmiApiService
import com.qawmierp.apk.core.network.dto.LoginRequest
import com.qawmierp.apk.core.network.dto.RegisterRequest
import com.qawmierp.apk.core.network.dto.UserDto
import com.qawmierp.apk.core.network.safeApiCall
import com.qawmierp.apk.core.storage.SessionManager
import com.qawmierp.apk.ui.navigation.Routes
import com.qawmierp.apk.ui.navigation.resolveHomeRouteForUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val targetRoute: String? = null,
    val showDefaultPasswordWarning: Boolean = false,
    val currentUser: UserDto? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val apiService: QawmiApiService,
    private val sessionManager: SessionManager,
    private val json: Json
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun checkExistingSession() {
        val token = sessionManager.getAccessToken()
        val cachedUser = sessionManager.getUser()
        if (token.isNullOrBlank() || cachedUser == null) {
            _uiState.value = AuthUiState(targetRoute = Routes.LOGIN)
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            when (val result = safeApiCall(json) { apiService.getMe() }) {
                is ApiResult.Success -> {
                    val user = result.data.user
                    sessionManager.saveSession(
                        accessToken = token,
                        refreshToken = sessionManager.getRefreshToken().orEmpty(),
                        user = user,
                        madrasa = result.data.madrasa
                    )
                    _uiState.value = AuthUiState(
                        currentUser = user,
                        targetRoute = resolveHomeRouteForUser(user)
                    )
                }
                is ApiResult.Error -> {
                    // If offline/network error, fallback to cached user role
                    if (result.httpCode == null) {
                        _uiState.value = AuthUiState(
                            currentUser = cachedUser,
                            targetRoute = resolveHomeRouteForUser(cachedUser)
                        )
                    } else {
                        sessionManager.clearSession()
                        _uiState.value = AuthUiState(targetRoute = Routes.LOGIN)
                    }
                }
            }
        }
    }

    fun login(identifier: String, password: String) {
        if (identifier.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "ইমেইল/আইডি এবং পাসওয়ার্ড দিন।")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            when (val result = safeApiCall(json) { apiService.login(LoginRequest(identifier.trim(), password)) }) {
                is ApiResult.Success -> {
                    val auth = result.data
                    sessionManager.saveSession(
                        accessToken = auth.access_token,
                        refreshToken = auth.refresh_token,
                        user = auth.user,
                        madrasa = auth.madrasa
                    )
                    _uiState.value = AuthUiState(
                        currentUser = auth.user,
                        showDefaultPasswordWarning = auth.user.is_default_password,
                        targetRoute = if (auth.user.is_default_password) null else resolveHomeRouteForUser(auth.user)
                    )
                }
                is ApiResult.Error -> {
                    _uiState.value = AuthUiState(error = result.message)
                }
            }
        }
    }

    fun register(
        madrasaName: String,
        fullName: String,
        email: String,
        password: String,
        phone: String,
        address: String
    ) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            val req = RegisterRequest(madrasaName, fullName, email, password, phone, address)
            when (val result = safeApiCall(json) { apiService.register(req) }) {
                is ApiResult.Success -> {
                    val auth = result.data
                    sessionManager.saveSession(
                        accessToken = auth.access_token,
                        refreshToken = auth.refresh_token,
                        user = auth.user,
                        madrasa = auth.madrasa
                    )
                    _uiState.value = AuthUiState(
                        currentUser = auth.user,
                        targetRoute = resolveHomeRouteForUser(auth.user)
                    )
                }
                is ApiResult.Error -> {
                    _uiState.value = AuthUiState(error = result.message)
                }
            }
        }
    }

    fun dismissDefaultPasswordWarning() {
        val user = _uiState.value.currentUser
        _uiState.value = _uiState.value.copy(
            showDefaultPasswordWarning = false,
            targetRoute = resolveHomeRouteForUser(user)
        )
    }

    fun getServerUrl(): String = sessionManager.getBaseUrl()
    fun getApiKey(): String = sessionManager.getApiKey()
    fun saveServerConfig(url: String, apiKey: String) {
        sessionManager.setBaseUrl(url)
        sessionManager.setApiKey(apiKey)
    }
}
