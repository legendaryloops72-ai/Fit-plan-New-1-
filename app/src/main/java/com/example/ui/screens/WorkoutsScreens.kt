package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.gemini.GeminiHealthRecommendations
import com.example.model.WorkoutItem
import com.example.model.WorkoutProgram
import com.example.ui.ScreenDestination
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun WorkoutsScreen(
    workouts: List<WorkoutItem>,
    programs: List<WorkoutProgram>,
    geminiRecommendations: GeminiHealthRecommendations = GeminiHealthRecommendations(),
    onStartAiWorkout: () -> Unit = {},
    onWorkoutClick: (String) -> Unit,
    onPlayWorkout: (String) -> Unit,
    onNavigateToCategory: (ScreenDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Strength", "Cardio", "HIIT", "Yoga", "Stretching")

    val filteredWorkouts = remember(selectedCategory, workouts) {
        if (selectedCategory == "All") workouts
        else workouts.filter { it.category.contains(selectedCategory, ignoreCase = true) }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(FitPlanBlack)
            .testTag("workouts_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        item {
            FitPlanTopBar(
                onSearchClick = { /* Search workouts */ }
            )
        }

        // Hero Banner
        item {
            HeroBanner(
                badgeText = stringResource(R.string.badge_get_stronger),
                title = stringResource(R.string.workouts_subtitle),
                subtitle = stringResource(R.string.discover_workouts_sub),
                ctaText = stringResource(R.string.workout_home_workout).uppercase(),
                accentColor = FitPlanNeonLime,
                onCtaClick = { onNavigateToCategory(ScreenDestination.HomeWorkout) }
            )
        }

        // Gemini AI Workout Recommendation
        item {
            GeminiWorkoutRecommendationCard(
                recommendations = geminiRecommendations,
                onStartAiWorkout = onStartAiWorkout,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // Workout Quick Discovery Category Cards
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.workout_types),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = FitPlanTextWhite
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        WorkoutTypePill(
                            title = stringResource(R.string.workout_home_workout),
                            subtitle = stringResource(R.string.sub_no_equipment),
                            color = FitPlanNeonLime,
                            onClick = { onNavigateToCategory(ScreenDestination.HomeWorkout) }
                        )
                    }
                    item {
                        WorkoutTypePill(
                            title = stringResource(R.string.workout_yoga),
                            subtitle = stringResource(R.string.sub_mind_balance),
                            color = FitPlanYogaLavender,
                            onClick = { onNavigateToCategory(ScreenDestination.Yoga) }
                        )
                    }
                    item {
                        WorkoutTypePill(
                            title = stringResource(R.string.workout_stretching),
                            subtitle = stringResource(R.string.sub_flexibility),
                            color = FitPlanCyan,
                            onClick = { onNavigateToCategory(ScreenDestination.Stretching) }
                        )
                    }
                    item {
                        WorkoutTypePill(
                            title = stringResource(R.string.workout_morning_move),
                            subtitle = stringResource(R.string.sub_15min_energy),
                            color = FitPlanMorningOrange,
                            onClick = { onNavigateToCategory(ScreenDestination.MorningMove) }
                        )
                    }
                    item {
                        WorkoutTypePill(
                            title = stringResource(R.string.workout_hiit_cardio),
                            subtitle = stringResource(R.string.sub_torch_calories),
                            color = FitPlanHiitRed,
                            onClick = { onNavigateToCategory(ScreenDestination.HiitCardio) }
                        )
                    }
                    item {
                        WorkoutTypePill(
                            title = stringResource(R.string.workout_relaxing_yoga),
                            subtitle = stringResource(R.string.sub_rest_unwind),
                            color = FitPlanYogaLavender,
                            onClick = { onNavigateToCategory(ScreenDestination.RelaxingYoga) }
                        )
                    }
                }
            }
        }

        // Category Filter Tabs
        item {
            CategoryTabsRow(
                tabs = categories,
                selectedTab = selectedCategory,
                onTabSelected = { selectedCategory = it }
            )
        }

        // Section Title
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.popular_workouts),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = FitPlanTextWhite
                )
                Text(
                    text = "${filteredWorkouts.size} ${stringResource(R.string.routines_count)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = FitPlanTextSecondary
                )
            }
        }

        // Workouts List
        items(filteredWorkouts) { workout ->
            WorkoutCard(
                workout = workout,
                onWorkoutClick = { onWorkoutClick(workout.id) },
                onPlayClick = { onPlayWorkout(workout.id) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }

        // Workout Programs Header
        item {
            Text(
                text = stringResource(R.string.workout_programs),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = FitPlanTextWhite,
                modifier = Modifier.padding(horizontal = 16.dp).padding(top = 20.dp, bottom = 10.dp)
            )
        }

        // Programs List
        items(programs) { program ->
            ProgramItemCard(program = program)
        }
    }
}

@Composable
fun WorkoutTypePill(
    title: String,
    subtitle: String,
    color: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(130.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(FitPlanCard)
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(color, CircleShape)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = FitPlanTextWhite,
                fontSize = 13.sp
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = FitPlanTextSecondary,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun ProgramItemCard(
    program: WorkoutProgram,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(FitPlanCard)
            .border(1.dp, FitPlanCardBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = com.example.ui.FitPlanLocalization.getProgramTitle(program),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = FitPlanTextWhite
                    )
                    Text(
                        text = "${program.durationWeeks} ${stringResource(R.string.weeks_unit)} • ${com.example.ui.FitPlanLocalization.getDifficultyLabel(program.level)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = FitPlanTextSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .background(FitPlanNeonLime.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${(program.progressPercent * 100).toInt()}% ${stringResource(R.string.done_suffix)}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = FitPlanNeonLime
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress = { program.progressPercent },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = FitPlanNeonLime,
                trackColor = FitPlanSurface
            )
        }
    }
}

// -------------------------------------------------------------
// Dedicated Workout Detail Screens matching the 6 specific Mockups
// -------------------------------------------------------------

@Composable
fun SpecificWorkoutHubScreen(
    title: String,
    subtitle: String,
    badgeText: String,
    accentColor: Color,
    durationMinutes: Int,
    caloriesBurned: Int,
    exercisesCount: Int,
    tabs: List<String>,
    workoutList: List<WorkoutItem>,
    onBackClick: () -> Unit,
    onStartWorkout: () -> Unit,
    onWorkoutItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(tabs.firstOrNull() ?: "All") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(FitPlanBlack),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        item {
            FitPlanTopBar(
                title = title,
                showBackButton = true,
                onBackClick = onBackClick
            )
        }

        // Angled Hero Banner
        item {
            HeroBanner(
                badgeText = badgeText,
                title = title.uppercase(),
                subtitle = subtitle,
                ctaText = stringResource(R.string.start_workout_action).uppercase(),
                accentColor = accentColor,
                onCtaClick = onStartWorkout
            )
        }

        // Stats Row (Duration, Calories, Exercises)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricStatCard(
                    title = stringResource(R.string.duration),
                    value = "$durationMinutes",
                    unit = stringResource(R.string.min_unit),
                    icon = Icons.Default.Timer,
                    iconTint = accentColor,
                    modifier = Modifier.weight(1f)
                )
                MetricStatCard(
                    title = stringResource(R.string.burn),
                    value = "$caloriesBurned",
                    unit = stringResource(R.string.kcal_unit),
                    icon = Icons.Default.LocalFireDepartment,
                    iconTint = FitPlanMorningOrange,
                    modifier = Modifier.weight(1f)
                )
                MetricStatCard(
                    title = stringResource(R.string.exercises),
                    value = "$exercisesCount",
                    unit = stringResource(R.string.moves_unit),
                    icon = Icons.Default.FitnessCenter,
                    iconTint = FitPlanCyan,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Category Filter Tabs
        item {
            CategoryTabsRow(
                tabs = tabs,
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                activeColor = accentColor
            )
        }

        // Section Title
        item {
            Text(
                text = stringResource(R.string.featured_routines),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = FitPlanTextWhite,
                modifier = Modifier.padding(horizontal = 16.dp).padding(top = 12.dp, bottom = 8.dp)
            )
        }

        // Workout cards
        items(workoutList) { item ->
            WorkoutCard(
                workout = item,
                onWorkoutClick = { onWorkoutItemClick(item.id) },
                onPlayClick = onStartWorkout,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }

        // Exercises Movement Library with Real Photographs
        val allExercises = workoutList.flatMap { it.exercises }.distinctBy { it.name }
        if (allExercises.isNotEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.exercise_movement_library),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = FitPlanTextWhite,
                    modifier = Modifier.padding(horizontal = 16.dp).padding(top = 18.dp, bottom = 8.dp)
                )
            }

            items(allExercises) { ex ->
                ExerciseRowCard(
                    exercise = ex,
                    accentColor = accentColor,
                    onPlay = onStartWorkout,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)
                )
            }
        }
    }
}

@Composable
fun ExerciseRowCard(
    exercise: com.example.model.ExerciseItem,
    accentColor: Color,
    onPlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(FitPlanCard)
            .border(1.dp, FitPlanCardBorder, RoundedCornerShape(16.dp))
            .clickable { onPlay() }
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val assetUrl = exercise.fullAssetUrl.ifEmpty {
                com.example.data.ExerciseAssetRegistry.getAssetUrlForName(exercise.name)
            }
            if (assetUrl.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(FitPlanSurface)
                        .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                ) {
                    AsyncImage(
                        model = assetUrl,
                        contentDescription = exercise.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = com.example.ui.FitPlanLocalization.getExerciseName(exercise),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = FitPlanTextWhite
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${exercise.sets} ${stringResource(R.string.sets_unit)} • ${exercise.reps}",
                    style = MaterialTheme.typography.bodySmall,
                    color = accentColor,
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = com.example.ui.FitPlanLocalization.getExerciseInstructions(exercise),
                    style = MaterialTheme.typography.bodySmall,
                    color = FitPlanTextSecondary,
                    fontSize = 10.sp,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = stringResource(R.string.start_action),
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
