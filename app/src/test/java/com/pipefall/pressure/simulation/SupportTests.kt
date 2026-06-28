package com.pipefall.pressure.simulation

import org.junit.Assert.assertEquals
import org.junit.Test

class SupportTests {
    private val support = SupportSystem()

    @Test
    fun isolatedCellUsesBaseStrengthAndExposedFacePenalty() {
        val board = Board.empty()
            .place(GridPosition(1, 1), Cell(Material.CONCRETE))

        assertEquals(6, support.effectiveStrengthAt(board, GridPosition(1, 1)))
    }

    @Test
    fun bondedNeighborsIncreaseSupportThroughAllMatchingRules() {
        val board = Board.empty()
            .place(GridPosition(0, 0), Cell(Material.CONCRETE))
            .place(GridPosition(1, 0), Cell(Material.CONCRETE))
            .place(GridPosition(1, 1), Cell(Material.REINFORCEMENT))
            .withRecalculatedBonds()

        assertEquals(16, support.effectiveStrengthAt(board, GridPosition(1, 0)))
    }

    @Test
    fun supportSnapshotUsesOccupiedCellsOnly() {
        val board = Board.empty()
            .place(GridPosition(0, 0), Cell(Material.CONCRETE))
            .place(GridPosition(1, 0), Cell(Material.CONCRETE))
            .place(GridPosition(0, 1), Cell(Material.REINFORCEMENT))
            .withRecalculatedBonds()

        assertEquals(
            mapOf(
                GridPosition(0, 0) to 16,
                GridPosition(1, 0) to 10,
                GridPosition(0, 1) to 4,
            ),
            support.effectiveStrengths(board),
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun unsupportedEmptyCellFails() {
        support.effectiveStrengthAt(Board.empty(), GridPosition(0, 0))
    }
}
