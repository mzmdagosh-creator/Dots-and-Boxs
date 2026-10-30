package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkGameColorScheme = darkColorScheme(
    primary = Player1Cyan,
    onPrimary = ObsidianBackground,
    primaryContainer = SlateSurfaceElevated,
    onPrimaryContainer = DotWhite,
    secondary = Player2Coral,
    onSecondary = ObsidianBackground,
    secondaryContainer = SlateSurfaceElevated,
    onSecondaryContainer = DotWhite,
    tertiary = StreakAmber,
    background = ObsidianBackground,
    onBackground = DotWhite,
    surface = SlateSurface,
    onSurface = DotWhite,
    surfaceVariant = SlateSurfaceElevated,
    onSurfaceVariant = MutedText,
    outline = SlateBorder,
    error = Color(0xFFF87171)
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkGameColorScheme,
        typography = Typography,
        content = content
    )
}
