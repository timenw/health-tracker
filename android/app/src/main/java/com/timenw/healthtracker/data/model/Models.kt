package com.timenw.healthtracker.data.model

import java.time.LocalDate

data class HealthRecord(
    val id: Long = System.currentTimeMillis(),
    val systolic: Int = 120,       // 收缩压 mmHg
    val diastolic: Int = 80,       // 舒张压 mmHg
    val heartRate: Int = 72,       // 心率 bpm
    val bloodSugar: Float = 5.5f,  // 血糖 mmol/L
    val weight: Float = 70f,       // 体重 kg
    val temperature: Float = 36.5f,// 体温 °C
    val oxygenSat: Int = 98,       // 血氧 %
    val waist: Int = 80,           // 腰围 cm
    val height: Int = 170,         // 身高 cm
    val date: String = LocalDate.now().toString(),
    val note: String = "",
    val tags: List<HealthTag> = emptyList()
) {
    val bmi: Float get() = if (height > 0) weight / ((height / 100f) * (height / 100f)) else 0f
    val bpStatus: String get() = when {
        systolic >= 140 || diastolic >= 90 -> "偏高"
        systolic < 90 || diastolic < 60 -> "偏低"
        else -> "正常"
    }
    val hrStatus: String get() = when {
        heartRate > 100 -> "偏快"
        heartRate < 60 -> "偏慢"
        else -> "正常"
    }
    val bsStatus: String get() = when {
        bloodSugar > 7.0f -> "偏高"
        bloodSugar < 3.9f -> "偏低"
        else -> "正常"
    }
    val bmiStatus: String get() = when {
        bmi < 18.5f -> "偏瘦"
        bmi < 24f -> "正常"
        bmi < 28f -> "超重"
        else -> "肥胖"
    }
}

enum class HealthTag(val displayName: String, val emoji: String) {
    FASTING("空腹", "🌅"), AFTER_MEAL("餐后", "🍽️"), MORNING("早晨", "☀️"),
    EVENING("晚间", "🌙"), EXERCISE("运动后", "🏃"), REST("休息", "😴"),
    MEDICATION("服药后", "💊"), NONE("无", "—")
}

data class UserSettings(
    val reminderEnabled: Boolean = true,
    val reminderHour: Int = 9,
    val reminderMinute: Int = 0,
    val heightCm: Int = 170,
    val weightKg: Float = 70f,
    val age: Int = 30
)

data class DailyHealthSummary(
    val date: String = LocalDate.now().toString(),
    val avgSystolic: Int = 0,
    val avgDiastolic: Int = 0,
    val avgHeartRate: Int = 0,
    val avgBloodSugar: Float = 0f,
    val latestWeight: Float = 0f,
    val latestBmi: Float = 0f,
    val avgOxygen: Int = 0,
    val recordCount: Int = 0,
    val records: List<HealthRecord> = emptyList()
)
