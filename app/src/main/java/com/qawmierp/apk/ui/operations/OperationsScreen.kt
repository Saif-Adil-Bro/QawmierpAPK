package com.qawmierp.apk.ui.operations

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
fun OperationsScreen(
    onBack: () -> Unit,
    viewModel: OperationsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    var showNoticeDialog by remember { mutableStateOf(false) }

    val tabs = listOf("বোর্ডিং", "লাইব্রেরি", "ইনভেন্টরি ও গাড়ি", "নোটিশ ও এসএমএস", "তারবিয়াহ ও নিরাপত্তা")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("অপারেশন ও সিকিউরিটি") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        floatingActionButton = {
            if (selectedTab == 3) {
                FloatingActionButton(onClick = { showNoticeDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Publish Notice")
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
                    0 -> {
                        item { Text("মিল হিসাব (${state.meals.size})", fontWeight = FontWeight.Bold) }
                        items(state.meals) { meal ->
                            OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                                Row(modifier = Modifier.padding(12.dp)) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("${meal.date} • ${meal.meal_type ?: "মিল"}", fontWeight = FontWeight.Bold)
                                        Text("ছাত্র: ${meal.total_students} জন", style = MaterialTheme.typography.bodySmall)
                                    }
                                    Text("৳${meal.cost.toInt()}", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        item { Spacer(modifier = Modifier.height(8.dp)); Text("বাজার খরচ (${state.bazar.size})", fontWeight = FontWeight.Bold) }
                        items(state.bazar) { b ->
                            OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                                Row(modifier = Modifier.padding(12.dp)) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(b.item_name, fontWeight = FontWeight.Bold)
                                        Text("${b.date} • ${b.quantity ?: ""}", style = MaterialTheme.typography.bodySmall)
                                    }
                                    Text("৳${b.total_price.toInt()}", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                    1 -> {
                        item { Text("কিতাবখানা (${state.books.size})", fontWeight = FontWeight.Bold) }
                        items(state.books) { book ->
                            OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(book.title, fontWeight = FontWeight.Bold)
                                    Text("লেখক: ${book.author ?: "-"} • পাওয়া যাচ্ছে: ${book.available_copies}/${book.total_copies}", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                        item { Spacer(modifier = Modifier.height(8.dp)); Text("ইস্যুকৃত বই (${state.bookIssues.size})", fontWeight = FontWeight.Bold) }
                        items(state.bookIssues) { issue ->
                            OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(issue.book_title ?: "বই", fontWeight = FontWeight.Bold)
                                    Text("ছাত্র: ${issue.student_name ?: "-"} • স্ট্যাটাস: ${issue.status}", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                    2 -> {
                        item { Text("ইনভেন্টরি (${state.inventory.size})", fontWeight = FontWeight.Bold) }
                        items(state.inventory) { item ->
                            OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                                Row(modifier = Modifier.padding(12.dp)) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(item.item_name, fontWeight = FontWeight.Bold)
                                        Text(item.category ?: "সাধারণ", style = MaterialTheme.typography.bodySmall)
                                    }
                                    Text("${item.quantity} ${item.unit ?: ""}", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        item { Spacer(modifier = Modifier.height(8.dp)); Text("পরিবহন (${state.transport.size})", fontWeight = FontWeight.Bold) }
                        items(state.transport) { t ->
                            OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(t.vehicle_name, fontWeight = FontWeight.Bold)
                                    Text("রুট: ${t.route_name ?: "-"} • ড্রাইভার: ${t.driver_name ?: "-"} (${t.driver_phone ?: "-"})", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                    3 -> {
                        item { Text("নোটিশ বোর্ড (${state.notices.size})", fontWeight = FontWeight.Bold) }
                        items(state.notices) { n ->
                            OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(n.title, fontWeight = FontWeight.Bold)
                                    n.content?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                                }
                            }
                        }
                        item { Spacer(modifier = Modifier.height(8.dp)); Text("SMS লগ (${state.smsLogs.size})", fontWeight = FontWeight.Bold) }
                        items(state.smsLogs) { sms ->
                            OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("${sms.recipient} • ${sms.status}", fontWeight = FontWeight.Bold)
                                    Text(sms.message, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                    4 -> {
                        item { Text("তারবিয়াহ ব্যাজ (${state.badges.size})", fontWeight = FontWeight.Bold) }
                        items(state.badges) { badge ->
                            OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                                Row(modifier = Modifier.padding(12.dp)) {
                                    Text(badge.title, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                                    Text("${badge.points} pts", color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                        item { Spacer(modifier = Modifier.height(8.dp)); Text("বায়োমেট্রিক ডিভাইস (${state.biometricDevices.size})", fontWeight = FontWeight.Bold) }
                        items(state.biometricDevices) { dev ->
                            OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                                Row(modifier = Modifier.padding(12.dp)) {
                                    Text(dev.device_name, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                                    Text(dev.status.uppercase(), color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                        item { Spacer(modifier = Modifier.height(8.dp)); Text("সিকিউরিটি অডিট লগ (${state.auditLogs.size})", fontWeight = FontWeight.Bold) }
                        items(state.auditLogs) { log ->
                            OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("${log.action} (${log.entity ?: "-"})", fontWeight = FontWeight.Bold)
                                    Text("${log.actor_name ?: "System"} • ${log.created_at ?: ""}", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showNoticeDialog) {
        var title by remember { mutableStateOf("") }
        var content by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showNoticeDialog = false },
            title = { Text("নতুন নোটিশ প্রকাশ") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("শিরোনাম") })
                    OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("বিস্তারিত") })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.publishNotice(title, content)
                    showNoticeDialog = false
                }) { Text("প্রকাশ করুন") }
            },
            dismissButton = {
                TextButton(onClick = { showNoticeDialog = false }) { Text("বাতিল") }
            }
        )
    }
}
