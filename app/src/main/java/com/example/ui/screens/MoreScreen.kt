package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.model.AppThemeMode
import com.example.model.UserProfile
import com.example.notifications.ReminderScheduleItem
import com.example.notifications.ReminderSettings
import com.example.notifications.ReminderType
import com.example.ui.components.FitPlanTopBar
import com.example.ui.theme.*

@Composable
fun MoreScreen(
    userProfile: UserProfile,
    reminderSettings: ReminderSettings,
    themeMode: AppThemeMode = userProfile.themeMode,
    onThemeModeChange: (AppThemeMode) -> Unit = {},
    onToggleTheme: () -> Unit = {},
    onToggleVoiceCoach: () -> Unit,
    onToggleReminder: (ReminderType, Boolean, Context) -> Unit,
    onUpdateReminderTime: (ReminderType, Int, Int, Context) -> Unit,
    onSendTestNotification: (ReminderType, Context) -> Unit,
    onLanguageChange: (String) -> Unit,
    onLogOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val colors = FitPlanTheme.colors
    val isDark = FitPlanTheme.isDark
    var editingReminderType by remember { mutableStateOf<ReminderType?>(null) }
    var notificationSentFeedback by remember { mutableStateOf<String?>(null) }
    var showLicensesDialog by remember { mutableStateOf(false) }

    if (showLicensesDialog) {
        ImageLicensesDialog(
            context = context,
            onDismiss = { showLicensesDialog = false }
        )
    }

    // Android 13+ Notification Permission Launcher
    val registryOwner = LocalActivityResultRegistryOwner.current
        ?: (context as? ActivityResultRegistryOwner)
    val permissionLauncher = if (registryOwner != null) {
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                notificationSentFeedback = "Notification permission granted!"
            }
        }
    } else {
        null
    }

    // Time Picker Dialog
    if (editingReminderType != null) {
        val type = editingReminderType!!
        val currentSchedule = reminderSettings.reminders.firstOrNull { it.type == type }
        val currentHour = currentSchedule?.hour ?: type.defaultHour
        val currentMinute = currentSchedule?.minute ?: type.defaultMinute

        ReminderTimePickerDialog(
            title = type.categoryLabel,
            initialHour = currentHour,
            initialMinute = currentMinute,
            onDismiss = { editingReminderType = null },
            onConfirm = { hour, minute ->
                onUpdateReminderTime(type, hour, minute, context)
                editingReminderType = null
                notificationSentFeedback = "Reminder scheduled for ${type.categoryLabel} at $hour:${if (minute < 10) "0$minute" else "$minute"}"
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .testTag("more_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        item {
            FitPlanTopBar()
        }

        // Profile Header Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(colors.card)
                    .border(1.dp, colors.cardBorder, RoundedCornerShape(20.dp))
                    .padding(18.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatar
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(colors.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userProfile.fullName.take(2).uppercase(),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = if (isDark) FitPlanBlack else Color.White
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = userProfile.fullName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = colors.textPrimary
                            )
                            Text(
                                text = userProfile.email,
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.textSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .background(colors.primary.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "${userProfile.level} • ${userProfile.streakDays}d Streak",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = colors.primary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Physical metrics strip
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(colors.surface, RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(stringResource(R.string.weight), style = MaterialTheme.typography.labelSmall, color = colors.textSecondary)
                            Text("${userProfile.weightKg} kg", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = colors.textPrimary)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(stringResource(R.string.height), style = MaterialTheme.typography.labelSmall, color = colors.textSecondary)
                            Text("${userProfile.heightCm} cm", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = colors.textPrimary)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(stringResource(R.string.target), style = MaterialTheme.typography.labelSmall, color = colors.textSecondary)
                            Text("${userProfile.targetWeightKg} kg", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = colors.primary)
                        }
                    }
                }
            }
        }

        // Notification Feedback Banner
        if (notificationSentFeedback != null) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.primary.copy(alpha = 0.15f))
                        .border(1.dp, colors.primary, RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = colors.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = notificationSentFeedback!!,
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.textPrimary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        IconButton(
                            onClick = { notificationSentFeedback = null },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dismiss",
                                tint = colors.textSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // Section: Appearance & Theme (User Request Priority)
        item {
            SectionHeader(stringResource(R.string.appearance))
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Main Theme Mode Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(colors.card)
                        .border(1.dp, colors.cardBorder, RoundedCornerShape(18.dp))
                        .padding(16.dp)
                        .testTag("theme_settings_card")
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(colors.primary.copy(alpha = 0.15f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isDark) Icons.Default.DarkMode else Icons.Default.LightMode,
                                        contentDescription = "Theme Icon",
                                        tint = colors.primary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = stringResource(R.string.appearance),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.textPrimary
                                    )
                                    Text(
                                        text = stringResource(R.string.choose_theme),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = colors.textSecondary
                                    )
                                }
                            }

                            // Quick Switch
                            Switch(
                                checked = isDark,
                                onCheckedChange = { onToggleTheme() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = if (isDark) FitPlanBlack else Color.White,
                                    checkedTrackColor = colors.primary,
                                    uncheckedTrackColor = colors.surface
                                ),
                                modifier = Modifier.testTag("theme_quick_toggle_switch")
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // 3-Way Segmented Theme Selector (Dark / Light / System)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(colors.surface, RoundedCornerShape(12.dp))
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Dark Mode Button
                            val isDarkSelected = themeMode == AppThemeMode.DARK
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isDarkSelected) colors.primary else Color.Transparent)
                                    .clickable { onThemeModeChange(AppThemeMode.DARK) }
                                    .padding(vertical = 10.dp)
                                    .testTag("theme_selector_dark"),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DarkMode,
                                        contentDescription = null,
                                        tint = if (isDarkSelected) (if (isDark) FitPlanBlack else Color.White) else colors.textSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = stringResource(R.string.theme_dark),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (isDarkSelected) (if (isDark) FitPlanBlack else Color.White) else colors.textSecondary
                                    )
                                }
                            }

                            // Light Mode Button
                            val isLightSelected = themeMode == AppThemeMode.LIGHT
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isLightSelected) colors.primary else Color.Transparent)
                                    .clickable { onThemeModeChange(AppThemeMode.LIGHT) }
                                    .padding(vertical = 10.dp)
                                    .testTag("theme_selector_light"),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LightMode,
                                        contentDescription = null,
                                        tint = if (isLightSelected) (if (isDark) FitPlanBlack else Color.White) else colors.textSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = stringResource(R.string.theme_light),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (isLightSelected) (if (isDark) FitPlanBlack else Color.White) else colors.textSecondary
                                    )
                                }
                            }

                            // System Mode Button
                            val isSystemSelected = themeMode == AppThemeMode.SYSTEM
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSystemSelected) colors.primary else Color.Transparent)
                                    .clickable { onThemeModeChange(AppThemeMode.SYSTEM) }
                                    .padding(vertical = 10.dp)
                                    .testTag("theme_selector_system"),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.BrightnessAuto,
                                        contentDescription = null,
                                        tint = if (isSystemSelected) (if (isDark) FitPlanBlack else Color.White) else colors.textSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = stringResource(R.string.theme_system),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (isSystemSelected) (if (isDark) FitPlanBlack else Color.White) else colors.textSecondary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Descriptive Status Tag
                        Text(
                            text = when (themeMode) {
                                AppThemeMode.DARK -> stringResource(R.string.theme_dark_desc)
                                AppThemeMode.LIGHT -> stringResource(R.string.theme_light_desc)
                                AppThemeMode.SYSTEM -> stringResource(R.string.theme_system_desc)
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.textMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Section: Daily Logging Reminders (WORKOUTS & MEALS)
        item {
            SectionHeader("Daily Reminders & Notifications")
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                reminderSettings.reminders.forEach { item ->
                    ReminderCardRow(
                        item = item,
                        onToggle = { enabled ->
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && enabled) {
                                permissionLauncher?.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                            onToggleReminder(item.type, enabled, context)
                        },
                        onEditTime = {
                            editingReminderType = item.type
                        },
                        onSendTest = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                permissionLauncher?.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                            onSendTestNotification(item.type, context)
                            notificationSentFeedback = "Test alert sent for ${item.type.categoryLabel}!"
                        }
                    )
                }
            }
        }

        // Section: Workout & Coaching Audio
        item {
            SectionHeader("Workout & Audio")
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SettingSwitchRow(
                    title = stringResource(R.string.voice_coach),
                    subtitle = "Real-time audio tips during exercises",
                    icon = Icons.Default.RecordVoiceOver,
                    checked = userProfile.voiceCoachEnabled,
                    onCheckedChange = { onToggleVoiceCoach() }
                )
            }
        }

        // Section: Preferences & Language
        item {
            SectionHeader(stringResource(R.string.app_preferences))
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Language Selection Row (English / Arabic RTL)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(colors.card)
                        .border(1.dp, colors.cardBorder, RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(colors.cyan.copy(alpha = 0.15f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Language, contentDescription = null, tint = colors.cyan, modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = stringResource(R.string.language),
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.textPrimary
                                    )
                                    val currentLangText = when (userProfile.selectedLanguage) {
                                        "ar" -> stringResource(R.string.lang_arabic)
                                        "es" -> stringResource(R.string.lang_spanish)
                                        else -> stringResource(R.string.lang_english)
                                    }
                                    Text(
                                        text = currentLangText,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = colors.textSecondary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // 3-way Language Selector: Arabic, English, Spanish
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(colors.surface, RoundedCornerShape(12.dp))
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Arabic
                            val isAr = userProfile.selectedLanguage == "ar"
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isAr) colors.primary else Color.Transparent)
                                    .clickable { onLanguageChange("ar") }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.lang_arabic),
                                    fontWeight = FontWeight.Bold,
                                    color = if (isAr) (if (isDark) FitPlanBlack else Color.White) else colors.textSecondary,
                                    fontSize = 11.sp
                                )
                            }

                            // English
                            val isEn = userProfile.selectedLanguage == "en"
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isEn) colors.primary else Color.Transparent)
                                    .clickable { onLanguageChange("en") }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.lang_english),
                                    fontWeight = FontWeight.Bold,
                                    color = if (isEn) (if (isDark) FitPlanBlack else Color.White) else colors.textSecondary,
                                    fontSize = 11.sp
                                )
                            }

                            // Spanish
                            val isEs = userProfile.selectedLanguage == "es"
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isEs) colors.primary else Color.Transparent)
                                    .clickable { onLanguageChange("es") }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.lang_spanish),
                                    fontWeight = FontWeight.Bold,
                                    color = if (isEs) (if (isDark) FitPlanBlack else Color.White) else colors.textSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                SettingNavRow(
                    title = stringResource(R.string.units_measurement),
                    subtitle = stringResource(R.string.metric_units),
                    icon = Icons.Default.Straighten,
                    onClick = {}
                )
            }
        }

        // Section: Account & Info
        item {
            SectionHeader(stringResource(R.string.account_support))
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SettingNavRow(
                    title = stringResource(R.string.privacy_data),
                    subtitle = stringResource(R.string.privacy_data_sub),
                    icon = Icons.Default.Security,
                    onClick = {}
                )

                SettingNavRow(
                    title = stringResource(R.string.help_faq),
                    subtitle = stringResource(R.string.help_faq_sub),
                    icon = Icons.Default.HelpOutline,
                    onClick = {}
                )

                SettingNavRow(
                    title = stringResource(R.string.image_licenses),
                    subtitle = stringResource(R.string.image_licenses_sub),
                    icon = Icons.Default.PhotoLibrary,
                    onClick = { showLicensesDialog = true }
                )

                SettingNavRow(
                    title = stringResource(R.string.about_fitplan),
                    subtitle = stringResource(R.string.about_fitplan_sub),
                    icon = Icons.Default.Info,
                    onClick = {}
                )
            }
        }

        // Log out button
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                OutlinedButton(
                    onClick = onLogOut,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("logout_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFFF4757)
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF4757).copy(alpha = 0.5f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Log Out",
                        tint = Color(0xFFFF4757),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.log_out),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ReminderCardRow(
    item: ReminderScheduleItem,
    onToggle: (Boolean) -> Unit,
    onEditTime: () -> Unit,
    onSendTest: () -> Unit
) {
    val colors = FitPlanTheme.colors
    val isDark = FitPlanTheme.isDark

    val accentColor = when (item.type) {
        ReminderType.WORKOUT -> colors.primary
        ReminderType.BREAKFAST -> colors.morningOrange
        ReminderType.LUNCH -> colors.cyan
        ReminderType.DINNER -> colors.yogaLavender
        ReminderType.HYDRATION -> colors.cyan
    }

    val icon = when (item.type) {
        ReminderType.WORKOUT -> Icons.Default.FitnessCenter
        ReminderType.BREAKFAST -> Icons.Default.WbSunny
        ReminderType.LUNCH -> Icons.Default.Restaurant
        ReminderType.DINNER -> Icons.Default.Nightlight
        ReminderType.HYDRATION -> Icons.Default.WaterDrop
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.card)
            .border(1.dp, colors.cardBorder, RoundedCornerShape(16.dp))
            .padding(14.dp)
            .testTag("reminder_${item.type.name.lowercase()}")
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(accentColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = item.type.categoryLabel,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = item.type.defaultTitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textSecondary,
                            maxLines = 1
                        )
                    }
                }

                Switch(
                    checked = item.isEnabled,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = if (isDark) FitPlanBlack else Color.White,
                        checkedTrackColor = accentColor,
                        uncheckedTrackColor = colors.surface
                    ),
                    modifier = Modifier.testTag("switch_${item.type.name.lowercase()}")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Time Selector & Test Alert Button Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.surface, RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Clickable Time Tag
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { onEditTime() }
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${stringResource(R.string.time_label)} ${item.formattedTime}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.edit_time_desc),
                        tint = colors.textSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                }

                // Instant Test Notification button
                Button(
                    onClick = onSendTest,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accentColor.copy(alpha = 0.2f),
                        contentColor = accentColor
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier
                        .height(32.dp)
                        .testTag("test_btn_${item.type.name.lowercase()}")
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.test_alert),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ReminderTimePickerDialog(
    title: String,
    initialHour: Int,
    initialMinute: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    val colors = FitPlanTheme.colors
    val isDark = FitPlanTheme.isDark
    var selectedHour by remember { mutableIntStateOf(initialHour) }
    var selectedMinute by remember { mutableIntStateOf(initialMinute) }

    val presetTimes = listOf(
        Pair(6, 0) to "06:00 AM",
        Pair(7, 0) to "07:00 AM",
        Pair(8, 30) to "08:30 AM",
        Pair(12, 0) to "12:00 PM",
        Pair(13, 0) to "01:00 PM",
        Pair(17, 30) to "05:30 PM",
        Pair(19, 30) to "07:30 PM",
        Pair(21, 0) to "09:00 PM"
    )

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(colors.card)
                .border(1.dp, colors.primary.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${stringResource(R.string.set_time)}: $title",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = stringResource(R.string.close), tint = colors.textSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Custom Hour & Minute adjusters
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.surface, RoundedCornerShape(14.dp))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Hour selector
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = { selectedHour = (selectedHour + 1) % 24 },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Increment Hour", tint = colors.primary)
                        }
                        Text(
                            text = if (selectedHour < 10) "0$selectedHour" else "$selectedHour",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Black,
                            color = colors.textPrimary
                        )
                        IconButton(
                            onClick = { selectedHour = if (selectedHour - 1 < 0) 23 else selectedHour - 1 },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Decrement Hour", tint = colors.primary)
                        }
                        Text("Hour (24h)", style = MaterialTheme.typography.labelSmall, color = colors.textSecondary, fontSize = 10.sp)
                    }

                    Text(
                        text = ":",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Black,
                        color = colors.primary,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    // Minute selector
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = { selectedMinute = (selectedMinute + 5) % 60 },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Increment Minute", tint = colors.primary)
                        }
                        Text(
                            text = if (selectedMinute < 10) "0$selectedMinute" else "$selectedMinute",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Black,
                            color = colors.textPrimary
                        )
                        IconButton(
                            onClick = { selectedMinute = if (selectedMinute - 5 < 0) 55 else selectedMinute - 5 },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Decrement Minute", tint = colors.primary)
                        }
                        Text(stringResource(R.string.minute_label), style = MaterialTheme.typography.labelSmall, color = colors.textSecondary, fontSize = 10.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = stringResource(R.string.quick_presets),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textSecondary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Presets wrap row
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    presetTimes.chunked(4).forEach { rowPresets ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            rowPresets.forEach { (time, label) ->
                                val isSelected = selectedHour == time.first && selectedMinute == time.second
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) colors.primary else colors.surface)
                                        .clickable {
                                            selectedHour = time.first
                                            selectedMinute = time.second
                                        }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) (if (isDark) FitPlanBlack else Color.White) else colors.textPrimary
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(stringResource(R.string.action_cancel), color = colors.textSecondary)
                    }

                    Button(
                        onClick = { onConfirm(selectedHour, selectedMinute) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(stringResource(R.string.action_save_time), color = if (isDark) FitPlanBlack else Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    val colors = FitPlanTheme.colors
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = colors.textSecondary,
        modifier = Modifier.padding(horizontal = 16.dp).padding(top = 18.dp, bottom = 8.dp)
    )
}

@Composable
fun SettingSwitchRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val colors = FitPlanTheme.colors
    val isDark = FitPlanTheme.isDark

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.card)
            .border(1.dp, colors.cardBorder, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(colors.primary.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = colors.primary, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = colors.textPrimary)
                    Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
                }
            }

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = if (isDark) FitPlanBlack else Color.White,
                    checkedTrackColor = colors.primary,
                    uncheckedTrackColor = colors.surface
                )
            )
        }
    }
}

@Composable
fun SettingNavRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    val colors = FitPlanTheme.colors

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.card)
            .border(1.dp, colors.cardBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() }
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
                        .background(colors.surface, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = colors.textSecondary, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = colors.textPrimary)
                    Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
                }
            }

            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = colors.textSecondary, modifier = Modifier.size(18.dp))
        }
    }
}

data class LicenseEntry(
    val exerciseName: String,
    val file: String,
    val source: String,
    val sourcePage: String,
    val photographer: String,
    val license: String,
    val licenseUrl: String
)

@Composable
fun ImageLicensesDialog(
    context: Context,
    onDismiss: () -> Unit
) {
    val entries = remember {
        val list = mutableListOf<LicenseEntry>()
        try {
            val jsonString = context.assets.open("image_licenses.json").bufferedReader().use { it.readText() }
            val jsonObject = org.json.JSONObject(jsonString)
            val keys = jsonObject.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                val obj = jsonObject.getJSONObject(key)
                list.add(
                    LicenseEntry(
                        exerciseName = obj.optString("mealName", obj.optString("exerciseName", key)),
                        file = obj.optString("file", ""),
                        source = obj.optString("source", "Wikimedia Commons"),
                        sourcePage = obj.optString("sourcePage", ""),
                        photographer = obj.optString("photographer", "Contributor"),
                        license = obj.optString("license", "Public domain"),
                        licenseUrl = obj.optString("licenseUrl", "")
                    )
                )
            }
        } catch (e: Exception) {
            // handle gracefully
        }
        list
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(24.dp))
                .background(FitPlanCard)
                .border(1.dp, FitPlanCardBorder, RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.image_licenses_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = FitPlanTextWhite
                        )
                        Text(
                            text = "${entries.size} verified real exercise & nutrition photos",
                            style = MaterialTheme.typography.bodySmall,
                            color = FitPlanNeonLime
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .background(FitPlanSurface, CircleShape)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = FitPlanTextWhite, modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(entries) { entry ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(FitPlanSurface)
                                .border(1.dp, FitPlanCardBorder, RoundedCornerShape(12.dp))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (entry.file.isNotEmpty()) {
                                coil.compose.AsyncImage(
                                    model = "file:///android_asset/${entry.file}",
                                    contentDescription = entry.exerciseName,
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = entry.exerciseName,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = FitPlanTextWhite
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Credit: ${entry.photographer}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = FitPlanTextSecondary,
                                    fontSize = 11.sp,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .background(FitPlanNeonLime.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = entry.license,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = FitPlanNeonLime,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = entry.source,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = FitPlanTextMuted,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
