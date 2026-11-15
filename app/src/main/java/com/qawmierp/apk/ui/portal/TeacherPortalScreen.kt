package com.qawmierp.apk.ui.portal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.qawmierp.apk.R
import com.qawmierp.apk.core.i18n.toBanglaNumber
import com.qawmierp.apk.ui.components.ResponsiveGrid
import com.qawmierp.apk.ui.components.TeacherPortalHeader
import com.qawmierp.apk.ui.navigation.Routes
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private data class TeacherKpi(
    val title: String,
    val value: String,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TeacherPortalScreen(
    onNavigate: (String) -> Unit,
    viewModel: PortalViewModel = hiltViewModel()
) {
    val state by viewModel.teacherState.collectAsState()
    val dateStr = remember { SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("bn", "BD")).format(Date()) }

    LaunchedEffect(Unit) {
        viewModel.loadTeacherPortal()
    }

    Scaffold(
        topBar = {
            TeacherPortalHeader(
                teacherName = state.data?.teacher?.full_name ?: state.userFullName,
                onRefresh = { viewModel.loadTeacherPortal() },
                onNavigate = onNavigate
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
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

            state.data?.let { portal ->
                // 1. Teacher Welcome Banner (Responsive FlowRow)
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(Color(0xFF0F172A), Color(0xFF064E3B), Color(0xFF0F172A))
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Surface(
                                color = Color(0xFF10B981).copy(alpha = 0.2f),
                                shape = CircleShape
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFF6EE7B7))
                                    Text("আসসালামু আলাইকুম ওয়া রাহমাতুল্লাহ", style = MaterialTheme.typography.labelSmall, color = Color(0xFF6EE7B7), fontWeight = FontWeight.Bold)
                                }
                            }

                            Text(
                                "স্বাগতম, ${portal.teacher?.full_name ?: "মুহতারাম উস্তাদ"}",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "পদবি: ${portal.teacher?.designation ?: "মুদাররিস"} | $dateStr",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFD1FAE5)
                            )

                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Button(
                                    onClick = { onNavigate(Routes.ATTENDANCE) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669))
                                ) {
                                    Icon(Icons.Default.FactCheck, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("হাজিরা গ্রহণ")
                                }
                                OutlinedButton(
                                    onClick = { onNavigate(Routes.HIFZ_KITAB) },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                                ) {
                                    Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("সবক / কিতাব")
                                }
                            }
                        }
                    }
                }

                // 2. Responsive 4 Teacher KPI Cards (1/2/4 columns)
                val teacherKpis = listOf(
                    TeacherKpi("বরাদ্দকৃত কিতাব", "${toBanglaNumber(portal.assigned_subjects.size)} টি", Color(0xFF047857)),
                    TeacherKpi("ক্লাস রুটিন", "${toBanglaNumber(portal.routines.size)} টি", Color(0xFF4338CA)),
                    TeacherKpi("নির্ধারিত শ্রেণি", "${toBanglaNumber(portal.classes.size)} টি", Color(0xFF0F766E)),
                    TeacherKpi("নোটিশ", "${toBanglaNumber(portal.notices.size)} টি", Color(0xFF7C3AED))
                )

                item {
                    ResponsiveGrid(items = teacherKpis) { kpi, mod ->
                        ElevatedCard(modifier = mod, shape = RoundedCornerShape(16.dp)) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(kpi.title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(kpi.value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = kpi.color, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                    }
                }

                // 3. Routine Card
                item {
                    Text("পাঠদান রুটিন ও সময়সূচি (${toBanglaNumber(portal.routines.size)})", fontWeight = FontWeight.Bold)
                }
                items(portal.routines) { routine ->
                    OutlinedCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("${routine.class_name ?: "-"} • ${routine.subject_name ?: "-"}", fontWeight = FontWeight.Bold)
                            Text("${routine.day_of_week ?: ""} (${routine.start_time ?: ""} - ${routine.end_time ?: ""})", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                // 4. Assigned Classes & Notices
                item { Text("নির্ধারিত ক্লাসসমূহ (${toBanglaNumber(portal.classes.size)})", fontWeight = FontWeight.Bold) }
                items(portal.classes) { cls ->
                    OutlinedCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                        Text(cls.name, modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Medium)
                    }
                }

                item { Text("নোটিশ (${toBanglaNumber(portal.notices.size)})", fontWeight = FontWeight.Bold) }
                items(portal.notices) { n ->
                    OutlinedCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(n.title, fontWeight = FontWeight.Bold)
                            n.content?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                        }
                    }
                }
            }
        }
    }
}
