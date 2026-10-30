package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloat
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TimerOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.NeonArcadeDashboard
import com.example.R
import com.example.model.CampaignLevel
import com.example.model.CpuDifficulty
import com.example.model.GameMode
import com.example.model.GridSize
import com.example.model.TimerMode
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalGamePalette
import com.example.viewmodel.MainMenuSection
import com.example.viewmodel.MatchHistoryItem
import com.example.viewmodel.PlayerSessionStats

private val NeonDarkBackground = Color(0xFF121212)
private val NeonCardSurface = Color(0xFF1A1E29)
private val NeonElevatedSurface = Color(0xFF222838)
private val NeonCyan = Color(0xFF00E5FF)
private val NeonMagenta = Color(0xFFFF007F)
private val NeonLime = Color(0xFF00E676)
private val NeonAmber = Color(0xFFFFEA00)
private val NeonTextPrimary = Color(0xFFF8FAFC)
private val NeonTextMuted = Color(0xFF94A3B8)
private val NeonBorder = Color(0xFF2D3748)

@Composable
fun SetupScreen(
    soundEnabled: Boolean = true,
    hapticsEnabled: Boolean = true,
    totalBoxesCaptured: Int = 0,
    mainMenuSection: MainMenuSection = MainMenuSection.CAMPAIGN,
    unlockedCampaignLevel: Int = 1,
    selectedCampaignLevel: CampaignLevel = CampaignLevel.LEVEL_1,
    selectedGameMode: GameMode = GameMode.CPU,
    selectedGridSize: GridSize = GridSize.SIZE_4X4,
    selectedCpuDifficulty: CpuDifficulty = CpuDifficulty.MEDIUM,
    selectedTimerMode: TimerMode = TimerMode.RELAXED,
    selectedPlayerCount: Int = 1,
    playerInputs: List<String> = listOf("A", "C", "D", "E"),
    sessionStats: Map<Int, PlayerSessionStats> = emptyMap(),
    matchHistory: List<MatchHistoryItem> = emptyList(),
    errorResId: Int? = null,
    onToggleSound: () -> Unit = {},
    onToggleHaptics: () -> Unit = {},
    onClearData: () -> Unit = {},
    onOpenCredits: () -> Unit = {},
    onSelectMainMenuSection: (MainMenuSection) -> Unit = {},
    onSelectCampaignLevel: (CampaignLevel) -> Unit = {},
    onStartCampaignLevel: (CampaignLevel) -> Unit = {},
    onResumeTournament: () -> Unit = {},
    onSelectGameMode: (GameMode) -> Unit = {},
    onSelectGridSize: (GridSize) -> Unit = {},
    onSelectCpuDifficulty: (CpuDifficulty) -> Unit = {},
    onSelectTimerMode: (TimerMode) -> Unit = {},
    onSelectPlayerCount: (Int) -> Unit = {},
    onPlayerInputChange: (Int, String) -> Unit = { _, _ -> },
    onStartGame: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    NeonArcadeDashboard(
        totalBoxesCaptured = totalBoxesCaptured,
        unlockedCampaignLevel = unlockedCampaignLevel,
        selectedCampaignLevel = selectedCampaignLevel,
        mainMenuSection = mainMenuSection,
        selectedGameMode = selectedGameMode,
        selectedGridSize = selectedGridSize,
        selectedCpuDifficulty = selectedCpuDifficulty,
        selectedTimerMode = selectedTimerMode,
        selectedPlayerCount = selectedPlayerCount,
        playerInputs = playerInputs,
        sessionStats = sessionStats,
        matchHistory = matchHistory,
        errorResId = errorResId,
        onResumeTournament = onResumeTournament,
        onSelectMainMenuSection = onSelectMainMenuSection,
        onSelectCampaignLevel = onSelectCampaignLevel,
        onStartCampaignLevel = onStartCampaignLevel,
        onSelectGameMode = onSelectGameMode,
        onSelectGridSize = onSelectGridSize,
        onSelectCpuDifficulty = onSelectCpuDifficulty,
        onSelectTimerMode = onSelectTimerMode,
        onSelectPlayerCount = onSelectPlayerCount,
        onPlayerInputChange = onPlayerInputChange,
        onStartCustomGame = onStartGame,
        modifier = modifier
    )
}

/**
 * Tournament Campaign Mode list displaying the 6 sequential levels:
 * - Level 1 (3x3, Easy), Level 2 (4x4, Easy), Level 3 (4x4, Medium),
 *   Level 4 (5x5, Medium), Level 5 (5x5, Hard), Level 6 (6x6, Hard).
 */
@Composable
fun TournamentList(
    unlockedCampaignLevel: Int = 1,
    selectedCampaignLevel: CampaignLevel = CampaignLevel.LEVEL_1,
    onSelectCampaignLevel: (CampaignLevel) -> Unit = {},
    onStartCampaignLevel: (CampaignLevel) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.5.dp, NeonCyan.copy(alpha = 0.6f), RoundedCornerShape(22.dp))
            .testTag("tournament_campaign_section"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = NeonCardSurface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "TOURNAMENT CAMPAIGN (6 STAGES)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = JetBrainsMonoFontFamily,
                        fontWeight = FontWeight.Bold
                    ),
                    color = NeonCyan
                )
                Text(
                    text = "Unlocked: $unlockedCampaignLevel / ${CampaignLevel.entries.size}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = JetBrainsMonoFontFamily,
                        fontWeight = FontWeight.Bold
                    ),
                    color = NeonAmber,
                    modifier = Modifier.testTag("campaign_unlocked_progress_text")
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CampaignLevel.entries.forEach { level ->
                    val isUnlocked = level.levelNumber <= unlockedCampaignLevel
                    val isCleared = level.levelNumber < unlockedCampaignLevel
                    val isSelected = selectedCampaignLevel == level && isUnlocked
                    val diffColor = when (level.difficulty) {
                        CpuDifficulty.EASY -> NeonLime
                        CpuDifficulty.MEDIUM -> NeonAmber
                        CpuDifficulty.HARD -> NeonMagenta
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(if (isUnlocked) 1f else 0.45f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isSelected) NeonCyan.copy(alpha = 0.14f) else NeonElevatedSurface
                            )
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = when {
                                    isSelected -> NeonCyan
                                    isCleared -> NeonLime.copy(alpha = 0.7f)
                                    else -> NeonBorder
                                },
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable(enabled = isUnlocked) {
                                onSelectCampaignLevel(level)
                            }
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                            .testTag("campaign_level_${level.levelNumber}_card"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isUnlocked) NeonDarkBackground else NeonBorder.copy(alpha = 0.4f)
                                    )
                                    .border(
                                        1.5.dp,
                                        if (isUnlocked) NeonCyan else NeonBorder,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (!isUnlocked) {
                                    Icon(
                                        imageVector = Icons.Filled.Lock,
                                        contentDescription = "LOCKED",
                                        tint = NeonTextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                } else if (isCleared) {
                                    Icon(
                                        imageVector = Icons.Filled.CheckCircle,
                                        contentDescription = "Cleared",
                                        tint = NeonLime,
                                        modifier = Modifier.size(18.dp)
                                    )
                                } else {
                                    Text(
                                        text = level.levelNumber.toString(),
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontFamily = JetBrainsMonoFontFamily,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = NeonCyan
                                    )
                                }
                            }

                            Column {
                                Text(
                                    text = level.title,
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = if (isUnlocked) NeonTextPrimary else NeonTextMuted
                                )
                                Text(
                                    text = "${level.gridSize.label} Grid • ${level.difficulty.label} AI",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = JetBrainsMonoFontFamily
                                    ),
                                    color = if (isUnlocked) diffColor else NeonTextMuted
                                )
                            }
                        }

                        if (isUnlocked) {
                            Button(
                                onClick = { onStartCampaignLevel(level) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = NeonCyan,
                                    contentColor = Color.Black
                                ),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                                modifier = Modifier
                                    .height(36.dp)
                                    .testTag("campaign_level_${level.levelNumber}")
                            ) {
                                Text(
                                    text = "Play",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(NeonDarkBackground)
                                    .border(1.dp, NeonBorder, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                                    .testTag("campaign_level_${level.levelNumber}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "LOCKED",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = NeonTextMuted
                                )
                            }
                        }
                    }
                }
            }

            val activeTargetLevel = if (selectedCampaignLevel.levelNumber <= unlockedCampaignLevel) {
                selectedCampaignLevel
            } else {
                CampaignLevel.fromNumber(unlockedCampaignLevel)
            }

            Button(
                onClick = { onStartCampaignLevel(activeTargetLevel) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("start_campaign_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonCyan,
                    contentColor = Color.Black
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.start_campaign_button, activeTargetLevel.levelNumber),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = JetBrainsMonoFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

/**
 * Custom Arcade Mode & Blitz Timer configuration panel:
 * - Grid size selection (3x3 to 6x6)
 * - Difficulty selection (Easy, Medium, Hard)
 * - Blitz Mode toggle (5-second turn countdown)
 */
@Composable
fun CustomArcadeConfigPanel(
    selectedGridSize: GridSize,
    selectedGameMode: GameMode,
    selectedCpuDifficulty: CpuDifficulty,
    selectedTimerMode: TimerMode,
    selectedPlayerCount: Int,
    playerInputs: List<String>,
    errorResId: Int?,
    onSelectGridSize: (GridSize) -> Unit,
    onSelectGameMode: (GameMode) -> Unit,
    onSelectCpuDifficulty: (CpuDifficulty) -> Unit,
    onSelectTimerMode: (TimerMode) -> Unit,
    onSelectPlayerCount: (Int) -> Unit,
    onPlayerInputChange: (Int, String) -> Unit,
    onStartGame: () -> Unit
) {
    val isCpuMode = selectedGameMode == GameMode.CPU
    val isBlitzEnabled = selectedTimerMode == TimerMode.BLITZ
    val palette = LocalGamePalette.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, NeonMagenta.copy(alpha = 0.65f), RoundedCornerShape(22.dp))
            .testTag("quick_match_section"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = NeonCardSurface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "CUSTOM ARCADE MODE",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = FontWeight.Bold
                ),
                color = NeonMagenta
            )

            // Grid Size Selector (3x3, 4x4, 5x5, 6x6)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = stringResource(R.string.grid_size_label),
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonTextMuted
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GridSize.entries.forEach { sizeOption ->
                        val isSelected = selectedGridSize == sizeOption
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) NeonCyan else NeonElevatedSurface)
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) NeonCyan else NeonBorder,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { onSelectGridSize(sizeOption) }
                                .testTag("setup_grid_${sizeOption.boxRows}x${sizeOption.boxCols}_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = sizeOption.label,
                                style = MaterialTheme.typography.labelLarge,
                                color = if (isSelected) Color.Black else NeonTextPrimary
                            )
                        }
                    }
                }
            }

            // Blitz Mode Toggle (5-Second Turn Timer)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(NeonElevatedSurface)
                        .border(
                            width = 1.dp,
                            color = if (isBlitzEnabled) NeonMagenta else NeonBorder,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = if (isBlitzEnabled) Icons.Filled.Timer else Icons.Filled.TimerOff,
                            contentDescription = "Blitz Mode",
                            tint = if (isBlitzEnabled) NeonMagenta else NeonTextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                        Column {
                            Text(
                                text = "Blitz Mode (5s)",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = NeonTextPrimary
                            )
                            Text(
                                text = "5 seconds per turn — AI moves instantly on timeout",
                                style = MaterialTheme.typography.labelSmall,
                                color = NeonTextMuted
                            )
                        }
                    }
                    Switch(
                        checked = isBlitzEnabled,
                        onCheckedChange = { checked ->
                            onSelectTimerMode(if (checked) TimerMode.BLITZ else TimerMode.RELAXED)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = NeonMagenta
                        ),
                        modifier = Modifier.testTag("blitz_mode_switch")
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ArcadeModePill(
                        title = stringResource(R.string.timer_relaxed_label),
                        icon = Icons.Filled.TimerOff,
                        isSelected = selectedTimerMode == TimerMode.RELAXED,
                        accentColor = NeonCyan,
                        testTag = "timer_relaxed_button",
                        onClick = { onSelectTimerMode(TimerMode.RELAXED) },
                        modifier = Modifier.weight(1f)
                    )
                    ArcadeModePill(
                        title = stringResource(R.string.timer_blitz_label),
                        icon = Icons.Filled.Timer,
                        isSelected = selectedTimerMode == TimerMode.BLITZ,
                        accentColor = NeonMagenta,
                        testTag = "timer_blitz_button",
                        onClick = { onSelectTimerMode(TimerMode.BLITZ) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Difficulty Selection (Easy, Medium, Hard)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = stringResource(R.string.cpu_difficulty_label),
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonTextMuted
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CpuDifficulty.entries.forEach { diff ->
                        val isSelected = selectedCpuDifficulty == diff
                        val diffAccent = when (diff) {
                            CpuDifficulty.EASY -> NeonLime
                            CpuDifficulty.MEDIUM -> NeonAmber
                            CpuDifficulty.HARD -> NeonMagenta
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) diffAccent else NeonElevatedSurface)
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) diffAccent else NeonBorder,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { onSelectCpuDifficulty(diff) }
                                .testTag("cpu_diff_${diff.name.lowercase()}_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = diff.label,
                                style = MaterialTheme.typography.labelLarge,
                                color = if (isSelected) Color.Black else NeonTextPrimary
                            )
                        }
                    }
                }
            }

            // Opponent Mode: Player vs CPU or Player vs Player
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ArcadeModePill(
                    title = stringResource(R.string.mode_cpu_button),
                    icon = Icons.Filled.SmartToy,
                    isSelected = isCpuMode,
                    accentColor = NeonMagenta,
                    testTag = "mode_cpu_button",
                    onClick = { onSelectGameMode(GameMode.CPU) },
                    modifier = Modifier.weight(1f)
                )
                ArcadeModePill(
                    title = stringResource(R.string.mode_pvp_button),
                    icon = Icons.Filled.Group,
                    isSelected = !isCpuMode,
                    accentColor = NeonCyan,
                    testTag = "mode_pvp_button",
                    onClick = { onSelectGameMode(GameMode.PVP) },
                    modifier = Modifier.weight(1f)
                )
            }

            // Player Initials Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PlayerInitialInputCard(
                    label = stringResource(R.string.player_one_label),
                    value = playerInputs.getOrElse(0) { "A" },
                    isLocked = false,
                    accentColor = NeonCyan,
                    accentFill = palette.p1Fill,
                    testTag = "player1_letter_input",
                    onValueChange = { onPlayerInputChange(0, it) },
                    modifier = Modifier.weight(1f)
                )
                PlayerInitialInputCard(
                    label = if (isCpuMode) {
                        stringResource(R.string.player_two_ai_label)
                    } else {
                        stringResource(R.string.player_two_label)
                    },
                    value = playerInputs.getOrElse(1) { if (isCpuMode) "C" else "B" },
                    isLocked = isCpuMode,
                    accentColor = NeonMagenta,
                    accentFill = palette.p2Fill,
                    testTag = "player2_letter_input",
                    onValueChange = { onPlayerInputChange(1, it) },
                    modifier = Modifier.weight(1f)
                )
            }

            AnimatedVisibility(visible = errorResId != null) {
                if (errorResId != null) {
                    Text(
                        text = stringResource(id = errorResId),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("setup_error_text")
                    )
                }
            }

            Button(
                onClick = onStartGame,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("start_game_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonMagenta,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.start_game_button),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = JetBrainsMonoFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
fun DashboardSessionHistoryCard(
    isCpuMode: Boolean,
    selectedPlayerCount: Int,
    sessionStats: Map<Int, PlayerSessionStats>,
    matchHistory: List<MatchHistoryItem>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, NeonBorder, RoundedCornerShape(18.dp))
            .testTag("dashboard_stats_history_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = NeonCardSurface)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val activeCount = if (isCpuMode) 2 else selectedPlayerCount
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.History,
                        contentDescription = null,
                        tint = NeonAmber,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = stringResource(R.string.session_stats_title).uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.Bold
                        ),
                        color = NeonTextMuted
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (pNum in 1..activeCount) {
                        val stat = sessionStats[pNum] ?: PlayerSessionStats()
                        val label = if (isCpuMode && pNum == 2) "CPU" else "P$pNum"
                        val accent = when (pNum) {
                            1 -> NeonCyan
                            2 -> NeonMagenta
                            3 -> NeonLime
                            else -> NeonAmber
                        }
                        Text(
                            text = "$label: ${stat.wins}W-${stat.losses}L",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.Bold
                            ),
                            color = accent
                        )
                    }
                }
            }

            if (matchHistory.isEmpty()) {
                Text(
                    text = stringResource(R.string.match_history_empty),
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonTextMuted
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(items = matchHistory, key = { it.matchNumber }) { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(NeonElevatedSurface)
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "#${item.matchNumber} (${item.gridSizeLabel}) • ${item.scoreSummary}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = JetBrainsMonoFontFamily
                                ),
                                color = NeonTextPrimary
                            )
                            Text(
                                text = item.winnerLabel,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = NeonCyan
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ArcadeModePill(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    accentColor: Color,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) accentColor else NeonElevatedSurface)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) accentColor else NeonBorder,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.Black else accentColor,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = if (isSelected) Color.Black else NeonTextPrimary
            )
        }
    }
}

@Composable
private fun PlayerInitialInputCard(
    label: String,
    value: String,
    isLocked: Boolean,
    accentColor: Color,
    accentFill: Color,
    testTag: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(NeonElevatedSurface)
            .border(1.5.dp, accentColor.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = accentColor,
            textAlign = TextAlign.Center
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = !isLocked,
            readOnly = isLocked,
            singleLine = true,
            textStyle = MaterialTheme.typography.headlineMedium.copy(
                fontFamily = JetBrainsMonoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                textAlign = TextAlign.Center,
                color = if (isLocked) accentColor else NeonTextPrimary
            ),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Characters,
                imeAction = ImeAction.Done
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = accentColor,
                unfocusedBorderColor = NeonBorder,
                disabledBorderColor = accentColor.copy(alpha = 0.7f),
                focusedContainerColor = accentFill,
                unfocusedContainerColor = NeonDarkBackground,
                disabledContainerColor = accentFill,
                disabledTextColor = accentColor,
                cursorColor = accentColor
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .size(width = 72.dp, height = 58.dp)
                .testTag(testTag)
        )
    }
}
