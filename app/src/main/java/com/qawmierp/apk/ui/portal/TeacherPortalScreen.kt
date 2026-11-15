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

                // 2. Responsive 4 Teacher KPI Cards (Exact app/teacher-portal/page.tsx parity)
                item {
                    val kpis = listOf(
                        Triple("বরাদ্দকৃত কিতাব", "${toBanglaNumber(portal.assigned_subjects.size)} টি", Icons.Default.MenuBook to (Color(0xFFECFDF5) to Color(0xFF047857))),
                        Triple("ক্লাস রুটিন", "${toBanglaNumber(portal.routines.size)} টি", Icons.Default.Schedule to (Color(0xFFEEF2FF) to Color(0xFF4338CA))),
                        Triple("নির্ধারিত শ্রেণি", "${toBanglaNumber(portal.classes.size)} টি", Icons.Default.CalendarMonth to (Color(0xFFF0FDFA) to Color(0xFF0F766E))),
                        Triple("নোটিশ", "${toBanglaNumber(portal.notices.size)} টি", Icons.Default.Notifications to (Color(0xFFFAF5FF) to Color(0xFF7C3AED)))
                    )
                    ResponsiveGrid(items = kpis) { (title, value, iconPair), mod ->
                        val (icon, colors) = iconPair
                        val (bg, tint) = colors
                        OutlinedCard(modifier = mod, shape = RoundedCornerShape(16.dp)) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier.size(38.dp).clip(RoundedCornerShape(10.dp)).background(bg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
                                }
                                Column {
                                    Text(title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = tint, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }
                        }
                    }
                }

                // 3. Routine Card (Exact time & class pill styling)
                item {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.Schedule, contentDescription = null, tint = Color(0xFF4F46E5), modifier = Modifier.size(18.dp))
                        Text("পাঠদান রুটিন ও সময়সূচি (${toBanglaNumber(portal.routines.size)})", fontWeight = FontWeight.Bold)
                    }
                }
                items(portal.routines) { routine ->
                    OutlinedCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Surface(color = Color(0xFFE0E7FF), shape = RoundedCornerShape(6.dp)) {
                                    Text(
                                        "${toBanglaNumber(routine.start_time ?: "")} - ${toBanglaNumber(routine.end_time ?: "")}",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF3730A3),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Surface(color = Color(0xFFD1FAE5), shape = RoundedCornerShape(6.dp)) {
                                    Text(
                                        routine.class_name ?: "-",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF065F46),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Text(routine.subject_name ?: "-", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                            routine.day_of_week?.let {
                                Text("বার: $it", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }

                // 4. Daily Teacher Modules Grid
                item {
                    Text("দৈনিক শিক্ষক মডিউলসমূহ", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                item {
                    val teacherModules = listOf(
                        Triple("দৈনিক হাজিরা গ্রহণ", Icons.Default.FactCheck, Color(0xFF047857) to Routes.ATTENDANCE),
                        Triple("সবক ও কিতাব এন্ট্রি", Icons.Default.MenuBook, Color(0xFF0F766E) to Routes.HIFZ_KITAB),
                        Triple("পরীক্ষা ও নম্বর এন্ট্রি", Icons.Default.Assignment, Color(0xFF4338CA) to Routes.EXAMS),
                        Triple("নোটিশ বোর্ড", Icons.Default.Notifications, Color(0xFF7C3AED) to Routes.NOTIFICATIONS)
                    )
                    ResponsiveGrid(items = teacherModules) { (title, icon, pair), mod ->
                        val (tint, route) = pair
                        OutlinedCard(onClick = { onNavigate(route) }, modifier = mod, shape = RoundedCornerShape(14.dp)) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(icon, contentDescription = title, tint = tint)
                                Text(title, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                    }
                }

                // 5. Assigned Classes & Notices
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
