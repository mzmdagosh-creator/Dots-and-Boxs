package com.example.model

/**
 * Pure Kotlin 2D-array engine for the 5x9 Dots and Boxes board game.
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

data class MoveOutcome(
    val newState: BoardState,
    val capturedBoxes: List<BoxCoordinate>,
    val grantedExtraTurn: Boolean
)

data class BoardState(
    val player1Letter: String = "A",
    val player2Letter: String = "B",
    val currentPlayer: Int = 1, // 1 or 2
    val player1Score: Int = 0,
    val player2Score: Int = 0,
    // 5 rows x 8 cols: 0 = unclaimed, 1 = claimed by P1, 2 = claimed by P2
    val horizontalEdges: List<List<Int>> = List(DOT_ROWS) { List(BOX_COLS) { 0 } },
    // 4 rows x 9 cols: 0 = unclaimed, 1 = claimed by P1, 2 = claimed by P2
    val verticalEdges: List<List<Int>> = List(BOX_ROWS) { List(DOT_COLS) { 0 } },
    // 4 rows x 8 cols: 0 = unclaimed, 1 = captured by P1, 2 = captured by P2
    val boxes: List<List<Int>> = List(BOX_ROWS) { List(BOX_COLS) { 0 } },
    val lastClaimedEdge: EdgeCoordinate? = null,
    val lastCapturedBoxes: Set<BoxCoordinate> = emptySet(),
    val hasExtraTurnStreak: Boolean = false,
    val totalMovesPlayed: Int = 0
) {
    val totalClaimedBoxes: Int
        get() = player1Score + player2Score

    val isGameOver: Boolean
        get() = totalClaimedBoxes == TOTAL_BOXES

    val winner: Int?
        get() = when {
            !isGameOver -> null
            player1Score > player2Score -> 1
            player2Score > player1Score -> 2
            else -> 0 // Draw
        }

    fun letterForPlayer(player: Int): String = if (player == 1) player1Letter else player2Letter

    companion object {
        const val DOT_ROWS = 5
        const val DOT_COLS = 9
        const val BOX_ROWS = DOT_ROWS - 1 // 4
        const val BOX_COLS = DOT_COLS - 1 // 8
        const val TOTAL_BOXES = BOX_ROWS * BOX_COLS // 32
    }
}

object DotsAndBoxesEngine {

    /**
     * Claims an unclaimed horizontal or vertical edge and performs O(1) adjacent-box
     * edge detection using the 2D arrays rather than scanning the entire board.
     *
     * Edge-Detection Logic:
     * 1. Horizontal edge at (r, c):
     *    - Borders the box ABOVE at (r - 1, c) when r > 0.
     *    - Borders the box BELOW at (r, c) when r < BOX_ROWS (4).
     * 2. Vertical edge at (r, c):
     *    - Borders the box to the LEFT at (r, c - 1) when c > 0.
     *    - Borders the box to the RIGHT at (r, c) when c < BOX_COLS (8).
     * 3. A box at (br, bc) is completed if and only if all 4 of its perimeter lines are non-zero:
     *    - Top:    horizontalEdges[br][bc] != 0
     *    - Bottom: horizontalEdges[br + 1][bc] != 0
     *    - Left:   verticalEdges[br][bc] != 0
     *    - Right:  verticalEdges[br][bc + 1] != 0
     * 4. If 1 or 2 boxes are completed by this single line, all completed boxes are awarded to
     *    `currentPlayer`, their score increases by `captured.size` (1 or 2), and `currentPlayer`
     *    retains the turn (one mandatory extra turn).
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
                // Ignore if already claimed
                if (hMutable[edge.row][edge.col] != 0) return null

                hMutable[edge.row][edge.col] = state.currentPlayer

                // Check adjacent box above (row - 1, col)
                if (edge.row > 0) {
                    candidateBoxes.add(BoxCoordinate(edge.row - 1, edge.col))
                }
                // Check adjacent box below (row, col)
                if (edge.row < BoardState.BOX_ROWS) {
                    candidateBoxes.add(BoxCoordinate(edge.row, edge.col))
                }
            }

            EdgeOrientation.VERTICAL -> {
                if (edge.row !in 0 until BoardState.BOX_ROWS || edge.col !in 0 until BoardState.DOT_COLS) {
                    return null
                }
                // Ignore if already claimed
                if (vMutable[edge.row][edge.col] != 0) return null

                vMutable[edge.row][edge.col] = state.currentPlayer

                // Check adjacent box to the left (row, col - 1)
                if (edge.col > 0) {
                    candidateBoxes.add(BoxCoordinate(edge.row, edge.col - 1))
                }
                // Check adjacent box to the right (row, col)
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
        val newP1Score = state.player1Score + if (state.currentPlayer == 1) capturedCount else 0
        val newP2Score = state.player2Score + if (state.currentPlayer == 2) capturedCount else 0

        // Grant a mandatory extra turn if at least 1 box was captured; otherwise switch player
        val grantedExtraTurn = capturedCount > 0
        val nextPlayer = if (grantedExtraTurn) {
            state.currentPlayer
        } else {
            if (state.currentPlayer == 1) 2 else 1
        }

        val updatedState = state.copy(
            currentPlayer = nextPlayer,
            player1Score = newP1Score,
            player2Score = newP2Score,
            horizontalEdges = hMutable.map { it.toList() },
            verticalEdges = vMutable.map { it.toList() },
            boxes = boxesMutable.map { it.toList() },
            lastClaimedEdge = edge,
            lastCapturedBoxes = newlyCaptured.toSet(),
            hasExtraTurnStreak = grantedExtraTurn,
            totalMovesPlayed = state.totalMovesPlayed + 1
        )

        return MoveOutcome(
            newState = updatedState,
            capturedBoxes = newlyCaptured,
            grantedExtraTurn = grantedExtraTurn
        )
    }

    private fun isBoxCompleted(
        boxRow: Int,
        boxCol: Int,
        hEdges: List<List<Int>>,
        vEdges: List<List<Int>>
    ): Boolean {
        val top = hEdges[boxRow][boxCol] != 0
        val bottom = hEdges[boxRow + 1][boxCol] != 0
        val left = vEdges[boxRow][boxCol] != 0
        val right = vEdges[boxRow][boxCol + 1] != 0
        return top && bottom && left && right
    }
}
