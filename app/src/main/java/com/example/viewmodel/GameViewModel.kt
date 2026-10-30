package com.example.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.HapticEngine
import com.example.audio.SoundEngine
import com.example.model.BoardState
import com.example.model.CampaignLevel
import com.example.model.CpuDifficulty
import com.example.model.DotsAndBoxesEngine
import com.example.model.EdgeCoordinate
import com.example.model.GameMode
import com.example.model.GridSize
import com.example.model.MoveOutcome
import com.example.model.TimerMode
import com.example.ui.theme.AppThemeMode
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.max
import kotlin.random.Random

enum class ScreenDestination {
    SETUP,
    GAME,
    CREDITS
}

enum class MainMenuSection {
    CAMPAIGN,
    QUICK_MATCH
}

enum class HapticEventType {
    LINE_TICK,
    BOX_CAPTURE_BUZZ,
    VICTORY_CELEBRATION
}

data class HapticEvent(
    val id: Long,
    val type: HapticEventType,
    val capturedCount: Int = 0
)

data class PlayerSessionStats(
    val wins: Int = 0,
    val losses: Int = 0,
    val ties: Int = 0
)

data class MatchHistoryItem(
    val matchNumber: Int,
    val gridSizeLabel: String,
    val scoreSummary: String,
    val winnerLabel: String,
    val winnerPlayerNumber: Int
)

data class GameUiState(
    val currentScreen: ScreenDestination = ScreenDestination.SETUP,
    val mainMenuSection: MainMenuSection = MainMenuSection.CAMPAIGN,
    val themeMode: AppThemeMode = AppThemeMode.CLASSIC_DARK,
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    // Persistent Player Stats & Tournament Campaign State (saved in SharedPreferences)
    val totalBoxesCaptured: Int = 0,
    val unlockedCampaignLevel: Int = 1,
    val selectedCampaignLevel: CampaignLevel = CampaignLevel.LEVEL_1,
    val activeCampaignLevel: CampaignLevel? = null,
    val justUnlockedNextCampaignLevel: Boolean = false,
    // Custom Arcade (Quick Match) State
    val selectedGridSize: GridSize = GridSize.SIZE_4X4,
    val selectedGameMode: GameMode = GameMode.CPU,
    val selectedCpuDifficulty: CpuDifficulty = CpuDifficulty.MEDIUM,
    val selectedTimerMode: TimerMode = TimerMode.RELAXED,
    val selectedPlayerCount: Int = 1, // 1 = CPU Mode, 2..4 = PvP Mode
    val playerInputs: List<String> = listOf("A", "C", "D", "E"),
    val setupErrorResId: Int? = null,
    // Active Board & Blitz Timer State
    val boardState: BoardState = BoardState.create(
        humanPlayerCount = 1,
        letters = listOf("A", "C"),
        cpuDifficulty = CpuDifficulty.MEDIUM,
        boxRows = GridSize.SIZE_4X4.boxRows,
        boxCols = GridSize.SIZE_4X4.boxCols
    ),
    val blitzRemainingSeconds: Float = 5.0f,
    val blitzRemainingFraction: Float = 1.0f,
    val lastTurnForfeitedByTimeout: Boolean = false,
    val blitzForfeitCount: Int = 0,
    // Session Stats, Match History, Haptics & Visuals
    val sessionStats: Map<Int, PlayerSessionStats> = mapOf(
        1 to PlayerSessionStats(),
        2 to PlayerSessionStats(),
        3 to PlayerSessionStats(),
        4 to PlayerSessionStats()
    ),
    val matchHistory: List<MatchHistoryItem> = emptyList(),
    val totalMatchesCompleted: Int = 0,
    val hoveredEdge: EdgeCoordinate? = null,
    val boardZoom: Float = 0.95f,
    val showHtmlDialog: Boolean = false,
    val lastHapticEvent: HapticEvent? = null,
    val victoryAnimationTriggerId: Int = 0
)

class GameViewModel : ViewModel() {

    companion object {
        const val PREFS_NAME = "dots_and_boxes_campaign_prefs"
        const val KEY_UNLOCKED_LEVEL = "unlocked_campaign_level"
        const val KEY_TOTAL_BOXES_CAPTURED = "total_boxes_captured"
        const val KEY_HAPTICS_ENABLED = "haptics_enabled"
        const val KEY_SESSION_STATS_JSON = "dots_and_boxes_session_stats"
        const val KEY_MATCH_HISTORY_JSON = "dots_and_boxes_match_history"
        const val KEY_TOTAL_MATCHES = "dots_and_boxes_total_matches"
        private const val BLITZ_DURATION_MS = 5000L
        private const val BLITZ_TICK_MS = 50L
    }

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var sharedPreferences: SharedPreferences? = null
    private var aiTurnJob: Job? = null
    private var blitzTimerJob: Job? = null
    private var hapticEventCounter: Long = 0L

    /**
     * Initializes SharedPreferences / local storage to load and persist:
     * - "Total Boxes Captured" (`KEY_TOTAL_BOXES_CAPTURED`)
     * - "Tournament Level Reached" (`KEY_UNLOCKED_LEVEL`, 1..6)
     * - "Session Stats" (`KEY_SESSION_STATS_JSON`)
     * - "Match History" (`KEY_MATCH_HISTORY_JSON`)
     * - Haptics toggle (`KEY_HAPTICS_ENABLED`)
     */
    fun attachSharedPreferences(prefs: SharedPreferences) {
        sharedPreferences = prefs
        val savedLevel = prefs.getInt(KEY_UNLOCKED_LEVEL, 1).coerceIn(1, CampaignLevel.entries.size)
        val savedBoxes = prefs.getInt(KEY_TOTAL_BOXES_CAPTURED, 0).coerceAtLeast(0)
        val savedHaptics = prefs.getBoolean(KEY_HAPTICS_ENABLED, true)
        val loadedStats = loadSessionStatsFromPrefs(prefs)
        val loadedHistory = loadMatchHistoryFromPrefs(prefs)
        val savedTotalMatches = prefs.getInt(
            KEY_TOTAL_MATCHES,
            loadedHistory.maxOfOrNull { it.matchNumber } ?: 0
        ).coerceAtLeast(0)

        HapticEngine.isHapticsEnabled = savedHaptics
        _uiState.update { state ->
            val effectiveLevel = max(state.unlockedCampaignLevel, savedLevel)
            state.copy(
                unlockedCampaignLevel = effectiveLevel,
                selectedCampaignLevel = CampaignLevel.fromNumber(effectiveLevel),
                totalBoxesCaptured = max(state.totalBoxesCaptured, savedBoxes),
                hapticsEnabled = savedHaptics,
                sessionStats = loadedStats ?: state.sessionStats,
                matchHistory = if (loadedHistory.isNotEmpty()) loadedHistory else state.matchHistory,
                totalMatchesCompleted = max(state.totalMatchesCompleted, savedTotalMatches)
            )
        }
    }

    private fun loadSessionStatsFromPrefs(prefs: SharedPreferences): Map<Int, PlayerSessionStats>? {
        val raw = prefs.getString(KEY_SESSION_STATS_JSON, null) ?: return null
        return runCatching {
            val json = JSONObject(raw)
            val result = mutableMapOf<Int, PlayerSessionStats>()
            for (pNum in 1..4) {
                val obj = json.optJSONObject(pNum.toString())
                result[pNum] = if (obj != null) {
                    PlayerSessionStats(
                        wins = obj.optInt("wins", 0),
                        losses = obj.optInt("losses", 0),
                        ties = obj.optInt("ties", 0)
                    )
                } else {
                    PlayerSessionStats()
                }
            }
            result
        }.getOrNull()
    }

    private fun loadMatchHistoryFromPrefs(prefs: SharedPreferences): List<MatchHistoryItem> {
        val raw = prefs.getString(KEY_MATCH_HISTORY_JSON, null) ?: return emptyList()
        return runCatching {
            val arr = JSONArray(raw)
            val items = mutableListOf<MatchHistoryItem>()
            for (i in 0 until arr.length()) {
                val obj = arr.optJSONObject(i) ?: continue
                items.add(
                    MatchHistoryItem(
                        matchNumber = obj.optInt("matchNumber", i + 1),
                        gridSizeLabel = obj.optString("gridSizeLabel", "4×4"),
                        scoreSummary = obj.optString("scoreSummary", ""),
                        winnerLabel = obj.optString("winnerLabel", ""),
                        winnerPlayerNumber = obj.optInt("winnerPlayerNumber", 0)
                    )
                )
            }
            items.take(5)
        }.getOrDefault(emptyList())
    }

    private fun savePerformanceDataToPrefs(
        sessionStats: Map<Int, PlayerSessionStats>,
        matchHistory: List<MatchHistoryItem>,
        totalMatchesCompleted: Int
    ) {
        val prefs = sharedPreferences ?: return
        runCatching {
            val statsJson = JSONObject()
            for ((pNum, stat) in sessionStats) {
                val obj = JSONObject()
                    .put("wins", stat.wins)
                    .put("losses", stat.losses)
                    .put("ties", stat.ties)
                statsJson.put(pNum.toString(), obj)
            }

            val historyArr = JSONArray()
            for (item in matchHistory.take(5)) {
                val obj = JSONObject()
                    .put("matchNumber", item.matchNumber)
                    .put("gridSizeLabel", item.gridSizeLabel)
                    .put("scoreSummary", item.scoreSummary)
                    .put("winnerLabel", item.winnerLabel)
                    .put("winnerPlayerNumber", item.winnerPlayerNumber)
                historyArr.put(obj)
            }

            prefs.edit()
                .putString(KEY_SESSION_STATS_JSON, statsJson.toString())
                .putString(KEY_MATCH_HISTORY_JSON, historyArr.toString())
                .putInt(KEY_TOTAL_MATCHES, totalMatchesCompleted)
                .apply()
        }
    }

    private fun saveUnlockedLevelToPrefs(unlockedLevel: Int) {
        val clamped = unlockedLevel.coerceIn(1, CampaignLevel.entries.size)
        sharedPreferences?.edit()?.putInt(KEY_UNLOCKED_LEVEL, clamped)?.apply()
    }

    private fun saveTotalBoxesCapturedToPrefs(totalBoxes: Int) {
        val clamped = totalBoxes.coerceAtLeast(0)
        sharedPreferences?.edit()?.putInt(KEY_TOTAL_BOXES_CAPTURED, clamped)?.apply()
    }

    private fun emitHaptic(type: HapticEventType, capturedCount: Int = 0) {
        if (!_uiState.value.hapticsEnabled) return
        hapticEventCounter++
        _uiState.update {
            it.copy(
                lastHapticEvent = HapticEvent(
                    id = hapticEventCounter,
                    type = type,
                    capturedCount = capturedCount
                )
            )
        }
    }

    fun setHapticsEnabled(enabled: Boolean) {
        HapticEngine.isHapticsEnabled = enabled
        sharedPreferences?.edit()?.putBoolean(KEY_HAPTICS_ENABLED, enabled)?.apply()
        _uiState.update { it.copy(hapticsEnabled = enabled) }
        if (enabled) {
            emitHaptic(HapticEventType.LINE_TICK)
        }
    }

    fun toggleHaptics() {
        setHapticsEnabled(!_uiState.value.hapticsEnabled)
    }

    /**
     * Clears saved player data in SharedPreferences ("Total Boxes Captured" -> 0,
     * "Tournament Level Reached" -> 1, Session Stats, and Match History).
     */
    fun clearPersistentData() {
        SoundEngine.playLineClick()
        emitHaptic(HapticEventType.LINE_TICK)
        sharedPreferences?.edit()
            ?.putInt(KEY_UNLOCKED_LEVEL, 1)
            ?.putInt(KEY_TOTAL_BOXES_CAPTURED, 0)
            ?.remove(KEY_SESSION_STATS_JSON)
            ?.remove(KEY_MATCH_HISTORY_JSON)
            ?.putInt(KEY_TOTAL_MATCHES, 0)
            ?.apply()
        _uiState.update { state ->
            state.copy(
                totalBoxesCaptured = 0,
                unlockedCampaignLevel = 1,
                selectedCampaignLevel = CampaignLevel.LEVEL_1,
                activeCampaignLevel = null,
                justUnlockedNextCampaignLevel = false,
                sessionStats = mapOf(
                    1 to PlayerSessionStats(),
                    2 to PlayerSessionStats(),
                    3 to PlayerSessionStats(),
                    4 to PlayerSessionStats()
                ),
                matchHistory = emptyList(),
                totalMatchesCompleted = 0
            )
        }
    }

    fun openCreditsScreen() {
        SoundEngine.playLineClick()
        emitHaptic(HapticEventType.LINE_TICK)
        _uiState.update { it.copy(currentScreen = ScreenDestination.CREDITS) }
    }

    fun closeCreditsScreen() {
        SoundEngine.playLineClick()
        emitHaptic(HapticEventType.LINE_TICK)
        _uiState.update { it.copy(currentScreen = ScreenDestination.SETUP) }
    }

    fun selectMainMenuSection(section: MainMenuSection) {
        SoundEngine.playLineClick()
        emitHaptic(HapticEventType.LINE_TICK)
        _uiState.update { it.copy(mainMenuSection = section, setupErrorResId = null) }
    }

    fun selectThemeMode(mode: AppThemeMode) {
        _uiState.update { it.copy(themeMode = mode) }
    }

    fun toggleThemeMode() {
        _uiState.update { state ->
            val next = if (state.themeMode == AppThemeMode.CLASSIC_DARK) {
                AppThemeMode.HIGH_CONTRAST
            } else {
                AppThemeMode.CLASSIC_DARK
            }
            state.copy(themeMode = next)
        }
    }

    fun setSoundEnabled(enabled: Boolean) {
        SoundEngine.isSoundEnabled = enabled
        _uiState.update { it.copy(soundEnabled = enabled) }
        if (enabled) {
            SoundEngine.playLineClick()
        }
    }

    fun toggleSound() {
        setSoundEnabled(!_uiState.value.soundEnabled)
    }

    fun selectTimerMode(timerMode: TimerMode) {
        SoundEngine.playLineClick()
        emitHaptic(HapticEventType.LINE_TICK)
        _uiState.update { state ->
            state.copy(
                selectedTimerMode = timerMode,
                blitzRemainingSeconds = BLITZ_DURATION_MS / 1000f,
                blitzRemainingFraction = 1f,
                lastTurnForfeitedByTimeout = false
            )
        }
        if (_uiState.value.currentScreen == ScreenDestination.GAME) {
            restartBlitzTimerIfNeeded()
        }
    }

    /**
     * Changing the grid size mid-game automatically resets the board,
     * clears all lines, and resets the score to 0-0.
     */
    fun selectGridSize(gridSize: GridSize) {
        aiTurnJob?.cancel()
        blitzTimerJob?.cancel()
        SoundEngine.playLineClick()
        emitHaptic(HapticEventType.LINE_TICK)
        _uiState.update { state ->
            val currentBoard = state.boardState
            val letters = currentBoard.players.map { it.letter }
            state.copy(
                selectedGridSize = gridSize,
                activeCampaignLevel = null,
                justUnlockedNextCampaignLevel = false,
                hoveredEdge = null,
                lastTurnForfeitedByTimeout = false,
                blitzRemainingSeconds = BLITZ_DURATION_MS / 1000f,
                blitzRemainingFraction = 1f,
                boardState = BoardState.create(
                    humanPlayerCount = currentBoard.humanPlayerCount,
                    letters = letters,
                    cpuDifficulty = state.selectedCpuDifficulty,
                    boxRows = gridSize.boxRows,
                    boxCols = gridSize.boxCols
                )
            )
        }
        restartBlitzTimerIfNeeded()
    }

    fun selectGameMode(mode: GameMode) {
        _uiState.update { state ->
            val updatedInputs = state.playerInputs.toMutableList()
            if (mode == GameMode.CPU) {
                val p1 = updatedInputs.getOrElse(0) { "A" }
                updatedInputs[1] = if (p1.equals("C", ignoreCase = true)) "🤖" else "C"
            } else if (updatedInputs.getOrNull(1) == "C" || updatedInputs.getOrNull(1) == "🤖") {
                updatedInputs[1] = "B"
            }
            state.copy(
                selectedGameMode = mode,
                selectedPlayerCount = if (mode == GameMode.CPU) 1 else state.selectedPlayerCount.coerceIn(2, 4),
                playerInputs = updatedInputs,
                setupErrorResId = null
            )
        }
    }

    fun selectCpuDifficulty(difficulty: CpuDifficulty) {
        emitHaptic(HapticEventType.LINE_TICK)
        _uiState.update { state ->
            state.copy(
                selectedCpuDifficulty = difficulty,
                boardState = state.boardState.copy(cpuDifficulty = difficulty),
                setupErrorResId = null
            )
        }
    }

    fun selectPlayerCount(count: Int) {
        val coerced = count.coerceIn(1, 4)
        if (coerced == 1) {
            selectGameMode(GameMode.CPU)
        } else {
            _uiState.update { state ->
                val updatedInputs = state.playerInputs.toMutableList()
                if (updatedInputs.getOrNull(1) == "C" || updatedInputs.getOrNull(1) == "🤖") {
                    updatedInputs[1] = "B"
                }
                state.copy(
                    selectedGameMode = GameMode.PVP,
                    selectedPlayerCount = coerced,
                    playerInputs = updatedInputs,
                    setupErrorResId = null
                )
            }
        }
    }

    fun updatePlayerInput(playerIndex: Int, raw: String) {
        if (playerIndex !in 0..3) return
        if (_uiState.value.selectedGameMode == GameMode.CPU && playerIndex == 1) return

        val sanitized = raw.trim().uppercase().takeLast(1)
        _uiState.update { state ->
            val updatedList = state.playerInputs.toMutableList()
            updatedList[playerIndex] = sanitized
            if (state.selectedGameMode == GameMode.CPU && playerIndex == 0) {
                updatedList[1] = if (sanitized.equals("C", ignoreCase = true)) "🤖" else "C"
            }
            state.copy(
                playerInputs = updatedList,
                setupErrorResId = null
            )
        }
    }

    fun selectCampaignLevel(level: CampaignLevel) {
        if (level.levelNumber > _uiState.value.unlockedCampaignLevel) return
        SoundEngine.playLineClick()
        emitHaptic(HapticEventType.LINE_TICK)
        _uiState.update {
            it.copy(
                selectedCampaignLevel = level,
                setupErrorResId = null
            )
        }
    }

    /**
     * Resumes the Tournament Campaign at the highest unlocked level.
     */
    fun resumeTournament() {
        val highestUnlocked = CampaignLevel.fromNumber(_uiState.value.unlockedCampaignLevel)
        startCampaignLevel(highestUnlocked)
    }

    /**
     * Starts a Tournament Campaign level if unlocked.
     */
    fun startCampaignLevel(level: CampaignLevel = _uiState.value.selectedCampaignLevel) {
        val state = _uiState.value
        if (level.levelNumber > state.unlockedCampaignLevel) return

        aiTurnJob?.cancel()
        blitzTimerJob?.cancel()
        SoundEngine.playLineClick()
        emitHaptic(HapticEventType.LINE_TICK)

        val p1Letter = state.playerInputs.getOrElse(0) { "A" }.ifBlank { "A" }.uppercase()
        val cpuLetter = if (p1Letter == "C") "🤖" else "C"

        val campaignBoard = BoardState.create(
            humanPlayerCount = 1,
            letters = listOf(p1Letter, cpuLetter),
            cpuDifficulty = level.difficulty,
            boxRows = level.gridSize.boxRows,
            boxCols = level.gridSize.boxCols
        )

        _uiState.update {
            it.copy(
                currentScreen = ScreenDestination.GAME,
                mainMenuSection = MainMenuSection.CAMPAIGN,
                selectedCampaignLevel = level,
                activeCampaignLevel = level,
                justUnlockedNextCampaignLevel = false,
                selectedGridSize = level.gridSize,
                selectedCpuDifficulty = level.difficulty,
                selectedGameMode = GameMode.CPU,
                selectedPlayerCount = 1,
                setupErrorResId = null,
                hoveredEdge = null,
                lastTurnForfeitedByTimeout = false,
                blitzRemainingSeconds = BLITZ_DURATION_MS / 1000f,
                blitzRemainingFraction = 1f,
                boardState = campaignBoard
            )
        }
    }

    fun advanceToNextCampaignLevel() {
        val currentLevel = _uiState.value.activeCampaignLevel ?: return
        val nextNumber = (currentLevel.levelNumber + 1).coerceAtMost(CampaignLevel.entries.size)
        if (nextNumber <= _uiState.value.unlockedCampaignLevel) {
            startCampaignLevel(CampaignLevel.fromNumber(nextNumber))
        }
    }

    /**
     * Starts a Custom Arcade (Quick Match) with the selected Grid Size (3x3 to 6x6),
     * Difficulty (Easy, Medium, Hard), and Blitz Mode (5-Second Timer).
     */
    fun startGame(emptyErrorResId: Int, sameLetterErrorResId: Int) {
        val state = _uiState.value
        val humanCount = if (state.selectedGameMode == GameMode.CPU) 1 else state.selectedPlayerCount
        val participantCount = if (humanCount == 1) 2 else humanCount
        val activeLetters = state.playerInputs.take(participantCount).map { it.trim().uppercase() }

        if (activeLetters.any { it.isEmpty() }) {
            _uiState.update { it.copy(setupErrorResId = emptyErrorResId) }
            return
        }
        if (activeLetters.distinct().size != activeLetters.size) {
            _uiState.update { it.copy(setupErrorResId = sameLetterErrorResId) }
            return
        }

        aiTurnJob?.cancel()
        blitzTimerJob?.cancel()
        SoundEngine.playLineClick()
        emitHaptic(HapticEventType.LINE_TICK)

        val initialBoard = BoardState.create(
            humanPlayerCount = humanCount,
            letters = activeLetters,
            cpuDifficulty = state.selectedCpuDifficulty,
            boxRows = state.selectedGridSize.boxRows,
            boxCols = state.selectedGridSize.boxCols
        )

        _uiState.update {
            it.copy(
                currentScreen = ScreenDestination.GAME,
                activeCampaignLevel = null,
                justUnlockedNextCampaignLevel = false,
                setupErrorResId = null,
                hoveredEdge = null,
                lastTurnForfeitedByTimeout = false,
                blitzRemainingSeconds = BLITZ_DURATION_MS / 1000f,
                blitzRemainingFraction = 1f,
                boardState = initialBoard
            )
        }
        restartBlitzTimerIfNeeded()
    }

    fun onEdgeHovered(edge: EdgeCoordinate?) {
        if (_uiState.value.boardState.isCurrentPlayerAi) return
        _uiState.update { it.copy(hoveredEdge = edge) }
    }

    fun onEdgeClicked(edge: EdgeCoordinate) {
        val currentBoard = _uiState.value.boardState
        if (currentBoard.isCurrentPlayerAi || currentBoard.isGameOver) return

        val outcome = DotsAndBoxesEngine.makeMove(currentBoard, edge) ?: return
        applyMoveOutcomeWithAudioAndStats(outcome, wasHumanMove = true)
        if (_uiState.value.boardState.isCurrentPlayerAi) {
            blitzTimerJob?.cancel()
            scheduleCpuTurnIfNeeded()
        } else {
            restartBlitzTimerIfNeeded()
        }
    }

    private fun applyMoveOutcomeWithAudioAndStats(outcome: MoveOutcome, wasHumanMove: Boolean) {
        val newBoard = outcome.newState
        val capturedCount = outcome.capturedBoxes.size

        SoundEngine.playLineClick()
        if (capturedCount > 0) {
            SoundEngine.playBoxPop(newBoard.currentComboCount)
            emitHaptic(HapticEventType.BOX_CAPTURE_BUZZ, capturedCount)
        } else {
            emitHaptic(HapticEventType.LINE_TICK)
        }

        val justFinished = newBoard.isGameOver && !_uiState.value.boardState.isGameOver
        if (justFinished) {
            blitzTimerJob?.cancel()
            SoundEngine.playGameEndFanfare()
            emitHaptic(HapticEventType.VICTORY_CELEBRATION, capturedCount)
        }

        var unlockedToPersist: Int? = null
        var boxesCapturedToPersist: Int? = null
        var performanceToPersist: Triple<Map<Int, PlayerSessionStats>, List<MatchHistoryItem>, Int>? = null

        _uiState.update { state ->
            val updatedTotalBoxes = if (wasHumanMove && capturedCount > 0) {
                (state.totalBoxesCaptured + capturedCount).also { boxesCapturedToPersist = it }
            } else {
                state.totalBoxesCaptured
            }

            val updatedStats = if (justFinished) {
                recordMatchResult(state.sessionStats, newBoard)
            } else {
                state.sessionStats
            }
            val nextMatchCount = if (justFinished) {
                state.totalMatchesCompleted + 1
            } else {
                state.totalMatchesCompleted
            }
            val updatedHistory = if (justFinished) {
                val entry = buildMatchHistoryItem(nextMatchCount, newBoard)
                (listOf(entry) + state.matchHistory).take(5)
            } else {
                state.matchHistory
            }

            if (justFinished) {
                performanceToPersist = Triple(updatedStats, updatedHistory, nextMatchCount)
            }

            // Check Tournament Campaign level progression unlock when player beats Computer
            val campaignLevel = state.activeCampaignLevel
            val playerWonCampaign = justFinished && campaignLevel != null && newBoard.winner == 1
            val newUnlockedLevel = if (playerWonCampaign && campaignLevel != null) {
                val candidateNext = (campaignLevel.levelNumber + 1).coerceAtMost(CampaignLevel.entries.size)
                max(state.unlockedCampaignLevel, candidateNext)
            } else {
                state.unlockedCampaignLevel
            }
            val didUnlockNew = playerWonCampaign &&
                campaignLevel != null &&
                campaignLevel.levelNumber < CampaignLevel.entries.size

            if (newUnlockedLevel > state.unlockedCampaignLevel) {
                unlockedToPersist = newUnlockedLevel
            }

            state.copy(
                boardState = newBoard,
                totalBoxesCaptured = updatedTotalBoxes,
                unlockedCampaignLevel = newUnlockedLevel,
                selectedCampaignLevel = if (newUnlockedLevel > state.unlockedCampaignLevel) {
                    CampaignLevel.fromNumber(newUnlockedLevel)
                } else {
                    state.selectedCampaignLevel
                },
                justUnlockedNextCampaignLevel = if (justFinished) didUnlockNew else state.justUnlockedNextCampaignLevel,
                sessionStats = updatedStats,
                matchHistory = updatedHistory,
                totalMatchesCompleted = nextMatchCount,
                hoveredEdge = null,
                lastTurnForfeitedByTimeout = false,
                blitzRemainingSeconds = BLITZ_DURATION_MS / 1000f,
                blitzRemainingFraction = 1f,
                victoryAnimationTriggerId = if (justFinished) {
                    state.victoryAnimationTriggerId + 1
                } else {
                    state.victoryAnimationTriggerId
                }
            )
        }

        unlockedToPersist?.let { saveUnlockedLevelToPrefs(it) }
        boxesCapturedToPersist?.let { saveTotalBoxesCapturedToPrefs(it) }
        performanceToPersist?.let { (stats, history, total) ->
            savePerformanceDataToPrefs(stats, history, total)
        }
    }

    /**
     * Resets the 5-second Blitz countdown state for the human player's turn.
     * The active 5-second visual countdown is driven by the `LaunchedEffect` in `GameScreen`.
     */
    private fun restartBlitzTimerIfNeeded() {
        blitzTimerJob?.cancel()
        _uiState.update {
            it.copy(
                blitzRemainingSeconds = BLITZ_DURATION_MS / 1000f,
                blitzRemainingFraction = 1f
            )
        }
    }

    /**
     * Forfeits the player's turn when the 5-second Blitz timer expires and executes the AI move instantly.
     */
    fun onBlitzTimeoutForfeitTurn() {
        val currentBoard = _uiState.value.boardState
        if (currentBoard.isGameOver || currentBoard.isCurrentPlayerAi) return

        blitzTimerJob?.cancel()
        SoundEngine.playLineClick()
        emitHaptic(HapticEventType.BOX_CAPTURE_BUZZ, 1)

        val forfeitedBoard = DotsAndBoxesEngine.forfeitTurn(currentBoard)
        _uiState.update { state ->
            state.copy(
                boardState = forfeitedBoard,
                hoveredEdge = null,
                lastTurnForfeitedByTimeout = true,
                blitzForfeitCount = state.blitzForfeitCount + 1,
                blitzRemainingSeconds = 0f,
                blitzRemainingFraction = 0f
            )
        }

        if (forfeitedBoard.isCurrentPlayerAi) {
            val cpuEdge = DotsAndBoxesEngine.cpuTakeTurn(
                state = forfeitedBoard,
                difficulty = forfeitedBoard.cpuDifficulty
            )
            if (cpuEdge != null) {
                val outcome = DotsAndBoxesEngine.makeMove(forfeitedBoard, cpuEdge)
                if (outcome != null) {
                    applyMoveOutcomeWithAudioAndStats(outcome, wasHumanMove = false)
                    _uiState.update { it.copy(lastTurnForfeitedByTimeout = true) }
                }
            }
            if (_uiState.value.boardState.isCurrentPlayerAi && !_uiState.value.boardState.isGameOver) {
                scheduleCpuTurnIfNeeded(immediateFirstMove = false)
            } else if (!_uiState.value.boardState.isGameOver) {
                restartBlitzTimerIfNeeded()
            }
        } else {
            restartBlitzTimerIfNeeded()
        }
    }

    private fun buildMatchHistoryItem(
        matchNumber: Int,
        finishedBoard: BoardState
    ): MatchHistoryItem {
        val winnerNum = finishedBoard.winner ?: 0
        val winnerLabel = when {
            winnerNum == 0 -> "Tie Game"
            finishedBoard.isPlayerAi(winnerNum) -> "Computer (${finishedBoard.letterForPlayer(winnerNum)})"
            else -> "Player $winnerNum (${finishedBoard.letterForPlayer(winnerNum)})"
        }
        val scoreSummary = finishedBoard.players.joinToString(" - ") { player ->
            val pName = if (player.isAi) "CPU" else "P${player.playerNumber}"
            "$pName: ${finishedBoard.scoreForPlayer(player.playerNumber)}"
        }
        return MatchHistoryItem(
            matchNumber = matchNumber,
            gridSizeLabel = "${finishedBoard.boxRows}×${finishedBoard.boxCols}",
            scoreSummary = scoreSummary,
            winnerLabel = winnerLabel,
            winnerPlayerNumber = winnerNum
        )
    }

    private fun recordMatchResult(
        currentStats: Map<Int, PlayerSessionStats>,
        finishedBoard: BoardState
    ): Map<Int, PlayerSessionStats> {
        val mutable = currentStats.toMutableMap()
        val winnerNum = finishedBoard.winner ?: return currentStats

        for (player in finishedBoard.players) {
            val pNum = player.playerNumber
            val prev = mutable[pNum] ?: PlayerSessionStats()
            mutable[pNum] = when {
                winnerNum == 0 -> prev.copy(ties = prev.ties + 1)
                winnerNum == pNum -> prev.copy(wins = prev.wins + 1)
                else -> prev.copy(losses = prev.losses + 1)
            }
        }
        return mutable
    }

    private fun scheduleCpuTurnIfNeeded(immediateFirstMove: Boolean = false) {
        aiTurnJob?.cancel()
        if (!_uiState.value.boardState.isCurrentPlayerAi) return

        aiTurnJob = viewModelScope.launch {
            var isFirstMove = immediateFirstMove
            while (_uiState.value.boardState.isCurrentPlayerAi && !_uiState.value.boardState.isGameOver) {
                val naturalDelayMs = if (isFirstMove) {
                    260L
                } else {
                    Random.nextLong(450L, 740L)
                }
                isFirstMove = false
                delay(naturalDelayMs)
                val latestBoard = _uiState.value.boardState
                if (!latestBoard.isCurrentPlayerAi || latestBoard.isGameOver) break

                val cpuEdge = DotsAndBoxesEngine.cpuTakeTurn(
                    state = latestBoard,
                    difficulty = latestBoard.cpuDifficulty
                ) ?: break
                val outcome = DotsAndBoxesEngine.makeMove(latestBoard, cpuEdge) ?: break
                applyMoveOutcomeWithAudioAndStats(outcome, wasHumanMove = false)
            }

            // Once CPU finishes its turn sequence, start the Blitz countdown for the human player if enabled
            if (!_uiState.value.boardState.isGameOver && !_uiState.value.boardState.isCurrentPlayerAi) {
                restartBlitzTimerIfNeeded()
            }
        }
    }

    fun adjustZoom(delta: Float) {
        _uiState.update { state ->
            state.copy(boardZoom = (state.boardZoom + delta).coerceIn(0.65f, 1.35f))
        }
    }

    fun setZoom(zoom: Float) {
        _uiState.update { state ->
            state.copy(boardZoom = zoom.coerceIn(0.65f, 1.35f))
        }
    }

    fun restartMatch() {
        aiTurnJob?.cancel()
        blitzTimerJob?.cancel()
        SoundEngine.playLineClick()
        emitHaptic(HapticEventType.LINE_TICK)
        val state = _uiState.value
        val currentBoard = state.boardState
        val letters = currentBoard.players.map { it.letter }
        _uiState.update {
            it.copy(
                hoveredEdge = null,
                justUnlockedNextCampaignLevel = false,
                lastTurnForfeitedByTimeout = false,
                blitzRemainingSeconds = BLITZ_DURATION_MS / 1000f,
                blitzRemainingFraction = 1f,
                boardState = BoardState.create(
                    humanPlayerCount = currentBoard.humanPlayerCount,
                    letters = letters,
                    cpuDifficulty = state.selectedCpuDifficulty,
                    boxRows = state.selectedGridSize.boxRows,
                    boxCols = state.selectedGridSize.boxCols
                )
            )
        }
        restartBlitzTimerIfNeeded()
    }

    fun returnToSetup() {
        aiTurnJob?.cancel()
        blitzTimerJob?.cancel()
        emitHaptic(HapticEventType.LINE_TICK)
        _uiState.update {
            it.copy(
                currentScreen = ScreenDestination.SETUP,
                hoveredEdge = null,
                lastTurnForfeitedByTimeout = false
            )
        }
    }

    fun toggleHtmlDialog(show: Boolean) {
        _uiState.update { it.copy(showHtmlDialog = show) }
    }
}
