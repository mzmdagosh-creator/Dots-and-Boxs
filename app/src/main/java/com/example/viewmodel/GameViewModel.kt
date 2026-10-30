package com.example.viewmodel

import androidx.lifecycle.ViewModel
import com.example.model.BoardState
import com.example.model.DotsAndBoxesEngine
import com.example.model.EdgeCoordinate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class ScreenDestination {
    SETUP,
    GAME
}

data class GameUiState(
    val currentScreen: ScreenDestination = ScreenDestination.SETUP,
    val player1Input: String = "A",
    val player2Input: String = "B",
    val setupErrorResId: Int? = null,
    val boardState: BoardState = BoardState(),
    val hoveredEdge: EdgeCoordinate? = null,
    val showHtmlDialog: Boolean = false
)

class GameViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    fun updatePlayer1Input(raw: String) {
        val sanitized = raw.trim().uppercase().takeLast(1)
        _uiState.update {
            it.copy(
                player1Input = sanitized,
                setupErrorResId = null
            )
        }
    }

    fun updatePlayer2Input(raw: String) {
        val sanitized = raw.trim().uppercase().takeLast(1)
        _uiState.update {
            it.copy(
                player2Input = sanitized,
                setupErrorResId = null
            )
        }
    }

    fun startGame(emptyErrorResId: Int, sameLetterErrorResId: Int) {
        val state = _uiState.value
        val p1 = state.player1Input.trim().uppercase()
        val p2 = state.player2Input.trim().uppercase()

        if (p1.length != 1 || p2.length != 1) {
            _uiState.update { it.copy(setupErrorResId = emptyErrorResId) }
            return
        }
        if (p1 == p2) {
            _uiState.update { it.copy(setupErrorResId = sameLetterErrorResId) }
            return
        }

        _uiState.update {
            it.copy(
                currentScreen = ScreenDestination.GAME,
                setupErrorResId = null,
                hoveredEdge = null,
                boardState = BoardState(
                    player1Letter = p1,
                    player2Letter = p2
                )
            )
        }
    }

    fun onEdgeHovered(edge: EdgeCoordinate?) {
        _uiState.update { it.copy(hoveredEdge = edge) }
    }

    fun onEdgeClicked(edge: EdgeCoordinate) {
        val currentBoard = _uiState.value.boardState
        val outcome = DotsAndBoxesEngine.makeMove(currentBoard, edge) ?: return
        _uiState.update {
            it.copy(
                boardState = outcome.newState,
                hoveredEdge = null
            )
        }
    }

    fun restartMatch() {
        val currentBoard = _uiState.value.boardState
        _uiState.update {
            it.copy(
                hoveredEdge = null,
                boardState = BoardState(
                    player1Letter = currentBoard.player1Letter,
                    player2Letter = currentBoard.player2Letter
                )
            )
        }
    }

    fun returnToSetup() {
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
