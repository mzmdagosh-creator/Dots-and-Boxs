package com.example

import com.example.model.BoardState
import com.example.model.DotsAndBoxesEngine
import com.example.model.EdgeCoordinate
import com.example.model.EdgeOrientation
import org.junit.Assert.assertEquals
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
        var state = BoardState(player1Letter = "X", player2Letter = "O")

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

        // Re-clicking the same edge returns null
        assertNull(
            DotsAndBoxesEngine.makeMove(
                outcome.newState,
                EdgeCoordinate(EdgeOrientation.VERTICAL, 0, 1)
            )
        )
    }

    @Test
    fun completingTwoBoxesSimultaneously_awardsTwoPointsAndOneExtraTurn() {
        var state = BoardState()
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
        // Now it is Player 1's turn (6 non-capturing moves played)
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
    }
}
