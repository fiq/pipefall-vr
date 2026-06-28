package com.pipefall.pressure.renderer

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MeshFactoryTest {
    @Test
    fun boardSurfaceIsCenteredAndSizedFromTheGrid() {
        val mesh = MeshFactory().createBoardSurface(columns = 12, rows = 20, cellSizeMeters = 0.12f)

        assertEquals(MeshDrawMode.TRIANGLES, mesh.drawMode)
        assertEquals(6, mesh.vertexCount)
        assertEquals(-0.72f, mesh.vertices[0], 0.0001f)
        assertEquals(-1.2f, mesh.vertices[1], 0.0001f)
        assertEquals(0f, mesh.vertices[2], 0.0001f)
        assertEquals(0.72f, mesh.vertices[15], 0.0001f)
        assertEquals(1.2f, mesh.vertices[16], 0.0001f)
        assertEquals(0f, mesh.vertices[17], 0.0001f)
    }

    @Test
    fun boardGridIncludesEachBoardBoundaryLine() {
        val mesh = MeshFactory().createBoardGrid(columns = 12, rows = 20, cellSizeMeters = 0.12f)

        assertEquals(MeshDrawMode.LINES, mesh.drawMode)
        assertEquals(68, mesh.vertexCount)
        assertTrue(mesh.vertices.contains(-0.72f))
        assertTrue(mesh.vertices.contains(0.72f))
        assertTrue(mesh.vertices.contains(-1.2f))
        assertTrue(mesh.vertices.contains(1.2f))
    }

    private fun FloatArray.contains(value: Float): Boolean =
        any { kotlin.math.abs(it - value) < 0.0001f }
}
