package com.pipefall.pressure.renderer

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.InputDevice
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import com.pipefall.pressure.QuestShellView
import com.pipefall.pressure.simulation.SimulationCommand

class QuestRenderView(context: Context) : FrameLayout(context) {
    private val surfaceView = GLSurfaceView(context)
    private val shellView = QuestShellView(context)

    val rendererController = InputController()
    val renderer = OpenXRRenderer()
    var onCommand: ((SimulationCommand) -> Unit)? = null
    private var triggerArmed = true

    init {
        isFocusable = true
        isFocusableInTouchMode = true
        surfaceView.setEGLContextClientVersion(3)
        surfaceView.preserveEGLContextOnPause = true
        surfaceView.setRenderer(renderer)
        surfaceView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY

        addView(
            surfaceView,
            LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            ),
        )

        addView(
            shellView,
            LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            ),
        )

        requestFocus()
    }

    fun onResume() {
        surfaceView.onResume()
    }

    fun onPause() {
        surfaceView.onPause()
    }

    override fun onGenericMotionEvent(event: MotionEvent): Boolean {
        if (event.isFromSource(InputDevice.SOURCE_JOYSTICK or InputDevice.SOURCE_GAMEPAD)) {
            val command = rendererController.onThumbstickX(event.getAxisValue(MotionEvent.AXIS_X))
            if (command != null) {
                dispatchCommand(command)
                return true
            }

            val triggerValue =
                maxOf(
                    event.getAxisValue(MotionEvent.AXIS_LTRIGGER),
                    event.getAxisValue(MotionEvent.AXIS_RTRIGGER),
                )
            if (triggerValue >= 0.5f && triggerArmed) {
                triggerArmed = false
                dispatchCommand(rendererController.onTriggerPressed())
                return true
            }
            if (triggerValue < 0.5f) {
                triggerArmed = true
            }
        }

        return super.onGenericMotionEvent(event)
    }

    override fun onKeyDown(
        keyCode: Int,
        event: KeyEvent,
    ): Boolean {
        if (event.repeatCount > 0) {
            return super.onKeyDown(keyCode, event)
        }

        val command =
            when (keyCode) {
                KeyEvent.KEYCODE_BUTTON_A,
                KeyEvent.KEYCODE_BUTTON_B,
                KeyEvent.KEYCODE_BUTTON_X,
                KeyEvent.KEYCODE_BUTTON_Y,
                KeyEvent.KEYCODE_BUTTON_R1,
                KeyEvent.KEYCODE_BUTTON_L1 -> rendererController.onRotateButtonPressed()

                KeyEvent.KEYCODE_BUTTON_L2,
                KeyEvent.KEYCODE_BUTTON_R2 -> rendererController.onTriggerPressed()

                else -> null
            }

        if (command != null) {
            dispatchCommand(command)
            return true
        }

        return super.onKeyDown(keyCode, event)
    }

    private fun dispatchCommand(command: SimulationCommand) {
        onCommand?.invoke(command)
        shellView.setStatus(commandLabel(command))
    }

    private fun commandLabel(command: SimulationCommand): String =
        when (command) {
            SimulationCommand.MoveLeft -> "move left"
            SimulationCommand.MoveRight -> "move right"
            SimulationCommand.RotateClockwise -> "rotate"
            SimulationCommand.HardDrop -> "drop"
            SimulationCommand.Spawn -> "spawn"
            is SimulationCommand.Step -> "step ${command.ticks}"
            is SimulationCommand.TickWater -> "tick water ${command.ticks}"
        }
}
