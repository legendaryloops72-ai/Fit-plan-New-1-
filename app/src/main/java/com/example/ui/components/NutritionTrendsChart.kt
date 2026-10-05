package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DailyNutritionTrend
import com.example.model.NutritionTrendMetric
import com.example.ui.theme.*

/**
 * High-performance Native Canvas visualization component displaying weekly daily calorie
 * and protein consumption trends with smooth Bezier curves, dual-bar gradients,
 * interactive touch exploration, target benchmark lines, and weekly summary analytics.
 */
@Composable
fun WeeklyNutritionTrendsChart(
    trends: List<DailyNutritionTrend>,
    modifier: Modifier = Modifier,
    initialMetric: NutritionTrendMetric = NutritionTrendMetric.COMBINED
) {
    var selectedMetric by remember { mutableStateOf(initialMetric) }
    var selectedDayIndex by remember { mutableStateOf(trends.indexOfFirst { it.isToday }.takeIf { it >= 0 } ?: (trends.size - 1)) }

    // Animation progress for smooth loading of the chart
    val animationProgress = remember { Animatable(0f) }
    LaunchedEffect(selectedMetric) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing)
        )
    }

    val selectedTrend = trends.getOrNull(selectedDayIndex) ?: trends.lastOrNull()

    val avgCalories = if (trends.isNotEmpty()) trends.map { it.calories }.average().toInt() else 0
    val avgProtein = if (trends.isNotEmpty()) trends.map { it.protein }.average().toInt() else 0
    val targetMetDays = trends.count { it.calorieGoalMet && it.proteinGoalMet }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(FitPlanCard)
            .border(1.dp, FitPlanCardBorder, RoundedCornerShape(22.dp))
            .padding(18.dp)
            .testTag("weekly_nutrition_trends_chart")
    ) {
        Column {
            // Header Row: Title & Subtitle + View Mode Metric Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(FitPlanMorningOrange.copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShowChart,
                                contentDescription = null,
                                tint = FitPlanMorningOrange,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = androidx.compose.ui.res.stringResource(com.example.R.string.weekly_nutrition_trends),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = FitPlanTextWhite
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = androidx.compose.ui.res.stringResource(com.example.R.string.daily_nutrition_trends_sub),
                        style = MaterialTheme.typography.bodySmall,
                        color = FitPlanTextSecondary,
                        fontSize = 11.sp
                    )
                }

                // Metric Toggle Chips (All / Calories / Protein)
                Row(
                    modifier = Modifier
                        .background(FitPlanSurface, RoundedCornerShape(10.dp))
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    MetricChip(
                        label = androidx.compose.ui.res.stringResource(com.example.R.string.cat_all),
                        isSelected = selectedMetric == NutritionTrendMetric.COMBINED,
                        selectedColor = FitPlanNeonLime,
                        onClick = { selectedMetric = NutritionTrendMetric.COMBINED }
                    )
                    MetricChip(
                        label = androidx.compose.ui.res.stringResource(com.example.R.string.kcal_unit),
                        isSelected = selectedMetric == NutritionTrendMetric.CALORIES,
                        selectedColor = FitPlanMorningOrange,
                        onClick = { selectedMetric = NutritionTrendMetric.CALORIES }
                    )
                    MetricChip(
                        label = androidx.compose.ui.res.stringResource(com.example.R.string.protein),
                        isSelected = selectedMetric == NutritionTrendMetric.PROTEIN,
                        selectedColor = FitPlanCyan,
                        onClick = { selectedMetric = NutritionTrendMetric.PROTEIN }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Interactive Day Inspector Tooltip Card
            if (selectedTrend != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(FitPlanSurface)
                        .border(1.dp, FitPlanCardBorder, RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (selectedTrend.fullDayName.isNotEmpty()) selectedTrend.fullDayName else selectedTrend.day,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = FitPlanTextWhite
                                )
                                if (selectedTrend.isToday) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .background(FitPlanNeonLime.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 5.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = androidx.compose.ui.res.stringResource(com.example.R.string.today_caps),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = FitPlanNeonLime,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 8.sp
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = androidx.compose.ui.res.stringResource(com.example.R.string.tap_to_inspect_macros),
                                style = MaterialTheme.typography.labelSmall,
                                color = FitPlanTextMuted,
                                fontSize = 9.sp
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Calorie stat
                            Column(horizontalAlignment = Alignment.End) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(FitPlanMorningOrange)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${selectedTrend.calories}",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Black,
                                        color = FitPlanMorningOrange
                                    )
                                    Text(
                                        text = " kcal",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = FitPlanTextSecondary,
                                        fontSize = 10.sp
                                    )
                                }
                                val calDelta = selectedTrend.calories - selectedTrend.calorieGoal
                                Text(
                                    text = if (calDelta >= 0) "+$calDelta vs goal" else "$calDelta vs goal",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (selectedTrend.calorieGoalMet) FitPlanNeonLime else FitPlanTextMuted,
                                    fontSize = 9.sp
                                )
                            }

                            // Protein stat
                            Column(horizontalAlignment = Alignment.End) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(FitPlanCyan)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${selectedTrend.protein}g",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Black,
                                        color = FitPlanCyan
                                    )
                                }
                                val proteinPercent = ((selectedTrend.protein.toFloat() / selectedTrend.proteinGoal) * 100).toInt()
                                Text(
                                    text = "$proteinPercent% target",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (selectedTrend.proteinGoalMet) FitPlanCyan else FitPlanTextMuted,
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Native Canvas Chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(trends) {
                            detectTapGestures { offset ->
                                val stepWidth = size.width / trends.size.toFloat()
                                val clickedIndex = (offset.x / stepWidth).toInt().coerceIn(0, trends.size - 1)
                                selectedDayIndex = clickedIndex
                            }
                        }
                ) {
                    val progress = animationProgress.value
                    val w = size.width
                    val h = size.height
                    val bottomPadding = 30f
                    val topPadding = 20f
                    val chartHeight = h - bottomPadding - topPadding

                    if (trends.isEmpty()) return@Canvas

                    // Draw Horizontal Goal Guidelines
                    val maxCalories = 3000f
                    val maxProtein = 200f

                    val calorieGoalY = topPadding + chartHeight * (1f - (2400f / maxCalories))
                    val proteinGoalY = topPadding + chartHeight * (1f - (160f / maxProtein))

                    // Draw background grid dashed lines
                    drawDottedLine(
                        start = Offset(0f, topPadding),
                        end = Offset(w, topPadding),
                        color = FitPlanCardBorder.copy(alpha = 0.5f)
                    )
                    drawDottedLine(
                        start = Offset(0f, topPadding + chartHeight * 0.5f),
                        end = Offset(w, topPadding + chartHeight * 0.5f),
                        color = FitPlanCardBorder.copy(alpha = 0.35f)
                    )
                    drawDottedLine(
                        start = Offset(0f, topPadding + chartHeight),
                        end = Offset(w, topPadding + chartHeight),
                        color = FitPlanCardBorder.copy(alpha = 0.7f)
                    )

                    val count = trends.size
                    val slotWidth = w / count.toFloat()

                    // Render chart based on selected metric mode
                    when (selectedMetric) {
                        NutritionTrendMetric.COMBINED -> {
                            // Draw Goal Reference Line (Calories)
                            drawLine(
                                color = FitPlanMorningOrange.copy(alpha = 0.35f),
                                start = Offset(0f, calorieGoalY),
                                end = Offset(w, calorieGoalY),
                                strokeWidth = 2f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                            )

                            val barGroupWidth = slotWidth * 0.55f
                            val individualBarWidth = barGroupWidth * 0.46f

                            trends.forEachIndexed { i, item ->
                                val centerX = i * slotWidth + slotWidth / 2f
                                val isSelected = i == selectedDayIndex

                                val calFraction = (item.calories.toFloat() / maxCalories).coerceIn(0.05f, 1f) * progress
                                val calBarHeight = chartHeight * calFraction
                                val calLeft = centerX - barGroupWidth / 2f

                                val proFraction = (item.protein.toFloat() / maxProtein).coerceIn(0.05f, 1f) * progress
                                val proBarHeight = chartHeight * proFraction
                                val proLeft = calLeft + individualBarWidth + (barGroupWidth * 0.08f)

                                val groundY = topPadding + chartHeight

                                // Selected day vertical indicator beam
                                if (isSelected) {
                                    drawRoundRect(
                                        color = FitPlanSurface.copy(alpha = 0.6f),
                                        topLeft = Offset(i * slotWidth + 4f, topPadding - 6f),
                                        size = Size(slotWidth - 8f, chartHeight + 12f),
                                        cornerRadius = CornerRadius(10f, 10f)
                                    )
                                }

                                // Calorie Bar (Orange Gradient)
                                val calGradient = Brush.verticalGradient(
                                    colors = listOf(
                                        FitPlanMorningOrange,
                                        FitPlanMorningOrange.copy(alpha = 0.45f)
                                    ),
                                    startY = groundY - calBarHeight,
                                    endY = groundY
                                )
                                drawRoundRect(
                                    brush = calGradient,
                                    topLeft = Offset(calLeft, groundY - calBarHeight),
                                    size = Size(individualBarWidth, calBarHeight),
                                    cornerRadius = CornerRadius(individualBarWidth / 2f, individualBarWidth / 2f)
                                )

                                // Protein Bar (Cyan Gradient)
                                val proGradient = Brush.verticalGradient(
                                    colors = listOf(
                                        FitPlanCyan,
                                        FitPlanCyan.copy(alpha = 0.45f)
                                    ),
                                    startY = groundY - proBarHeight,
                                    endY = groundY
                                )
                                drawRoundRect(
                                    brush = proGradient,
                                    topLeft = Offset(proLeft, groundY - proBarHeight),
                                    size = Size(individualBarWidth, proBarHeight),
                                    cornerRadius = CornerRadius(individualBarWidth / 2f, individualBarWidth / 2f)
                                )

                                // Highlight top ring if selected
                                if (isSelected) {
                                    drawCircle(
                                        color = Color.White,
                                        radius = 3.5f,
                                        center = Offset(calLeft + individualBarWidth / 2f, groundY - calBarHeight)
                                    )
                                    drawCircle(
                                        color = Color.White,
                                        radius = 3.5f,
                                        center = Offset(proLeft + individualBarWidth / 2f, groundY - proBarHeight)
                                    )
                                }
                            }
                        }

                        NutritionTrendMetric.CALORIES -> {
                            // Goal line
                            drawLine(
                                color = FitPlanMorningOrange.copy(alpha = 0.5f),
                                start = Offset(0f, calorieGoalY),
                                end = Offset(w, calorieGoalY),
                                strokeWidth = 2f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
                            )

                            drawSmoothCurveChart(
                                values = trends.map { (it.calories.toFloat() / maxCalories).coerceIn(0.05f, 1f) },
                                progress = progress,
                                topPadding = topPadding,
                                chartHeight = chartHeight,
                                slotWidth = slotWidth,
                                strokeColor = FitPlanMorningOrange,
                                gradientColors = listOf(
                                    FitPlanMorningOrange.copy(alpha = 0.45f),
                                    FitPlanMorningOrange.copy(alpha = 0.08f),
                                    Color.Transparent
                                ),
                                selectedIndex = selectedDayIndex
                            )
                        }

                        NutritionTrendMetric.PROTEIN -> {
                            // Goal line
                            drawLine(
                                color = FitPlanCyan.copy(alpha = 0.5f),
                                start = Offset(0f, proteinGoalY),
                                end = Offset(w, proteinGoalY),
                                strokeWidth = 2f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
                            )

                            drawSmoothCurveChart(
                                values = trends.map { (it.protein.toFloat() / maxProtein).coerceIn(0.05f, 1f) },
                                progress = progress,
                                topPadding = topPadding,
                                chartHeight = chartHeight,
                                slotWidth = slotWidth,
                                strokeColor = FitPlanCyan,
                                gradientColors = listOf(
                                    FitPlanCyan.copy(alpha = 0.5f),
                                    FitPlanNeonLime.copy(alpha = 0.1f),
                                    Color.Transparent
                                ),
                                selectedIndex = selectedDayIndex
                            )
                        }
                    }
                }
            }

            // X-Axis Day Labels Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                trends.forEachIndexed { index, item ->
                    val isSelected = index == selectedDayIndex
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isSelected) {
                                    when (selectedMetric) {
                                        NutritionTrendMetric.COMBINED -> FitPlanNeonLime.copy(alpha = 0.2f)
                                        NutritionTrendMetric.CALORIES -> FitPlanMorningOrange.copy(alpha = 0.2f)
                                        NutritionTrendMetric.PROTEIN -> FitPlanCyan.copy(alpha = 0.2f)
                                    }
                                } else Color.Transparent
                            )
                            .clickable { selectedDayIndex = index }
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.day,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal,
                            color = if (isSelected) {
                                when (selectedMetric) {
                                    NutritionTrendMetric.COMBINED -> FitPlanNeonLime
                                    NutritionTrendMetric.CALORIES -> FitPlanMorningOrange
                                    NutritionTrendMetric.PROTEIN -> FitPlanCyan
                                }
                            } else FitPlanTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Bottom Summary & Benchmark Legend Footer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(FitPlanSurface)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Calories Legend Dot
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(FitPlanMorningOrange)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = androidx.compose.ui.res.stringResource(com.example.R.string.calories),
                        style = MaterialTheme.typography.labelSmall,
                        color = FitPlanTextSecondary,
                        fontSize = 10.sp
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    // Protein Legend Dot
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(FitPlanCyan)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = androidx.compose.ui.res.stringResource(com.example.R.string.protein),
                        style = MaterialTheme.typography.labelSmall,
                        color = FitPlanTextSecondary,
                        fontSize = 10.sp
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    // Goal benchmark line indicator
                    Box(
                        modifier = Modifier
                            .width(12.dp)
                            .height(2.dp)
                            .background(FitPlanTextMuted)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = androidx.compose.ui.res.stringResource(com.example.R.string.target),
                        style = MaterialTheme.typography.labelSmall,
                        color = FitPlanTextMuted,
                        fontSize = 10.sp
                    )
                }

                Text(
                    text = "Avg: $avgCalories kcal • ${avgProtein}g",
                    style = MaterialTheme.typography.labelSmall,
                    color = FitPlanNeonLime,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            }
        }
    }
}

/**
 * Draws smooth Bezier curve with gradient area fill and data point rings on Canvas.
 */
private fun DrawScope.drawSmoothCurveChart(
    values: List<Float>,
    progress: Float,
    topPadding: Float,
    chartHeight: Float,
    slotWidth: Float,
    strokeColor: Color,
    gradientColors: List<Color>,
    selectedIndex: Int
) {
    if (values.size < 2) return

    val groundY = topPadding + chartHeight
    val points = values.mapIndexed { index, fraction ->
        val x = index * slotWidth + slotWidth / 2f
        val y = groundY - (chartHeight * fraction * progress)
        Offset(x, y)
    }

    // Build smooth Bezier Curve Path
    val path = Path()
    val fillPath = Path()

    path.moveTo(points.first().x, points.first().y)
    fillPath.moveTo(points.first().x, groundY)
    fillPath.lineTo(points.first().x, points.first().y)

    for (i in 0 until points.size - 1) {
        val p0 = points[i]
        val p1 = points[i + 1]
        val controlX = (p0.x + p1.x) / 2f

        path.cubicTo(
            controlX, p0.y,
            controlX, p1.y,
            p1.x, p1.y
        )
        fillPath.cubicTo(
            controlX, p0.y,
            controlX, p1.y,
            p1.x, p1.y
        )
    }

    fillPath.lineTo(points.last().x, groundY)
    fillPath.close()

    // Draw gradient area fill
    drawPath(
        path = fillPath,
        brush = Brush.verticalGradient(
            colors = gradientColors,
            startY = topPadding,
            endY = groundY
        )
    )

    // Draw main curve stroke line
    drawPath(
        path = path,
        color = strokeColor,
        style = Stroke(width = 4f, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )

    // Draw point circles
    points.forEachIndexed { index, pt ->
        val isSelected = index == selectedIndex
        if (isSelected) {
            // Draw vertical guide line
            drawLine(
                color = strokeColor.copy(alpha = 0.5f),
                start = Offset(pt.x, topPadding),
                end = Offset(pt.x, groundY),
                strokeWidth = 2f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
            )

            drawCircle(
                color = strokeColor.copy(alpha = 0.35f),
                radius = 12f,
                center = pt
            )
            drawCircle(
                color = strokeColor,
                radius = 6f,
                center = pt
            )
            drawCircle(
                color = Color.White,
                radius = 3f,
                center = pt
            )
        } else {
            drawCircle(
                color = FitPlanBlack,
                radius = 4.5f,
                center = pt
            )
            drawCircle(
                color = strokeColor,
                radius = 3f,
                center = pt
            )
        }
    }
}

/**
 * Draws dotted guideline on Canvas.
 */
private fun DrawScope.drawDottedLine(
    start: Offset,
    end: Offset,
    color: Color
) {
    drawLine(
        color = color,
        start = start,
        end = end,
        strokeWidth = 1f,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
    )
}

@Composable
private fun MetricChip(
    label: String,
    isSelected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) selectedColor else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
            color = if (isSelected) FitPlanBlack else FitPlanTextSecondary,
            fontSize = 10.sp
        )
    }
}
