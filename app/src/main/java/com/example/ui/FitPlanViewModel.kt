package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.FitPlanRepository
import com.example.data.gemini.*
import com.example.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class ScreenDestination {
    object Login : ScreenDestination()
    object SignUp : ScreenDestination()
    object Home : ScreenDestination()
    object Workouts : ScreenDestination()
    object Nutrition : ScreenDestination()
    object Progress : ScreenDestination()
    object Activities : ScreenDestination()
    object More : ScreenDestination()
    object AiCoach : ScreenDestination()

    // Sub-screens directly mapped from mockups
    object HomeWorkout : ScreenDestination()
    object Yoga : ScreenDestination()
    object Stretching : ScreenDestination()
    object MorningMove : ScreenDestination()
    object HiitCardio : ScreenDestination()
    object RelaxingYoga : ScreenDestination()

    object Running : ScreenDestination()
    object Cycling : ScreenDestination()
    object Walking : ScreenDestination()

    data class WorkoutDetail(val workoutId: String) : ScreenDestination()
    data class WorkoutPlayer(val workoutId: String) : ScreenDestination()
    data class ActivityTracker(val type: ActivityType) : ScreenDestination()
}

class FitPlanViewModel(
    private val repository: FitPlanRepository = FitPlanRepository.instance
) : ViewModel() {

    // Navigation Stack
    private val _currentScreen = MutableStateFlow<ScreenDestination>(ScreenDestination.Home)
    val currentScreen: StateFlow<ScreenDestination> = _currentScreen.asStateFlow()

    private val screenHistory = mutableListOf<ScreenDestination>()

    // Data from repository
    val isLoggedIn = repository.isLoggedIn
    val themeMode = repository.themeMode
    val userProfile = repository.userProfile
    val workouts = repository.workouts
    val programs = repository.programs
    val meals = repository.meals
    val waterGlasses = repository.waterGlasses
    val macros = repository.macros
    val recipes = repository.recipes
    val todaySteps = repository.todaySteps
    val todayCalories = repository.todayCalories
    val todayActiveMinutes = repository.todayActiveMinutes
    val weeklyStats = repository.weeklyStats
    val weeklyNutritionTrends = repository.weeklyNutritionTrends
    val bodyMetrics = repository.bodyMetrics
    val activitySessions = repository.activitySessions
    val reminderSettings = repository.reminderSettings

    // Gemini AI Advisor Engine
    private val fitnessAdvisor = GeminiFitnessAdvisor()

    private val _geminiRecommendations = MutableStateFlow(GeminiHealthRecommendations())
    val geminiRecommendations: StateFlow<GeminiHealthRecommendations> = _geminiRecommendations.asStateFlow()

    private val _isGeminiLoading = MutableStateFlow(false)
    val isGeminiLoading: StateFlow<Boolean> = _isGeminiLoading.asStateFlow()

    private val _aiCoachChatMessages = MutableStateFlow<List<AiCoachMessage>>(initialCoachMessages())
    val aiCoachChatMessages: StateFlow<List<AiCoachMessage>> = _aiCoachChatMessages.asStateFlow()

    private val _isSendingCoach = MutableStateFlow(false)
    val isSendingCoach: StateFlow<Boolean> = _isSendingCoach.asStateFlow()

    private val _isCoachSheetOpen = MutableStateFlow(false)
    val isCoachSheetOpen: StateFlow<Boolean> = _isCoachSheetOpen.asStateFlow()

    init {
        loadGeminiRecommendations()
    }

    // Live Workout Player State
    private val _playerExerciseIndex = MutableStateFlow(0)
    val playerExerciseIndex: StateFlow<Int> = _playerExerciseIndex.asStateFlow()

    private val _playerSecondsRemaining = MutableStateFlow(45)
    val playerSecondsRemaining: StateFlow<Int> = _playerSecondsRemaining.asStateFlow()

    private val _isPlayerRunning = MutableStateFlow(false)
    val isPlayerRunning: StateFlow<Boolean> = _isPlayerRunning.asStateFlow()

    private val _isWorkoutFinished = MutableStateFlow(false)
    val isWorkoutFinished: StateFlow<Boolean> = _isWorkoutFinished.asStateFlow()

    private var workoutTimerJob: Job? = null

    // Live Activity Tracker State (GPS Run/Ride/Walk)
    private val _trackerDurationSec = MutableStateFlow(0)
    val trackerDurationSec: StateFlow<Int> = _trackerDurationSec.asStateFlow()

    private val _trackerDistanceKm = MutableStateFlow(0.0f)
    val trackerDistanceKm: StateFlow<Float> = _trackerDistanceKm.asStateFlow()

    private val _isTrackerRunning = MutableStateFlow(false)
    val isTrackerRunning: StateFlow<Boolean> = _isTrackerRunning.asStateFlow()

    private var trackerJob: Job? = null

    // Navigation
    fun navigateTo(destination: ScreenDestination) {
        screenHistory.add(_currentScreen.value)
        _currentScreen.value = destination
    }

    fun navigateBack(): Boolean {
        if (screenHistory.isNotEmpty()) {
            _currentScreen.value = screenHistory.removeAt(screenHistory.size - 1)
            return true
        }
        if (_currentScreen.value != ScreenDestination.Home) {
            _currentScreen.value = ScreenDestination.Home
            return true
        }
        return false
    }

    fun setLoggedIn(loggedIn: Boolean) {
        repository.setLoggedIn(loggedIn)
        if (loggedIn) {
            _currentScreen.value = ScreenDestination.Home
        } else {
            _currentScreen.value = ScreenDestination.Login
        }
    }

    fun initPreferences(context: android.content.Context) {
        repository.initPreferences(context)
        val lang = userProfile.value.selectedLanguage
        if (_aiCoachChatMessages.value.size <= 1) {
            _aiCoachChatMessages.value = listOf(
                AiCoachMessage(
                    id = "init_welcome",
                    sender = MessageSender.AI_COACH,
                    text = getInitialCoachWelcome(lang)
                )
            )
        }
    }

    fun setThemeMode(mode: AppThemeMode, context: android.content.Context? = null) {
        repository.setThemeMode(mode, context)
    }

    fun setLanguage(langCode: String, context: android.content.Context? = null) {
        updateLanguage(langCode, context)
    }

    fun toggleTheme(context: android.content.Context? = null) {
        repository.toggleTheme(context)
    }

    fun toggleMealEaten(mealId: String) = repository.toggleMealEaten(mealId)
    fun toggleMealFavorite(mealId: String) = repository.toggleMealFavorite(mealId)
    fun toggleRecipeFavorite(recipeId: String) = repository.toggleRecipeFavorite(recipeId)
    fun addRecipeToTodayMeals(recipe: RecipeItem) {
        val newMeal = MealItem(
            id = "meal_${recipe.id}_${System.currentTimeMillis()}",
            name = recipe.title,
            mealType = recipe.category,
            time = "Scheduled",
            calories = recipe.calories,
            protein = recipe.protein,
            carbs = recipe.carbs,
            fats = recipe.fats,
            isEaten = false,
            imageAsset = recipe.imageAsset,
            ingredients = recipe.ingredients,
            instructions = recipe.instructions,
            isFavorite = recipe.isFavorite
        )
        repository.addMeal(newMeal)
    }
    fun addWaterGlass() = repository.addWaterGlass()
    fun removeWaterGlass() = repository.removeWaterGlass()
    fun setWaterGlasses(count: Int) = repository.setWaterGlasses(count)
    fun resetWater() = repository.resetWater()
    fun updateLanguage(langCode: String, context: android.content.Context? = null) {
        repository.updateLanguage(langCode, context)
        if (_aiCoachChatMessages.value.size <= 1) {
            _aiCoachChatMessages.value = listOf(
                AiCoachMessage(
                    id = "init_${System.currentTimeMillis()}",
                    sender = MessageSender.AI_COACH,
                    text = getInitialCoachWelcome(langCode)
                )
            )
        }
        loadGeminiRecommendations(forceRefresh = true)
    }
    fun toggleVoiceCoach(context: android.content.Context? = null) =
        repository.toggleVoiceCoach(context)
    fun updateProfile(name: String, email: String, weight: Float) =
        repository.updateProfile(name, email, weight)

    // Reminder Actions
    fun toggleReminder(
        type: com.example.notifications.ReminderType,
        isEnabled: Boolean,
        context: android.content.Context
    ) {
        repository.toggleReminder(type, isEnabled)
        val currentItem = reminderSettings.value.reminders.firstOrNull { it.type == type }
        val updatedItem = currentItem?.copy(isEnabled = isEnabled)
            ?: com.example.notifications.ReminderScheduleItem(type = type, isEnabled = isEnabled)
        com.example.notifications.FitPlanNotificationHelper.scheduleDailyReminder(context, updatedItem)
    }

    fun updateReminderTime(
        type: com.example.notifications.ReminderType,
        hour: Int,
        minute: Int,
        context: android.content.Context
    ) {
        repository.updateReminderTime(type, hour, minute)
        val currentItem = reminderSettings.value.reminders.firstOrNull { it.type == type }
        val updatedItem = currentItem?.copy(hour = hour, minute = minute)
            ?: com.example.notifications.ReminderScheduleItem(type = type, isEnabled = true, hour = hour, minute = minute)
        com.example.notifications.FitPlanNotificationHelper.scheduleDailyReminder(context, updatedItem)
    }

    fun sendTestNotification(
        type: com.example.notifications.ReminderType,
        context: android.content.Context
    ) {
        com.example.notifications.FitPlanNotificationHelper.sendTestNotification(context, type)
    }

    // Workout Player Controls
    fun startWorkoutPlayer(workoutId: String) {
        _playerExerciseIndex.value = 0
        _isWorkoutFinished.value = false
        val currentWorkout = workouts.value.firstOrNull { it.id == workoutId } ?: workouts.value.first()
        val firstEx = currentWorkout.exercises.firstOrNull()
        _playerSecondsRemaining.value = firstEx?.durationSec ?: 45
        _isPlayerRunning.value = true
        navigateTo(ScreenDestination.WorkoutPlayer(workoutId))
        startWorkoutTimer(workoutId)
    }

    private fun startWorkoutTimer(workoutId: String) {
        workoutTimerJob?.cancel()
        workoutTimerJob = viewModelScope.launch {
            while (_playerSecondsRemaining.value > 0) {
                delay(1000)
                _playerSecondsRemaining.value -= 1
            }
            // Auto advance or complete
            nextExercise(workoutId)
        }
    }

    fun nextExercise(workoutId: String) {
        val currentWorkout = workouts.value.firstOrNull { it.id == workoutId } ?: workouts.value.first()
        if (_playerExerciseIndex.value < currentWorkout.exercises.size - 1) {
            _playerExerciseIndex.value += 1
            val nextEx = currentWorkout.exercises[_playerExerciseIndex.value]
            _playerSecondsRemaining.value = nextEx.durationSec
            startWorkoutTimer(workoutId)
        } else {
            workoutTimerJob?.cancel()
            _isWorkoutFinished.value = true
        }
    }

    fun previousExercise(workoutId: String) {
        val currentWorkout = workouts.value.firstOrNull { it.id == workoutId } ?: workouts.value.first()
        if (_playerExerciseIndex.value > 0) {
            _playerExerciseIndex.value -= 1
            val prevEx = currentWorkout.exercises[_playerExerciseIndex.value]
            _playerSecondsRemaining.value = prevEx.durationSec
            startWorkoutTimer(workoutId)
        }
    }

    fun toggleWorkoutPauseResume(workoutId: String) {
        if (workoutTimerJob?.isActive == true) {
            workoutTimerJob?.cancel()
        } else {
            startWorkoutTimer(workoutId)
        }
    }

    // Activity Tracker Controls
    fun startActivityTracker(type: ActivityType) {
        _trackerDurationSec.value = 0
        _trackerDistanceKm.value = 0.0f
        _isTrackerRunning.value = true
        navigateTo(ScreenDestination.ActivityTracker(type))

        trackerJob?.cancel()
        trackerJob = viewModelScope.launch {
            while (_isTrackerRunning.value) {
                delay(1000)
                _trackerDurationSec.value += 1
                // Increment distance slightly to simulate active movement
                val speedFactor = when (type) {
                    ActivityType.RUNNING -> 0.0028f // ~10 km/h
                    ActivityType.CYCLING -> 0.0055f // ~20 km/h
                    ActivityType.WALKING -> 0.0014f // ~5 km/h
                    else -> 0.0020f
                }
                _trackerDistanceKm.value += speedFactor
            }
        }
    }

    fun toggleTrackerPause() {
        _isTrackerRunning.value = !_isTrackerRunning.value
    }

    fun finishActivityTracker(type: ActivityType) {
        trackerJob?.cancel()
        _isTrackerRunning.value = false
        val durationMins = maxOf(1, _trackerDurationSec.value / 60)
        val dist = if (_trackerDistanceKm.value < 0.1f) 1.25f else _trackerDistanceKm.value
        val cals = (dist * when (type) {
            ActivityType.RUNNING -> 75
            ActivityType.CYCLING -> 40
            ActivityType.WALKING -> 50
            else -> 60
        }).toInt()

        val paceStr = when (type) {
            ActivityType.RUNNING -> "5'18\" /km"
            ActivityType.CYCLING -> "22.4 km/h"
            ActivityType.WALKING -> "11'05\" /km"
            else -> "N/A"
        }

        repository.addActivitySession(
            ActivitySession(
                id = "session_${System.currentTimeMillis()}",
                type = type,
                date = "Just now",
                distanceKm = (dist * 100).toInt() / 100f,
                durationMinutes = durationMins,
                avgPaceOrSpeed = paceStr,
                caloriesBurned = cals
            )
        )
        // Refresh Gemini insights to reflect newly recorded activity session
        loadGeminiRecommendations()
        navigateBack()
    }

    // --- Gemini AI Actions ---

    fun openCoachSheet() {
        _isCoachSheetOpen.value = true
    }

    fun closeCoachSheet() {
        _isCoachSheetOpen.value = false
    }

    fun loadGeminiRecommendations(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _isGeminiLoading.value = true
            try {
                val result = fitnessAdvisor.getDailyRecommendations(
                    userProfile = userProfile.value,
                    todaySteps = todaySteps.value,
                    todayCalories = todayCalories.value,
                    todayMinutes = todayActiveMinutes.value,
                    waterGlasses = waterGlasses.value,
                    meals = meals.value,
                    activitySessions = activitySessions.value
                )
                _geminiRecommendations.value = result
            } catch (e: Exception) {
                // Keep current or fallback
            } finally {
                _isGeminiLoading.value = false
            }
        }
    }

    fun askGeminiCoach(question: String) {
        if (question.isBlank()) return
        val userMsg = AiCoachMessage(
            id = "msg_${System.currentTimeMillis()}",
            sender = MessageSender.USER,
            text = question
        )
        _aiCoachChatMessages.update { it + userMsg }
        _isSendingCoach.value = true

        viewModelScope.launch {
            try {
                val reply = fitnessAdvisor.askCoach(
                    userQuestion = question,
                    userProfile = userProfile.value,
                    todaySteps = todaySteps.value,
                    todayCalories = todayCalories.value,
                    waterGlasses = waterGlasses.value
                )
                val coachMsg = AiCoachMessage(
                    id = "msg_${System.currentTimeMillis() + 1}",
                    sender = MessageSender.AI_COACH,
                    text = reply
                )
                _aiCoachChatMessages.update { it + coachMsg }
            } catch (e: Exception) {
                val errorText = when (userProfile.value.selectedLanguage) {
                    "ar" -> "أقوم بتحليل بيانات نشاطك وتمرينك الحالي. استمر في السعي نحو أهدافك اليومية!"
                    "es" -> "Estoy analizando los datos actuales de tu entrenamiento. ¡Sigue adelante hacia tus metas diarias!"
                    else -> "I'm analyzing your current workout data. Keep pushing towards your daily goals!"
                }
                val errorMsg = AiCoachMessage(
                    id = "msg_${System.currentTimeMillis() + 1}",
                    sender = MessageSender.AI_COACH,
                    text = errorText
                )
                _aiCoachChatMessages.update { it + errorMsg }
            } finally {
                _isSendingCoach.value = false
            }
        }
    }

    fun startAiRecommendedWorkout() {
        val rec = _geminiRecommendations.value
        val exItems = rec.exercises.mapIndexed { index, ex ->
            ExerciseItem(
                id = "ai_ex_$index",
                name = ex.name,
                sets = ex.sets,
                reps = ex.reps,
                durationSec = ex.durationSec,
                restSec = ex.restSec,
                instructions = ex.instructions
            )
        }.ifEmpty {
            listOf(
                ExerciseItem("ai_1", "Dynamic Warm-up Jacks", 3, "45 sec", 45, 15, "Stay on toes and breathe."),
                ExerciseItem("ai_2", "Tempo Bodyweight Squats", 3, "15 reps", 45, 20, "Drive through heels."),
                ExerciseItem("ai_3", "Core Mountain Climbers", 3, "30 sec", 30, 15, "Keep back flat and hips level."),
                ExerciseItem("ai_4", "Push-ups with Hold", 3, "12 reps", 40, 20, "Engage core and chest."),
                ExerciseItem("ai_5", "Restorative Cool Down Stretch", 2, "60 sec", 60, 0, "Deep diaphragmatic breathing.")
            )
        }

        val aiWorkout = WorkoutItem(
            id = "ai_rec_${System.currentTimeMillis()}",
            title = rec.workoutTitle,
            category = rec.workoutCategory,
            durationMinutes = rec.workoutDurationMinutes,
            difficulty = rec.workoutDifficulty,
            calories = rec.workoutCalories,
            exercisesCount = exItems.size,
            description = rec.workoutRationale,
            accentColorHex = 0xFF00D2D3,
            exercises = exItems
        )

        repository.addWorkout(aiWorkout)
        startWorkoutPlayer(aiWorkout.id)
    }

    companion object {
        fun getInitialCoachWelcome(language: String): String = when (language) {
            "ar" -> "مرحباً! أنا مدربك الشخصي الذكي المدعوم بـ Gemini. أقوم بتحليل نشاطك، وترطيبك، وسعراتك الحرارية، وأهدافك باستمرار لتقديم أفضل التوجيهات الرياضية والصحية لك. كيف يمكنني مساعدتك اليوم؟"
            "es" -> "¡Hola! Soy tu entrenador personal IA con tecnología Gemini. Analizo continuamente tu actividad, hidratación, calorías y metas para brindarte la mejor guía deportiva. ¿Cómo puedo ayudarte hoy?"
            else -> "Hello! I am your Gemini AI Coach. I continuously analyze your activity, hydration, calories, and goals to provide optimal guidance. How can I help you today?"
        }

        private fun initialCoachMessages(): List<AiCoachMessage> = listOf(
            AiCoachMessage(
                id = "init_1",
                sender = MessageSender.AI_COACH,
                text = getInitialCoachWelcome("en")
            )
        )
    }
}
