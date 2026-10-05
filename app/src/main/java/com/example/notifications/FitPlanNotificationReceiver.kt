package com.example.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class FitPlanNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action

        if (action == Intent.ACTION_BOOT_COMPLETED) {
            // Re-register default active schedules after device reboot
            ReminderType.entries.forEach { type ->
                FitPlanNotificationHelper.scheduleDailyReminder(
                    context,
                    ReminderScheduleItem(type = type, isEnabled = true, hour = type.defaultHour, minute = type.defaultMinute)
                )
            }
            return
        }

        if (action == FitPlanNotificationHelper.ACTION_FITPLAN_REMINDER) {
            val typeName = intent.getStringExtra(FitPlanNotificationHelper.EXTRA_REMINDER_TYPE)
            val type = typeName?.let { runCatching { ReminderType.valueOf(it) }.getOrNull() } ?: ReminderType.WORKOUT
            val title = intent.getStringExtra(FitPlanNotificationHelper.EXTRA_TITLE) ?: type.defaultTitle
            val message = intent.getStringExtra(FitPlanNotificationHelper.EXTRA_MESSAGE) ?: type.defaultMessage

            // Display notification
            FitPlanNotificationHelper.showNotification(
                context = context,
                id = type.id,
                title = title,
                message = message
            )

            // Reschedule for next day at the same time
            FitPlanNotificationHelper.scheduleDailyReminder(
                context,
                ReminderScheduleItem(type = type, isEnabled = true, hour = type.defaultHour, minute = type.defaultMinute)
            )
        }
    }
}
