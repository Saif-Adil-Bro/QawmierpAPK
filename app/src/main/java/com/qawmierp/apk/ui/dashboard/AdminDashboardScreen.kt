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
import com.qawmierp.apk.ui.components.AdminPanelHeader
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
    val badgeTint: Color,
    val footerLabel: String,
    val route: String,
    val progressPct: Int? = null
)

private data class FinanceKpiItem(
    val title: String,
    val badgeText: String,
    val badgeBg: Color,
    val badgeTint: Color,
    val amount: String,
    val amountColor: Color,
    val subtitle: String,
    val actionLabel: String?,
    val route: String
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
    var now by remember { mutableStateOf(Date()) }
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(60_000L)
            now = Date()
        }
    }
    val dateStr = remember(now) { SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("bn", "BD")).format(now) }
    val timeStr = remember(now) { SimpleDateFormat("hh:mm a", Locale("bn", "BD")).format(now) }

    Scaffold(
        topBar = {
            AdminPanelHeader(
                madrasaName = state.madrasaName,
                userFullName = state.userFullName,
                userRole = state.userRole,
                onRefresh = { viewModel.loadDashboard() },
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
            // 1. Contextual Hero Welcome Banner (Exact PremiumDashboardClient.tsx parity)
            item {
                OutlinedCard(
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Apartment, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFF059669))
                                    Text(state.madrasaName, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
                                }
                            }
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(13.dp), tint = Color(0xFF059669))
                                    Text(dateStr, style = MaterialTheme.typography.labelSmall)
                                }
                            }
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(13.dp), tint = Color(0xFFF59E0B))
                                    Text(timeStr, style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "আস-সালামু আলাইকুম, ${state.userFullName}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold
                            )
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "প্রাতিষ্ঠানিক ও আর্থিক কার্যক্রমের রিয়েল-টাইম সারসংক্ষেপ।",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Surface(
                                    color = Color(0xFFECFDF5),
                                    shape = RoundedCornerShape(6.dp),
                                    border = BorderStroke(1.dp, Color(0xFFA7F3D0))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color(0xFF047857))
                                        Text(translateRoleToBangla(state.userRole), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold, color = Color(0xFF047857))
                                    }
                                }
                            }
                        }

                        // 3 Interactive Quick Actions
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { onNavigate(Routes.ATTENDANCE) },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669), contentColor = Color.White)
                            ) {
                                Icon(Icons.Default.CheckBox, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("হাজিরা গ্রহণ", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = { onNavigate(Routes.HIFZ_KITAB) },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFF059669))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("দৈনিক পড়া ও সবক", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = { onNavigate(Routes.FEES) },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFFD97706))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("ফি আদায়", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            }
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
                    KpiItem("মোট শিক্ষার্থী", "${toBanglaNumber(d.students_count)} জন", Icons.Default.School, Color(0xFFECFDF5), Color(0xFF059669), "সকল শিক্ষার্থী", Routes.STUDENTS),
                    KpiItem("শিক্ষক ও স্টাফ", "${toBanglaNumber(d.teachers_count)} জন", Icons.Default.People, Color(0xFFF0F9FF), Color(0xFF0284C7), "শিক্ষক ও স্টাফ", Routes.OPERATIONS),
                    KpiItem("সক্রিয় জামাত", "${toBanglaNumber(d.classes_count)} টি", Icons.Default.MenuBook, Color(0xFFFFFBEB), Color(0xFFD97706), "জামাত ও সবক", Routes.HIFZ_KITAB),
                    KpiItem("আজকের হাজিরা", "${toBanglaNumber(d.today_present)} জন", Icons.Default.HowToReg, Color(0xFFF0FDFA), Color(0xFF0D9488), "হার: ${toBanglaNumber(attendancePct)}%", Routes.ATTENDANCE, attendancePct)
                )

                // 3. Academic & Attendance Header + Finance Scope Toggle
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("সার্বিক পরিসংখ্যান", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            FilterChip(selected = financeScope == "total", onClick = { financeScope = "total" }, label = { Text("সর্বমোট") })
                            FilterChip(selected = financeScope == "today", onClick = { financeScope = "today" }, label = { Text("আজকের") })
                        }
                    }
                }

                item {
                    ResponsiveGrid(items = academicKpis) { item, mod ->
                        WebStyleKpiCard(item, mod) { onNavigate(item.route) }
                    }
                }

                // 4. Financial Highlight Strip (3 Columns)
                val activeIncome = if (financeScope == "today") d.today_income else d.total_income
                val activeExpense = if (financeScope == "today") d.today_expense else d.total_expense
                val activeNet = activeIncome - activeExpense
                val savingsRate = if (activeIncome > 0) ((activeNet / activeIncome) * 100).toInt() else 0

                val financeKpis = listOf(
                    FinanceKpiItem(
                        title = if (financeScope == "today") "আজকের মোট আয়" else "মোট আয় (ফান্ড কালেকশন)",
                        badgeText = "আয়",
                        badgeBg = Color(0xFFECFDF5),
                        badgeTint = Color(0xFF047857),
                        amount = formatBanglaCurrency(activeIncome),
                        amountColor = Color(0xFF059669),
                        subtitle = if (financeScope == "today") "আজকের ফি ও অনুদান সংগ্রহ" else "আজকের কালেকশন: ${formatBanglaCurrency(d.today_income)}",
                        actionLabel = "হিস্ট্রি ↗",
                        route = Routes.FINANCE
                    ),
                    FinanceKpiItem(
                        title = if (financeScope == "today") "আজকের মোট ব্যয়" else "মোট ব্যয় (বোর্ডিং সহ)",
                        badgeText = "ব্যয়",
                        badgeBg = Color(0xFFFFF1F2),
                        badgeTint = Color(0xFFBE123C),
                        amount = formatBanglaCurrency(activeExpense),
                        amountColor = Color(0xFFE11D48),
                        subtitle = if (financeScope == "today") "আজকের সাধারণ ও বাজার খরচ" else "সর্বমোট প্রাতিষ্ঠানিক খরচ",
                        actionLabel = null,
                        route = Routes.FINANCE
                    ),
                    FinanceKpiItem(
                        title = if (financeScope == "today") "আজকের নিট উদ্বৃত্ত" else "নিট ফান্ড স্থিতি (উদ্বৃত্ত)",
                        badgeText = if (activeNet >= 0) "উদ্বৃত্ত" else "ঘাটতি",
                        badgeBg = if (activeNet >= 0) Color(0xFFF0F9FF) else Color(0xFFFFFBEB),
                        badgeTint = if (activeNet >= 0) Color(0xFF0369A1) else Color(0xFFB45309),
                        amount = formatBanglaCurrency(kotlin.math.abs(activeNet)),
                        amountColor = if (activeNet >= 0) Color(0xFF0284C7) else Color(0xFFD97706),
                        subtitle = if (activeIncome > 0) "আয়-ব্যয় অনুপাত: ${toBanglaNumber(savingsRate)}%" else "আর্থিক সার্বিক অবস্থা",
                        actionLabel = "অ্যাকাউন্টিং ↗",
                        route = Routes.FINANCE
                    )
                )

                item {
                    ResponsiveGrid(items = financeKpis, maxColumnsTablet = 3) { item, mod ->
                        WebStyleFinanceCard(item, mod) { onNavigate(item.route) }
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
private fun WebStyleKpiCard(item: KpiItem, modifier: Modifier = Modifier, onClick: () -> Unit) {
    OutlinedCard(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        item.title,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        item.value,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(item.badgeBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(item.icon, contentDescription = null, tint = item.badgeTint, modifier = Modifier.size(20.dp))
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))

            if (item.progressPct != null) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(item.footerLabel, style = MaterialTheme.typography.labelSmall, color = Color(0xFF047857), fontWeight = FontWeight.SemiBold)
                    }
                    LinearProgressIndicator(
                        progress = { (item.progressPct.coerceIn(0, 100)) / 100f },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFF10B981),
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(item.footerLabel, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun WebStyleFinanceCard(item: FinanceKpiItem, modifier: Modifier = Modifier, onClick: () -> Unit) {
    OutlinedCard(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    item.title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Surface(color = item.badgeBg, shape = RoundedCornerShape(100.dp)) {
                    Text(
                        item.badgeText,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = item.badgeTint
                    )
                }
            }

            Text(
                item.amount,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = item.amountColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    item.subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                item.actionLabel?.let { lbl ->
                    Text(lbl, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = item.amountColor)
                }
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
