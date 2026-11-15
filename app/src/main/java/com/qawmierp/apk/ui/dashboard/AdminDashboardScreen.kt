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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.qawmierp.apk.R
import com.qawmierp.apk.core.i18n.formatBanglaCurrency
import com.qawmierp.apk.core.i18n.toBanglaNumber
import com.qawmierp.apk.core.i18n.translateRoleToBangla
import com.qawmierp.apk.ui.navigation.Routes
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onNavigate: (String) -> Unit,
    viewModel: AdminDashboardViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var financeScope by remember { mutableStateOf("total") } // "total" or "today"
    val dateStr = remember { SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("bn", "BD")).format(Date()) }

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
            // 1. Contextual Hero Welcome Banner (matching PremiumDashboardClient.tsx)
            item {
                OutlinedCard(
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
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

                // 3. 4 Core Academic KPI Cards
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        WebStyleKpiCard(
                            title = "মোট শিক্ষার্থী",
                            value = "${toBanglaNumber(d.students_count)} জন",
                            icon = Icons.Default.School,
                            badgeBg = Color(0xFFECFDF5),
                            badgeTint = Color(0xFF047857),
                            modifier = Modifier.weight(1f)
                        )
                        WebStyleKpiCard(
                            title = "শিক্ষক ও স্টাফ",
                            value = "${toBanglaNumber(d.teachers_count)} জন",
                            icon = Icons.Default.People,
                            badgeBg = Color(0xFFEEF2FF),
                            badgeTint = Color(0xFF4338CA),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        WebStyleKpiCard(
                            title = "সক্রিয় জামাত / শ্রেণি",
                            value = "${toBanglaNumber(d.classes_count)} টি",
                            icon = Icons.Default.MenuBook,
                            badgeBg = Color(0xFFFEF3C7),
                            badgeTint = Color(0xFFB45309),
                            modifier = Modifier.weight(1f)
                        )
                        WebStyleKpiCard(
                            title = "আজকের হাজিরা (${toBanglaNumber(attendancePct)}%)",
                            value = "${toBanglaNumber(d.today_present)} / ${toBanglaNumber(d.today_absent)}",
                            icon = Icons.Default.HowToReg,
                            badgeBg = Color(0xFFCCFBF1),
                            badgeTint = Color(0xFF0F766E),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // 4. Finance Scope Toggle (সর্বমোট vs আজকের) + 3 Finance KPI Cards
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("আর্থিক সারসংক্ষেপ", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            FilterChip(
                                selected = financeScope == "total",
                                onClick = { financeScope = "total" },
                                label = { Text("সর্বমোট") }
                            )
                            FilterChip(
                                selected = financeScope == "today",
                                onClick = { financeScope = "today" },
                                label = { Text("আজকের") }
                            )
                        }
                    }
                }

                val activeIncome = if (financeScope == "today") d.today_income else d.total_income
                val activeExpense = if (financeScope == "today") d.today_expense else d.total_expense
                val activeNet = activeIncome - activeExpense

                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        WebStyleKpiCard(
                            title = if (financeScope == "today") "আজকের আয়" else "মোট আয়",
                            value = formatBanglaCurrency(activeIncome),
                            icon = Icons.Default.TrendingUp,
                            badgeBg = Color(0xFFECFDF5),
                            badgeTint = Color(0xFF047857),
                            modifier = Modifier.weight(1f)
                        )
                        WebStyleKpiCard(
                            title = if (financeScope == "today") "আজকের ব্যয়" else "মোট ব্যয়",
                            value = formatBanglaCurrency(activeExpense),
                            icon = Icons.Default.TrendingDown,
                            badgeBg = Color(0xFFFFF1F2),
                            badgeTint = Color(0xFFBE123C),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    val savingsRate = if (activeIncome > 0) ((activeNet / activeIncome) * 100).toInt() else 0
                    WebStyleKpiCard(
                        title = "নিট তহবিল ব্যালেন্স (সঞ্চয় ${toBanglaNumber(savingsRate)}%)",
                        value = formatBanglaCurrency(activeNet),
                        icon = Icons.Default.AccountBalanceWallet,
                        badgeBg = Color(0xFFCCFBF1),
                        badgeTint = Color(0xFF0F766E),
                        modifier = Modifier.fillMaxWidth()
                    )
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

            // 6. Quick Modules Grid
            item {
                Text("সকল মডিউল", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ActionTile(stringResource(R.string.students), Icons.Default.People, Color(0xFF047857), Modifier.weight(1f)) {
                        onNavigate(Routes.STUDENTS)
                    }
                    ActionTile(stringResource(R.string.attendance), Icons.Default.FactCheck, Color(0xFF0F766E), Modifier.weight(1f)) {
                        onNavigate(Routes.ATTENDANCE)
                    }
                }
            }
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ActionTile(stringResource(R.string.hifz_kitab), Icons.Default.MenuBook, Color(0xFF4338CA), Modifier.weight(1f)) {
                        onNavigate(Routes.HIFZ_KITAB)
                    }
                    ActionTile(stringResource(R.string.fees), Icons.Default.Payments, Color(0xFFB45309), Modifier.weight(1f)) {
                        onNavigate(Routes.FEES)
                    }
                }
            }
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ActionTile("পরীক্ষা ও ছুটি", Icons.Default.Assignment, Color(0xFF7C3AED), Modifier.weight(1f)) {
                        onNavigate(Routes.EXAMS)
                    }
                    ActionTile("হিসাব ও যাকাত", Icons.Default.AccountBalance, Color(0xFFBE123C), Modifier.weight(1f)) {
                        onNavigate(Routes.FINANCE)
                    }
                }
            }
            item {
                ActionTile("বোর্ডিং, লাইব্রেরি ও সিকিউরিটি", Icons.Default.AdminPanelSettings, Color(0xFF334155), Modifier.fillMaxWidth()) {
                    onNavigate(Routes.OPERATIONS)
                }
            }
        }
    }
}

@Composable
private fun WebStyleKpiCard(
    title: String,
    value: String,
    icon: ImageVector,
    badgeBg: Color,
    badgeTint: Color,
    modifier: Modifier = Modifier
) {
    ElevatedCard(modifier = modifier, shape = RoundedCornerShape(16.dp)) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(badgeBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = badgeTint, modifier = Modifier.size(20.dp))
            }
            Column {
                Text(title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
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
            Text(title, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
