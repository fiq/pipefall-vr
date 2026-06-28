package com.pipefall.pressure.renderer

import android.opengl.GLES32

class MeshFactory {
    fun createUnitCube(): IntArray =
        intArrayOf(
            GLES32.GL_TRIANGLES,
            36,
        )

    fun createUnitPrism(): IntArray =
        intArrayOf(
            GLES32.GL_TRIANGLES,
            36,
        )
}
