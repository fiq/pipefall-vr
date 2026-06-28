package com.pipefall.pressure.renderer

import com.pipefall.pressure.simulation.SimulationCommand

class InputController {
    fun onThumbstickX(x: Float): SimulationCommand? =
        when {
            x <= -0.5f -> SimulationCommand.MoveLeft
            x >= 0.5f -> SimulationCommand.MoveRight
            else -> null
        }

    fun onRotateButtonPressed(): SimulationCommand =
        SimulationCommand.RotateClockwise

    fun onTriggerPressed(): SimulationCommand =
        SimulationCommand.HardDrop
}
