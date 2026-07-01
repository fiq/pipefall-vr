package com.pipefall.pressure.debug

import com.pipefall.pressure.simulation.PressureSystem
import com.pipefall.pressure.simulation.SimulationState
import com.pipefall.pressure.simulation.StructuralState
import com.pipefall.pressure.simulation.SupportSystem

/**
 * Produces a [StatisticsSnapshot] from a [SimulationState].
 *
 * Stays pure Kotlin with no Android or rendering imports so the snapshot
 * remains unit-testable. The pressure and support heatmaps are recomputed
 * from the board using the same default systems the simulation uses.
 */
class Statistics(
    private val pressureSystem: PressureSystem = PressureSystem(),
    private val supportSystem: SupportSystem = SupportSystem(),
) {
    fun snapshot(state: SimulationState): StatisticsSnapshot {
        val board = state.board
        val waterHeight = state.water.height

        val pressureHeatmap = pressureSystem.pressuresFor(board, waterHeight)
        val supportHeatmap = supportSystem.effectiveStrengths(board)

        val maxPressure = pressureHeatmap.values.maxOrNull() ?: 0
        val crackedCount = board.cells().values.count { it.state == StructuralState.CRACKED }
        val failedCount = state.recentlyFailedPositions.size

        return StatisticsSnapshot(
            waterHeight = waterHeight,
            maxPressure = maxPressure,
            crackedCount = crackedCount,
            failedCount = failedCount,
            occupiedCount = board.occupiedCount,
            gameOver = state.gameOver,
            ticksElapsed = state.ticksElapsed,
            pressureHeatmap = pressureHeatmap,
            supportHeatmap = supportHeatmap,
        )
    }
}
