package com.pipefall.pressure.simulation

data class Module(
    val type: ModuleType,
    val cells: List<ModuleCell>,
) {
    init {
        require(cells.size in MIN_CELLS..MAX_CELLS) {
            "module ${type.displayName} must contain $MIN_CELLS to $MAX_CELLS cells"
        }
        require(cells.map { it.offset }.toSet().size == cells.size) {
            "module ${type.displayName} contains duplicate offsets"
        }
        require(cells.all { it.offset.x >= 0 && it.offset.y >= 0 }) {
            "module ${type.displayName} offsets must be normalized"
        }
        require(isConnected()) {
            "module ${type.displayName} cells must be connected"
        }
    }

    val width: Int =
        cells.maxOf { it.offset.x } + 1

    val height: Int =
        cells.maxOf { it.offset.y } + 1

    fun cellMap(): Map<GridPosition, Cell> =
        cells.associate { it.offset to it.cell }

    fun rotatedClockwise(): Module =
        copy(
            cells = cells.map {
                ModuleCell(
                    offset = GridPosition(
                        x = height - 1 - it.offset.y,
                        y = it.offset.x,
                    ),
                    cell = it.cell,
                )
            }.sortedByOffset(),
        )

    fun rotatedClockwise(turns: Int): Module {
        require(turns >= 0) { "turns must be non-negative" }
        return (0 until turns).fold(this) { module, _ ->
            module.rotatedClockwise()
        }
    }

    private fun isConnected(): Boolean {
        val remaining = cells.map { it.offset }.toMutableSet()
        val frontier = ArrayDeque<GridPosition>()
        frontier.add(remaining.first())
        remaining.remove(remaining.first())

        while (frontier.isNotEmpty()) {
            val current = frontier.removeFirst()
            val connectedNeighbors = current.orthogonalNeighbors().filter { it in remaining }
            connectedNeighbors.forEach {
                remaining.remove(it)
                frontier.add(it)
            }
        }

        return remaining.isEmpty()
    }

    companion object {
        const val MIN_CELLS: Int = 2
        const val MAX_CELLS: Int = 6
    }
}

internal fun List<ModuleCell>.sortedByOffset(): List<ModuleCell> =
    sortedWith(compareBy<ModuleCell> { it.offset.y }.thenBy { it.offset.x })
