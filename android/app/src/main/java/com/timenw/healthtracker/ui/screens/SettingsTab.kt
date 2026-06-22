package com.timenw.healthtracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.timenw.healthtracker.data.model.UserSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTab(settings: UserSettings, onSettingsChanged: (UserSettings) -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        CenterAlignedTopAppBar(title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Settings, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp)); Text("设置", fontWeight = FontWeight.Bold)
            }
        })
        LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item { Spacer(modifier = Modifier.height(8.dp)) }
            item { Text("个人信息", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold) }
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("身高: ${settings.heightCm} cm", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Slider(value = settings.heightCm.toFloat(), onValueChange = { onSettingsChanged(settings.copy(heightCm = it.toInt())) }, valueRange = 140f..200f, steps = 59)
                        Spacer(modifier = Modifier.height(16.dp)); HorizontalDivider(); Spacer(modifier = Modifier.height(16.dp))
                        Text("体重: ${settings.weightKg.toInt()} kg", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Slider(value = settings.weightKg, onValueChange = { onSettingsChanged(settings.copy(weightKg = it)) }, valueRange = 40f..150f, steps = 109)
                        Spacer(modifier = Modifier.height(16.dp)); HorizontalDivider(); Spacer(modifier = Modifier.height(16.dp))
                        Text("年龄: ${settings.age} 岁", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Slider(value = settings.age.toFloat(), onValueChange = { onSettingsChanged(settings.copy(age = it.toInt())) }, valueRange = 10f..80f, steps = 69)
                    }
                }
            }
            item { Text("提醒设置", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold) }
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("测量提醒", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                                Text(text = if (settings.reminderEnabled) "已开启 — ${settings.reminderHour}:${String.format("%02d", settings.reminderMinute)}" else "已关闭",
                                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(checked = settings.reminderEnabled, onCheckedChange = { onSettingsChanged(settings.copy(reminderEnabled = it)) })
                        }
                    }
                }
            }
            item { Text("关于", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold) }
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("测了么", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                        Text("版本 1.0.0", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "一款专为关注健康的人设计的指标记录工具，帮助你追踪血压、心率、血糖等健康数据。",
                            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}
