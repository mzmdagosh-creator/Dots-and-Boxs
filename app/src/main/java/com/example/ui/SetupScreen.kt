package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TimerOff
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.outlined.GridOn
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.CampaignLevel
import com.example.model.CpuDifficulty
import com.example.model.GameMode
import com.example.model.GridSize
import com.example.model.TimerMode
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalGamePalette
import com.example.viewmodel.MainMenuSection
import com.example.viewmodel.MatchHistoryItem
import com.example.viewmodel.PlayerSessionStats

@Composable
fun SetupScreen(
    themeMode: AppThemeMode,
    soundEnabled: Boolean,
    hapticsEnabled: Boolean,
    totalBoxesCaptured: Int,
    mainMenuSection: MainMenuSection,
    unlockedCampaignLevel: Int,
    selectedCampaignLevel: CampaignLevel,
    selectedGameMode: GameMode,
    selectedGridSize: GridSize,
    selectedCpuDifficulty: CpuDifficulty,
    selectedTimerMode: TimerMode,
    selectedPlayerCount: Int,
    playerInputs: List<String>,
    sessionStats: Map<Int, PlayerSessionStats>,
    matchHistory: List<MatchHistoryItem>,
    errorResId: Int?,
    onSelectThemeMode: (AppThemeMode) -> Unit,
    onSetSoundEnabled: (Boolean) -> Unit,
    onToggleHaptics: () -> Unit,
    onClearData: () -> Unit,
    onOpenCredits: () -> Unit,
    onSelectMainMenuSection: (MainMenuSection) -> Unit,
    onSelectCampaignLevel: (CampaignLevel) -> Unit,
    onStartCampaignLevel: (CampaignLevel) -> Unit,
    onResumeTournament: () -> Unit,
    onSelectGameMode: (GameMode) -> Unit,
    onSelectGridSize: (GridSize) -> Unit,
    onSelectCpuDifficulty: (CpuDifficulty) -> Unit,
    onSelectTimerMode: (TimerMode) -> Unit,
    onSelectPlayerCount: (Int) -> Unit,
    onPlayerInputChange: (Int, String) -> Unit,
    onStartGame: () -> Unit,
    onOpenHtmlViewer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalGamePalette.current
    val scrollState = rememberScrollState()
    val isCpuMode = selectedGameMode == GameMode.CPU

    val infiniteTransition = rememberInfiniteTransition(label = "neonDashboardGlow")
    val headerGlow by infiniteTransition.animateFloat(
        initialValue = 0.65f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "headerGlow"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        palette.backgroundRadialTop,
                        palette.backgroundDeep
                    )
                )
            )
            .testTag("main_menu_dashboard"),
        contentAlignment = Alignment.Center
    ) {
        // 1. Subtle, infinite animation of faint cyan and magenta dots drifting slowly across the screen
        DriftingNeonDotsBackground()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 580.dp)
                .verticalScroll(scrollState)
                .padding(horizontal = 18.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 2. Glowing Neon Arcade Header + SharedPreferences Player Stats ("Total Boxes Captured" & "Tournament Level Reached")
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(24.dp),
                        spotColor = palette.p1Accent.copy(alpha = 0.5f * headerGlow)
                    )
                    .border(
                        width = 1.5.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                palette.p1Accent.copy(alpha = headerGlow),
                                palette.p2Accent.copy(alpha = headerGlow)
                            )
                        ),
                        shape = RoundedCornerShape(24.dp)
                    ),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = palette.surface.copy(alpha = 0.94f))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(104.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_hero_dots_boxes),
                            contentDescription = stringResource(R.string.hero_banner_desc),
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(10.dp)
                                .clip(RoundedCornerShape(50))
                                .background(palette.backgroundDeep.copy(alpha = 0.88f))
                                .border(1.dp, palette.p1Accent.copy(alpha = 0.6f), RoundedCornerShape(50))
                                .padding(horizontal = 12.dp, vertical = 5.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.GridOn,
                                    contentDescription = null,
                                    tint = palette.p1Accent,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "NEON ARCADE • V6",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = palette.primaryText
                                )
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.setup_title),
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp,
                                shadow = Shadow(
                                    color = palette.p1Accent.copy(alpha = headerGlow),
                                    offset = Offset.Zero,
                                    blurRadius = 24f
                                )
                            ),
                            color = palette.primaryText,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.testTag("neon_arcade_title")
                        )
                        Text(
                            text = stringResource(R.string.setup_subtitle),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = JetBrainsMonoFontFamily,
                                letterSpacing = 1.1.sp,
                                shadow = Shadow(
                                    color = palette.p2Accent.copy(alpha = 0.7f),
                                    offset = Offset.Zero,
                                    blurRadius = 12f
                                )
                            ),
                            color = palette.p2Accent,
                            textAlign = TextAlign.Center
                        )

                        // Player Stats Row (Read from SharedPreferences)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("player_stats_header_row"),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Stat 1: Total Boxes Captured
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(palette.backgroundDeep)
                                    .border(1.dp, palette.p1Accent.copy(alpha = 0.65f), RoundedCornerShape(14.dp))
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                                    .testTag("total_boxes_captured_stat"),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.GridView,
                                        contentDescription = null,
                                        tint = palette.p1Accent,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = stringResource(R.string.stat_total_boxes_captured),
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                        color = palette.mutedText,
                                        textAlign = TextAlign.Center
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = totalBoxesCaptured.toString(),
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontWeight = FontWeight.Black,
                                        shadow = Shadow(
                                            color = palette.p1Accent,
                                            offset = Offset.Zero,
                                            blurRadius = 12f
                                        )
                                    ),
                                    color = palette.p1Accent,
                                    modifier = Modifier.testTag("total_boxes_captured_value")
                                )
                            }

                            // Stat 2: Tournament Level Reached
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(palette.backgroundDeep)
                                    .border(1.dp, palette.p2Accent.copy(alpha = 0.65f), RoundedCornerShape(14.dp))
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                                    .testTag("tournament_level_reached_stat"),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.EmojiEvents,
                                        contentDescription = null,
                                        tint = palette.p2Accent,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = stringResource(R.string.stat_tournament_level_reached),
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                        color = palette.mutedText,
                                        textAlign = TextAlign.Center
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Lvl $unlockedCampaignLevel / ${CampaignLevel.entries.size}",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontWeight = FontWeight.Black,
                                        shadow = Shadow(
                                            color = palette.p2Accent,
                                            offset = Offset.Zero,
                                            blurRadius = 12f
                                        )
                                    ),
                                    color = palette.p2Accent,
                                    modifier = Modifier.testTag("tournament_level_reached_value")
                                )
                            }
                        }
                    }
                }
            }

            // 3. Large, Glowing Main Action Cards: "Resume Tournament" & "Custom Arcade"
            val currentReachedLevel = CampaignLevel.fromNumber(unlockedCampaignLevel)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GlowingMainActionCard(
                    title = stringResource(R.string.main_menu_campaign_button),
                    subtitle = "Lvl ${currentReachedLevel.levelNumber}: ${currentReachedLevel.gridSize.label} • ${currentReachedLevel.difficulty.label} AI",
                    actionLabel = "Play Lvl ${currentReachedLevel.levelNumber}",
                    icon = Icons.Filled.EmojiEvents,
                    isSelected = mainMenuSection == MainMenuSection.CAMPAIGN,
                    accentColor = palette.p1Accent,
                    glowAlpha = headerGlow,
                    cardTestTag = "menu_campaign_button",
                    actionButtonTestTag = "resume_tournament_action_button",
                    onSelectCard = { onSelectMainMenuSection(MainMenuSection.CAMPAIGN) },
                    onQuickAction = onResumeTournament,
                    modifier = Modifier.weight(1f)
                )

                GlowingMainActionCard(
                    title = stringResource(R.string.main_menu_quick_match_button),
                    subtitle = "${selectedGridSize.label} • ${selectedCpuDifficulty.label} • ${if (selectedTimerMode == TimerMode.BLITZ) "Blitz 5s" else "Relaxed"}",
                    actionLabel = "Launch Arcade",
                    icon = Icons.Filled.Bolt,
                    isSelected = mainMenuSection == MainMenuSection.QUICK_MATCH,
                    accentColor = palette.p2Accent,
                    glowAlpha = headerGlow,
                    cardTestTag = "menu_quick_match_button",
                    actionButtonTestTag = "custom_arcade_action_button",
                    onSelectCard = { onSelectMainMenuSection(MainMenuSection.QUICK_MATCH) },
                    onQuickAction = onStartGame,
                    modifier = Modifier.weight(1f)
                )
            }

            // 4. Active Mode Configuration Card (Tournament Campaign 6-Level Progression OR Custom Arcade Setup)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, palette.border, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = palette.surface.copy(alpha = 0.94f))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // TOURNAMENT CAMPAIGN SECTION (6 Linear Levels, SharedPreferences unlocked state)
                    AnimatedVisibility(visible = mainMenuSection == MainMenuSection.CAMPAIGN) {
                        TournamentCampaignSection(
                            unlockedCampaignLevel = unlockedCampaignLevel,
                            selectedCampaignLevel = selectedCampaignLevel,
                            onSelectCampaignLevel = onSelectCampaignLevel,
                            onStartCampaignLevel = onStartCampaignLevel
                        )
                    }

                    // CUSTOM ARCADE (QUICK MATCH) SECTION (Grid Size 3x3-6x6, Difficulty Easy/Medium/Hard, Blitz 5s Timer)
                    AnimatedVisibility(visible = mainMenuSection == MainMenuSection.QUICK_MATCH) {
                        QuickMatchConfigSection(
                            selectedGridSize = selectedGridSize,
                            selectedGameMode = selectedGameMode,
                            selectedCpuDifficulty = selectedCpuDifficulty,
                            selectedTimerMode = selectedTimerMode,
                            selectedPlayerCount = selectedPlayerCount,
                            playerInputs = playerInputs,
                            errorResId = errorResId,
                            isCpuMode = isCpuMode,
                            onSelectGridSize = onSelectGridSize,
                            onSelectGameMode = onSelectGameMode,
                            onSelectCpuDifficulty = onSelectCpuDifficulty,
                            onSelectTimerMode = onSelectTimerMode,
                            onSelectPlayerCount = onSelectPlayerCount,
                            onPlayerInputChange = onPlayerInputChange,
                            onStartGame = onStartGame
                        )
                    }

                    // Theme Picker ('Classic Dark' vs 'High Contrast') & Sound Engine Toggle
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.theme_picker_label),
                            style = MaterialTheme.typography.labelSmall,
                            color = palette.mutedText
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ModeTogglePill(
                                title = stringResource(R.string.theme_classic_dark),
                                icon = Icons.Filled.DarkMode,
                                isSelected = themeMode == AppThemeMode.CLASSIC_DARK,
                                accentColor = palette.p1Accent,
                                testTag = "theme_classic_dark_button",
                                onClick = { onSelectThemeMode(AppThemeMode.CLASSIC_DARK) },
                                modifier = Modifier.weight(1f)
                            )

                            ModeTogglePill(
                                title = stringResource(R.string.theme_high_contrast),
                                icon = Icons.Filled.Contrast,
                                isSelected = themeMode == AppThemeMode.HIGH_CONTRAST,
                                accentColor = palette.p2Accent,
                                testTag = "theme_high_contrast_button",
                                onClick = { onSelectThemeMode(AppThemeMode.HIGH_CONTRAST) },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Text(
                            text = stringResource(R.string.sound_effects_label),
                            style = MaterialTheme.typography.labelSmall,
                            color = palette.mutedText
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ModeTogglePill(
                                title = stringResource(R.string.sound_on),
                                icon = Icons.Filled.VolumeUp,
                                isSelected = soundEnabled,
                                accentColor = palette.p3Accent,
                                testTag = "sound_on_button",
                                onClick = { onSetSoundEnabled(true) },
                                modifier = Modifier.weight(1f)
                            )

                            ModeTogglePill(
                                title = stringResource(R.string.sound_muted),
                                icon = Icons.Filled.VolumeOff,
                                isSelected = !soundEnabled,
                                accentColor = palette.p2Accent,
                                testTag = "sound_mute_button",
                                onClick = { onSetSoundEnabled(false) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // 5. Persistent Session Stats, Match History (Last 5), & Web HTML Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, palette.border, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = palette.surface.copy(alpha = 0.94f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val activeCount = if (isCpuMode) 2 else selectedPlayerCount
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.session_stats_title).uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = palette.mutedText
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            for (pNum in 1..activeCount) {
                                val stat = sessionStats[pNum] ?: PlayerSessionStats()
                                val label = if (isCpuMode && pNum == 2) "CPU" else "P$pNum"
                                Text(
                                    text = "$label: ${stat.wins}W-${stat.losses}L",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = palette.primaryText
                                )
                            }
                        }
                    }

                    // Match History Section (Last 5 Completed Games)
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.History,
                                contentDescription = null,
                                tint = palette.p2Accent,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = stringResource(R.string.match_history_title).uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = palette.mutedText
                            )
                        }

                        if (matchHistory.isEmpty()) {
                            Text(
                                text = stringResource(R.string.match_history_empty),
                                style = MaterialTheme.typography.labelSmall,
                                color = palette.mutedText
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 110.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                items(
                                    items = matchHistory,
                                    key = { it.matchNumber }
                                ) { item ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(palette.surfaceElevated)
                                            .padding(horizontal = 10.dp, vertical = 5.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "#${item.matchNumber} (${item.gridSizeLabel}) • ${item.scoreSummary}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = palette.primaryText
                                        )
                                        Text(
                                            text = item.winnerLabel,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = palette.p1Accent
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null,
                            tint = palette.p1Accent,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = stringResource(R.string.rules_summary),
                            style = MaterialTheme.typography.bodyMedium,
                            color = palette.mutedText
                        )
                    }

                    OutlinedButton(
                        onClick = onOpenHtmlViewer,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("open_html_viewer_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = palette.primaryText)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Code,
                            contentDescription = null,
                            tint = palette.p2Accent,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = stringResource(R.string.html_dialog_title),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }

            // 6. Settings Row at Bottom: Small icon buttons for Haptics (On/Off), Clear Data, and Info/Credits
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, palette.border, RoundedCornerShape(18.dp))
                    .testTag("dashboard_settings_row"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = palette.surface.copy(alpha = 0.94f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Haptics (On/Off) Icon Button
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(onClick = onToggleHaptics)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("haptics_toggle_button"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(
                            onClick = onToggleHaptics,
                            modifier = Modifier.size(48.dp)
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
                        Text(
                            text = if (hapticsEnabled) {
                                stringResource(R.string.settings_haptics_on)
                            } else {
                                stringResource(R.string.settings_haptics_off)
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = if (hapticsEnabled) palette.p1Accent else palette.mutedText
                        )
                    }

                    // Clear Data Icon Button
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(onClick = onClearData)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("clear_data_button"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(
                            onClick = onClearData,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.DeleteSweep,
                                contentDescription = stringResource(R.string.settings_clear_data),
                                tint = palette.p2Accent
                            )
                        }
                        Text(
                            text = stringResource(R.string.settings_clear_data),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = palette.p2Accent
                        )
                    }

                    // Info / Credits Icon Button
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(onClick = onOpenCredits)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("info_credits_button"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(
                            onClick = onOpenCredits,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = stringResource(R.string.settings_info_credits),
                                tint = palette.streakAccent
                            )
                        }
                        Text(
                            text = stringResource(R.string.settings_info_credits),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = palette.streakAccent
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GlowingMainActionCard(
    title: String,
    subtitle: String,
    actionLabel: String,
    icon: ImageVector,
    isSelected: Boolean,
    accentColor: Color,
    glowAlpha: Float,
    cardTestTag: String,
    actionButtonTestTag: String,
    onSelectCard: () -> Unit,
    onQuickAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalGamePalette.current

    Card(
        modifier = modifier
            .shadow(
                elevation = if (isSelected) 16.dp else 6.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = accentColor.copy(alpha = if (isSelected) glowAlpha else 0.3f)
            )
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) accentColor else palette.border,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onSelectCard)
            .testTag(cardTestTag),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                accentColor.copy(alpha = 0.14f)
            } else {
                palette.surface.copy(alpha = 0.92f)
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.18f))
                    .border(1.5.dp, accentColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    shadow = if (isSelected) {
                        Shadow(
                            color = accentColor,
                            offset = Offset.Zero,
                            blurRadius = 14f
                        )
                    } else {
                        null
                    }
                ),
                color = palette.primaryText,
                textAlign = TextAlign.Center
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 10.sp
                ),
                color = palette.mutedText,
                textAlign = TextAlign.Center
            )

            Button(
                onClick = onQuickAction,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = accentColor,
                    contentColor = Color.Black
                ),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
                    .testTag(actionButtonTestTag)
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = actionLabel,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
private fun TournamentCampaignSection(
    unlockedCampaignLevel: Int,
    selectedCampaignLevel: CampaignLevel,
    onSelectCampaignLevel: (CampaignLevel) -> Unit,
    onStartCampaignLevel: (CampaignLevel) -> Unit
) {
    val palette = LocalGamePalette.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("tournament_campaign_section"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.campaign_section_title).uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = palette.p1Accent
            )
            Text(
                text = "Unlocked: $unlockedCampaignLevel / ${CampaignLevel.entries.size}",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = FontWeight.Bold
                ),
                color = palette.streakAccent,
                modifier = Modifier.testTag("campaign_unlocked_progress_text")
            )
        }

        Text(
            text = stringResource(R.string.campaign_section_subtitle),
            style = MaterialTheme.typography.bodySmall,
            color = palette.mutedText
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CampaignLevel.entries.forEach { level ->
                val isUnlocked = level.levelNumber <= unlockedCampaignLevel
                val isCleared = level.levelNumber < unlockedCampaignLevel
                val isSelected = selectedCampaignLevel == level && isUnlocked
                val diffColor = when (level.difficulty) {
                    CpuDifficulty.EASY -> palette.p3Accent
                    CpuDifficulty.MEDIUM -> palette.streakAccent
                    CpuDifficulty.HARD -> palette.p2Accent
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(if (isUnlocked) 1f else 0.52f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (isSelected) palette.p1Fill else palette.surfaceElevated
                        )
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = when {
                                isSelected -> palette.p1Accent
                                isCleared -> palette.p3Accent.copy(alpha = 0.65f)
                                else -> palette.border
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
                                    if (isUnlocked) palette.backgroundDeep else palette.border.copy(alpha = 0.4f)
                                )
                                .border(
                                    1.5.dp,
                                    if (isUnlocked) palette.p1Accent else palette.border,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!isUnlocked) {
                                Icon(
                                    imageVector = Icons.Filled.Lock,
                                    contentDescription = stringResource(R.string.campaign_level_locked),
                                    tint = palette.mutedText,
                                    modifier = Modifier.size(16.dp)
                                )
                            } else if (isCleared) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = stringResource(R.string.campaign_level_completed),
                                    tint = palette.p3Accent,
                                    modifier = Modifier.size(18.dp)
                                )
                            } else {
                                Text(
                                    text = level.levelNumber.toString(),
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = palette.p1Accent
                                )
                            }
                        }

                        Column {
                            Text(
                                text = level.title,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = palette.primaryText
                            )
                            Text(
                                text = "${level.gridSize.label} Grid • ${level.difficulty.name} AI",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = JetBrainsMonoFontFamily
                                ),
                                color = diffColor
                            )
                        }
                    }

                    if (isUnlocked) {
                        Button(
                            onClick = { onStartCampaignLevel(level) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) palette.p1Accent else palette.backgroundDeep,
                                contentColor = if (isSelected) Color.Black else palette.p1Accent
                            ),
                            contentPadding = PaddingValues(
                                horizontal = 12.dp,
                                vertical = 0.dp
                            ),
                            modifier = Modifier
                                .height(36.dp)
                                .testTag("campaign_level_${level.levelNumber}")
                        ) {
                            Text(
                                text = "Play",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    } else {
                        Text(
                            text = stringResource(R.string.campaign_level_locked).uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = palette.mutedText,
                            modifier = Modifier.testTag("campaign_level_${level.levelNumber}")
                        )
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
                .height(54.dp)
                .testTag("start_campaign_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = palette.p1Accent,
                contentColor = Color.Black
            )
        ) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.start_campaign_button, activeTargetLevel.levelNumber),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
private fun QuickMatchConfigSection(
    selectedGridSize: GridSize,
    selectedGameMode: GameMode,
    selectedCpuDifficulty: CpuDifficulty,
    selectedTimerMode: TimerMode,
    selectedPlayerCount: Int,
    playerInputs: List<String>,
    errorResId: Int?,
    isCpuMode: Boolean,
    onSelectGridSize: (GridSize) -> Unit,
    onSelectGameMode: (GameMode) -> Unit,
    onSelectCpuDifficulty: (CpuDifficulty) -> Unit,
    onSelectTimerMode: (TimerMode) -> Unit,
    onSelectPlayerCount: (Int) -> Unit,
    onPlayerInputChange: (Int, String) -> Unit,
    onStartGame: () -> Unit
) {
    val palette = LocalGamePalette.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("quick_match_section"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Grid Size Selector (3x3, 4x4, 5x5, 6x6)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.grid_size_label),
                style = MaterialTheme.typography.labelSmall,
                color = palette.mutedText
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
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isSelected) palette.p1Accent else palette.surfaceElevated
                            )
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) palette.p1Accent else palette.border,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { onSelectGridSize(sizeOption) }
                            .testTag("setup_grid_${sizeOption.boxRows}x${sizeOption.boxCols}_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = sizeOption.label,
                            style = MaterialTheme.typography.labelLarge,
                            color = if (isSelected) Color.Black else palette.primaryText
                        )
                    }
                }
            }
        }

        // Modifier Toggle: "Blitz Mode (5-Second Timer)" vs "Relaxed (Off)"
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.timer_mode_label),
                style = MaterialTheme.typography.labelSmall,
                color = palette.mutedText
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ModeTogglePill(
                    title = stringResource(R.string.timer_relaxed_label),
                    icon = Icons.Filled.TimerOff,
                    isSelected = selectedTimerMode == TimerMode.RELAXED,
                    accentColor = palette.p1Accent,
                    testTag = "timer_relaxed_button",
                    onClick = { onSelectTimerMode(TimerMode.RELAXED) },
                    modifier = Modifier.weight(1f)
                )

                ModeTogglePill(
                    title = stringResource(R.string.timer_blitz_label),
                    icon = Icons.Filled.Timer,
                    isSelected = selectedTimerMode == TimerMode.BLITZ,
                    accentColor = palette.p2Accent,
                    testTag = "timer_blitz_button",
                    onClick = { onSelectTimerMode(TimerMode.BLITZ) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Opponent Type: Player vs CPU or Player vs Player
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.game_mode_label),
                style = MaterialTheme.typography.labelSmall,
                color = palette.mutedText
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ModeTogglePill(
                    title = stringResource(R.string.mode_cpu_button),
                    icon = Icons.Filled.SmartToy,
                    isSelected = isCpuMode,
                    accentColor = palette.p2Accent,
                    testTag = "mode_cpu_button",
                    onClick = { onSelectGameMode(GameMode.CPU) },
                    modifier = Modifier.weight(1f)
                )

                ModeTogglePill(
                    title = stringResource(R.string.mode_pvp_button),
                    icon = Icons.Filled.Group,
                    isSelected = !isCpuMode,
                    accentColor = palette.p1Accent,
                    testTag = "mode_pvp_button",
                    onClick = { onSelectGameMode(GameMode.PVP) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // CPU Difficulty Segmented Control (Easy, Medium, Hard)
        AnimatedVisibility(visible = isCpuMode) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.cpu_difficulty_label),
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.mutedText
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CpuDifficulty.entries.forEach { diff ->
                        val isSelected = selectedCpuDifficulty == diff
                        val diffAccent = when (diff) {
                            CpuDifficulty.EASY -> palette.p3Accent
                            CpuDifficulty.MEDIUM -> palette.streakAccent
                            CpuDifficulty.HARD -> palette.p2Accent
                        }
                        val diffLabel = when (diff) {
                            CpuDifficulty.EASY -> stringResource(R.string.diff_easy)
                            CpuDifficulty.MEDIUM -> stringResource(R.string.diff_medium)
                            CpuDifficulty.HARD -> stringResource(R.string.diff_hard)
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (isSelected) diffAccent else palette.surfaceElevated
                                )
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) diffAccent else palette.border,
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable { onSelectCpuDifficulty(diff) }
                                .testTag("cpu_diff_${diff.name.lowercase()}_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = diffLabel,
                                style = MaterialTheme.typography.labelLarge,
                                color = if (isSelected) Color.Black else palette.primaryText
                            )
                        }
                    }
                }

                val diffDescription = when (selectedCpuDifficulty) {
                    CpuDifficulty.EASY -> stringResource(R.string.diff_easy_desc)
                    CpuDifficulty.MEDIUM -> stringResource(R.string.diff_medium_desc)
                    CpuDifficulty.HARD -> stringResource(R.string.diff_hard_desc)
                }
                Text(
                    text = diffDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.mutedText,
                    textAlign = TextAlign.Center
                )
            }
        }

        // PvP Player Count Selector
        AnimatedVisibility(visible = !isCpuMode) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.player_count_label),
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.mutedText
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (count in 2..4) {
                        val isSelected = selectedPlayerCount == count
                        val pillAccent = when (count) {
                            2 -> palette.p1Accent
                            3 -> palette.p3Accent
                            else -> palette.p4Accent
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (isSelected) pillAccent else palette.surfaceElevated
                                )
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) pillAccent else palette.border,
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable { onSelectPlayerCount(count) }
                                .testTag("player_count_${count}_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$count Players",
                                style = MaterialTheme.typography.labelLarge,
                                color = if (isSelected) Color.Black else palette.primaryText
                            )
                        }
                    }
                }
            }
        }

        // Row 1: Player 1 (Cyan) and Computer / Player 2 (Magenta)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PlayerInitialInputCard(
                label = stringResource(R.string.player_one_label),
                value = playerInputs.getOrElse(0) { "A" },
                isLocked = false,
                accentColor = palette.p1Accent,
                accentFill = palette.p1Fill,
                quickOptions = listOf("A", "X", "1", "P"),
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
                accentColor = palette.p2Accent,
                accentFill = palette.p2Fill,
                quickOptions = if (isCpuMode) emptyList() else listOf("B", "O", "2", "Z"),
                testTag = "player2_letter_input",
                onValueChange = { onPlayerInputChange(1, it) },
                modifier = Modifier.weight(1f)
            )
        }

        // Row 2: Player 3 and Player 4 (shown when PvP 3P or 4P is selected)
        AnimatedVisibility(visible = !isCpuMode && selectedPlayerCount >= 3) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PlayerInitialInputCard(
                    label = stringResource(R.string.player_three_label),
                    value = playerInputs.getOrElse(2) { "D" },
                    isLocked = false,
                    accentColor = palette.p3Accent,
                    accentFill = palette.p3Fill,
                    quickOptions = listOf("D", "M", "3", "E"),
                    testTag = "player3_letter_input",
                    onValueChange = { onPlayerInputChange(2, it) },
                    modifier = Modifier.weight(1f)
                )

                if (selectedPlayerCount == 4) {
                    PlayerInitialInputCard(
                        label = stringResource(R.string.player_four_label),
                        value = playerInputs.getOrElse(3) { "E" },
                        isLocked = false,
                        accentColor = palette.p4Accent,
                        accentFill = palette.p4Fill,
                        quickOptions = listOf("E", "W", "4", "V"),
                        testTag = "player4_letter_input",
                        onValueChange = { onPlayerInputChange(3, it) },
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
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
                .height(54.dp)
                .testTag("start_game_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = palette.p1Accent,
                contentColor = Color.Black
            )
        ) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = stringResource(R.string.start_game_button),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
private fun ModeTogglePill(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    accentColor: Color,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalGamePalette.current
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) accentColor else palette.surfaceElevated)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) accentColor else palette.border,
                shape = RoundedCornerShape(14.dp)
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
                modifier = Modifier.size(17.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = if (isSelected) Color.Black else palette.primaryText
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
    quickOptions: List<String>,
    testTag: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalGamePalette.current
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(palette.surfaceElevated)
            .border(1.5.dp, accentColor.copy(alpha = 0.55f), RoundedCornerShape(18.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (isLocked) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(13.dp)
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = accentColor,
                textAlign = TextAlign.Center
            )
        }

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = !isLocked,
            readOnly = isLocked,
            singleLine = true,
            textStyle = MaterialTheme.typography.headlineMedium.copy(
                fontFamily = JetBrainsMonoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                textAlign = TextAlign.Center,
                color = if (isLocked) accentColor else palette.primaryText
            ),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Characters,
                imeAction = ImeAction.Done
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = accentColor,
                unfocusedBorderColor = palette.border,
                disabledBorderColor = accentColor.copy(alpha = 0.7f),
                focusedContainerColor = accentFill,
                unfocusedContainerColor = palette.backgroundDeep,
                disabledContainerColor = accentFill,
                disabledTextColor = accentColor,
                cursorColor = accentColor
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .size(width = 76.dp, height = 64.dp)
                .testTag(testTag)
        )

        if (quickOptions.isNotEmpty()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                quickOptions.forEach { preset ->
                    val isSelected = value.equals(preset, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) accentColor else palette.backgroundDeep)
                            .border(1.dp, if (isSelected) accentColor else palette.border, CircleShape)
                            .clickable { onValueChange(preset) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = preset,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = if (isSelected) Color.Black else palette.mutedText
                        )
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier.height(28.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "CPU AUTO",
                    style = MaterialTheme.typography.labelSmall,
                    color = accentColor
                )
            }
        }
    }
}
