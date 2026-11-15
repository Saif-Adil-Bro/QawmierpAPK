package com.qawmierp.apk.ui.portal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.qawmierp.apk.R
import com.qawmierp.apk.core.i18n.formatBanglaCurrency
import com.qawmierp.apk.core.i18n.toBanglaNumber
import com.qawmierp.apk.ui.components.ResponsiveGrid
import com.qawmierp.apk.ui.navigation.Routes

private data class PortalKpi(
    val title: String,
    val value: String,
    val subtitle: String,
    val valueColor: Color
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
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
                title = {
                    Text(
                        stringResource(R.string.student_portal_title),
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                actions = {
                    IconButton(onClick = { viewModel.loadStudentPortal(state.data?.active_student?.id) }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
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
            state.error?.let { err ->
                item { Text(err, color = MaterialTheme.colorScheme.error) }
            }

            state.data?.let { portal ->
                // 1. Multi-child Switcher Bar
                if (portal.students.size > 1) {
                    item {
                        OutlinedCard(shape = RoundedCornerShape(16.dp)) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    "সন্তান নির্বাচন করুন (${toBanglaNumber(portal.students.size)} জন নিবন্ধিত):",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(portal.students) { child ->
                                        val selected = child.id == portal.active_student?.id
                                        FilterChip(
                                            selected = selected,
                                            onClick = { viewModel.loadStudentPortal(child.id) },
                                            label = {
                                                Text("${child.full_name} (রোল: ${toBanglaNumber(child.roll_number ?: "-")})")
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 2. Child Profile Hero Banner (Responsive FlowRow)
                portal.active_student?.let { student ->
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(24.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(Color(0xFF064E3B), Color(0xFF115E59), Color(0xFF0F172A))
                                    )
                                )
                                .padding(20.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(64.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(Color.White.copy(alpha = 0.15f))
                                            .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(16.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (!student.photo_url.isNullOrBlank()) {
                                            AsyncImage(
                                                model = student.photo_url,
                                                contentDescription = student.full_name,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        } else {
                                            Text(
                                                text = student.full_name.take(1),
                                                style = MaterialTheme.typography.headlineMedium,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Surface(
                                            color = Color(0xFF10B981).copy(alpha = 0.25f),
                                            shape = CircleShape
                                        ) {
                                            Text(
                                                "● নিয়মিত শিক্ষার্থী",
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color(0xFFA7F3D0),
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Text(
                                            student.full_name,
                                            style = MaterialTheme.typography.titleLarge,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        FlowRow(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                "আইডি: ${student.student_id_code ?: "-"}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color(0xFFD1FAE5)
                                            )
                                            Text(
                                                "• রোল: ${toBanglaNumber(student.roll_number ?: "-")}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color(0xFFD1FAE5)
                                            )
                                            Text(
                                                "• জামাত: ${student.classes?.name ?: "-"}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color(0xFFA7F3D0)
                                            )
                                        }
                                    }
                                }

                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    OutlinedButton(
                                        onClick = { onNavigate(Routes.FEES) },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                                    ) {
                                        Icon(Icons.Default.CreditCard, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("ফি ও রসিদ")
                                    }
                                    Button(
                                        onClick = { onNavigate(Routes.EXAMS) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF10B981),
                                            contentColor = Color(0xFF022C22)
                                        )
                                    ) {
                                        Icon(Icons.Default.EmojiEvents, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("ফলাফল শিট", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                // 3. Responsive 4 Portal KPI Cards (1/2/4 columns)
                val totalDays = portal.attendance.size
                val presentDays = portal.attendance.count { it.status.equals("present", ignoreCase = true) }
                val rate = if (totalDays > 0) ((presentDays.toFloat() / totalDays) * 100).toInt() else 100
                val totalDue = portal.fees.filter { !it.status.equals("paid", ignoreCase = true) }.sumOf { it.amount - it.paid_amount }
                val latestSabak = portal.hifz_logs.firstOrNull()?.sabak ?: "—"

                val kpis = listOf(
                    PortalKpi("উপস্থিতির হার", "${toBanglaNumber(rate)}%", "মোট ${toBanglaNumber(totalDays)} দিন", Color(0xFF047857)),
                    PortalKpi("বকেয়া ফি", formatBanglaCurrency(totalDue), "${toBanglaNumber(portal.fees.size)} টি এন্ট্রি", if (totalDue > 0) Color(0xFFBE123C) else Color(0xFF047857)),
                    PortalKpi("সর্বশেষ সবক", latestSabak, "${toBanglaNumber(portal.hifz_logs.size)} টি লগ", Color(0xFF4338CA)),
                    PortalKpi("নোটিশ বোর্ড", "${toBanglaNumber(portal.notices.size)} টি", "সাম্প্রতিক ঘোষণা", Color(0xFFB45309))
                )

                item {
                    ResponsiveGrid(items = kpis) { kpi, mod ->
                        ElevatedCard(modifier = mod, shape = RoundedCornerShape(16.dp)) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(kpi.title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(kpi.value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = kpi.valueColor, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(kpi.subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                    }
                }

                // 4. Recent Attendance, Hifz, Exam Results & Notices
                item { Text("সাম্প্রতিক হাজিরা (${toBanglaNumber(portal.attendance.size)})", fontWeight = FontWeight.Bold) }
                items(portal.attendance.take(5)) { att ->
                    OutlinedCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                        Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(att.date)
                            Spacer(modifier = Modifier.weight(1f))
                            Text(att.status.uppercase(), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                item { Text("হিফজ ও সবক লগ (${toBanglaNumber(portal.hifz_logs.size)})", fontWeight = FontWeight.Bold) }
                items(portal.hifz_logs.take(5)) { hifz ->
                    OutlinedCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("${hifz.date} — সবক: ${hifz.sabak ?: "-"}", fontWeight = FontWeight.Medium)
                            Text("সবকী: ${hifz.sabqi ?: "-"} | মনজিল: ${hifz.manzil ?: "-"}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                item { Text("পরীক্ষার ফলাফল (${toBanglaNumber(portal.exam_results.size)})", fontWeight = FontWeight.Bold) }
                items(portal.exam_results.take(5)) { res ->
                    OutlinedCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                        Row(modifier = Modifier.padding(12.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(res.subject_name ?: "বিষয়", fontWeight = FontWeight.Bold)
                                Text(res.exam_name ?: "পরীক্ষা", style = MaterialTheme.typography.bodySmall)
                            }
                            Text("${toBanglaNumber(res.marks_obtained?.toInt() ?: 0)} (${res.grade ?: "-"})", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }

                item { Text("নোটিশ বোর্ড (${toBanglaNumber(portal.notices.size)})", fontWeight = FontWeight.Bold) }
                items(portal.notices.take(5)) { notice ->
                    OutlinedCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
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
