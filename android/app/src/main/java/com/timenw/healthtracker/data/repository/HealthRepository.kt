package com.timenw.healthtracker.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.timenw.healthtracker.data.model.*
import java.time.LocalDate

class HealthRepository(private val context: Context) {
    private val prefs = context.getSharedPreferences("health_tracker", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun getHealthRecords(date: LocalDate = LocalDate.now()): List<HealthRecord> {
        val key = "healths_${date}"
        val json = prefs.getString(key, "[]") ?: "[]"
        val type = object : TypeToken<List<HealthRecord>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    fun addHealthRecord(record: HealthRecord) {
        val records = getHealthRecords(LocalDate.parse(record.date)).toMutableList()
        records.add(record)
        saveHealthRecords(records, LocalDate.parse(record.date))
    }

    fun removeHealthRecord(id: Long, date: LocalDate = LocalDate.now()) {
        val records = getHealthRecords(date).toMutableList()
        records.removeAll { it.id == id }
        saveHealthRecords(records, date)
    }

    private fun saveHealthRecords(records: List<HealthRecord>, date: LocalDate) {
        prefs.edit().putString("healths_${date}", gson.toJson(records)).apply()
    }

    fun getDailySummary(date: LocalDate = LocalDate.now()): DailyHealthSummary {
        val records = getHealthRecords(date)
        if (records.isEmpty()) return DailyHealthSummary(date = date.toString())
        return DailyHealthSummary(
            date = date.toString(),
            avgSystolic = records.map { it.systolic }.average().toInt(),
            avgDiastolic = records.map { it.diastolic }.average().toInt(),
            avgHeartRate = records.map { it.heartRate }.average().toInt(),
            avgBloodSugar = records.map { it.bloodSugar }.average().toFloat(),
            latestWeight = records.lastOrNull()?.weight ?: 0f,
            latestBmi = records.lastOrNull()?.bmi ?: 0f,
            avgOxygen = records.map { it.oxygenSat }.average().toInt(),
            recordCount = records.size,
            records = records
        )
    }

    fun getWeeklyData(): List<DailyHealthSummary> {
        val today = LocalDate.now()
        return (0..6).map { daysAgo -> getDailySummary(today.minusDays(daysAgo.toLong())) }.reversed()
    }

    fun getSettings(): UserSettings {
        val json = prefs.getString("settings", null)
        return if (json != null) gson.fromJson(json, UserSettings::class.java) else UserSettings()
    }

    fun saveSettings(settings: UserSettings) {
        prefs.edit().putString("settings", gson.toJson(settings)).apply()
    }
}
