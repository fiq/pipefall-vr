package com.pipefall.pressure.simulation

sealed interface SimulationCommand {
    data object Spawn : SimulationCommand

    data object MoveLeft : SimulationCommand

    data object MoveRight : SimulationCommand

    data object RotateClockwise : SimulationCommand

    data object HardDrop : SimulationCommand

    data class TickWater(
        val ticks: Int = 1,
    ) : SimulationCommand

    data class Step(
        val ticks: Int = 1,
    ) : SimulationCommand
}
