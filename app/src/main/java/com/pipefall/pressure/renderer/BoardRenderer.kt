package com.pipefall.pressure.renderer

import android.opengl.GLES32
import android.opengl.Matrix
import com.pipefall.pressure.simulation.Board
import com.pipefall.pressure.simulation.Material
import com.pipefall.pressure.simulation.ModuleCell
import com.pipefall.pressure.simulation.SimulationState
import com.pipefall.pressure.simulation.StructuralState

class BoardRenderer(
    private val meshFactory: MeshFactory = MeshFactory(),
    private val waterRenderer: WaterRenderer = WaterRenderer(meshFactory),
    private val crackRenderer: CrackRenderer = CrackRenderer(meshFactory),
) {
    private val boardDistanceMeters = 2.5f
    private val cellSizeMeters = 0.12f
    private val cellDepthMeters = 0.06f
    private val cellCenterOffsetMeters = cellDepthMeters / 2f
    private val activeModuleLiftMeters = 0.11f
    private val surfaceColor = floatArrayOf(0.68f, 0.70f, 0.72f, 0.84f)
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
    private val viewModelMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)

    fun onSurfaceCreated() {
        program = GlProgram(VERTEX_SHADER, FRAGMENT_SHADER).also { shader ->
            positionHandle = shader.attributeLocation("aPosition")
            mvpHandle = shader.uniformLocation("uMvpMatrix")
            colorHandle = shader.uniformLocation("uColor")
        }
        waterRenderer.onSurfaceCreated()
        crackRenderer.onSurfaceCreated()
    }

    fun draw(
        state: SimulationState,
        viewMatrix: FloatArray,
        projectionMatrix: FloatArray,
        viewportWidth: Int,
        viewportHeight: Int,
    ) {
        if (viewportWidth <= 0 || viewportHeight <= 0) {
            return
        }

        ensureMeshes(state.board)

        Matrix.setIdentityM(boardModelMatrix, 0)
        Matrix.translateM(boardModelMatrix, 0, 0f, 0f, -boardDistanceMeters)

        Matrix.multiplyMM(viewModelMatrix, 0, viewMatrix, 0, boardModelMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewModelMatrix, 0)

        val shader = program ?: return
        shader.use()
        GLES32.glEnable(GLES32.GL_DEPTH_TEST)
        GLES32.glDepthMask(true)

        waterRenderer.draw(state, boardModelMatrix, viewMatrix, projectionMatrix, cellSizeMeters)
        drawMesh(surfaceMesh, surfaceColor)
        drawMesh(gridMesh, gridColor)
        drawLockedCells(state.board, viewMatrix, projectionMatrix)
        drawActiveModule(state, viewMatrix, projectionMatrix)
        crackRenderer.draw(state, boardModelMatrix, viewMatrix, projectionMatrix, cellSizeMeters)
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

    private fun drawLockedCells(
        board: Board,
        viewMatrix: FloatArray,
        projectionMatrix: FloatArray,
    ) {
        val currentCellMesh = cellMesh ?: return
        val halfWidth = board.width * cellSizeMeters / 2f
        val halfHeight = board.height * cellSizeMeters / 2f

        for (position in board.occupiedPositions()) {
            val cell = board[position] ?: continue
            val color = colorFor(cell.material, cell.state)
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

    private fun drawActiveModule(
        state: SimulationState,
        viewMatrix: FloatArray,
        projectionMatrix: FloatArray,
    ) {
        val activeModule = state.activeModule ?: return
        val currentCellMesh = cellMesh ?: return
        val halfWidth = state.board.width * cellSizeMeters / 2f
        val halfHeight = state.board.height * cellSizeMeters / 2f
        val moduleOriginX = activeModule.origin.x * cellSizeMeters
        val moduleOriginY = activeModule.origin.y * cellSizeMeters

        for (moduleCell in activeModule.module.cells) {
            val worldX = -halfWidth + moduleOriginX + cellSizeMeters * (moduleCell.offset.x + 0.5f)
            val worldY = -halfHeight + moduleOriginY + cellSizeMeters * (moduleCell.offset.y + 0.5f)

            Matrix.setIdentityM(cellLocalMatrix, 0)
            Matrix.translateM(cellLocalMatrix, 0, worldX, worldY, cellCenterOffsetMeters + activeModuleLiftMeters)
            Matrix.scaleM(cellLocalMatrix, 0, cellSizeMeters, cellSizeMeters, cellDepthMeters)
            Matrix.multiplyMM(cellModelMatrix, 0, boardModelMatrix, 0, cellLocalMatrix, 0)
            Matrix.multiplyMM(viewModelMatrix, 0, viewMatrix, 0, cellModelMatrix, 0)
            Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewModelMatrix, 0)
            drawMesh(currentCellMesh, activeModuleColorFor(moduleCell))
        }
    }

    private fun colorFor(
        material: Material,
        state: StructuralState = StructuralState.STABLE,
    ): FloatArray {
        val base = when (material) {
            Material.CONCRETE -> floatArrayOf(0.76f, 0.77f, 0.79f, 0.97f)
            Material.STEEL -> floatArrayOf(0.48f, 0.52f, 0.56f, 0.97f)
            Material.DRAIN -> floatArrayOf(0.28f, 0.42f, 0.66f, 0.96f)
            Material.SPILLWAY -> floatArrayOf(0.67f, 0.61f, 0.46f, 0.96f)
            Material.REINFORCEMENT -> floatArrayOf(0.67f, 0.49f, 0.28f, 0.96f)
            Material.SERVICE_SHAFT -> floatArrayOf(0.35f, 0.34f, 0.32f, 0.96f)
        }
        return if (state == StructuralState.CRACKED) {
            darken(base)
        } else {
            base
        }
    }

    private fun darken(color: FloatArray): FloatArray =
        floatArrayOf(
            (color[0] * 0.7f),
            (color[1] * 0.7f),
            (color[2] * 0.7f),
            color[3],
        )

    private fun activeModuleColorFor(moduleCell: ModuleCell): FloatArray {
        val baseColor = colorFor(moduleCell.cell.material)
        return floatArrayOf(
            (baseColor[0] + 0.08f).coerceAtMost(1f),
            (baseColor[1] + 0.08f).coerceAtMost(1f),
            (baseColor[2] + 0.08f).coerceAtMost(1f),
            0.92f,
        )
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
