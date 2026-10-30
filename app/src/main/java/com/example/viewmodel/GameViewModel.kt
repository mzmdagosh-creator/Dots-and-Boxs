package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.BoardState
import com.example.model.DotsAndBoxesEngine
import com.example.model.EdgeCoordinate
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ScreenDestination {
    SETUP,
    GAME
}

data class GameUiState(
    val currentScreen: ScreenDestination = ScreenDestination.SETUP,
    val selectedPlayerCount: Int = 2, // 1, 2, 3, or 4
    val playerInputs: List<String> = listOf("A", "B", "C", "D"),
    val setupErrorResId: Int? = null,
    val boardState: BoardState = BoardState.create(2, listOf("A", "B")),
    val hoveredEdge: EdgeCoordinate? = null,
    val boardZoom: Float = 0.92f, // Slightly zoomed out by default so all 5x9 dots have generous breathing room
    val showHtmlDialog: Boolean = false
)

class GameViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var aiTurnJob: Job? = null

    fun selectPlayerCount(count: Int) {
        val coerced = count.coerceIn(1, 4)
        _uiState.update {
            it.copy(
                selectedPlayerCount = coerced,
                setupErrorResId = null
            )
        }
    }

    fun updatePlayerInput(playerIndex: Int, raw: String) {
        if (playerIndex !in 0..3) return
        val sanitized = raw.trim().uppercase().takeLast(1)
        _uiState.update { state ->
            val updatedList = state.playerInputs.toMutableList()
            updatedList[playerIndex] = sanitized
            state.copy(
                playerInputs = updatedList,
                setupErrorResId = null
            )
        }
    }

    fun startGame(emptyErrorResId: Int, sameLetterErrorResId: Int) {
        val state = _uiState.value
        // In 1-Player mode, there are 2 participants (Player 1 + AI Bot at index 1)
        val participantCount = if (state.selectedPlayerCount == 1) 2 else state.selectedPlayerCount
        val activeLetters = state.playerInputs.take(participantCount).map { it.trim().uppercase() }

        if (activeLetters.any { it.length != 1 }) {
            _uiState.update { it.copy(setupErrorResId = emptyErrorResId) }
            return
        }
        if (activeLetters.distinct().size != activeLetters.size) {
            _uiState.update { it.copy(setupErrorResId = sameLetterErrorResId) }
            return
        }

        aiTurnJob?.cancel()
        val initialBoard = BoardState.create(
            humanPlayerCount = state.selectedPlayerCount,
            letters = activeLetters
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
        if (currentBoard.isCurrentPlayerAi || currentBoard.isGameOver) return

        val outcome = DotsAndBoxesEngine.makeMove(currentBoard, edge) ?: return
        _uiState.update {
            it.copy(
                boardState = outcome.newState,
                hoveredEdge = null
            )
        }
        scheduleAiTurnIfNeeded()
    }

    private fun scheduleAiTurnIfNeeded() {
        aiTurnJob?.cancel()
        if (!_uiState.value.boardState.isCurrentPlayerAi) return

        aiTurnJob = viewModelScope.launch {
            while (_uiState.value.boardState.isCurrentPlayerAi && !_uiState.value.boardState.isGameOver) {
                delay(420L)
                val latestBoard = _uiState.value.boardState
                if (!latestBoard.isCurrentPlayerAi || latestBoard.isGameOver) break
                val aiEdge = DotsAndBoxesEngine.chooseSmartAiMove(latestBoard) ?: break
                val outcome = DotsAndBoxesEngine.makeMove(latestBoard, aiEdge) ?: break
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
                    letters = letters
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
