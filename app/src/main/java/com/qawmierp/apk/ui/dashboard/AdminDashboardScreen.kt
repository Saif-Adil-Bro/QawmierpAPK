package com.qawmierp.apk.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.qawmierp.apk.R
import com.qawmierp.apk.ui.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onNavigate: (String) -> Unit,
    viewModel: AdminDashboardViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(state.madrasaName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(stringResource(R.string.dashboard_title), style = MaterialTheme.typography.bodySmall)
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigate(Routes.NOTIFICATIONS) }) {
                        Icon(Icons.Default.Notifications, contentDescription = stringResource(R.string.notifications))
                    }
                    IconButton(onClick = { onNavigate(Routes.SETTINGS) }) {
                        Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.settings))
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        viewModel.search(it)
                    },
                    placeholder = { Text("ছাত্র, উস্তাদ, ক্লাস খুঁজুন...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            state.searchResults?.let { results ->
                if (results.students.isNotEmpty() || results.teachers.isNotEmpty()) {
                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("সার্চ ফলাফল", fontWeight = FontWeight.Bold)
                                results.students.take(3).forEach {
                                    Text("ছাত্র: ${it.full_name} (${it.roll_number ?: "-"})", style = MaterialTheme.typography.bodySmall)
                                }
                                results.teachers.take(3).forEach {
                                    Text("উস্তাদ: ${it.full_name}", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }

            if (state.isLoading) {
                item {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }

            state.error?.let { err ->
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                        Text(err, modifier = Modifier.padding(12.dp), color = MaterialTheme.colorScheme.onErrorContainer)
                    }
                }
            }

            state.dashboard?.let { d ->
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        KpiCard("মোট ছাত্র", d.students_count.toString(), Modifier.weight(1f))
                        KpiCard("মোট উস্তাদ", d.teachers_count.toString(), Modifier.weight(1f))
                        KpiCard("ক্লাস", d.classes_count.toString(), Modifier.weight(1f))
                    }
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        KpiCard("আজ উপস্থিত", d.today_present.toString(), Modifier.weight(1f))
                        KpiCard("আজ অনুপস্থিত", d.today_absent.toString(), Modifier.weight(1f))
                    }
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        KpiCard("আজকের আয়", "৳${d.today_income.toInt()}", Modifier.weight(1f))
                        KpiCard("আজকের ব্যয়", "৳${d.today_expense.toInt()}", Modifier.weight(1f))
                        KpiCard("নিট ব্যালেন্স", "৳${d.net_balance.toInt()}", Modifier.weight(1f))
                    }
                }
                item {
                    AttendanceDonutChart(present = d.today_present, absent = d.today_absent)
                }
                if (d.monthly_income_expense.isNotEmpty()) {
                    item {
                        MonthlyIncomeExpenseChart(items = d.monthly_income_expense)
                    }
                }
                if (d.exam_pass_rate.isNotEmpty()) {
                    item {
                        ExamPassRateSection(rates = d.exam_pass_rate)
                    }
                }
            }

            item {
                Text("দ্রুত মডিউলসমূহ", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ActionTile(stringResource(R.string.students), Icons.Default.People, Modifier.weight(1f)) {
                        onNavigate(Routes.STUDENTS)
                    }
                    ActionTile(stringResource(R.string.attendance), Icons.Default.FactCheck, Modifier.weight(1f)) {
                        onNavigate(Routes.ATTENDANCE)
                    }
                }
            }
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ActionTile(stringResource(R.string.hifz_kitab), Icons.Default.MenuBook, Modifier.weight(1f)) {
                        onNavigate(Routes.HIFZ_KITAB)
                    }
                    ActionTile(stringResource(R.string.fees), Icons.Default.Payments, Modifier.weight(1f)) {
                        onNavigate(Routes.FEES)
                    }
                }
            }
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ActionTile("পরীক্ষা ও ছুটি", Icons.Default.Assignment, Modifier.weight(1f)) {
                        onNavigate(Routes.EXAMS)
                    }
                    ActionTile("হিসাব ও যাকাত", Icons.Default.AccountBalance, Modifier.weight(1f)) {
                        onNavigate(Routes.FINANCE)
                    }
                }
            }
            item {
                ActionTile("বোর্ডিং, লাইব্রেরি ও সিকিউরিটি", Icons.Default.AdminPanelSettings, Modifier.fillMaxWidth()) {
                    onNavigate(Routes.OPERATIONS)
                }
            }
        }
    }
}

@Composable
private fun KpiCard(title: String, value: String, modifier: Modifier = Modifier) {
    ElevatedCard(modifier = modifier) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun ActionTile(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedCard(onClick = onClick, modifier = modifier) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary)
            Text(title, fontWeight = FontWeight.Medium)
        }
    }
}
