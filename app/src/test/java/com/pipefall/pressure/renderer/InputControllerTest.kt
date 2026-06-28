package com.pipefall.pressure.renderer

import com.pipefall.pressure.simulation.SimulationCommand
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class InputControllerTest {
    @Test
    fun thumbstickEmitsOneCommandPerDirectionEntry() {
        val controller = InputController()

        assertEquals(SimulationCommand.MoveLeft, controller.onThumbstickX(-1f))
        assertNull(controller.onThumbstickX(-0.75f))
        assertNull(controller.onThumbstickX(-0.25f))
        assertEquals(SimulationCommand.MoveLeft, controller.onThumbstickX(-0.8f))
        assertNull(controller.onThumbstickX(0f))
        assertEquals(SimulationCommand.MoveRight, controller.onThumbstickX(1f))
    }

    @Test
    fun buttonsMapToRotateAndDropCommands() {
        val controller = InputController()

        assertEquals(SimulationCommand.RotateClockwise, controller.onRotateButtonPressed())
        assertEquals(SimulationCommand.HardDrop, controller.onTriggerPressed())
    }
}
