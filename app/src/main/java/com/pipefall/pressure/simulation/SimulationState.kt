package com.pipefall.pressure.simulation

data class SimulationState(
    val board: Board = Board.empty(),
    val activeModule: ActiveModuleState? = null,
    val water: WaterState = WaterState(),
    val gameOver: Boolean = false,
    val ticksElapsed: Int = 0,
    val nextSpawnIndex: Int = 0,
) {
    init {
        require(nextSpawnIndex >= 0) { "nextSpawnIndex must be non-negative" }
    }
}
