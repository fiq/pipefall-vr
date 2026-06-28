package com.pipefall.pressure.renderer

import android.opengl.GLES32
import android.opengl.Matrix
import com.pipefall.pressure.simulation.SimulationState

class WaterRenderer(
    private val meshFactory: MeshFactory = MeshFactory(),
) {
    private val waterColor = floatArrayOf(0.18f, 0.44f, 0.78f, 0.42f)
    private val waterOffsetMeters = 0.018f

    private var program: GlProgram? = null
    private var positionHandle = 0
    private var mvpHandle = 0
    private var colorHandle = 0
    private var waterMesh: GpuMesh? = null

    private val waterLocalMatrix = FloatArray(16)
    private val waterModelMatrix = FloatArray(16)
    private val viewModelMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)

    fun onSurfaceCreated() {
        program = GlProgram(VERTEX_SHADER, FRAGMENT_SHADER).also { shader ->
            positionHandle = shader.attributeLocation("aPosition")
            mvpHandle = shader.uniformLocation("uMvpMatrix")
            colorHandle = shader.uniformLocation("uColor")
        }

        if (waterMesh == null) {
            waterMesh = meshFactory.createUnitQuad().toGpuMesh()
        }
    }

    fun draw(
        state: SimulationState,
        boardModelMatrix: FloatArray,
        viewMatrix: FloatArray,
        projectionMatrix: FloatArray,
        cellSizeMeters: Float,
    ) {
        val currentWaterMesh = waterMesh ?: return
        val waterHeight = state.water.height.coerceAtMost(state.board.height)
        if (waterHeight <= 0) {
            return
        }

        val halfWidth = state.board.width * cellSizeMeters / 2f
        val halfHeight = state.board.height * cellSizeMeters / 2f
        val waterHeightMeters = waterHeight * cellSizeMeters

        val shader = program ?: return
        shader.use()
        GLES32.glEnable(GLES32.GL_BLEND)
        GLES32.glBlendFunc(GLES32.GL_SRC_ALPHA, GLES32.GL_ONE_MINUS_SRC_ALPHA)

        Matrix.setIdentityM(waterLocalMatrix, 0)
        Matrix.translateM(
            waterLocalMatrix,
            0,
            0f,
            -halfHeight + waterHeightMeters / 2f,
            -waterOffsetMeters,
        )
        Matrix.scaleM(waterLocalMatrix, 0, halfWidth * 2f, waterHeightMeters, 1f)
        Matrix.multiplyMM(waterModelMatrix, 0, boardModelMatrix, 0, waterLocalMatrix, 0)
        Matrix.multiplyMM(viewModelMatrix, 0, viewMatrix, 0, waterModelMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewModelMatrix, 0)

        GLES32.glUniformMatrix4fv(mvpHandle, 1, false, mvpMatrix, 0)
        GLES32.glUniform4f(colorHandle, waterColor[0], waterColor[1], waterColor[2], waterColor[3])
        GLES32.glEnableVertexAttribArray(positionHandle)
        GLES32.glVertexAttribPointer(positionHandle, 3, GLES32.GL_FLOAT, false, 0, currentWaterMesh.vertices)
        GLES32.glDrawArrays(currentWaterMesh.drawMode, 0, currentWaterMesh.vertexCount)
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
