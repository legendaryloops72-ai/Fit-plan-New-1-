package com.example.model

data class DailyStepStat(
    val day: String,
    val steps: Int,
    val isPeak: Boolean = false
)

data class GoalItem(
    val title: String,
    val current: Int,
    val target: Int,
    val unit: String,
    val colorHex: Long
)

data class BodyMetric(
    val title: String,
    val current: Float,
    val unit: String,
    val delta: String,
    val isPositive: Boolean
)

data class UserProfile(
    val fullName: String = "Alex Rivers",
    val email: String = "alex.fitness@fitplan.app",
    val streakDays: Int = 14,
    val level: String = "Pro Athlete",
    val weightKg: Float = 76.5f,
    val heightCm: Int = 182,
    val targetWeightKg: Float = 74.0f,
    val dailyStepGoal: Int = 10000,
    val dailyCalorieGoal: Int = 2400,
    val dailyActiveMinutesGoal: Int = 45,
    val notificationsEnabled: Boolean = true,
    val voiceCoachEnabled: Boolean = true,
    val selectedLanguage: String = "en", // "en" or "ar"
    val themeMode: AppThemeMode = AppThemeMode.DARK
)
