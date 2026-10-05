package com.example.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.ActivitySession
import com.example.model.ActivityType
import com.example.ui.ScreenDestination
import com.example.ui.components.*
import com.example.ui.theme.*

// 1. Main Activities Screen
@Composable
fun ActivitiesScreen(
    sessions: List<ActivitySession>,
    onNavigate: (ScreenDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf("All Activities") }
    val tabs = listOf("All Activities", "Outdoor", "Indoor", "Sports", "Mind & Body")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(FitPlanBlack)
            .testTag("activities_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        item {
            FitPlanTopBar(
                onSearchClick = { /* Search activities */ }
            )
        }

        item {
            HeroBanner(
                badgeText = "STAY ACTIVE",
                title = stringResource(R.string.activities_subtitle),
                subtitle = "Track real outdoor workouts, GPS routes, and cardio stats",
                ctaText = "RUNNING",
                accentColor = FitPlanNeonLime,
                onCtaClick = { onNavigate(ScreenDestination.Running) }
            )
        }

        item {
            CategoryTabsRow(
                tabs = tabs,
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                activeColor = FitPlanNeonLime
            )
        }

        // Today's Activity Highlight Card
        item {
            TodayActivitySummaryCard(
                onStartRun = { onNavigate(ScreenDestination.Running) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // Popular Activities 2x3 Grid
        item {
            Text(
                text = stringResource(R.string.popular_activities),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = FitPlanTextWhite,
                modifier = Modifier.padding(horizontal = 16.dp).padding(top = 14.dp, bottom = 10.dp)
            )
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PopularActivityTile(
                        title = "Running",
                        subtitle = "GPS Pace & Route",
                        icon = Icons.Default.DirectionsRun,
                        accentColor = FitPlanNeonLime,
                        onClick = { onNavigate(ScreenDestination.Running) },
                        modifier = Modifier.weight(1f)
                    )
                    PopularActivityTile(
                        title = "Cycling",
                        subtitle = "Speed & Distance",
                        icon = Icons.Default.DirectionsBike,
                        accentColor = FitPlanCyan,
                        onClick = { onNavigate(ScreenDestination.Cycling) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PopularActivityTile(
                        title = "Walking",
                        subtitle = "Steps & Calories",
                        icon = Icons.Default.DirectionsWalk,
                        accentColor = FitPlanMorningOrange,
                        onClick = { onNavigate(ScreenDestination.Walking) },
                        modifier = Modifier.weight(1f)
                    )
                    PopularActivityTile(
                        title = "Home Workout",
                        subtitle = "Bodyweight",
                        icon = Icons.Default.FitnessCenter,
                        accentColor = FitPlanNeonLime,
                        onClick = { onNavigate(ScreenDestination.HomeWorkout) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PopularActivityTile(
                        title = "Yoga",
                        subtitle = "Mind & Flow",
                        icon = Icons.Default.SelfImprovement,
                        accentColor = FitPlanYogaLavender,
                        onClick = { onNavigate(ScreenDestination.Yoga) },
                        modifier = Modifier.weight(1f)
                    )
                    PopularActivityTile(
                        title = "Stretching",
                        subtitle = "Full Mobility",
                        icon = Icons.Default.AccessibilityNew,
                        accentColor = FitPlanCyan,
                        onClick = { onNavigate(ScreenDestination.Stretching) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Recent Sessions
        item {
            Text(
                text = stringResource(R.string.recent_sessions),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = FitPlanTextWhite,
                modifier = Modifier.padding(horizontal = 16.dp).padding(top = 20.dp, bottom = 8.dp)
            )
        }

        items(sessions) { session ->
            ActivitySessionCard(
                session = session,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
fun TodayActivitySummaryCard(
    onStartRun: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(FitPlanCard)
            .border(1.dp, FitPlanCardBorder, RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.todays_activity_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = FitPlanTextWhite
                    )
                    Text(
                        text = "Morning Outdoor Run • GPS Tracked",
                        style = MaterialTheme.typography.bodySmall,
                        color = FitPlanTextSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(FitPlanNeonLime)
                        .clickable { onStartRun() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Start",
                        tint = FitPlanBlack,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricStatCard("Distance", "5.24", "km", Icons.Default.Route, FitPlanNeonLime, modifier = Modifier.weight(1f))
                MetricStatCard("Duration", "28", "min", Icons.Default.Timer, FitPlanCyan, modifier = Modifier.weight(1f))
                MetricStatCard("Calories", "390", "kcal", Icons.Default.LocalFireDepartment, FitPlanMorningOrange, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun PopularActivityTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(FitPlanCard)
            .border(1.dp, FitPlanCardBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = FitPlanTextWhite
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = FitPlanTextSecondary,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun ActivitySessionCard(
    session: ActivitySession,
    modifier: Modifier = Modifier
) {
    val icon = when (session.type) {
        ActivityType.RUNNING -> Icons.Default.DirectionsRun
        ActivityType.CYCLING -> Icons.Default.DirectionsBike
        ActivityType.WALKING -> Icons.Default.DirectionsWalk
        else -> Icons.Default.FitnessCenter
    }
    val color = when (session.type) {
        ActivityType.RUNNING -> FitPlanNeonLime
        ActivityType.CYCLING -> FitPlanCyan
        ActivityType.WALKING -> FitPlanMorningOrange
        else -> FitPlanTextWhite
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(FitPlanCard)
            .border(1.dp, FitPlanCardBorder, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(color.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "${session.type.titleResId} • ${session.routeName}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = FitPlanTextWhite
                    )
                    Text(
                        text = "${session.date} • ${session.durationMinutes} min • ${session.avgPaceOrSpeed}",
                        style = MaterialTheme.typography.bodySmall,
                        color = FitPlanTextSecondary
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${session.distanceKm} km",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Black,
                    color = FitPlanTextWhite
                )
                Text(
                    text = "${session.caloriesBurned} kcal",
                    style = MaterialTheme.typography.labelSmall,
                    color = FitPlanMorningOrange
                )
            }
        }
    }
}

// --------------------------------------------------------------------
// 2. Running Screen (Mockup: GPS Ready, Tabs, Metrics, Map, Goals, Voice Coach, Start Run)
// --------------------------------------------------------------------
@Composable
fun RunningScreen(
    voiceCoachEnabled: Boolean,
    onToggleVoiceCoach: () -> Unit,
    onBackClick: () -> Unit,
    onStartRun: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf("Outdoor") }
    val tabs = listOf("Outdoor", "Treadmill", "Trail", "Custom")

    var selectedGoal by remember { mutableStateOf("Free Run") }
    val goals = listOf("Free Run", "5 km", "10 km", "30 min")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(FitPlanBlack)
            .testTag("running_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        item {
            FitPlanTopBar(
                title = stringResource(R.string.running),
                showBackButton = true,
                onBackClick = onBackClick,
                showGpsReady = true
            )
        }

        // Hero Banner
        item {
            HeroBanner(
                badgeText = "RUNNING",
                title = stringResource(R.string.run_further_stronger),
                subtitle = "Track your cadence, heart rate zones, and GPS routes",
                ctaText = "START RUN",
                accentColor = FitPlanNeonLime,
                onCtaClick = onStartRun
            )
        }

        // Mode tabs (Outdoor, Treadmill, Trail, Custom)
        item {
            CategoryTabsRow(
                tabs = tabs,
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                activeColor = FitPlanNeonLime
            )
        }

        // 4 Running Metrics
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricStatCard("Distance", "5.24", "km", Icons.Default.Route, FitPlanNeonLime, modifier = Modifier.weight(1f))
                MetricStatCard("Duration", "28:15", "", Icons.Default.Timer, FitPlanCyan, modifier = Modifier.weight(1f))
                MetricStatCard("Avg. Pace", "5'21\"", "/km", Icons.Default.Speed, FitPlanMorningOrange, modifier = Modifier.weight(1f))
                MetricStatCard("Calories", "390", "kcal", Icons.Default.LocalFireDepartment, FitPlanHiitRed, modifier = Modifier.weight(1f))
            }
        }

        // Interactive Map Preview
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.route_preview),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = FitPlanTextWhite
                    )
                    Text(
                        text = stringResource(R.string.gps_high_accuracy),
                        style = MaterialTheme.typography.labelSmall,
                        color = FitPlanNeonLime,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                InteractiveMapCanvas(
                    routeColor = FitPlanNeonLime,
                    showGpsPulse = true
                )
            }
        }

        // Set Your Goal
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.set_your_goal),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = FitPlanTextWhite
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    goals.forEach { goal ->
                        val isSelected = goal == selectedGoal
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) FitPlanNeonLime else FitPlanCard)
                                .border(1.dp, if (isSelected) FitPlanNeonLime else FitPlanCardBorder, RoundedCornerShape(12.dp))
                                .clickable { selectedGoal = goal }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = goal,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) FitPlanBlack else FitPlanTextSecondary
                            )
                        }
                    }
                }
            }
        }

        // Voice Coach & Music Controls
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(FitPlanCard)
                    .border(1.dp, FitPlanCardBorder, RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(FitPlanNeonLime.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.RecordVoiceOver,
                                contentDescription = null,
                                tint = FitPlanNeonLime,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = stringResource(R.string.voice_coach),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = FitPlanTextWhite
                            )
                            Text(
                                text = stringResource(R.string.voice_coach_desc),
                                style = MaterialTheme.typography.bodySmall,
                                color = FitPlanTextSecondary
                            )
                        }
                    }

                    Switch(
                        checked = voiceCoachEnabled,
                        onCheckedChange = { onToggleVoiceCoach() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = FitPlanBlack,
                            checkedTrackColor = FitPlanNeonLime,
                            uncheckedTrackColor = FitPlanSurface
                        )
                    )
                }
            }
        }

        // Large Start Run Button
        item {
            Button(
                onClick = onStartRun,
                colors = ButtonDefaults.buttonColors(
                    containerColor = FitPlanNeonLime,
                    contentColor = FitPlanBlack
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
                    .height(56.dp)
                    .testTag("start_run_button")
            ) {
                Icon(imageVector = Icons.Default.DirectionsRun, contentDescription = null, tint = FitPlanBlack)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.start_run).uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }
        }

        // Summary Statistics Row: Last Run, This Week, Best Distance
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricStatCard("Last Run", "5.24", "km", Icons.Default.History, FitPlanNeonLime, modifier = Modifier.weight(1f))
                MetricStatCard("This Week", "18.6", "km", Icons.Default.CalendarToday, FitPlanCyan, modifier = Modifier.weight(1f))
                MetricStatCard("Best Run", "12.4", "km", Icons.Default.EmojiEvents, FitPlanMorningOrange, modifier = Modifier.weight(1f))
            }
        }
    }
}

// --------------------------------------------------------------------
// 3. Cycling Screen (Mockup: Outdoor/Indoor/Routes/Challenges, Speed, Routes)
// --------------------------------------------------------------------
@Composable
fun CyclingScreen(
    onBackClick: () -> Unit,
    onStartCycling: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf("Outdoor") }
    val tabs = listOf("Outdoor", "Indoor", "Routes", "Challenges")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(FitPlanBlack)
            .testTag("cycling_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        item {
            FitPlanTopBar(
                title = stringResource(R.string.cycling),
                showBackButton = true,
                onBackClick = onBackClick,
                showGpsReady = true
            )
        }

        item {
            HeroBanner(
                badgeText = "CYCLING",
                title = stringResource(R.string.pedal_today),
                subtitle = "Track cadence, elevation gain, and maximum speeds",
                ctaText = "START CYCLING",
                accentColor = FitPlanCyan,
                onCtaClick = onStartCycling
            )
        }

        item {
            CategoryTabsRow(
                tabs = tabs,
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                activeColor = FitPlanCyan
            )
        }

        // Metrics
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricStatCard("Distance", "14.80", "km", Icons.Default.Route, FitPlanCyan, modifier = Modifier.weight(1f))
                MetricStatCard("Duration", "44:10", "", Icons.Default.Timer, FitPlanNeonLime, modifier = Modifier.weight(1f))
                MetricStatCard("Speed", "20.2", "km/h", Icons.Default.Speed, FitPlanMorningOrange, modifier = Modifier.weight(1f))
                MetricStatCard("Calories", "480", "kcal", Icons.Default.LocalFireDepartment, FitPlanHiitRed, modifier = Modifier.weight(1f))
            }
        }

        // Map
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "Route Preview • Coastal Highway",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = FitPlanTextWhite
                )
                Spacer(modifier = Modifier.height(10.dp))
                InteractiveMapCanvas(routeColor = FitPlanCyan)
            }
        }

        // Start Cycling Button
        item {
            Button(
                onClick = onStartCycling,
                colors = ButtonDefaults.buttonColors(
                    containerColor = FitPlanCyan,
                    contentColor = FitPlanBlack
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .height(56.dp)
            ) {
                Icon(imageVector = Icons.Default.DirectionsBike, contentDescription = null, tint = FitPlanBlack)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.start_cycling).uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }
        }

        // Popular Routes
        item {
            Text(
                text = stringResource(R.string.popular_routes),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = FitPlanTextWhite,
                modifier = Modifier.padding(horizontal = 16.dp).padding(top = 10.dp, bottom = 8.dp)
            )
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PopularRouteCard("Coastal Highway Loop", "14.8 km • +120m elevation", "Medium")
                PopularRouteCard("Sunset Hill Climb", "8.2 km • +280m elevation", "Hard")
                PopularRouteCard("Lakeside Green Belt", "22.5 km • Flat & fast", "Easy")
            }
        }
    }
}

@Composable
fun PopularRouteCard(name: String, desc: String, difficulty: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(FitPlanCard)
            .border(1.dp, FitPlanCardBorder, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = FitPlanTextWhite)
                Text(text = desc, style = MaterialTheme.typography.bodySmall, color = FitPlanTextSecondary)
            }
            Box(
                modifier = Modifier
                    .background(FitPlanSurface, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(text = difficulty, style = MaterialTheme.typography.labelSmall, color = FitPlanCyan, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// --------------------------------------------------------------------
// 4. Walking Screen (Mockup: Daily Goal 5,600 / 10,000 Ring, Metrics, Map)
// --------------------------------------------------------------------
@Composable
fun WalkingScreen(
    onBackClick: () -> Unit,
    onStartWalking: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(FitPlanBlack)
            .testTag("walking_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        item {
            FitPlanTopBar(
                title = stringResource(R.string.walking),
                showBackButton = true,
                onBackClick = onBackClick,
                showGpsReady = true
            )
        }

        item {
            HeroBanner(
                badgeText = "WALKING",
                title = stringResource(R.string.every_step_counts),
                subtitle = "Build daily momentum, lower stress, and boost metabolism",
                ctaText = "START WALKING",
                accentColor = FitPlanMorningOrange,
                onCtaClick = onStartWalking
            )
        }

        // Daily Walk Goal Card with Arc
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(FitPlanCard)
                    .border(1.dp, FitPlanCardBorder, RoundedCornerShape(20.dp))
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(90.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawArc(
                                color = FitPlanSurface,
                                startAngle = 135f,
                                sweepAngle = 270f,
                                useCenter = false,
                                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 18f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                            )
                            drawArc(
                                color = FitPlanMorningOrange,
                                startAngle = 135f,
                                sweepAngle = 270f * 0.56f,
                                useCenter = false,
                                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 18f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("56%", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = FitPlanTextWhite)
                            Text(stringResource(R.string.activity_goal_label), style = MaterialTheme.typography.labelSmall, color = FitPlanTextSecondary, fontSize = 9.sp)
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(stringResource(R.string.daily_walk_goal), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = FitPlanTextWhite)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("5,600 / 10,000 steps today", style = MaterialTheme.typography.bodyMedium, color = FitPlanMorningOrange, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("3.6 km walked • 185 kcal burned", style = MaterialTheme.typography.bodySmall, color = FitPlanTextSecondary)
                    }
                }
            }
        }

        // Metrics
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricStatCard("Steps", "5,600", "", Icons.Default.DirectionsWalk, FitPlanMorningOrange, modifier = Modifier.weight(1f))
                MetricStatCard("Distance", "3.60", "km", Icons.Default.Route, FitPlanNeonLime, modifier = Modifier.weight(1f))
                MetricStatCard("Duration", "42:00", "", Icons.Default.Timer, FitPlanCyan, modifier = Modifier.weight(1f))
                MetricStatCard("Calories", "185", "kcal", Icons.Default.LocalFireDepartment, FitPlanHiitRed, modifier = Modifier.weight(1f))
            }
        }

        // Map Preview
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "Walk Route • Sunset Boulevard",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = FitPlanTextWhite
                )
                Spacer(modifier = Modifier.height(10.dp))
                InteractiveMapCanvas(routeColor = FitPlanMorningOrange)
            }
        }

        // Start Walking Button
        item {
            Button(
                onClick = onStartWalking,
                colors = ButtonDefaults.buttonColors(
                    containerColor = FitPlanMorningOrange,
                    contentColor = FitPlanBlack
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .height(56.dp)
            ) {
                Icon(imageVector = Icons.Default.DirectionsWalk, contentDescription = null, tint = FitPlanBlack)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.start_walking).uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}
