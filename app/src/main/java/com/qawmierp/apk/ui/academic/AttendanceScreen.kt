package com.qawmierp.apk.ui.academic

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.qawmierp.apk.R
import com.qawmierp.apk.core.network.dto.BulkAttendanceRecordDto
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    onBack: () -> Unit,
    viewModel: AcademicViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val today = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()) }
    val statusMap = remember { mutableStateMapOf<String, String>() }

    LaunchedEffect(Unit) {
        viewModel.loadStudents()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${stringResource(R.string.attendance)} ($today)") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        bottomBar = {
            Surface(tonalElevation = 3.dp) {
                Button(
                    onClick = {
                        val records = state.students.map { s ->
                            BulkAttendanceRecordDto(
                                student_id = s.id,
                                status = statusMap[s.id] ?: "present"
                            )
                        }
                        viewModel.submitBulkAttendance(today, records)
                    },
                    enabled = state.students.isNotEmpty() && !state.isLoading,
                    modifier = Modifier.fillMaxWidth().padding(16.dp).height(48.dp)
                ) {
                    Text("হাজিরা জমা দিন (${state.students.size} জন)")
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
            state.statusMessage?.let { msg ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Text(msg, modifier = Modifier.padding(12.dp))
                }
            }

            if (state.isLoading) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.students) { student ->
                    val current = statusMap[student.id] ?: "present"
                    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(student.full_name, fontWeight = FontWeight.Bold)
                                Text("রোল: ${student.roll_number ?: "-"}", style = MaterialTheme.typography.bodySmall)
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                FilterChip(
                                    selected = current == "present",
                                    onClick = { statusMap[student.id] = "present" },
                                    label = { Text("উপস্থিত") }
                                )
                                FilterChip(
                                    selected = current == "absent",
                                    onClick = { statusMap[student.id] = "absent" },
                                    label = { Text("অনুপস্থিত") }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
