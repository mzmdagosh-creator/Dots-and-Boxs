package com.example.model

/**
 * Pure Kotlin 2D-array engine for the 5x9 Dots and Boxes board game supporting:
 * - "Player vs Player" (PvP, 2 to 4 players) and "Player vs Computer (CPU)"
 * - Three CPU Difficulty tiers: `EASY` (Naive), `MEDIUM` (Careful), and `HARD` (Competitive Strategy)
 * - Multi-square round combo tracking (`currentComboCount`, `comboChainBoxes`, `comboTriggerId`)
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

enum class GameMode {
    PVP,
    CPU
}

enum class CpuDifficulty {
    EASY,
    MEDIUM,
    HARD
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
    val gameMode: GameMode = GameMode.PVP,
    val cpuDifficulty: CpuDifficulty = CpuDifficulty.MEDIUM,
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
            letters: List<String>,
            cpuDifficulty: CpuDifficulty = CpuDifficulty.MEDIUM
        ): BoardState {
            val coercedCount = humanPlayerCount.coerceIn(1, 4)
            val mode = if (coercedCount == 1) GameMode.CPU else GameMode.PVP
            val playerConfigs = if (coercedCount == 1) {
                // Player vs Computer (CPU) mode: Player 1 (Human) vs Player 2 (Locked CPU 'C' or '🤖')
                val p1Letter = letters.getOrNull(0)?.ifBlank { "A" } ?: "A"
                val cpuLetter = if (p1Letter.equals("C", ignoreCase = true)) "🤖" else "C"
                listOf(
                    PlayerConfig(1, p1Letter, isAi = false),
                    PlayerConfig(2, cpuLetter, isAi = true)
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
                gameMode = mode,
                cpuDifficulty = cpuDifficulty,
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
     * Reusable CPU AI heuristic selector (`cpuTakeTurn`) supporting Easy, Medium, and Hard tiers.
     */
    fun cpuTakeTurn(
        state: BoardState,
        difficulty: CpuDifficulty = state.cpuDifficulty
    ): EdgeCoordinate? {
        if (state.isGameOver) return null

        val availableEdges = getAllAvailableEdges(state.horizontalEdges, state.verticalEdges)
        if (availableEdges.isEmpty()) return null

        return when (difficulty) {
            CpuDifficulty.EASY -> chooseEasyCpuMove(state, availableEdges)
            CpuDifficulty.MEDIUM -> chooseMediumCpuMove(state, availableEdges)
            CpuDifficulty.HARD -> chooseHardCpuMove(state, availableEdges)
        }
    }

    fun chooseSmartAiMove(state: BoardState): EdgeCoordinate? =
        cpuTakeTurn(state, state.cpuDifficulty)

    /**
     * Easy (Naive):
     * - Prioritizes completing any 4th side (capturing a box) if a scoring move is immediately available.
     * - If no scoring move is available, randomly picks any unselected line on the entire board.
     */
    private fun chooseEasyCpuMove(
        state: BoardState,
        availableEdges: List<EdgeCoordinate>
    ): EdgeCoordinate {
        val scoringMoves = availableEdges.filter { edge ->
            getAdjacentBoxes(edge).any { box ->
                countClaimedSides(box.row, box.col, state.horizontalEdges, state.verticalEdges) == 3
            }
        }
        if (scoringMoves.isNotEmpty()) return scoringMoves.random()
        return availableEdges.random()
    }

    /**
     * Medium (Careful):
     * - Prioritizes scoring (completing a 4th side) first (preferring double-box captures).
     * - If no scoring move is available, scans the board and ACTIVELY AVOIDS making a move
     *   that would complete the 3rd side of any box.
     * - If all available lines inevitably complete a 3rd side, picks randomly from the safest
     *   possible lines (exposing 1 box rather than 2).
     */
    private fun chooseMediumCpuMove(
        state: BoardState,
        availableEdges: List<EdgeCoordinate>
    ): EdgeCoordinate {
        val doubleCaptures = mutableListOf<EdgeCoordinate>()
        val singleCaptures = mutableListOf<EdgeCoordinate>()
        val safeMoves = mutableListOf<EdgeCoordinate>()
        val singleRiskyMoves = mutableListOf<EdgeCoordinate>()
        val doubleRiskyMoves = mutableListOf<EdgeCoordinate>()

        for (edge in availableEdges) {
            val adj = getAdjacentBoxes(edge)
            var completable = 0
            var thirdSidesCreated = 0

            for (box in adj) {
                val sides = countClaimedSides(
                    box.row,
                    box.col,
                    state.horizontalEdges,
                    state.verticalEdges
                )
                if (sides == 3) completable++
                else if (sides == 2) thirdSidesCreated++
            }

            when {
                completable >= 2 -> doubleCaptures.add(edge)
                completable == 1 -> singleCaptures.add(edge)
                thirdSidesCreated == 0 -> safeMoves.add(edge)
                thirdSidesCreated == 1 -> singleRiskyMoves.add(edge)
                else -> doubleRiskyMoves.add(edge)
            }
        }

        if (doubleCaptures.isNotEmpty()) return doubleCaptures.random()
        if (singleCaptures.isNotEmpty()) return singleCaptures.random()
        if (safeMoves.isNotEmpty()) return safeMoves.random()
        if (singleRiskyMoves.isNotEmpty()) return singleRiskyMoves.random()
        return doubleRiskyMoves.random()
    }

    /**
     * Hard (Competitive Strategy):
     * - Prioritizes scoring first, evaluating if a scoring move unlocks consecutive scoring cascades
     *   (where the other adjacent box has 2 sides, chaining into another capture) or double-captures.
     * - Actively avoids setting up 3rd sides, preferring safe lines bordering boxes with fewer sides.
     * - If forced to make a "bad" move (giving the opponent a box), simulates the opponent's
     *   consecutive chain sweep on a cloned 2D board for every candidate move and picks the move
     *   that gives away the SHORTEST chain of boxes (minimizing damage).
     */
    private fun chooseHardCpuMove(
        state: BoardState,
        availableEdges: List<EdgeCoordinate>
    ): EdgeCoordinate {
        data class ScoredMove(val edge: EdgeCoordinate, val priority: Int)

        val scoringMoves = mutableListOf<ScoredMove>()
        val safeMoves = mutableListOf<ScoredMove>()
        val riskyMoves = mutableListOf<EdgeCoordinate>()

        for (edge in availableEdges) {
            val adj = getAdjacentBoxes(edge)
            var completable = 0
            var createsThird = 0
            var existingSidesSum = 0

            for (box in adj) {
                val sides = countClaimedSides(
                    box.row,
                    box.col,
                    state.horizontalEdges,
                    state.verticalEdges
                )
                existingSidesSum += sides
                if (sides == 3) completable++
                else if (sides == 2) createsThird++
            }

            if (completable > 0) {
                val cascadeBonus = if (createsThird > 0) 5 else 0
                scoringMoves.add(ScoredMove(edge, completable * 10 + cascadeBonus))
            } else if (createsThird == 0) {
                safeMoves.add(ScoredMove(edge, -existingSidesSum))
            } else {
                riskyMoves.add(edge)
            }
        }

        if (scoringMoves.isNotEmpty()) {
            val maxPriority = scoringMoves.maxOf { it.priority }
            return scoringMoves.filter { it.priority == maxPriority }.random().edge
        }

        if (safeMoves.isNotEmpty()) {
            val bestScore = safeMoves.maxOf { it.priority }
            return safeMoves.filter { it.priority == bestScore }.random().edge
        }

        // Forced sacrifice: evaluate opponent chain length for each risky move and minimize damage
        var minChainDamage = Int.MAX_VALUE
        val bestSacrifices = mutableListOf<EdgeCoordinate>()

        for (edge in riskyMoves) {
            val chainDamage = simulateOpponentChainDamage(
                state.horizontalEdges,
                state.verticalEdges,
                edge
            )
            if (chainDamage < minChainDamage) {
                minChainDamage = chainDamage
                bestSacrifices.clear()
                bestSacrifices.add(edge)
            } else if (chainDamage == minChainDamage) {
                bestSacrifices.add(edge)
            }
        }

        return bestSacrifices.random()
    }

    /**
     * Simulates making `candidateEdge` on a copy of the 2D board and counts how many
     * consecutive 3-sided boxes the opponent can immediately capture in a chain sweep.
     */
    fun simulateOpponentChainDamage(
        hEdges: List<List<Int>>,
        vEdges: List<List<Int>>,
        candidateEdge: EdgeCoordinate
    ): Int {
        val simH = hEdges.map { it.toMutableList() }.toMutableList()
        val simV = vEdges.map { it.toMutableList() }.toMutableList()

        fun markEdge(edge: EdgeCoordinate) {
            if (edge.orientation == EdgeOrientation.HORIZONTAL) {
                simH[edge.row][edge.col] = 9
            } else {
                simV[edge.row][edge.col] = 9
            }
        }

        markEdge(candidateEdge)

        var totalBoxesGivenAway = 0
        var foundCompletableBox = true

        while (foundCompletableBox) {
            foundCompletableBox = false
            for (r in 0 until BoardState.BOX_ROWS) {
                for (c in 0 until BoardState.BOX_COLS) {
                    if (countClaimedSides(r, c, simH, simV) == 3) {
                        val fourthEdge = findFourthUnclaimedEdge(r, c, simH, simV)
                        if (fourthEdge != null) {
                            // Count how many 3-sided boxes this 4th edge completes (1 or 2)
                            var newlyCompleted = 0
                            for (adj in getAdjacentBoxes(fourthEdge)) {
                                if (countClaimedSides(adj.row, adj.col, simH, simV) == 3) {
                                    newlyCompleted++
                                }
                            }
                            markEdge(fourthEdge)
                            totalBoxesGivenAway += newlyCompleted
                            foundCompletableBox = true
                        }
                    }
                }
            }
        }
        return totalBoxesGivenAway
    }

    private fun findFourthUnclaimedEdge(
        boxRow: Int,
        boxCol: Int,
        hEdges: List<List<Int>>,
        vEdges: List<List<Int>>
    ): EdgeCoordinate? {
        if (hEdges[boxRow][boxCol] == 0) {
            return EdgeCoordinate(EdgeOrientation.HORIZONTAL, boxRow, boxCol)
        }
        if (hEdges[boxRow + 1][boxCol] == 0) {
            return EdgeCoordinate(EdgeOrientation.HORIZONTAL, boxRow + 1, boxCol)
        }
        if (vEdges[boxRow][boxCol] == 0) {
            return EdgeCoordinate(EdgeOrientation.VERTICAL, boxRow, boxCol)
        }
        if (vEdges[boxRow][boxCol + 1] == 0) {
            return EdgeCoordinate(EdgeOrientation.VERTICAL, boxRow, boxCol + 1)
        }
        return null
    }

    private fun getAllAvailableEdges(
        hEdges: List<List<Int>>,
        vEdges: List<List<Int>>
    ): List<EdgeCoordinate> {
        val list = mutableListOf<EdgeCoordinate>()
        for (r in 0 until BoardState.DOT_ROWS) {
            for (c in 0 until BoardState.BOX_COLS) {
                if (hEdges[r][c] == 0) list.add(EdgeCoordinate(EdgeOrientation.HORIZONTAL, r, c))
            }
        }
        for (r in 0 until BoardState.BOX_ROWS) {
            for (c in 0 until BoardState.DOT_COLS) {
                if (vEdges[r][c] == 0) list.add(EdgeCoordinate(EdgeOrientation.VERTICAL, r, c))
            }
        }
        return list
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
