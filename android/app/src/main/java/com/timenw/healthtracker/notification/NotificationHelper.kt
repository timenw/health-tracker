package com.timenw.healthtracker.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat

object NotificationHelper {
    private const val CHANNEL_ID = "health_tracker_channel"
    private const val CHANNEL_NAME = "健康提醒"
    fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT).apply { description = "测了么提醒" }
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }
    fun sendMeasurementReminder(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("🩺 该测健康指标了")
            .setContentText("记录今天的血压、心率、血糖等健康数据")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT).setAutoCancel(true).build()
        manager.notify(6001, notification)
    }
}
