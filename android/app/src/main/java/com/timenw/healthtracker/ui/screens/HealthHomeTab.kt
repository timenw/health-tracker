package com.timenw.healthtracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timenw.healthtracker.data.model.*
import com.timenw.healthtracker.ui.components.SummaryCard
import com.timenw.healthtracker.ui.components.EmptyStateView
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HealthHomeTab(
    summary: DailyHealthSummary,
    records: List<HealthRecord>,
    onAddRecord: (HealthRecord) -> Unit,
    onRemoveRecord: (Long) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    val formatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    Column(modifier = Modifier.fillMaxSize()) {
        CenterAlignedTopAppBar(title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🩺", fontSize = 24.sp); Spacer(modifier = Modifier.width(8.dp))
                Text("测了么", fontWeight = FontWeight.Bold)
            }
        })

        LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    SummaryCard(title = "血压", value = "${summary.avgSystolic}/${summary.avgDiastolic}", modifier = Modifier.weight(1f), emoji = "❤️",
                        status = if (summary.records.isNotEmpty()) summary.records.last().bpStatus else "")
                    Spacer(modifier = Modifier.width(8.dp))
                    SummaryCard(title = "心率", value = "${summary.avgHeartRate}bpm", modifier = Modifier.weight(1f), emoji = "💓",
                        status = if (summary.records.isNotEmpty()) summary.records.last().hrStatus else "")
                }
            }
            item {
                Row(modifier = Modifier.fillMaxWidth()) {
                    SummaryCard(title = "血糖", value = "${String.format("%.1f", summary.avgBloodSugar)}", modifier = Modifier.weight(1f), emoji = "🩸",
                        status = if (summary.records.isNotEmpty()) summary.records.last().bsStatus else "")
                    Spacer(modifier = Modifier.width(8.dp))
                    SummaryCard(title = "BMI", value = "${String.format("%.1f", summary.latestBmi)}", modifier = Modifier.weight(1f), emoji = "⚖️",
                        status = if (summary.records.isNotEmpty()) summary.records.last().bmiStatus else "")
                }
            }
            item {
                FilledTonalButton(onClick = { showAddDialog = true }, modifier = Modifier.fillMaxWidth().height(48.dp)) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp)); Text("记录指标")
                }
            }
            item { Text("今日记录", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold) }
            if (records.isEmpty()) {
                item { EmptyStateView(emoji = "🩺", title = "还没有健康记录", subtitle = "记录你的血压、心率、血糖等健康指标") }
            } else {
                items(records.reversed(), key = { it.id }) { record ->
                    Card(modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))) {
                        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "${record.systolic}/${record.diastolic} mmHg · ${record.heartRate}bpm",
                                        style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                                    Text(text = "血糖 ${String.format("%.1f", record.bloodSugar)} · 体重 ${String.format("%.1f", record.weight)}kg · BMI ${String.format("%.1f", record.bmi)}",
                                        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    if (record.temperature > 0) {
                                        Text(text = "体温 ${record.temperature}°C · 血氧 ${record.oxygenSat}%",
                                            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                Text(text = record.date,
                                    style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                IconButton(onClick = { onRemoveRecord(record.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "删除", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                            if (record.tags.isNotEmpty() && record.tags.first() != HealthTag.NONE) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    record.tags.take(4).forEach { tag ->
                                        SuggestionChip(onClick = {},
                                            label = { Text("${tag.emoji} ${tag.displayName}", style = MaterialTheme.typography.labelSmall) },
                                            modifier = Modifier.height(28.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }

    if (showAddDialog) {
        HealthRecordDialog(onDismiss = { showAddDialog = false },
            onSave = { record -> onAddRecord(record); showAddDialog = false })
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HealthRecordDialog(onDismiss: () -> Unit, onSave: (HealthRecord) -> Unit) {
    var systolic by remember { mutableStateOf("") }
    var diastolic by remember { mutableStateOf("") }
    var heartRate by remember { mutableStateOf("") }
    var bloodSugar by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var temperature by remember { mutableStateOf("") }
    var oxygen by remember { mutableStateOf("") }
    var waist by remember { mutableStateOf("") }
    var selectedTags by remember { mutableStateOf(setOf<HealthTag>()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("记录健康指标") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = systolic, onValueChange = { systolic = it.filter { c -> c.isDigit() } }, label = { Text("收缩压") }, modifier = Modifier.weight(1f), singleLine = true)
                    OutlinedTextField(value = diastolic, onValueChange = { diastolic = it.filter { c -> c.isDigit() } }, label = { Text("舒张压") }, modifier = Modifier.weight(1f), singleLine = true)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = heartRate, onValueChange = { heartRate = it.filter { c -> c.isDigit() } }, label = { Text("心率") }, modifier = Modifier.weight(1f), singleLine = true)
                    OutlinedTextField(value = bloodSugar, onValueChange = { bloodSugar = it.filter { c -> c.isDigit() || c == '.' } }, label = { Text("血糖") }, modifier = Modifier.weight(1f), singleLine = true)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = weight, onValueChange = { weight = it.filter { c -> c.isDigit() || c == '.' } }, label = { Text("体重 kg") }, modifier = Modifier.weight(1f), singleLine = true)
                    OutlinedTextField(value = temperature, onValueChange = { temperature = it.filter { c -> c.isDigit() || c == '.' } }, label = { Text("体温 °C") }, modifier = Modifier.weight(1f), singleLine = true)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = oxygen, onValueChange = { oxygen = it.filter { c -> c.isDigit() } }, label = { Text("血氧 %") }, modifier = Modifier.weight(1f), singleLine = true)
                    OutlinedTextField(value = waist, onValueChange = { waist = it.filter { c -> c.isDigit() } }, label = { Text("腰围 cm") }, modifier = Modifier.weight(1f), singleLine = true)
                }
                Text("标签", style = MaterialTheme.typography.labelMedium)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    HealthTag.values().filter { it != HealthTag.NONE }.forEach { tag ->
                        FilterChip(selected = selectedTags.contains(tag),
                            onClick = { selectedTags = if (selectedTags.contains(tag)) selectedTags - tag else selectedTags + tag },
                            label = { Text("${tag.emoji} ${tag.displayName}", style = MaterialTheme.typography.labelSmall) })
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSave(HealthRecord(
                    systolic = systolic.toIntOrNull() ?: 0,
                    diastolic = diastolic.toIntOrNull() ?: 0,
                    heartRate = heartRate.toIntOrNull() ?: 0,
                    bloodSugar = bloodSugar.toFloatOrNull() ?: 0f,
                    weight = weight.toFloatOrNull() ?: 0f,
                    temperature = temperature.toFloatOrNull() ?: 0f,
                    oxygenSat = oxygen.toIntOrNull() ?: 0,
                    waist = waist.toIntOrNull() ?: 0,
                    tags = selectedTags.toList()
                ))
            }) { Text("保存") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } }
    )
}
