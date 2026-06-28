package com.pipefall.pressure.simulation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CollisionTests {
    private val beam = EngineeringModules.all.first { it.type == ModuleType.REINFORCED_BEAM }

    @Test
    fun moduleCanBePlacedWhenAllCellsAreInBoundsAndEmpty() {
        val board = Board.empty()

        assertTrue(board.canPlace(beam, GridPosition(4, 0)))
        assertFalse(board.collides(beam, GridPosition(4, 0)))
    }

    @Test
    fun moduleCollidesWhenAnyCellLeavesBoard() {
        val board = Board.empty()

        assertTrue(board.collides(beam, GridPosition(-1, 0)))
        assertTrue(board.collides(beam, GridPosition(10, 0)))
        assertTrue(board.collides(beam.rotatedClockwise(), GridPosition(0, 18)))
        assertTrue(board.collides(beam, GridPosition(0, -1)))
    }

    @Test
    fun moduleCollidesWithLockedCells() {
        val board = Board.empty()
            .place(GridPosition(5, 0), Cell(Material.CONCRETE))

        assertTrue(board.collides(beam, GridPosition(4, 0)))
    }

    @Test
    fun lockingModuleWritesCellsWithoutMutatingOriginalBoard() {
        val original = Board.empty()

        val locked = original.lock(beam, GridPosition(4, 0))

        assertNull(original[GridPosition(4, 0)])
        assertEquals(Material.CONCRETE, locked[GridPosition(4, 0)]?.material)
        assertEquals(Material.CONCRETE, locked[GridPosition(5, 0)]?.material)
        assertEquals(Material.CONCRETE, locked[GridPosition(6, 0)]?.material)
        assertEquals(3, locked.occupiedCount)
    }

    @Test
    fun lockingModuleRecalculatesBondsAcrossExistingAndNewCells() {
        val board = Board.empty()
            .place(GridPosition(0, 0), Cell(Material.CONCRETE))

        val locked = board.lock(
            module = EngineeringModules.all.first { it.type == ModuleType.INSPECTION_SHAFT },
            origin = GridPosition(0, 1),
        )

        assertEquals(setOf(GridPosition(0, 1)), locked[GridPosition(0, 0)]?.bondedNeighbors)
        assertEquals(
            setOf(GridPosition(0, 0), GridPosition(0, 2)),
            locked[GridPosition(0, 1)]?.bondedNeighbors,
        )
        assertEquals(setOf(GridPosition(0, 1)), locked[GridPosition(0, 2)]?.bondedNeighbors)
    }

    @Test(expected = IllegalArgumentException::class)
    fun lockingCollidingModuleFails() {
        Board.empty().lock(beam, GridPosition(10, 0))
    }
}
