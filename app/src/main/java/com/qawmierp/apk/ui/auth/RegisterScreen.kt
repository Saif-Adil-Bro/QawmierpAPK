package com.qawmierp.apk.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateToRoute: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var madrasaName by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(state.targetRoute) {
        state.targetRoute?.let { onNavigateToRoute(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("নতুন মাদ্রাসা নিবন্ধন") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Back") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(value = madrasaName, onValueChange = { madrasaName = it }, label = { Text("মাদ্রাসার নাম") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = fullName, onValueChange = { fullName = it }, label = { Text("মুহতামিম / অ্যাডমিনের নাম") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("ইমেইল") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("মোবাইল নম্বর") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("ঠিকানা") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("পাসওয়ার্ড") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            state.error?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Button(
                onClick = { viewModel.register(madrasaName, fullName, email, password, phone, address) },
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                if (state.isLoading) CircularProgressIndicator(modifier = Modifier.size(22.dp))
                else Text("নিবন্ধন সম্পন্ন করুন")
            }
        }
    }
}
