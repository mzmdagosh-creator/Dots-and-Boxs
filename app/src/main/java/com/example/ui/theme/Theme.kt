package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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

private val HighContrastLightColorScheme = lightColorScheme(
    primary = HighContrastP1,
    onPrimary = Color.White,
    primaryContainer = HighContrastSurfaceElevated,
    onPrimaryContainer = HighContrastText,
    secondary = HighContrastP2,
    onSecondary = Color.White,
    secondaryContainer = HighContrastSurfaceElevated,
    onSecondaryContainer = HighContrastText,
    tertiary = HighContrastAmber,
    background = HighContrastBackground,
    onBackground = HighContrastText,
    surface = HighContrastSurface,
    onSurface = HighContrastText,
    surfaceVariant = HighContrastSurfaceElevated,
    onSurfaceVariant = HighContrastMutedText,
    outline = HighContrastBorder,
    error = Color(0xFFC81E1E)
)

@Composable
fun MyApplicationTheme(
    themeMode: AppThemeMode = AppThemeMode.CLASSIC_DARK,
    content: @Composable () -> Unit
) {
    val palette = when (themeMode) {
        AppThemeMode.CLASSIC_DARK -> ClassicDarkGamePalette
        AppThemeMode.HIGH_CONTRAST -> HighContrastLightGamePalette
    }
    val colorScheme = when (themeMode) {
        AppThemeMode.CLASSIC_DARK -> DarkGameColorScheme
        AppThemeMode.HIGH_CONTRAST -> HighContrastLightColorScheme
    }

    CompositionLocalProvider(LocalGamePalette provides palette) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
