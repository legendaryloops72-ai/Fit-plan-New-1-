package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import com.example.model.ActivityType
import com.example.ui.FitPlanLocalization
import com.example.ui.FitPlanViewModel
import com.example.ui.ScreenDestination
import com.example.ui.components.FitPlanBottomNav
import com.example.ui.components.GeminiCoachBottomSheet
import com.example.ui.screens.*
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {

    private val viewModel: FitPlanViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize persistent user preferences
        viewModel.initPreferences(this)

        // Create notification channels for reminders
        com.example.notifications.FitPlanNotificationHelper.createNotificationChannel(this)

        setContent {
            val currentScreen by viewModel.currentScreen.collectAsState()
            val isLoggedIn by viewModel.isLoggedIn.collectAsState()
            val themeMode by viewModel.themeMode.collectAsState()
            val userProfile by viewModel.userProfile.collectAsState()
            val reminderSettings by viewModel.reminderSettings.collectAsState()
            val workouts by viewModel.workouts.collectAsState()
            val programs by viewModel.programs.collectAsState()
            val meals by viewModel.meals.collectAsState()
            val macros by viewModel.macros.collectAsState()
            val waterGlasses by viewModel.waterGlasses.collectAsState()
            val recipes by viewModel.recipes.collectAsState()
            val todaySteps by viewModel.todaySteps.collectAsState()
            val todayCalories by viewModel.todayCalories.collectAsState()
            val todayMinutes by viewModel.todayActiveMinutes.collectAsState()
            val weeklyStats by viewModel.weeklyStats.collectAsState()
            val weeklyNutritionTrends by viewModel.weeklyNutritionTrends.collectAsState()
            val bodyMetrics by viewModel.bodyMetrics.collectAsState()
            val activitySessions by viewModel.activitySessions.collectAsState()

            // Player state
            val playerExerciseIndex by viewModel.playerExerciseIndex.collectAsState()
            val playerSecondsRemaining by viewModel.playerSecondsRemaining.collectAsState()
            val isWorkoutFinished by viewModel.isWorkoutFinished.collectAsState()

            // Tracker state
            val trackerDurationSec by viewModel.trackerDurationSec.collectAsState()
            val trackerDistanceKm by viewModel.trackerDistanceKm.collectAsState()
            val isTrackerRunning by viewModel.isTrackerRunning.collectAsState()

            // Gemini AI state
            val geminiRecommendations by viewModel.geminiRecommendations.collectAsState()
            val isGeminiLoading by viewModel.isGeminiLoading.collectAsState()
            val aiCoachChatMessages by viewModel.aiCoachChatMessages.collectAsState()
            val isSendingCoach by viewModel.isSendingCoach.collectAsState()
            val isCoachSheetOpen by viewModel.isCoachSheetOpen.collectAsState()

            val currentLang = userProfile.selectedLanguage
            val currentLocale = remember(currentLang) {
                when (currentLang) {
                    "ar" -> java.util.Locale("ar")
                    "es" -> java.util.Locale("es")
                    else -> java.util.Locale("en")
                }
            }

            val baseConfig = androidx.compose.ui.platform.LocalConfiguration.current
            val configuration = remember(currentLocale, baseConfig) {
                android.content.res.Configuration(baseConfig).apply {
                    setLocale(currentLocale)
                    setLayoutDirection(currentLocale)
                }
            }

            val localizedContext = remember(currentLocale) {
                baseContext.createConfigurationContext(configuration)
            }

            // Dynamic RTL layout direction based on selected language
            val layoutDirection = if (currentLang == "ar") {
                LayoutDirection.Rtl
            } else {
                LayoutDirection.Ltr
            }

            CompositionLocalProvider(
                androidx.compose.ui.platform.LocalConfiguration provides configuration,
                androidx.compose.ui.platform.LocalContext provides localizedContext,
                LocalActivityResultRegistryOwner provides this@MainActivity,
                LocalLayoutDirection provides layoutDirection
            ) {
                FitPlanTheme(
                    themeMode = themeMode,
                    onThemeChange = { newMode -> viewModel.setThemeMode(newMode, this@MainActivity) }
                ) {
                    BackHandler(enabled = currentScreen != ScreenDestination.Home && currentScreen != ScreenDestination.Login) {
                        viewModel.navigateBack()
                    }

                    val showBottomNav = currentScreen !is ScreenDestination.Login &&
                            currentScreen !is ScreenDestination.SignUp &&
                            currentScreen !is ScreenDestination.WorkoutPlayer &&
                            currentScreen !is ScreenDestination.ActivityTracker

                    Scaffold(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(FitPlanTheme.colors.background),
                        bottomBar = {
                            if (showBottomNav) {
                                FitPlanBottomNav(
                                    currentScreen = currentScreen,
                                    onNavigate = { destination ->
                                        viewModel.navigateTo(destination)
                                    }
                                )
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .background(FitPlanTheme.colors.background)
                        ) {
                            when (val screen = currentScreen) {
                                is ScreenDestination.Login -> {
                                    LoginScreen(
                                        onSignInSuccess = { viewModel.setLoggedIn(true) },
                                        onNavigateToSignUp = { viewModel.navigateTo(ScreenDestination.SignUp) }
                                    )
                                }

                                is ScreenDestination.SignUp -> {
                                    SignUpScreen(
                                        onSignUpSuccess = { viewModel.setLoggedIn(true) },
                                        onNavigateToSignIn = { viewModel.navigateTo(ScreenDestination.Login) }
                                    )
                                }

                                is ScreenDestination.Home -> {
                                    HomeScreen(
                                        todaySteps = todaySteps,
                                        todayCalories = todayCalories,
                                        todayMinutes = todayMinutes,
                                        waterGlasses = waterGlasses,
                                        onAddWater = { viewModel.addWaterGlass() },
                                        onRemoveWater = { viewModel.removeWaterGlass() },
                                        onSetWaterGlasses = { viewModel.setWaterGlasses(it) },
                                        recommendedWorkouts = workouts,
                                        todayMeal = meals.firstOrNull(),
                                        geminiRecommendations = geminiRecommendations,
                                        isGeminiLoading = isGeminiLoading,
                                        onRefreshRecommendations = { viewModel.loadGeminiRecommendations(forceRefresh = true) },
                                        onAskCoachClick = { viewModel.openCoachSheet() },
                                        onStartAiWorkout = { viewModel.startAiRecommendedWorkout() },
                                        onNavigate = { viewModel.navigateTo(it) },
                                        onWorkoutClick = { workoutId ->
                                            viewModel.startWorkoutPlayer(workoutId)
                                        },
                                        onPlayWorkout = { workoutId ->
                                            viewModel.startWorkoutPlayer(workoutId)
                                        }
                                    )
                                }

                                is ScreenDestination.Workouts -> {
                                    WorkoutsScreen(
                                        workouts = workouts,
                                        programs = programs,
                                        geminiRecommendations = geminiRecommendations,
                                        onStartAiWorkout = { viewModel.startAiRecommendedWorkout() },
                                        onWorkoutClick = { workoutId ->
                                            viewModel.startWorkoutPlayer(workoutId)
                                        },
                                        onPlayWorkout = { workoutId ->
                                            viewModel.startWorkoutPlayer(workoutId)
                                        },
                                        onNavigateToCategory = { viewModel.navigateTo(it) }
                                    )
                                }

                                is ScreenDestination.HomeWorkout -> {
                                    val homeWorkouts = workouts.filter { it.title.contains("Home", ignoreCase = true) || it.category.contains("Body", ignoreCase = true) }
                                    SpecificWorkoutHubScreen(
                                        title = stringResource(R.string.workout_home_workout),
                                        subtitle = stringResource(R.string.workout_home_workout_sub),
                                        badgeText = stringResource(R.string.workout_home_workout_badge),
                                        accentColor = FitPlanNeonLime,
                                        durationMinutes = 25,
                                        caloriesBurned = 240,
                                        exercisesCount = 9,
                                        tabs = listOf(
                                            stringResource(R.string.tab_all_workouts),
                                            stringResource(R.string.tab_upper_body),
                                            stringResource(R.string.tab_lower_body),
                                            stringResource(R.string.tab_core),
                                            stringResource(R.string.tab_cardio)
                                        ),
                                        workoutList = if (homeWorkouts.isNotEmpty()) homeWorkouts else workouts,
                                        onBackClick = { viewModel.navigateBack() },
                                        onStartWorkout = { viewModel.startWorkoutPlayer("w1") },
                                        onWorkoutItemClick = { viewModel.startWorkoutPlayer(it) }
                                    )
                                }

                                is ScreenDestination.Yoga -> {
                                    val yogaWorkouts = workouts.filter { it.category.contains("Yoga", ignoreCase = true) }
                                    SpecificWorkoutHubScreen(
                                        title = stringResource(R.string.workout_yoga),
                                        subtitle = stringResource(R.string.workout_yoga_sub),
                                        badgeText = stringResource(R.string.workout_yoga_badge),
                                        accentColor = FitPlanYogaLavender,
                                        durationMinutes = 35,
                                        caloriesBurned = 180,
                                        exercisesCount = 8,
                                        tabs = listOf(
                                            stringResource(R.string.tab_all_yoga),
                                            stringResource(R.string.tab_vinyasa),
                                            stringResource(R.string.tab_hatha),
                                            stringResource(R.string.tab_restorative),
                                            stringResource(R.string.tab_flow)
                                        ),
                                        workoutList = if (yogaWorkouts.isNotEmpty()) yogaWorkouts else workouts,
                                        onBackClick = { viewModel.navigateBack() },
                                        onStartWorkout = { viewModel.startWorkoutPlayer("w2") },
                                        onWorkoutItemClick = { viewModel.startWorkoutPlayer(it) }
                                    )
                                }

                                is ScreenDestination.Stretching -> {
                                    val stretchWorkouts = workouts.filter { it.category.contains("Stretching", ignoreCase = true) }
                                    SpecificWorkoutHubScreen(
                                        title = stringResource(R.string.workout_stretching),
                                        subtitle = stringResource(R.string.workout_stretching_sub),
                                        badgeText = stringResource(R.string.workout_stretching_badge),
                                        accentColor = FitPlanCyan,
                                        durationMinutes = 20,
                                        caloriesBurned = 110,
                                        exercisesCount = 7,
                                        tabs = listOf(
                                            stringResource(R.string.tab_all_stretching),
                                            stringResource(R.string.tab_full_body),
                                            stringResource(R.string.tab_lower_body),
                                            stringResource(R.string.tab_upper_body),
                                            stringResource(R.string.tab_dynamic)
                                        ),
                                        workoutList = if (stretchWorkouts.isNotEmpty()) stretchWorkouts else workouts,
                                        onBackClick = { viewModel.navigateBack() },
                                        onStartWorkout = { viewModel.startWorkoutPlayer("w3") },
                                        onWorkoutItemClick = { viewModel.startWorkoutPlayer(it) }
                                    )
                                }

                                is ScreenDestination.MorningMove -> {
                                    val morningWorkouts = workouts.filter { it.title.contains("Morning", ignoreCase = true) }
                                    SpecificWorkoutHubScreen(
                                        title = stringResource(R.string.workout_morning_move),
                                        subtitle = stringResource(R.string.workout_morning_move_sub),
                                        badgeText = stringResource(R.string.workout_morning_move_badge),
                                        accentColor = FitPlanMorningOrange,
                                        durationMinutes = 15,
                                        caloriesBurned = 130,
                                        exercisesCount = 6,
                                        tabs = listOf(
                                            stringResource(R.string.tab_all_morning),
                                            stringResource(R.string.tab_wake_up),
                                            stringResource(R.string.tab_energy_boost),
                                            stringResource(R.string.tab_quick_stretch),
                                            stringResource(R.string.tab_cardio_start)
                                        ),
                                        workoutList = if (morningWorkouts.isNotEmpty()) morningWorkouts else workouts,
                                        onBackClick = { viewModel.navigateBack() },
                                        onStartWorkout = { viewModel.startWorkoutPlayer("w4") },
                                        onWorkoutItemClick = { viewModel.startWorkoutPlayer(it) }
                                    )
                                }

                                is ScreenDestination.HiitCardio -> {
                                    val hiitWorkouts = workouts.filter { it.category.contains("HIIT", ignoreCase = true) }
                                    SpecificWorkoutHubScreen(
                                        title = stringResource(R.string.workout_hiit_cardio),
                                        subtitle = stringResource(R.string.workout_hiit_cardio_sub),
                                        badgeText = stringResource(R.string.workout_hiit_cardio_badge),
                                        accentColor = FitPlanHiitRed,
                                        durationMinutes = 30,
                                        caloriesBurned = 380,
                                        exercisesCount = 10,
                                        tabs = listOf(
                                            stringResource(R.string.tab_all_hiit),
                                            stringResource(R.string.tab_tabata),
                                            stringResource(R.string.tab_fat_burn),
                                            stringResource(R.string.tab_cardio_blast),
                                            stringResource(R.string.tab_core_hiit)
                                        ),
                                        workoutList = if (hiitWorkouts.isNotEmpty()) hiitWorkouts else workouts,
                                        onBackClick = { viewModel.navigateBack() },
                                        onStartWorkout = { viewModel.startWorkoutPlayer("w5") },
                                        onWorkoutItemClick = { viewModel.startWorkoutPlayer(it) }
                                    )
                                }

                                is ScreenDestination.RelaxingYoga -> {
                                    val relaxingWorkouts = workouts.filter { it.title.contains("Relaxing", ignoreCase = true) || it.category.contains("Yoga", ignoreCase = true) }
                                    SpecificWorkoutHubScreen(
                                        title = stringResource(R.string.workout_relaxing_yoga),
                                        subtitle = stringResource(R.string.workout_relaxing_yoga_sub),
                                        badgeText = stringResource(R.string.workout_relaxing_yoga_badge),
                                        accentColor = FitPlanYogaLavender,
                                        durationMinutes = 25,
                                        caloriesBurned = 120,
                                        exercisesCount = 7,
                                        tabs = listOf(
                                            stringResource(R.string.tab_all_relaxing_yoga),
                                            stringResource(R.string.tab_bedtime),
                                            stringResource(R.string.tab_deep_stretch),
                                            stringResource(R.string.tab_mindful),
                                            stringResource(R.string.tab_stress_relief)
                                        ),
                                        workoutList = if (relaxingWorkouts.isNotEmpty()) relaxingWorkouts else workouts,
                                        onBackClick = { viewModel.navigateBack() },
                                        onStartWorkout = { viewModel.startWorkoutPlayer("w6") },
                                        onWorkoutItemClick = { viewModel.startWorkoutPlayer(it) }
                                    )
                                }

                                is ScreenDestination.Running -> {
                                    RunningScreen(
                                        voiceCoachEnabled = userProfile.voiceCoachEnabled,
                                        onToggleVoiceCoach = { viewModel.toggleVoiceCoach() },
                                        onBackClick = { viewModel.navigateBack() },
                                        onStartRun = { viewModel.startActivityTracker(ActivityType.RUNNING) }
                                    )
                                }

                                is ScreenDestination.Cycling -> {
                                    CyclingScreen(
                                        onBackClick = { viewModel.navigateBack() },
                                        onStartCycling = { viewModel.startActivityTracker(ActivityType.CYCLING) }
                                    )
                                }

                                is ScreenDestination.Walking -> {
                                    WalkingScreen(
                                        onBackClick = { viewModel.navigateBack() },
                                        onStartWalking = { viewModel.startActivityTracker(ActivityType.WALKING) }
                                    )
                                }

                                is ScreenDestination.Activities -> {
                                    ActivitiesScreen(
                                        sessions = activitySessions,
                                        onNavigate = { viewModel.navigateTo(it) }
                                    )
                                }

                                is ScreenDestination.Nutrition -> {
                                    NutritionScreen(
                                        meals = meals,
                                        macros = macros,
                                        waterGlasses = waterGlasses,
                                        recipes = recipes,
                                        weeklyNutritionTrends = weeklyNutritionTrends,
                                        onToggleMeal = { viewModel.toggleMealEaten(it) },
                                        onToggleMealFavorite = { viewModel.toggleMealFavorite(it) },
                                        onToggleRecipeFavorite = { viewModel.toggleRecipeFavorite(it) },
                                        onAddRecipeToToday = { viewModel.addRecipeToTodayMeals(it) },
                                        onAddWater = { viewModel.addWaterGlass() },
                                        onRemoveWater = { viewModel.removeWaterGlass() },
                                        onSetWaterGlasses = { viewModel.setWaterGlasses(it) }
                                    )
                                }

                                is ScreenDestination.Progress -> {
                                    ProgressScreen(
                                        weeklyStats = weeklyStats,
                                        weeklyNutritionTrends = weeklyNutritionTrends,
                                        bodyMetrics = bodyMetrics,
                                        recentWorkouts = workouts,
                                        onWorkoutClick = { viewModel.startWorkoutPlayer(it) }
                                    )
                                }

                                is ScreenDestination.More -> {
                                    MoreScreen(
                                        userProfile = userProfile,
                                        reminderSettings = reminderSettings,
                                        themeMode = themeMode,
                                        onThemeModeChange = { newMode ->
                                            viewModel.setThemeMode(newMode, this@MainActivity)
                                        },
                                        onToggleTheme = {
                                            viewModel.toggleTheme(this@MainActivity)
                                        },
                                        onToggleVoiceCoach = { viewModel.toggleVoiceCoach(this@MainActivity) },
                                        onToggleReminder = { type, enabled, ctx ->
                                            viewModel.toggleReminder(type, enabled, ctx)
                                        },
                                        onUpdateReminderTime = { type, hour, min, ctx ->
                                            viewModel.updateReminderTime(type, hour, min, ctx)
                                        },
                                        onSendTestNotification = { type, ctx ->
                                            viewModel.sendTestNotification(type, ctx)
                                        },
                                        onLanguageChange = { viewModel.updateLanguage(it, this@MainActivity) },
                                        onLogOut = { viewModel.setLoggedIn(false) }
                                    )
                                }

                                is ScreenDestination.AiCoach -> {
                                    GeminiAiCoachScreen(
                                        userProfile = userProfile,
                                        todaySteps = todaySteps,
                                        todayCalories = todayCalories,
                                        todayMinutes = todayMinutes,
                                        waterGlasses = waterGlasses,
                                        recommendations = geminiRecommendations,
                                        isLoading = isGeminiLoading,
                                        chatMessages = aiCoachChatMessages,
                                        isSendingCoach = isSendingCoach,
                                        onRefreshRecommendations = { viewModel.loadGeminiRecommendations(forceRefresh = true) },
                                        onSendMessage = { viewModel.askGeminiCoach(it) },
                                        onStartAiWorkout = { viewModel.startAiRecommendedWorkout() },
                                        onBack = { viewModel.navigateBack() }
                                    )
                                }

                                is ScreenDestination.WorkoutDetail -> {
                                    val workout = workouts.firstOrNull { it.id == screen.workoutId } ?: workouts.first()
                                    SpecificWorkoutHubScreen(
                                        title = FitPlanLocalization.getWorkoutTitle(workout),
                                        subtitle = FitPlanLocalization.getWorkoutSubtitle(workout),
                                        badgeText = FitPlanLocalization.getWorkoutBadge(workout),
                                        accentColor = androidx.compose.ui.graphics.Color(workout.accentColorHex),
                                        durationMinutes = workout.durationMinutes,
                                        caloriesBurned = workout.calories,
                                        exercisesCount = workout.exercisesCount,
                                        tabs = listOf(
                                            stringResource(R.string.tab_all),
                                            stringResource(R.string.tab_warm_up),
                                            stringResource(R.string.tab_main_workout),
                                            stringResource(R.string.tab_cool_down)
                                        ),
                                        workoutList = workouts,
                                        onBackClick = { viewModel.navigateBack() },
                                        onStartWorkout = { viewModel.startWorkoutPlayer(workout.id) },
                                        onWorkoutItemClick = { viewModel.startWorkoutPlayer(it) }
                                    )
                                }

                                is ScreenDestination.WorkoutPlayer -> {
                                    val workout = workouts.firstOrNull { it.id == screen.workoutId } ?: workouts.first()
                                    WorkoutPlayerScreen(
                                        workout = workout,
                                        exerciseIndex = playerExerciseIndex,
                                        secondsRemaining = playerSecondsRemaining,
                                        isFinished = isWorkoutFinished,
                                        onPrevious = { viewModel.previousExercise(workout.id) },
                                        onNext = { viewModel.nextExercise(workout.id) },
                                        onTogglePause = { viewModel.toggleWorkoutPauseResume(workout.id) },
                                        onFinish = {
                                            viewModel.navigateBack()
                                        },
                                        onBack = { viewModel.navigateBack() }
                                    )
                                }

                                is ScreenDestination.ActivityTracker -> {
                                    LiveActivityTrackerScreen(
                                        type = screen.type,
                                        durationSec = trackerDurationSec,
                                        distanceKm = trackerDistanceKm,
                                        isRunning = isTrackerRunning,
                                        onTogglePause = { viewModel.toggleTrackerPause() },
                                        onFinish = { viewModel.finishActivityTracker(screen.type) },
                                        onBack = { viewModel.navigateBack() }
                                    )
                                }
                            }
                        }

                        if (isCoachSheetOpen) {
                            GeminiCoachBottomSheet(
                                chatMessages = aiCoachChatMessages,
                                isSending = isSendingCoach,
                                onSendMessage = { viewModel.askGeminiCoach(it) },
                                onDismiss = { viewModel.closeCoachSheet() }
                            )
                        }
                    }
                }
            }
        }
    }
}
