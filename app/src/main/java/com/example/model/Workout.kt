package com.example.model

import androidx.compose.ui.graphics.Color

data class ExerciseItem(
    val id: String,
    val name: String,
    val sets: Int = 3,
    val reps: String = "12 reps",
    val durationSec: Int = 45,
    val restSec: Int = 30,
    val instructions: String = "Keep your core engaged and maintain steady breathing throughout.",
    val imageAssetPath: String = ""
) {
    val fullAssetUrl: String
        get() = if (imageAssetPath.isNotEmpty()) "file:///android_asset/$imageAssetPath" else ""
}

data class WorkoutItem(
    val id: String,
    val title: String,
    val category: String,
    val durationMinutes: Int,
    val difficulty: String,
    val calories: Int,
    val exercisesCount: Int = 8,
    val description: String = "",
    val accentColorHex: Long = 0xFFCEFD27,
    val exercises: List<ExerciseItem> = emptyList(),
    val isFavorite: Boolean = false,
    val imageAssetPath: String = ""
) {
    val fullAssetUrl: String
        get() = if (imageAssetPath.isNotEmpty()) "file:///android_asset/$imageAssetPath"
                else exercises.firstOrNull()?.fullAssetUrl ?: ""
}

data class WorkoutProgram(
    val id: String,
    val title: String,
    val durationWeeks: Int = 4,
    val level: String = "Intermediate",
    val progressPercent: Float = 0.45f,
    val subtitle: String = "Build lean muscle & strength"
)
