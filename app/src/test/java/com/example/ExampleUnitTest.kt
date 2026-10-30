package com.example

import com.example.model.BoardState
import com.example.model.DotsAndBoxesEngine
import com.example.model.EdgeCoordinate
import com.example.model.EdgeOrientation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun boardDimensions_are5x9DotsAnd4x8Boxes() {
        val state = BoardState()
        assertEquals(5, state.horizontalEdges.size)
        assertEquals(8, state.horizontalEdges[0].size)
        assertEquals(4, state.verticalEdges.size)
        assertEquals(9, state.verticalEdges[0].size)
        assertEquals(4, state.boxes.size)
        assertEquals(8, state.boxes[0].size)
    }

    @Test
    fun completingSingleBox_capturesBoxAndGrantsExtraTurn() {
        var state = BoardState.create(2, listOf("X", "O"))

        // P1 claims top (0,0) -> turn passes to P2
        state = DotsAndBoxesEngine.makeMove(
            state,
            EdgeCoordinate(EdgeOrientation.HORIZONTAL, 0, 0)
        )!!.newState
        assertEquals(2, state.currentPlayer)

        // P2 claims bottom (1,0) -> turn passes to P1
        state = DotsAndBoxesEngine.makeMove(
            state,
            EdgeCoordinate(EdgeOrientation.HORIZONTAL, 1, 0)
        )!!.newState
        assertEquals(1, state.currentPlayer)

        // P1 claims left (0,0) -> turn passes to P2
        state = DotsAndBoxesEngine.makeMove(
            state,
            EdgeCoordinate(EdgeOrientation.VERTICAL, 0, 0)
        )!!.newState
        assertEquals(2, state.currentPlayer)

        // P2 claims right (0,1) -> completes box (0,0), P2 score = 1, P2 keeps turn!
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

        // Re-clicking the same edge returns null
        assertNull(
            DotsAndBoxesEngine.makeMove(
                outcome.newState,
                EdgeCoordinate(EdgeOrientation.VERTICAL, 0, 1)
            )
        )
    }

    @Test
    fun completingTwoBoxesSimultaneously_triggersMultiSquareComboAndOneExtraTurn() {
        var state = BoardState.create(2, listOf("A", "B"))
        // Pre-fill 3 sides of box (0,0) and 3 sides of box (0,1), leaving shared vertical edge (0,1) open
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

        // P1 claims shared vertical edge (0,1) -> captures both (0,0) and (0,1)
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
    fun consecutiveCapturesInSameRound_accumulateMultiSquareComboAndResetOnNonCapture() {
        var state = BoardState.create(2, listOf("A", "B"))
        // Set up box (0,0) with 3 sides and box (0,2) with 3 sides
        val setupMoves = listOf(
            EdgeCoordinate(EdgeOrientation.HORIZONTAL, 0, 0),
            EdgeCoordinate(EdgeOrientation.HORIZONTAL, 1, 0),
            EdgeCoordinate(EdgeOrientation.VERTICAL, 0, 0),
            EdgeCoordinate(EdgeOrientation.HORIZONTAL, 0, 2),
            EdgeCoordinate(EdgeOrientation.HORIZONTAL, 1, 2),
            EdgeCoordinate(EdgeOrientation.VERTICAL, 0, 2)
        )
        for (edge in setupMoves) {
            state = DotsAndBoxesEngine.makeMove(state, edge)!!.newState
        }

        // Move 1 by P1: captures box (0,0) -> combo = 1
        state = DotsAndBoxesEngine.makeMove(
            state,
            EdgeCoordinate(EdgeOrientation.VERTICAL, 0, 1)
        )!!.newState
        assertEquals(1, state.currentComboCount)
        assertFalse(state.isMultiSquareCombo)

        // Move 2 by P1 (extra turn): captures box (0,2) -> combo = 2 (Multi-Square Combo!)
        state = DotsAndBoxesEngine.makeMove(
            state,
            EdgeCoordinate(EdgeOrientation.VERTICAL, 0, 3)
        )!!.newState
        assertEquals(2, state.currentComboCount)
        assertTrue(state.isMultiSquareCombo)
        assertEquals(2, state.comboChainBoxes.size)

        // Move 3 by P1 (extra turn): non-capturing move -> combo resets to 0 and turn passes to P2
        state = DotsAndBoxesEngine.makeMove(
            state,
            EdgeCoordinate(EdgeOrientation.HORIZONTAL, 4, 7)
        )!!.newState
        assertEquals(0, state.currentComboCount)
        assertFalse(state.isMultiSquareCombo)
        assertTrue(state.comboChainBoxes.isEmpty())
        assertEquals(2, state.currentPlayer)
    }

    @Test
    fun fourPlayersMode_rotatesTurnsFrom1Through4() {
        var state = BoardState.create(4, listOf("A", "B", "C", "D"))
        assertEquals(4, state.activePlayerCount)
        assertEquals(1, state.currentPlayer)

        state = DotsAndBoxesEngine.makeMove(
            state,
            EdgeCoordinate(EdgeOrientation.HORIZONTAL, 0, 0)
        )!!.newState
        assertEquals(2, state.currentPlayer)

        state = DotsAndBoxesEngine.makeMove(
            state,
            EdgeCoordinate(EdgeOrientation.HORIZONTAL, 0, 1)
        )!!.newState
        assertEquals(3, state.currentPlayer)

        state = DotsAndBoxesEngine.makeMove(
            state,
            EdgeCoordinate(EdgeOrientation.HORIZONTAL, 0, 2)
        )!!.newState
        assertEquals(4, state.currentPlayer)

        state = DotsAndBoxesEngine.makeMove(
            state,
            EdgeCoordinate(EdgeOrientation.HORIZONTAL, 0, 3)
        )!!.newState
        assertEquals(1, state.currentPlayer)
    }

    @Test
    fun singlePlayerMode_configuresAiOpponentAndSelectsCaptureMove() {
        var state = BoardState.create(1, listOf("A", "C"))
        assertEquals(2, state.activePlayerCount)
        assertFalse(state.isPlayerAi(1))
        assertTrue(state.isPlayerAi(2))

        // Set up 3 sides of box (0,0)
        state = DotsAndBoxesEngine.makeMove(state, EdgeCoordinate(EdgeOrientation.HORIZONTAL, 0, 0))!!.newState
        state = DotsAndBoxesEngine.makeMove(state, EdgeCoordinate(EdgeOrientation.HORIZONTAL, 1, 0))!!.newState
        state = DotsAndBoxesEngine.makeMove(state, EdgeCoordinate(EdgeOrientation.VERTICAL, 0, 0))!!.newState

        // AI should pick the 4th side (VERTICAL, 0, 1) to complete box (0,0)
        val aiChoice = DotsAndBoxesEngine.chooseSmartAiMove(state)
        assertEquals(EdgeCoordinate(EdgeOrientation.VERTICAL, 0, 1), aiChoice)
    }
}
