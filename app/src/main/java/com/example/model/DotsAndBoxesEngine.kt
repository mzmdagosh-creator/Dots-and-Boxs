package com.example.model

/**
 * Pure Kotlin 2D-array engine for the 5x9 Dots and Boxes board game supporting 1 to 4 players
 * (including a smart single-player AI opponent when `humanPlayerCount == 1`) and
 * multi-square round combo tracking (`currentComboCount`, `comboChainBoxes`, `comboTriggerId`).
 *
 * Grid Geometry:
 * - 5 rows by 9 columns of dots (`DOT_ROWS = 5`, `DOT_COLS = 9`)
 * - Creates a playable area of 4 rows by 8 columns of boxes (`BOX_ROWS = 4`, `BOX_COLS = 8`, 32 boxes total)
 * - Horizontal edges: 5 rows x 8 columns (`horizontalEdges[r][c]`, where r in 0..4, c in 0..7)
 * - Vertical edges: 4 rows x 9 columns (`verticalEdges[r][c]`, where r in 0..3, c in 0..8)
 */
enum class EdgeOrientation {
    HORIZONTAL,
    VERTICAL
}

data class EdgeCoordinate(
    val orientation: EdgeOrientation,
    val row: Int,
    val col: Int
)

data class BoxCoordinate(
    val row: Int,
    val col: Int
)

data class PlayerConfig(
    val playerNumber: Int, // 1..4
    val letter: String,
    val isAi: Boolean = false
)

data class MoveOutcome(
    val newState: BoardState,
    val capturedBoxes: List<BoxCoordinate>,
    val grantedExtraTurn: Boolean
)

data class BoardState(
    val humanPlayerCount: Int = 2, // 1..4 configured by user
    val players: List<PlayerConfig> = listOf(
        PlayerConfig(1, "A", isAi = false),
        PlayerConfig(2, "B", isAi = false)
    ),
    val currentPlayer: Int = 1, // 1..players.size
    val scores: Map<Int, Int> = mapOf(1 to 0, 2 to 0),
    // 5 rows x 8 cols: 0 = unclaimed, 1..4 = claimed by Player N
    val horizontalEdges: List<List<Int>> = List(DOT_ROWS) { List(BOX_COLS) { 0 } },
    // 4 rows x 9 cols: 0 = unclaimed, 1..4 = claimed by Player N
    val verticalEdges: List<List<Int>> = List(BOX_ROWS) { List(DOT_COLS) { 0 } },
    // 4 rows x 8 cols: 0 = unclaimed, 1..4 = captured by Player N
    val boxes: List<List<Int>> = List(BOX_ROWS) { List(BOX_COLS) { 0 } },
    val lastClaimedEdge: EdgeCoordinate? = null,
    val lastCapturedBoxes: Set<BoxCoordinate> = emptySet(),
    val comboChainBoxes: Set<BoxCoordinate> = emptySet(),
    val currentComboCount: Int = 0,
    val comboTriggerId: Int = 0,
    val hasExtraTurnStreak: Boolean = false,
    val totalMovesPlayed: Int = 0
) {
    val activePlayerCount: Int
        get() = players.size

    val player1Letter: String
        get() = letterForPlayer(1)

    val player2Letter: String
        get() = letterForPlayer(2)

    val player1Score: Int
        get() = scoreForPlayer(1)

    val player2Score: Int
        get() = scoreForPlayer(2)

    val totalClaimedBoxes: Int
        get() = scores.values.sum()

    val isGameOver: Boolean
        get() = totalClaimedBoxes == TOTAL_BOXES

    val isMultiSquareCombo: Boolean
        get() = currentComboCount >= 2

    val isCurrentPlayerAi: Boolean
        get() = !isGameOver && players.firstOrNull { it.playerNumber == currentPlayer }?.isAi == true

    /**
     * Returns winning player number (1..4), or 0 if tied for first place, or null if game not over.
     */
    val winner: Int?
        get() {
            if (!isGameOver) return null
            val maxScore = scores.values.maxOrNull() ?: 0
            val topPlayers = scores.filterValues { it == maxScore }.keys
            return if (topPlayers.size == 1) topPlayers.first() else 0
        }

    val maxScore: Int
        get() = scores.values.maxOrNull() ?: 0

    fun letterForPlayer(player: Int): String =
        players.firstOrNull { it.playerNumber == player }?.letter ?: player.toString()

    fun scoreForPlayer(player: Int): Int = scores[player] ?: 0

    fun isPlayerAi(player: Int): Boolean =
        players.firstOrNull { it.playerNumber == player }?.isAi == true

    companion object {
        const val DOT_ROWS = 5
        const val DOT_COLS = 9
        const val BOX_ROWS = DOT_ROWS - 1 // 4
        const val BOX_COLS = DOT_COLS - 1 // 8
        const val TOTAL_BOXES = BOX_ROWS * BOX_COLS // 32

        fun create(
            humanPlayerCount: Int,
            letters: List<String>
        ): BoardState {
            val coercedCount = humanPlayerCount.coerceIn(1, 4)
            val playerConfigs = if (coercedCount == 1) {
                // 1 Player mode: Player 1 (Human) vs Player 2 (Smart AI)
                val p1Letter = letters.getOrNull(0)?.ifBlank { "A" } ?: "A"
                val aiDefault = letters.getOrNull(1)?.ifBlank { "C" } ?: "C"
                val aiLetter = if (aiDefault.equals(p1Letter, ignoreCase = true)) {
                    if (p1Letter == "X") "O" else "X"
                } else {
                    aiDefault
                }
                listOf(
                    PlayerConfig(1, p1Letter, isAi = false),
                    PlayerConfig(2, aiLetter, isAi = true)
                )
            } else {
                (1..coercedCount).map { idx ->
                    val fallback = ('A' + (idx - 1)).toString()
                    PlayerConfig(
                        playerNumber = idx,
                        letter = letters.getOrNull(idx - 1)?.ifBlank { fallback } ?: fallback,
                        isAi = false
                    )
                }
            }

            return BoardState(
                humanPlayerCount = coercedCount,
                players = playerConfigs,
                currentPlayer = 1,
                scores = playerConfigs.associate { it.playerNumber to 0 }
            )
        }
    }
}

object DotsAndBoxesEngine {

    /**
     * Claims an unclaimed horizontal or vertical edge and performs O(1) adjacent-box
     * edge detection using the 2D arrays rather than scanning the entire board.
     *
     * Combo & Multi-Square Round Tracking:
     * - Whenever a player completes 1 or 2 boxes, `currentComboCount` increases by `capturedCount`
     *   and `comboChainBoxes` accumulates all boxes captured by this player during the current round.
     * - When `currentComboCount >= 2` (either via a simultaneous 2-box double capture OR consecutive
     *   captures across extra turns in the same round), `isMultiSquareCombo` becomes true and
     *   `comboTriggerId` increments so the UI triggers a synchronized pulse + small camera zoom!
     * - When a move captures 0 boxes, `currentComboCount` resets to 0, `comboChainBoxes` clears,
     *   and the turn advances cyclically: `(currentPlayer % activePlayerCount) + 1`.
     */
    fun makeMove(state: BoardState, edge: EdgeCoordinate): MoveOutcome? {
        if (state.isGameOver) return null

        val hMutable = state.horizontalEdges.map { it.toMutableList() }.toMutableList()
        val vMutable = state.verticalEdges.map { it.toMutableList() }.toMutableList()
        val boxesMutable = state.boxes.map { it.toMutableList() }.toMutableList()

        val candidateBoxes = mutableListOf<BoxCoordinate>()

        when (edge.orientation) {
            EdgeOrientation.HORIZONTAL -> {
                if (edge.row !in 0 until BoardState.DOT_ROWS || edge.col !in 0 until BoardState.BOX_COLS) {
                    return null
                }
                if (hMutable[edge.row][edge.col] != 0) return null

                hMutable[edge.row][edge.col] = state.currentPlayer

                if (edge.row > 0) {
                    candidateBoxes.add(BoxCoordinate(edge.row - 1, edge.col))
                }
                if (edge.row < BoardState.BOX_ROWS) {
                    candidateBoxes.add(BoxCoordinate(edge.row, edge.col))
                }
            }

            EdgeOrientation.VERTICAL -> {
                if (edge.row !in 0 until BoardState.BOX_ROWS || edge.col !in 0 until BoardState.DOT_COLS) {
                    return null
                }
                if (vMutable[edge.row][edge.col] != 0) return null

                vMutable[edge.row][edge.col] = state.currentPlayer

                if (edge.col > 0) {
                    candidateBoxes.add(BoxCoordinate(edge.row, edge.col - 1))
                }
                if (edge.col < BoardState.BOX_COLS) {
                    candidateBoxes.add(BoxCoordinate(edge.row, edge.col))
                }
            }
        }

        val newlyCaptured = mutableListOf<BoxCoordinate>()
        for (box in candidateBoxes) {
            if (boxesMutable[box.row][box.col] == 0 &&
                isBoxCompleted(box.row, box.col, hMutable, vMutable)
            ) {
                boxesMutable[box.row][box.col] = state.currentPlayer
                newlyCaptured.add(box)
            }
        }

        val capturedCount = newlyCaptured.size
        val updatedScores = state.scores.toMutableMap()
        updatedScores[state.currentPlayer] =
            (updatedScores[state.currentPlayer] ?: 0) + capturedCount

        // Grant a mandatory extra turn if at least 1 box was captured; otherwise advance to next player (1..N)
        val grantedExtraTurn = capturedCount > 0
        val nextPlayer = if (grantedExtraTurn) {
            state.currentPlayer
        } else {
            (state.currentPlayer % state.activePlayerCount) + 1
        }

        val newComboCount = if (grantedExtraTurn) state.currentComboCount + capturedCount else 0
        val newComboChain = if (grantedExtraTurn) {
            state.comboChainBoxes + newlyCaptured
        } else {
            emptySet()
        }
        val newComboTriggerId = if (grantedExtraTurn) {
            state.comboTriggerId + 1
        } else {
            state.comboTriggerId
        }

        val updatedState = state.copy(
            currentPlayer = nextPlayer,
            scores = updatedScores,
            horizontalEdges = hMutable.map { it.toList() },
            verticalEdges = vMutable.map { it.toList() },
            boxes = boxesMutable.map { it.toList() },
            lastClaimedEdge = edge,
            lastCapturedBoxes = newlyCaptured.toSet(),
            comboChainBoxes = newComboChain,
            currentComboCount = newComboCount,
            comboTriggerId = newComboTriggerId,
            hasExtraTurnStreak = grantedExtraTurn,
            totalMovesPlayed = state.totalMovesPlayed + 1
        )

        return MoveOutcome(
            newState = updatedState,
            capturedBoxes = newlyCaptured,
            grantedExtraTurn = grantedExtraTurn
        )
    }

    /**
     * Smart AI move selector for 1-Player mode:
     * 1. Priority 1: Any move that immediately captures 2 boxes (double-capture combo).
     * 2. Priority 2: Any move that immediately captures 1 box.
     * 3. Priority 3: "Safe" moves that do not create a 3-sided box for the opponent.
     * 4. Priority 4: Fallback to any available edge if all remaining moves give a 3rd edge.
     */
    fun chooseSmartAiMove(state: BoardState): EdgeCoordinate? {
        if (state.isGameOver) return null

        val availableEdges = mutableListOf<EdgeCoordinate>()
        for (r in 0 until BoardState.DOT_ROWS) {
            for (c in 0 until BoardState.BOX_COLS) {
                if (state.horizontalEdges[r][c] == 0) {
                    availableEdges.add(EdgeCoordinate(EdgeOrientation.HORIZONTAL, r, c))
                }
            }
        }
        for (r in 0 until BoardState.BOX_ROWS) {
            for (c in 0 until BoardState.DOT_COLS) {
                if (state.verticalEdges[r][c] == 0) {
                    availableEdges.add(EdgeCoordinate(EdgeOrientation.VERTICAL, r, c))
                }
            }
        }
        if (availableEdges.isEmpty()) return null

        var bestCaptureEdge: EdgeCoordinate? = null
        var bestCaptureCount = 0
        val safeEdges = mutableListOf<EdgeCoordinate>()

        for (edge in availableEdges) {
            val adjacentBoxes = getAdjacentBoxes(edge)
            var completesCount = 0
            var createsThirdSide = false

            for (box in adjacentBoxes) {
                val currentSides = countClaimedSides(
                    box.row,
                    box.col,
                    state.horizontalEdges,
                    state.verticalEdges
                )
                if (currentSides == 3) {
                    completesCount++
                } else if (currentSides == 2) {
                    createsThirdSide = true
                }
            }

            if (completesCount > bestCaptureCount) {
                bestCaptureCount = completesCount
                bestCaptureEdge = edge
            } else if (completesCount == 0 && !createsThirdSide) {
                safeEdges.add(edge)
            }
        }

        if (bestCaptureEdge != null) return bestCaptureEdge
        if (safeEdges.isNotEmpty()) return safeEdges.random()
        return availableEdges.random()
    }

    private fun getAdjacentBoxes(edge: EdgeCoordinate): List<BoxCoordinate> {
        val result = mutableListOf<BoxCoordinate>()
        when (edge.orientation) {
            EdgeOrientation.HORIZONTAL -> {
                if (edge.row > 0) result.add(BoxCoordinate(edge.row - 1, edge.col))
                if (edge.row < BoardState.BOX_ROWS) result.add(BoxCoordinate(edge.row, edge.col))
            }
            EdgeOrientation.VERTICAL -> {
                if (edge.col > 0) result.add(BoxCoordinate(edge.row, edge.col - 1))
                if (edge.col < BoardState.BOX_COLS) result.add(BoxCoordinate(edge.row, edge.col))
            }
        }
        return result
    }

    private fun countClaimedSides(
        boxRow: Int,
        boxCol: Int,
        hEdges: List<List<Int>>,
        vEdges: List<List<Int>>
    ): Int {
        var count = 0
        if (hEdges[boxRow][boxCol] != 0) count++
        if (hEdges[boxRow + 1][boxCol] != 0) count++
        if (vEdges[boxRow][boxCol] != 0) count++
        if (vEdges[boxRow][boxCol + 1] != 0) count++
        return count
    }

    private fun isBoxCompleted(
        boxRow: Int,
        boxCol: Int,
        hEdges: List<List<Int>>,
        vEdges: List<List<Int>>
    ): Boolean = countClaimedSides(boxRow, boxCol, hEdges, vEdges) == 4
}
