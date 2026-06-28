package com.pipefall.pressure.simulation

import org.junit.Assert.assertEquals
import org.junit.Test

class PressureTests {
    private val pressure = PressureSystem()

    @Test
    fun pressureIsZeroWhenWaterHasNotReachedTheCell() {
        assertEquals(0, pressure.pressureAt(cellY = 6, waterHeight = 5))
    }

    @Test
    fun pressureIncreasesWithDepthBelowWaterline() {
        assertEquals(5, pressure.pressureAt(cellY = 2, waterHeight = 7))
        assertEquals(1, pressure.pressureAt(GridPosition(3, 6), waterHeight = 7))
    }

    @Test
    fun pressureSnapshotUsesOnlyLockedCells() {
        val board = Board.empty()
            .place(GridPosition(0, 0), Cell(Material.CONCRETE))
            .place(GridPosition(4, 3), Cell(Material.STEEL))

        assertEquals(
            mapOf(
                GridPosition(0, 0) to 4,
                GridPosition(4, 3) to 1,
            ),
            pressure.pressuresFor(board, waterHeight = 4),
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun negativeWaterHeightFails() {
        pressure.pressureAt(cellY = 0, waterHeight = -1)
    }
}
