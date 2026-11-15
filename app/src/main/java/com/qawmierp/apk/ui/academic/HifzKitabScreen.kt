package com.qawmierp.apk.ui.academic

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
import com.qawmierp.apk.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HifzKitabScreen(
    onBack: () -> Unit,
    viewModel: AcademicViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadHifzAndKitab()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.hifz_kitab)) },
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
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (state.isLoading) item { LinearProgressIndicator(modifier = Modifier.fillMaxWidth()) }

            item { Text("হিফজ লগ (${state.hifzLogs.size})", fontWeight = FontWeight.Bold) }
            items(state.hifzLogs) { h ->
                OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("${h.date} — সবক: ${h.sabak ?: "-"}", fontWeight = FontWeight.Medium)
                        Text("সবকী: ${h.sabqi ?: "-"} | মনজিল: ${h.manzil ?: "-"}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            item { Text("কিতাব লগ (${state.kitabLogs.size})", fontWeight = FontWeight.Bold) }
            items(state.kitabLogs) { k ->
                OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("${k.date} — ${k.kitab_name ?: "কিতাব"}", fontWeight = FontWeight.Medium)
                        Text("পৃষ্ঠা: ${k.page_from ?: "-"} থেকে ${k.page_to ?: "-"}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
