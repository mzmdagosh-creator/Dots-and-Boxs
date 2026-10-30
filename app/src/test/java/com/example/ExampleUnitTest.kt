package com.example

import com.example.model.BoardState
import com.example.model.CpuDifficulty
import com.example.model.DotsAndBoxesEngine
import com.example.model.EdgeCoordinate
import com.example.model.EdgeOrientation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
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
        var state = BoardState.create(2, listOf("A", "B"))
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
    fun cpuDifficulties_easyMediumHardPrioritizeScoringAndAvoidOrMinimizeThirdSides() {
        var state = BoardState.create(1, listOf("A", "C"), CpuDifficulty.HARD)
        assertEquals("C", state.player2Letter)
        assertTrue(state.isPlayerAi(2))

        // Set up 2 sides of box (0,0): top (0,0) and bottom (1,0)
        state = DotsAndBoxesEngine.makeMove(state, EdgeCoordinate(EdgeOrientation.HORIZONTAL, 0, 0))!!.newState
        state = DotsAndBoxesEngine.makeMove(state, EdgeCoordinate(EdgeOrientation.HORIZONTAL, 1, 0))!!.newState

        // Medium and Hard must avoid completing the 3rd side of box (0,0) -> VERTICAL (0,0) and VERTICAL (0,1)
        val mediumMove = DotsAndBoxesEngine.cpuTakeTurn(state, CpuDifficulty.MEDIUM)!!
        assertNotEquals(EdgeCoordinate(EdgeOrientation.VERTICAL, 0, 0), mediumMove)
        assertNotEquals(EdgeCoordinate(EdgeOrientation.VERTICAL, 0, 1), mediumMove)

        val hardMove = DotsAndBoxesEngine.cpuTakeTurn(state, CpuDifficulty.HARD)!!
        assertNotEquals(EdgeCoordinate(EdgeOrientation.VERTICAL, 0, 0), hardMove)
        assertNotEquals(EdgeCoordinate(EdgeOrientation.VERTICAL, 0, 1), hardMove)

        // Now add the 3rd side of box (0,0): VERTICAL (0,0)
        state = DotsAndBoxesEngine.makeMove(state, EdgeCoordinate(EdgeOrientation.VERTICAL, 0, 0))!!.newState

        // All three difficulties (Easy, Medium, Hard) must take the 4th side VERTICAL (0,1)
        val expectedCapture = EdgeCoordinate(EdgeOrientation.VERTICAL, 0, 1)
        assertEquals(expectedCapture, DotsAndBoxesEngine.cpuTakeTurn(state, CpuDifficulty.EASY))
        assertEquals(expectedCapture, DotsAndBoxesEngine.cpuTakeTurn(state, CpuDifficulty.MEDIUM))
        assertEquals(expectedCapture, DotsAndBoxesEngine.cpuTakeTurn(state, CpuDifficulty.HARD))
    }
}
