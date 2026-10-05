package com.example.model

enum class ActivityType(val titleResId: String) {
    RUNNING("Running"),
    CYCLING("Cycling"),
    WALKING("Walking"),
    HOME_WORKOUT("Home Workout"),
    YOGA("Yoga"),
    STRETCHING("Stretching")
}

data class GeoPoint(
    val x: Float,
    val y: Float
)

data class ActivityRoute(
    val id: String,
    val name: String,
    val distanceKm: Float,
    val elevationGainMeters: Int,
    val points: List<GeoPoint>
)

data class ActivitySession(
    val id: String,
    val type: ActivityType,
    val date: String,
    val distanceKm: Float,
    val durationMinutes: Int,
    val avgPaceOrSpeed: String,
    val caloriesBurned: Int,
    val routeName: String = "Riverside Park Trail"
)
