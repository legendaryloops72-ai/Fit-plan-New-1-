package com.example.notifications

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R
import java.util.Calendar

object FitPlanNotificationHelper {

    const val CHANNEL_ID = "fitplan_reminders_channel"
    const val CHANNEL_NAME = "FITPLAN Reminders & Alerts"
    const val EXTRA_REMINDER_TYPE = "extra_reminder_type"
    const val EXTRA_TITLE = "extra_title"
    const val EXTRA_MESSAGE = "extra_message"
    const val ACTION_FITPLAN_REMINDER = "com.example.FITPLAN_REMINDER"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                importance
            ).apply {
                description = "Daily reminders to log workouts, meals and hydration"
                enableVibration(true)
                enableLights(true)
            }

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            notificationManager?.createNotificationChannel(channel)
        }
    }

    fun showNotification(
        context: Context,
        id: Int,
        title: String,
        message: String
    ) {
        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        val notificationManager = NotificationManagerCompat.from(context)
        try {
            notificationManager.notify(id, builder.build())
        } catch (e: SecurityException) {
            // Handled when POST_NOTIFICATIONS permission not granted
        }
    }

    fun scheduleDailyReminder(
        context: Context,
        item: ReminderScheduleItem
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val intent = Intent(context, FitPlanNotificationReceiver::class.java).apply {
            action = ACTION_FITPLAN_REMINDER
            putExtra(EXTRA_REMINDER_TYPE, item.type.name)
            putExtra(EXTRA_TITLE, item.type.defaultTitle)
            putExtra(EXTRA_MESSAGE, item.type.defaultMessage)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            item.type.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (!item.isEnabled) {
            alarmManager.cancel(pendingIntent)
            return
        }

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, item.hour)
            set(Calendar.MINUTE, item.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            // If time is before now, schedule for next day
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } else {
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        }
    }

    fun cancelReminder(context: Context, type: ReminderType) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, FitPlanNotificationReceiver::class.java).apply {
            action = ACTION_FITPLAN_REMINDER
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            type.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    fun sendTestNotification(context: Context, type: ReminderType) {
        showNotification(
            context = context,
            id = type.id,
            title = type.defaultTitle,
            message = type.defaultMessage
        )
    }
}
