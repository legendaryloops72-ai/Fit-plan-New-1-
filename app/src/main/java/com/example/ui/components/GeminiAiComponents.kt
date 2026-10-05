package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.gemini.AiCoachMessage
import com.example.data.gemini.GeminiHealthRecommendations
import com.example.data.gemini.MessageSender
import com.example.ui.theme.*

@Composable
fun GeminiDailyTipsCard(
    recommendations: GeminiHealthRecommendations,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    onAskCoachClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Hydration", "Nutrition", "Movement", "Mindset")

    val infiniteTransition = rememberInfiniteTransition(label = "geminiGlow")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (isLoading) 360f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "loadingRotation"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF1E1528),
                        Color(0xFF151821),
                        FitPlanCard
                    )
                )
            )
            .border(
                1.5.dp,
                Brush.horizontalGradient(
                    listOf(
                        FitPlanYogaLavender.copy(alpha = 0.6f),
                        FitPlanCyan.copy(alpha = 0.5f),
                        FitPlanNeonLime.copy(alpha = 0.4f)
                    )
                ),
                RoundedCornerShape(22.dp)
            )
            .padding(18.dp)
            .testTag("gemini_daily_tips_card")
    ) {
        Column {
            // Header Row: Gemini AI Badge, Headline, and Refresh / Ask Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(FitPlanYogaLavender, FitPlanCyan)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Gemini AI",
                            tint = FitPlanBlack,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = stringResource(R.string.gemini_ai_health_tips),
                                style = MaterialTheme.typography.labelSmall,
                                color = FitPlanYogaLavender,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(FitPlanNeonLime.copy(alpha = 0.2f))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.badge_live),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = FitPlanNeonLime
                                )
                            }
                        }
                        Text(
                            text = recommendations.headline,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = FitPlanTextWhite,
                            maxLines = 1
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onRefresh,
                        enabled = !isLoading,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(FitPlanSurface)
                            .testTag("gemini_refresh_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh Gemini Tips",
                            tint = if (isLoading) FitPlanYogaLavender else FitPlanTextWhite,
                            modifier = Modifier
                                .size(18.dp)
                                .rotate(rotation)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tab Selector (Hydration, Nutrition, Movement, Mindset)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(FitPlanSurface)
                    .padding(3.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                tabs.forEachIndexed { index, tabName ->
                    val isSelected = selectedTab == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) FitPlanCard else Color.Transparent)
                            .clickable { selectedTab = index }
                            .padding(vertical = 7.dp)
                            .testTag("gemini_tab_$index"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tabName,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) FitPlanTextWhite else FitPlanTextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tip Content Area with Smooth Animation
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
                },
                label = "tipTransition"
            ) { targetTab ->
                val (tipText, icon, iconColor) = when (targetTab) {
                    0 -> Triple(recommendations.hydrationTip, Icons.Default.WaterDrop, FitPlanCyan)
                    1 -> Triple(recommendations.nutritionTip, Icons.Default.Restaurant, FitPlanNeonLime)
                    2 -> Triple(recommendations.movementTip, Icons.Default.DirectionsRun, FitPlanMorningOrange)
                    else -> Triple(recommendations.mindsetQuote, Icons.Default.EmojiEvents, FitPlanYogaLavender)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(FitPlanSurface.copy(alpha = 0.8f))
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(iconColor.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = iconColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = when (targetTab) {
                                    0 -> "Hydration Strategy"
                                    1 -> "Nutrition & Macro Balance"
                                    2 -> "Movement & Recovery"
                                    else -> "Daily Mindset"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = iconColor
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = tipText,
                                style = MaterialTheme.typography.bodySmall,
                                color = FitPlanTextWhite,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Ask Gemini Coach Action Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(FitPlanYogaLavender.copy(alpha = 0.12f))
                    .clickable { onAskCoachClick() }
                    .padding(horizontal = 14.dp, vertical = 10.dp)
                    .testTag("ask_gemini_coach_bar"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = null,
                        tint = FitPlanYogaLavender,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.have_questions_ask_coach),
                        style = MaterialTheme.typography.labelSmall,
                        color = FitPlanYogaLavender,
                        fontWeight = FontWeight.Bold
                    )
                }

                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = FitPlanYogaLavender,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun GeminiWorkoutRecommendationCard(
    recommendations: GeminiHealthRecommendations,
    onStartAiWorkout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(FitPlanCard)
            .border(
                1.dp,
                Brush.horizontalGradient(
                    listOf(FitPlanNeonLime.copy(alpha = 0.6f), FitPlanCyan.copy(alpha = 0.4f))
                ),
                RoundedCornerShape(22.dp)
            )
            .padding(18.dp)
            .testTag("gemini_workout_recommendation_card")
    ) {
        Column {
            // Header: AI Tag and Duration/Calories Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(FitPlanNeonLime.copy(alpha = 0.18f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = FitPlanNeonLime,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = stringResource(R.string.gemini_ai_pick),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                color = FitPlanNeonLime,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(FitPlanSurface)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = recommendations.workoutCategory.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = FitPlanTextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }

                // Difficulty tag
                Text(
                    text = recommendations.workoutDifficulty,
                    style = MaterialTheme.typography.labelSmall,
                    color = FitPlanMorningOrange,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Workout Title
            Text(
                text = recommendations.workoutTitle,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = FitPlanTextWhite
            )

            Spacer(modifier = Modifier.height(6.dp))

            // AI Rationale (Why this fits today's data)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(FitPlanSurface)
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = null,
                        tint = FitPlanCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = recommendations.workoutRationale,
                        style = MaterialTheme.typography.bodySmall,
                        color = FitPlanTextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Workout Stats & Exercise chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = FitPlanNeonLime,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${recommendations.workoutDurationMinutes} mins",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = FitPlanTextWhite
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = FitPlanHiitRed,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${recommendations.workoutCalories} kcal",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = FitPlanTextWhite
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FitnessCenter,
                        contentDescription = null,
                        tint = FitPlanCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${if (recommendations.exercises.isNotEmpty()) recommendations.exercises.size else 5} exercises",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = FitPlanTextWhite
                    )
                }
            }

            // Exercise Photo Cards in Gemini Recommendation
            if (recommendations.exercises.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(recommendations.exercises) { ex ->
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(FitPlanSurface)
                                .border(1.dp, FitPlanCardBorder, RoundedCornerShape(12.dp))
                                .padding(end = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (ex.fullAssetUrl.isNotEmpty()) {
                                AsyncImage(
                                    model = ex.fullAssetUrl,
                                    contentDescription = ex.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            } else {
                                Spacer(modifier = Modifier.width(10.dp))
                            }

                            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                Text(
                                    text = ex.name,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = FitPlanTextWhite,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = ex.reps,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = FitPlanNeonLime,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Start AI Workout Button
            Button(
                onClick = onStartAiWorkout,
                colors = ButtonDefaults.buttonColors(
                    containerColor = FitPlanNeonLime,
                    contentColor = FitPlanBlack
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("start_ai_workout_button")
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = FitPlanBlack,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.start_recommended_workout),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeminiCoachBottomSheet(
    chatMessages: List<AiCoachMessage>,
    isSending: Boolean,
    onSendMessage: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var queryText by remember { mutableStateOf("") }
    val suggestions = listOf(
        "What should I eat post-workout?",
        "How can I hit 10k steps today?",
        "Tips for faster muscle recovery",
        "Best stretching for tight hips"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = FitPlanCard,
        dragHandle = {
            BottomSheetDefaults.DragHandle(color = FitPlanCardBorder)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxHeight(0.85f)
        ) {
            // Coach Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(FitPlanYogaLavender, FitPlanCyan)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = FitPlanBlack,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = stringResource(R.string.gemini_ai_fitness_coach),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = FitPlanTextWhite
                        )
                        Text(
                            text = stringResource(R.string.ai_powered_gemini),
                            style = MaterialTheme.typography.bodySmall,
                            color = FitPlanYogaLavender,
                            fontSize = 11.sp
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = FitPlanTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Suggested Quick Prompt Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(suggestions) { prompt ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(FitPlanSurface)
                            .border(1.dp, FitPlanYogaLavender.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                            .clickable {
                                onSendMessage(prompt)
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = prompt,
                            style = MaterialTheme.typography.labelSmall,
                            color = FitPlanTextWhite,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Chat Messages List
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                androidx.compose.foundation.lazy.LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(chatMessages) { message ->
                        val isUser = message.sender == MessageSender.USER
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.85f)
                                    .clip(
                                        RoundedCornerShape(
                                            topStart = 16.dp,
                                            topEnd = 16.dp,
                                            bottomStart = if (isUser) 16.dp else 4.dp,
                                            bottomEnd = if (isUser) 4.dp else 16.dp
                                        )
                                    )
                                    .background(
                                        if (isUser) FitPlanYogaLavender.copy(alpha = 0.25f)
                                        else FitPlanSurface
                                    )
                                    .border(
                                        1.dp,
                                        if (isUser) FitPlanYogaLavender.copy(alpha = 0.5f) else FitPlanCardBorder,
                                        RoundedCornerShape(16.dp)
                                    )
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = if (isUser) "You" else "Gemini Coach",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isUser) FitPlanYogaLavender else FitPlanCyan
                                        )
                                        Text(
                                            text = message.timestamp,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = FitPlanTextMuted,
                                            fontSize = 9.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = message.text,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = FitPlanTextWhite,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }

                    if (isSending) {
                        item {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(8.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = FitPlanYogaLavender,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.ai_coach_analyzing_stats),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = FitPlanYogaLavender
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Input Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(FitPlanSurface)
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = queryText,
                    onValueChange = { queryText = it },
                    placeholder = {
                        Text(
                            text = stringResource(R.string.ai_coach_ask_placeholder),
                            color = FitPlanTextMuted,
                            fontSize = 13.sp
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedTextColor = FitPlanTextWhite,
                        unfocusedTextColor = FitPlanTextWhite,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("gemini_coach_input")
                )

                IconButton(
                    onClick = {
                        if (queryText.isNotBlank()) {
                            val text = queryText
                            queryText = ""
                            onSendMessage(text)
                        }
                    },
                    enabled = queryText.isNotBlank() && !isSending,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (queryText.isNotBlank()) FitPlanNeonLime else FitPlanCard)
                        .testTag("gemini_coach_send_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = if (queryText.isNotBlank()) FitPlanBlack else FitPlanTextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
