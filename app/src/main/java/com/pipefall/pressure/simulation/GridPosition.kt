package com.pipefall.pressure.simulation

data class GridPosition(
    val x: Int,
    val y: Int,
) {
    fun translated(deltaX: Int, deltaY: Int): GridPosition =
        GridPosition(x + deltaX, y + deltaY)

    fun orthogonalNeighbors(): List<GridPosition> =
        listOf(
            translated(-1, 0),
            translated(1, 0),
            translated(0, -1),
            translated(0, 1),
        )
}
