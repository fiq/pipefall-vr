package com.pipefall.pressure.debug

import com.pipefall.pressure.simulation.Board
import com.pipefall.pressure.simulation.Cell
import com.pipefall.pressure.simulation.GridPosition
import com.pipefall.pressure.simulation.Material
import com.pipefall.pressure.simulation.SimulationState
import com.pipefall.pressure.simulation.StructuralState
import com.pipefall.pressure.simulation.WaterState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StatisticsTest {
    @Test
    fun snapshotOfEmptyBoardReportsZeros() {
        val statistics = Statistics()

        val snapshot = statistics.snapshot(SimulationState())

        assertEquals(0, snapshot.waterHeight)
        assertEquals(0, snapshot.maxPressure)
        assertEquals(0, snapshot.crackedCount)
        assertEquals(0, snapshot.failedCount)
        assertEquals(0, snapshot.occupiedCount)
        assertEquals(0, snapshot.ticksElapsed)
        assertFalse(snapshot.gameOver)
        assertTrue(snapshot.pressureHeatmap.isEmpty())
        assertTrue(snapshot.supportHeatmap.isEmpty())
    }

    @Test
    fun snapshotReportsWaterHeightAndMaxPressureForSubmergedCells() {
        val statistics = Statistics()
        val state = SimulationState(
            board = Board.empty()
                .place(GridPosition(0, 0), Cell(Material.CONCRETE))
                .place(GridPosition(0, 1), Cell(Material.CONCRETE))
                .withRecalculatedBonds(),
            water = WaterState(height = 5),
        )

        val snapshot = statistics.snapshot(state)

        assertEquals(5, snapshot.waterHeight)
        assertEquals(5, snapshot.maxPressure)
        assertEquals(2, snapshot.occupiedCount)
        assertEquals(5, snapshot.pressureHeatmap[GridPosition(0, 0)])
        assertEquals(4, snapshot.pressureHeatmap[GridPosition(0, 1)])
    }

    @Test
    fun snapshotCountsCrackedCells() {
        val statistics = Statistics()
        val state = SimulationState(
            board = Board.empty()
                .place(GridPosition(0, 0), Cell(Material.CONCRETE, state = StructuralState.CRACKED))
                .place(GridPosition(1, 0), Cell(Material.CONCRETE))
                .withRecalculatedBonds(),
        )

        val snapshot = statistics.snapshot(state)

        assertEquals(1, snapshot.crackedCount)
        assertEquals(2, snapshot.occupiedCount)
    }

    @Test
    fun snapshotCountsRecentlyFailedPositions() {
        val statistics = Statistics()
        val state = SimulationState(
            board = Board.empty()
                .place(GridPosition(0, 0), Cell(Material.CONCRETE)),
            recentlyFailedPositions = setOf(GridPosition(0, 0), GridPosition(1, 0)),
        )

        val snapshot = statistics.snapshot(state)

        assertEquals(2, snapshot.failedCount)
        assertEquals(1, snapshot.occupiedCount)
    }

    @Test
    fun snapshotReportsGameOverAndTicksElapsed() {
        val statistics = Statistics()
        val state = SimulationState(
            water = WaterState(height = 20),
            gameOver = true,
            ticksElapsed = 42,
        )

        val snapshot = statistics.snapshot(state)

        assertTrue(snapshot.gameOver)
        assertEquals(42, snapshot.ticksElapsed)
    }

    @Test
    fun snapshotPopulatesSupportHeatmapForOccupiedCells() {
        val statistics = Statistics()
        val state = SimulationState(
            board = Board.empty()
                .place(GridPosition(0, 0), Cell(Material.CONCRETE))
                .place(GridPosition(1, 0), Cell(Material.CONCRETE))
                .withRecalculatedBonds(),
        )

        val snapshot = statistics.snapshot(state)

        assertEquals(2, snapshot.supportHeatmap.size)
        assertTrue(snapshot.supportHeatmap.containsKey(GridPosition(0, 0)))
        assertTrue(snapshot.supportHeatmap.containsKey(GridPosition(1, 0)))
    }
}
