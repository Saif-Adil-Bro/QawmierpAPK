package com.qawmierp.apk.ui.portal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.MenuBook
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
fun TeacherPortalScreen(
    onNavigate: (String) -> Unit,
    viewModel: PortalViewModel = hiltViewModel()
) {
    val state by viewModel.teacherState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadTeacherPortal()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.teacher_portal_title)) },
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

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { onNavigate(Routes.ATTENDANCE) }, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.FactCheck, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(stringResource(R.string.attendance))
                    }
                    Button(onClick = { onNavigate(Routes.HIFZ_KITAB) }, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.MenuBook, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(stringResource(R.string.hifz_kitab))
                    }
                }
            }

            state.data?.let { portal ->
                portal.teacher?.let { t ->
                    item {
                        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(t.full_name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                Text(t.designation ?: "উস্তাদ")
                            }
                        }
                    }
                }

                item { Text("ক্লাস রুটিন (${portal.routines.size})", fontWeight = FontWeight.SemiBold) }
                items(portal.routines) { routine ->
                    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("${routine.class_name ?: "-"} • ${routine.subject_name ?: "-"}", fontWeight = FontWeight.Medium)
                            Text("${routine.day_of_week ?: ""} (${routine.start_time ?: ""} - ${routine.end_time ?: ""})", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                item { Text("নির্ধারিত ক্লাসসমূহ (${portal.classes.size})", fontWeight = FontWeight.SemiBold) }
                items(portal.classes) { cls ->
                    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                        Text(cls.name, modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}
