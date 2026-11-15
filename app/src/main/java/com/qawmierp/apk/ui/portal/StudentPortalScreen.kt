package com.qawmierp.apk.ui.portal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
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
fun StudentPortalScreen(
    onNavigate: (String) -> Unit,
    viewModel: PortalViewModel = hiltViewModel()
) {
    val state by viewModel.studentState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadStudentPortal()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.student_portal_title)) },
                actions = {
                    IconButton(onClick = { onNavigate(Routes.NOTIFICATIONS) }) {
                        Icon(Icons.Default.Notifications, contentDescription = null)
                    }
                    IconButton(onClick = { onNavigate(Routes.SETTINGS) }) {
                        Icon(Icons.Default.Settings, contentDescription = null)
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
            if (state.isOfflineCache) {
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                        Text("অফলাইন ক্যাশ থেকে তথ্য দেখানো হচ্ছে", modifier = Modifier.padding(10.dp))
                    }
                }
            }
            if (state.isLoading) {
                item { LinearProgressIndicator(modifier = Modifier.fillMaxWidth()) }
            }
            state.error?.let { err ->
                item { Text(err, color = MaterialTheme.colorScheme.error) }
            }

            state.data?.let { portal ->
                if (portal.students.size > 1) {
                    item {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(portal.students) { child ->
                                FilterChip(
                                    selected = child.id == portal.active_student?.id,
                                    onClick = { viewModel.loadStudentPortal(child.id) },
                                    label = { Text(child.full_name) }
                                )
                            }
                        }
                    }
                }

                portal.active_student?.let { student ->
                    item {
                        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(student.full_name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                Text("রোল: ${student.roll_number ?: "-"} | আইডি: ${student.student_id_code ?: "-"}")
                                Text("ক্লাস: ${student.classes?.name ?: "-"}")
                            }
                        }
                    }
                }

                item { Text("সাম্প্রতিক হাজিরা (${portal.attendance.size})", fontWeight = FontWeight.SemiBold) }
                items(portal.attendance.take(5)) { att ->
                    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(att.date)
                            Spacer(modifier = Modifier.weight(1f))
                            Text(att.status.uppercase(), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                item { Text("হিফজ লগ (${portal.hifz_logs.size})", fontWeight = FontWeight.SemiBold) }
                items(portal.hifz_logs.take(5)) { hifz ->
                    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("${hifz.date} — সবক: ${hifz.sabak ?: "-"}", fontWeight = FontWeight.Medium)
                            Text("সবকী: ${hifz.sabqi ?: "-"} | মনজিল: ${hifz.manzil ?: "-"}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                item { Text("বকেয়া ও ফি (${portal.fees.size})", fontWeight = FontWeight.SemiBold) }
                items(portal.fees.take(5)) { fee ->
                    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(12.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(fee.title ?: "মাসিক ফি", fontWeight = FontWeight.Medium)
                                Text("Status: ${fee.status}", style = MaterialTheme.typography.bodySmall)
                            }
                            Text("৳${fee.amount.toInt()}", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                item { Text("নোটিশ (${portal.notices.size})", fontWeight = FontWeight.SemiBold) }
                items(portal.notices.take(5)) { notice ->
                    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(notice.title, fontWeight = FontWeight.Bold)
                            notice.content?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                        }
                    }
                }
            }
        }
    }
}
