package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*

@Composable
fun FitPlanTopBar(
    title: String? = null,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    showGpsReady: Boolean = false,
    showThemeToggle: Boolean = true,
    onNotificationClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val themeState = LocalThemeState.current
    val colors = FitPlanTheme.colors
    val isDark = FitPlanTheme.isDark

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (showBackButton) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .testTag("back_button")
                        .size(40.dp)
                        .background(colors.card, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = colors.textPrimary
                    )
                }
                if (title != null) {
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        color = colors.textPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else {
            // FITPLAN Logo & Tagline
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.testTag("fitplan_header_logo")
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_fitplan_dumbbell),
                    contentDescription = "FITPLAN Logo",
                    tint = colors.primary,
                    modifier = Modifier.size(width = 36.dp, height = 22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = colors.textPrimary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = stringResource(R.string.tagline),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.primary,
                        fontSize = 8.sp,
                        letterSpacing = 0.8.sp
                    )
                }
            }
        }

        // Right side indicators & Quick Theme Toggle
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (showGpsReady) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(colors.primary.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(colors.primary, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.gps_ready),
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            if (showThemeToggle) {
                IconButton(
                    onClick = { themeState.toggleTheme() },
                    modifier = Modifier
                        .testTag("header_theme_toggle_button")
                        .size(40.dp)
                        .background(colors.card, CircleShape)
                ) {
                    Icon(
                        imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = if (isDark) "Switch to Light Theme" else "Switch to Dark Theme",
                        tint = colors.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            IconButton(
                onClick = onSearchClick,
                modifier = Modifier
                    .testTag("header_search_button")
                    .size(40.dp)
                    .background(colors.card, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = colors.textSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box {
                IconButton(
                    onClick = onNotificationClick,
                    modifier = Modifier
                        .testTag("header_notifications_button")
                        .size(40.dp)
                        .background(colors.card, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = colors.textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                // Unread red dot
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(8.dp)
                        .background(Color(0xFFFF3B30), CircleShape)
                )
            }
        }
    }
}
