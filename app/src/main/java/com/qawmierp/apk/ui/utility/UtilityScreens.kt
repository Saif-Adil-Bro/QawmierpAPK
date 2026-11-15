package com.qawmierp.apk.ui.utility

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qawmierp.apk.R
import com.qawmierp.apk.core.network.ApiResult
import com.qawmierp.apk.core.network.QawmiApiService
import com.qawmierp.apk.core.network.dto.MarkNotificationRequest
import com.qawmierp.apk.core.network.dto.NotificationDto
import com.qawmierp.apk.core.network.dto.StudentFeeDto
import com.qawmierp.apk.core.network.safeApiCall
import com.qawmierp.apk.core.storage.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class UtilityViewModel @Inject constructor(
    private val apiService: QawmiApiService,
    val sessionManager: SessionManager,
    private val json: Json
) : ViewModel() {

    private val _fees = MutableStateFlow<List<StudentFeeDto>>(emptyList())
    val fees: StateFlow<List<StudentFeeDto>> = _fees.asStateFlow()

    private val _notifications = MutableStateFlow<List<NotificationDto>>(emptyList())
    val notifications: StateFlow<List<NotificationDto>> = _notifications.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadFees() {
        viewModelScope.launch {
            _isLoading.value = true
            when (val res = safeApiCall(json) { apiService.getStudentFees() }) {
                is ApiResult.Success -> _fees.value = res.data
                is ApiResult.Error -> Unit
            }
            _isLoading.value = false
        }
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _isLoading.value = true
            when (val res = safeApiCall(json) { apiService.getNotifications() }) {
                is ApiResult.Success -> _notifications.value = res.data
                is ApiResult.Error -> Unit
            }
            _isLoading.value = false
        }
    }

    fun markAllRead() {
        viewModelScope.launch {
            safeApiCall(json) { apiService.markNotifications(MarkNotificationRequest(mark_all = true)) }
            loadNotifications()
        }
    }

    fun logout(onDone: () -> Unit) {
        viewModelScope.launch {
            runCatching { apiService.logout() }
            sessionManager.clearSession()
            onDone()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeesScreen(
    onBack: () -> Unit,
    viewModel: UtilityViewModel = hiltViewModel()
) {
    val fees by viewModel.fees.collectAsState()
    val loading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadFees() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.fees)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (loading) item { LinearProgressIndicator(modifier = Modifier.fillMaxWidth()) }
            items(fees) { fee ->
                OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(14.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(fee.title ?: "মাসিক ফি", fontWeight = FontWeight.Bold)
                            Text("স্ট্যাটাস: ${fee.status}", style = MaterialTheme.typography.bodySmall)
                        }
                        Text("৳${fee.amount.toInt()}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onBack: () -> Unit,
    viewModel: UtilityViewModel = hiltViewModel()
) {
    val items by viewModel.notifications.collectAsState()
    val loading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadNotifications() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.notifications)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    TextButton(onClick = { viewModel.markAllRead() }) { Text("সব পঠিত") }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (loading) item { LinearProgressIndicator(modifier = Modifier.fillMaxWidth()) }
            items(items) { n ->
                OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(n.title, fontWeight = if (n.is_read) FontWeight.Normal else FontWeight.Bold)
                        Text(n.message, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onLoggedOut: () -> Unit,
    viewModel: UtilityViewModel = hiltViewModel()
) {
    val session = viewModel.sessionManager
    val currentLang by session.languageFlow.collectAsState()
    var baseUrl by remember { mutableStateOf(session.getBaseUrl()) }
    var apiKey by remember { mutableStateOf(session.getApiKey()) }
    var savedBanner by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("${stringResource(R.string.language)}:", fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = currentLang == "bn",
                    onClick = { session.setLanguage("bn") },
                    label = { Text("বাংলা (ডিফল্ট)") }
                )
                FilterChip(
                    selected = currentLang == "en",
                    onClick = { session.setLanguage("en") },
                    label = { Text("English") }
                )
            }

            HorizontalDivider()
            Text(stringResource(R.string.server_settings), fontWeight = FontWeight.SemiBold)
            OutlinedTextField(
                value = baseUrl,
                onValueChange = { baseUrl = it },
                label = { Text("Base URL") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = apiKey,
                onValueChange = { apiKey = it },
                label = { Text("x-api-key") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    session.setBaseUrl(baseUrl)
                    session.setApiKey(apiKey)
                    savedBanner = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("সার্ভার কনফিগারেশন সেভ করুন")
            }
            if (savedBanner) {
                Text("সংরক্ষিত হয়েছে!", color = MaterialTheme.colorScheme.primary)
            }

            Spacer(modifier = Modifier.weight(1f))
            OutlinedButton(
                onClick = { viewModel.logout(onLoggedOut) },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.logout))
            }
        }
    }
}
