package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.model.ActivityType
import com.example.model.WorkoutItem
import com.example.ui.FitPlanLocalization
import com.example.ui.components.InteractiveMapCanvas
import com.example.ui.components.MetricStatCard
import com.example.ui.theme.*

// 1. Live Workout Player
@Composable
fun WorkoutPlayerScreen(
    workout: WorkoutItem,
    exerciseIndex: Int,
    secondsRemaining: Int,
    isFinished: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onTogglePause: () -> Unit,
    onFinish: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentExercise = workout.exercises.getOrNull(exerciseIndex)
        ?: workout.exercises.firstOrNull()
    val totalExercises = workout.exercises.size
    val accentColor = Color(workout.accentColorHex)

    if (isFinished) {
        // Workout Completed Celebration Dialog
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(FitPlanBlack)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(FitPlanCard)
                    .border(2.dp, accentColor, RoundedCornerShape(24.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(accentColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = FitPlanBlack,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(R.string.workout_completed_title),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = FitPlanTextWhite
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "${stringResource(R.string.workout_completed_sub)} • ${FitPlanLocalization.getWorkoutTitle(workout)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = FitPlanTextSecondary
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MetricStatCard("Burned", "${workout.calories}", "kcal", Icons.Default.LocalFireDepartment, FitPlanMorningOrange, modifier = Modifier.weight(1f))
                        MetricStatCard("Time", "${workout.durationMinutes}", "min", Icons.Default.Timer, accentColor, modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onFinish,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = accentColor,
                            contentColor = FitPlanBlack
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(
                            text = "SAVE & FINISH",
                            fontWeight = FontWeight.Black,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(FitPlanBlack)
                .statusBarsPadding()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .background(FitPlanCard, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = FitPlanTextWhite
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = workout.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = FitPlanTextWhite
                    )
                    Text(
                        text = "Exercise ${exerciseIndex + 1} of $totalExercises",
                        style = MaterialTheme.typography.bodySmall,
                        color = FitPlanTextSecondary
                    )
                }

                IconButton(
                    onClick = { /* Sound / coach */ },
                    modifier = Modifier
                        .size(40.dp)
                        .background(FitPlanCard, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Volume",
                        tint = accentColor
                    )
                }
            }

            // Real Exercise Form Photograph Showcase
            val assetUrl = currentExercise?.fullAssetUrl?.ifEmpty {
                com.example.data.ExerciseAssetRegistry.getAssetUrlForName(currentExercise.name)
            } ?: ""

            if (assetUrl.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(FitPlanCard)
                        .border(1.5.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.BottomStart
                ) {
                    AsyncImage(
                        model = assetUrl,
                        contentDescription = currentExercise?.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .padding(10.dp)
                            .background(FitPlanBlack.copy(alpha = 0.75f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.real_form_photo),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = accentColor,
                            fontSize = 9.sp
                        )
                    }
                }
            }

            // Exercise Title and Info
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .background(accentColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${currentExercise?.sets} ${stringResource(R.string.sets_unit)} • ${currentExercise?.reps}",
                        style = MaterialTheme.typography.labelMedium,
                        color = accentColor,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = currentExercise?.let { FitPlanLocalization.getExerciseName(it) } ?: "",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Black,
                    color = FitPlanTextWhite
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = currentExercise?.let { FitPlanLocalization.getExerciseInstructions(it) } ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = FitPlanTextSecondary
                )
            }

            // Big Timer Display
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .clip(CircleShape)
                    .background(FitPlanCard)
                    .border(4.dp, accentColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = String.format("%02d:%02d", secondsRemaining / 60, secondsRemaining % 60),
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Black,
                        color = FitPlanTextWhite
                    )
                    Text(
                        text = stringResource(R.string.remaining_label),
                        style = MaterialTheme.typography.labelSmall,
                        color = FitPlanTextSecondary,
                        fontSize = 9.sp
                    )
                }
            }

            // Next Up Preview
            val nextExercise = workout.exercises.getOrNull(exerciseIndex + 1)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(FitPlanSurface)
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.up_next_label),
                            style = MaterialTheme.typography.labelSmall,
                            color = FitPlanTextMuted,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = nextExercise?.let { FitPlanLocalization.getExerciseName(it) } ?: stringResource(R.string.finish),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = FitPlanTextWhite
                        )
                    }
                    Text(
                        text = nextExercise?.reps ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = accentColor
                    )
                }
            }

            // Player Controls (Previous, Pause/Play, Next)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onPrevious,
                    enabled = exerciseIndex > 0,
                    modifier = Modifier
                        .size(52.dp)
                        .background(FitPlanCard, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Previous",
                        tint = if (exerciseIndex > 0) FitPlanTextWhite else FitPlanTextMuted,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(accentColor)
                        .clickable { onTogglePause() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Pause,
                        contentDescription = "Pause",
                        tint = FitPlanBlack,
                        modifier = Modifier.size(36.dp)
                    )
                }

                IconButton(
                    onClick = onNext,
                    modifier = Modifier
                        .size(52.dp)
                        .background(FitPlanCard, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next",
                        tint = FitPlanTextWhite,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

// 2. Live GPS Activity Tracker (For Running, Cycling, Walking)
@Composable
fun LiveActivityTrackerScreen(
    type: ActivityType,
    durationSec: Int,
    distanceKm: Float,
    isRunning: Boolean,
    onTogglePause: () -> Unit,
    onFinish: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = when (type) {
        ActivityType.RUNNING -> FitPlanNeonLime
        ActivityType.CYCLING -> FitPlanCyan
        ActivityType.WALKING -> FitPlanMorningOrange
        else -> FitPlanNeonLime
    }

    val typeTitle = when (type) {
        ActivityType.RUNNING -> "Live Running"
        ActivityType.CYCLING -> "Live Cycling"
        ActivityType.WALKING -> "Live Walking"
        else -> "Live Workout"
    }

    val minutes = durationSec / 60
    val seconds = durationSec % 60
    val durationFormatted = String.format("%02d:%02d", minutes, seconds)

    val estimatedCalories = (distanceKm * when (type) {
        ActivityType.RUNNING -> 75
        ActivityType.CYCLING -> 40
        ActivityType.WALKING -> 50
        else -> 60
    }).toInt()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FitPlanBlack)
            .statusBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top GPS Status Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .background(FitPlanCard, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = FitPlanTextWhite
                )
            }

            Text(
                text = typeTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = FitPlanTextWhite
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(accentColor.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Box(modifier = Modifier.size(8.dp).background(accentColor, CircleShape))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.gps_lock_badge),
                    style = MaterialTheme.typography.labelSmall,
                    color = accentColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Live Map Canvas
        InteractiveMapCanvas(
            routeColor = accentColor,
            showGpsPulse = true,
            modifier = Modifier.fillMaxWidth().height(220.dp)
        )

        // Huge Distance Metric Display
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = String.format("%.2f", distanceKm),
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Black,
                color = FitPlanTextWhite,
                fontSize = 58.sp
            )
            Text(
                text = stringResource(R.string.kilometers_unit),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Black,
                color = accentColor,
                letterSpacing = 2.sp
            )
        }

        // 3 Secondary Stats Row (Duration, Pace/Speed, Calories)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricStatCard(stringResource(R.string.duration), durationFormatted, "", Icons.Default.Timer, accentColor, modifier = Modifier.weight(1f))
            val paceLabel = if (type == ActivityType.CYCLING) stringResource(R.string.metric_speed) else stringResource(R.string.metric_pace)
            val paceValue = if (type == ActivityType.CYCLING) "20.5" else "5'24\""
            val paceUnit = if (type == ActivityType.CYCLING) "km/h" else "/km"
            MetricStatCard(paceLabel, paceValue, paceUnit, Icons.Default.Speed, FitPlanCyan, modifier = Modifier.weight(1f))
            MetricStatCard(stringResource(R.string.calories), "$estimatedCalories", stringResource(R.string.kcal_unit), Icons.Default.LocalFireDepartment, FitPlanMorningOrange, modifier = Modifier.weight(1f))
        }

        // Controls (Pause/Resume & Finish Button)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = onTogglePause,
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, FitPlanCardBorder),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = FitPlanTextWhite)
            ) {
                Icon(
                    imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isRunning) stringResource(R.string.pause) else stringResource(R.string.resume),
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = onFinish,
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = accentColor,
                    contentColor = FitPlanBlack
                )
            ) {
                Icon(imageVector = Icons.Default.Stop, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.action_finish),
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}
