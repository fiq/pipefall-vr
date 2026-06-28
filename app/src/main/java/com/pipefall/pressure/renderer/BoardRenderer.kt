package com.pipefall.pressure.renderer

import android.opengl.GLES32
import android.opengl.Matrix
import com.pipefall.pressure.simulation.Board
import com.pipefall.pressure.simulation.Material
import com.pipefall.pressure.simulation.SimulationState

class BoardRenderer(
    private val meshFactory: MeshFactory = MeshFactory(),
) {
    private val boardDistanceMeters = 2.5f
    private val cellSizeMeters = 0.12f
    private val cellDepthMeters = 0.06f
    private val cellCenterOffsetMeters = cellDepthMeters / 2f
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
    private var cellMesh: GpuMesh? = null

    private val boardModelMatrix = FloatArray(16)
    private val cellLocalMatrix = FloatArray(16)
    private val cellModelMatrix = FloatArray(16)
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

        Matrix.setIdentityM(boardModelMatrix, 0)
        Matrix.setIdentityM(viewMatrix, 0)
        Matrix.setIdentityM(projectionMatrix, 0)

        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 0f, 0f, 0f, -1f, 0f, 1f, 0f)
        Matrix.perspectiveM(projectionMatrix, 0, 42f, viewportWidth.toFloat() / viewportHeight.toFloat(), 0.1f, 10f)
        Matrix.translateM(boardModelMatrix, 0, 0f, 0f, -boardDistanceMeters)

        Matrix.multiplyMM(viewModelMatrix, 0, viewMatrix, 0, boardModelMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewModelMatrix, 0)

        val shader = program ?: return
        shader.use()
        GLES32.glEnable(GLES32.GL_DEPTH_TEST)
        GLES32.glDepthMask(true)

        drawMesh(surfaceMesh, surfaceColor)
        drawMesh(gridMesh, gridColor)
        drawLockedCells(state.board)
    }

    private fun ensureMeshes(board: Board) {
        if (board.width == cachedColumns && board.height == cachedRows) {
            return
        }

        cachedColumns = board.width
        cachedRows = board.height
        surfaceMesh = meshFactory.createBoardSurface(board.width, board.height, cellSizeMeters).toGpuMesh()
        gridMesh = meshFactory.createBoardGrid(board.width, board.height, cellSizeMeters).toGpuMesh()
        cellMesh = meshFactory.createUnitCube().toGpuMesh()
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

    private fun drawLockedCells(board: Board) {
        val currentCellMesh = cellMesh ?: return
        val halfWidth = board.width * cellSizeMeters / 2f
        val halfHeight = board.height * cellSizeMeters / 2f

        for (position in board.occupiedPositions()) {
            val cell = board[position] ?: continue
            val color = colorFor(cell.material)
            val x = -halfWidth + cellSizeMeters * (position.x + 0.5f)
            val y = -halfHeight + cellSizeMeters * (position.y + 0.5f)

            Matrix.setIdentityM(cellLocalMatrix, 0)
            Matrix.translateM(cellLocalMatrix, 0, x, y, cellCenterOffsetMeters)
            Matrix.scaleM(cellLocalMatrix, 0, cellSizeMeters, cellSizeMeters, cellDepthMeters)
            Matrix.multiplyMM(cellModelMatrix, 0, boardModelMatrix, 0, cellLocalMatrix, 0)
            Matrix.multiplyMM(viewModelMatrix, 0, viewMatrix, 0, cellModelMatrix, 0)
            Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewModelMatrix, 0)
            drawMesh(currentCellMesh, color)
        }
    }

    private fun colorFor(material: Material): FloatArray =
        when (material) {
            Material.CONCRETE -> floatArrayOf(0.76f, 0.77f, 0.79f, 1f)
            Material.STEEL -> floatArrayOf(0.48f, 0.52f, 0.56f, 1f)
            Material.DRAIN -> floatArrayOf(0.28f, 0.42f, 0.66f, 1f)
            Material.SPILLWAY -> floatArrayOf(0.67f, 0.61f, 0.46f, 1f)
            Material.REINFORCEMENT -> floatArrayOf(0.67f, 0.49f, 0.28f, 1f)
            Material.SERVICE_SHAFT -> floatArrayOf(0.35f, 0.34f, 0.32f, 1f)
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
