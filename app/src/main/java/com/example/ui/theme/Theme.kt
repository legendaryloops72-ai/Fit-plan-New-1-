package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.example.model.AppThemeMode

/**
 * Semantic theme color palette for FITPLAN.
 * Seamlessly transitions between High-Contrast Dark Athletic and Crisp Daylight Light aesthetic.
 */
@Immutable
data class FitPlanColors(
    val background: Color,
    val bgDark: Color,
    val surface: Color,
    val card: Color,
    val cardBorder: Color,
    val cardBorderLight: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val divider: Color,
    val glass: Color,
    val primary: Color,
    val primaryGlow: Color,
    val primaryDark: Color,
    val cyan: Color,
    val cyanGlow: Color,
    val yogaLavender: Color,
    val morningOrange: Color,
    val hiitRed: Color,
    val green: Color,
    val yellow: Color,
    val isDark: Boolean
)

val DarkFitPlanColors = FitPlanColors(
    background = FitPlanBlack,
    bgDark = FitPlanBgDark,
    surface = FitPlanSurface,
    card = FitPlanCard,
    cardBorder = FitPlanCardBorder,
    cardBorderLight = FitPlanCardBorderLight,
    textPrimary = FitPlanTextWhite,
    textSecondary = FitPlanTextSecondary,
    textMuted = FitPlanTextMuted,
    divider = FitPlanDivider,
    glass = FitPlanGlass,
    primary = FitPlanNeonLime,
    primaryGlow = FitPlanNeonLimeGlow,
    primaryDark = FitPlanNeonLimeDark,
    cyan = FitPlanCyan,
    cyanGlow = FitPlanCyanGlow,
    yogaLavender = FitPlanYogaLavender,
    morningOrange = FitPlanMorningOrange,
    hiitRed = FitPlanHiitRed,
    green = FitPlanGreen,
    yellow = FitPlanYellow,
    isDark = true
)

val LightFitPlanColors = FitPlanColors(
    background = FitPlanLightBg,
    bgDark = Color(0xFFE2E8F0),
    surface = FitPlanLightSurface,
    card = FitPlanLightCard,
    cardBorder = FitPlanLightCardBorder,
    cardBorderLight = FitPlanLightCardBorderLight,
    textPrimary = FitPlanLightTextPrimary,
    textSecondary = FitPlanLightTextSecondary,
    textMuted = FitPlanLightTextMuted,
    divider = FitPlanLightDivider,
    glass = FitPlanLightGlass,
    primary = FitPlanLightPrimary,
    primaryGlow = FitPlanLightPrimaryGlow,
    primaryDark = Color(0xFF4D6C00),
    cyan = FitPlanLightCyan,
    cyanGlow = Color(0x220284C7),
    yogaLavender = Color(0xFF8B5CF6),
    morningOrange = Color(0xFFEA580C),
    hiitRed = Color(0xFFDC2626),
    green = Color(0xFF16A34A),
    yellow = Color(0xFFD97706),
    isDark = false
)

private val DarkColorScheme = darkColorScheme(
    primary = FitPlanNeonLime,
    onPrimary = FitPlanBlack,
    primaryContainer = FitPlanSurface,
    onPrimaryContainer = FitPlanNeonLime,
    secondary = FitPlanCyan,
    onSecondary = FitPlanBlack,
    secondaryContainer = FitPlanCard,
    onSecondaryContainer = FitPlanTextWhite,
    tertiary = FitPlanYogaLavender,
    onTertiary = FitPlanBlack,
    background = FitPlanBlack,
    onBackground = FitPlanTextWhite,
    surface = FitPlanSurface,
    onSurface = FitPlanTextWhite,
    surfaceVariant = FitPlanCard,
    onSurfaceVariant = FitPlanTextSecondary,
    outline = FitPlanCardBorder,
    outlineVariant = FitPlanDivider
)

private val LightColorScheme = lightColorScheme(
    primary = FitPlanLightPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE8F5D0),
    onPrimaryContainer = Color(0xFF263900),
    secondary = FitPlanLightCyan,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0F2FE),
    onSecondaryContainer = Color(0xFF0369A1),
    tertiary = Color(0xFF8B5CF6),
    onTertiary = Color.White,
    background = FitPlanLightBg,
    onBackground = FitPlanLightTextPrimary,
    surface = FitPlanLightSurface,
    onSurface = FitPlanLightTextPrimary,
    surfaceVariant = FitPlanLightCard,
    onSurfaceVariant = FitPlanLightTextSecondary,
    outline = FitPlanLightCardBorder,
    outlineVariant = FitPlanLightDivider
)

val LocalFitPlanColors = staticCompositionLocalOf { DarkFitPlanColors }

/**
 * Controller providing global theme state manipulation.
 */
@Stable
class ThemeState(
    val themeMode: AppThemeMode,
    val isDark: Boolean,
    private val onThemeChange: (AppThemeMode) -> Unit
) {
    fun setThemeMode(mode: AppThemeMode) = onThemeChange(mode)

    fun toggleTheme() {
        val newMode = if (isDark) AppThemeMode.LIGHT else AppThemeMode.DARK
        onThemeChange(newMode)
    }
}

val LocalThemeState = staticCompositionLocalOf {
    ThemeState(
        themeMode = AppThemeMode.DARK,
        isDark = true,
        onThemeChange = {}
    )
}

/**
 * FITPLAN Theme Accessor
 */
object FitPlanTheme {
    val colors: FitPlanColors
        @Composable
        @ReadOnlyComposable
        get() = LocalFitPlanColors.current

    val isDark: Boolean
        @Composable
        @ReadOnlyComposable
        get() = LocalFitPlanColors.current.isDark

    val controller: ThemeState
        @Composable
        @ReadOnlyComposable
        get() = LocalThemeState.current
}

@Composable
fun FitPlanTheme(
    themeMode: AppThemeMode = AppThemeMode.DARK,
    onThemeChange: (AppThemeMode) -> Unit = {},
    content: @Composable () -> Unit
) {
    val systemInDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        AppThemeMode.DARK -> true
        AppThemeMode.LIGHT -> false
        AppThemeMode.SYSTEM -> systemInDark
    }

    val customColors = if (isDark) DarkFitPlanColors else LightFitPlanColors
    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme

    val themeState = remember(themeMode, isDark, onThemeChange) {
        ThemeState(
            themeMode = themeMode,
            isDark = isDark,
            onThemeChange = onThemeChange
        )
    }

    CompositionLocalProvider(
        LocalFitPlanColors provides customColors,
        LocalThemeState provides themeState
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

@Composable
fun FitPlanTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    val themeMode = if (darkTheme) AppThemeMode.DARK else AppThemeMode.LIGHT
    FitPlanTheme(themeMode = themeMode, onThemeChange = {}, content = content)
}

// Backward compatibility alias for tests
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    FitPlanTheme(darkTheme = darkTheme, content = content)
}
