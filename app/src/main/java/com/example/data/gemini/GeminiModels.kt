package com.example.data.gemini

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    @Json(name = "contents") val contents: List<GeminiContent>,
    @Json(name = "generationConfig") val generationConfig: GeminiGenerationConfig? = null,
    @Json(name = "systemInstruction") val systemInstruction: GeminiContent? = null
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    @Json(name = "parts") val parts: List<GeminiPart>,
    @Json(name = "role") val role: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiPart(
    @Json(name = "text") val text: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiGenerationConfig(
    @Json(name = "temperature") val temperature: Float? = 0.7f,
    @Json(name = "topP") val topP: Float? = 0.95f,
    @Json(name = "topK") val topK: Int? = 40,
    @Json(name = "responseMimeType") val responseMimeType: String? = "application/json"
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    @Json(name = "candidates") val candidates: List<GeminiCandidate>? = null
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    @Json(name = "content") val content: GeminiContent? = null,
    @Json(name = "finishReason") val finishReason: String? = null
)

// Parsed Domain Models for Health & Workout Recommendations
@JsonClass(generateAdapter = true)
data class GeminiExerciseItem(
    @Json(name = "name") val name: String,
    @Json(name = "sets") val sets: Int = 3,
    @Json(name = "reps") val reps: String = "12 reps",
    @Json(name = "durationSec") val durationSec: Int = 45,
    @Json(name = "restSec") val restSec: Int = 20,
    @Json(name = "instructions") val instructions: String = "Maintain proper form.",
    @Json(name = "imageAssetPath") val imageAssetPath: String? = null
) {
    val fullAssetUrl: String
        get() = if (!imageAssetPath.isNullOrEmpty()) {
            "file:///android_asset/$imageAssetPath"
        } else {
            com.example.data.ExerciseAssetRegistry.getAssetUrlForName(name)
        }
}

@JsonClass(generateAdapter = true)
data class GeminiHealthRecommendations(
    @Json(name = "headline") val headline: String = "Today's AI Health & Fitness Blueprint",
    @Json(name = "hydrationTip") val hydrationTip: String = "Drink 250ml before your workout to optimize muscle performance.",
    @Json(name = "nutritionTip") val nutritionTip: String = "Focus on lean protein and complex carbs to hit your daily macro target.",
    @Json(name = "movementTip") val movementTip: String = "Take a 10-minute active recovery stroll to meet your daily step goal.",
    @Json(name = "mindsetQuote") val mindsetQuote: String = "Consistency is the DNA of mastery.",
    @Json(name = "workoutTitle") val workoutTitle: String = "AI Metabolic Booster & Core",
    @Json(name = "workoutCategory") val workoutCategory: String = "Full Body",
    @Json(name = "workoutDurationMinutes") val workoutDurationMinutes: Int = 25,
    @Json(name = "workoutDifficulty") val workoutDifficulty: String = "Intermediate",
    @Json(name = "workoutCalories") val workoutCalories: Int = 260,
    @Json(name = "workoutRationale") val workoutRationale: String = "Designed specifically to bridge your remaining daily calorie deficit and boost metabolic burn.",
    @Json(name = "exercises") val exercises: List<GeminiExerciseItem> = emptyList()
)

data class AiCoachMessage(
    val id: String,
    val sender: MessageSender,
    val text: String,
    val timestamp: String = "Just now"
)

enum class MessageSender {
    USER,
    AI_COACH
}
