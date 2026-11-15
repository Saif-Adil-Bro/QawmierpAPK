package com.qawmierp.apk.ui.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.qawmierp.apk.core.network.dto.ExamPassRateDto
import com.qawmierp.apk.core.network.dto.MonthlyIncomeExpenseDto

@Composable
fun MonthlyIncomeExpenseChart(
    items: List<MonthlyIncomeExpenseDto>,
    modifier: Modifier = Modifier
) {
    if (items.isEmpty()) return
    val displayItems = items.takeLast(6)
    val maxVal = displayItems.maxOf { maxOf(it.income, it.expense) }.coerceAtLeast(1.0)
    val incomeColor = MaterialTheme.colorScheme.primary
    val expenseColor = Color(0xFFE11D48)

    ElevatedCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("মাসিক আয় ও ব্যয়", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    LegendDot(incomeColor, "আয়")
                    LegendDot(expenseColor, "ব্যয়")
                }
            }

            Canvas(modifier = Modifier.fillMaxWidth().height(130.dp)) {
                val groupWidth = size.width / displayItems.size
                val barWidth = (groupWidth * 0.28f).coerceAtMost(24f)
                val gap = barWidth * 0.35f

                displayItems.forEachIndexed { index, item ->
                    val centerX = groupWidth * index + groupWidth / 2f
                    val incomeHeight = ((item.income / maxVal) * (size.height - 12f)).toFloat()
                    val expenseHeight = ((item.expense / maxVal) * (size.height - 12f)).toFloat()

                    drawRoundRect(
                        color = incomeColor,
                        topLeft = Offset(centerX - barWidth - gap / 2f, size.height - incomeHeight),
                        size = Size(barWidth, incomeHeight),
                        cornerRadius = CornerRadius(6f, 6f)
                    )
                    drawRoundRect(
                        color = expenseColor,
                        topLeft = Offset(centerX + gap / 2f, size.height - expenseHeight),
                        size = Size(barWidth, expenseHeight),
                        cornerRadius = CornerRadius(6f, 6f)
                    )
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                displayItems.forEach {
                    Text(it.month.take(3), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun AttendanceDonutChart(
    present: Int,
    absent: Int,
    modifier: Modifier = Modifier
) {
    val total = (present + absent).coerceAtLeast(1)
    val presentAngle = (present.toFloat() / total.toFloat()) * 360f
    val presentPct = ((present.toFloat() / total.toFloat()) * 100).toInt()
    val primaryColor = MaterialTheme.colorScheme.primary
    val absentColor = Color(0xFFF59E0B)

    ElevatedCard(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(84.dp)) {
                Canvas(modifier = Modifier.size(80.dp)) {
                    drawArc(
                        color = absentColor,
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 14f, cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = primaryColor,
                        startAngle = -90f,
                        sweepAngle = presentAngle,
                        useCenter = false,
                        style = Stroke(width = 14f, cap = StrokeCap.Round)
                    )
                }
                Text("$presentPct%", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("আজকের হাজিরা অনুপাত", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                LegendDot(primaryColor, "উপস্থিত: $present জন")
                LegendDot(absentColor, "অনুপস্থিত: $absent জন")
            }
        }
    }
}

@Composable
fun ExamPassRateSection(rates: List<ExamPassRateDto>) {
    if (rates.isEmpty()) return
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("পরীক্ষার পাসের হার", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            rates.take(4).forEach { item ->
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(item.exam_name, style = MaterialTheme.typography.bodySmall)
                        Text("${item.pass_rate.toInt()}%", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                    }
                    LinearProgressIndicator(
                        progress = { (item.pass_rate / 100.0).toFloat().coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth().height(6.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
