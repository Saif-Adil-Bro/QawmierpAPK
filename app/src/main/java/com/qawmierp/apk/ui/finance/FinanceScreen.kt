package com.qawmierp.apk.ui.finance

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
fun FinanceScreen(
    onBack: () -> Unit,
    viewModel: FinanceViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    var showAddDialog by remember { mutableStateOf(false) }

    val tabs = listOf("ব্যয়", "যাকাত ও ফান্ড", "দাতা ও অনুদান", "মাহফিল ও কালেকশন")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("হিসাব, যাকাত ও অনুদান") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        floatingActionButton = {
            if (selectedTab == 0 || selectedTab == 2) {
                FloatingActionButton(onClick = { showAddDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
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
                    0 -> items(state.expenses) { exp ->
                        OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.padding(12.dp)) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(exp.title, fontWeight = FontWeight.Bold)
                                    Text("${exp.category ?: "সাধারণ"} • ${exp.expense_date ?: ""}", style = MaterialTheme.typography.bodySmall)
                                }
                                Text("৳${exp.amount.toInt()}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                    1 -> items(state.funds) { fund ->
                        OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.padding(12.dp)) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(fund.name, fontWeight = FontWeight.Bold)
                                    Text(fund.fund_type ?: "General", style = MaterialTheme.typography.bodySmall)
                                }
                                Text("৳${fund.balance.toInt()}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                    2 -> {
                        item { Text("সাম্প্রতিক অনুদান (${state.donations.size})", fontWeight = FontWeight.Bold) }
                        items(state.donations) { don ->
                            OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                                Row(modifier = Modifier.padding(12.dp)) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(don.donor_name ?: "দাতা", fontWeight = FontWeight.Bold)
                                        Text("${don.donation_date ?: ""} • ${don.payment_method ?: "cash"}", style = MaterialTheme.typography.bodySmall)
                                    }
                                    Text("৳${don.amount.toInt()}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                        item { Spacer(modifier = Modifier.height(8.dp)); Text("নিবন্ধিত দাতা (${state.donors.size})", fontWeight = FontWeight.Bold) }
                        items(state.donors) { donor ->
                            OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(donor.full_name, fontWeight = FontWeight.Bold)
                                    Text("মোবাইল: ${donor.phone ?: "-"}", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                    3 -> items(state.fundraising) { f ->
                        OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(f.title, fontWeight = FontWeight.Bold)
                                Text("সংগৃহীত: ৳${f.collected_amount.toInt()} / লক্ষ্য: ৳${f.target_amount.toInt()}", style = MaterialTheme.typography.bodySmall)
                                LinearProgressIndicator(
                                    progress = {
                                        if (f.target_amount > 0) (f.collected_amount / f.target_amount).toFloat().coerceIn(0f, 1f) else 0f
                                    },
                                    modifier = Modifier.fillMaxWidth().padding(top = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var titleOrDonor by remember { mutableStateOf("") }
        var amountStr by remember { mutableStateOf("") }
        var date by remember { mutableStateOf("2026-11-15") }
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text(if (selectedTab == 0) "নতুন ব্যয় এন্ট্রি" else "নতুন অনুদান এন্ট্রি") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = titleOrDonor,
                        onValueChange = { titleOrDonor = it },
                        label = { Text(if (selectedTab == 0) "ব্যয়ের বিবরণ" else "দাতার নাম") }
                    )
                    OutlinedTextField(value = amountStr, onValueChange = { amountStr = it }, label = { Text("পরিমাণ (৳)") })
                    OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("তারিখ (YYYY-MM-DD)") })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val amt = amountStr.toDoubleOrNull() ?: 0.0
                    if (selectedTab == 0) viewModel.addExpense(titleOrDonor, amt, date)
                    else viewModel.addDonation(titleOrDonor, amt, date)
                    showAddDialog = false
                }) { Text("সংরক্ষণ করুন") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("বাতিল") }
            }
        )
    }
}
