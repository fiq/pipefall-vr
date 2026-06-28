package com.pipefall.pressure.simulation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FailureTests {
    private val failureSystem = FailureSystem()

    @Test
    fun cellsBecomeCrackedWhenPressureExceedsEffectiveStrength() {
        val board = Board.empty()
            .place(GridPosition(1, 1), Cell(Material.CONCRETE))

        val resolved = failureSystem.resolve(board, waterHeight = 8)

        assertEquals(StructuralState.CRACKED, resolved[GridPosition(1, 1)]?.state)
    }

    @Test
    fun cellsFailAndAreRemovedWhenPressureExceedsOneAndHalfTimesEffectiveStrength() {
        val board = Board.empty()
            .place(GridPosition(1, 0), Cell(Material.CONCRETE))

        val resolved = failureSystem.resolve(board, waterHeight = 10)

        assertTrue(resolved.cells().isEmpty())
    }

    @Test
    fun failureCascadeRecalculatesPressureAndSupportUntilStable() {
        val board = Board.empty()
            .place(GridPosition(0, 0), Cell(Material.CONCRETE))
            .place(GridPosition(1, 0), Cell(Material.REINFORCEMENT))

        val resolved = failureSystem.resolve(board, waterHeight = 11)

        assertTrue(resolved.cells().isEmpty())
    }

    @Test(expected = IllegalArgumentException::class)
    fun negativeWaterHeightFails() {
        failureSystem.resolve(Board.empty(), waterHeight = -1)
    }
}
