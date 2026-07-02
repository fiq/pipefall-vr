package com.pipefall.pressure.renderer

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.opengl.GLSurfaceView
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.Gravity
import android.view.InputDevice
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.pipefall.pressure.QuestShellView
import com.pipefall.pressure.R
import com.pipefall.pressure.debug.Overlay
import com.pipefall.pressure.debug.Statistics
import com.pipefall.pressure.simulation.SimulationCommand

class QuestRenderView(context: Context) : FrameLayout(context) {
    private val surfaceView = GLSurfaceView(context)
    private val shellView = QuestShellView(context)
    private val debugView = TextView(context)
    private val gameOverView = TextView(context)
    private val statistics = Statistics()
    private val overlay = Overlay()

    val rendererController = InputController()
    val renderer = OpenXRRenderer()
    var onCommand: ((SimulationCommand) -> Unit)? = null
    private var triggerArmed = true

    private val debugHandler = Handler(Looper.getMainLooper())
    private val debugUpdateRunnable = object : Runnable {
        override fun run() {
            updateDebugOverlay()
            updateGameOverBanner()
            debugHandler.postDelayed(this, DEBUG_UPDATE_INTERVAL_MS)
        }
    }

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

        debugView.apply {
            setTextColor(Color.rgb(120, 240, 120))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
            typeface = Typeface.MONOSPACE
            gravity = Gravity.START or Gravity.TOP
            setPadding(dp(12), dp(72), dp(12), dp(12))
            text = ""
        }

        addView(
            debugView,
            LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            ),
        )

        gameOverView.apply {
            setTextColor(Color.rgb(240, 80, 80))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 32f)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            setPadding(dp(24), dp(24), dp(24), dp(24))
            text = context.getString(R.string.game_over)
            visibility = View.GONE
        }

        addView(
            gameOverView,
            LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            ),
        )

        requestFocus()
    }

    fun onResume() {
        surfaceView.onResume()
        debugHandler.removeCallbacks(debugUpdateRunnable)
        debugHandler.post(debugUpdateRunnable)
    }

    fun onPause() {
        debugHandler.removeCallbacks(debugUpdateRunnable)
        surfaceView.onPause()
    }

    private fun updateDebugOverlay() {
        val snapshot = statistics.snapshot(renderer.state)
        val lines = overlay.render(snapshot, renderer.fps)
        debugView.text = lines.joinToString("\n")
    }

    private fun updateGameOverBanner() {
        gameOverView.visibility =
            if (renderer.state.gameOver) View.VISIBLE else View.GONE
    }

    fun setStatus(text: CharSequence) {
        shellView.setStatus(text)
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

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    private companion object {
        const val DEBUG_UPDATE_INTERVAL_MS = 250L
    }
}
