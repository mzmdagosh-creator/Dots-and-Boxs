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
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
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
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.BoardState
import com.example.model.BoxCoordinate
import com.example.model.EdgeCoordinate
import com.example.model.EdgeOrientation
import com.example.model.PlayerConfig
import com.example.ui.theme.DotWhite
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.MutedText
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.Player1Cyan
import com.example.ui.theme.Player2Coral
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.SlateSurfaceElevated
import com.example.ui.theme.StreakAmber
import com.example.ui.theme.UnclaimedEdgeIdle
import com.example.ui.theme.playerAccentColor
import com.example.ui.theme.playerFillColor
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun GameScreen(
    boardState: BoardState,
    hoveredEdge: EdgeCoordinate?,
    boardZoom: Float,
    onEdgeHover: (EdgeCoordinate?) -> Unit,
    onEdgeClick: (EdgeCoordinate) -> Unit,
    onAdjustZoom: (Float) -> Unit,
    onSetZoom: (Float) -> Unit,
    onRestartMatch: () -> Unit,
    onReturnToSetup: () -> Unit,
    onOpenHtmlViewer: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onReturnToSetup()
    }

    val scrollState = rememberScrollState()

    // Small Zoom + Pulse animations triggered on each multi-square round (combo)
    val boardComboZoomAnim = remember { Animatable(1f) }
    val comboPulseWaveAnim = remember { Animatable(0f) }

    LaunchedEffect(boardState.comboTriggerId) {
        if (boardState.comboTriggerId > 0 && boardState.currentComboCount >= 2) {
            // Calculate a responsive small zoom punch based on combo size (e.g., 1.045f up to 1.085f)
            val targetZoomPunch =
                1.038f + (boardState.currentComboCount.coerceAtMost(6) * 0.008f)

            launch {
                boardComboZoomAnim.snapTo(1f)
                boardComboZoomAnim.animateTo(
                    targetValue = targetZoomPunch,
                    animationSpec = tween(durationMillis = 135, easing = FastOutSlowInEasing)
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
                    animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing)
                )
            }
        } else {
            boardComboZoomAnim.snapTo(1f)
            comboPulseWaveAnim.snapTo(0f)
        }
    }

    val activeComboColor = playerAccentColor(boardState.currentPlayer)
    val pulseGlowAlpha = comboPulseWaveAnim.value

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF111A2E),
                        ObsidianBackground
                    )
                )
            )
            .verticalScroll(scrollState)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Top Action Bar
        TopGameToolbar(
            claimedBoxes = boardState.totalClaimedBoxes,
            onReturnToSetup = onReturnToSetup,
            onRestartMatch = onRestartMatch,
            onOpenHtmlViewer = onOpenHtmlViewer
        )

        // Scoreboard showing 1-4 players' chosen letters, current scores, active turn highlight, and combo pulse banner
        ScoreboardHeader(
            boardState = boardState,
            comboZoomScale = boardComboZoomAnim.value,
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 760.dp)
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
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 760.dp)
            )
        }

        // 5x9 Dots (4x8 Boxes) Interactive Grid Board with Multi-Square Combo Pulse & Small Zoom
        val boardBorderColor = if (pulseGlowAlpha > 0.02f) {
            activeComboColor.copy(alpha = (0.45f + pulseGlowAlpha * 0.55f).coerceIn(0f, 1f))
        } else {
            SlateBorder
        }
        val boardBorderWidth = if (pulseGlowAlpha > 0.02f) {
            (1.dp + (pulseGlowAlpha * 2.5f).dp)
        } else {
            1.dp
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 760.dp)
                .graphicsLayer {
                    scaleX = boardComboZoomAnim.value
                    scaleY = boardComboZoomAnim.value
                }
                .drawBehind {
                    if (pulseGlowAlpha > 0.01f) {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    activeComboColor.copy(alpha = 0.28f * pulseGlowAlpha),
                                    Color.Transparent
                                )
                            ),
                            radius = size.maxDimension * (0.55f + (1f - pulseGlowAlpha) * 0.25f)
                        )
                    }
                }
                .border(boardBorderWidth, boardBorderColor, RoundedCornerShape(22.dp)),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = SlateSurface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Board Zoom Bar + Live Combo Indicator Pill
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
                        .fillMaxWidth()
                        .pointerInput(Unit) {
                            detectTransformGestures { _, _, zoomChange, _ ->
                                if (zoomChange != 1f) {
                                    onSetZoom(boardZoom * zoomChange)
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    val safeAvailableWidth = (maxWidth - 4.dp).coerceAtLeast(240.dp)
                    val baseDotSlotSize = (safeAvailableWidth * 0.036f).coerceIn(10.dp, 16.dp)
                    val baseBoxSize =
                        ((safeAvailableWidth - (baseDotSlotSize * 9)) / 8f).coerceIn(18.dp, 64.dp)

                    val scaledDotSlotSize = (baseDotSlotSize * boardZoom).coerceIn(8.dp, 22.dp)
                    val scaledBoxSize = (baseBoxSize * boardZoom).coerceIn(14.dp, 76.dp)

                    val horizontalScroll = rememberScrollState()
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(horizontalScroll),
                        contentAlignment = Alignment.Center
                    ) {
                        DotsAndBoxesGrid(
                            boardState = boardState,
                            hoveredEdge = hoveredEdge,
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
                text = "5×9 GRID",
                style = MaterialTheme.typography.labelSmall,
                color = MutedText
            )

            AnimatedVisibility(visible = comboCount >= 2) {
                Box(
                    modifier = Modifier
                        .scale(1f + comboPulseFraction * 0.14f)
                        .clip(RoundedCornerShape(50))
                        .background(comboAccentColor.copy(alpha = 0.22f))
                        .border(1.dp, comboAccentColor, RoundedCornerShape(50))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                        .testTag("combo_pulse_chip")
                ) {
                    Text(
                        text = "${comboCount}× COMBO!",
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
                    .size(34.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SlateSurfaceElevated)
                    .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                    .clickable { onAdjustZoom(-0.1f) }
                    .testTag("zoom_out_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.ZoomOut,
                    contentDescription = stringResource(R.string.zoom_out_desc),
                    tint = DotWhite,
                    modifier = Modifier.size(18.dp)
                )
            }

            Box(
                modifier = Modifier
                    .height(34.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SlateSurfaceElevated)
                    .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                    .clickable { onResetZoom() }
                    .padding(horizontal = 10.dp)
                    .testTag("zoom_reset_button"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${(boardZoom * 100).roundToInt()}% FIT",
                    style = MaterialTheme.typography.labelSmall,
                    color = Player1Cyan
                )
            }

            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SlateSurfaceElevated)
                    .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                    .clickable { onAdjustZoom(0.1f) }
                    .testTag("zoom_in_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.ZoomIn,
                    contentDescription = stringResource(R.string.zoom_in_desc),
                    tint = DotWhite,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun TopGameToolbar(
    claimedBoxes: Int,
    onReturnToSetup: () -> Unit,
    onRestartMatch: () -> Unit,
    onOpenHtmlViewer: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 760.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        OutlinedButton(
            onClick = onReturnToSetup,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = DotWhite),
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
                color = MutedText
            )
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { claimedBoxes / 32f },
                modifier = Modifier
                    .width(104.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(50)),
                color = Player1Cyan,
                trackColor = SlateSurfaceElevated
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onOpenHtmlViewer,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SlateSurfaceElevated)
                    .border(1.dp, SlateBorder, RoundedCornerShape(12.dp))
                    .testTag("game_html_viewer_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.Code,
                    contentDescription = stringResource(R.string.view_html_button),
                    tint = Player2Coral
                )
            }

            IconButton(
                onClick = onRestartMatch,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SlateSurfaceElevated)
                    .border(1.dp, SlateBorder, RoundedCornerShape(12.dp))
                    .testTag("restart_match_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = stringResource(R.string.reset_board_button),
                    tint = Player1Cyan
                )
            }
        }
    }
}

@Composable
private fun ScoreboardHeader(
    boardState: BoardState,
    comboZoomScale: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .border(1.dp, SlateBorder, RoundedCornerShape(20.dp))
            .testTag("scoreboard_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SlateSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val players = boardState.players
            val chunkedPlayers = players.chunked(2)
            for (rowPlayers in chunkedPlayers) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (player in rowPlayers) {
                        val isTurn = !boardState.isGameOver &&
                            boardState.currentPlayer == player.playerNumber
                        PlayerScorePill(
                            player = player,
                            score = boardState.scoreForPlayer(player.playerNumber),
                            isCurrentTurn = isTurn,
                            comboScale = if (isTurn && boardState.isMultiSquareCombo) {
                                comboZoomScale
                            } else {
                                1f
                            },
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

            // Extra Turn / Multi-Square Combo Streak Banner
            AnimatedVisibility(visible = boardState.hasExtraTurnStreak && !boardState.isGameOver) {
                val activeColor = playerAccentColor(boardState.currentPlayer)
                val activeLabel = if (boardState.isPlayerAi(boardState.currentPlayer)) {
                    "AI (${boardState.letterForPlayer(boardState.currentPlayer)})"
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
                        .scale(if (boardState.isMultiSquareCombo) comboZoomScale else 1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (boardState.isMultiSquareCombo) {
                                activeColor.copy(alpha = 0.24f)
                            } else {
                                activeColor.copy(alpha = 0.14f)
                            }
                        )
                        .border(
                            width = if (boardState.isMultiSquareCombo) 1.5.dp else 1.dp,
                            color = activeColor,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("combo_streak_banner"),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Bolt,
                        contentDescription = null,
                        tint = StreakAmber,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$badgeText — $activeLabel",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = DotWhite
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
    comboScale: Float,
    accentColor: Color,
    accentFill: Color,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val borderColor by animateColorAsState(
        targetValue = if (isCurrentTurn) accentColor else SlateBorder.copy(alpha = 0.5f),
        label = "scoreBorderColor"
    )
    val containerColor by animateColorAsState(
        targetValue = if (isCurrentTurn) accentFill else SlateSurfaceElevated,
        label = "scoreContainerColor"
    )

    Row(
        modifier = modifier
            .graphicsLayer {
                scaleX = comboScale
                scaleY = comboScale
            }
            .clip(RoundedCornerShape(14.dp))
            .background(containerColor)
            .border(
                width = if (isCurrentTurn) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(14.dp)
            )
            .padding(horizontal = 10.dp, vertical = 8.dp)
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
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(ObsidianBackground)
                    .border(1.5.dp, accentColor, RoundedCornerShape(10.dp)),
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
                    text = if (player.isAi) "AI BOT" else "PLAYER ${player.playerNumber}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedText
                )
                Spacer(modifier = Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (isCurrentTurn) accentColor else ObsidianBackground.copy(alpha = 0.6f)
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
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (isCurrentTurn) ObsidianBackground else MutedText
                    )
                }
            }
        }

        Text(
            text = score.toString(),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontFamily = JetBrainsMonoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            ),
            color = DotWhite,
            modifier = Modifier.testTag("player${player.playerNumber}_score_value")
        )
    }
}

/**
 * Renders the mathematical 5x9 dot grid (9 interleaved rows x 17 interleaved columns).
 * During a multi-square combo round (`boardState.isMultiSquareCombo`), all boxes in
 * `boardState.comboChainBoxes` pulse in unison with a glowing halo and scale pop.
 */
@Composable
private fun DotsAndBoxesGrid(
    boardState: BoardState,
    hoveredEdge: EdgeCoordinate?,
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
            // 1. Even Grid Row: Dots (9) interleaved with Horizontal Lines (8)
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
                            activePlayerColor = activePlayerColor,
                            width = boxSize,
                            slotHeight = (dotSlotSize * 1.8f).coerceAtLeast(20.dp),
                            onHover = onEdgeHover,
                            onClick = { onEdgeClick(edgeCoord) }
                        )
                    }
                }
            }

            // 2. Odd Grid Row: Vertical Lines (9) interleaved with Playable 1x1 Boxes (8)
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
                            val isInComboChain = boardState.isMultiSquareCombo &&
                                boardState.comboChainBoxes.contains(boxCoord)

                            PlayableBoxCell(
                                row = dotRow,
                                col = dotCol,
                                owner = boxOwner,
                                letter = ownerLetter,
                                isNewlyCaptured = boardState.lastCapturedBoxes.contains(boxCoord),
                                isInComboChain = isInComboChain,
                                comboPulseFraction = if (isInComboChain) comboPulseFraction else 0f,
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
    val visualDotSize = (slotSize * 0.78f).coerceAtLeast(7.dp)
    Box(
        modifier = Modifier.size(slotSize),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(visualDotSize)
                .shadow(elevation = 3.dp, shape = CircleShape, spotColor = DotWhite)
                .clip(CircleShape)
                .background(DotWhite)
        )
    }
}

@Composable
private fun HorizontalEdgeCell(
    edge: EdgeCoordinate,
    claimedBy: Int,
    isLastClaimed: Boolean,
    isHoveredExternal: Boolean,
    activePlayerColor: Color,
    width: Dp,
    slotHeight: Dp,
    onHover: (EdgeCoordinate?) -> Unit,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()

    val showHover = claimedBy == 0 && (isHovered || isPressed || isHoveredExternal)
    LaunchedEffect(isHovered, isPressed) {
        if (isHovered || isPressed) onHover(edge)
    }

    val barColor by animateColorAsState(
        targetValue = if (claimedBy != 0) {
            playerAccentColor(claimedBy)
        } else if (showHover) {
            activePlayerColor.copy(alpha = 0.7f)
        } else {
            UnclaimedEdgeIdle
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
                .clip(RoundedCornerShape(50))
                .background(barColor)
                .then(
                    if (isLastClaimed) {
                        Modifier.border(1.dp, DotWhite, RoundedCornerShape(50))
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
    activePlayerColor: Color,
    slotWidth: Dp,
    height: Dp,
    onHover: (EdgeCoordinate?) -> Unit,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()

    val showHover = claimedBy == 0 && (isHovered || isPressed || isHoveredExternal)
    LaunchedEffect(isHovered, isPressed) {
        if (isHovered || isPressed) onHover(edge)
    }

    val barColor by animateColorAsState(
        targetValue = if (claimedBy != 0) {
            playerAccentColor(claimedBy)
        } else if (showHover) {
            activePlayerColor.copy(alpha = 0.7f)
        } else {
            UnclaimedEdgeIdle
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
                .clip(RoundedCornerShape(50))
                .background(barColor)
                .then(
                    if (isLastClaimed) {
                        Modifier.border(1.dp, DotWhite, RoundedCornerShape(50))
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
    val baseFill = if (owner != 0) playerFillColor(owner) else Color.Transparent
    val accentColor = if (owner != 0) playerAccentColor(owner) else Color.Transparent

    val bgColor by animateColorAsState(
        targetValue = if (isInComboChain && comboPulseFraction > 0.05f) {
            accentColor.copy(alpha = (0.36f + comboPulseFraction * 0.32f).coerceIn(0f, 0.85f))
        } else {
            baseFill
        },
        label = "boxFillColor"
    )

    val baseLetterScale by animateFloatAsState(
        targetValue = if (owner != 0) 1f else 0.4f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "boxLetterScale"
    )

    // Extra pulse zoom on every box participating in the current multi-square combo
    val comboBoxScale = if (isInComboChain) {
        1f + (comboPulseFraction * 0.16f)
    } else {
        1f
    }

    Box(
        modifier = Modifier
            .size(size)
            .padding(2.dp)
            .graphicsLayer {
                scaleX = comboBoxScale
                scaleY = comboBoxScale
            }
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .then(
                if ((isNewlyCaptured || isInComboChain) && owner != 0) {
                    Modifier.border(
                        width = if (isInComboChain) 2.dp else 1.5.dp,
                        color = if (isInComboChain && comboPulseFraction > 0.1f) {
                            DotWhite
                        } else {
                            accentColor.copy(alpha = 0.9f)
                        },
                        shape = RoundedCornerShape(6.dp)
                    )
                } else {
                    Modifier
                }
            )
            .testTag("box_${row}_${col}"),
        contentAlignment = Alignment.Center
    ) {
        if (owner != 0) {
            Text(
                text = letter,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = when {
                        size >= 44.dp -> 19.sp
                        size >= 28.dp -> 14.sp
                        else -> 11.sp
                    }
                ),
                color = if (isInComboChain && comboPulseFraction > 0.25f) DotWhite else accentColor,
                modifier = Modifier.scale(baseLetterScale)
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
    val winner = boardState.winner
    val accentColor = if (winner != null && winner > 0) {
        playerAccentColor(winner)
    } else {
        StreakAmber
    }

    val titleText = if (winner != null && winner > 0) {
        val winnerName = if (boardState.isPlayerAi(winner)) "AI Bot" else "Player $winner"
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
            .border(2.dp, accentColor, RoundedCornerShape(20.dp))
            .testTag("game_over_banner"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SlateSurfaceElevated)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
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
                    modifier = Modifier.size(30.dp)
                )
                Column {
                    Text(
                        text = titleText,
                        style = MaterialTheme.typography.titleMedium,
                        color = DotWhite
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
                    contentColor = ObsidianBackground
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
    val context = LocalContext.current
    val htmlContent = remember {
        runCatching {
            context.assets.open("index.html").bufferedReader().use { it.readText() }
        }.getOrDefault("<!-- index.html not found -->")
    }
    val copiedToastText = stringResource(R.string.copied_toast)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SlateSurface,
        titleContentColor = DotWhite,
        textContentColor = DotWhite,
        title = {
            Text(
                text = stringResource(R.string.html_dialog_title),
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Surface(
                color = ObsidianBackground,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
                    .border(1.dp, SlateBorder, RoundedCornerShape(12.dp))
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
                    color = DotWhite,
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
                    containerColor = Player1Cyan,
                    contentColor = ObsidianBackground
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
                    color = MutedText
                )
            }
        }
    )
}
