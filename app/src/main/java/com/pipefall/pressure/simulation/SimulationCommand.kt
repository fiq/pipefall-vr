package com.pipefall.pressure.simulation

sealed interface SimulationCommand {
    data class TickWater(
        val ticks: Int = 1,
    ) : SimulationCommand

    data class Step(
        val ticks: Int = 1,
    ) : SimulationCommand
}
