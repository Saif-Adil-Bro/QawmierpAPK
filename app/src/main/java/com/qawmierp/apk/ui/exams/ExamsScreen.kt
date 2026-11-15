package com.qawmierp.apk.ui.exams

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamsScreen(
    onBack: () -> Unit,
    viewModel: ExamsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    var showLeaveDialog by remember { mutableStateOf(false) }

    val tabs = listOf("পরীক্ষা", "ফলাফল", "রুটিন", "ছুটি ও ভর্তি")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("পরীক্ষা, ছুটি ও ভর্তি") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        floatingActionButton = {
            if (selectedTab == 3) {
                FloatingActionButton(onClick = { showLeaveDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Apply Leave")
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            ScrollableTabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(selected = selectedTab == index, onClick = { selectedTab = index }, text = { Text(title) })
                }
            }

            if (state.isLoading) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())

            state.statusMessage?.let { msg ->
                Card(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                    Text(msg, modifier = Modifier.padding(12.dp))
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                when (selectedTab) {
                    0 -> items(state.exams) { exam ->
                        OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(exam.name, fontWeight = FontWeight.Bold)
                                Text("${exam.start_date ?: "-"} থেকে ${exam.end_date ?: "-"} • ${exam.status ?: ""}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                    1 -> items(state.results) { res ->
                        OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.padding(12.dp)) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(res.student_name ?: res.student_id ?: "ছাত্র", fontWeight = FontWeight.Bold)
                                    Text(res.subject_name ?: "বিষয়", style = MaterialTheme.typography.bodySmall)
                                }
                                Text("${res.marks_obtained}/${res.total_marks} (${res.grade ?: "-"})", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                    2 -> items(state.routines) { r ->
                        OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("${r.class_name ?: "-"} • ${r.subject_name ?: "-"}", fontWeight = FontWeight.Bold)
                                Text("${r.exam_date ?: ""} (${r.start_time ?: ""} - ${r.end_time ?: ""})", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                    3 -> {
                        item { Text("ছুটির আবেদন (${state.leaves.size})", fontWeight = FontWeight.Bold) }
                        items(state.leaves) { leave ->
                            OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(leave.applicant_name ?: "আবেদনকারী", fontWeight = FontWeight.Bold)
                                    Text("${leave.reason ?: ""} (${leave.start_date ?: ""} - ${leave.end_date ?: ""}) • ${leave.status}", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                        item { Spacer(modifier = Modifier.height(8.dp)); Text("নতুন ভর্তি আবেদন (${state.admissions.size})", fontWeight = FontWeight.Bold) }
                        items(state.admissions) { adm ->
                            OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(adm.applicant_name, fontWeight = FontWeight.Bold)
                                    Text("ক্লাস: ${adm.applied_class ?: "-"} • স্ট্যাটাস: ${adm.status}", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showLeaveDialog) {
        var reason by remember { mutableStateOf("") }
        var startDate by remember { mutableStateOf("2026-11-15") }
        var endDate by remember { mutableStateOf("2026-11-16") }
        AlertDialog(
            onDismissRequest = { showLeaveDialog = false },
            title = { Text("ছুটির আবেদন") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = reason, onValueChange = { reason = it }, label = { Text("কারণ") })
                    OutlinedTextField(value = startDate, onValueChange = { startDate = it }, label = { Text("শুরুর তারিখ (YYYY-MM-DD)") })
                    OutlinedTextField(value = endDate, onValueChange = { endDate = it }, label = { Text("শেষ তারিখ (YYYY-MM-DD)") })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.applyLeave(reason, startDate, endDate)
                    showLeaveDialog = false
                }) { Text("জমা দিন") }
            },
            dismissButton = {
                TextButton(onClick = { showLeaveDialog = false }) { Text("বাতিল") }
            }
        )
    }
}
