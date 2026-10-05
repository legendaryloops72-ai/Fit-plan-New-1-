package com.example.notifications

enum class ReminderType(
    val id: Int,
    val defaultTitle: String,
    val defaultMessage: String,
    val defaultHour: Int,
    val defaultMinute: Int,
    val categoryLabel: String
) {
    WORKOUT(
        id = 1001,
        defaultTitle = "Time to Crush Your Workout! 💪",
        defaultMessage = "Keep your streak alive! Today's workout session is ready for you.",
        defaultHour = 7,
        defaultMinute = 0,
        categoryLabel = "Workout Reminder"
    ),
    BREAKFAST(
        id = 1002,
        defaultTitle = "Log Your Breakfast 🍳",
        defaultMessage = "Fuel your body right. Track your morning calories and protein.",
        defaultHour = 8,
        defaultMinute = 30,
        categoryLabel = "Breakfast Logging"
    ),
    LUNCH(
        id = 1003,
        defaultTitle = "Log Your Lunch 🥗",
        defaultMessage = "Stay aligned with your daily macro targets. Record your lunch now.",
        defaultHour = 13,
        defaultMinute = 0,
        categoryLabel = "Lunch Logging"
    ),
    DINNER(
        id = 1004,
        defaultTitle = "Log Your Dinner 🍲",
        defaultMessage = "Complete your daily nutrition log and review your remaining macros.",
        defaultHour = 19,
        defaultMinute = 30,
        categoryLabel = "Dinner Logging"
    ),
    HYDRATION(
        id = 1005,
        defaultTitle = "Hydration Check 💧",
        defaultMessage = "Drink a glass of water! Stay hydrated to perform at your best.",
        defaultHour = 11,
        defaultMinute = 0,
        categoryLabel = "Water Tracker"
    )
}

data class ReminderScheduleItem(
    val type: ReminderType,
    val isEnabled: Boolean = true,
    val hour: Int = type.defaultHour,
    val minute: Int = type.defaultMinute
) {
    val formattedTime: String
        get() {
            val period = if (hour >= 12) "PM" else "AM"
            val displayHour = when {
                hour == 0 -> 12
                hour > 12 -> hour - 12
                else -> hour
            }
            val displayMin = if (minute < 10) "0$minute" else "$minute"
            return "$displayHour:$displayMin $period"
        }
}

data class ReminderSettings(
    val reminders: List<ReminderScheduleItem> = ReminderType.entries.map {
        ReminderScheduleItem(type = it, isEnabled = true, hour = it.defaultHour, minute = it.defaultMinute)
    }
)
