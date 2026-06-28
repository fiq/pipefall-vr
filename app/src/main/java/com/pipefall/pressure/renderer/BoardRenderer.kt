package com.pipefall.pressure.renderer

import android.opengl.GLES32
import android.opengl.Matrix
import com.pipefall.pressure.simulation.Board
import com.pipefall.pressure.simulation.SimulationState

class BoardRenderer(
    private val meshFactory: MeshFactory = MeshFactory(),
) {
    private val boardDistanceMeters = 2.5f
    private val cellSizeMeters = 0.12f
    private val surfaceColor = floatArrayOf(0.68f, 0.70f, 0.72f, 1f)
    private val gridColor = floatArrayOf(0.36f, 0.39f, 0.42f, 1f)

    private var program: GlProgram? = null
    private var positionHandle = 0
    private var mvpHandle = 0
    private var colorHandle = 0

    private var cachedColumns = -1
    private var cachedRows = -1
    private var surfaceMesh: GpuMesh? = null
    private var gridMesh: GpuMesh? = null

    private val modelMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewModelMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)

    fun onSurfaceCreated() {
        program = GlProgram(VERTEX_SHADER, FRAGMENT_SHADER).also { shader ->
            positionHandle = shader.attributeLocation("aPosition")
            mvpHandle = shader.uniformLocation("uMvpMatrix")
            colorHandle = shader.uniformLocation("uColor")
        }
    }

    fun draw(
        state: SimulationState,
        viewportWidth: Int,
        viewportHeight: Int,
    ) {
        if (viewportWidth <= 0 || viewportHeight <= 0) {
            return
        }

        ensureMeshes(state.board)

        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.setIdentityM(viewMatrix, 0)
        Matrix.setIdentityM(projectionMatrix, 0)

        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 0f, 0f, 0f, -1f, 0f, 1f, 0f)
        Matrix.perspectiveM(projectionMatrix, 0, 42f, viewportWidth.toFloat() / viewportHeight.toFloat(), 0.1f, 10f)
        Matrix.translateM(modelMatrix, 0, 0f, 0f, -boardDistanceMeters)

        Matrix.multiplyMM(viewModelMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewModelMatrix, 0)

        val shader = program ?: return
        shader.use()
        GLES32.glEnable(GLES32.GL_DEPTH_TEST)
        GLES32.glDepthMask(true)

        drawMesh(surfaceMesh, surfaceColor)
        drawMesh(gridMesh, gridColor)
    }

    private fun ensureMeshes(board: Board) {
        if (board.width == cachedColumns && board.height == cachedRows) {
            return
        }

        cachedColumns = board.width
        cachedRows = board.height
        surfaceMesh = meshFactory.createBoardSurface(board.width, board.height, cellSizeMeters).toGpuMesh()
        gridMesh = meshFactory.createBoardGrid(board.width, board.height, cellSizeMeters).toGpuMesh()
    }

    private fun drawMesh(mesh: GpuMesh?, color: FloatArray) {
        val currentMesh = mesh ?: return

        GLES32.glUniformMatrix4fv(mvpHandle, 1, false, mvpMatrix, 0)
        GLES32.glUniform4f(colorHandle, color[0], color[1], color[2], color[3])
        GLES32.glEnableVertexAttribArray(positionHandle)
        GLES32.glVertexAttribPointer(positionHandle, 3, GLES32.GL_FLOAT, false, 0, currentMesh.vertices)
        GLES32.glDrawArrays(currentMesh.drawMode, 0, currentMesh.vertexCount)
        GLES32.glDisableVertexAttribArray(positionHandle)
    }

    private companion object {
        const val VERTEX_SHADER =
            """
            #version 300 es
            layout(location = 0) in vec3 aPosition;
            uniform mat4 uMvpMatrix;
            void main() {
                gl_Position = uMvpMatrix * vec4(aPosition, 1.0);
            }
            """

        const val FRAGMENT_SHADER =
            """
            #version 300 es
            precision mediump float;
            uniform vec4 uColor;
            out vec4 fragColor;
            void main() {
                fragColor = uColor;
            }
            """
    }
}
