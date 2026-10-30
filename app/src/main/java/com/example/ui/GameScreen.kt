package com.example.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TimerOff
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.audio.HapticEngine
import com.example.model.BoardState
import com.example.model.BoxCoordinate
import com.example.model.CampaignLevel
import com.example.model.CpuDifficulty
import com.example.model.EdgeCoordinate
import com.example.model.EdgeOrientation
import com.example.model.GridSize
import com.example.model.PlayerConfig
import com.example.model.TimerMode
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalGamePalette
import com.example.ui.theme.playerAccentColor
import com.example.ui.theme.playerFillColor
import com.example.viewmodel.HapticEvent
import com.example.viewmodel.HapticEventType
import com.example.viewmodel.MatchHistoryItem
import com.example.viewmodel.PlayerSessionStats
import java.util.Locale
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun GameScreen(
    soundEnabled: Boolean,
    hapticsEnabled: Boolean,
    activeCampaignLevel: CampaignLevel?,
    justUnlockedNextCampaignLevel: Boolean,
    selectedGridSize: GridSize,
    selectedCpuDifficulty: CpuDifficulty,
    selectedTimerMode: TimerMode,
    blitzRemainingSeconds: Float,
    blitzRemainingFraction: Float,
    lastTurnForfeitedByTimeout: Boolean,
    boardState: BoardState,
    sessionStats: Map<Int, PlayerSessionStats>,
    matchHistory: List<MatchHistoryItem>,
    hoveredEdge: EdgeCoordinate?,
    boardZoom: Float,
    lastHapticEvent: HapticEvent?,
    victoryAnimationTriggerId: Int,
    onSelectGridSize: (GridSize) -> Unit,
    onSelectCpuDifficulty: (CpuDifficulty) -> Unit,
    onSelectTimerMode: (TimerMode) -> Unit,
    onAdvanceToNextCampaignLevel: () -> Unit,
    onToggleSound: () -> Unit,
    onToggleHaptics: () -> Unit,
    onEdgeHover: (EdgeCoordinate?) -> Unit,
    onEdgeClick: (EdgeCoordinate) -> Unit,
    onAdjustZoom: (Float) -> Unit,
    onSetZoom: (Float) -> Unit,
    onRestartMatch: () -> Unit,
    onReturnToSetup: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalGamePalette.current
    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current

    // Trigger hardware haptic "tick" on line draw and heavier "buzz" on box capture
    LaunchedEffect(lastHapticEvent?.id) {
        val event = lastHapticEvent ?: return@LaunchedEffect
        when (event.type) {
            HapticEventType.LINE_TICK -> {
                HapticEngine.triggerLineTick(context, hapticFeedback)
            }
            HapticEventType.BOX_CAPTURE_BUZZ -> {
                HapticEngine.triggerBoxCaptureBuzz(context, hapticFeedback, event.capturedCount)
            }
            HapticEventType.VICTORY_CELEBRATION -> {
                HapticEngine.triggerVictoryCelebration(context, hapticFeedback)
            }
        }
    }

    BackHandler {
        onReturnToSetup()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "neonPulse")
    val neonGlowPulse by infiniteTransition.animateFloat(
        initialValue = 0.65f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "neonGlowPulse"
    )
    val comboHeartbeatScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 380, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "comboHeartbeatScale"
    )

    val boardComboZoomAnim = remember { Animatable(1f) }
    val comboPulseWaveAnim = remember { Animatable(0f) }
    val cameraShakeAnim = remember { Animatable(0f) }
    val overlayBurstScale = remember { Animatable(0f) }
    val overlayBurstAlpha = remember { Animatable(0f) }

    LaunchedEffect(boardState.comboTriggerId) {
        if (boardState.comboTriggerId > 0) {
            val isCombo = boardState.currentComboCount >= 2 || boardState.lastCapturedBoxes.size >= 2
            val targetZoomPunch = if (isCombo) {
                1.04f + (boardState.currentComboCount.coerceAtMost(6) * 0.007f)
            } else {
                1.015f
            }

            launch {
                val shakeMagnitude = if (isCombo) 8f else 3.5f
                for (step in listOf(1f, -0.85f, 0.7f, -0.5f, 0.3f, 0f)) {
                    cameraShakeAnim.animateTo(
                        targetValue = step * shakeMagnitude,
                        animationSpec = tween(durationMillis = 40)
                    )
                }
            }

            launch {
                boardComboZoomAnim.snapTo(1f)
                boardComboZoomAnim.animateTo(
                    targetValue = targetZoomPunch,
                    animationSpec = tween(durationMillis = 120, easing = FastOutSlowInEasing)
                )
                boardComboZoomAnim.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                )
            }

            launch {
                comboPulseWaveAnim.snapTo(1f)
                comboPulseWaveAnim.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 680, easing = FastOutSlowInEasing)
                )
            }

            if (isCombo) {
                launch {
                    overlayBurstScale.snapTo(0.3f)
                    overlayBurstAlpha.snapTo(1f)
                    overlayBurstScale.animateTo(
                        targetValue = 1.10f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    )
                    overlayBurstScale.animateTo(
                        targetValue = 1.0f,
                        animationSpec = tween(durationMillis = 200)
                    )
                    overlayBurstAlpha.animateTo(
                        targetValue = 0f,
                        animationSpec = tween(durationMillis = 340)
                    )
                }
            }
        } else {
            boardComboZoomAnim.snapTo(1f)
            comboPulseWaveAnim.snapTo(0f)
            cameraShakeAnim.snapTo(0f)
            overlayBurstAlpha.snapTo(0f)
        }
    }

    val activeComboColor = playerAccentColor(boardState.currentPlayer)
    val pulseGlowAlpha = comboPulseWaveAnim.value

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        palette.backgroundRadialTop,
                        palette.backgroundDeep
                    )
                )
            )
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        // Subtle drifting cyan and magenta arcade dots in background
        DriftingNeonDotsBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 820.dp)
                .graphicsLayer {
                    translationX = cameraShakeAnim.value
                    translationY = -cameraShakeAnim.value * 0.6f
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Utility & Progress Bar
            TopGameToolbar(
                soundEnabled = soundEnabled,
                hapticsEnabled = hapticsEnabled,
                claimedBoxes = boardState.totalClaimedBoxes,
                totalBoxes = boardState.totalBoxes,
                onToggleSound = onToggleSound,
                onToggleHaptics = onToggleHaptics,
                onReturnToSetup = onReturnToSetup
            )

            // Top Score Trackers (Cyan vs Magenta) + Blitz 5s Diminishing Progress Bar
            ScoreboardHeader(
                boardState = boardState,
                activeCampaignLevel = activeCampaignLevel,
                selectedTimerMode = selectedTimerMode,
                blitzRemainingSeconds = blitzRemainingSeconds,
                blitzRemainingFraction = blitzRemainingFraction,
                lastTurnForfeitedByTimeout = lastTurnForfeitedByTimeout,
                neonGlowPulse = neonGlowPulse,
                comboHeartbeatScale = comboHeartbeatScale,
                modifier = Modifier.fillMaxWidth()
            )

            // Winner Banner when all boxes are claimed
            AnimatedVisibility(
                visible = boardState.isGameOver,
                enter = fadeIn() + scaleIn(
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                )
            ) {
                GameOverBanner(
                    boardState = boardState,
                    activeCampaignLevel = activeCampaignLevel,
                    justUnlockedNextCampaignLevel = justUnlockedNextCampaignLevel,
                    onAdvanceToNextCampaignLevel = onAdvanceToNextCampaignLevel,
                    onPlayAgain = onRestartMatch,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Centered Dynamic Game Board Container (3x3, 4x4, 5x5, 6x6)
            val boardBorderColor = if (pulseGlowAlpha > 0.02f) {
                activeComboColor.copy(alpha = (0.45f + pulseGlowAlpha * 0.55f).coerceIn(0f, 1f))
            } else {
                palette.border
            }
            val boardBorderWidth = if (pulseGlowAlpha > 0.02f) {
                (1.dp + (pulseGlowAlpha * 2.5f).dp)
            } else {
                1.dp
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 4.dp)
                    .graphicsLayer {
                        scaleX = boardComboZoomAnim.value
                        scaleY = boardComboZoomAnim.value
                    }
                    .drawBehind {
                        if (pulseGlowAlpha > 0.01f) {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        activeComboColor.copy(alpha = 0.34f * pulseGlowAlpha),
                                        Color.Transparent
                                    )
                                ),
                                radius = size.maxDimension * (0.55f + (1f - pulseGlowAlpha) * 0.25f)
                            )
                        }
                    }
                    .border(boardBorderWidth, boardBorderColor, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = palette.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    BoardZoomToolbar(
                        gridLabel = "${boardState.boxRows}×${boardState.boxCols}",
                        boardZoom = boardZoom,
                        comboCount = boardState.currentComboCount,
                        comboAccentColor = activeComboColor,
                        comboPulseFraction = pulseGlowAlpha,
                        onAdjustZoom = onAdjustZoom,
                        onResetZoom = { onSetZoom(0.95f) }
                    )

                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectTransformGestures { _, _, zoomChange, _ ->
                                    if (zoomChange != 1f) {
                                        onSetZoom(boardZoom * zoomChange)
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        val safeStageWidth = (maxWidth * 0.92f).coerceAtLeast(180.dp)
                        val safeStageHeight = (maxHeight * 0.92f).coerceAtLeast(140.dp)

                        val dotRows = boardState.dotRows
                        val dotCols = boardState.dotCols
                        val boxRows = boardState.boxRows
                        val boxCols = boardState.boxCols

                        // Dots are 8.dp radius (16.dp diameter) by default, scaled down gracefully only if stage is tiny
                        val maxGridDim = max(boxRows, boxCols)
                        val idealDotDiameter = 16.dp
                        val maxBoxByWidth = (safeStageWidth - (idealDotDiameter * dotCols)) / boxCols.toFloat()
                        val maxBoxByHeight = (safeStageHeight - (idealDotDiameter * dotRows)) / boxRows.toFloat()
                        val rawBoxSize = min(maxBoxByWidth, maxBoxByHeight)

                        val baseDotDiameter = if (rawBoxSize < 24.dp && maxGridDim >= 5) {
                            12.dp
                        } else {
                            idealDotDiameter
                        }
                        val recomputedBoxW = (safeStageWidth - (baseDotDiameter * dotCols)) / boxCols.toFloat()
                        val recomputedBoxH = (safeStageHeight - (baseDotDiameter * dotRows)) / boxRows.toFloat()
                        val baseBoxSize = min(recomputedBoxW, recomputedBoxH).coerceIn(20.dp, 76.dp)

                        val scaledDotSlotSize = (baseDotDiameter * boardZoom).coerceIn(10.dp, 22.dp)
                        val scaledBoxSize = (baseBoxSize * boardZoom).coerceIn(18.dp, 88.dp)

                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            DotsAndBoxesGrid(
                                boardState = boardState,
                                hoveredEdge = hoveredEdge,
                                neonGlowPulse = neonGlowPulse,
                                comboPulseFraction = pulseGlowAlpha,
                                dotSlotSize = scaledDotSlotSize,
                                boxSize = scaledBoxSize,
                                onEdgeHover = onEdgeHover,
                                onEdgeClick = onEdgeClick
                            )
                        }
                    }
                }
            }

            // Bottom Control Bar & Match History / Session Stats Footer
            BottomControlsAndFooter(
                activeCampaignLevel = activeCampaignLevel,
                selectedGridSize = selectedGridSize,
                selectedCpuDifficulty = selectedCpuDifficulty,
                selectedTimerMode = selectedTimerMode,
                players = boardState.players,
                sessionStats = sessionStats,
                matchHistory = matchHistory,
                onSelectGridSize = onSelectGridSize,
                onSelectCpuDifficulty = onSelectCpuDifficulty,
                onSelectTimerMode = onSelectTimerMode,
                onRestartMatch = onRestartMatch,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Giant Animated Neon Combo Overlay Burst ("COMBO x2!", "DOUBLE CAPTURE!")
        if (overlayBurstAlpha.value > 0.02f && boardState.currentComboCount >= 2) {
            val isDouble = boardState.lastCapturedBoxes.size >= 2
            val mainTitle = if (isDouble) {
                "DOUBLE CAPTURE!"
            } else {
                "COMBO x${boardState.currentComboCount}!"
            }
            val subTitle = if (isDouble) {
                "COMBO x${boardState.currentComboCount} STREAK!"
            } else {
                "NEON BONUS STREAK!"
            }

            Box(
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = overlayBurstScale.value
                        scaleY = overlayBurstScale.value
                        alpha = overlayBurstAlpha.value
                    }
                    .clip(RoundedCornerShape(20.dp))
                    .background(palette.backgroundDeep.copy(alpha = 0.92f))
                    .border(2.5.dp, activeComboColor, RoundedCornerShape(20.dp))
                    .padding(horizontal = 24.dp, vertical = 14.dp)
                    .testTag("combo_burst_overlay"),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = mainTitle,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.Black,
                            fontSize = 26.sp,
                            shadow = Shadow(
                                color = activeComboColor,
                                offset = Offset.Zero,
                                blurRadius = 24f
                            )
                        ),
                        color = palette.primaryText,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = subTitle,
                        style = MaterialTheme.typography.labelLarge.copy(
                            shadow = Shadow(
                                color = palette.streakAccent,
                                offset = Offset.Zero,
                                blurRadius = 16f
                            )
                        ),
                        color = palette.streakAccent,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Full-Screen Victory Animation (Confetti Burst + Celebratory Radial Glow) when match ends
        if (boardState.isGameOver) {
            FullScreenVictoryAnimationOverlay(
                boardState = boardState,
                triggerId = victoryAnimationTriggerId,
                activeCampaignLevel = activeCampaignLevel,
                justUnlockedNextCampaignLevel = justUnlockedNextCampaignLevel,
                onAdvanceToNextCampaignLevel = onAdvanceToNextCampaignLevel,
                onPlayAgain = onRestartMatch,
                onReturnToSetup = onReturnToSetup
            )
        }
    }
}

/**
 * Bottom Footer containing:
 * 1. Grid Size segmented selector (`3×3`, `4×4`, `5×5`, `6×6`) — resets board & score to 0-0 on change.
 * 2. Difficulty Toggle (`Easy`, `Medium`, `Hard`) + `Restart Game` button.
 * 3. Persistent Session Stats (`W - L`) & Scrollable `Match History` panel listing the final score
 *    and winner of the last 5 completed games within the current session.
 */
@Composable
private fun BottomControlsAndFooter(
    activeCampaignLevel: CampaignLevel?,
    selectedGridSize: GridSize,
    selectedCpuDifficulty: CpuDifficulty,
    selectedTimerMode: TimerMode,
    players: List<PlayerConfig>,
    sessionStats: Map<Int, PlayerSessionStats>,
    matchHistory: List<MatchHistoryItem>,
    onSelectGridSize: (GridSize) -> Unit,
    onSelectCpuDifficulty: (CpuDifficulty) -> Unit,
    onSelectTimerMode: (TimerMode) -> Unit,
    onRestartMatch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalGamePalette.current

    Card(
        modifier = modifier
            .border(1.dp, palette.border, RoundedCornerShape(18.dp))
            .testTag("bottom_footer_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = palette.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Row 1: Grid Size Segmented Selector (3x3, 4x4, 5x5, 6x6) + Blitz Timer Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = "GRID:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    ),
                    color = palette.mutedText
                )

                GridSize.entries.forEach { sizeOption ->
                    val isSelected = selectedGridSize == sizeOption
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                            .clip(RoundedCornerShape(9.dp))
                            .background(
                                if (isSelected) palette.p1Accent else palette.surfaceElevated
                            )
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) palette.p1Accent else palette.border,
                                shape = RoundedCornerShape(9.dp)
                            )
                            .clickable { onSelectGridSize(sizeOption) }
                            .testTag("grid_size_${sizeOption.boxRows}x${sizeOption.boxCols}_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = sizeOption.label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.5.sp
                            ),
                            color = if (isSelected) Color.Black else palette.primaryText
                        )
                    }
                }

                val isBlitz = selectedTimerMode == TimerMode.BLITZ
                Box(
                    modifier = Modifier
                        .height(34.dp)
                        .clip(RoundedCornerShape(9.dp))
                        .background(if (isBlitz) palette.p2Accent else palette.surfaceElevated)
                        .border(
                            width = if (isBlitz) 1.5.dp else 1.dp,
                            color = if (isBlitz) palette.p2Accent else palette.border,
                            shape = RoundedCornerShape(9.dp)
                        )
                        .clickable {
                            onSelectTimerMode(
                                if (isBlitz) TimerMode.RELAXED else TimerMode.BLITZ
                            )
                        }
                        .padding(horizontal = 8.dp)
                        .testTag("game_timer_toggle_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = if (isBlitz) Icons.Filled.Timer else Icons.Filled.TimerOff,
                            contentDescription = stringResource(R.string.timer_mode_label),
                            tint = if (isBlitz) Color.White else palette.mutedText,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = if (isBlitz) "Blitz (5s)" else "Relaxed",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            color = if (isBlitz) Color.White else palette.primaryText
                        )
                    }
                }
            }

            // Row 2: CPU Difficulty Toggle (Easy, Medium, Hard) + Restart Game Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = if (activeCampaignLevel != null) "LVL ${activeCampaignLevel.levelNumber}:" else "AI:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = if (activeCampaignLevel != null) palette.p1Accent else palette.mutedText
                )

                CpuDifficulty.entries.forEach { diff ->
                    val isSelected = selectedCpuDifficulty == diff
                    val diffColor = when (diff) {
                        CpuDifficulty.EASY -> palette.p3Accent
                        CpuDifficulty.MEDIUM -> palette.streakAccent
                        CpuDifficulty.HARD -> palette.p2Accent
                    }
                    val label = when (diff) {
                        CpuDifficulty.EASY -> stringResource(R.string.diff_easy)
                        CpuDifficulty.MEDIUM -> stringResource(R.string.diff_medium)
                        CpuDifficulty.HARD -> stringResource(R.string.diff_hard)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .clip(RoundedCornerShape(9.dp))
                            .background(
                                if (isSelected) diffColor else palette.surfaceElevated
                            )
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) diffColor else palette.border,
                                shape = RoundedCornerShape(9.dp)
                            )
                            .clickable { onSelectCpuDifficulty(diff) }
                            .testTag("game_diff_${diff.name.lowercase()}_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = if (isSelected) Color.Black else palette.primaryText
                        )
                    }
                }

                Button(
                    onClick = onRestartMatch,
                    shape = RoundedCornerShape(9.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = palette.p2Accent,
                        contentColor = Color.White
                    ),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        horizontal = 10.dp,
                        vertical = 0.dp
                    ),
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("restart_match_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = stringResource(R.string.restart_game_button),
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.restart_game_button),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            // Row 3: Persistent Session Stats Bar (Wins & Losses per player)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(palette.surfaceElevated)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .testTag("session_stats_footer"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.EmojiEvents,
                        contentDescription = null,
                        tint = palette.streakAccent,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = stringResource(R.string.session_stats_title).uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = palette.mutedText
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    players.forEach { player ->
                        val pNum = player.playerNumber
                        val stat = sessionStats[pNum] ?: PlayerSessionStats()
                        val accent = playerAccentColor(pNum)
                        val nameTag = if (player.isAi) "CPU" else "P$pNum"
                        Row(
                            modifier = Modifier.testTag("player${pNum}_stats_pill"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text(
                                text = "$nameTag:",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                color = accent
                            )
                            Text(
                                text = stringResource(
                                    R.string.session_stats_tally_format,
                                    stat.wins,
                                    stat.losses
                                ),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                color = palette.primaryText,
                                modifier = Modifier.testTag("player${pNum}_stats_value")
                            )
                        }
                    }
                }
            }

            // Row 4: Match History Panel (Scrollable list of the last 5 completed games in current session)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(palette.surfaceElevated)
                    .padding(horizontal = 8.dp, vertical = 5.dp)
                    .testTag("match_history_panel"),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.History,
                            contentDescription = null,
                            tint = palette.p1Accent,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = stringResource(R.string.match_history_title).uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.5.sp
                            ),
                            color = palette.mutedText
                        )
                    }
                    Text(
                        text = "${matchHistory.size} / 5",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 9.5.sp
                        ),
                        color = palette.mutedText
                    )
                }

                if (matchHistory.isEmpty()) {
                    Text(
                        text = stringResource(R.string.match_history_empty),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = palette.mutedText
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 56.dp)
                            .testTag("match_history_list"),
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        items(
                            items = matchHistory,
                            key = { it.matchNumber }
                        ) { item ->
                            val winnerColor = when (item.winnerPlayerNumber) {
                                1 -> palette.p1Accent
                                2 -> palette.p2Accent
                                3 -> palette.p3Accent
                                4 -> palette.p4Accent
                                else -> palette.streakAccent
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(palette.backgroundDeep.copy(alpha = 0.55f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                                    .testTag("match_history_item_${item.matchNumber}"),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "#${item.matchNumber} [${item.gridSizeLabel}] ${item.scoreSummary}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontSize = 10.sp
                                    ),
                                    color = palette.primaryText
                                )
                                Text(
                                    text = "Winner: ${item.winnerLabel}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    ),
                                    color = winnerColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BoardZoomToolbar(
    gridLabel: String,
    boardZoom: Float,
    comboCount: Int,
    comboAccentColor: Color,
    comboPulseFraction: Float,
    onAdjustZoom: (Float) -> Unit,
    onResetZoom: () -> Unit
) {
    val palette = LocalGamePalette.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "$gridLabel BOX GRID • 8dp DOTS • 6dp LINES",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = palette.mutedText
            )

            AnimatedVisibility(visible = comboCount >= 2) {
                Box(
                    modifier = Modifier
                        .scale(1f + comboPulseFraction * 0.14f)
                        .clip(RoundedCornerShape(50))
                        .background(comboAccentColor.copy(alpha = 0.25f))
                        .border(1.dp, comboAccentColor, RoundedCornerShape(50))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                        .testTag("combo_pulse_chip")
                ) {
                    Text(
                        text = "COMBO x$comboCount!",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = comboAccentColor
                    )
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(palette.surfaceElevated)
                    .border(1.dp, palette.border, RoundedCornerShape(7.dp))
                    .clickable { onAdjustZoom(-0.1f) }
                    .testTag("zoom_out_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.ZoomOut,
                    contentDescription = stringResource(R.string.zoom_out_desc),
                    tint = palette.primaryText,
                    modifier = Modifier.size(15.dp)
                )
            }

            Box(
                modifier = Modifier
                    .height(28.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(palette.surfaceElevated)
                    .border(1.dp, palette.border, RoundedCornerShape(7.dp))
                    .clickable { onResetZoom() }
                    .padding(horizontal = 7.dp)
                    .testTag("zoom_reset_button"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${(boardZoom * 100).roundToInt()}%",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = palette.p1Accent
                )
            }

            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(palette.surfaceElevated)
                    .border(1.dp, palette.border, RoundedCornerShape(7.dp))
                    .clickable { onAdjustZoom(0.1f) }
                    .testTag("zoom_in_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.ZoomIn,
                    contentDescription = stringResource(R.string.zoom_in_desc),
                    tint = palette.primaryText,
                    modifier = Modifier.size(15.dp)
                )
            }
        }
    }
}

@Composable
private fun TopGameToolbar(
    soundEnabled: Boolean,
    hapticsEnabled: Boolean,
    claimedBoxes: Int,
    totalBoxes: Int,
    onToggleSound: () -> Unit,
    onToggleHaptics: () -> Unit,
    onReturnToSetup: () -> Unit
) {
    val palette = LocalGamePalette.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        OutlinedButton(
            onClick = onReturnToSetup,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = palette.primaryText),
            modifier = Modifier
                .height(48.dp)
                .testTag("back_to_setup_button")
        ) {
            Icon(
                imageVector = Icons.Filled.Tune,
                contentDescription = stringResource(R.string.change_players_button),
                modifier = Modifier.size(17.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = stringResource(R.string.change_players_button),
                style = MaterialTheme.typography.labelLarge
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.boxes_remaining_format, claimedBoxes, totalBoxes),
                style = MaterialTheme.typography.labelSmall,
                color = palette.mutedText
            )
            Spacer(modifier = Modifier.height(3.dp))
            LinearProgressIndicator(
                progress = { if (totalBoxes > 0) claimedBoxes.toFloat() / totalBoxes else 0f },
                modifier = Modifier
                    .width(96.dp)
                    .height(5.dp)
                    .clip(RoundedCornerShape(50)),
                color = palette.p1Accent,
                trackColor = palette.surfaceElevated
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onToggleHaptics,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(palette.surfaceElevated)
                    .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                    .testTag("game_haptics_toggle_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.Vibration,
                    contentDescription = if (hapticsEnabled) {
                        stringResource(R.string.settings_haptics_on)
                    } else {
                        stringResource(R.string.settings_haptics_off)
                    },
                    tint = if (hapticsEnabled) palette.p1Accent else palette.mutedText
                )
            }

            IconButton(
                onClick = onToggleSound,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(palette.surfaceElevated)
                    .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                    .testTag("game_sound_toggle_button")
            ) {
                Icon(
                    imageVector = if (soundEnabled) Icons.Filled.VolumeUp else Icons.Filled.VolumeOff,
                    contentDescription = stringResource(R.string.sound_effects_label),
                    tint = if (soundEnabled) palette.p3Accent else palette.mutedText
                )
            }
        }
    }
}

@Composable
private fun ScoreboardHeader(
    boardState: BoardState,
    activeCampaignLevel: CampaignLevel?,
    selectedTimerMode: TimerMode,
    blitzRemainingSeconds: Float,
    blitzRemainingFraction: Float,
    lastTurnForfeitedByTimeout: Boolean,
    neonGlowPulse: Float,
    comboHeartbeatScale: Float,
    modifier: Modifier = Modifier
) {
    val palette = LocalGamePalette.current

    Card(
        modifier = modifier
            .border(1.dp, palette.border, RoundedCornerShape(18.dp))
            .testTag("scoreboard_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = palette.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (activeCampaignLevel != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(palette.surfaceElevated)
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                        .testTag("active_campaign_level_banner"),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.EmojiEvents,
                            contentDescription = null,
                            tint = palette.streakAccent,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "${activeCampaignLevel.title} — ${activeCampaignLevel.subtitle}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = palette.primaryText
                        )
                    }
                    Text(
                        text = "${activeCampaignLevel.gridSize.label} • ${activeCampaignLevel.difficulty.label.uppercase()}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 10.sp
                        ),
                        color = palette.p1Accent
                    )
                }
            }

            val players = boardState.players
            val chunkedPlayers = players.chunked(2)
            for (rowPlayers in chunkedPlayers) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (player in rowPlayers) {
                        val isTurn = !boardState.isGameOver &&
                            boardState.currentPlayer == player.playerNumber
                        val isComboStreak = isTurn && boardState.isMultiSquareCombo
                        PlayerScorePill(
                            player = player,
                            score = boardState.scoreForPlayer(player.playerNumber),
                            isCurrentTurn = isTurn,
                            isComboStreak = isComboStreak,
                            neonGlowPulse = neonGlowPulse,
                            heartbeatScale = if (isComboStreak) comboHeartbeatScale else 1f,
                            accentColor = playerAccentColor(player.playerNumber),
                            accentFill = playerFillColor(player.playerNumber),
                            testTag = "player${player.playerNumber}_score_card",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowPlayers.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            // Diminishing animated progress bar during the player's turn when Blitz (5s) is active
            val showBlitzBar = selectedTimerMode == TimerMode.BLITZ &&
                activeCampaignLevel == null &&
                !boardState.isGameOver &&
                !boardState.isCurrentPlayerAi

            AnimatedVisibility(visible = showBlitzBar) {
                val animatedFraction by animateFloatAsState(
                    targetValue = blitzRemainingFraction.coerceIn(0f, 1f),
                    animationSpec = tween(durationMillis = 80),
                    label = "blitzProgressFraction"
                )
                val barColor = when {
                    animatedFraction > 0.5f -> palette.p1Accent
                    animatedFraction > 0.25f -> palette.streakAccent
                    else -> palette.p2Accent
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(palette.backgroundDeep)
                        .border(1.dp, barColor.copy(alpha = 0.65f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .testTag("blitz_timer_container"),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Timer,
                                contentDescription = null,
                                tint = barColor,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "BLITZ TURN TIMER",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = barColor
                            )
                        }
                        Text(
                            text = stringResource(R.string.blitz_countdown_format, blitzRemainingSeconds),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = palette.primaryText,
                            modifier = Modifier.testTag("blitz_timer_seconds_text")
                        )
                    }
                    LinearProgressIndicator(
                        progress = { animatedFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(50))
                            .testTag("blitz_timer_progress_bar"),
                        color = barColor,
                        trackColor = palette.surfaceElevated
                    )
                }
            }

            AnimatedVisibility(visible = lastTurnForfeitedByTimeout && !boardState.isGameOver) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(palette.p2Accent.copy(alpha = 0.18f))
                        .border(1.dp, palette.p2Accent, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                        .testTag("blitz_timeout_banner"),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.blitz_timeout_forfeit_notice),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = palette.p2Accent
                    )
                }
            }

            AnimatedVisibility(visible = boardState.hasExtraTurnStreak && !boardState.isGameOver) {
                val activeColor = playerAccentColor(boardState.currentPlayer)
                val activeLabel = if (boardState.isPlayerAi(boardState.currentPlayer)) {
                    "Computer (${boardState.letterForPlayer(boardState.currentPlayer)})"
                } else {
                    "Player ${boardState.currentPlayer} (${boardState.letterForPlayer(boardState.currentPlayer)})"
                }
                val badgeText = if (boardState.isMultiSquareCombo) {
                    stringResource(R.string.combo_badge_format, boardState.currentComboCount)
                } else {
                    stringResource(R.string.extra_turn_badge)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(if (boardState.isMultiSquareCombo) comboHeartbeatScale else 1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(activeColor.copy(alpha = 0.24f))
                        .border(1.5.dp, activeColor, RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                        .testTag("combo_streak_banner"),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Bolt,
                        contentDescription = null,
                        tint = palette.streakAccent,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$badgeText — $activeLabel",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = palette.primaryText
                    )
                }
            }
        }
    }
}

@Composable
private fun PlayerScorePill(
    player: PlayerConfig,
    score: Int,
    isCurrentTurn: Boolean,
    isComboStreak: Boolean,
    neonGlowPulse: Float,
    heartbeatScale: Float,
    accentColor: Color,
    accentFill: Color,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val palette = LocalGamePalette.current
    val borderColor by animateColorAsState(
        targetValue = if (isCurrentTurn) {
            accentColor.copy(alpha = (0.55f + neonGlowPulse * 0.45f).coerceIn(0f, 1f))
        } else {
            palette.border.copy(alpha = 0.45f)
        },
        label = "scoreBorderColor"
    )
    val containerColor by animateColorAsState(
        targetValue = if (isCurrentTurn) accentFill else palette.surfaceElevated,
        label = "scoreContainerColor"
    )

    Row(
        modifier = modifier
            .graphicsLayer {
                scaleX = heartbeatScale
                scaleY = heartbeatScale
            }
            .drawBehind {
                if (isCurrentTurn) {
                    val glowStrength = if (isComboStreak) 0.42f else 0.24f * neonGlowPulse
                    drawRoundRect(
                        color = accentColor.copy(alpha = glowStrength),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(14.dp.toPx())
                    )
                }
            }
            .clip(RoundedCornerShape(14.dp))
            .background(containerColor)
            .border(
                width = if (isCurrentTurn) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(14.dp)
            )
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .background(palette.backgroundDeep)
                    .border(1.5.dp, accentColor, RoundedCornerShape(9.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = player.letter,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = JetBrainsMonoFontFamily,
                        fontWeight = FontWeight.Bold
                    ),
                    color = accentColor
                )
            }

            Column {
                Text(
                    text = if (player.isAi) "COMPUTER" else "PLAYER ${player.playerNumber}",
                    style = MaterialTheme.typography.labelSmall,
                    color = accentColor
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (isCurrentTurn) accentColor else palette.backgroundDeep.copy(alpha = 0.6f)
                        )
                        .padding(horizontal = 6.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = when {
                            isCurrentTurn && player.isAi -> stringResource(R.string.turn_indicator_ai_thinking)
                            isCurrentTurn -> stringResource(R.string.turn_indicator_active)
                            else -> stringResource(R.string.turn_indicator_waiting)
                        },
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (isCurrentTurn) Color.Black else palette.mutedText
                    )
                }
            }
        }

        Text(
            text = score.toString(),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontFamily = JetBrainsMonoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            ),
            color = palette.primaryText,
            modifier = Modifier.testTag("player${player.playerNumber}_score_value")
        )
    }
}

/**
 * Dynamic Dots and Boxes Grid supporting 3x3 up to 6x6 boxes:
 * - Solid white circles for dots (8.dp radius = 16.dp diameter)
 * - 6.dp line thickness (20% opacity faint grey unselected, Bright Cyan P1, Neon Magenta Computer)
 * - 40% opacity player color box fills when completed
 */
@Composable
private fun DotsAndBoxesGrid(
    boardState: BoardState,
    hoveredEdge: EdgeCoordinate?,
    neonGlowPulse: Float,
    comboPulseFraction: Float,
    dotSlotSize: Dp,
    boxSize: Dp,
    onEdgeHover: (EdgeCoordinate?) -> Unit,
    onEdgeClick: (EdgeCoordinate) -> Unit
) {
    val activePlayerColor = playerAccentColor(boardState.currentPlayer)

    Column(
        modifier = Modifier.testTag("dots_and_boxes_grid"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (dotRow in 0 until boardState.dotRows) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (dotCol in 0 until boardState.dotCols) {
                    DotNode(slotSize = dotSlotSize)

                    if (dotCol < boardState.boxCols) {
                        val edgeCoord = EdgeCoordinate(
                            orientation = EdgeOrientation.HORIZONTAL,
                            row = dotRow,
                            col = dotCol
                        )
                        val claimedBy = boardState.horizontalEdges[dotRow][dotCol]
                        HorizontalEdgeCell(
                            edge = edgeCoord,
                            claimedBy = claimedBy,
                            isLastClaimed = boardState.lastClaimedEdge == edgeCoord,
                            isHoveredExternal = hoveredEdge == edgeCoord,
                            neonGlowPulse = neonGlowPulse,
                            activePlayerColor = activePlayerColor,
                            width = boxSize,
                            slotHeight = (dotSlotSize * 1.4f).coerceAtLeast(18.dp),
                            onHover = onEdgeHover,
                            onClick = { onEdgeClick(edgeCoord) }
                        )
                    }
                }
            }

            if (dotRow < boardState.boxRows) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (dotCol in 0 until boardState.dotCols) {
                        val vEdgeCoord = EdgeCoordinate(
                            orientation = EdgeOrientation.VERTICAL,
                            row = dotRow,
                            col = dotCol
                        )
                        val claimedBy = boardState.verticalEdges[dotRow][dotCol]
                        VerticalEdgeCell(
                            edge = vEdgeCoord,
                            claimedBy = claimedBy,
                            isLastClaimed = boardState.lastClaimedEdge == vEdgeCoord,
                            isHoveredExternal = hoveredEdge == vEdgeCoord,
                            neonGlowPulse = neonGlowPulse,
                            activePlayerColor = activePlayerColor,
                            slotWidth = dotSlotSize,
                            height = boxSize,
                            onHover = onEdgeHover,
                            onClick = { onEdgeClick(vEdgeCoord) }
                        )

                        if (dotCol < boardState.boxCols) {
                            val boxOwner = boardState.boxes[dotRow][dotCol]
                            val boxCoord = BoxCoordinate(dotRow, dotCol)
                            val ownerLetter = if (boxOwner != 0) {
                                boardState.letterForPlayer(boxOwner)
                            } else {
                                ""
                            }
                            val isNewlyCaptured = boardState.lastCapturedBoxes.contains(boxCoord)
                            val isInComboChain = boardState.isMultiSquareCombo &&
                                boardState.comboChainBoxes.contains(boxCoord)

                            PlayableBoxCell(
                                row = dotRow,
                                col = dotCol,
                                owner = boxOwner,
                                letter = ownerLetter,
                                isNewlyCaptured = isNewlyCaptured,
                                isInComboChain = isInComboChain,
                                comboPulseFraction = if (isNewlyCaptured || isInComboChain) {
                                    comboPulseFraction
                                } else {
                                    0f
                                },
                                size = boxSize
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Solid white circle dot with 8.dp radius (16.dp diameter).
 */
@Composable
private fun DotNode(slotSize: Dp) {
    val palette = LocalGamePalette.current
    Box(
        modifier = Modifier.size(slotSize),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(slotSize)
                .shadow(elevation = 3.dp, shape = CircleShape, spotColor = palette.dotColor)
                .clip(CircleShape)
                .background(palette.dotColor)
        )
    }
}

/**
 * 6.dp thickness horizontal line with smooth Jetpack Compose point-A-to-point-B draw animation:
 * - Unselected: faint grey (20% opacity)
 * - Player 1: Bright Cyan (#00E5FF)
 * - Computer / Player 2: Neon Magenta (#FF007F)
 */
@Composable
private fun HorizontalEdgeCell(
    edge: EdgeCoordinate,
    claimedBy: Int,
    isLastClaimed: Boolean,
    isHoveredExternal: Boolean,
    neonGlowPulse: Float,
    activePlayerColor: Color,
    width: Dp,
    slotHeight: Dp,
    onHover: (EdgeCoordinate?) -> Unit,
    onClick: () -> Unit
) {
    val palette = LocalGamePalette.current
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()

    val showHover = claimedBy == 0 && (isHovered || isPressed || isHoveredExternal)
    LaunchedEffect(isHovered, isPressed) {
        if (isHovered || isPressed) onHover(edge)
    }

    val ownerColor = if (claimedBy != 0) playerAccentColor(claimedBy) else Color.Transparent
    val trackColor by animateColorAsState(
        targetValue = if (showHover) {
            activePlayerColor.copy(alpha = 0.65f)
        } else {
            palette.idleEdge
        },
        animationSpec = tween(durationMillis = 180),
        label = "hEdgeTrackColor"
    )
    // Smoothly animate drawing the line from Point A (left dot, 0f) to Point B (right dot, 1f)
    val drawProgress by animateFloatAsState(
        targetValue = if (claimedBy != 0) 1f else 0f,
        animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing),
        label = "hEdgeDrawProgress"
    )

    val barThickness = 6.dp

    Box(
        modifier = Modifier
            .width(width)
            .height(slotHeight)
            .hoverable(interactionSource = interactionSource)
            .then(
                if (claimedBy == 0) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick
                    )
                } else {
                    Modifier
                }
            )
            .semantics {
                contentDescription = "Horizontal line row ${edge.row} column ${edge.col}"
            }
            .testTag("h_edge_${edge.row}_${edge.col}"),
        contentAlignment = Alignment.Center
    ) {
        // Unselected 20% opacity faint grey track (or hover preview)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 1.dp)
                .height(barThickness)
                .clip(RoundedCornerShape(50))
                .background(trackColor),
            contentAlignment = Alignment.CenterStart
        ) {
            // Claimed neon line drawing smoothly from Point A (left) to Point B (right)
            if (claimedBy != 0 || drawProgress > 0.01f) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(drawProgress.coerceIn(0f, 1f))
                        .height(barThickness)
                        .drawBehind {
                            drawRoundRect(
                                color = ownerColor.copy(alpha = 0.45f * neonGlowPulse),
                                size = size.copy(height = size.height * 2.2f),
                                topLeft = Offset(0f, -size.height * 0.6f)
                            )
                        }
                        .clip(RoundedCornerShape(50))
                        .background(ownerColor)
                        .then(
                            if (isLastClaimed) {
                                Modifier.border(1.dp, palette.primaryText, RoundedCornerShape(50))
                            } else {
                                Modifier
                            }
                        )
                )
            }
        }
    }
}

/**
 * 6.dp thickness vertical line with smooth Jetpack Compose point-A-to-point-B draw animation:
 * - Unselected: faint grey (20% opacity)
 * - Player 1: Bright Cyan (#00E5FF)
 * - Computer / Player 2: Neon Magenta (#FF007F)
 */
@Composable
private fun VerticalEdgeCell(
    edge: EdgeCoordinate,
    claimedBy: Int,
    isLastClaimed: Boolean,
    isHoveredExternal: Boolean,
    neonGlowPulse: Float,
    activePlayerColor: Color,
    slotWidth: Dp,
    height: Dp,
    onHover: (EdgeCoordinate?) -> Unit,
    onClick: () -> Unit
) {
    val palette = LocalGamePalette.current
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()

    val showHover = claimedBy == 0 && (isHovered || isPressed || isHoveredExternal)
    LaunchedEffect(isHovered, isPressed) {
        if (isHovered || isPressed) onHover(edge)
    }

    val ownerColor = if (claimedBy != 0) playerAccentColor(claimedBy) else Color.Transparent
    val trackColor by animateColorAsState(
        targetValue = if (showHover) {
            activePlayerColor.copy(alpha = 0.65f)
        } else {
            palette.idleEdge
        },
        animationSpec = tween(durationMillis = 180),
        label = "vEdgeTrackColor"
    )
    // Smoothly animate drawing the line from Point A (top dot, 0f) to Point B (bottom dot, 1f)
    val drawProgress by animateFloatAsState(
        targetValue = if (claimedBy != 0) 1f else 0f,
        animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing),
        label = "vEdgeDrawProgress"
    )

    val barThickness = 6.dp

    Box(
        modifier = Modifier
            .width(slotWidth)
            .height(height)
            .hoverable(interactionSource = interactionSource)
            .then(
                if (claimedBy == 0) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick
                    )
                } else {
                    Modifier
                }
            )
            .semantics {
                contentDescription = "Vertical line row ${edge.row} column ${edge.col}"
            }
            .testTag("v_edge_${edge.row}_${edge.col}"),
        contentAlignment = Alignment.Center
    ) {
        // Unselected 20% opacity faint grey track (or hover preview)
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .padding(vertical = 1.dp)
                .width(barThickness)
                .clip(RoundedCornerShape(50))
                .background(trackColor),
            contentAlignment = Alignment.TopCenter
        ) {
            // Claimed neon line drawing smoothly from Point A (top) to Point B (bottom)
            if (claimedBy != 0 || drawProgress > 0.01f) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight(drawProgress.coerceIn(0f, 1f))
                        .width(barThickness)
                        .drawBehind {
                            drawRoundRect(
                                color = ownerColor.copy(alpha = 0.45f * neonGlowPulse),
                                size = size.copy(width = size.width * 2.2f),
                                topLeft = Offset(-size.width * 0.6f, 0f)
                            )
                        }
                        .clip(RoundedCornerShape(50))
                        .background(ownerColor)
                        .then(
                            if (isLastClaimed) {
                                Modifier.border(1.dp, palette.primaryText, RoundedCornerShape(50))
                            } else {
                                Modifier
                            }
                        )
                )
            }
        }
    }
}

/**
 * PlayableBoxCell filled with 40% opacity of the owning player's color when completed,
 * pulsing once when filled using Jetpack Compose animations.
 */
@Composable
private fun PlayableBoxCell(
    row: Int,
    col: Int,
    owner: Int,
    letter: String,
    isNewlyCaptured: Boolean,
    isInComboChain: Boolean,
    comboPulseFraction: Float,
    size: Dp
) {
    val palette = LocalGamePalette.current
    val accentColor = if (owner != 0) playerAccentColor(owner) else Color.Transparent
    // Smoothly fade in 40% opacity fill of the owning player's color
    val boxFillColor by animateColorAsState(
        targetValue = if (owner != 0) accentColor.copy(alpha = 0.40f) else Color.Transparent,
        animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing),
        label = "boxFillColorAnim"
    )

    // Captured boxes pulse once when filled (1.0f -> 1.16f -> 1.0f)
    val singleCapturePulse = remember { Animatable(1f) }
    val charJumpScale = remember { Animatable(0.2f) }
    LaunchedEffect(owner) {
        if (owner != 0) {
            launch {
                singleCapturePulse.snapTo(0.85f)
                singleCapturePulse.animateTo(
                    targetValue = 1.16f,
                    animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing)
                )
                singleCapturePulse.animateTo(
                    targetValue = 1.0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
            }
            charJumpScale.snapTo(0.25f)
            charJumpScale.animateTo(
                targetValue = 1.75f,
                animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing)
            )
            charJumpScale.animateTo(
                targetValue = 1.0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        } else {
            singleCapturePulse.snapTo(1f)
            charJumpScale.snapTo(0.2f)
        }
    }

    val comboBoxScale = if (isNewlyCaptured || isInComboChain) {
        singleCapturePulse.value * (1f + (comboPulseFraction * 0.12f))
    } else {
        singleCapturePulse.value
    }

    Box(
        modifier = Modifier
            .size(size)
            .padding(1.5.dp)
            .graphicsLayer {
                scaleX = comboBoxScale
                scaleY = comboBoxScale
            }
            .clip(RoundedCornerShape(6.dp))
            .background(boxFillColor)
            .then(
                if ((isNewlyCaptured || isInComboChain) && owner != 0) {
                    Modifier.border(
                        width = if (isInComboChain) 2.dp else 1.5.dp,
                        color = if (comboPulseFraction > 0.2f) palette.primaryText else accentColor,
                        shape = RoundedCornerShape(6.dp)
                    )
                } else {
                    Modifier
                }
            )
            .testTag("box_${row}_${col}"),
        contentAlignment = Alignment.Center
    ) {
        if (isNewlyCaptured && comboPulseFraction > 0.02f && owner != 0) {
            val particleProgress = 1f - comboPulseFraction
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(this.size.width / 2f, this.size.height / 2f)
                val maxRadius = this.size.maxDimension * 0.85f
                val particleCount = 12
                for (i in 0 until particleCount) {
                    val angle = (2.0 * PI * i) / particleCount
                    val distance = maxRadius * particleProgress
                    val px = center.x + (cos(angle) * distance).toFloat()
                    val py = center.y + (sin(angle) * distance).toFloat()
                    drawCircle(
                        color = accentColor.copy(alpha = comboPulseFraction),
                        radius = (3.5.dp.toPx() * comboPulseFraction).coerceAtLeast(1f),
                        center = Offset(px, py)
                    )
                }
            }
        }

        if (owner != 0) {
            Text(
                text = letter,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = when {
                        size >= 44.dp -> 18.sp
                        size >= 28.dp -> 14.sp
                        else -> 11.sp
                    },
                    shadow = Shadow(
                        color = accentColor,
                        offset = Offset.Zero,
                        blurRadius = 14f
                    )
                ),
                color = palette.primaryText,
                modifier = Modifier.scale(charJumpScale.value)
            )
        }
    }
}

@Composable
private fun GameOverBanner(
    boardState: BoardState,
    activeCampaignLevel: CampaignLevel?,
    justUnlockedNextCampaignLevel: Boolean,
    onAdvanceToNextCampaignLevel: () -> Unit,
    onPlayAgain: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalGamePalette.current
    val winner = boardState.winner
    val accentColor = if (winner != null && winner > 0) {
        playerAccentColor(winner)
    } else {
        palette.streakAccent
    }

    val titleText = if (winner != null && winner > 0) {
        val winnerName = if (boardState.isPlayerAi(winner)) "Computer" else "Player $winner"
        stringResource(
            R.string.winner_banner_player,
            winnerName,
            boardState.letterForPlayer(winner),
            boardState.scoreForPlayer(winner)
        )
    } else {
        stringResource(R.string.winner_banner_tie, boardState.maxScore)
    }

    val summaryScores = boardState.players.joinToString("  •  ") { p ->
        "${p.letter}: ${boardState.scoreForPlayer(p.playerNumber)}"
    }

    val nextLevel = activeCampaignLevel?.nextLevel()
    val canAdvanceCampaign = activeCampaignLevel != null && winner == 1 && nextLevel != null

    Card(
        modifier = modifier
            .border(2.dp, accentColor, RoundedCornerShape(16.dp))
            .testTag("game_over_banner"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = palette.surfaceElevated)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.EmojiEvents,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            text = titleText,
                            style = MaterialTheme.typography.titleMedium,
                            color = palette.primaryText
                        )
                        Text(
                            text = summaryScores,
                            style = MaterialTheme.typography.labelSmall,
                            color = accentColor
                        )
                        if (justUnlockedNextCampaignLevel && nextLevel != null) {
                            Text(
                                text = stringResource(
                                    R.string.campaign_next_level_unlocked,
                                    nextLevel.levelNumber
                                ),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = palette.streakAccent,
                                modifier = Modifier.testTag("campaign_unlocked_notice")
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (canAdvanceCampaign) {
                        Button(
                            onClick = onAdvanceToNextCampaignLevel,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = palette.streakAccent,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier.testTag("next_campaign_level_button")
                        ) {
                            Text(
                                text = stringResource(R.string.next_level_button),
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }

                    Button(
                        onClick = onPlayAgain,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = accentColor,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.testTag("play_again_button")
                    ) {
                        Text(
                            text = stringResource(R.string.play_again_button),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}

private data class ConfettiParticleSpec(
    val angleRad: Float,
    val speedFactor: Float,
    val startXFraction: Float,
    val fallSpeed: Float,
    val driftAmplitude: Float,
    val rotationSpeed: Float,
    val sizeDp: Float,
    val colorIndex: Int
)

/**
 * Full-Screen Victory Animation (Confetti Burst + Celebratory Radial Glow) triggered
 * after a match ends and the winner is declared.
 */
@Composable
private fun FullScreenVictoryAnimationOverlay(
    boardState: BoardState,
    triggerId: Int,
    activeCampaignLevel: CampaignLevel?,
    justUnlockedNextCampaignLevel: Boolean,
    onAdvanceToNextCampaignLevel: () -> Unit,
    onPlayAgain: () -> Unit,
    onReturnToSetup: () -> Unit
) {
    val palette = LocalGamePalette.current
    val winner = boardState.winner
    val winnerAccent = if (winner != null && winner > 0) {
        playerAccentColor(winner)
    } else {
        palette.streakAccent
    }

    var isCardDismissed by remember(triggerId, boardState.isGameOver) { mutableStateOf(false) }

    val burstProgress = remember(triggerId) { Animatable(0f) }
    val glowPulseTransition = rememberInfiniteTransition(label = "victoryGlowTransition")
    val glowPulse by glowPulseTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "victoryGlowPulse"
    )
    val continuousFallPhase by glowPulseTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "victoryContinuousFall"
    )

    LaunchedEffect(triggerId, boardState.isGameOver) {
        if (boardState.isGameOver) {
            burstProgress.snapTo(0f)
            burstProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 2200, easing = FastOutSlowInEasing)
            )
        }
    }

    val particles = remember {
        List(76) { idx ->
            val goldenAngle = idx * 2.3999632f
            ConfettiParticleSpec(
                angleRad = goldenAngle,
                speedFactor = 0.25f + ((idx * 37) % 100) / 100f * 0.85f,
                startXFraction = ((idx * 19) % 100) / 100f,
                fallSpeed = 0.4f + ((idx * 29) % 70) / 100f,
                driftAmplitude = 18f + (idx % 5) * 9f,
                rotationSpeed = (idx % 7 - 3) * 120f,
                sizeDp = 5f + (idx % 4) * 2.5f,
                colorIndex = idx % 5
            )
        }
    }

    val confettiColors = listOf(
        palette.p1Accent,
        palette.p2Accent,
        palette.streakAccent,
        palette.p3Accent,
        palette.p4Accent
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("victory_confetti_overlay"),
        contentAlignment = Alignment.Center
    ) {
        // Full-Screen Celebratory Radial Glow + Multi-Stage Confetti Burst Canvas
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .testTag("victory_confetti_canvas")
        ) {
            val center = Offset(size.width / 2f, size.height * 0.42f)
            val maxDim = size.maxDimension

            // 1. Celebratory Radial Glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        winnerAccent.copy(alpha = 0.28f * glowPulse),
                        palette.streakAccent.copy(alpha = 0.12f * glowPulse),
                        Color.Transparent
                    ),
                    center = center,
                    radius = maxDim * (0.48f + 0.14f * glowPulse)
                ),
                center = center,
                radius = maxDim * 0.65f
            )

            // 2. Expanding Shockwave Ring
            val ringProgress = burstProgress.value
            if (ringProgress < 0.98f) {
                drawCircle(
                    color = winnerAccent.copy(alpha = (1f - ringProgress) * 0.55f),
                    center = center,
                    radius = maxDim * 0.55f * ringProgress,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4.dp.toPx())
                )
            }

            // 3. Confetti Burst + Cascading Shimmer Particles
            val t = burstProgress.value
            for (p in particles) {
                val cColor = confettiColors[p.colorIndex]
                // Radial burst trajectory combined with gravity & continuous celebratory cascade
                val burstRadius = maxDim * 0.52f * p.speedFactor * sin(t * (PI / 2.0)).toFloat()
                val gravityY = (t * t) * size.height * 0.55f * p.fallSpeed +
                    continuousFallPhase * size.height * 0.22f
                val swayX = sin((t * 6f + p.startXFraction * 10f).toDouble()).toFloat() * p.driftAmplitude

                val px = (center.x + cos(p.angleRad) * burstRadius + swayX)
                    .coerceIn(0f, size.width)
                val py = ((center.y + sin(p.angleRad) * burstRadius * 0.7f + gravityY) % size.height)
                    .let { if (it < 0f) it + size.height else it }

                val particleAlpha = (0.92f - t * 0.25f).coerceIn(0.35f, 0.95f)
                val rectW = p.sizeDp.dp.toPx()
                val rectH = (p.sizeDp * 0.58f).dp.toPx()

                rotate(
                    degrees = (t * p.rotationSpeed + continuousFallPhase * 180f) % 360f,
                    pivot = Offset(px, py)
                ) {
                    drawRect(
                        color = cColor.copy(alpha = particleAlpha),
                        topLeft = Offset(px - rectW / 2f, py - rectH / 2f),
                        size = Size(rectW, rectH)
                    )
                }
            }
        }

        // Celebratory Victory Card (can be dismissed to inspect the board directly)
        AnimatedVisibility(
            visible = !isCardDismissed,
            enter = fadeIn(tween(240)) + scaleIn(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
        ) {
            val headlineText = when {
                winner == null || winner == 0 -> stringResource(R.string.winner_banner_tie, boardState.maxScore)
                boardState.isPlayerAi(winner) -> "COMPUTER WINS (${boardState.scoreForPlayer(winner)} PTS)"
                else -> "PLAYER $winner VICTORY! (${boardState.scoreForPlayer(winner)} PTS)"
            }
            val nextLevel = activeCampaignLevel?.nextLevel()
            val canAdvanceCampaign = activeCampaignLevel != null && winner == 1 && nextLevel != null

            Card(
                modifier = Modifier
                    .widthIn(max = 380.dp)
                    .padding(horizontal = 20.dp)
                    .shadow(24.dp, RoundedCornerShape(24.dp), spotColor = winnerAccent)
                    .border(2.5.dp, winnerAccent, RoundedCornerShape(24.dp))
                    .testTag("victory_celebration_modal"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = palette.backgroundDeep.copy(alpha = 0.94f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .scale(0.95f + glowPulse * 0.12f)
                            .clip(CircleShape)
                            .background(winnerAccent.copy(alpha = 0.22f))
                            .border(2.dp, winnerAccent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.EmojiEvents,
                            contentDescription = null,
                            tint = winnerAccent,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Text(
                        text = stringResource(R.string.victory_overlay_subtitle),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = JetBrainsMonoFontFamily,
                            letterSpacing = 1.5.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = palette.streakAccent,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = headlineText,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.Black,
                            shadow = Shadow(
                                color = winnerAccent,
                                offset = Offset.Zero,
                                blurRadius = 18f
                            )
                        ),
                        color = palette.primaryText,
                        textAlign = TextAlign.Center
                    )

                    val scoreBreakdown = boardState.players.joinToString("   •   ") { p ->
                        val label = if (p.isAi) "CPU (${p.letter})" else "P${p.playerNumber} (${p.letter})"
                        "$label: ${boardState.scoreForPlayer(p.playerNumber)}"
                    }
                    Text(
                        text = scoreBreakdown,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.Bold
                        ),
                        color = winnerAccent,
                        textAlign = TextAlign.Center
                    )

                    if (activeCampaignLevel != null) {
                        val campaignStatusText = when {
                            winner == 1 && nextLevel == null -> stringResource(R.string.campaign_completed_banner)
                            justUnlockedNextCampaignLevel && nextLevel != null -> stringResource(
                                R.string.campaign_next_level_unlocked,
                                nextLevel.levelNumber
                            )
                            winner == 1 && nextLevel != null -> "Level ${activeCampaignLevel.levelNumber} Cleared!"
                            else -> "Beat the Computer on ${activeCampaignLevel.title} to unlock the next stage!"
                        }
                        Text(
                            text = campaignStatusText,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = palette.streakAccent,
                            textAlign = TextAlign.Center
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (canAdvanceCampaign) {
                            Button(
                                onClick = onAdvanceToNextCampaignLevel,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("victory_next_level_button"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = palette.streakAccent,
                                    contentColor = Color.Black
                                )
                            ) {
                                Text(
                                    text = stringResource(R.string.next_level_button),
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }

                        Button(
                            onClick = onPlayAgain,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("victory_play_again_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = winnerAccent,
                                contentColor = Color.Black
                            )
                        ) {
                            Text(
                                text = stringResource(R.string.play_again_button),
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = onReturnToSetup,
                            modifier = Modifier.testTag("victory_main_menu_button")
                        ) {
                            Text(
                                text = "Main Menu",
                                color = palette.mutedText
                            )
                        }
                        TextButton(
                            onClick = { isCardDismissed = true },
                            modifier = Modifier.testTag("dismiss_victory_overlay_button")
                        ) {
                            Text(
                                text = "Inspect Board",
                                color = palette.primaryText
                            )
                        }
                    }
                }
            }
        }
    }
}
