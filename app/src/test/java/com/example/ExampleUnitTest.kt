package com.example
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun dynamicGridSizes_scaleMatricesCorrectlyFrom3x3To6x6() {
        for (grid in GridSize.entries) {
            val state = BoardState.create(
                humanPlayerCount = 1,
                letters = listOf("A", "C"),
                cpuDifficulty = CpuDifficulty.MEDIUM,
                boxRows = grid.boxRows,
                boxCols = grid.boxCols
            )
            assertEquals(grid.boxRows + 1, state.horizontalEdges.size)
            assertEquals(grid.boxCols, state.horizontalEdges[0].size)
            assertEquals(grid.boxRows, state.verticalEdges.size)
            assertEquals(grid.boxCols + 1, state.verticalEdges[0].size)
            assertEquals(grid.boxRows, state.boxes.size)
            assertEquals(grid.boxCols, state.boxes[0].size)
            assertEquals(grid.boxRows * grid.boxCols, state.totalBoxes)
        }
    }

    @Test
    fun campaignLevels_followSixStageLinearProgression() {
        val levels = CampaignLevel.entries
        assertEquals(6, levels.size)

        assertEquals(GridSize.SIZE_3X3, CampaignLevel.LEVEL_1.gridSize)
        assertEquals(CpuDifficulty.EASY, CampaignLevel.LEVEL_1.difficulty)

        assertEquals(GridSize.SIZE_4X4, CampaignLevel.LEVEL_2.gridSize)
        assertEquals(CpuDifficulty.EASY, CampaignLevel.LEVEL_2.difficulty)

        assertEquals(GridSize.SIZE_4X4, CampaignLevel.LEVEL_3.gridSize)
        assertEquals(CpuDifficulty.MEDIUM, CampaignLevel.LEVEL_3.difficulty)

        assertEquals(GridSize.SIZE_5X5, CampaignLevel.LEVEL_4.gridSize)
        assertEquals(CpuDifficulty.MEDIUM, CampaignLevel.LEVEL_4.difficulty)

        assertEquals(GridSize.SIZE_5X5, CampaignLevel.LEVEL_5.gridSize)
        assertEquals(CpuDifficulty.HARD, CampaignLevel.LEVEL_5.difficulty)

        assertEquals(GridSize.SIZE_6X6, CampaignLevel.LEVEL_6.gridSize)
        assertEquals(CpuDifficulty.HARD, CampaignLevel.LEVEL_6.difficulty)
    }

    @Test
    fun blitzTimeout_forfeitsTurnImmediatelyToOpponent() {
        val state = BoardState.create(
            humanPlayerCount = 1,
            letters = listOf("A", "C"),
            cpuDifficulty = CpuDifficulty.MEDIUM,
            boxRows = 3,
            boxCols = 3
        )
        assertEquals(1, state.currentPlayer)
        val afterForfeit = DotsAndBoxesEngine.forfeitTurn(state)
        assertEquals(2, afterForfeit.currentPlayer)
        assertEquals(0, afterForfeit.currentComboCount)
        assertFalse(afterForfeit.hasExtraTurnStreak)
    }

    @Test
    fun minimaxDepth_isThreeForUpTo4x4_andTwoFor5x5Plus() {
        assertEquals(3, DotsAndBoxesEngine.minimaxSearchDepth(3, 3))
        assertEquals(3, DotsAndBoxesEngine.minimaxSearchDepth(4, 4))
        assertEquals(2, DotsAndBoxesEngine.minimaxSearchDepth(5, 5))
        assertEquals(2, DotsAndBoxesEngine.minimaxSearchDepth(6, 6))
    }

    @Test
    fun completingSingleBox_capturesBoxAndGrantsExtraTurn() {
        var state = BoardState.create(2, listOf("X", "O"), boxRows = 4, boxCols = 4)

        state = DotsAndBoxesEngine.makeMove(
            state,
            EdgeCoordinate(EdgeOrientation.HORIZONTAL, 0, 0)
        )!!.newState
        assertEquals(2, state.currentPlayer)

        state = DotsAndBoxesEngine.makeMove(
            state,
            EdgeCoordinate(EdgeOrientation.HORIZONTAL, 1, 0)
        )!!.newState
        assertEquals(1, state.currentPlayer)

        state = DotsAndBoxesEngine.makeMove(
            state,
            EdgeCoordinate(EdgeOrientation.VERTICAL, 0, 0)
        )!!.newState
        assertEquals(2, state.currentPlayer)

        val outcome = DotsAndBoxesEngine.makeMove(
            state,
            EdgeCoordinate(EdgeOrientation.VERTICAL, 0, 1)
        )
        assertNotNull(outcome)
        assertTrue(outcome!!.grantedExtraTurn)
        assertEquals(1, outcome.capturedBoxes.size)
        assertEquals(1, outcome.newState.player2Score)
        assertEquals(2, outcome.newState.currentPlayer)
        assertEquals(2, outcome.newState.boxes[0][0])
        assertEquals(1, outcome.newState.currentComboCount)
        assertFalse(outcome.newState.isMultiSquareCombo)

        assertNull(
            DotsAndBoxesEngine.makeMove(
                outcome.newState,
                EdgeCoordinate(EdgeOrientation.VERTICAL, 0, 1)
            )
        )
    }

    @Test
    fun completingTwoBoxesSimultaneously_triggersMultiSquareComboAndOneExtraTurn() {
        var state = BoardState.create(2, listOf("A", "B"), boxRows = 4, boxCols = 4)
        val moves = listOf(
            EdgeCoordinate(EdgeOrientation.HORIZONTAL, 0, 0),
            EdgeCoordinate(EdgeOrientation.HORIZONTAL, 1, 0),
            EdgeCoordinate(EdgeOrientation.VERTICAL, 0, 0),
            EdgeCoordinate(EdgeOrientation.HORIZONTAL, 0, 1),
            EdgeCoordinate(EdgeOrientation.HORIZONTAL, 1, 1),
            EdgeCoordinate(EdgeOrientation.VERTICAL, 0, 2)
        )
        for (edge in moves) {
            state = DotsAndBoxesEngine.makeMove(state, edge)!!.newState
        }
        assertEquals(1, state.currentPlayer)

        val outcome = DotsAndBoxesEngine.makeMove(
            state,
            EdgeCoordinate(EdgeOrientation.VERTICAL, 0, 1)
        )!!
        assertEquals(2, outcome.capturedBoxes.size)
        assertEquals(2, outcome.newState.player1Score)
        assertEquals(1, outcome.newState.currentPlayer)
        assertTrue(outcome.grantedExtraTurn)
        assertEquals(2, outcome.newState.currentComboCount)
        assertTrue(outcome.newState.isMultiSquareCombo)
        assertEquals(2, outcome.newState.comboChainBoxes.size)
        assertEquals(1, outcome.newState.comboTriggerId)
    }

    @Test
    fun cpuDifficulties_easyRandom_mediumGreedySafe_hardMinimaxAlphaBeta() {
        var state = BoardState.create(
            humanPlayerCount = 1,
            letters = listOf("A", "C"),
            cpuDifficulty = CpuDifficulty.HARD,
            boxRows = 3,
            boxCols = 3
        )
        assertEquals("C", state.player2Letter)
        assertTrue(state.isPlayerAi(2))

        // Set up 2 sides of box (0,0): top (0,0) and bottom (1,0)
        state = DotsAndBoxesEngine.makeMove(state, EdgeCoordinate(EdgeOrientation.HORIZONTAL, 0, 0))!!.newState
        state = DotsAndBoxesEngine.makeMove(state, EdgeCoordinate(EdgeOrientation.HORIZONTAL, 1, 0))!!.newState

        // Easy selects any valid un-drawn line
        val easyMove = DotsAndBoxesEngine.cpuTakeTurn(state, CpuDifficulty.EASY)
        assertNotNull(easyMove)
        assertNotNull(DotsAndBoxesEngine.makeMove(state, easyMove!!))

        // Medium (Greedy & Safe) and Hard (Minimax Alpha-Beta depth 3 on 3x3) must avoid completing the 3rd side of box (0,0)
        val mediumMove = DotsAndBoxesEngine.cpuTakeTurn(state, CpuDifficulty.MEDIUM)!!
        assertNotEquals(EdgeCoordinate(EdgeOrientation.VERTICAL, 0, 0), mediumMove)
        assertNotEquals(EdgeCoordinate(EdgeOrientation.VERTICAL, 0, 1), mediumMove)

        val hardMove = DotsAndBoxesEngine.cpuTakeTurn(state, CpuDifficulty.HARD)!!
        assertNotEquals(EdgeCoordinate(EdgeOrientation.VERTICAL, 0, 0), hardMove)
        assertNotEquals(EdgeCoordinate(EdgeOrientation.VERTICAL, 0, 1), hardMove)

        // Now add the 3rd side of box (0,0): VERTICAL (0,0), so box (0,0) has 3 walls
        state = DotsAndBoxesEngine.makeMove(state, EdgeCoordinate(EdgeOrientation.VERTICAL, 0, 0))!!.newState

        // Medium and Hard must take the 4th side VERTICAL (0,1)
        val expectedCapture = EdgeCoordinate(EdgeOrientation.VERTICAL, 0, 1)
        assertEquals(expectedCapture, DotsAndBoxesEngine.cpuTakeTurn(state, CpuDifficulty.MEDIUM))
        assertEquals(expectedCapture, DotsAndBoxesEngine.cpuTakeTurn(state, CpuDifficulty.HARD))
    }
}
