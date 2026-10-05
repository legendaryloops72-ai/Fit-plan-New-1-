package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.AppThemeMode

object FitPlanPreferences {
    private const val PREFS_NAME = "fitplan_user_preferences"
    private const val KEY_THEME_MODE = "pref_theme_mode"
    private const val KEY_LANGUAGE = "pref_language"
    private const val KEY_VOICE_COACH = "pref_voice_coach"
    private const val KEY_NOTIFICATIONS = "pref_notifications"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getThemeMode(context: Context?): AppThemeMode {
        if (context == null) return AppThemeMode.DARK
        val savedKey = getPrefs(context).getString(KEY_THEME_MODE, AppThemeMode.DARK.storageKey)
        return AppThemeMode.fromStorageKey(savedKey)
    }

    fun saveThemeMode(context: Context?, mode: AppThemeMode) {
        if (context == null) return
        getPrefs(context).edit().putString(KEY_THEME_MODE, mode.storageKey).apply()
    }

    fun getLanguage(context: Context?): String {
        if (context == null) return "en"
        val saved = getPrefs(context).getString(KEY_LANGUAGE, null)
        if (saved != null) return saved
        val systemLang = java.util.Locale.getDefault().language
        return if (systemLang == "ar" || systemLang == "es") systemLang else "en"
    }

    fun saveLanguage(context: Context?, lang: String) {
        if (context == null) return
        getPrefs(context).edit().putString(KEY_LANGUAGE, lang).apply()
    }

    fun isVoiceCoachEnabled(context: Context?): Boolean {
        if (context == null) return true
        return getPrefs(context).getBoolean(KEY_VOICE_COACH, true)
    }

    fun saveVoiceCoachEnabled(context: Context?, enabled: Boolean) {
        if (context == null) return
        getPrefs(context).edit().putBoolean(KEY_VOICE_COACH, enabled).apply()
    }
}
