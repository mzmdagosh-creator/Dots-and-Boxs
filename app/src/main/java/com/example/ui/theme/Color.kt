package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

enum class AppThemeMode {
    CLASSIC_DARK,
    HIGH_CONTRAST
}

// Classic Dark Palette Constants
val ObsidianBackground = Color(0xFF0B1120)
val SlateSurface = Color(0xFF131C31)
val SlateSurfaceElevated = Color(0xFF1E293B)
val SlateBorder = Color(0xFF334155)
val DotWhite = Color(0xFFF8FAFC)
val MutedText = Color(0xFF94A3B8)

// Classic Dark Player Accents
val Player1Cyan = Color(0xFF22D3EE)
val Player1CyanDim = Color(0xFF0891B2)
val Player1CyanFill = Color(0x3822D3EE)

val Player2Coral = Color(0xFFFB7185)
val Player2CoralDim = Color(0xFFE11D48)
val Player2CoralFill = Color(0x38FB7185)

val Player3Emerald = Color(0xFF34D399)
val Player3EmeraldDim = Color(0xFF059669)
val Player3EmeraldFill = Color(0x3834D399)

val Player4Violet = Color(0xFFC084FC)
val Player4VioletDim = Color(0xFF9333EA)
val Player4VioletFill = Color(0x38C084FC)

val StreakAmber = Color(0xFFFACC15)
val UnclaimedEdgeIdle = Color(0xFF1E293B)
val UnclaimedEdgeHover = Color(0xFF475569)

// High Contrast Light Palette Constants
val HighContrastBackground = Color(0xFFF8FAFC)
val HighContrastRadialTop = Color(0xFFE2E8F0)
val HighContrastSurface = Color(0xFFFFFFFF)
val HighContrastSurfaceElevated = Color(0xFFF1F5F9)
val HighContrastBorder = Color(0xFF0F172A)
val HighContrastText = Color(0xFF090D16)
val HighContrastMutedText = Color(0xFF334155)
val HighContrastIdleEdge = Color(0xFFCBD5E1)

val HighContrastP1 = Color(0xFF0044CC)
val HighContrastP1Fill = Color(0x330044CC)
val HighContrastP2 = Color(0xFFC81E1E)
val HighContrastP2Fill = Color(0x33C81E1E)
val HighContrastP3 = Color(0xFF046C4E)
val HighContrastP3Fill = Color(0x33046C4E)
val HighContrastP4 = Color(0xFF6B21A8)
val HighContrastP4Fill = Color(0x336B21A8)
val HighContrastAmber = Color(0xFFB45309)

@Immutable
data class GamePalette(
    val themeMode: AppThemeMode = AppThemeMode.CLASSIC_DARK,
    val backgroundDeep: Color = ObsidianBackground,
    val backgroundRadialTop: Color = Color(0xFF172554),
    val surface: Color = SlateSurface,
    val surfaceElevated: Color = SlateSurfaceElevated,
    val border: Color = SlateBorder,
    val primaryText: Color = DotWhite,
    val mutedText: Color = MutedText,
    val dotColor: Color = DotWhite,
    val idleEdge: Color = UnclaimedEdgeIdle,
    val streakAccent: Color = StreakAmber,
    val p1Accent: Color = Player1Cyan,
    val p1Fill: Color = Player1CyanFill,
    val p2Accent: Color = Player2Coral,
    val p2Fill: Color = Player2CoralFill,
    val p3Accent: Color = Player3Emerald,
    val p3Fill: Color = Player3EmeraldFill,
    val p4Accent: Color = Player4Violet,
    val p4Fill: Color = Player4VioletFill
)

val ClassicDarkGamePalette = GamePalette(
    themeMode = AppThemeMode.CLASSIC_DARK
)

val HighContrastLightGamePalette = GamePalette(
    themeMode = AppThemeMode.HIGH_CONTRAST,
    backgroundDeep = HighContrastBackground,
    backgroundRadialTop = HighContrastRadialTop,
    surface = HighContrastSurface,
    surfaceElevated = HighContrastSurfaceElevated,
    border = HighContrastBorder,
    primaryText = HighContrastText,
    mutedText = HighContrastMutedText,
    dotColor = HighContrastText,
    idleEdge = HighContrastIdleEdge,
    streakAccent = HighContrastAmber,
    p1Accent = HighContrastP1,
    p1Fill = HighContrastP1Fill,
    p2Accent = HighContrastP2,
    p2Fill = HighContrastP2Fill,
    p3Accent = HighContrastP3,
    p3Fill = HighContrastP3Fill,
    p4Accent = HighContrastP4,
    p4Fill = HighContrastP4Fill
)

val LocalGamePalette = staticCompositionLocalOf { ClassicDarkGamePalette }

@Composable
fun playerAccentColor(playerNumber: Int): Color {
    val palette = LocalGamePalette.current
    return when (playerNumber) {
        1 -> palette.p1Accent
        2 -> palette.p2Accent
        3 -> palette.p3Accent
        4 -> palette.p4Accent
        else -> palette.p1Accent
    }
}

@Composable
fun playerFillColor(playerNumber: Int): Color {
    val palette = LocalGamePalette.current
    return when (playerNumber) {
        1 -> palette.p1Fill
        2 -> palette.p2Fill
        3 -> palette.p3Fill
        4 -> palette.p4Fill
        else -> palette.p1Fill
    }
}
