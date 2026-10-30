package com.example.model

import kotlin.math.max
import kotlin.math.min

/**
 * Dynamic 2D-matrix engine for the native Android Dots and Boxes game supporting:
 * - Dynamic Grid Sizes from 3x3 up to 6x6 boxes (`GridSize.SIZE_3X3`, `SIZE_4X4`, `SIZE_5X5`, `SIZE_6X6`)
 * - Single Player vs Computer (CPU) and Multi-Player PvP
 * - Three CPU Difficulty tiers:
 *   - `EASY`: Selects a valid un-drawn line completely at random.
 *   - `MEDIUM`: Greedy and Safe approach. Completes any 3-sided box if available; otherwise strictly
 *     avoids drawing the 3rd line of any box; picks randomly if no safe moves exist.
 *   - `HARD`: Minimax algorithm with Alpha-Beta Pruning looking ahead to maximize the computer's
 *     score while minimizing the player's score, using dynamic depth (depth 3 for 3x3 grids,
 *     depth 2 for larger grids 4x4, 5x5, 6x6).
 */
enum class EdgeOrientation {
    HORIZONTAL,
    VERTICAL
}

enum class GameMode {
    PVP,
    CPU
}

enum class CpuDifficulty(val label: String) {
    EASY("Easy"),
    MEDIUM("Medium"),
    HARD("Hard")
}

enum class GridSize(
    val boxRows: Int,
    val boxCols: Int,
    val label: String
) {
    SIZE_3X3(3, 3, "3×3"),
    SIZE_4X4(4, 4, "4×4"),
    SIZE_5X5(5, 5, "5×5"),
    SIZE_6X6(6, 6, "6×6");

    val dotRows: Int get() = boxRows + 1
    val dotCols: Int get() = boxCols + 1
    val totalBoxes: Int get() = boxRows * boxCols

    companion object {
        fun fromDimensions(rows: Int, cols: Int): GridSize =
            entries.firstOrNull { it.boxRows == rows && it.boxCols == cols } ?: SIZE_4X4
    }
}

/**
 * Timer mode for Quick Match:
 * - `RELAXED`: No turn timer ("Relaxed (Off)")
 * - `BLITZ`: 5-second turn countdown ("Blitz (5s)"). If 5s runs out, player forfeits turn to CPU.
 */
enum class TimerMode(val durationSeconds: Int) {
    RELAXED(0),
    BLITZ(5)
}

/**
 * Tournament Campaign Mode linear progression of 6 levels:
 * - Level 1: 3x3 Grid, Easy AI
 * - Level 2: 4x4 Grid, Easy AI
 * - Level 3: 4x4 Grid, Medium AI
 * - Level 4: 5x5 Grid, Medium AI
 * - Level 5: 5x5 Grid, Hard AI
 * - Level 6: 6x6 Grid, Hard AI
 */
enum class CampaignLevel(
    val levelNumber: Int,
    val title: String,
    val subtitle: String,
    val gridSize: GridSize,
    val difficulty: CpuDifficulty
) {
    LEVEL_1(1, "Level 1", "3×3 Grid • Easy AI", GridSize.SIZE_3X3, CpuDifficulty.EASY),
    LEVEL_2(2, "Level 2", "4×4 Grid • Easy AI", GridSize.SIZE_4X4, CpuDifficulty.EASY),
    LEVEL_3(3, "Level 3", "4×4 Grid • Medium AI", GridSize.SIZE_4X4, CpuDifficulty.MEDIUM),
    LEVEL_4(4, "Level 4", "5×5 Grid • Medium AI", GridSize.SIZE_5X5, CpuDifficulty.MEDIUM),
    LEVEL_5(5, "Level 5", "5×5 Grid • Hard AI", GridSize.SIZE_5X5, CpuDifficulty.HARD),
    LEVEL_6(6, "Level 6", "6×6 Grid • Hard AI", GridSize.SIZE_6X6, CpuDifficulty.HARD);

    fun nextLevel(): CampaignLevel? =
        entries.firstOrNull { it.levelNumber == levelNumber + 1 }

    companion object {
        fun fromNumber(levelNumber: Int): CampaignLevel =
            entries.firstOrNull { it.levelNumber == levelNumber } ?: LEVEL_1
    }
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
    val boxRows: Int = DEFAULT_BOX_ROWS,
    val boxCols: Int = DEFAULT_BOX_COLS,
    val gameMode: GameMode = GameMode.CPU,
    val cpuDifficulty: CpuDifficulty = CpuDifficulty.MEDIUM,
    val humanPlayerCount: Int = 1, // 1 = vs CPU, 2..4 = PvP
    val players: List<PlayerConfig> = listOf(
        PlayerConfig(1, "P1", isAi = false),
        PlayerConfig(2, "CPU", isAi = true)
    ),
    val currentPlayer: Int = 1, // 1..players.size
    val scores: Map<Int, Int> = mapOf(1 to 0, 2 to 0),
    // (boxRows + 1) rows x boxCols cols: 0 = unclaimed, 1..4 = claimed by Player N
    val horizontalEdges: List<List<Int>> = List(boxRows + 1) { List(boxCols) { 0 } },
    // boxRows rows x (boxCols + 1) cols: 0 = unclaimed, 1..4 = claimed by Player N
    val verticalEdges: List<List<Int>> = List(boxRows) { List(boxCols + 1) { 0 } },
    // boxRows rows x boxCols cols: 0 = unclaimed, 1..4 = captured by Player N
    val boxes: List<List<Int>> = List(boxRows) { List(boxCols) { 0 } },
    val lastClaimedEdge: EdgeCoordinate? = null,
    val lastCapturedBoxes: Set<BoxCoordinate> = emptySet(),
    val comboChainBoxes: Set<BoxCoordinate> = emptySet(),
    val currentComboCount: Int = 0,
    val comboTriggerId: Int = 0,
    val hasExtraTurnStreak: Boolean = false,
    val totalMovesPlayed: Int = 0
) {
    val dotRows: Int
        get() = boxRows + 1

    val dotCols: Int
        get() = boxCols + 1

    val totalBoxes: Int
        get() = boxRows * boxCols

    val gridSize: GridSize
        get() = GridSize.fromDimensions(boxRows, boxCols)

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
        get() = totalClaimedBoxes == totalBoxes

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
        const val DEFAULT_BOX_ROWS = 4
        const val DEFAULT_BOX_COLS = 4

        fun create(
            humanPlayerCount: Int = 1,
            letters: List<String> = listOf("A", "C"),
            cpuDifficulty: CpuDifficulty = CpuDifficulty.MEDIUM,
            boxRows: Int = DEFAULT_BOX_ROWS,
            boxCols: Int = DEFAULT_BOX_COLS
        ): BoardState {
            val safeRows = boxRows.coerceIn(2, 10)
            val safeCols = boxCols.coerceIn(2, 10)
            val coercedCount = humanPlayerCount.coerceIn(1, 4)
            val mode = if (coercedCount == 1) GameMode.CPU else GameMode.PVP
            val playerConfigs = if (coercedCount == 1) {
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
                boxRows = safeRows,
                boxCols = safeCols,
                gameMode = mode,
                cpuDifficulty = cpuDifficulty,
                humanPlayerCount = coercedCount,
                players = playerConfigs,
                currentPlayer = 1,
                scores = playerConfigs.associate { it.playerNumber to 0 },
                horizontalEdges = List(safeRows + 1) { List(safeCols) { 0 } },
                verticalEdges = List(safeRows) { List(safeCols + 1) { 0 } },
                boxes = List(safeRows) { List(safeCols) { 0 } }
            )
        }
    }
}

object DotsAndBoxesEngine {

    /**
     * Dynamic Minimax search depth:
     * - Depth 3 for small grids (3x3, 4x4)
     * - Depth 2 for larger grids (5x5, 6x6)
     */
    fun minimaxSearchDepth(boxRows: Int, boxCols: Int): Int =
        if (boxRows <= 4 && boxCols <= 4) 3 else 2

    /**
     * Checks whether the given edge coordinate is valid and unclaimed on the board.
     */
    fun isEdgeAvailable(state: BoardState, edge: EdgeCoordinate): Boolean {
        return when (edge.orientation) {
            EdgeOrientation.HORIZONTAL -> {
                edge.row in 0 until state.dotRows &&
                    edge.col in 0 until state.boxCols &&
                    state.horizontalEdges[edge.row][edge.col] == 0
            }
            EdgeOrientation.VERTICAL -> {
                edge.row in 0 until state.boxRows &&
                    edge.col in 0 until state.dotCols &&
                    state.verticalEdges[edge.row][edge.col] == 0
            }
        }
    }

    /**
     * Forfeits the active player's turn (used when the 5s Blitz timer expires)
     * and passes control immediately to the next player / Computer opponent.
     */
    fun forfeitTurn(state: BoardState): BoardState {
        if (state.isGameOver) return state
        val nextPlayer = (state.currentPlayer % state.activePlayerCount) + 1
        return state.copy(
            currentPlayer = nextPlayer,
            currentComboCount = 0,
            comboChainBoxes = emptySet(),
            lastCapturedBoxes = emptySet(),
            hasExtraTurnStreak = false
        )
    }

    /**
     * Claims an unclaimed horizontal or vertical edge and performs O(1) adjacent-box
     * edge detection using the dynamically sized 2D matrices.
     * When a player completes the 4th wall of a box, they score a point (+1 per box)
     * and MUST take an additional turn (`grantedExtraTurn = true`).
     */
    fun makeMove(state: BoardState, edge: EdgeCoordinate): MoveOutcome? {
        if (state.isGameOver) return null

        val hMutable = state.horizontalEdges.map { it.toMutableList() }.toMutableList()
        val vMutable = state.verticalEdges.map { it.toMutableList() }.toMutableList()
        val boxesMutable = state.boxes.map { it.toMutableList() }.toMutableList()

        val candidateBoxes = mutableListOf<BoxCoordinate>()

        when (edge.orientation) {
            EdgeOrientation.HORIZONTAL -> {
                if (edge.row !in 0 until state.dotRows || edge.col !in 0 until state.boxCols) {
                    return null
                }
                if (hMutable[edge.row][edge.col] != 0) return null

                hMutable[edge.row][edge.col] = state.currentPlayer

                if (edge.row > 0) {
                    candidateBoxes.add(BoxCoordinate(edge.row - 1, edge.col))
                }
                if (edge.row < state.boxRows) {
                    candidateBoxes.add(BoxCoordinate(edge.row, edge.col))
                }
            }

            EdgeOrientation.VERTICAL -> {
                if (edge.row !in 0 until state.boxRows || edge.col !in 0 until state.dotCols) {
                    return null
                }
                if (vMutable[edge.row][edge.col] != 0) return null

                vMutable[edge.row][edge.col] = state.currentPlayer

                if (edge.col > 0) {
                    candidateBoxes.add(BoxCoordinate(edge.row, edge.col - 1))
                }
                if (edge.col < state.boxCols) {
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
     * Selects the Computer Opponent's next line move according to `difficulty`:
     * - `EASY`: Selects a valid un-drawn line completely at random.
     * - `MEDIUM`: Greedy and Safe approach. Always completes a 3-sided box if available.
     *   Otherwise, strictly avoids drawing the 3rd line of any box. Picks randomly if no safe moves exist.
     * - `HARD`: Minimax algorithm with Alpha-Beta Pruning (depth 3 for 3x3 grids, depth 2 for larger grids).
     */
    fun cpuTakeTurn(
        state: BoardState,
        difficulty: CpuDifficulty = state.cpuDifficulty
    ): EdgeCoordinate? {
        if (state.isGameOver) return null

        val availableEdges = getAllAvailableEdges(
            boxRows = state.boxRows,
            boxCols = state.boxCols,
            hEdges = state.horizontalEdges,
            vEdges = state.verticalEdges
        )
        if (availableEdges.isEmpty()) return null

        return when (difficulty) {
            CpuDifficulty.EASY -> chooseEasyCpuMove(availableEdges)
            CpuDifficulty.MEDIUM -> chooseMediumCpuMove(state, availableEdges)
            CpuDifficulty.HARD -> chooseHardMinimaxMove(state, availableEdges)
        }
    }

    /**
     * Easy: Selects a valid un-drawn line completely at random.
     */
    private fun chooseEasyCpuMove(availableEdges: List<EdgeCoordinate>): EdgeCoordinate =
        availableEdges.random()

    /**
     * Medium (Greedy and Safe):
     * - Always completes a 3-sided box if available (preferring double-box captures).
     * - Otherwise, strictly avoids drawing the 3rd line of any box.
     * - Picks randomly if no safe moves exist.
     */
    private fun chooseMediumCpuMove(
        state: BoardState,
        availableEdges: List<EdgeCoordinate>
    ): EdgeCoordinate {
        val doubleCaptures = mutableListOf<EdgeCoordinate>()
        val singleCaptures = mutableListOf<EdgeCoordinate>()
        val safeMoves = mutableListOf<EdgeCoordinate>()
        val riskyMoves = mutableListOf<EdgeCoordinate>()

        for (edge in availableEdges) {
            val adj = getAdjacentBoxes(edge, state.boxRows, state.boxCols)
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
                else -> riskyMoves.add(edge)
            }
        }

        if (doubleCaptures.isNotEmpty()) return doubleCaptures.random()
        if (singleCaptures.isNotEmpty()) return singleCaptures.random()
        if (safeMoves.isNotEmpty()) return safeMoves.random()
        return riskyMoves.random()
    }

    /**
     * Hard: Minimax algorithm with Alpha-Beta Pruning.
     * Uses dynamic search depth:
     * - Depth 3 for 3x3 grids (`boxRows == 3 && boxCols == 3`)
     * - Depth 2 for larger grids (`4x4`, `5x5`, `6x6`)
     */
    private fun chooseHardMinimaxMove(
        state: BoardState,
        availableEdges: List<EdgeCoordinate>
    ): EdgeCoordinate {
        val cpuPlayer = state.currentPlayer
        val humanPlayer = if (cpuPlayer == 2) 1 else 2
        val searchDepth = minimaxSearchDepth(state.boxRows, state.boxCols)

        // Order candidate moves for optimal alpha-beta pruning (captures first, safe second, risky last)
        val orderedMoves = orderMovesForPruning(state, availableEdges).shuffled()
            .sortedByDescending { moveOrderingPriority(state, it) }

        var bestScore = Int.MIN_VALUE
        var alpha = Int.MIN_VALUE
        val beta = Int.MAX_VALUE
        val bestCandidates = mutableListOf<EdgeCoordinate>()

        for (edge in orderedMoves) {
            val outcome = makeMove(state, edge) ?: continue
            val nextIsMaximizing = outcome.newState.currentPlayer == cpuPlayer
            val eval = minimaxAlphaBeta(
                state = outcome.newState,
                depth = searchDepth - 1,
                alpha = alpha,
                beta = beta,
                isMaximizing = nextIsMaximizing,
                cpuPlayer = cpuPlayer,
                humanPlayer = humanPlayer
            )

            if (eval > bestScore) {
                bestScore = eval
                bestCandidates.clear()
                bestCandidates.add(edge)
            } else if (eval == bestScore) {
                bestCandidates.add(edge)
            }
            alpha = max(alpha, bestScore)
        }

        return if (bestCandidates.isNotEmpty()) bestCandidates.random() else availableEdges.random()
    }

    /**
     * Recursive Minimax with Alpha-Beta Pruning accounting for Dots and Boxes bonus turns.
     */
    private fun minimaxAlphaBeta(
        state: BoardState,
        depth: Int,
        alpha: Int,
        beta: Int,
        isMaximizing: Boolean,
        cpuPlayer: Int,
        humanPlayer: Int
    ): Int {
        if (depth <= 0 || state.isGameOver) {
            return evaluateBoardHeuristic(state, cpuPlayer, humanPlayer)
        }

        val available = getAllAvailableEdges(
            boxRows = state.boxRows,
            boxCols = state.boxCols,
            hEdges = state.horizontalEdges,
            vEdges = state.verticalEdges
        )
        if (available.isEmpty()) {
            return evaluateBoardHeuristic(state, cpuPlayer, humanPlayer)
        }

        val ordered = orderMovesForPruning(state, available)
        var currentAlpha = alpha
        var currentBeta = beta

        if (isMaximizing) {
            var maxEval = Int.MIN_VALUE
            for (edge in ordered) {
                val outcome = makeMove(state, edge) ?: continue
                val nextIsMax = outcome.newState.currentPlayer == cpuPlayer
                val eval = minimaxAlphaBeta(
                    state = outcome.newState,
                    depth = depth - 1,
                    alpha = currentAlpha,
                    beta = currentBeta,
                    isMaximizing = nextIsMax,
                    cpuPlayer = cpuPlayer,
                    humanPlayer = humanPlayer
                )
                maxEval = max(maxEval, eval)
                currentAlpha = max(currentAlpha, eval)
                if (currentBeta <= currentAlpha) break // Beta cut-off
            }
            return maxEval
        } else {
            var minEval = Int.MAX_VALUE
            for (edge in ordered) {
                val outcome = makeMove(state, edge) ?: continue
                val nextIsMax = outcome.newState.currentPlayer == cpuPlayer
                val eval = minimaxAlphaBeta(
                    state = outcome.newState,
                    depth = depth - 1,
                    alpha = currentAlpha,
                    beta = currentBeta,
                    isMaximizing = nextIsMax,
                    cpuPlayer = cpuPlayer,
                    humanPlayer = humanPlayer
                )
                minEval = min(minEval, eval)
                currentBeta = min(currentBeta, eval)
                if (currentBeta <= currentAlpha) break // Alpha cut-off
            }
            return minEval
        }
    }

    /**
     * Heuristic board evaluation at leaf nodes:
     * - Primary weight: `(cpuScore - humanScore) * 100`
     * - Secondary tactical weight: any unclaimed 3-sided box on the board belongs to whoever
     *   currently holds the turn (`+35` per 3-sided box if it's CPU's turn, `-35` if it's Human's turn).
     */
    private fun evaluateBoardHeuristic(
        state: BoardState,
        cpuPlayer: Int,
        humanPlayer: Int
    ): Int {
        val cpuScore = state.scoreForPlayer(cpuPlayer)
        val humanScore = state.scoreForPlayer(humanPlayer)
        var scoreDiff = (cpuScore - humanScore) * 100

        if (!state.isGameOver) {
            var threeSidedCount = 0
            for (r in 0 until state.boxRows) {
                for (c in 0 until state.boxCols) {
                    if (state.boxes[r][c] == 0 &&
                        countClaimedSides(r, c, state.horizontalEdges, state.verticalEdges) == 3
                    ) {
                        threeSidedCount++
                    }
                }
            }
            if (threeSidedCount > 0) {
                if (state.currentPlayer == cpuPlayer) {
                    scoreDiff += threeSidedCount * 35
                } else {
                    scoreDiff -= threeSidedCount * 35
                }
            }
        }
        return scoreDiff
    }

    private fun orderMovesForPruning(
        state: BoardState,
        moves: List<EdgeCoordinate>
    ): List<EdgeCoordinate> =
        moves.sortedByDescending { moveOrderingPriority(state, it) }

    private fun moveOrderingPriority(state: BoardState, edge: EdgeCoordinate): Int {
        val adj = getAdjacentBoxes(edge, state.boxRows, state.boxCols)
        var completable = 0
        var createsThird = 0
        for (box in adj) {
            val sides = countClaimedSides(
                box.row,
                box.col,
                state.horizontalEdges,
                state.verticalEdges
            )
            if (sides == 3) completable++
            else if (sides == 2) createsThird++
        }
        return when {
            completable >= 2 -> 30
            completable == 1 -> 20
            createsThird == 0 -> 10
            createsThird == 1 -> -10
            else -> -20
        }
    }

    private fun getAllAvailableEdges(
        boxRows: Int,
        boxCols: Int,
        hEdges: List<List<Int>>,
        vEdges: List<List<Int>>
    ): List<EdgeCoordinate> {
        val list = mutableListOf<EdgeCoordinate>()
        for (r in 0..boxRows) {
            for (c in 0 until boxCols) {
                if (hEdges[r][c] == 0) list.add(EdgeCoordinate(EdgeOrientation.HORIZONTAL, r, c))
            }
        }
        for (r in 0 until boxRows) {
            for (c in 0..boxCols) {
                if (vEdges[r][c] == 0) list.add(EdgeCoordinate(EdgeOrientation.VERTICAL, r, c))
            }
        }
        return list
    }

    private fun getAdjacentBoxes(
        edge: EdgeCoordinate,
        boxRows: Int,
        boxCols: Int
    ): List<BoxCoordinate> {
        val result = mutableListOf<BoxCoordinate>()
        when (edge.orientation) {
            EdgeOrientation.HORIZONTAL -> {
                if (edge.row > 0) result.add(BoxCoordinate(edge.row - 1, edge.col))
                if (edge.row < boxRows) result.add(BoxCoordinate(edge.row, edge.col))
            }
            EdgeOrientation.VERTICAL -> {
                if (edge.col > 0) result.add(BoxCoordinate(edge.row, edge.col - 1))
                if (edge.col < boxCols) result.add(BoxCoordinate(edge.row, edge.col))
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
