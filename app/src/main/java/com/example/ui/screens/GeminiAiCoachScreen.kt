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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.gemini.AiCoachMessage
import com.example.data.gemini.GeminiHealthRecommendations
import com.example.model.UserProfile
import com.example.ui.components.GeminiDailyTipsCard
import com.example.ui.components.GeminiWorkoutRecommendationCard
import com.example.ui.theme.*

@Composable
fun GeminiAiCoachScreen(
    userProfile: UserProfile,
    todaySteps: Int,
    todayCalories: Int,
    todayMinutes: Int,
    waterGlasses: Int,
    recommendations: GeminiHealthRecommendations,
    isLoading: Boolean,
    chatMessages: List<AiCoachMessage>,
    isSendingCoach: Boolean,
    onRefreshRecommendations: () -> Unit,
    onSendMessage: (String) -> Unit,
    onStartAiWorkout: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var queryInput by remember { mutableStateOf("") }
    val quickQuestions = listOf(
        "Suggest a high protein dinner",
        "How to avoid knee pain during squats?",
        "Adjust workout for active recovery",
        "Hydration timing for workout"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(FitPlanBlack)
            .testTag("gemini_ai_coach_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Top Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(FitPlanCard)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = FitPlanTextWhite
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.ai_personal_coach),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
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

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(FitPlanYogaLavender.copy(alpha = 0.25f), FitPlanCyan.copy(alpha = 0.2f))
                            )
                        )
                        .border(1.dp, FitPlanYogaLavender.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = FitPlanYogaLavender,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(R.string.ai_active_badge),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black,
                            color = FitPlanYogaLavender,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        // Live Health Data Analysis Summary Card
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
                Column {
                    Text(
                        text = stringResource(R.string.ai_data_analyzed_today),
                        style = MaterialTheme.typography.labelSmall,
                        color = FitPlanTextSecondary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "$todaySteps",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = FitPlanNeonLime
                            )
                            Text(text = stringResource(R.string.steps), style = MaterialTheme.typography.labelSmall, color = FitPlanTextMuted)
                        }
                        Column {
                            Text(
                                text = "$todayCalories ${stringResource(R.string.kcal_unit)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = FitPlanHiitRed
                            )
                            Text(text = stringResource(R.string.burn), style = MaterialTheme.typography.labelSmall, color = FitPlanTextMuted)
                        }
                        Column {
                            Text(
                                text = "${waterGlasses * 250} ml",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = FitPlanCyan
                            )
                            Text(text = stringResource(R.string.water), style = MaterialTheme.typography.labelSmall, color = FitPlanTextMuted)
                        }
                        Column {
                            Text(
                                text = "${userProfile.weightKg} kg",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = FitPlanMorningOrange
                            )
                            Text(text = stringResource(R.string.weight), style = MaterialTheme.typography.labelSmall, color = FitPlanTextMuted)
                        }
                    }
                }
            }
        }

        // Gemini Daily Tips Card
        item {
            GeminiDailyTipsCard(
                recommendations = recommendations,
                isLoading = isLoading,
                onRefresh = onRefreshRecommendations,
                onAskCoachClick = { /* Already on coach screen, focus chat */ },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // Gemini Personalized Workout Recommendation
        item {
            GeminiWorkoutRecommendationCard(
                recommendations = recommendations,
                onStartAiWorkout = onStartAiWorkout,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // Ask Coach Section Header
        item {
            Text(
                text = stringResource(R.string.ai_live_chat_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = FitPlanTextWhite,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }

        // Quick Suggestion Chips
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                quickQuestions.chunked(2).forEach { rowItems ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowItems.forEach { q ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(FitPlanSurface)
                                    .border(1.dp, FitPlanYogaLavender.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                                    .clickable { onSendMessage(q) }
                                    .padding(horizontal = 10.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = q,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = FitPlanTextWhite,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Chat Messages List
        items(chatMessages) { msg ->
            val isUser = msg.sender == com.example.data.gemini.MessageSender.USER
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
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
                            if (isUser) FitPlanYogaLavender.copy(alpha = 0.25f) else FitPlanCard
                        )
                        .border(
                            1.dp,
                            if (isUser) FitPlanYogaLavender.copy(alpha = 0.5f) else FitPlanCardBorder,
                            RoundedCornerShape(16.dp)
                        )
                        .padding(14.dp)
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
                                text = msg.timestamp,
                                style = MaterialTheme.typography.labelSmall,
                                color = FitPlanTextMuted,
                                fontSize = 9.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = msg.text,
                            style = MaterialTheme.typography.bodyMedium,
                            color = FitPlanTextWhite,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }

        if (isSendingCoach) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = FitPlanYogaLavender,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = stringResource(R.string.ai_coach_writing_advice),
                        style = MaterialTheme.typography.bodySmall,
                        color = FitPlanYogaLavender
                    )
                }
            }
        }

        // Input Box
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(FitPlanCard)
                    .border(1.dp, FitPlanCardBorder, RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = queryInput,
                    onValueChange = { queryInput = it },
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
                        .testTag("gemini_coach_screen_input")
                )

                IconButton(
                    onClick = {
                        if (queryInput.isNotBlank()) {
                            val text = queryInput
                            queryInput = ""
                            onSendMessage(text)
                        }
                    },
                    enabled = queryInput.isNotBlank() && !isSendingCoach,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (queryInput.isNotBlank()) FitPlanNeonLime else FitPlanSurface)
                        .testTag("gemini_coach_screen_send")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = if (queryInput.isNotBlank()) FitPlanBlack else FitPlanTextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
