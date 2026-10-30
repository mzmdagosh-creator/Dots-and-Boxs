package com.example.ui.theme

import androidx.compose.ui.graphics.Color

val ObsidianBackground = Color(0xFF0B1120)
val SlateSurface = Color(0xFF131C31)
val SlateSurfaceElevated = Color(0xFF1E293B)
val SlateBorder = Color(0xFF334155)
val DotWhite = Color(0xFFF8FAFC)
val MutedText = Color(0xFF94A3B8)

// Player 1: Electric Cyan
val Player1Cyan = Color(0xFF22D3EE)
val Player1CyanDim = Color(0xFF0891B2)
val Player1CyanFill = Color(0x3822D3EE)

// Player 2 / AI: Vibrant Coral
val Player2Coral = Color(0xFFFB7185)
val Player2CoralDim = Color(0xFFE11D48)
val Player2CoralFill = Color(0x38FB7185)

// Player 3: Neon Emerald
val Player3Emerald = Color(0xFF34D399)
val Player3EmeraldDim = Color(0xFF059669)
val Player3EmeraldFill = Color(0x3834D399)

// Player 4: Electric Violet
val Player4Violet = Color(0xFFC084FC)
val Player4VioletDim = Color(0xFF9333EA)
val Player4VioletFill = Color(0x38C084FC)

// Accents
val StreakAmber = Color(0xFFFACC15)
val UnclaimedEdgeIdle = Color(0xFF1E293B)
val UnclaimedEdgeHover = Color(0xFF475569)

fun playerAccentColor(playerNumber: Int): Color = when (playerNumber) {
    1 -> Player1Cyan
    2 -> Player2Coral
    3 -> Player3Emerald
    4 -> Player4Violet
    else -> Player1Cyan
}

fun playerFillColor(playerNumber: Int): Color = when (playerNumber) {
    1 -> Player1CyanFill
    2 -> Player2CoralFill
    3 -> Player3EmeraldFill
    4 -> Player4VioletFill
    else -> Player1CyanFill
}
