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
import com.qawmierp.apk.ui.components.StudentPortalHeader
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
            StudentPortalHeader(
                userFullName = state.data?.active_student?.full_name ?: state.userFullName,
                photoUrl = state.data?.active_student?.photo_url,
                onRefresh = { viewModel.loadStudentPortal(state.data?.active_student?.id) },
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

                // 3. Responsive 4 Rich Portal KPI Cards (Exact app/portal/page.tsx parity)
                val totalDays = portal.attendance.size
                val presentDays = portal.attendance.count { it.status.equals("present", ignoreCase = true) }
                val absentDays = portal.attendance.count { it.status.equals("absent", ignoreCase = true) }
                val rate = if (totalDays > 0) ((presentDays.toFloat() / totalDays) * 100).toInt() else 100
                val totalDue = portal.fees.filter { !it.status.equals("paid", ignoreCase = true) }.sumOf { it.amount - it.paid_amount }
                val totalPaid = portal.fees.sumOf { it.paid_amount }
                val latestSabak = portal.hifz_logs.firstOrNull()?.sabak ?: "দৈনিক সবক চলমান"
                val latestGrade = portal.exam_results.firstOrNull()?.grade ?: "A+"
                val latestExamName = portal.exam_results.firstOrNull()?.exam_name ?: "সাময়িক পরীক্ষা"

                item {
                    val cards: List<@Composable (Modifier) -> Unit> = listOf(
                        { mod ->
                            OutlinedCard(onClick = { onNavigate(Routes.ATTENDANCE) }, modifier = mod, shape = RoundedCornerShape(16.dp)) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Text("উপস্থিতির হার", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                                        Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFFECFDF5)), contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(16.dp))
                                        }
                                    }
                                    Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text("${toBanglaNumber(rate)}%", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                                        Text("সন্তোষজনক", style = MaterialTheme.typography.labelSmall, color = Color(0xFF059669), fontWeight = FontWeight.SemiBold)
                                    }
                                    LinearProgressIndicator(
                                        progress = { (rate.coerceIn(0, 100)) / 100f },
                                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(4.dp)),
                                        color = Color(0xFF10B981),
                                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    Text("উপস্থিত: ${toBanglaNumber(presentDays)} • অনুপস্থিত: ${toBanglaNumber(absentDays)}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }
                        },
                        { mod ->
                            OutlinedCard(onClick = { onNavigate(Routes.HIFZ_KITAB) }, modifier = mod, shape = RoundedCornerShape(16.dp)) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Text("চলমান সবক (হিফজ)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFFF0FDFA)), contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.MenuBook, contentDescription = null, tint = Color(0xFF0D9488), modifier = Modifier.size(16.dp))
                                        }
                                    }
                                    Text(latestSabak, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Surface(color = Color(0xFFFFFBEB), shape = RoundedCornerShape(8.dp)) {
                                        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(13.dp))
                                            Text("মোট ${toBanglaNumber(portal.hifz_logs.size)} টি লগ", style = MaterialTheme.typography.labelSmall, color = Color(0xFFD97706), fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        },
                        { mod ->
                            OutlinedCard(onClick = { onNavigate(Routes.FEES) }, modifier = mod, shape = RoundedCornerShape(16.dp)) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Text(if (totalDue > 0) "বকেয়া ফি" else "পরিশোধিত ফি", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                                        Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFFEFF6FF)), contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.CreditCard, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(16.dp))
                                        }
                                    }
                                    Text(
                                        formatBanglaCurrency(if (totalDue > 0) totalDue else totalPaid),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (totalDue > 0) Color(0xFFBE123C) else Color(0xFF047857)
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(13.dp))
                                        Text("${toBanglaNumber(portal.fees.size)} টি ফি রসিদ", style = MaterialTheme.typography.labelSmall, color = Color(0xFF059669), fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        },
                        { mod ->
                            OutlinedCard(onClick = { onNavigate(Routes.EXAMS) }, modifier = mod, shape = RoundedCornerShape(16.dp)) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Text("পরীক্ষার গ্রেড", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                                        Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFFFAF5FF)), contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color(0xFF9333EA), modifier = Modifier.size(16.dp))
                                        }
                                    }
                                    Text(latestGrade, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = Color(0xFF7E22CE))
                                    Text(latestExamName, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text("মার্কশিট দেখুন ›", style = MaterialTheme.typography.labelSmall, color = Color(0xFF9333EA), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    )
                    ResponsiveGrid(items = cards) { renderCard, mod ->
                        renderCard(mod)
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
