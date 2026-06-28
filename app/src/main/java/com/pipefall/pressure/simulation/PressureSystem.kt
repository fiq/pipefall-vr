package com.pipefall.pressure.simulation

class PressureSystem {
    fun pressureAt(cellY: Int, waterHeight: Int): Int {
        require(cellY >= 0) { "cellY must be non-negative" }
        require(waterHeight >= 0) { "waterHeight must be non-negative" }
        return maxOf(0, waterHeight - cellY)
    }

    fun pressureAt(position: GridPosition, waterHeight: Int): Int =
        pressureAt(position.y, waterHeight)

    fun pressuresFor(board: Board, waterHeight: Int): Map<GridPosition, Int> =
        board.occupiedPositions().associateWith { position ->
            pressureAt(position, waterHeight)
        }
}
