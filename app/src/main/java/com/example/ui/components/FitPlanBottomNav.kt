package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.ScreenDestination
import com.example.ui.theme.*

data class NavItem(
    val title: String,
    val destination: ScreenDestination,
    val icon: ImageVector,
    val testTag: String
)

@Composable
fun FitPlanBottomNav(
    currentScreen: ScreenDestination,
    onNavigate: (ScreenDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = FitPlanTheme.colors

    val items = listOf(
        NavItem(stringResource(R.string.nav_home), ScreenDestination.Home, Icons.Default.Home, "nav_home"),
        NavItem(stringResource(R.string.nav_workouts), ScreenDestination.Workouts, Icons.Default.FitnessCenter, "nav_workouts"),
        NavItem(stringResource(R.string.nav_nutrition), ScreenDestination.Nutrition, Icons.Default.Restaurant, "nav_nutrition"),
        NavItem(stringResource(R.string.nav_progress), ScreenDestination.Progress, Icons.Default.BarChart, "nav_progress"),
        NavItem(stringResource(R.string.nav_more), ScreenDestination.More, Icons.Default.MoreHoriz, "nav_more")
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("fitplan_bottom_navigation"),
        color = colors.surface,
        tonalElevation = 8.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = when (item.destination) {
                    ScreenDestination.Home -> currentScreen == ScreenDestination.Home
                    ScreenDestination.Workouts -> currentScreen in listOf(
                        ScreenDestination.Workouts,
                        ScreenDestination.HomeWorkout,
                        ScreenDestination.Yoga,
                        ScreenDestination.Stretching,
                        ScreenDestination.MorningMove,
                        ScreenDestination.HiitCardio,
                        ScreenDestination.RelaxingYoga
                    )
                    ScreenDestination.Nutrition -> currentScreen == ScreenDestination.Nutrition
                    ScreenDestination.Progress -> currentScreen == ScreenDestination.Progress
                    ScreenDestination.More -> currentScreen in listOf(
                        ScreenDestination.More,
                        ScreenDestination.Activities,
                        ScreenDestination.Running,
                        ScreenDestination.Cycling,
                        ScreenDestination.Walking
                    )
                    else -> currentScreen == item.destination
                }

                Column(
                    modifier = Modifier
                        .testTag(item.testTag)
                        .clip(CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onNavigate(item.destination)
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) colors.primary.copy(alpha = 0.15f) else androidx.compose.ui.graphics.Color.Transparent),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = if (isSelected) colors.primary else colors.textSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) colors.primary else colors.textSecondary
                    )
                }
            }
        }
    }
}
