package com.example.model

data class MacroNutrient(
    val name: String,
    val current: Int,
    val goal: Int,
    val unit: String = "g",
    val colorHex: Long = 0xFFCEFD27
)

data class MealItem(
    val id: String,
    val name: String,
    val mealType: String, // Breakfast, Lunch, Dinner, Snack
    val time: String,
    val calories: Int,
    val protein: Int,
    val carbs: Int = 40,
    val fats: Int = 14,
    val isEaten: Boolean = false,
    val imageAsset: String = "",
    val ingredients: List<String> = emptyList(),
    val instructions: List<String> = emptyList(),
    val isFavorite: Boolean = false
) {
    val fullAssetUrl: String
        get() = if (imageAsset.isNotEmpty()) "file:///android_asset/$imageAsset" else ""
}

data class RecipeItem(
    val id: String,
    val title: String,
    val prepTimeMinutes: Int,
    val calories: Int,
    val difficulty: String,
    val category: String, // Breakfast, Lunch, Dinner, Snack
    val protein: Int = 30,
    val carbs: Int = 35,
    val fats: Int = 12,
    val imageAsset: String = "",
    val ingredients: List<String> = listOf("Fresh greens", "Grilled chicken", "Olive oil", "Quinoa"),
    val instructions: List<String> = emptyList(),
    val isFavorite: Boolean = false
) {
    val fullAssetUrl: String
        get() = if (imageAsset.isNotEmpty()) "file:///android_asset/$imageAsset" else ""
}

data class DailyNutritionTrend(
    val day: String,
    val fullDayName: String = "",
    val calories: Int,
    val calorieGoal: Int = 2400,
    val protein: Int,
    val proteinGoal: Int = 160,
    val isToday: Boolean = false
) {
    val calorieGoalMet: Boolean
        get() = calories in (calorieGoal - 200)..(calorieGoal + 200) || calories >= calorieGoal
    val proteinGoalMet: Boolean
        get() = protein >= proteinGoal
}

enum class NutritionTrendMetric {
    COMBINED,
    CALORIES,
    PROTEIN
}

