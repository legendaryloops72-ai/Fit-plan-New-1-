package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.gemini.GeminiHealthRecommendations
import com.example.model.WorkoutItem
import com.example.ui.ScreenDestination
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    todaySteps: Int,
    todayCalories: Int,
    todayMinutes: Int,
    waterGlasses: Int,
    onAddWater: () -> Unit,
    onRemoveWater: () -> Unit,
    onSetWaterGlasses: (Int) -> Unit = {},
    recommendedWorkouts: List<WorkoutItem>,
    todayMeal: com.example.model.MealItem? = null,
    geminiRecommendations: GeminiHealthRecommendations = GeminiHealthRecommendations(),
    isGeminiLoading: Boolean = false,
    onRefreshRecommendations: () -> Unit = {},
    onAskCoachClick: () -> Unit = {},
    onStartAiWorkout: () -> Unit = {},
    onNavigate: (ScreenDestination) -> Unit,
    onWorkoutClick: (String) -> Unit,
    onPlayWorkout: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = FitPlanTheme.colors
    val isDark = FitPlanTheme.isDark

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Top Header
        item {
            FitPlanTopBar(
                onSearchClick = { onNavigate(ScreenDestination.Workouts) },
                onNotificationClick = { onNavigate(ScreenDestination.Progress) }
            )
        }

        // Hero Banner
        item {
            HeroBanner(
                badgeText = stringResource(R.string.better_you_everyday),
                title = stringResource(R.string.discipline_builds_freedom),
                subtitle = stringResource(R.string.track_train_stay_healthy),
                ctaText = stringResource(R.string.lets_go),
                accentColor = colors.primary,
                onCtaClick = { onNavigate(ScreenDestination.HomeWorkout) }
            )
        }

        // Gemini AI Daily Health Tips & Plan Card
        item {
            GeminiDailyTipsCard(
                recommendations = geminiRecommendations,
                isLoading = isGeminiLoading,
                onRefresh = onRefreshRecommendations,
                onAskCoachClick = onAskCoachClick,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // 4 Fast Action Cards Grid
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HomeShortcutCard(
                        title = stringResource(R.string.nav_workouts),
                        subtitle = stringResource(R.string.get_stronger),
                        icon = Icons.Default.FitnessCenter,
                        accentColor = colors.primary,
                        onClick = { onNavigate(ScreenDestination.Workouts) },
                        modifier = Modifier.weight(1f)
                    )
                    HomeShortcutCard(
                        title = stringResource(R.string.nav_nutrition),
                        subtitle = stringResource(R.string.eat_better),
                        icon = Icons.Default.Restaurant,
                        accentColor = colors.cyan,
                        onClick = { onNavigate(ScreenDestination.Nutrition) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HomeShortcutCard(
                        title = stringResource(R.string.nav_progress),
                        subtitle = stringResource(R.string.track_results),
                        icon = Icons.Default.BarChart,
                        accentColor = colors.morningOrange,
                        onClick = { onNavigate(ScreenDestination.Progress) },
                        modifier = Modifier.weight(1f)
                    )
                    HomeShortcutCard(
                        title = "AI Coach",
                        subtitle = "Personalized tips",
                        icon = Icons.Default.AutoAwesome,
                        accentColor = colors.yogaLavender,
                        onClick = { onNavigate(ScreenDestination.AiCoach) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Today's Progress Card
        item {
            TodayProgressCircle(
                progress = (todaySteps.toFloat() / 10000f).coerceIn(0.1f, 1f),
                steps = todaySteps,
                stepsGoal = 10000,
                calories = todayCalories,
                activeMinutes = todayMinutes,
                onSeeAllClick = { onNavigate(ScreenDestination.Progress) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // Daily Water Intake Counter UI & Goal Progress Indicator
        item {
            WaterTrackerWidget(
                currentGlasses = waterGlasses,
                targetGlasses = 8,
                onAddGlass = onAddWater,
                onRemoveGlass = onRemoveWater,
                onSetGlasses = onSetWaterGlasses,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // Featured Daily Nutrition Preview Card with Real Photograph
        if (todayMeal != null) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 10.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.home_todays_nutrition),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    Text(
                        text = stringResource(R.string.home_view_nutrition),
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.cyan,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onNavigate(ScreenDestination.Nutrition) }
                    )
                }
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(colors.card)
                        .border(1.dp, colors.cardBorder, RoundedCornerShape(18.dp))
                        .clickable { onNavigate(ScreenDestination.Nutrition) }
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                                .background(colors.surface)
                        ) {
                            if (todayMeal.fullAssetUrl.isNotEmpty()) {
                                coil.compose.AsyncImage(
                                    model = todayMeal.fullAssetUrl,
                                    contentDescription = todayMeal.name,
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        androidx.compose.ui.graphics.Brush.verticalGradient(
                                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.65f))
                                        )
                                    )
                            )
                            Box(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .align(Alignment.TopStart)
                                    .background(FitPlanBlack.copy(alpha = 0.75f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "${todayMeal.mealType.uppercase()} • ${todayMeal.time}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = colors.cyan,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = todayMeal.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${todayMeal.calories} kcal • ${todayMeal.protein}g protein",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.cyan,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(colors.cyan.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = null,
                                    tint = colors.cyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Gemini AI Custom Workout Recommendation Card
        item {
            GeminiWorkoutRecommendationCard(
                recommendations = geminiRecommendations,
                onStartAiWorkout = onStartAiWorkout,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // Recommended for You Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.recommended_for_you),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
                Text(
                    text = stringResource(R.string.view_all),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigate(ScreenDestination.Workouts) }
                )
            }
        }

        // Recommended Workouts
        items(recommendedWorkouts.take(3)) { workout ->
            WorkoutCard(
                workout = workout,
                onWorkoutClick = { onWorkoutClick(workout.id) },
                onPlayClick = { onPlayWorkout(workout.id) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }

        // Bottom Inspirational Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(colors.surface)
                    .border(1.dp, colors.cardBorder, RoundedCornerShape(18.dp))
                    .padding(18.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.build_healthier_banner),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.start_today_guided_routines),
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textSecondary
                        )
                    }
                    Button(
                        onClick = { onNavigate(ScreenDestination.HomeWorkout) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.primary,
                            contentColor = if (isDark) FitPlanBlack else Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.action_start_caps),
                            fontWeight = FontWeight.Black,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HomeShortcutCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = FitPlanTheme.colors

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(colors.card)
            .border(1.dp, colors.cardBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
                fontSize = 11.sp
            )
        }
    }
}
