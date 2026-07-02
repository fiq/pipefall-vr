package com.pipefall.pressure.renderer

import android.opengl.GLES32
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.pipefall.pressure.simulation.SimulationState

class OpenXRRenderer(
    private val boardRenderer: BoardRenderer = BoardRenderer(),
) : GLSurfaceView.Renderer {
    @Volatile
    var state: SimulationState = SimulationState()

    @Volatile
    var viewportWidth: Int = 0

    @Volatile
    var viewportHeight: Int = 0

    @Volatile
    var fps: Int = 0
        private set

    private var frameCount: Int = 0
    private var lastFpsNanos: Long = 0L

    private val viewMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)

    override fun onSurfaceCreated(
        gl: javax.microedition.khronos.opengles.GL10?,
        config: javax.microedition.khronos.egl.EGLConfig?,
    ) {
        GLES32.glClearColor(0.07f, 0.09f, 0.11f, 1f)
        GLES32.glEnable(GLES32.GL_DEPTH_TEST)
        boardRenderer.onSurfaceCreated()
    }

    override fun onSurfaceChanged(
        gl: javax.microedition.khronos.opengles.GL10?,
        width: Int,
        height: Int,
    ) {
        viewportWidth = width
        viewportHeight = height
        GLES32.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: javax.microedition.khronos.opengles.GL10?) {
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)
        computeCameraMatrices()
        boardRenderer.draw(state, viewMatrix, projectionMatrix, viewportWidth, viewportHeight)
        updateFps()
    }

    private fun computeCameraMatrices() {
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 0f, 0f, 0f, -1f, 0f, 1f, 0f)
        if (viewportWidth > 0 && viewportHeight > 0) {
            Matrix.perspectiveM(
                projectionMatrix,
                0,
                FIELD_OF_VIEW_DEGREES,
                viewportWidth.toFloat() / viewportHeight.toFloat(),
                NEAR_PLANE,
                FAR_PLANE,
            )
        }
    }

    private fun updateFps() {
        val now = System.nanoTime()
        if (lastFpsNanos == 0L) {
            lastFpsNanos = now
            return
        }
        frameCount++
        val elapsedNanos = now - lastFpsNanos
        if (elapsedNanos >= ONE_SECOND_NANOS) {
            fps = (frameCount * ONE_SECOND_NANOS / elapsedNanos).toInt()
            frameCount = 0
            lastFpsNanos = now
        }
    }

    private companion object {
        const val ONE_SECOND_NANOS = 1_000_000_000L
        const val FIELD_OF_VIEW_DEGREES = 42f
        const val NEAR_PLANE = 0.1f
        const val FAR_PLANE = 10f
    }
}
