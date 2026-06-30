package com.pipefall.pressure.renderer

import android.opengl.GLES32
import android.opengl.Matrix
import com.pipefall.pressure.simulation.Board
import com.pipefall.pressure.simulation.GridPosition
import com.pipefall.pressure.simulation.SimulationState
import com.pipefall.pressure.simulation.StructuralState

class CrackRenderer(
    private val meshFactory: MeshFactory = MeshFactory(),
) {
    private val crackColor = floatArrayOf(0.05f, 0.05f, 0.05f, 0.95f)
    private val failureFlashColor = floatArrayOf(0.82f, 0.16f, 0.12f, 0.85f)
    private val crackDepthOffset = 0.001f

    private var program: GlProgram? = null
    private var positionHandle = 0
    private var mvpHandle = 0
    private var colorHandle = 0
    private var crackMesh: GpuMesh? = null
    private var flashMesh: GpuMesh? = null

    private val localMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)
    private val viewModelMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)

    fun onSurfaceCreated() {
        program = GlProgram(VERTEX_SHADER, FRAGMENT_SHADER).also { shader ->
            positionHandle = shader.attributeLocation("aPosition")
            mvpHandle = shader.uniformLocation("uMvpMatrix")
            colorHandle = shader.uniformLocation("uColor")
        }

        if (crackMesh == null) {
            crackMesh = meshFactory.createCrackLines().toGpuMesh()
        }
        if (flashMesh == null) {
            flashMesh = meshFactory.createUnitCube().toGpuMesh()
        }
    }

    fun draw(
        state: SimulationState,
        boardModelMatrix: FloatArray,
        viewMatrix: FloatArray,
        projectionMatrix: FloatArray,
        cellSizeMeters: Float,
    ) {
        val shader = program ?: return
        val board = state.board
        val halfWidth = board.width * cellSizeMeters / 2f
        val halfHeight = board.height * cellSizeMeters / 2f

        shader.use()
        GLES32.glEnable(GLES32.GL_BLEND)
        GLES32.glBlendFunc(GLES32.GL_SRC_ALPHA, GLES32.GL_ONE_MINUS_SRC_ALPHA)

        drawCracks(board, boardModelMatrix, viewMatrix, projectionMatrix, cellSizeMeters, halfWidth, halfHeight)
        drawFailureFlash(
            state.recentlyFailedPositions,
            boardModelMatrix,
            viewMatrix,
            projectionMatrix,
            cellSizeMeters,
            halfWidth,
            halfHeight,
        )
    }

    private fun drawCracks(
        board: Board,
        boardModelMatrix: FloatArray,
        viewMatrix: FloatArray,
        projectionMatrix: FloatArray,
        cellSizeMeters: Float,
        halfWidth: Float,
        halfHeight: Float,
    ) {
        val currentCrackMesh = crackMesh ?: return
        val crackedPositions = board.occupiedPositions()
            .filter { position ->
                board[position]?.state == StructuralState.CRACKED
            }

        for (position in crackedPositions) {
            positionOverlay(
                position,
                boardModelMatrix,
                viewMatrix,
                projectionMatrix,
                cellSizeMeters,
                halfWidth,
                halfHeight,
                depth = crackDepthOffset,
                scale = cellSizeMeters,
            )
            drawMesh(currentCrackMesh, crackColor)
        }
    }

    private fun drawFailureFlash(
        failedPositions: Set<GridPosition>,
        boardModelMatrix: FloatArray,
        viewMatrix: FloatArray,
        projectionMatrix: FloatArray,
        cellSizeMeters: Float,
        halfWidth: Float,
        halfHeight: Float,
    ) {
        val currentFlashMesh = flashMesh ?: return
        for (position in failedPositions) {
            positionOverlay(
                position,
                boardModelMatrix,
                viewMatrix,
                projectionMatrix,
                cellSizeMeters,
                halfWidth,
                halfHeight,
                depth = 0f,
                scale = cellSizeMeters,
            )
            drawMesh(currentFlashMesh, failureFlashColor)
        }
    }

    private fun positionOverlay(
        position: GridPosition,
        boardModelMatrix: FloatArray,
        viewMatrix: FloatArray,
        projectionMatrix: FloatArray,
        cellSizeMeters: Float,
        halfWidth: Float,
        halfHeight: Float,
        depth: Float,
        scale: Float,
    ) {
        val x = -halfWidth + cellSizeMeters * (position.x + 0.5f)
        val y = -halfHeight + cellSizeMeters * (position.y + 0.5f)

        Matrix.setIdentityM(localMatrix, 0)
        Matrix.translateM(localMatrix, 0, x, y, depth)
        Matrix.scaleM(localMatrix, 0, scale, scale, scale)
        Matrix.multiplyMM(modelMatrix, 0, boardModelMatrix, 0, localMatrix, 0)
        Matrix.multiplyMM(viewModelMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewModelMatrix, 0)
    }

    private fun drawMesh(mesh: GpuMesh, color: FloatArray) {
        GLES32.glUniformMatrix4fv(mvpHandle, 1, false, mvpMatrix, 0)
        GLES32.glUniform4f(colorHandle, color[0], color[1], color[2], color[3])
        GLES32.glEnableVertexAttribArray(positionHandle)
        GLES32.glVertexAttribPointer(positionHandle, 3, GLES32.GL_FLOAT, false, 0, mesh.vertices)
        GLES32.glDrawArrays(mesh.drawMode, 0, mesh.vertexCount)
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
