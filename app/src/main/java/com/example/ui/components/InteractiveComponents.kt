package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.model.DailyStepStat
import com.example.model.WorkoutItem
import com.example.ui.theme.*

// 1. Metric Stat Card
@Composable
fun MetricStatCard(
    title: String,
    value: String,
    unit: String = "",
    icon: ImageVector,
    iconTint: Color = FitPlanNeonLime,
    subtitle: String? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(FitPlanCard)
            .border(1.dp, FitPlanCardBorder, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = FitPlanTextSecondary,
                    fontWeight = FontWeight.Medium
                )
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(iconTint.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = FitPlanTextWhite
                )
                if (unit.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = unit,
                        style = MaterialTheme.typography.bodySmall,
                        color = FitPlanTextSecondary,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
            }

            if (subtitle != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = FitPlanNeonLime,
                    fontSize = 10.sp
                )
            }
        }
    }
}

// 2. Today's Progress Circular Gauge
@Composable
fun TodayProgressCircle(
    progress: Float = 0.70f,
    steps: Int,
    stepsGoal: Int,
    calories: Int,
    activeMinutes: Int,
    onSeeAllClick: () -> Unit,
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
                        text = androidx.compose.ui.res.stringResource(com.example.R.string.todays_progress),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = FitPlanTextWhite
                    )
                    Text(
                        text = "${androidx.compose.ui.res.stringResource(com.example.R.string.daily_goal)}: ${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = FitPlanTextSecondary
                    )
                }
                TextButton(onClick = onSeeAllClick) {
                    Text(
                        text = androidx.compose.ui.res.stringResource(com.example.R.string.see_all),
                        color = FitPlanNeonLime,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circular Progress Arc
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val stroke = 10.dp.toPx()
                        // Track background
                        drawArc(
                            color = FitPlanSurface,
                            startAngle = 135f,
                            sweepAngle = 270f,
                            useCenter = false,
                            style = Stroke(width = stroke, cap = StrokeCap.Round)
                        )
                        // Progress arc
                        drawArc(
                            brush = Brush.sweepGradient(
                                listOf(FitPlanNeonLimeDark, FitPlanNeonLime, FitPlanCyan)
                            ),
                            startAngle = 135f,
                            sweepAngle = 270f * progress.coerceIn(0f, 1f),
                            useCenter = false,
                            style = Stroke(width = stroke, cap = StrokeCap.Round)
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = FitPlanTextWhite
                        )
                        Text(
                            text = androidx.compose.ui.res.stringResource(com.example.R.string.target),
                            style = MaterialTheme.typography.labelSmall,
                            color = FitPlanTextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(18.dp))

                // 3 Metrics Breakdown
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ProgressMetricRow(
                        label = androidx.compose.ui.res.stringResource(com.example.R.string.steps),
                        value = "$steps / $stepsGoal",
                        accentColor = FitPlanNeonLime,
                        icon = Icons.Default.DirectionsWalk
                    )
                    ProgressMetricRow(
                        label = androidx.compose.ui.res.stringResource(com.example.R.string.calories),
                        value = "$calories ${androidx.compose.ui.res.stringResource(com.example.R.string.kcal_unit)}",
                        accentColor = FitPlanMorningOrange,
                        icon = Icons.Default.LocalFireDepartment
                    )
                    ProgressMetricRow(
                        label = androidx.compose.ui.res.stringResource(com.example.R.string.minutes),
                        value = "$activeMinutes ${androidx.compose.ui.res.stringResource(com.example.R.string.min_unit)}",
                        accentColor = FitPlanCyan,
                        icon = Icons.Default.Timer
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Motivation Quote Pill
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(FitPlanSurface, RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = androidx.compose.ui.res.stringResource(com.example.R.string.small_steps_quote),
                    style = MaterialTheme.typography.bodySmall,
                    color = FitPlanNeonLime,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun ProgressMetricRow(
    label: String,
    value: String,
    accentColor: Color,
    icon: ImageVector
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(accentColor.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(12.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = FitPlanTextSecondary
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = FitPlanTextWhite
        )
    }
}

// 3. Weekly Activity Canvas Bar Chart
@Composable
fun WeeklyActivityChart(
    weeklyStats: List<DailyStepStat>,
    modifier: Modifier = Modifier
) {
    var selectedIndex by remember { mutableStateOf(3) } // Thursday default peak

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
                        text = androidx.compose.ui.res.stringResource(com.example.R.string.weekly_activity_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = FitPlanTextWhite
                    )
                    Text(
                        text = androidx.compose.ui.res.stringResource(com.example.R.string.vs_last_week),
                        style = MaterialTheme.typography.labelSmall,
                        color = FitPlanNeonLime,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    modifier = Modifier
                        .background(FitPlanSurface, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    val currentStat = weeklyStats.getOrNull(selectedIndex)
                    Text(
                        text = "${currentStat?.steps ?: 0} ${androidx.compose.ui.res.stringResource(com.example.R.string.steps)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = FitPlanNeonLime,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Bars Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                val maxSteps = (weeklyStats.maxOfOrNull { it.steps } ?: 12000).toFloat()

                weeklyStats.forEachIndexed { index, stat ->
                    val isSelected = index == selectedIndex
                    val heightRatio = (stat.steps / maxSteps).coerceIn(0.15f, 1.0f)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedIndex = index }
                    ) {
                        if (stat.isPeak) {
                            Box(
                                modifier = Modifier
                                    .background(FitPlanNeonLime, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = androidx.compose.ui.res.stringResource(com.example.R.string.peak_badge),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 8.sp,
                                    color = FitPlanBlack,
                                    fontWeight = FontWeight.Black
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }

                        // Bar
                        Box(
                            modifier = Modifier
                                .width(20.dp)
                                .height((100 * heightRatio).dp)
                                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                .background(
                                    if (isSelected || stat.isPeak) FitPlanNeonLime
                                    else FitPlanCardBorderLight
                                )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Day label
                        Text(
                            text = stat.day,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) FitPlanNeonLime else FitPlanTextSecondary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

// 4. Interactive Map Canvas (Neo-Brutalist Dark Map Preview)
@Composable
fun InteractiveMapCanvas(
    routeColor: Color = FitPlanNeonLime,
    showCompass: Boolean = true,
    showGpsPulse: Boolean = true,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 8f,
        targetValue = 24f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseScale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(FitPlanBlack)
            .border(1.dp, FitPlanCardBorder, RoundedCornerShape(18.dp))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Background Map Grid & Roads
            val roadColor = Color(0xFF1E222A)
            val pathColor = Color(0xFF282F3B)

            // Street lines
            drawLine(roadColor, Offset(0f, h * 0.3f), Offset(w, h * 0.3f), strokeWidth = 8f)
            drawLine(roadColor, Offset(0f, h * 0.7f), Offset(w, h * 0.7f), strokeWidth = 12f)
            drawLine(roadColor, Offset(w * 0.25f, 0f), Offset(w * 0.25f, h), strokeWidth = 8f)
            drawLine(roadColor, Offset(w * 0.75f, 0f), Offset(w * 0.75f, h), strokeWidth = 10f)

            // Stylized park area
            drawRect(
                color = Color(0xFF121B16),
                topLeft = Offset(w * 0.3f, h * 0.35f),
                size = Size(w * 0.4f, h * 0.3f)
            )

            // Running/Cycling Route Polyline
            val routePath = Path().apply {
                moveTo(w * 0.15f, h * 0.75f)
                cubicTo(
                    w * 0.2f, h * 0.4f,
                    w * 0.4f, h * 0.2f,
                    w * 0.55f, h * 0.35f
                )
                cubicTo(
                    w * 0.7f, h * 0.5f,
                    w * 0.8f, h * 0.25f,
                    w * 0.85f, h * 0.65f
                )
            }

            // Glow around route
            drawPath(
                path = routePath,
                color = routeColor.copy(alpha = 0.3f),
                style = Stroke(width = 12f, cap = StrokeCap.Round)
            )
            // Main route
            drawPath(
                path = routePath,
                color = routeColor,
                style = Stroke(width = 5f, cap = StrokeCap.Round)
            )

            // Start Dot
            drawCircle(Color(0xFF2ED573), radius = 7f, center = Offset(w * 0.15f, h * 0.75f))
            drawCircle(Color.White, radius = 3f, center = Offset(w * 0.15f, h * 0.75f))

            // Current / Live GPS pulse
            if (showGpsPulse) {
                val currentPos = Offset(w * 0.85f, h * 0.65f)
                drawCircle(
                    color = routeColor.copy(alpha = pulseAlpha),
                    radius = pulseScale,
                    center = currentPos
                )
                drawCircle(color = routeColor, radius = 8f, center = currentPos)
                drawCircle(color = Color.White, radius = 4f, center = currentPos)
            }
        }

        // Top right Map overlays
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(FitPlanSurface.copy(alpha = 0.85f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Layers,
                    contentDescription = "Map Layers",
                    tint = FitPlanTextWhite,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(FitPlanSurface.copy(alpha = 0.85f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Explore,
                    contentDescription = "Compass",
                    tint = FitPlanNeonLime,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Bottom left Live Route badge
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
                .background(FitPlanSurface.copy(alpha = 0.85f), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = "Riverside Park Trail • 5.2 km",
                style = MaterialTheme.typography.labelSmall,
                color = FitPlanTextWhite,
                fontSize = 10.sp
            )
        }
    }
}

// 5. Workout Card
@Composable
fun WorkoutCard(
    workout: WorkoutItem,
    onWorkoutClick: () -> Unit,
    onPlayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = Color(workout.accentColorHex)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(FitPlanCard)
            .border(1.dp, FitPlanCardBorder, RoundedCornerShape(18.dp))
            .clickable { onWorkoutClick() }
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Exercise / Workout Real Photo Thumbnail
            if (workout.fullAssetUrl.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(FitPlanSurface)
                        .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                ) {
                    AsyncImage(
                        model = workout.fullAssetUrl,
                        contentDescription = workout.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                // Category Tag
                Box(
                    modifier = Modifier
                        .background(accentColor.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = com.example.ui.FitPlanLocalization.getCategoryLabel(workout.category).uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = accentColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = com.example.ui.FitPlanLocalization.getWorkoutTitle(workout),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = FitPlanTextWhite
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = FitPlanTextSecondary,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${workout.durationMinutes} ${androidx.compose.ui.res.stringResource(com.example.R.string.min_unit)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = FitPlanTextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = FitPlanMorningOrange,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${workout.calories} ${androidx.compose.ui.res.stringResource(com.example.R.string.kcal_unit)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = FitPlanTextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = "• ${workout.exercises.size.takeIf { it > 0 } ?: workout.exercisesCount} ${androidx.compose.ui.res.stringResource(com.example.R.string.moves_unit)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = FitPlanTextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Play Circle Button
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(accentColor)
                    .clickable { onPlayClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Start",
                    tint = FitPlanBlack,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

// 6. Category Filter Tabs Row
@Composable
fun CategoryTabsRow(
    tabs: List<String>,
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    activeColor: Color = FitPlanNeonLime,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(tabs) { tab ->
            val isSelected = tab.equals(selectedTab, ignoreCase = true)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) activeColor else FitPlanCard)
                    .border(
                        1.dp,
                        if (isSelected) activeColor else FitPlanCardBorder,
                        RoundedCornerShape(20.dp)
                    )
                    .clickable { onTabSelected(tab) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = tab,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) FitPlanBlack else FitPlanTextSecondary
                )
            }
        }
    }
}

// 7. Water Tracker Widget (Dashboard & Nutrition Counter with Goal Progress)
@Composable
fun WaterTrackerWidget(
    currentGlasses: Int,
    targetGlasses: Int = 8,
    onAddGlass: () -> Unit,
    onRemoveGlass: (() -> Unit)? = null,
    onSetGlasses: ((Int) -> Unit)? = null,
    onReset: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val mlPerGlass = 250
    val currentMl = currentGlasses * mlPerGlass
    val targetMl = targetGlasses * mlPerGlass
    val progress = (currentGlasses.toFloat() / targetGlasses.toFloat()).coerceIn(0f, 1f)
    val isGoalAchieved = currentGlasses >= targetGlasses

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "waterProgress"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(FitPlanCard)
            .border(1.dp, if (isGoalAchieved) FitPlanCyan.copy(alpha = 0.5f) else FitPlanCardBorder, RoundedCornerShape(20.dp))
            .padding(18.dp)
            .testTag("water_intake_widget")
    ) {
        Column {
            // Header Row: Title, Subtitle, and Goal Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(FitPlanCyan.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = null,
                            tint = FitPlanCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = stringResource(R.string.daily_water_intake),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = FitPlanTextWhite
                        )
                        Text(
                            text = if (isGoalAchieved) "🎉 Daily goal completed!" else "$currentMl ml of $targetMl ml goal",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isGoalAchieved) FitPlanNeonLime else FitPlanTextSecondary
                        )
                    }
                }

                // Completion percentage badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isGoalAchieved) FitPlanNeonLime.copy(alpha = 0.2f) else FitPlanCyan.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isGoalAchieved) FitPlanNeonLime else FitPlanCyan,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Goal Progress Indicator (Linear Bar)
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "$currentGlasses / $targetGlasses glasses ($currentMl ml)",
                        style = MaterialTheme.typography.labelSmall,
                        color = FitPlanTextWhite,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Goal: $targetMl ml",
                        style = MaterialTheme.typography.labelSmall,
                        color = FitPlanTextMuted
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Custom Gradient Progress Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(FitPlanSurface)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedProgress)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(5.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(FitPlanCyanDark, FitPlanCyan, FitPlanNeonLime)
                                )
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Interactive Counter Row (- / count / +)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(FitPlanSurface)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Decrement Button (-)
                IconButton(
                    onClick = { onRemoveGlass?.invoke() },
                    enabled = currentGlasses > 0 && onRemoveGlass != null,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            if (currentGlasses > 0 && onRemoveGlass != null) FitPlanCard
                            else FitPlanCard.copy(alpha = 0.4f)
                        )
                        .testTag("water_counter_decrement")
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Decrease water intake",
                        tint = if (currentGlasses > 0 && onRemoveGlass != null) FitPlanTextWhite else FitPlanTextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Center Counter Display
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.testTag("water_counter_value")
                ) {
                    Text(
                        text = "$currentGlasses",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        color = FitPlanCyan
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.glasses_unit),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = FitPlanTextWhite
                        )
                        Text(
                            text = "${currentGlasses * 250} ml logged",
                            style = MaterialTheme.typography.labelSmall,
                            color = FitPlanTextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }

                // Increment Button (+)
                Button(
                    onClick = onAddGlass,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FitPlanCyan,
                        contentColor = FitPlanBlack
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("water_counter_increment")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = FitPlanBlack
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "+250ml",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelMedium,
                        color = FitPlanBlack
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Interactive Glass Visual Grid / Tap-to-Log
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (i in 1..targetGlasses) {
                    val isFilled = i <= currentGlasses
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 2.dp)
                            .height(38.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isFilled) FitPlanCyan else FitPlanSurface)
                            .border(
                                1.dp,
                                if (isFilled) FitPlanCyan else FitPlanCardBorder,
                                RoundedCornerShape(6.dp)
                            )
                            .clickable {
                                if (onSetGlasses != null) {
                                    onSetGlasses(i)
                                } else if (i > currentGlasses) {
                                    onAddGlass()
                                }
                            }
                            .testTag("water_glass_$i"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = "Glass $i",
                            tint = if (isFilled) FitPlanBlack else FitPlanTextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
