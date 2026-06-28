package com.pipefall.pressure.simulation

data class SimulationState(
    val board: Board = Board.empty(),
    val water: WaterState = WaterState(),
    val gameOver: Boolean = false,
    val ticksElapsed: Int = 0,
)
