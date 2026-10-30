package com.example.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.hoverable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.BoardState
import com.example.model.BoxCoordinate
import com.example.model.EdgeCoordinate
import com.example.model.EdgeOrientation
import com.example.ui.theme.DotWhite
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.MutedText
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.Player1Cyan
import com.example.ui.theme.Player1CyanFill
import com.example.ui.theme.Player2Coral
import com.example.ui.theme.Player2CoralFill
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.SlateSurfaceElevated
import com.example.ui.theme.StreakAmber
import com.example.ui.theme.UnclaimedEdgeHover
import com.example.ui.theme.UnclaimedEdgeIdle

@Composable
fun GameScreen(
    boardState: BoardState,
    hoveredEdge: EdgeCoordinate?,
    onEdgeHover: (EdgeCoordinate?) -> Unit,
    onEdgeClick: (EdgeCoordinate) -> Unit,
    onRestartMatch: () -> Unit,
    onReturnToSetup: () -> Unit,
    onOpenHtmlViewer: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onReturnToSetup()
    }

    val scrollState = rememberScrollState()

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
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Action Bar
        TopGameToolbar(
            claimedBoxes = boardState.totalClaimedBoxes,
            onReturnToSetup = onReturnToSetup,
            onRestartMatch = onRestartMatch,
            onOpenHtmlViewer = onOpenHtmlViewer
        )

        // Scoreboard showing both players' chosen letters, current scores, and active turn border/highlight
        ScoreboardHeader(
            boardState = boardState,
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

        // 5x9 Dots (4x8 Boxes) Interactive Grid Board
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 760.dp)
                .border(1.dp, SlateBorder, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SlateSurface)
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 18.dp),
                contentAlignment = Alignment.Center
            ) {
                // Calculate responsive cell geometry so 9 dot columns + 8 box columns fit cleanly
                // or scroll horizontally if on an ultra-narrow compact viewport
                val availableWidth = maxWidth
                val minDotSize = 12.dp
                val computedBoxWidth = ((availableWidth - (minDotSize * 9)) / 8).coerceIn(30.dp, 62.dp)
                val dotSize = if (computedBoxWidth >= 44.dp) 14.dp else 12.dp

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
                        dotSize = dotSize,
                        boxSize = computedBoxWidth,
                        onEdgeHover = onEdgeHover,
                        onEdgeClick = onEdgeClick
                    )
                }
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
                    .width(120.dp)
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
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .border(1.dp, SlateBorder, RoundedCornerShape(22.dp))
            .testTag("scoreboard_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = SlateSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PlayerScorePill(
                    playerNumber = 1,
                    letter = boardState.player1Letter,
                    score = boardState.player1Score,
                    isCurrentTurn = !boardState.isGameOver && boardState.currentPlayer == 1,
                    accentColor = Player1Cyan,
                    accentFill = Player1CyanFill,
                    testTag = "player1_score_card",
                    modifier = Modifier.weight(1f)
                )

                PlayerScorePill(
                    playerNumber = 2,
                    letter = boardState.player2Letter,
                    score = boardState.player2Score,
                    isCurrentTurn = !boardState.isGameOver && boardState.currentPlayer == 2,
                    accentColor = Player2Coral,
                    accentFill = Player2CoralFill,
                    testTag = "player2_score_card",
                    modifier = Modifier.weight(1f)
                )
            }

            // Extra Turn Streak Indicator
            AnimatedVisibility(visible = boardState.hasExtraTurnStreak && !boardState.isGameOver) {
                val activeColor = if (boardState.currentPlayer == 1) Player1Cyan else Player2Coral
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(activeColor.copy(alpha = 0.14f))
                        .border(1.dp, activeColor.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
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
                        text = "${stringResource(R.string.extra_turn_badge)} — P${boardState.currentPlayer} (${
                            boardState.letterForPlayer(boardState.currentPlayer)
                        })",
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
    playerNumber: Int,
    letter: String,
    score: Int,
    isCurrentTurn: Boolean,
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
            .clip(RoundedCornerShape(16.dp))
            .background(containerColor)
            .border(
                width = if (isCurrentTurn) 2.5.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ObsidianBackground)
                    .border(1.5.dp, accentColor, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = letter,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = JetBrainsMonoFontFamily,
                        fontWeight = FontWeight.Bold
                    ),
                    color = accentColor
                )
            }

            Column {
                Text(
                    text = "PLAYER $playerNumber",
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
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (isCurrentTurn) {
                            stringResource(R.string.turn_indicator_active)
                        } else {
                            stringResource(R.string.turn_indicator_waiting)
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
                fontWeight = FontWeight.Bold
            ),
            color = DotWhite,
            modifier = Modifier.testTag("player${playerNumber}_score_value")
        )
    }
}

/**
 * Renders the mathematical 5x9 dot grid (9 interleaved rows x 17 interleaved columns):
 * - Even rows (0, 2, 4, 6, 8): 9 Dots alternating with 8 Horizontal Edges
 * - Odd rows (1, 3, 5, 7): 9 Vertical Edges alternating with 8 Playable Boxes
 */
@Composable
private fun DotsAndBoxesGrid(
    boardState: BoardState,
    hoveredEdge: EdgeCoordinate?,
    dotSize: Dp,
    boxSize: Dp,
    onEdgeHover: (EdgeCoordinate?) -> Unit,
    onEdgeClick: (EdgeCoordinate) -> Unit
) {
    val activePlayerColor = if (boardState.currentPlayer == 1) Player1Cyan else Player2Coral

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
                    // Dot Node
                    DotNode(size = dotSize)

                    // Horizontal Edge to the right of this dot (if not the 9th column)
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
                            dotSize = dotSize,
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
                            width = dotSize,
                            height = boxSize,
                            onHover = onEdgeHover,
                            onClick = { onEdgeClick(vEdgeCoord) }
                        )

                        // Playable Box to the right of this vertical line (if not the 9th column)
                        if (dotCol < BoardState.BOX_COLS) {
                            val boxOwner = boardState.boxes[dotRow][dotCol]
                            val boxCoord = BoxCoordinate(dotRow, dotCol)
                            val ownerLetter = when (boxOwner) {
                                1 -> boardState.player1Letter
                                2 -> boardState.player2Letter
                                else -> ""
                            }
                            PlayableBoxCell(
                                row = dotRow,
                                col = dotCol,
                                owner = boxOwner,
                                letter = ownerLetter,
                                isNewlyCaptured = boardState.lastCapturedBoxes.contains(boxCoord),
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
private fun DotNode(size: Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .shadow(elevation = 4.dp, shape = CircleShape, spotColor = DotWhite)
            .clip(CircleShape)
            .background(DotWhite)
    )
}

@Composable
private fun HorizontalEdgeCell(
    edge: EdgeCoordinate,
    claimedBy: Int,
    isLastClaimed: Boolean,
    isHoveredExternal: Boolean,
    activePlayerColor: Color,
    width: Dp,
    dotSize: Dp,
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
        targetValue = when (claimedBy) {
            1 -> Player1Cyan
            2 -> Player2Coral
            else -> if (showHover) activePlayerColor.copy(alpha = 0.65f) else UnclaimedEdgeIdle
        },
        label = "hEdgeColor"
    )

    val barThickness = if (claimedBy != 0 || showHover) 8.dp else 6.dp

    Box(
        modifier = Modifier
            .width(width)
            .height((dotSize * 2).coerceAtLeast(28.dp))
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
                .padding(horizontal = 2.dp)
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
    width: Dp,
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
        targetValue = when (claimedBy) {
            1 -> Player1Cyan
            2 -> Player2Coral
            else -> if (showHover) activePlayerColor.copy(alpha = 0.65f) else UnclaimedEdgeIdle
        },
        label = "vEdgeColor"
    )

    val barThickness = if (claimedBy != 0 || showHover) 8.dp else 6.dp

    Box(
        modifier = Modifier
            .width((width * 2).coerceAtLeast(28.dp))
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
                .padding(vertical = 2.dp)
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
    size: Dp
) {
    val bgColor by animateColorAsState(
        targetValue = when (owner) {
            1 -> Player1CyanFill
            2 -> Player2CoralFill
            else -> Color.Transparent
        },
        label = "boxFillColor"
    )

    val letterScale by animateFloatAsState(
        targetValue = if (owner != 0) 1f else 0.4f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "boxLetterScale"
    )

    val textColor = when (owner) {
        1 -> Player1Cyan
        2 -> Player2Coral
        else -> Color.Transparent
    }

    Box(
        modifier = Modifier
            .size(size)
            .padding(3.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .then(
                if (isNewlyCaptured && owner != 0) {
                    Modifier.border(
                        width = 1.5.dp,
                        color = textColor.copy(alpha = 0.85f),
                        shape = RoundedCornerShape(8.dp)
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
                    fontSize = if (size >= 44.dp) 20.sp else 15.sp
                ),
                color = textColor,
                modifier = Modifier.scale(letterScale)
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
    val accentColor = when (winner) {
        1 -> Player1Cyan
        2 -> Player2Coral
        else -> StreakAmber
    }

    val titleText = when (winner) {
        1 -> stringResource(R.string.winner_banner_player, 1, boardState.player1Letter)
        2 -> stringResource(R.string.winner_banner_player, 2, boardState.player2Letter)
        else -> stringResource(R.string.winner_banner_tie)
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
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.EmojiEvents,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(32.dp)
                )
                Column {
                    Text(
                        text = titleText,
                        style = MaterialTheme.typography.titleLarge,
                        color = DotWhite
                    )
                    Text(
                        text = "${boardState.player1Letter}: ${boardState.player1Score}  •  ${boardState.player2Letter}: ${boardState.player2Score}",
                        style = MaterialTheme.typography.labelLarge,
                        color = accentColor
                    )
                }
            }

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
