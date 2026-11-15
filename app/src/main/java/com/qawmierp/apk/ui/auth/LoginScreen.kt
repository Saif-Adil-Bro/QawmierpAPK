package com.qawmierp.apk.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.qawmierp.apk.R

@Composable
fun LoginScreen(
    onNavigateToRoute: (String) -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var identifier by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showServerDialog by remember { mutableStateOf(false) }

    LaunchedEffect(state.targetRoute) {
        state.targetRoute?.let { onNavigateToRoute(it) }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { showServerDialog = true }) {
                    Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.server_settings))
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.login_title),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = identifier,
                onValueChange = { identifier = it },
                label = { Text(stringResource(R.string.identifier_hint)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(R.string.password_hint)) },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            state.error?.let { err ->
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = err, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = { viewModel.login(identifier, password) },
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                } else {
                    Text(stringResource(R.string.login_button))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            TextButton(onClick = onNavigateToRegister) {
                Text(stringResource(R.string.register_link))
            }
        }
    }

    if (showServerDialog) {
        var baseUrl by remember { mutableStateOf(viewModel.getServerUrl()) }
        var apiKey by remember { mutableStateOf(viewModel.getApiKey()) }
        AlertDialog(
            onDismissRequest = { showServerDialog = false },
            title = { Text(stringResource(R.string.server_settings)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = baseUrl,
                        onValueChange = { baseUrl = it },
                        label = { Text("Base URL (https://.../api/mobile/)") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = apiKey,
                        onValueChange = { apiKey = it },
                        label = { Text("x-api-key") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.saveServerConfig(baseUrl, apiKey)
                    showServerDialog = false
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showServerDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (state.showDefaultPasswordWarning) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissDefaultPasswordWarning() },
            title = { Text("ডিফল্ট পাসওয়ার্ড সতর্কতা") },
            text = { Text("আপনি ডিফল্ট পাসওয়ার্ড দিয়ে লগইন করেছেন। নিরাপত্তার জন্য অনুগ্রহ করে সেটিংস থেকে পাসওয়ার্ড পরিবর্তন করুন।") },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissDefaultPasswordWarning() }) { Text("ঠিক আছে") }
            }
        )
    }
}
