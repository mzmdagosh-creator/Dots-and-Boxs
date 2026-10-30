package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.BoardState
import com.example.model.CpuDifficulty
import com.example.model.DotsAndBoxesEngine
import com.example.model.EdgeCoordinate
import com.example.model.GameMode
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class ScreenDestination {
    SETUP,
    GAME
}

data class GameUiState(
    val currentScreen: ScreenDestination = ScreenDestination.SETUP,
    val selectedGameMode: GameMode = GameMode.PVP, // Default: Player vs Player
    val selectedCpuDifficulty: CpuDifficulty = CpuDifficulty.MEDIUM,
    val selectedPlayerCount: Int = 2, // 1 = CPU Mode, 2..4 = PvP Mode
    val playerInputs: List<String> = listOf("A", "B", "D", "E"),
    val setupErrorResId: Int? = null,
    val boardState: BoardState = BoardState.create(2, listOf("A", "B")),
    val hoveredEdge: EdgeCoordinate? = null,
    val boardZoom: Float = 0.92f,
    val showHtmlDialog: Boolean = false
)

class GameViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var aiTurnJob: Job? = null

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
        _uiState.update {
            it.copy(
                selectedCpuDifficulty = difficulty,
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
        // In CPU mode, Player 2's letter input is locked to CPU
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
        val initialBoard = BoardState.create(
            humanPlayerCount = humanCount,
            letters = activeLetters,
            cpuDifficulty = state.selectedCpuDifficulty
        )

        _uiState.update {
            it.copy(
                currentScreen = ScreenDestination.GAME,
                setupErrorResId = null,
                hoveredEdge = null,
                boardState = initialBoard
            )
        }
    }

    fun onEdgeHovered(edge: EdgeCoordinate?) {
        if (_uiState.value.boardState.isCurrentPlayerAi) return
        _uiState.update { it.copy(hoveredEdge = edge) }
    }

    fun onEdgeClicked(edge: EdgeCoordinate) {
        val currentBoard = _uiState.value.boardState
        // Block all human interaction during CPU's turn or after game over
        if (currentBoard.isCurrentPlayerAi || currentBoard.isGameOver) return

        val outcome = DotsAndBoxesEngine.makeMove(currentBoard, edge) ?: return
        _uiState.update {
            it.copy(
                boardState = outcome.newState,
                hoveredEdge = null
            )
        }
        scheduleCpuTurnIfNeeded()
    }

    /**
     * Naturally delayed CPU action loop (600ms to 1000ms per move).
     * When the CPU captures a box, it keeps the turn, waits the natural delay again,
     * and calls `DotsAndBoxesEngine.cpuTakeTurn(...)` based on its difficulty tier.
     */
    private fun scheduleCpuTurnIfNeeded() {
        aiTurnJob?.cancel()
        if (!_uiState.value.boardState.isCurrentPlayerAi) return

        aiTurnJob = viewModelScope.launch {
            while (_uiState.value.boardState.isCurrentPlayerAi && !_uiState.value.boardState.isGameOver) {
                val naturalDelayMs = Random.nextLong(600L, 1001L)
                delay(naturalDelayMs)
                val latestBoard = _uiState.value.boardState
                if (!latestBoard.isCurrentPlayerAi || latestBoard.isGameOver) break

                val cpuEdge = DotsAndBoxesEngine.cpuTakeTurn(
                    state = latestBoard,
                    difficulty = latestBoard.cpuDifficulty
                ) ?: break
                val outcome = DotsAndBoxesEngine.makeMove(latestBoard, cpuEdge) ?: break
                _uiState.update {
                    it.copy(
                        boardState = outcome.newState,
                        hoveredEdge = null
                    )
                }
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
        val currentBoard = _uiState.value.boardState
        val letters = currentBoard.players.map { it.letter }
        _uiState.update {
            it.copy(
                hoveredEdge = null,
                boardState = BoardState.create(
                    humanPlayerCount = currentBoard.humanPlayerCount,
                    letters = letters,
                    cpuDifficulty = currentBoard.cpuDifficulty
                )
            )
        }
    }

    fun returnToSetup() {
        aiTurnJob?.cancel()
        _uiState.update {
            it.copy(
                currentScreen = ScreenDestination.SETUP,
                hoveredEdge = null
            )
        }
    }

    fun toggleHtmlDialog(show: Boolean) {
        _uiState.update { it.copy(showHtmlDialog = show) }
    }
}
