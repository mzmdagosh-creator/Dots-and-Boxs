package com.example

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.CampaignLevel
import com.example.model.CpuDifficulty
import com.example.model.GameMode
import com.example.model.GridSize
import com.example.model.TimerMode
import com.example.ui.CreditsScreen
import com.example.ui.CustomArcadeConfigPanel
import com.example.ui.DashboardSessionHistoryCard
import com.example.ui.GameScreen
import com.example.ui.TournamentList
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.GameViewModel
import com.example.viewmodel.MainMenuSection
import com.example.viewmodel.MatchHistoryItem
import com.example.viewmodel.PlayerSessionStats
import com.example.viewmodel.ScreenDestination
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private val ArcadeDarkBackground = Color(0xFF121212)
private val ArcadeCardSurface = Color(0xFF1A1E29)
private val ArcadeCyan = Color(0xFF00E5FF)
private val ArcadeMagenta = Color(0xFFFF007F)
private val ArcadeTextPrimary = Color(0xFFF8FAFC)
private val ArcadeTextMuted = Color(0xFF94A3B8)

private data class FloatingDotParticle(
    val xFraction: Float,
    val yFraction: Float,
    val radiusDp: Float,
    val speedX: Float,
    val speedY: Float,
    val phaseOffset: Float,
    val isCyan: Boolean,
    val baseAlpha: Float
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DotsAndBoxesApp()
        }
    }
}

@Composable
fun DotsAndBoxesApp(
    viewModel: GameViewModel = viewModel()
) {
    val context = LocalContext.current
    LaunchedEffect(viewModel, context) {
        val prefs = context.getSharedPreferences(GameViewModel.PREFS_NAME, Context.MODE_PRIVATE)
        viewModel.attachSharedPreferences(prefs)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MyApplicationTheme(themeMode = AppThemeMode.CLASSIC_DARK) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = ArcadeDarkBackground,
            contentWindowInsets = WindowInsets.safeDrawing
        ) { innerPadding ->
            Crossfade(
                targetState = uiState.currentScreen,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                label = "neonArcadeScreenTransition"
            ) { destination ->
                when (destination) {
                    ScreenDestination.SETUP -> {
                        NeonArcadeDashboard(
                            totalBoxesCaptured = uiState.totalBoxesCaptured,
                            unlockedCampaignLevel = uiState.unlockedCampaignLevel,
                            selectedCampaignLevel = uiState.selectedCampaignLevel,
                            mainMenuSection = uiState.mainMenuSection,
                            selectedGameMode = uiState.selectedGameMode,
                            selectedGridSize = uiState.selectedGridSize,
                            selectedCpuDifficulty = uiState.selectedCpuDifficulty,
                            selectedTimerMode = uiState.selectedTimerMode,
                            selectedPlayerCount = uiState.selectedPlayerCount,
                            playerInputs = uiState.playerInputs,
                            sessionStats = uiState.sessionStats,
                            matchHistory = uiState.matchHistory,
                            errorResId = uiState.setupErrorResId,
                            onResumeTournament = viewModel::resumeTournament,
                            onSelectMainMenuSection = viewModel::selectMainMenuSection,
                            onSelectCampaignLevel = viewModel::selectCampaignLevel,
                            onStartCampaignLevel = viewModel::startCampaignLevel,
                            onSelectGameMode = viewModel::selectGameMode,
                            onSelectGridSize = viewModel::selectGridSize,
                            onSelectCpuDifficulty = viewModel::selectCpuDifficulty,
                            onSelectTimerMode = viewModel::selectTimerMode,
                            onSelectPlayerCount = viewModel::selectPlayerCount,
                            onPlayerInputChange = viewModel::updatePlayerInput,
                            onStartCustomGame = {
                                viewModel.startGame(
                                    emptyErrorResId = R.string.error_empty_letter,
                                    sameLetterErrorResId = R.string.error_same_letter
                                )
                            }
                        )
                    }

                    ScreenDestination.CREDITS -> {
                        CreditsScreen(
                            onBack = viewModel::closeCreditsScreen
                        )
                    }

                    ScreenDestination.GAME -> {
                        GameScreen(
                            soundEnabled = uiState.soundEnabled,
                            hapticsEnabled = uiState.hapticsEnabled,
                            activeCampaignLevel = uiState.activeCampaignLevel,
                            justUnlockedNextCampaignLevel = uiState.justUnlockedNextCampaignLevel,
                            selectedGridSize = uiState.selectedGridSize,
                            selectedCpuDifficulty = uiState.selectedCpuDifficulty,
                            selectedTimerMode = uiState.selectedTimerMode,
                            blitzRemainingSeconds = uiState.blitzRemainingSeconds,
                            blitzRemainingFraction = uiState.blitzRemainingFraction,
                            lastTurnForfeitedByTimeout = uiState.lastTurnForfeitedByTimeout,
                            boardState = uiState.boardState,
                            sessionStats = uiState.sessionStats,
                            matchHistory = uiState.matchHistory,
                            hoveredEdge = uiState.hoveredEdge,
                            boardZoom = uiState.boardZoom,
                            lastHapticEvent = uiState.lastHapticEvent,
                            victoryAnimationTriggerId = uiState.victoryAnimationTriggerId,
                            onSelectGridSize = viewModel::selectGridSize,
                            onSelectCpuDifficulty = viewModel::selectCpuDifficulty,
                            onSelectTimerMode = viewModel::selectTimerMode,
                            onBlitzTimeout = viewModel::onBlitzTimeoutForfeitTurn,
                            onAdvanceToNextCampaignLevel = viewModel::advanceToNextCampaignLevel,
                            onToggleSound = viewModel::toggleSound,
                            onToggleHaptics = viewModel::toggleHaptics,
                            onEdgeHover = viewModel::onEdgeHovered,
                            onEdgeClick = viewModel::onEdgeClicked,
                            onAdjustZoom = viewModel::adjustZoom,
                            onSetZoom = viewModel::setZoom,
                            onRestartMatch = viewModel::restartMatch,
                            onReturnToSetup = viewModel::returnToSetup
                        )
                    }
                }
            }
        }
    }
}

/**
 * Main Menu UI (Strictly "Neon Arcade Dashboard"):
 * - Pure dark mode background (`Color(0xFF121212)`) with a subtle floating dots animation.
 * - Glowing neon header "DOTS & BOXES".
 * - Stats displaying "Total Boxes Captured" and "Tournament Level Reached" (from SharedPreferences).
 * - Two large glowing action buttons: Cyan "Resume Tournament" and Magenta "Custom Arcade".
 */
@Composable
fun NeonArcadeDashboard(
    totalBoxesCaptured: Int = 0,
    unlockedCampaignLevel: Int = 1,
    selectedCampaignLevel: CampaignLevel = CampaignLevel.LEVEL_1,
    mainMenuSection: MainMenuSection = MainMenuSection.CAMPAIGN,
    selectedGameMode: GameMode = GameMode.CPU,
    selectedGridSize: GridSize = GridSize.SIZE_4X4,
    selectedCpuDifficulty: CpuDifficulty = CpuDifficulty.MEDIUM,
    selectedTimerMode: TimerMode = TimerMode.RELAXED,
    selectedPlayerCount: Int = 1,
    playerInputs: List<String> = listOf("A", "C", "D", "E"),
    sessionStats: Map<Int, PlayerSessionStats> = emptyMap(),
    matchHistory: List<MatchHistoryItem> = emptyList(),
    errorResId: Int? = null,
    onResumeTournament: () -> Unit = {},
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
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val infiniteTransition = rememberInfiniteTransition(label = "neonArcadeTransition")
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
        label = "floatingDotsDrift"
    )

    val floatingDots = remember {
        listOf(
            FloatingDotParticle(0.10f, 0.12f, 5.0f, 1.0f, 1.0f, 0.00f, true, 0.24f),
            FloatingDotParticle(0.26f, 0.22f, 3.8f, -1.0f, 1.0f, 0.14f, false, 0.22f),
            FloatingDotParticle(0.45f, 0.09f, 5.4f, 1.0f, -1.0f, 0.28f, true, 0.20f),
            FloatingDotParticle(0.68f, 0.18f, 4.2f, -1.0f, -1.0f, 0.42f, false, 0.24f),
            FloatingDotParticle(0.88f, 0.14f, 5.2f, 1.0f, 1.0f, 0.56f, true, 0.19f),
            FloatingDotParticle(0.15f, 0.42f, 4.6f, -1.0f, 1.0f, 0.70f, false, 0.22f),
            FloatingDotParticle(0.35f, 0.48f, 3.5f, 1.0f, -1.0f, 0.84f, true, 0.20f),
            FloatingDotParticle(0.58f, 0.39f, 5.6f, -1.0f, 1.0f, 0.18f, false, 0.20f),
            FloatingDotParticle(0.82f, 0.46f, 4.0f, 1.0f, 1.0f, 0.33f, true, 0.24f),
            FloatingDotParticle(0.12f, 0.72f, 5.0f, 1.0f, -1.0f, 0.49f, true, 0.22f),
            FloatingDotParticle(0.32f, 0.81f, 4.2f, -1.0f, -1.0f, 0.63f, false, 0.23f),
            FloatingDotParticle(0.54f, 0.68f, 5.2f, 1.0f, 1.0f, 0.77f, true, 0.19f),
            FloatingDotParticle(0.75f, 0.78f, 4.4f, -1.0f, 1.0f, 0.91f, false, 0.24f),
            FloatingDotParticle(0.90f, 0.86f, 3.8f, 1.0f, -1.0f, 0.25f, true, 0.21f)
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .testTag("main_menu_dashboard"),
        contentAlignment = Alignment.TopCenter
    ) {
        // Subtle floating neon dots animation over pure dark Color(0xFF121212)
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
                val dotColor = if (dot.isCyan) ArcadeCyan else ArcadeMagenta
                val radiusPx = dot.radiusDp.dp.toPx()

                drawCircle(
                    color = dotColor.copy(alpha = dot.baseAlpha * 0.4f),
                    radius = radiusPx * 2.5f,
                    center = Offset(centerX, centerY)
                )
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
            // Header: Glowing neon title "DOTS & BOXES"
            NeonArcadeHeader(glowAlpha = headerGlow)

            // Stats: "Total Boxes Captured" and "Tournament Level Reached" (read from SharedPreferences)
            NeonArcadeStatsRow(
                totalBoxesCaptured = totalBoxesCaptured,
                unlockedCampaignLevel = unlockedCampaignLevel,
                totalCampaignLevels = CampaignLevel.entries.size
            )

            // Main Actions: Two large glowing buttons (Cyan "Resume Tournament" & Magenta "Custom Arcade")
            NeonArcadeMainActions(
                mainMenuSection = mainMenuSection,
                unlockedCampaignLevel = unlockedCampaignLevel,
                glowAlpha = headerGlow,
                onResumeTournamentClick = {
                    onSelectMainMenuSection(MainMenuSection.CAMPAIGN)
                    onResumeTournament()
                },
                onCustomArcadeClick = {
                    if (mainMenuSection != MainMenuSection.QUICK_MATCH) {
                        onSelectMainMenuSection(MainMenuSection.QUICK_MATCH)
                    } else {
                        onStartCustomGame()
                    }
                }
            )

            // Tournament Campaign Mode (Levels 1..6 sequentially unlocked via SharedPreferences)
            AnimatedVisibility(visible = mainMenuSection == MainMenuSection.CAMPAIGN) {
                TournamentList(
                    unlockedCampaignLevel = unlockedCampaignLevel,
                    selectedCampaignLevel = selectedCampaignLevel,
                    onSelectCampaignLevel = onSelectCampaignLevel,
                    onStartCampaignLevel = onStartCampaignLevel
                )
            }

            // Custom Arcade Mode & Blitz Timer (3x3 to 6x6, Easy/Medium/Hard, 5s Blitz Mode toggle)
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

            // Session Stats & Match History
            DashboardSessionHistoryCard(
                isCpuMode = selectedGameMode == GameMode.CPU,
                selectedPlayerCount = selectedPlayerCount,
                sessionStats = sessionStats,
                matchHistory = matchHistory
            )
        }
    }
}

@Composable
private fun NeonArcadeHeader(
    glowAlpha: Float,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(ArcadeCardSurface)
            .border(
                width = 1.5.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        ArcadeCyan.copy(alpha = glowAlpha),
                        ArcadeMagenta.copy(alpha = glowAlpha)
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
                    color = ArcadeCyan.copy(alpha = glowAlpha),
                    offset = Offset.Zero,
                    blurRadius = 28f
                )
            ),
            color = ArcadeTextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.testTag("neon_arcade_title")
        )

        Text(
            text = "NEON ARCADE DASHBOARD",
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = JetBrainsMonoFontFamily,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.4.sp,
                shadow = Shadow(
                    color = ArcadeMagenta.copy(alpha = 0.75f),
                    offset = Offset.Zero,
                    blurRadius = 14f
                )
            ),
            color = ArcadeMagenta,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun NeonArcadeStatsRow(
    totalBoxesCaptured: Int,
    unlockedCampaignLevel: Int,
    totalCampaignLevels: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .testTag("player_stats_header_row"),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Total Boxes Captured (from SharedPreferences)
        Column(
            modifier = Modifier
                .weight(1f)
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(16.dp),
                    spotColor = ArcadeCyan.copy(alpha = 0.45f)
                )
                .clip(RoundedCornerShape(16.dp))
                .background(ArcadeCardSurface)
                .border(1.5.dp, ArcadeCyan, RoundedCornerShape(16.dp))
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
                    tint = ArcadeCyan,
                    modifier = Modifier.size(15.dp)
                )
                Text(
                    text = "Total Boxes Captured",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = JetBrainsMonoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    ),
                    color = ArcadeTextPrimary,
                    textAlign = TextAlign.Center
                )
            }
            Text(
                text = totalBoxesCaptured.toString(),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = FontWeight.Black,
                    shadow = Shadow(
                        color = ArcadeCyan,
                        offset = Offset.Zero,
                        blurRadius = 14f
                    )
                ),
                color = ArcadeCyan,
                modifier = Modifier.testTag("total_boxes_captured_value")
            )
        }

        // Tournament Level Reached (from SharedPreferences)
        Column(
            modifier = Modifier
                .weight(1f)
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(16.dp),
                    spotColor = ArcadeMagenta.copy(alpha = 0.45f)
                )
                .clip(RoundedCornerShape(16.dp))
                .background(ArcadeCardSurface)
                .border(1.5.dp, ArcadeMagenta, RoundedCornerShape(16.dp))
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
                    tint = ArcadeMagenta,
                    modifier = Modifier.size(15.dp)
                )
                Text(
                    text = "Tournament Level Reached",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = JetBrainsMonoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    ),
                    color = ArcadeTextPrimary,
                    textAlign = TextAlign.Center
                )
            }
            Text(
                text = "Lvl $unlockedCampaignLevel / $totalCampaignLevels",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = FontWeight.Black,
                    shadow = Shadow(
                        color = ArcadeMagenta,
                        offset = Offset.Zero,
                        blurRadius = 14f
                    )
                ),
                color = ArcadeMagenta,
                modifier = Modifier.testTag("tournament_level_reached_value")
            )
        }
    }
}

@Composable
private fun NeonArcadeMainActions(
    mainMenuSection: MainMenuSection,
    unlockedCampaignLevel: Int,
    glowAlpha: Float,
    onResumeTournamentClick: () -> Unit,
    onCustomArcadeClick: () -> Unit,
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
            onClick = onResumeTournamentClick,
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isCampaignSelected) ArcadeCyan else ArcadeCardSurface,
                contentColor = if (isCampaignSelected) Color.Black else ArcadeCyan
            ),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 14.dp),
            modifier = Modifier
                .weight(1f)
                .height(84.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(18.dp),
                    spotColor = ArcadeCyan.copy(alpha = glowAlpha)
                )
                .border(
                    width = 2.dp,
                    color = ArcadeCyan,
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
                    text = "Lvl ${currentLevel.levelNumber} • ${currentLevel.gridSize.label} ${currentLevel.difficulty.label}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 10.sp
                    ),
                    color = if (isCampaignSelected) Color.Black.copy(alpha = 0.8f) else ArcadeTextMuted,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Large Glowing Magenta Button: "Custom Arcade"
        Button(
            onClick = onCustomArcadeClick,
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (!isCampaignSelected) ArcadeMagenta else ArcadeCardSurface,
                contentColor = if (!isCampaignSelected) Color.White else ArcadeMagenta
            ),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 14.dp),
            modifier = Modifier
                .weight(1f)
                .height(84.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(18.dp),
                    spotColor = ArcadeMagenta.copy(alpha = glowAlpha)
                )
                .border(
                    width = 2.dp,
                    color = ArcadeMagenta,
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
                    text = "3×3–6×6 • Blitz Mode",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 10.sp
                    ),
                    color = if (!isCampaignSelected) Color.White.copy(alpha = 0.9f) else ArcadeTextMuted,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
