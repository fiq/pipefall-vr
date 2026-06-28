package com.pipefall.pressure.renderer

import android.opengl.GLES32
import android.opengl.GLSurfaceView
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
        boardRenderer.draw(state, viewportWidth, viewportHeight)
    }
}
