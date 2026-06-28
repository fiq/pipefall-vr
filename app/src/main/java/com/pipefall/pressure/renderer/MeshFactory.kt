package com.pipefall.pressure.renderer

enum class MeshDrawMode {
    TRIANGLES,
    LINES,
}

data class Mesh(
    val drawMode: MeshDrawMode,
    val vertices: FloatArray,
) {
    val vertexCount: Int
        get() = vertices.size / POSITION_COMPONENTS

    companion object {
        private const val POSITION_COMPONENTS = 3
    }
}

class MeshFactory {
    private val gridDepthOffset = 0.001f

    fun createUnitCube(): Mesh =
        Mesh(
            drawMode = MeshDrawMode.TRIANGLES,
            vertices =
                floatArrayOf(
                    -0.5f, -0.5f, 0.5f,
                    0.5f, -0.5f, 0.5f,
                    -0.5f, 0.5f, 0.5f,
                    -0.5f, 0.5f, 0.5f,
                    0.5f, -0.5f, 0.5f,
                    0.5f, 0.5f, 0.5f,
                    -0.5f, -0.5f, -0.5f,
                    -0.5f, 0.5f, -0.5f,
                    0.5f, -0.5f, -0.5f,
                    -0.5f, 0.5f, -0.5f,
                    0.5f, 0.5f, -0.5f,
                    0.5f, -0.5f, -0.5f,
                    -0.5f, -0.5f, -0.5f,
                    -0.5f, -0.5f, 0.5f,
                    -0.5f, 0.5f, -0.5f,
                    -0.5f, -0.5f, 0.5f,
                    -0.5f, 0.5f, 0.5f,
                    -0.5f, 0.5f, -0.5f,
                    0.5f, -0.5f, -0.5f,
                    0.5f, 0.5f, -0.5f,
                    0.5f, -0.5f, 0.5f,
                    0.5f, -0.5f, 0.5f,
                    0.5f, 0.5f, -0.5f,
                    0.5f, 0.5f, 0.5f,
                    -0.5f, 0.5f, -0.5f,
                    -0.5f, 0.5f, 0.5f,
                    0.5f, 0.5f, -0.5f,
                    -0.5f, 0.5f, 0.5f,
                    0.5f, 0.5f, 0.5f,
                    0.5f, 0.5f, -0.5f,
                    -0.5f, -0.5f, -0.5f,
                    0.5f, -0.5f, -0.5f,
                    -0.5f, -0.5f, 0.5f,
                    -0.5f, -0.5f, 0.5f,
                    0.5f, -0.5f, -0.5f,
                    0.5f, -0.5f, 0.5f,
                ),
        )

    fun createBoardSurface(
        columns: Int,
        rows: Int,
        cellSizeMeters: Float,
    ): Mesh {
        val boardWidth = columns * cellSizeMeters
        val boardHeight = rows * cellSizeMeters
        val halfWidth = boardWidth / 2f
        val halfHeight = boardHeight / 2f

        return Mesh(
            drawMode = MeshDrawMode.TRIANGLES,
            vertices =
                floatArrayOf(
                    -halfWidth, -halfHeight, 0f,
                    halfWidth, -halfHeight, 0f,
                    -halfWidth, halfHeight, 0f,
                    -halfWidth, halfHeight, 0f,
                    halfWidth, -halfHeight, 0f,
                    halfWidth, halfHeight, 0f,
                ),
        )
    }

    fun createBoardGrid(
        columns: Int,
        rows: Int,
        cellSizeMeters: Float,
    ): Mesh {
        val boardWidth = columns * cellSizeMeters
        val boardHeight = rows * cellSizeMeters
        val halfWidth = boardWidth / 2f
        val halfHeight = boardHeight / 2f
        val vertices = ArrayList<Float>((columns + rows + 2) * 12)

        for (column in 0..columns) {
            val x = -halfWidth + column * cellSizeMeters
            vertices.add(x)
            vertices.add(-halfHeight)
            vertices.add(0f)
            vertices.add(x)
            vertices.add(halfHeight)
            vertices.add(0f)
        }

        for (row in 0..rows) {
            val y = -halfHeight + row * cellSizeMeters
            vertices.add(-halfWidth)
            vertices.add(y)
            vertices.add(gridDepthOffset)
            vertices.add(halfWidth)
            vertices.add(y)
            vertices.add(gridDepthOffset)
        }

        return Mesh(
            drawMode = MeshDrawMode.LINES,
            vertices = vertices.toFloatArray(),
        )
    }

    private fun ArrayList<Float>.toFloatArray(): FloatArray =
        FloatArray(size) { index -> get(index) }
}
