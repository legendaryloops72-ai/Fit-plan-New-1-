package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FitPlanRepository {

    // Auth State
    private val _isLoggedIn = MutableStateFlow(true)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    // User Profile & Preferences
    private val _themeMode = MutableStateFlow(AppThemeMode.DARK)
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    private val _userProfile = MutableStateFlow(UserProfile(themeMode = AppThemeMode.DARK))
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    // Workouts
    private val _workouts = MutableStateFlow(initialWorkouts())
    val workouts: StateFlow<List<WorkoutItem>> = _workouts.asStateFlow()

    private val _programs = MutableStateFlow(initialPrograms())
    val programs: StateFlow<List<WorkoutProgram>> = _programs.asStateFlow()

    // Nutrition
    private val _meals = MutableStateFlow(initialMeals())
    val meals: StateFlow<List<MealItem>> = _meals.asStateFlow()

    private val _waterGlasses = MutableStateFlow(5) // 5 out of 8 glasses
    val waterGlasses: StateFlow<Int> = _waterGlasses.asStateFlow()

    private val _macros = MutableStateFlow(initialMacros())
    val macros: StateFlow<List<MacroNutrient>> = _macros.asStateFlow()

    private val _recipes = MutableStateFlow(initialRecipes())
    val recipes: StateFlow<List<RecipeItem>> = _recipes.asStateFlow()

    // Progress
    private val _todaySteps = MutableStateFlow(8420)
    val todaySteps: StateFlow<Int> = _todaySteps.asStateFlow()

    private val _todayCalories = MutableStateFlow(1850)
    val todayCalories: StateFlow<Int> = _todayCalories.asStateFlow()

    private val _todayActiveMinutes = MutableStateFlow(42)
    val todayActiveMinutes: StateFlow<Int> = _todayActiveMinutes.asStateFlow()

    private val _weeklyStats = MutableStateFlow(initialWeeklyStats())
    val weeklyStats: StateFlow<List<DailyStepStat>> = _weeklyStats.asStateFlow()

    private val _weeklyNutritionTrends = MutableStateFlow(initialWeeklyNutritionTrends())
    val weeklyNutritionTrends: StateFlow<List<DailyNutritionTrend>> = _weeklyNutritionTrends.asStateFlow()

    private val _bodyMetrics = MutableStateFlow(initialBodyMetrics())
    val bodyMetrics: StateFlow<List<BodyMetric>> = _bodyMetrics.asStateFlow()

    // Activities
    private val _activitySessions = MutableStateFlow(initialActivitySessions())
    val activitySessions: StateFlow<List<ActivitySession>> = _activitySessions.asStateFlow()

    // Reminders
    private val _reminderSettings = MutableStateFlow(com.example.notifications.ReminderSettings())
    val reminderSettings: StateFlow<com.example.notifications.ReminderSettings> = _reminderSettings.asStateFlow()

    // Actions
    fun toggleReminder(type: com.example.notifications.ReminderType, isEnabled: Boolean) {
        _reminderSettings.update { settings ->
            settings.copy(
                reminders = settings.reminders.map {
                    if (it.type == type) it.copy(isEnabled = isEnabled) else it
                }
            )
        }
    }

    fun updateReminderTime(type: com.example.notifications.ReminderType, hour: Int, minute: Int) {
        _reminderSettings.update { settings ->
            settings.copy(
                reminders = settings.reminders.map {
                    if (it.type == type) it.copy(hour = hour, minute = minute) else it
                }
            )
        }
    }

    fun setLoggedIn(loggedIn: Boolean) {
        _isLoggedIn.value = loggedIn
    }

    fun toggleMealEaten(mealId: String) {
        _meals.update { list ->
            list.map { if (it.id == mealId) it.copy(isEaten = !it.isEaten) else it }
        }
    }

    fun toggleMealFavorite(mealId: String) {
        _meals.update { list ->
            list.map { if (it.id == mealId) it.copy(isFavorite = !it.isFavorite) else it }
        }
    }

    fun toggleRecipeFavorite(recipeId: String) {
        _recipes.update { list ->
            list.map { if (it.id == recipeId) it.copy(isFavorite = !it.isFavorite) else it }
        }
    }

    fun addMeal(meal: MealItem) {
        _meals.update { current ->
            if (current.any { it.id == meal.id }) {
                current.map { if (it.id == meal.id) meal else it }
            } else {
                current + meal
            }
        }
    }

    fun addWaterGlass() {
        if (_waterGlasses.value < 16) {
            _waterGlasses.value += 1
        }
    }

    fun removeWaterGlass() {
        if (_waterGlasses.value > 0) {
            _waterGlasses.value -= 1
        }
    }

    fun setWaterGlasses(count: Int) {
        _waterGlasses.value = count.coerceIn(0, 16)
    }

    fun resetWater() {
        _waterGlasses.value = 0
    }

    fun initPreferences(context: android.content.Context) {
        val savedTheme = FitPlanPreferences.getThemeMode(context)
        val savedLang = FitPlanPreferences.getLanguage(context)
        val savedVoice = FitPlanPreferences.isVoiceCoachEnabled(context)

        _themeMode.value = savedTheme
        _userProfile.update {
            it.copy(
                themeMode = savedTheme,
                selectedLanguage = savedLang,
                voiceCoachEnabled = savedVoice
            )
        }
    }

    fun setThemeMode(mode: AppThemeMode, context: android.content.Context? = null) {
        _themeMode.value = mode
        _userProfile.update { it.copy(themeMode = mode) }
        FitPlanPreferences.saveThemeMode(context, mode)
    }

    fun toggleTheme(context: android.content.Context? = null) {
        val nextMode = if (_themeMode.value == AppThemeMode.DARK) AppThemeMode.LIGHT else AppThemeMode.DARK
        setThemeMode(nextMode, context)
    }

    fun updateLanguage(langCode: String, context: android.content.Context? = null) {
        _userProfile.update { it.copy(selectedLanguage = langCode) }
        FitPlanPreferences.saveLanguage(context, langCode)
    }

    fun toggleVoiceCoach(context: android.content.Context? = null) {
        val updated = !_userProfile.value.voiceCoachEnabled
        _userProfile.update { it.copy(voiceCoachEnabled = updated) }
        FitPlanPreferences.saveVoiceCoachEnabled(context, updated)
    }

    fun updateProfile(name: String, email: String, weight: Float) {
        _userProfile.update { it.copy(fullName = name, email = email, weightKg = weight) }
    }

    fun addWorkout(workout: WorkoutItem) {
        _workouts.update { currentList ->
            listOf(workout) + currentList.filter { it.id != workout.id }
        }
    }

    fun addActivitySession(session: ActivitySession) {
        _activitySessions.update { listOf(session) + it }
        _todaySteps.value += (session.distanceKm * 1350).toInt()
        _todayCalories.value += session.caloriesBurned
        _todayActiveMinutes.value += session.durationMinutes
    }

    companion object {
        val instance by lazy { FitPlanRepository() }

        private fun initialWorkouts(): List<WorkoutItem> = listOf(
            WorkoutItem(
                id = "w1",
                title = "Home Workout",
                category = "Full Body",
                durationMinutes = 25,
                difficulty = "All Levels",
                calories = 240,
                exercisesCount = 7,
                description = "High energy bodyweight workout designed to burn calories and tone muscle without any equipment.",
                accentColorHex = 0xFFCEFD27,
                imageAssetPath = "exercises/home_workout/push_ups.webp",
                exercises = listOf(
                    ExerciseItem("e1", "Push-ups", 3, "15 reps", 40, 25, "Keep back flat and elbows at 45 degrees.", "exercises/home_workout/push_ups.webp"),
                    ExerciseItem("e2", "Bodyweight Squats", 3, "20 reps", 45, 20, "Drive through heels, knees tracking toes.", "exercises/home_workout/bodyweight_squats.webp"),
                    ExerciseItem("e3", "Plank Hold", 3, "45 sec", 45, 30, "Engage core, do not let hips sag.", "exercises/home_workout/plank.webp"),
                    ExerciseItem("e4", "Mountain Climbers", 3, "30 sec", 30, 20, "Drive knees rapidly toward chest.", "exercises/home_workout/mountain_climbers.webp"),
                    ExerciseItem("e5", "Lunges", 3, "12 each leg", 50, 25, "Keep torso upright and step firmly.", "exercises/home_workout/lunges.webp"),
                    ExerciseItem("e6", "Jumping Jacks", 3, "45 sec", 45, 15, "Stay light on your feet.", "exercises/home_workout/jumping_jacks.webp"),
                    ExerciseItem("e7", "Glute Bridges", 3, "15 reps", 40, 20, "Squeeze glutes at the peak hold.", "exercises/home_workout/glute_bridges.webp")
                )
            ),
            WorkoutItem(
                id = "w2",
                title = "Yoga",
                category = "Yoga",
                durationMinutes = 35,
                difficulty = "Intermediate",
                calories = 180,
                exercisesCount = 6,
                description = "Harmonize breath and flow with restorative postures that boost balance and mental clarity.",
                accentColorHex = 0xFFC59BFF,
                imageAssetPath = "exercises/yoga/sun_salutation_a.webp",
                exercises = listOf(
                    ExerciseItem("y1", "Sun Salutation A", 3, "Flow", 60, 20, "Inhale upward reach, exhale fold forward.", "exercises/yoga/sun_salutation_a.webp"),
                    ExerciseItem("y2", "Downward-Facing Dog", 3, "5 Breaths", 45, 15, "Lengthen spine, heels reaching to floor.", "exercises/yoga/downward_facing_dog.webp"),
                    ExerciseItem("y3", "Warrior II", 3, "30 sec each side", 60, 20, "Strong front knee bend, gaze over front hand.", "exercises/yoga/warrior_ii.webp"),
                    ExerciseItem("y4", "Triangle Pose", 3, "30 sec each side", 60, 20, "Open chest toward the ceiling.", "exercises/yoga/triangle_pose.webp"),
                    ExerciseItem("y5", "Tree Pose", 3, "45 sec each side", 90, 15, "Find a focal point to stabilize balance.", "exercises/yoga/tree_pose.webp"),
                    ExerciseItem("y6", "Child's Pose", 2, "60 sec", 60, 20, "Sink hips back, arms stretched forward.", "exercises/yoga/childs_pose.webp")
                )
            ),
            WorkoutItem(
                id = "w3",
                title = "Stretching",
                category = "Stretching",
                durationMinutes = 20,
                difficulty = "Beginner",
                calories = 110,
                exercisesCount = 5,
                description = "Full body mobility session to eliminate tightness, improve joint health and prevent injury.",
                accentColorHex = 0xFF00D2D3,
                imageAssetPath = "exercises/stretching/cat_cow.webp",
                exercises = listOf(
                    ExerciseItem("s1", "Neck & Shoulder Rolls", 2, "10 each side", 30, 10, "Gentle smooth circular motions.", "exercises/stretching/neck_shoulder_rolls.webp"),
                    ExerciseItem("s2", "Cat-Cow Stretch", 3, "10 cycles", 45, 15, "Arch and round back in sync with breath.", "exercises/stretching/cat_cow.webp"),
                    ExerciseItem("s3", "Seated Hamstring Stretch", 3, "30 sec", 60, 15, "Hinge at hips, reach toward toes.", "exercises/stretching/hamstring_stretch.webp"),
                    ExerciseItem("s4", "Hip Flexor Stretch", 2, "30 sec each", 60, 15, "Tuck pelvis and gently push forward.", "exercises/stretching/hip_flexor_stretch.webp"),
                    ExerciseItem("s5", "Chest Opener", 2, "30 sec", 30, 15, "Clasp hands behind back and expand ribcage.", "exercises/stretching/chest_opener.webp")
                )
            ),
            WorkoutItem(
                id = "w4",
                title = "Morning Move",
                category = "Morning Move",
                durationMinutes = 15,
                difficulty = "All Levels",
                calories = 130,
                exercisesCount = 5,
                description = "Energize your morning in 15 minutes. Boost blood circulation and start your day with focus.",
                accentColorHex = 0xFFFF9F43,
                imageAssetPath = "exercises/morning_move/gentle_arm_swings.webp",
                exercises = listOf(
                    ExerciseItem("m1", "Gentle Arm Swings", 2, "30 sec", 30, 10, "Loosen shoulder joints and upper torso.", "exercises/morning_move/gentle_arm_swings.webp"),
                    ExerciseItem("m2", "High Knees March", 2, "45 sec", 45, 15, "Elevate heart rate gradually.", "exercises/morning_move/high_knee_march.webp"),
                    ExerciseItem("m3", "Side Lunges", 2, "10 each side", 40, 15, "Warm up adductors and hips.", "exercises/morning_move/side_lunges.webp"),
                    ExerciseItem("m4", "Torso Twists", 2, "30 sec", 30, 10, "Activate spinal rotation.", "exercises/morning_move/torso_twists.webp"),
                    ExerciseItem("m5", "Calf Raises", 3, "20 reps", 30, 10, "Contract calves at peak.", "exercises/morning_move/calf_raises.webp")
                )
            ),
            WorkoutItem(
                id = "w5",
                title = "HIIT Cardio",
                category = "HIIT",
                durationMinutes = 30,
                difficulty = "Advanced",
                calories = 380,
                exercisesCount = 5,
                description = "High Intensity Interval Training to torch maximum calories and improve cardiovascular VO2 max.",
                accentColorHex = 0xFFFF4757,
                imageAssetPath = "exercises/hiit/burpees.webp",
                exercises = listOf(
                    ExerciseItem("h1", "Burpees", 4, "45 sec on", 45, 15, "Explosive jump at the top.", "exercises/hiit/burpees.webp"),
                    ExerciseItem("h2", "Speed Skaters", 4, "45 sec on", 45, 15, "Lateral bounding with soft landing.", "exercises/hiit/speed_skaters.webp"),
                    ExerciseItem("h3", "Jump Squats", 4, "40 sec on", 40, 20, "Power through legs with arm swing.", "exercises/hiit/jump_squats.webp"),
                    ExerciseItem("h4", "Sprint in Place", 4, "30 sec MAX", 30, 30, "Maximum effort fast feet.", "exercises/hiit/sprint_in_place.webp"),
                    ExerciseItem("h5", "Tuck Jumps", 3, "30 sec", 30, 30, "Knees up to chest level.", "exercises/hiit/tuck_jumps.webp")
                )
            ),
            WorkoutItem(
                id = "w6",
                title = "Relaxing Yoga",
                category = "Yoga",
                durationMinutes = 25,
                difficulty = "Beginner",
                calories = 120,
                exercisesCount = 5,
                description = "Slow down your breath, release muscle tension, and prepare your nervous system for deep restorative sleep.",
                accentColorHex = 0xFFC59BFF,
                imageAssetPath = "exercises/relaxing_yoga/deep_belly_breathing.webp",
                exercises = listOf(
                    ExerciseItem("r1", "Deep Belly Breathing", 1, "3 min", 180, 0, "Calm parasympathetic nervous system.", "exercises/relaxing_yoga/deep_belly_breathing.webp"),
                    ExerciseItem("r2", "Reclining Butterfly", 2, "2 min", 120, 15, "Soles of feet together, knees drop wide.", "exercises/relaxing_yoga/reclining_butterfly.webp"),
                    ExerciseItem("r3", "Supine Spinal Twist", 2, "90 sec each", 180, 15, "Gentle release for lower lumbar spine.", "exercises/relaxing_yoga/supine_spinal_twist.webp"),
                    ExerciseItem("r4", "Legs Up The Wall", 1, "4 min", 240, 0, "Restores venous return and calms the mind.", "exercises/relaxing_yoga/legs_up_the_wall.webp"),
                    ExerciseItem("r5", "Savasana", 1, "5 min", 300, 0, "Complete physical and mental surrender.", "exercises/relaxing_yoga/savasana.webp")
                )
            )
        )

        private fun initialPrograms(): List<WorkoutProgram> = listOf(
            WorkoutProgram("p1", "Full Body Shred", 4, "Intermediate", 0.65f, "Target fat loss & muscle tone"),
            WorkoutProgram("p2", "Core Strength Mastery", 3, "All Levels", 0.40f, "Six-pack definition & stability"),
            WorkoutProgram("p3", "Mobility & Flow", 2, "Beginner", 0.85f, "Peak flexibility & posture repair")
        )

        private fun initialMeals(): List<MealItem> = listOf(
            MealItem(
                id = "m1",
                name = "Oatmeal with Blueberries & Whey",
                mealType = "Breakfast",
                time = "08:00 AM",
                calories = 420,
                protein = 28,
                carbs = 55,
                fats = 8,
                isEaten = true,
                imageAsset = "nutrition/oatmeal_blueberries.webp",
                ingredients = listOf(
                    "1 cup rolled whole oats",
                    "1 scoop premium vanilla whey protein",
                    "1/2 cup fresh wild blueberries",
                    "1 cup unsweetened almond milk",
                    "1 tbsp organic chia seeds",
                    "1/2 tsp Ceylon cinnamon"
                ),
                instructions = listOf(
                    "Simmer rolled oats in almond milk over medium heat for 4-5 minutes until smooth and creamy.",
                    "Remove from stove and allow to cool slightly before gently whisking in vanilla whey protein.",
                    "Top with fresh blueberries, chia seeds, and a fragrant dust of cinnamon."
                )
            ),
            MealItem(
                id = "m2",
                name = "Grilled Chicken Breast with Quinoa",
                mealType = "Lunch",
                time = "01:00 PM",
                calories = 580,
                protein = 52,
                carbs = 48,
                fats = 14,
                isEaten = true,
                imageAsset = "nutrition/grilled_chicken_quinoa.webp",
                ingredients = listOf(
                    "200g lean boneless chicken breast",
                    "3/4 cup cooked white and red quinoa",
                    "1 cup tender steamed broccoli florets",
                    "1 tbsp extra virgin olive oil",
                    "1 clove minced garlic & lemon wedge",
                    "Cracked sea salt, oregano & pepper"
                ),
                instructions = listOf(
                    "Marinate chicken with garlic, olive oil, dried oregano, sea salt, and black pepper.",
                    "Grill on medium-high heat for 6-7 minutes each side until golden char marks form.",
                    "Serve hot alongside warm fluffy quinoa and vibrant steamed broccoli."
                )
            ),
            MealItem(
                id = "m3",
                name = "Greek Yogurt & Almonds",
                mealType = "Snack",
                time = "04:30 PM",
                calories = 220,
                protein = 18,
                carbs = 12,
                fats = 11,
                isEaten = false,
                imageAsset = "nutrition/greek_yogurt_almonds.webp",
                ingredients = listOf(
                    "170g non-fat authentic Greek yogurt",
                    "18g raw sliced California almonds",
                    "1 tsp raw wildflower honey",
                    "1 tsp golden flaxseed meal",
                    "Pinch of fresh ground nutmeg"
                ),
                instructions = listOf(
                    "Ladle thick Greek yogurt into a chilled ceramic bowl.",
                    "Evenly scatter golden roasted almond slices and flaxseeds on top.",
                    "Drizzle raw honey in a spiral and serve immediately."
                )
            ),
            MealItem(
                id = "m4",
                name = "Baked Salmon with Steamed Asparagus",
                mealType = "Dinner",
                time = "07:30 PM",
                calories = 630,
                protein = 46,
                carbs = 16,
                fats = 32,
                isEaten = false,
                imageAsset = "nutrition/baked_salmon_asparagus.webp",
                ingredients = listOf(
                    "220g Atlantic wild salmon fillet",
                    "8 tender spears fresh green asparagus",
                    "1 tbsp pure avocado oil",
                    "Fresh dill sprigs & organic lemon slices",
                    "Smoked paprika, flaky salt & black pepper"
                ),
                instructions = listOf(
                    "Preheat oven to 200°C (400°F) and prepare parchment-lined baking tray.",
                    "Season salmon and trimmed asparagus spears with oil, lemon slices, dill, and seasonings.",
                    "Bake for 12-14 minutes until salmon flakes tenderly under a fork."
                )
            )
        )

        private fun initialMacros(): List<MacroNutrient> = listOf(
            MacroNutrient("Protein", 126, 160, "g", 0xFFCEFD27),
            MacroNutrient("Carbs", 115, 210, "g", 0xFF00D2D3),
            MacroNutrient("Fats", 53, 70, "g", 0xFFFF9F43)
        )

        private fun initialRecipes(): List<RecipeItem> = listOf(
            RecipeItem(
                id = "r1",
                title = "Avocado & Egg Power Bowl",
                prepTimeMinutes = 15,
                calories = 380,
                difficulty = "Easy",
                category = "Breakfast",
                protein = 22,
                carbs = 24,
                fats = 20,
                imageAsset = "nutrition/avocado_egg_bowl.webp",
                ingredients = listOf(
                    "2 organic free-range eggs (poached or soft-boiled)",
                    "1/2 ripe Haas avocado, fanned",
                    "1 cup mixed baby spinach and wild rocket",
                    "1 slice toasted whole wheat sourdough",
                    "1 tbsp toasted sesame seeds & red chili flakes"
                ),
                instructions = listOf(
                    "Poach or soft-boil eggs in gently simmering water for 5-6 minutes.",
                    "Arrange fresh greens in a bowl and fan avocado slices alongside.",
                    "Place warm eggs in the center, season with chili flakes, and serve with sourdough."
                )
            ),
            RecipeItem(
                id = "r2",
                title = "Lemon Grilled Salmon",
                prepTimeMinutes = 20,
                calories = 520,
                difficulty = "Medium",
                category = "Dinner",
                protein = 45,
                carbs = 10,
                fats = 28,
                imageAsset = "nutrition/lemon_grilled_salmon.webp",
                ingredients = listOf(
                    "200g fresh salmon fillet",
                    "2 tbsp fresh lemon juice and zest",
                    "1 tbsp fresh chopped Italian parsley",
                    "1 tbsp cold-pressed olive oil",
                    "1 tsp coarse Dijon mustard & black pepper"
                ),
                instructions = listOf(
                    "Whisk together lemon juice, zest, olive oil, Dijon, and chopped parsley.",
                    "Brush mixture over the salmon fillet and let rest for 10 minutes.",
                    "Grill on high heat for 4-5 minutes per side until seared outside and moist inside."
                )
            ),
            RecipeItem(
                id = "r3",
                title = "Mediterranean Quinoa Salad",
                prepTimeMinutes = 12,
                calories = 340,
                difficulty = "Easy",
                category = "Lunch",
                protein = 18,
                carbs = 45,
                fats = 10,
                imageAsset = "nutrition/mediterranean_quinoa_salad.webp",
                ingredients = listOf(
                    "1 cup cooked tri-color organic quinoa",
                    "1/2 cup diced Persian cucumbers",
                    "1/2 cup sweet cherry tomatoes, halved",
                    "30g crumbled sheep milk feta cheese",
                    "2 tbsp pitted Kalamata olives",
                    "1 tbsp extra virgin lemon vinaigrette"
                ),
                instructions = listOf(
                    "Cook quinoa and let cool completely to preserve crunch.",
                    "Toss quinoa with diced cucumbers, cherry tomatoes, and Kalamata olives.",
                    "Drizzle with lemon vinaigrette and sprinkle crumbled feta before serving."
                )
            ),
            RecipeItem(
                id = "r4",
                title = "Protein Peanut Butter Bites",
                prepTimeMinutes = 10,
                calories = 210,
                difficulty = "Quick",
                category = "Snack",
                protein = 12,
                carbs = 20,
                fats = 9,
                imageAsset = "nutrition/protein_peanut_butter_bites.webp",
                ingredients = listOf(
                    "1/2 cup all-natural creamy peanut butter",
                    "1/4 cup pure maple syrup or raw honey",
                    "1 scoop vanilla plant or whey protein isolate",
                    "1/2 cup rolled whole oats",
                    "1 tbsp 70% dark chocolate chips",
                    "1 tbsp chia seeds"
                ),
                instructions = listOf(
                    "In a bowl, mix peanut butter, maple syrup, protein powder, and oats until a cohesive dough forms.",
                    "Fold in chia seeds and mini dark chocolate chips.",
                    "Roll into 1-inch energy balls and chill in refrigerator for 20 minutes."
                )
            )
        )

        private fun initialWeeklyStats(): List<DailyStepStat> = listOf(
            DailyStepStat("Mon", 7200, false),
            DailyStepStat("Tue", 9400, false),
            DailyStepStat("Wed", 8100, false),
            DailyStepStat("Thu", 11200, true), // Peak
            DailyStepStat("Fri", 8900, false),
            DailyStepStat("Sat", 10500, false),
            DailyStepStat("Sun", 8420, false)
        )

        private fun initialWeeklyNutritionTrends(): List<DailyNutritionTrend> = listOf(
            DailyNutritionTrend("Mon", "Monday", 2150, 2400, 142, 160, false),
            DailyNutritionTrend("Tue", "Tuesday", 2380, 2400, 158, 160, false),
            DailyNutritionTrend("Wed", "Wednesday", 1920, 2400, 126, 160, false),
            DailyNutritionTrend("Thu", "Thursday", 2510, 2400, 168, 160, false),
            DailyNutritionTrend("Fri", "Friday", 2290, 2400, 148, 160, false),
            DailyNutritionTrend("Sat", "Saturday", 2620, 2400, 175, 160, false),
            DailyNutritionTrend("Sun", "Sunday (Today)", 2400, 2400, 160, 160, true)
        )

        private fun initialBodyMetrics(): List<BodyMetric> = listOf(
            BodyMetric("Weight", 76.5f, "kg", "-1.2 kg", true),
            BodyMetric("Body Fat", 14.8f, "%", "-0.8%", true),
            BodyMetric("Muscle Mass", 42.1f, "kg", "+0.9 kg", true)
        )

        private fun initialActivitySessions(): List<ActivitySession> = listOf(
            ActivitySession("a1", ActivityType.RUNNING, "Today, 07:15 AM", 5.24f, 28, "5'21\" /km", 390, "Riverside Park Trail"),
            ActivitySession("a2", ActivityType.CYCLING, "Yesterday", 14.80f, 44, "20.2 km/h", 480, "Coastal Highway Loop"),
            ActivitySession("a3", ActivityType.WALKING, "2 days ago", 3.60f, 42, "11'40\" /km", 185, "Sunset Boulevard")
        )
    }
}
