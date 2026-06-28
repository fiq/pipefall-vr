package com.pipefall.pressure.simulation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SimulationTests {
    @Test
    fun spawnPlacesTheNextEngineeringModuleAtTheTopCenter() {
        val simulation = Simulation(
            moduleGenerator = ModuleGenerator(modules = listOf(EngineeringModules.all.first())),
        )

        val next = simulation.apply(SimulationState(), SimulationCommand.Spawn)

        assertEquals(ActiveModuleState(
            module = EngineeringModules.all.first(),
            origin = GridPosition(5, 18),
        ), next.activeModule)
        assertEquals(0, next.nextSpawnIndex)
        assertFalse(next.gameOver)
    }

    @Test
    fun moveAndRotateActiveModuleRemainDeterministic() {
        val pillar = EngineeringModules.all.first { it.type == ModuleType.SHORT_PILLAR }
        val simulation = Simulation(
            moduleGenerator = ModuleGenerator(modules = listOf(pillar)),
        )

        val spawned = simulation.apply(SimulationState(), SimulationCommand.Spawn)
        val moved = simulation.apply(spawned, SimulationCommand.MoveLeft)
        val rotated = simulation.apply(moved, SimulationCommand.RotateClockwise)

        assertEquals(GridPosition(4, 17), moved.activeModule?.origin)
        assertEquals(3, rotated.activeModule?.module?.width)
        assertEquals(1, rotated.activeModule?.module?.height)
        assertEquals(GridPosition(4, 17), rotated.activeModule?.origin)
    }

    @Test
    fun hardDropLocksTheActiveModuleAndAdvancesSpawnIndex() {
        val beam = EngineeringModules.all.first { it.type == ModuleType.REINFORCED_BEAM }
        val simulation = Simulation(
            moduleGenerator = ModuleGenerator(modules = listOf(beam)),
        )

        val spawned = simulation.apply(SimulationState(), SimulationCommand.Spawn)
        val dropped = simulation.apply(spawned, SimulationCommand.HardDrop)

        assertNull(dropped.activeModule)
        assertEquals(1, dropped.nextSpawnIndex)
        assertEquals(3, dropped.board.occupiedCount)
        assertEquals(Material.CONCRETE, dropped.board[GridPosition(4, 0)]?.material)
        assertEquals(Material.CONCRETE, dropped.board[GridPosition(5, 0)]?.material)
        assertEquals(Material.CONCRETE, dropped.board[GridPosition(6, 0)]?.material)
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
    fun stepAdvancesWaterAndResolvesFailureCascade() {
        val simulation = Simulation(
            waterSystem = WaterSystem(ticksPerRise = 1, risePerStep = 16, maxHeight = 20),
        )
        val state = SimulationState(
            board = Board.empty()
                .place(GridPosition(0, 0), Cell(Material.CONCRETE))
                .place(GridPosition(0, 1), Cell(Material.CONCRETE))
                .withRecalculatedBonds(),
        )

        val next = simulation.step(state)

        assertTrue(next.board.cells().isEmpty())
        assertEquals(WaterState(height = 16, tickRemainder = 0), next.water)
        assertFalse(next.gameOver)
        assertEquals(1, next.ticksElapsed)
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
