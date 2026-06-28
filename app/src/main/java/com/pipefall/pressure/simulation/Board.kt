package com.pipefall.pressure.simulation

class Board private constructor(
    val width: Int,
    val height: Int,
    private val lockedCells: Map<GridPosition, Cell>,
) {
    init {
        require(width > 0) { "width must be positive" }
        require(height > 0) { "height must be positive" }
        require(lockedCells.keys.all(::isInBounds)) { "all cells must be in bounds" }
    }

    val occupiedCount: Int
        get() = lockedCells.size

    fun isInBounds(position: GridPosition): Boolean =
        position.x in 0 until width && position.y in 0 until height

    fun isEmpty(position: GridPosition): Boolean =
        isInBounds(position) && position !in lockedCells

    operator fun get(position: GridPosition): Cell? =
        lockedCells[position]

    fun place(position: GridPosition, cell: Cell): Board {
        require(isInBounds(position)) { "position $position is out of bounds" }
        require(position !in lockedCells) { "position $position is already occupied" }
        return Board(width, height, lockedCells + (position to cell))
    }

    fun placeAll(cells: Map<GridPosition, Cell>): Board {
        require(cells.keys.all(::isInBounds)) { "all cells must be in bounds" }
        require(cells.keys.none { it in lockedCells }) { "cells overlap existing board cells" }
        return Board(width, height, lockedCells + cells)
    }

    fun positionedCells(module: Module, origin: GridPosition): Map<GridPosition, Cell> =
        module.cells.associate {
            it.offset.translated(origin.x, origin.y) to it.cell
        }

    fun collides(module: Module, origin: GridPosition): Boolean =
        positionedCells(module, origin).keys.any { position ->
            !isInBounds(position) || position in lockedCells
        }

    fun canPlace(module: Module, origin: GridPosition): Boolean =
        !collides(module, origin)

    fun lock(module: Module, origin: GridPosition): Board {
        require(canPlace(module, origin)) {
            "module ${module.type.displayName} collides at origin $origin"
        }
        return placeAll(positionedCells(module, origin)).withRecalculatedBonds()
    }

    fun remove(position: GridPosition): Board {
        require(isInBounds(position)) { "position $position is out of bounds" }
        return if (position in lockedCells) {
            Board(width, height, lockedCells - position)
        } else {
            this
        }
    }

    fun occupiedPositions(): List<GridPosition> =
        lockedCells.keys.sortedWith(compareBy<GridPosition> { it.y }.thenBy { it.x })

    fun cells(): Map<GridPosition, Cell> =
        lockedCells.toMap()

    fun bondedNeighborPositions(position: GridPosition): Set<GridPosition> {
        require(isInBounds(position)) { "position $position is out of bounds" }
        return position
            .orthogonalNeighbors()
            .filter { it in lockedCells }
            .toSet()
    }

    fun withRecalculatedBonds(): Board =
        Board(
            width = width,
            height = height,
            lockedCells = lockedCells.mapValues { (position, cell) ->
                cell.copy(bondedNeighbors = bondedNeighborPositions(position))
            },
        )

    companion object {
        const val DEFAULT_WIDTH: Int = 12
        const val DEFAULT_HEIGHT: Int = 20

        fun empty(
            width: Int = DEFAULT_WIDTH,
            height: Int = DEFAULT_HEIGHT,
        ): Board = Board(width, height, emptyMap())
    }
}
