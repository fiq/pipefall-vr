package com.pipefall.pressure.simulation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SimulationTests {
    @Test
    fun stepAdvancesWaterAndResolvesFailures() {
        val simulation = Simulation(
            waterSystem = WaterSystem(ticksPerRise = 1, risePerStep = 10, maxHeight = 20),
        )
        val state = SimulationState(
            board = Board.empty()
                .place(GridPosition(1, 0), Cell(Material.CONCRETE)),
        )

        val next = simulation.step(state)

        assertTrue(next.board.cells().isEmpty())
        assertEquals(WaterState(height = 10, tickRemainder = 0), next.water)
        assertFalse(next.gameOver)
        assertEquals(1, next.ticksElapsed)
    }

    @Test
    fun tickWaterAdvancesWithoutResolvingFailures() {
        val simulation = Simulation(
            waterSystem = WaterSystem(ticksPerRise = 1, risePerStep = 10, maxHeight = 20),
        )
        val state = SimulationState(
            board = Board.empty()
                .place(GridPosition(1, 0), Cell(Material.CONCRETE)),
        )

        val next = simulation.tickWater(state)

        assertEquals(1, next.board.occupiedCount)
        assertEquals(StructuralState.STABLE, next.board[GridPosition(1, 0)]?.state)
        assertEquals(WaterState(height = 10, tickRemainder = 0), next.water)
        assertFalse(next.gameOver)
    }

    @Test
    fun stepMarksGameOverWhenWaterReachesTheTop() {
        val simulation = Simulation(
            waterSystem = WaterSystem(ticksPerRise = 1, risePerStep = 5, maxHeight = 5),
        )

        val next = simulation.step(SimulationState())

        assertTrue(next.gameOver)
        assertEquals(WaterState(height = 5, tickRemainder = 0), next.water)
    }
}
