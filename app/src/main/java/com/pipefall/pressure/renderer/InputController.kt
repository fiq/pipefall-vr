package com.pipefall.pressure.renderer

import com.pipefall.pressure.simulation.SimulationCommand

class InputController {
    private var thumbstickDirection: Int = 0

    fun onThumbstickX(x: Float): SimulationCommand? =
        when {
            x <= -0.5f -> emitThumbstickDirection(-1, SimulationCommand.MoveLeft)
            x >= 0.5f -> emitThumbstickDirection(1, SimulationCommand.MoveRight)
            else -> {
                thumbstickDirection = 0
                null
            }
        }

    fun onRotateButtonPressed(): SimulationCommand =
        SimulationCommand.RotateClockwise

    fun onTriggerPressed(): SimulationCommand =
        SimulationCommand.HardDrop

    private fun emitThumbstickDirection(
        direction: Int,
        command: SimulationCommand,
    ): SimulationCommand? {
        if (thumbstickDirection == direction) {
            return null
        }

        thumbstickDirection = direction
        return command
    }
}
