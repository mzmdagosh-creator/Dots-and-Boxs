package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.compose.ui.platform.testTag
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
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalGamePalette
import com.example.viewmodel.MainMenuSection
import com.example.viewmodel.MatchHistoryItem
import com.example.viewmodel.PlayerSessionStats
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// Strict Neon Arcade Color Constants
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

private data class FloatingNeonDotSpec(
    val xFraction: Float,
    val yFraction: Float,
    val radiusDp: Float,
    val speedX: Float,
    val speedY: Float,
    val phaseOffset: Float,
    val isCyan: Boolean,
    val baseAlpha: Float
)

@Composable
fun SetupScreen(
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
    onToggleSound: () -> Unit,
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
    modifier: Modifier = Modifier
) {
    NeonDashboardScreen(
        totalBoxesCaptured = totalBoxesCaptured,
        unlockedCampaignLevel = unlockedCampaignLevel,
        selectedCampaignLevel = selectedCampaignLevel,
        mainMenuSection = mainMenuSection,
        soundEnabled = soundEnabled,
        hapticsEnabled = hapticsEnabled,
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
        onOpenCustomArcade = { onSelectMainMenuSection(MainMenuSection.QUICK_MATCH) },
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
        onToggleSound = onToggleSound,
        onToggleHaptics = onToggleHaptics,
        onClearData = onClearData,
        onOpenCredits = onOpenCredits,
        modifier = modifier
    )
}

/**
 * 1. `NeonDashboardScreen()`:
 * The main Neon Arcade Dashboard screen with a strictly dark background (`Color(0xFF121212)`)
 * and an infinite animated background of floating cyan and magenta dots.
 */
@Composable
fun NeonDashboardScreen(
    totalBoxesCaptured: Int = 0,
    unlockedCampaignLevel: Int = 1,
    selectedCampaignLevel: CampaignLevel = CampaignLevel.LEVEL_1,
    mainMenuSection: MainMenuSection = MainMenuSection.CAMPAIGN,
    soundEnabled: Boolean = true,
    hapticsEnabled: Boolean = true,
    selectedGameMode: GameMode = GameMode.CPU,
    selectedGridSize: GridSize = GridSize.SIZE_4X4,
    selectedCpuDifficulty: CpuDifficulty = CpuDifficulty.MEDIUM,
    selectedTimerMode: TimerMode = TimerMode.RELAXED,
    selectedPlayerCount: Int = 2,
    playerInputs: List<String> = listOf("A", "C", "D", "E"),
    sessionStats: Map<Int, PlayerSessionStats> = emptyMap(),
    matchHistory: List<MatchHistoryItem> = emptyList(),
    errorResId: Int? = null,
    onResumeTournament: () -> Unit = {},
    onOpenCustomArcade: () -> Unit = {},
    onSelectMainMenuSection: (MainMenuSection) -> Unit = {},
    onSelectCampaignLevel: (CampaignLevel) -> Unit = {},
    onStartCampaignLevel: (CampaignLevel) -> Unit = {},
    onSelectGameMode: (GameMode) -> Unit = {},
    onSelectGridSize: (GridSize) -> Unit = {},
    onSelectCpuDifficulty: (CpuDifficulty) -> Unit = {},
    onSelectTimerMode: (TimerMode) -> Unit = {},
    onSelectPlayerCount: (Int) -> Unit = {},
    onPlayerInputChange: (Int, String) -> Unit = { _, _ -> },
    onStartCustomGame: () -> Unit = {},
    onToggleSound: () -> Unit = {},
    onToggleHaptics: () -> Unit = {},
    onClearData: () -> Unit = {},
    onOpenCredits: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
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
    val driftProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 24000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "floatingNeonDots"
    )

    val floatingDots = remember {
        listOf(
            FloatingNeonDotSpec(0.10f, 0.12f, 5.0f, 1.0f, 1.0f, 0.00f, true, 0.24f),
            FloatingNeonDotSpec(0.26f, 0.22f, 3.8f, -1.0f, 1.0f, 0.14f, false, 0.22f),
            FloatingNeonDotSpec(0.45f, 0.09f, 5.4f, 1.0f, -1.0f, 0.28f, true, 0.20f),
            FloatingNeonDotSpec(0.68f, 0.18f, 4.2f, -1.0f, -1.0f, 0.42f, false, 0.24f),
            FloatingNeonDotSpec(0.88f, 0.14f, 5.2f, 1.0f, 1.0f, 0.56f, true, 0.19f),
            FloatingNeonDotSpec(0.15f, 0.42f, 4.6f, -1.0f, 1.0f, 0.70f, false, 0.22f),
            FloatingNeonDotSpec(0.35f, 0.48f, 3.5f, 1.0f, -1.0f, 0.84f, true, 0.20f),
            FloatingNeonDotSpec(0.58f, 0.39f, 5.6f, -1.0f, 1.0f, 0.18f, false, 0.20f),
            FloatingNeonDotSpec(0.82f, 0.46f, 4.0f, 1.0f, 1.0f, 0.33f, true, 0.24f),
            FloatingNeonDotSpec(0.12f, 0.72f, 5.0f, 1.0f, -1.0f, 0.49f, true, 0.22f),
            FloatingNeonDotSpec(0.32f, 0.81f, 4.2f, -1.0f, -1.0f, 0.63f, false, 0.23f),
            FloatingNeonDotSpec(0.54f, 0.68f, 5.2f, 1.0f, 1.0f, 0.77f, true, 0.19f),
            FloatingNeonDotSpec(0.75f, 0.78f, 4.4f, -1.0f, 1.0f, 0.91f, false, 0.24f),
            FloatingNeonDotSpec(0.90f, 0.86f, 3.8f, 1.0f, -1.0f, 0.25f, true, 0.21f)
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NeonDarkBackground)
            .testTag("main_menu_dashboard"),
        contentAlignment = Alignment.TopCenter
    ) {
        // Animated background of floating cyan and magenta dots
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .testTag("drifting_neon_dots_background")
        ) {
            val width = size.width
            val height = size.height
            if (width <= 0f || height <= 0f) return@Canvas

            val angle = driftProgress * 2f * PI.toFloat()

            floatingDots.forEach { dot ->
                val phaseAngle = angle + dot.phaseOffset * 2f * PI.toFloat()
                val offsetX = cos(phaseAngle * dot.speedX) * (width * 0.045f)
                val offsetY = sin(phaseAngle * dot.speedY) * (height * 0.045f)

                val centerX = ((dot.xFraction * width + offsetX) % width + width) % width
                val centerY = ((dot.yFraction * height + offsetY) % height + height) % height
                val dotColor = if (dot.isCyan) NeonCyan else NeonMagenta
                val radiusPx = dot.radiusDp.dp.toPx()

                // Outer neon halo
                drawCircle(
                    color = dotColor.copy(alpha = dot.baseAlpha * 0.4f),
                    radius = radiusPx * 2.5f,
                    center = Offset(centerX, centerY)
                )
                // Bright floating neon core
                drawCircle(
                    color = dotColor.copy(alpha = dot.baseAlpha),
                    radius = radiusPx,
                    center = Offset(centerX, centerY)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 580.dp)
                .verticalScroll(scrollState)
                .padding(horizontal = 18.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 2. Glowing Header Text that says "DOTS & BOXES"
            NeonHeaderSection(glowAlpha = headerGlow)

            // 3. StatsRow() showing "Total Boxes Captured: 0" and "Tournament Level Reached: Lvl 1 / 6" with neon borders
            StatsRow(
                totalBoxesCaptured = totalBoxesCaptured,
                unlockedCampaignLevel = unlockedCampaignLevel,
                totalCampaignLevels = CampaignLevel.entries.size
            )

            // 4. MainActionsRow() containing two large glowing buttons:
            //    A Cyan button for "Resume Tournament" and a Magenta button for "Custom Arcade"
            MainActionsRow(
                mainMenuSection = mainMenuSection,
                unlockedCampaignLevel = unlockedCampaignLevel,
                glowAlpha = headerGlow,
                onResumeTournamentClick = {
                    onSelectMainMenuSection(MainMenuSection.CAMPAIGN)
                    onResumeTournament()
                },
                onCustomArcadeClick = {
                    if (mainMenuSection == MainMenuSection.QUICK_MATCH) {
                        onStartCustomGame()
                    } else {
                        onOpenCustomArcade()
                    }
                },
                onSelectCampaignTab = { onSelectMainMenuSection(MainMenuSection.CAMPAIGN) },
                onSelectCustomArcadeTab = { onSelectMainMenuSection(MainMenuSection.QUICK_MATCH) }
            )

            // 5. TournamentList() below the buttons that displays 6 rows for the campaign levels,
            //    with Level 1 saying "Play" and Levels 2-6 grayed out and marked "LOCKED"
            AnimatedVisibility(visible = mainMenuSection == MainMenuSection.CAMPAIGN) {
                TournamentList(
                    unlockedCampaignLevel = unlockedCampaignLevel,
                    selectedCampaignLevel = selectedCampaignLevel,
                    onSelectCampaignLevel = onSelectCampaignLevel,
                    onStartCampaignLevel = onStartCampaignLevel
                )
            }

            // Custom Arcade Panel when "Custom Arcade" is active
            AnimatedVisibility(visible = mainMenuSection == MainMenuSection.QUICK_MATCH) {
                CustomArcadeConfigPanel(
                    selectedGridSize = selectedGridSize,
                    selectedGameMode = selectedGameMode,
                    selectedCpuDifficulty = selectedCpuDifficulty,
                    selectedTimerMode = selectedTimerMode,
                    selectedPlayerCount = selectedPlayerCount,
                    playerInputs = playerInputs,
                    errorResId = errorResId,
                    onSelectGridSize = onSelectGridSize,
                    onSelectGameMode = onSelectGameMode,
                    onSelectCpuDifficulty = onSelectCpuDifficulty,
                    onSelectTimerMode = onSelectTimerMode,
                    onSelectPlayerCount = onSelectPlayerCount,
                    onPlayerInputChange = onPlayerInputChange,
                    onStartGame = onStartCustomGame
                )
            }

            // Persistent Session Stats & Match History (Last 5)
            DashboardSessionHistoryCard(
                isCpuMode = selectedGameMode == GameMode.CPU,
                selectedPlayerCount = selectedPlayerCount,
                sessionStats = sessionStats,
                matchHistory = matchHistory
            )

            // Bottom Arcade Settings Bar (Haptics, SFX, Clear Data, Info/Credits)
            DashboardBottomSettingsRow(
                soundEnabled = soundEnabled,
                hapticsEnabled = hapticsEnabled,
                onToggleSound = onToggleSound,
                onToggleHaptics = onToggleHaptics,
                onClearData = onClearData,
                onOpenCredits = onOpenCredits
            )
        }
    }
}

/**
 * 2. Glowing Header Text that says "DOTS & BOXES"
 */
@Composable
fun NeonHeaderSection(
    glowAlpha: Float,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(NeonCardSurface)
            .border(
                width = 1.5.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        NeonCyan.copy(alpha = glowAlpha),
                        NeonMagenta.copy(alpha = glowAlpha)
                    )
                ),
                shape = RoundedCornerShape(22.dp)
            )
            .padding(horizontal = 20.dp, vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = "DOTS & BOXES",
            style = MaterialTheme.typography.displayLarge.copy(
                fontFamily = JetBrainsMonoFontFamily,
                fontWeight = FontWeight.Black,
                fontSize = 32.sp,
                letterSpacing = 3.sp,
                shadow = Shadow(
                    color = NeonCyan.copy(alpha = glowAlpha),
                    offset = Offset.Zero,
                    blurRadius = 28f
                )
            ),
            color = NeonTextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.testTag("neon_arcade_title")
        )

        Text(
            text = "NEON ARCADE DASHBOARD • TOURNAMENT & CUSTOM ARCADE",
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = JetBrainsMonoFontFamily,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                shadow = Shadow(
                    color = NeonMagenta.copy(alpha = 0.75f),
                    offset = Offset.Zero,
                    blurRadius = 14f
                )
            ),
            color = NeonMagenta,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * 3. `StatsRow()`:
 * Displays "Total Boxes Captured: 0" and "Tournament Level Reached: Lvl 1 / 6" with neon borders.
 */
@Composable
fun StatsRow(
    totalBoxesCaptured: Int = 0,
    unlockedCampaignLevel: Int = 1,
    totalCampaignLevels: Int = 6,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .testTag("player_stats_header_row"),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Neon Cyan Stat Box: Total Boxes Captured
        Column(
            modifier = Modifier
                .weight(1f)
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(16.dp),
                    spotColor = NeonCyan.copy(alpha = 0.45f)
                )
                .clip(RoundedCornerShape(16.dp))
                .background(NeonCardSurface)
                .border(1.5.dp, NeonCyan, RoundedCornerShape(16.dp))
                .padding(horizontal = 12.dp, vertical = 12.dp)
                .testTag("total_boxes_captured_stat"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.GridView,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(15.dp)
                )
                Text(
                    text = "Total Boxes Captured: $totalBoxesCaptured",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = JetBrainsMonoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    ),
                    color = NeonTextPrimary,
                    textAlign = TextAlign.Center
                )
            }
            Text(
                text = totalBoxesCaptured.toString(),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = FontWeight.Black,
                    shadow = Shadow(
                        color = NeonCyan,
                        offset = Offset.Zero,
                        blurRadius = 14f
                    )
                ),
                color = NeonCyan,
                modifier = Modifier.testTag("total_boxes_captured_value")
            )
        }

        // Neon Magenta Stat Box: Tournament Level Reached
        Column(
            modifier = Modifier
                .weight(1f)
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(16.dp),
                    spotColor = NeonMagenta.copy(alpha = 0.45f)
                )
                .clip(RoundedCornerShape(16.dp))
                .background(NeonCardSurface)
                .border(1.5.dp, NeonMagenta, RoundedCornerShape(16.dp))
                .padding(horizontal = 12.dp, vertical = 12.dp)
                .testTag("tournament_level_reached_stat"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.EmojiEvents,
                    contentDescription = null,
                    tint = NeonMagenta,
                    modifier = Modifier.size(15.dp)
                )
                Text(
                    text = "Tournament Level Reached: Lvl $unlockedCampaignLevel / $totalCampaignLevels",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = JetBrainsMonoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    ),
                    color = NeonTextPrimary,
                    textAlign = TextAlign.Center
                )
            }
            Text(
                text = "Lvl $unlockedCampaignLevel / $totalCampaignLevels",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = FontWeight.Black,
                    shadow = Shadow(
                        color = NeonMagenta,
                        offset = Offset.Zero,
                        blurRadius = 14f
                    )
                ),
                color = NeonMagenta,
                modifier = Modifier.testTag("tournament_level_reached_value")
            )
        }
    }
}

/**
 * 4. `MainActionsRow()`:
 * Contains two large glowing buttons:
 * - A Cyan button for "Resume Tournament"
 * - A Magenta button for "Custom Arcade"
 */
@Composable
fun MainActionsRow(
    mainMenuSection: MainMenuSection,
    unlockedCampaignLevel: Int,
    glowAlpha: Float,
    onResumeTournamentClick: () -> Unit,
    onCustomArcadeClick: () -> Unit,
    onSelectCampaignTab: () -> Unit = {},
    onSelectCustomArcadeTab: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val currentLevel = CampaignLevel.fromNumber(unlockedCampaignLevel)
    val isCampaignSelected = mainMenuSection == MainMenuSection.CAMPAIGN

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Large Glowing Cyan Button: "Resume Tournament"
        Button(
            onClick = {
                onSelectCampaignTab()
                onResumeTournamentClick()
            },
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isCampaignSelected) NeonCyan else NeonCardSurface,
                contentColor = if (isCampaignSelected) Color.Black else NeonCyan
            ),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 14.dp),
            modifier = Modifier
                .weight(1f)
                .height(84.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(18.dp),
                    spotColor = NeonCyan.copy(alpha = glowAlpha)
                )
                .border(
                    width = 2.dp,
                    color = NeonCyan,
                    shape = RoundedCornerShape(18.dp)
                )
                .testTag("menu_campaign_button")
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.EmojiEvents,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Resume Tournament",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        ),
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Lvl ${currentLevel.levelNumber} • ${currentLevel.gridSize.label} ${currentLevel.difficulty.label} AI",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 10.sp
                    ),
                    color = if (isCampaignSelected) Color.Black.copy(alpha = 0.8f) else NeonTextMuted,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Large Glowing Magenta Button: "Custom Arcade"
        Button(
            onClick = {
                if (mainMenuSection != MainMenuSection.QUICK_MATCH) {
                    onSelectCustomArcadeTab()
                } else {
                    onCustomArcadeClick()
                }
            },
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (!isCampaignSelected) NeonMagenta else NeonCardSurface,
                contentColor = if (!isCampaignSelected) Color.White else NeonMagenta
            ),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 14.dp),
            modifier = Modifier
                .weight(1f)
                .height(84.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(18.dp),
                    spotColor = NeonMagenta.copy(alpha = glowAlpha)
                )
                .border(
                    width = 2.dp,
                    color = NeonMagenta,
                    shape = RoundedCornerShape(18.dp)
                )
                .testTag("menu_quick_match_button")
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Bolt,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Custom Arcade",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        ),
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "3×3–6×6 • Blitz 5s • AI / PvP",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 10.sp
                    ),
                    color = if (!isCampaignSelected) Color.White.copy(alpha = 0.9f) else NeonTextMuted,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * 5. `TournamentList()`:
 * Displays 6 rows for the campaign levels below the main action buttons,
 * with Level 1 saying "Play" and Levels 2-6 grayed out and marked "LOCKED"
 * (and dynamically unlocking levels 2..6 as the player wins campaign stages).
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
                                    text = "${level.gridSize.label} Grid • ${level.difficulty.name} AI",
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

@Composable
private fun CustomArcadeConfigPanel(
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CUSTOM ARCADE CONFIGURATION",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = JetBrainsMonoFontFamily,
                        fontWeight = FontWeight.Bold
                    ),
                    color = NeonMagenta
                )
                Text(
                    text = "RETURN TO TOURNAMENT",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = JetBrainsMonoFontFamily,
                        fontWeight = FontWeight.Bold
                    ),
                    color = NeonCyan
                )
            }

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

            // Turn Timer Modifier: Relaxed vs Blitz (5s)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = stringResource(R.string.timer_mode_label),
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonTextMuted
                )
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

            // Opponent Mode: Player vs CPU or Player vs Player
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = stringResource(R.string.game_mode_label),
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonTextMuted
                )
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
            }

            // CPU Difficulty (Easy, Medium, Hard Minimax)
            AnimatedVisibility(visible = isCpuMode) {
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
            }

            // PvP Player Count (2..4)
            AnimatedVisibility(visible = !isCpuMode) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (count in 2..4) {
                        val isSelected = selectedPlayerCount == count
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
                                .clickable { onSelectPlayerCount(count) }
                                .testTag("player_count_${count}_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$count Players",
                                style = MaterialTheme.typography.labelLarge,
                                color = if (isSelected) Color.Black else NeonTextPrimary
                            )
                        }
                    }
                }
            }

            // Player Initials Row 1
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
                    accentColor = NeonMagenta,
                    accentFill = palette.p2Fill,
                    quickOptions = if (isCpuMode) emptyList() else listOf("B", "O", "2", "Z"),
                    testTag = "player2_letter_input",
                    onValueChange = { onPlayerInputChange(1, it) },
                    modifier = Modifier.weight(1f)
                )
            }

            // Player Initials Row 2 (3P/4P)
            AnimatedVisibility(visible = !isCpuMode && selectedPlayerCount >= 3) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PlayerInitialInputCard(
                        label = stringResource(R.string.player_three_label),
                        value = playerInputs.getOrElse(2) { "D" },
                        isLocked = false,
                        accentColor = NeonLime,
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
                            accentColor = NeonAmber,
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
private fun DashboardSessionHistoryCard(
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
private fun DashboardBottomSettingsRow(
    soundEnabled: Boolean,
    hapticsEnabled: Boolean,
    onToggleSound: () -> Unit,
    onToggleHaptics: () -> Unit,
    onClearData: () -> Unit,
    onOpenCredits: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, NeonBorder, RoundedCornerShape(18.dp))
            .testTag("dashboard_settings_row"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = NeonCardSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Haptics (On/Off)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onToggleHaptics)
                    .padding(horizontal = 4.dp)
                    .testTag("haptics_toggle_button"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onToggleHaptics, modifier = Modifier.size(48.dp)) {
                    Icon(
                        imageVector = Icons.Filled.Vibration,
                        contentDescription = if (hapticsEnabled) {
                            stringResource(R.string.settings_haptics_on)
                        } else {
                            stringResource(R.string.settings_haptics_off)
                        },
                        tint = if (hapticsEnabled) NeonCyan else NeonTextMuted
                    )
                }
                Text(
                    text = if (hapticsEnabled) {
                        stringResource(R.string.settings_haptics_on)
                    } else {
                        stringResource(R.string.settings_haptics_off)
                    },
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (hapticsEnabled) NeonCyan else NeonTextMuted
                )
            }

            // Audio SFX (On/Off)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onToggleSound)
                    .padding(horizontal = 4.dp)
                    .testTag("sound_toggle_button"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onToggleSound, modifier = Modifier.size(48.dp)) {
                    Icon(
                        imageVector = if (soundEnabled) Icons.Filled.VolumeUp else Icons.Filled.VolumeOff,
                        contentDescription = if (soundEnabled) {
                            stringResource(R.string.sound_on)
                        } else {
                            stringResource(R.string.sound_muted)
                        },
                        tint = if (soundEnabled) NeonLime else NeonTextMuted
                    )
                }
                Text(
                    text = if (soundEnabled) {
                        stringResource(R.string.sound_on)
                    } else {
                        stringResource(R.string.sound_muted)
                    },
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (soundEnabled) NeonLime else NeonTextMuted
                )
            }

            // Clear Data
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onClearData)
                    .padding(horizontal = 4.dp)
                    .testTag("clear_data_button"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onClearData, modifier = Modifier.size(48.dp)) {
                    Icon(
                        imageVector = Icons.Filled.DeleteSweep,
                        contentDescription = stringResource(R.string.settings_clear_data),
                        tint = NeonMagenta
                    )
                }
                Text(
                    text = stringResource(R.string.settings_clear_data),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = NeonMagenta
                )
            }

            // Info / Credits
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onOpenCredits)
                    .padding(horizontal = 4.dp)
                    .testTag("info_credits_button"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onOpenCredits, modifier = Modifier.size(48.dp)) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = stringResource(R.string.settings_info_credits),
                        tint = NeonAmber
                    )
                }
                Text(
                    text = stringResource(R.string.settings_info_credits),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = NeonAmber
                )
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
    quickOptions: List<String>,
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
                fontSize = 24.sp,
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
                .size(width = 72.dp, height = 60.dp)
                .testTag(testTag)
        )

        if (quickOptions.isNotEmpty()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                quickOptions.forEach { preset ->
                    val isSelected = value.equals(preset, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) accentColor else NeonDarkBackground)
                            .border(1.dp, if (isSelected) accentColor else NeonBorder, CircleShape)
                            .clickable { onValueChange(preset) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = preset,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isSelected) Color.Black else NeonTextMuted
                        )
                    }
                }
            }
        } else {
            Text(
                text = "CPU AUTO",
                style = MaterialTheme.typography.labelSmall,
                color = accentColor
            )
        }
    }
}
