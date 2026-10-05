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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.BodyMetric
import com.example.model.DailyNutritionTrend
import com.example.model.DailyStepStat
import com.example.model.WorkoutItem
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun ProgressScreen(
    weeklyStats: List<DailyStepStat>,
    weeklyNutritionTrends: List<DailyNutritionTrend> = emptyList(),
    bodyMetrics: List<BodyMetric>,
    recentWorkouts: List<WorkoutItem>,
    onWorkoutClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf("Overview") }
    val tabs = listOf("Overview", "Body Metrics", "Workouts", "Goals")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(FitPlanBlack)
            .testTag("progress_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        item {
            FitPlanTopBar()
        }

        // Hero Banner
        item {
            HeroBanner(
                badgeText = "KEEP GOING",
                title = stringResource(R.string.progress_subtitle),
                subtitle = "Consistency is what turns average into excellence",
                ctaText = "GOALS",
                accentColor = FitPlanMorningOrange,
                onCtaClick = { selectedTab = "Goals" }
            )
        }

        // Sub Tabs
        item {
            CategoryTabsRow(
                tabs = tabs,
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                activeColor = FitPlanMorningOrange
            )
        }

        // 3 Key Stats Row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricStatCard(
                    title = "Active Time",
                    value = "42",
                    unit = "min",
                    icon = Icons.Default.Timer,
                    iconTint = FitPlanNeonLime,
                    subtitle = "+12% vs last week",
                    modifier = Modifier.weight(1f)
                )
                MetricStatCard(
                    title = "Calories",
                    value = "1,850",
                    unit = "kcal",
                    icon = Icons.Default.LocalFireDepartment,
                    iconTint = FitPlanMorningOrange,
                    subtitle = "+8% vs last week",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Weekly Activity Bar Chart
        item {
            WeeklyActivityChart(
                weeklyStats = weeklyStats,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // Weekly Calorie & Protein Nutrition Consumption Trends Chart
        if (weeklyNutritionTrends.isNotEmpty()) {
            item {
                WeeklyNutritionTrendsChart(
                    trends = weeklyNutritionTrends,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }

        // Streak Widget Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(FitPlanCard)
                    .border(1.dp, FitPlanCardBorder, RoundedCornerShape(18.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(FitPlanMorningOrange.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Whatshot,
                                contentDescription = null,
                                tint = FitPlanMorningOrange,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = stringResource(R.string.streak_14_days),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = FitPlanTextWhite
                            )
                            Text(
                                text = stringResource(R.string.streak_best_hint),
                                style = MaterialTheme.typography.bodySmall,
                                color = FitPlanTextSecondary
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .background(FitPlanMorningOrange, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.streak_fire_badge),
                            style = MaterialTheme.typography.labelSmall,
                            color = FitPlanBlack,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }

        // Goals Breakdown
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(FitPlanCard)
                    .border(1.dp, FitPlanCardBorder, RoundedCornerShape(18.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.daily_goals_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = FitPlanTextWhite
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    GoalProgressRow(stringResource(R.string.steps), "8,420", "10,000", 0.84f, FitPlanNeonLime)
                    Spacer(modifier = Modifier.height(10.dp))
                    GoalProgressRow(stringResource(R.string.calories_burned), "1,850", "2,400 ${stringResource(R.string.kcal_unit)}", 0.77f, FitPlanMorningOrange)
                    Spacer(modifier = Modifier.height(10.dp))
                    GoalProgressRow(stringResource(R.string.workout_duration), "42", "45 ${stringResource(R.string.min_unit)}", 0.93f, FitPlanCyan)
                }
            }
        }

        // Body Metrics Row
        item {
            Text(
                text = stringResource(R.string.tab_body_metrics),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = FitPlanTextWhite,
                modifier = Modifier.padding(horizontal = 16.dp).padding(top = 14.dp, bottom = 8.dp)
            )
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                bodyMetrics.forEach { metric ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(FitPlanCard)
                            .border(1.dp, FitPlanCardBorder, RoundedCornerShape(14.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                text = metric.title,
                                style = MaterialTheme.typography.bodySmall,
                                color = FitPlanTextSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${metric.current} ${metric.unit}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = FitPlanTextWhite
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = metric.delta,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (metric.isPositive) FitPlanNeonLime else FitPlanHiitRed,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Recent Workouts
        item {
            Text(
                text = stringResource(R.string.recent_workouts),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = FitPlanTextWhite,
                modifier = Modifier.padding(horizontal = 16.dp).padding(top = 20.dp, bottom = 8.dp)
            )
        }

        items(recentWorkouts.take(3)) { workout ->
            WorkoutCard(
                workout = workout,
                onWorkoutClick = { onWorkoutClick(workout.id) },
                onPlayClick = { onWorkoutClick(workout.id) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
fun GoalProgressRow(
    title: String,
    current: String,
    target: String,
    progress: Float,
    color: Color
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = FitPlanTextSecondary
            )
            Text(
                text = "$current / $target",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = FitPlanTextWhite
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape),
            color = color,
            trackColor = FitPlanSurface
        )
    }
}
