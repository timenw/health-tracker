package com.timenw.healthtracker.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.timenw.healthtracker.data.model.DailyHealthSummary
import com.timenw.healthtracker.ui.components.SummaryCard
import com.timenw.healthtracker.ui.theme.HealthSafe
import com.timenw.healthtracker.ui.theme.HealthDanger
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsTab(weeklyData: List<DailyHealthSummary>) {
    Column(modifier = Modifier.fillMaxSize()) {
        CenterAlignedTopAppBar(title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.BarChart, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp)); Text("数据统计", fontWeight = FontWeight.Bold)
            }
        })
        LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item { Spacer(modifier = Modifier.height(8.dp)) }
            item {
                val avgSys = if (weeklyData.isNotEmpty()) weeklyData.map { it.avgSystolic }.average().toInt() else 0
                val avgDia = if (weeklyData.isNotEmpty()) weeklyData.map { it.avgDiastolic }.average().toInt() else 0
                val avgHr = if (weeklyData.isNotEmpty()) weeklyData.map { it.avgHeartRate }.average().toInt() else 0
                val avgBs = if (weeklyData.isNotEmpty()) weeklyData.map { it.avgBloodSugar }.average().toFloat() else 0f
                Text("本周总览", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    SummaryCard(title = "平均血压", value = "${avgSys}/${avgDia}", modifier = Modifier.weight(1f), emoji = "❤️")
                    Spacer(modifier = Modifier.width(8.dp))
                    SummaryCard(title = "平均心率", value = "${avgHr}bpm", modifier = Modifier.weight(1f), emoji = "💓")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    SummaryCard(title = "平均血糖", value = "${String.format("%.1f", avgBs)}", modifier = Modifier.weight(1f), emoji = "🩸")
                    Spacer(modifier = Modifier.width(8.dp))
                    SummaryCard(title = "最新BMI", value = "${String.format("%.1f", weeklyData.lastOrNull()?.latestBmi ?: 0f)}", modifier = Modifier.weight(1f), emoji = "⚖️")
                }
            }
            item {
                Text("血压趋势", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (weeklyData.all { it.recordCount == 0 }) {
                            Text(text = "暂无数据，开始记录健康指标吧 🩺", style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(vertical = 24.dp))
                        } else {
                            BpBarChart(weeklyData)
                        }
                    }
                }
            }
            item {
                Text("健康提示", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                Card(modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f))) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("🩺 健康指标参考范围：", fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("• 血压：收缩压 90-140 / 舒张压 60-90 mmHg", style = MaterialTheme.typography.bodySmall)
                        Text("• 心率：60-100 bpm", style = MaterialTheme.typography.bodySmall)
                        Text("• 血糖：空腹 3.9-6.1 mmol/L", style = MaterialTheme.typography.bodySmall)
                        Text("• BMI：18.5-24 正常", style = MaterialTheme.typography.bodySmall)
                        Text("• 血氧：95-100%", style = MaterialTheme.typography.bodySmall)
                        Text("• 体温：36.0-37.2°C", style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("💡 如指标持续异常，请及时就医", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun BpBarChart(data: List<DailyHealthSummary>) {
    val maxBp = data.maxOfOrNull { it.avgSystolic } ?: 140
    val dayFormatter = SimpleDateFormat("E", Locale.getDefault())
    Row(modifier = Modifier.fillMaxWidth().height(160.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.Bottom) {
        data.forEach { summary ->
            val barHeight = (summary.avgSystolic.toFloat() / maxBp.toFloat().coerceAtLeast(1f)).coerceIn(0f, 1f)
            val isNormal = summary.avgSystolic in 90..140
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom, modifier = Modifier.weight(1f)) {
                Text(text = "${summary.avgSystolic}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(4.dp))
                Canvas(modifier = Modifier.fillMaxWidth(0.6f).height(100.dp)) {
                    val barWidth = size.width; val barH = size.height * barHeight
                    drawRect(color = if (isNormal) HealthSafe else HealthDanger, topLeft = Offset(0f, size.height - barH),
                        size = androidx.compose.ui.geometry.Size(barWidth, barH))
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = try { dayFormatter.format(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(summary.date) ?: Date()) } catch (e: Exception) { summary.date.takeLast(2) },
                    style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
