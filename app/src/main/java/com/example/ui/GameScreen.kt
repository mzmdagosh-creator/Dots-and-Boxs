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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
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
import com.example.model.BoardState
import com.example.model.BoxCoordinate
import com.example.model.EdgeCoordinate
import com.example.model.EdgeOrientation
import com.example.model.PlayerConfig
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalGamePalette
import com.example.ui.theme.playerAccentColor
import com.example.ui.theme.playerFillColor
import com.example.viewmodel.PlayerSessionStats
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun GameScreen(
    themeMode: AppThemeMode,
    soundEnabled: Boolean,
    boardState: BoardState,
    sessionStats: Map<Int, PlayerSessionStats>,
    hoveredEdge: EdgeCoordinate?,
    boardZoom: Float,
    onToggleTheme: () -> Unit,
    onToggleSound: () -> Unit,
    onEdgeHover: (EdgeCoordinate?) -> Unit,
    onEdgeClick: (EdgeCoordinate) -> Unit,
    onAdjustZoom: (Float) -> Unit,
    onSetZoom: (Float) -> Unit,
    onRestartMatch: () -> Unit,
    onReturnToSetup: () -> Unit,
    onOpenHtmlViewer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalGamePalette.current

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
        targetValue = 1.055f,
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
                1.045f + (boardState.currentComboCount.coerceAtMost(6) * 0.008f)
            } else {
                1.018f
            }

            launch {
                val shakeMagnitude = if (isCombo) 10f else 4.5f
                for (step in listOf(1f, -0.85f, 0.7f, -0.5f, 0.3f, 0f)) {
                    cameraShakeAnim.animateTo(
                        targetValue = step * shakeMagnitude,
                        animationSpec = tween(durationMillis = 42)
                    )
                }
            }

            launch {
                boardComboZoomAnim.snapTo(1f)
                boardComboZoomAnim.animateTo(
                    targetValue = targetZoomPunch,
                    animationSpec = tween(durationMillis = 130, easing = FastOutSlowInEasing)
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
                    animationSpec = tween(durationMillis = 720, easing = FastOutSlowInEasing)
                )
            }

            if (isCombo) {
                launch {
                    overlayBurstScale.snapTo(0.3f)
                    overlayBurstAlpha.snapTo(1f)
                    overlayBurstScale.animateTo(
                        targetValue = 1.12f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    )
                    overlayBurstScale.animateTo(
                        targetValue = 1.0f,
                        animationSpec = tween(durationMillis = 220)
                    )
                    overlayBurstAlpha.animateTo(
                        targetValue = 0f,
                        animationSpec = tween(durationMillis = 360)
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

    // Outer Wrapper Container with natural padding boundary and zero-scroll auto-fit layout
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
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
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
            // Top Action Bar with Quick Theme & Sound Toggles
            TopGameToolbar(
                themeMode = themeMode,
                soundEnabled = soundEnabled,
                claimedBoxes = boardState.totalClaimedBoxes,
                onToggleTheme = onToggleTheme,
                onToggleSound = onToggleSound,
                onReturnToSetup = onReturnToSetup,
                onRestartMatch = onRestartMatch,
                onOpenHtmlViewer = onOpenHtmlViewer
            )

            // Scoreboard with Pulsing Turn Neon Indicator & Combo Heartbeat
            ScoreboardHeader(
                boardState = boardState,
                neonGlowPulse = neonGlowPulse,
                comboHeartbeatScale = comboHeartbeatScale,
                modifier = Modifier.fillMaxWidth()
            )

            // Winner Banner if all 32 boxes are claimed
            AnimatedVisibility(
                visible = boardState.isGameOver,
                enter = fadeIn() + scaleIn(
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                )
            ) {
                GameOverBanner(
                    boardState = boardState,
                    onPlayAgain = onRestartMatch,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Auto-Scaling 5x9 Grid Container (100% visible in remaining viewport height & width, zero scrolling)
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
                    .border(boardBorderWidth, boardBorderColor, RoundedCornerShape(22.dp)),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = palette.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    BoardZoomToolbar(
                        boardZoom = boardZoom,
                        comboCount = boardState.currentComboCount,
                        comboAccentColor = activeComboColor,
                        comboPulseFraction = pulseGlowAlpha,
                        onAdjustZoom = onAdjustZoom,
                        onResetZoom = { onSetZoom(0.92f) }
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
                        val safeStageWidth = (maxWidth * 0.94f).coerceAtLeast(200.dp)
                        val safeStageHeight = (maxHeight * 0.94f).coerceAtLeast(130.dp)

                        val dotCandidateW = (safeStageWidth * 0.034f).coerceIn(8.dp, 14.dp)
                        val dotCandidateH = (safeStageHeight * 0.06f).coerceIn(8.dp, 14.dp)
                        val baseDotSlotSize = min(dotCandidateW, dotCandidateH)

                        val maxBoxByWidth = (safeStageWidth - (baseDotSlotSize * 9)) / 8f
                        val maxBoxByHeight = (safeStageHeight - (baseDotSlotSize * 5)) / 4f
                        val baseBoxSize = min(maxBoxByWidth, maxBoxByHeight).coerceIn(16.dp, 64.dp)

                        val scaledDotSlotSize = (baseDotSlotSize * boardZoom).coerceIn(7.dp, 20.dp)
                        val scaledBoxSize = (baseBoxSize * boardZoom).coerceIn(14.dp, 72.dp)

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

            // Persistent Session Stats Section in the Footer
            SessionStatsFooter(
                players = boardState.players,
                sessionStats = sessionStats,
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
                            fontSize = 28.sp,
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
    }
}

/**
 * Persistent 'Stats' section in the footer tracking wins and losses across matches
 * within the current session for each active player.
 */
@Composable
private fun SessionStatsFooter(
    players: List<PlayerConfig>,
    sessionStats: Map<Int, PlayerSessionStats>,
    modifier: Modifier = Modifier
) {
    val palette = LocalGamePalette.current

    Card(
        modifier = modifier
            .border(1.dp, palette.border, RoundedCornerShape(16.dp))
            .testTag("session_stats_footer"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = palette.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.EmojiEvents,
                    contentDescription = null,
                    tint = palette.streakAccent,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = stringResource(R.string.session_stats_title).uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold
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
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(palette.surfaceElevated)
                            .border(1.dp, accent.copy(alpha = 0.65f), RoundedCornerShape(50))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                            .testTag("player${pNum}_stats_pill"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "$nameTag (${player.letter}):",
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
        }
    }
}

@Composable
private fun BoardZoomToolbar(
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
                text = "5×9 AUTO-FIT GRID",
                style = MaterialTheme.typography.labelSmall,
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
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(palette.surfaceElevated)
                    .border(1.dp, palette.border, RoundedCornerShape(8.dp))
                    .clickable { onAdjustZoom(-0.1f) }
                    .testTag("zoom_out_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.ZoomOut,
                    contentDescription = stringResource(R.string.zoom_out_desc),
                    tint = palette.primaryText,
                    modifier = Modifier.size(16.dp)
                )
            }

            Box(
                modifier = Modifier
                    .height(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(palette.surfaceElevated)
                    .border(1.dp, palette.border, RoundedCornerShape(8.dp))
                    .clickable { onResetZoom() }
                    .padding(horizontal = 8.dp)
                    .testTag("zoom_reset_button"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${(boardZoom * 100).roundToInt()}% FIT",
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.p1Accent
                )
            }

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(palette.surfaceElevated)
                    .border(1.dp, palette.border, RoundedCornerShape(8.dp))
                    .clickable { onAdjustZoom(0.1f) }
                    .testTag("zoom_in_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.ZoomIn,
                    contentDescription = stringResource(R.string.zoom_in_desc),
                    tint = palette.primaryText,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun TopGameToolbar(
    themeMode: AppThemeMode,
    soundEnabled: Boolean,
    claimedBoxes: Int,
    onToggleTheme: () -> Unit,
    onToggleSound: () -> Unit,
    onReturnToSetup: () -> Unit,
    onRestartMatch: () -> Unit,
    onOpenHtmlViewer: () -> Unit
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
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.change_players_button),
                modifier = Modifier.size(18.dp)
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
                text = stringResource(R.string.boxes_remaining_format, claimedBoxes),
                style = MaterialTheme.typography.labelSmall,
                color = palette.mutedText
            )
            Spacer(modifier = Modifier.height(3.dp))
            LinearProgressIndicator(
                progress = { claimedBoxes / 32f },
                modifier = Modifier
                    .width(80.dp)
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
                onClick = onToggleTheme,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(palette.surfaceElevated)
                    .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                    .testTag("game_theme_toggle_button")
            ) {
                Icon(
                    imageVector = if (themeMode == AppThemeMode.CLASSIC_DARK) {
                        Icons.Filled.DarkMode
                    } else {
                        Icons.Filled.Contrast
                    },
                    contentDescription = stringResource(R.string.theme_picker_label),
                    tint = palette.p1Accent
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

            IconButton(
                onClick = onOpenHtmlViewer,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(palette.surfaceElevated)
                    .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                    .testTag("game_html_viewer_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.Code,
                    contentDescription = stringResource(R.string.view_html_button),
                    tint = palette.p2Accent
                )
            }

            IconButton(
                onClick = onRestartMatch,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(palette.surfaceElevated)
                    .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                    .testTag("restart_match_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = stringResource(R.string.reset_board_button),
                    tint = palette.p1Accent
                )
            }
        }
    }
}

@Composable
private fun ScoreboardHeader(
    boardState: BoardState,
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

            AnimatedVisibility(visible = boardState.hasExtraTurnStreak && !boardState.isGameOver) {
                val activeColor = playerAccentColor(boardState.currentPlayer)
                val activeLabel = if (boardState.isPlayerAi(boardState.currentPlayer)) {
                    "CPU (${boardState.letterForPlayer(boardState.currentPlayer)})"
                } else {
                    "P${boardState.currentPlayer} (${boardState.letterForPlayer(boardState.currentPlayer)})"
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
                    text = if (player.isAi) "CPU" else "PLAYER ${player.playerNumber}",
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.mutedText
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
                        color = if (isCurrentTurn) Color.White else palette.mutedText
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
        for (dotRow in 0 until BoardState.DOT_ROWS) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (dotCol in 0 until BoardState.DOT_COLS) {
                    DotNode(slotSize = dotSlotSize)

                    if (dotCol < BoardState.BOX_COLS) {
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
                            slotHeight = (dotSlotSize * 1.5f).coerceAtLeast(16.dp),
                            onHover = onEdgeHover,
                            onClick = { onEdgeClick(edgeCoord) }
                        )
                    }
                }
            }

            if (dotRow < BoardState.BOX_ROWS) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (dotCol in 0 until BoardState.DOT_COLS) {
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

                        if (dotCol < BoardState.BOX_COLS) {
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

@Composable
private fun DotNode(slotSize: Dp) {
    val palette = LocalGamePalette.current
    val visualDotSize = (slotSize * 0.78f).coerceAtLeast(6.dp)
    Box(
        modifier = Modifier.size(slotSize),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(visualDotSize)
                .shadow(elevation = 4.dp, shape = CircleShape, spotColor = palette.dotColor)
                .clip(CircleShape)
                .background(palette.dotColor)
        )
    }
}

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
    val barColor by animateColorAsState(
        targetValue = if (claimedBy != 0) {
            ownerColor
        } else if (showHover) {
            activePlayerColor.copy(alpha = 0.75f)
        } else {
            palette.idleEdge
        },
        label = "hEdgeColor"
    )

    val barThickness = if (claimedBy != 0 || showHover) 7.dp else 5.dp

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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 1.dp)
                .height(barThickness)
                .drawBehind {
                    if (claimedBy != 0) {
                        drawRoundRect(
                            color = ownerColor.copy(alpha = 0.45f * neonGlowPulse),
                            size = size.copy(height = size.height * 2.2f),
                            topLeft = Offset(0f, -size.height * 0.6f)
                        )
                    }
                }
                .clip(RoundedCornerShape(50))
                .background(barColor)
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
    val barColor by animateColorAsState(
        targetValue = if (claimedBy != 0) {
            ownerColor
        } else if (showHover) {
            activePlayerColor.copy(alpha = 0.75f)
        } else {
            palette.idleEdge
        },
        label = "vEdgeColor"
    )

    val barThickness = if (claimedBy != 0 || showHover) 7.dp else 5.dp

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
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .padding(vertical = 1.dp)
                .width(barThickness)
                .drawBehind {
                    if (claimedBy != 0) {
                        drawRoundRect(
                            color = ownerColor.copy(alpha = 0.45f * neonGlowPulse),
                            size = size.copy(width = size.width * 2.2f),
                            topLeft = Offset(-size.width * 0.6f, 0f)
                        )
                    }
                }
                .clip(RoundedCornerShape(50))
                .background(barColor)
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
    val baseFill = if (owner != 0) playerFillColor(owner) else Color.Transparent
    val accentColor = if (owner != 0) playerAccentColor(owner) else Color.Transparent

    val charJumpScale = remember { Animatable(0.2f) }
    LaunchedEffect(owner) {
        if (owner != 0) {
            charJumpScale.snapTo(0.25f)
            charJumpScale.animateTo(
                targetValue = 1.82f,
                animationSpec = tween(durationMillis = 160, easing = FastOutSlowInEasing)
            )
            charJumpScale.animateTo(
                targetValue = 1.0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        } else {
            charJumpScale.snapTo(0.2f)
        }
    }

    val comboBoxScale = if (isNewlyCaptured || isInComboChain) {
        1f + (comboPulseFraction * 0.20f)
    } else {
        1f
    }

    Box(
        modifier = Modifier
            .size(size)
            .padding(1.5.dp)
            .graphicsLayer {
                scaleX = comboBoxScale
                scaleY = comboBoxScale
            }
            .drawBehind {
                if (owner != 0) {
                    drawRoundRect(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                accentColor.copy(
                                    alpha = (0.42f + comboPulseFraction * 0.45f).coerceIn(0f, 0.9f)
                                ),
                                baseFill
                            )
                        ),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx())
                    )
                }
            }
            .clip(RoundedCornerShape(6.dp))
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
                        size >= 42.dp -> 18.sp
                        size >= 26.dp -> 13.sp
                        else -> 10.sp
                    },
                    shadow = Shadow(
                        color = accentColor,
                        offset = Offset.Zero,
                        blurRadius = 18f
                    )
                ),
                color = if (comboPulseFraction > 0.3f) palette.primaryText else accentColor,
                modifier = Modifier.scale(charJumpScale.value)
            )
        }
    }
}

@Composable
private fun GameOverBanner(
    boardState: BoardState,
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
        val winnerName = if (boardState.isPlayerAi(winner)) "CPU" else "Player $winner"
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

    Card(
        modifier = modifier
            .border(2.dp, accentColor, RoundedCornerShape(18.dp))
            .testTag("game_over_banner"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = palette.surfaceElevated)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.EmojiEvents,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(28.dp)
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
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onPlayAgain,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = accentColor,
                    contentColor = Color.White
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

@Composable
fun SingleFileHtmlDialog(
    onDismiss: () -> Unit
) {
    val palette = LocalGamePalette.current
    val context = LocalContext.current
    val htmlContent = remember {
        runCatching {
            context.assets.open("index.html").bufferedReader().use { it.readText() }
        }.getOrDefault("<!-- index.html not found -->")
    }
    val copiedToastText = stringResource(R.string.copied_toast)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = palette.surface,
        titleContentColor = palette.primaryText,
        textContentColor = palette.primaryText,
        title = {
            Text(
                text = stringResource(R.string.html_dialog_title),
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Surface(
                color = palette.backgroundDeep,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
                    .border(1.dp, palette.border, RoundedCornerShape(12.dp))
            ) {
                val vScroll = rememberScrollState()
                val hScroll = rememberScrollState()
                Text(
                    text = htmlContent,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    ),
                    color = palette.primaryText,
                    modifier = Modifier
                        .verticalScroll(vScroll)
                        .horizontalScroll(hScroll)
                        .padding(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val clipboard =
                        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("index.html", htmlContent))
                    Toast.makeText(context, copiedToastText, Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = palette.p1Accent,
                    contentColor = Color.White
                ),
                modifier = Modifier.testTag("copy_html_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.ContentCopy,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = stringResource(R.string.copy_html_button))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("close_html_dialog_button")
            ) {
                Text(
                    text = stringResource(R.string.close_button),
                    color = palette.mutedText
                )
            }
        }
    )
}
