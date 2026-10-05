package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun HeroBanner(
    badgeText: String,
    title: String,
    subtitle: String,
    ctaText: String = "LET'S GO",
    accentColor: Color = FitPlanTheme.colors.primary,
    onCtaClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = FitPlanTheme.colors
    val isDark = FitPlanTheme.isDark

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    colors = if (isDark) listOf(
                        colors.surface,
                        colors.card,
                        colors.background
                    ) else listOf(
                        colors.card,
                        Color(0xFFE2E8F0),
                        colors.surface
                    )
                )
            )
            .border(1.dp, colors.cardBorder, RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Skewed / Angled Neo-Brutalist Badge
            Box(
                modifier = Modifier
                    .rotate(-2f)
                    .background(accentColor, RoundedCornerShape(6.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = badgeText.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isDark) FitPlanBlack else Color.White,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Bold Title
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
                color = colors.textPrimary,
                letterSpacing = (-0.5).sp,
                lineHeight = 28.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Subtitle
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary
            )

            Spacer(modifier = Modifier.height(18.dp))

            // CTA Button
            Row(
                modifier = Modifier
                    .testTag("hero_cta_button")
                    .clip(CircleShape)
                    .background(accentColor)
                    .clickable { onCtaClick() }
                    .padding(horizontal = 18.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = ctaText,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isDark) FitPlanBlack else Color.White,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = if (isDark) FitPlanBlack else Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
