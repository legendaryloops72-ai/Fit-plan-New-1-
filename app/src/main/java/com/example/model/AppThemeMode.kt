package com.example.model

enum class AppThemeMode(val storageKey: String, val title: String) {
    DARK("dark", "Dark Mode"),
    LIGHT("light", "Light Mode"),
    SYSTEM("system", "System Default");

    companion object {
        fun fromStorageKey(key: String?): AppThemeMode = when (key?.lowercase()) {
            "light" -> LIGHT
            "system" -> SYSTEM
            else -> DARK
        }
    }
}
