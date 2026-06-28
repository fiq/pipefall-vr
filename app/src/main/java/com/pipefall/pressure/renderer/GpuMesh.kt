package com.pipefall.pressure.renderer

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

data class GpuMesh(
    val drawMode: Int,
    val vertexCount: Int,
    val vertices: FloatBuffer,
)

fun Mesh.toGpuMesh(): GpuMesh =
    GpuMesh(
        drawMode = when (drawMode) {
            MeshDrawMode.TRIANGLES -> android.opengl.GLES32.GL_TRIANGLES
            MeshDrawMode.LINES -> android.opengl.GLES32.GL_LINES
        },
        vertexCount = vertexCount,
        vertices = vertices.toFloatBuffer(),
    )

private fun FloatArray.toFloatBuffer(): FloatBuffer =
    ByteBuffer.allocateDirect(size * Float.SIZE_BYTES)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()
        .apply {
            put(this@toFloatBuffer)
            position(0)
        }
