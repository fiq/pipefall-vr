package com.pipefall.pressure.simulation

class FailureSystem(
    private val pressureSystem: PressureSystem = PressureSystem(),
    private val supportSystem: SupportSystem = SupportSystem(),
) {
    fun resolve(board: Board, waterHeight: Int): Board {
        require(waterHeight >= 0) { "waterHeight must be non-negative" }

        var currentBoard = board.withRecalculatedBonds()

        while (true) {
            val nextBoard = resolveOnce(currentBoard, waterHeight)
            if (nextBoard.cells() == currentBoard.cells()) {
                return currentBoard
            }
            currentBoard = nextBoard
        }
    }

    private fun resolveOnce(board: Board, waterHeight: Int): Board {
        val pressureByPosition = pressureSystem.pressuresFor(board, waterHeight)
        val effectiveStrengthByPosition = supportSystem.effectiveStrengths(board)
        val crackedPositions = mutableSetOf<GridPosition>()
        val failedPositions = mutableSetOf<GridPosition>()

        board.occupiedPositions().forEach { position ->
            val pressure = pressureByPosition.getValue(position)
            val effectiveStrength = effectiveStrengthByPosition.getValue(position)

            if (pressure.toLong() * 2 > effectiveStrength.toLong() * 3) {
                failedPositions += position
            } else if (pressure > effectiveStrength) {
                crackedPositions += position
            }
        }

        val updatedCells = board.cells().toMutableMap()
        crackedPositions.forEach { position ->
            updatedCells[position] = updatedCells.getValue(position).copy(state = StructuralState.CRACKED)
        }
        failedPositions.forEach { position ->
            updatedCells.remove(position)
        }

        return Board.empty(width = board.width, height = board.height)
            .placeAll(updatedCells)
            .withRecalculatedBonds()
    }
}
