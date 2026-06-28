package com.pipefall.pressure.simulation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class BoardTest {
    @Test
    fun emptyBoardUsesDamDimensions() {
        val board = Board.empty()

        assertEquals(12, board.width)
        assertEquals(20, board.height)
        assertTrue(board.isInBounds(GridPosition(0, 0)))
        assertTrue(board.isInBounds(GridPosition(11, 19)))
        assertFalse(board.isInBounds(GridPosition(12, 19)))
        assertFalse(board.isInBounds(GridPosition(11, 20)))
    }

    @Test
    fun placingCellReturnsNewBoardWithoutMutatingOriginal() {
        val original = Board.empty()
        val position = GridPosition(3, 4)
        val cell = Cell(Material.CONCRETE)

        val updated = original.place(position, cell)

        assertNull(original[position])
        assertEquals(0, original.occupiedCount)
        assertEquals(cell, updated[position])
        assertEquals(1, updated.occupiedCount)
    }

    @Test
    fun recalculatedBondsUseTouchingFacesOnly() {
        val board = Board.empty()
            .place(GridPosition(0, 0), Cell(Material.CONCRETE))
            .place(GridPosition(1, 0), Cell(Material.STEEL))
            .place(GridPosition(0, 1), Cell(Material.DRAIN))
            .place(GridPosition(2, 2), Cell(Material.CONCRETE))
            .withRecalculatedBonds()

        assertEquals(
            setOf(GridPosition(1, 0), GridPosition(0, 1)),
            board[GridPosition(0, 0)]?.bondedNeighbors,
        )
        assertEquals(emptySet<GridPosition>(), board[GridPosition(2, 2)]?.bondedNeighbors)
    }

    @Test
    fun removingEmptyPositionReturnsSameBoard() {
        val board = Board.empty()

        assertSame(board, board.remove(GridPosition(0, 0)))
    }

    @Test(expected = IllegalArgumentException::class)
    fun placingOutsideBoardFails() {
        Board.empty().place(GridPosition(12, 0), Cell(Material.CONCRETE))
    }
}
