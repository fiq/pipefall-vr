package com.pipefall.pressure.debug

import com.pipefall.pressure.simulation.GridPosition

/**
 * Immutable read-only view of the simulation state for debug overlays.
 *
 * Failed cells are removed from the board by [com.pipefall.pressure.simulation.FailureSystem],
 * so [failedCount] reflects the positions that disappeared during the most recent step
 * (carried by [com.pipefall.pressure.simulation.SimulationState.recentlyFailedPositions]),
 * not a persistent failed-cell tally.
 */
data class StatisticsSnapshot(
    val waterHeight: Int,
    val maxPressure: Int,
    val crackedCount: Int,
    val failedCount: Int,
    val occupiedCount: Int,
    val gameOver: Boolean,
    val ticksElapsed: Int,
    val pressureHeatmap: Map<GridPosition, Int>,
    val supportHeatmap: Map<GridPosition, Int>,
) {
    init {
        require(waterHeight >= 0) { "waterHeight must be non-negative" }
        require(maxPressure >= 0) { "maxPressure must be non-negative" }
        require(crackedCount >= 0) { "crackedCount must be non-negative" }
        require(failedCount >= 0) { "failedCount must be non-negative" }
        require(occupiedCount >= 0) { "occupiedCount must be non-negative" }
        require(ticksElapsed >= 0) { "ticksElapsed must be non-negative" }
    }
}
