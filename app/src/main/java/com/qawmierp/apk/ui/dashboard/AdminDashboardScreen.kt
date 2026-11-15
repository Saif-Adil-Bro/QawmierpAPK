package com.qawmierp.apk.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.qawmierp.apk.R
import com.qawmierp.apk.core.i18n.formatBanglaCurrency
import com.qawmierp.apk.core.i18n.toBanglaNumber
import com.qawmierp.apk.core.i18n.translateRoleToBangla
import com.qawmierp.apk.ui.components.ResponsiveGrid
import com.qawmierp.apk.ui.navigation.Routes
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private data class KpiItem(
    val title: String,
    val value: String,
    val icon: ImageVector,
    val badgeBg: Color,
    val badgeTint: Color
)

private data class ModuleItem(
    val title: String,
    val icon: ImageVector,
    val tint: Color,
    val route: String
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AdminDashboardScreen(
    onNavigate: (String) -> Unit,
    viewModel: AdminDashboardViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var financeScope by remember { mutableStateOf("total") }
    val dateStr = remember { SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("bn", "BD")).format(Date()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            state.madrasaName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(stringResource(R.string.dashboard_title), style = MaterialTheme.typography.bodySmall)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadDashboard() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
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
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Contextual Hero Welcome Banner (Responsive FlowRow)
            item {
                OutlinedCard(
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Apartment, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                                    Text(state.madrasaName, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                }
                            }
                            Surface(
                                color = Color(0xFFECFDF5),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFF047857))
                                    Text(translateRoleToBangla(state.userRole), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color(0xFF047857))
                                }
                            }
                        }

                        Text(
                            text = "স্বাগতম, ${state.userFullName}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(dateStr, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // 2. Universal Search
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        viewModel.search(it)
                    },
                    placeholder = { Text("ছাত্র, উস্তাদ, ক্লাস বা দাতা খুঁজুন...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            state.searchResults?.let { results ->
                if (results.students.isNotEmpty() || results.teachers.isNotEmpty()) {
                    item {
                        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("সার্চ ফলাফল", fontWeight = FontWeight.Bold)
                                results.students.take(3).forEach {
                                    Text("ছাত্র: ${it.full_name} (রোল: ${toBanglaNumber(it.roll_number ?: "-")})", style = MaterialTheme.typography.bodySmall)
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
                item { LinearProgressIndicator(modifier = Modifier.fillMaxWidth()) }
            }

            state.error?.let { err ->
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                        Text(err, modifier = Modifier.padding(12.dp), color = MaterialTheme.colorScheme.onErrorContainer)
                    }
                }
            }

            state.dashboard?.let { d ->
                val totalMarked = d.today_present + d.today_absent
                val attendancePct = if (totalMarked > 0) ((d.today_present.toFloat() / totalMarked) * 100).toInt() else 0

                val academicKpis = listOf(
                    KpiItem("মোট শিক্ষার্থী", "${toBanglaNumber(d.students_count)} জন", Icons.Default.School, Color(0xFFECFDF5), Color(0xFF047857)),
                    KpiItem("শিক্ষক ও স্টাফ", "${toBanglaNumber(d.teachers_count)} জন", Icons.Default.People, Color(0xFFEEF2FF), Color(0xFF4338CA)),
                    KpiItem("সক্রিয় জামাত", "${toBanglaNumber(d.classes_count)} টি", Icons.Default.MenuBook, Color(0xFFFEF3C7), Color(0xFFB45309)),
                    KpiItem("আজকের হাজিরা (${toBanglaNumber(attendancePct)}%)", "${toBanglaNumber(d.today_present)} / ${toBanglaNumber(d.today_absent)}", Icons.Default.HowToReg, Color(0xFFCCFBF1), Color(0xFF0F766E))
                )

                // 3. Responsive 1/2/4-column Academic KPIs
                item {
                    ResponsiveGrid(items = academicKpis) { item, mod ->
                        WebStyleKpiCard(item, mod)
                    }
                }

                // 4. Finance Scope Toggle (সর্বমোট vs আজকের) + Responsive Finance KPIs
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("আর্থিক সারসংক্ষেপ", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            FilterChip(selected = financeScope == "total", onClick = { financeScope = "total" }, label = { Text("সর্বমোট") })
                            FilterChip(selected = financeScope == "today", onClick = { financeScope = "today" }, label = { Text("আজকের") })
                        }
                    }
                }

                val activeIncome = if (financeScope == "today") d.today_income else d.total_income
                val activeExpense = if (financeScope == "today") d.today_expense else d.total_expense
                val activeNet = activeIncome - activeExpense
                val savingsRate = if (activeIncome > 0) ((activeNet / activeIncome) * 100).toInt() else 0

                val financeKpis = listOf(
                    KpiItem(if (financeScope == "today") "আজকের আয়" else "মোট আয়", formatBanglaCurrency(activeIncome), Icons.Default.TrendingUp, Color(0xFFECFDF5), Color(0xFF047857)),
                    KpiItem(if (financeScope == "today") "আজকের ব্যয়" else "মোট ব্যয়", formatBanglaCurrency(activeExpense), Icons.Default.TrendingDown, Color(0xFFFFF1F2), Color(0xFFBE123C)),
                    KpiItem("নিট ব্যালেন্স (${toBanglaNumber(savingsRate)}%)", formatBanglaCurrency(activeNet), Icons.Default.AccountBalanceWallet, Color(0xFFCCFBF1), Color(0xFF0F766E))
                )

                item {
                    ResponsiveGrid(items = financeKpis, maxColumnsTablet = 3) { item, mod ->
                        WebStyleKpiCard(item, mod)
                    }
                }

                // 5. Charts
                item { AttendanceDonutChart(present = d.today_present, absent = d.today_absent) }
                if (d.monthly_income_expense.isNotEmpty()) {
                    item { MonthlyIncomeExpenseChart(items = d.monthly_income_expense) }
                }
                if (d.exam_pass_rate.isNotEmpty()) {
                    item { ExamPassRateSection(rates = d.exam_pass_rate) }
                }
            }

            // 6. Responsive Quick Modules Grid
            item {
                Text("সকল মডিউল", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            item {
                val modules = listOf(
                    ModuleItem(stringResource(R.string.students), Icons.Default.People, Color(0xFF047857), Routes.STUDENTS),
                    ModuleItem(stringResource(R.string.attendance), Icons.Default.FactCheck, Color(0xFF0F766E), Routes.ATTENDANCE),
                    ModuleItem(stringResource(R.string.hifz_kitab), Icons.Default.MenuBook, Color(0xFF4338CA), Routes.HIFZ_KITAB),
                    ModuleItem(stringResource(R.string.fees), Icons.Default.Payments, Color(0xFFB45309), Routes.FEES),
                    ModuleItem("পরীক্ষা ও ছুটি", Icons.Default.Assignment, Color(0xFF7C3AED), Routes.EXAMS),
                    ModuleItem("হিসাব ও যাকাত", Icons.Default.AccountBalance, Color(0xFFBE123C), Routes.FINANCE),
                    ModuleItem("বোর্ডিং ও লাইব্রেরি", Icons.Default.AdminPanelSettings, Color(0xFF334155), Routes.OPERATIONS)
                )
                ResponsiveGrid(items = modules) { m, mod ->
                    ActionTile(m.title, m.icon, m.tint, mod) { onNavigate(m.route) }
                }
            }
        }
    }
}

@Composable
private fun WebStyleKpiCard(item: KpiItem, modifier: Modifier = Modifier) {
    ElevatedCard(modifier = modifier, shape = RoundedCornerShape(16.dp)) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(item.badgeBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(item.icon, contentDescription = null, tint = item.badgeTint, modifier = Modifier.size(20.dp))
            }
            Column {
                Text(
                    item.title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    item.value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun ActionTile(
    title: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedCard(onClick = onClick, modifier = modifier, shape = RoundedCornerShape(14.dp)) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(icon, contentDescription = title, tint = tint)
            Text(
                title,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
