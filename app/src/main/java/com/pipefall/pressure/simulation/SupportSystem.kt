package com.pipefall.pressure.simulation

class SupportSystem(
    val supportBonus: Int = 2,
    val materialBonus: Int = 1,
    val reinforcementBonus: Int = 3,
    val exposedFacePenalty: Int = 1,
) {
    init {
        require(supportBonus >= 0) { "supportBonus must be non-negative" }
        require(materialBonus >= 0) { "materialBonus must be non-negative" }
        require(reinforcementBonus >= 0) { "reinforcementBonus must be non-negative" }
        require(exposedFacePenalty >= 0) { "exposedFacePenalty must be non-negative" }
    }

    fun effectiveStrengthAt(board: Board, position: GridPosition): Int {
        val cell = requireNotNull(board[position]) { "no cell at $position" }
        val bondedNeighbors = cell.bondedNeighbors.map { neighborPosition ->
            neighborPosition to requireNotNull(board[neighborPosition]) {
                "no cell at $neighborPosition"
            }
        }

        val bondedNeighborCount = bondedNeighbors.size
        val sameMaterialBondCount = bondedNeighbors.count { (_, neighbor) ->
            neighbor.material == cell.material
        }
        val reinforcementNeighborCount = bondedNeighbors.count { (_, neighbor) ->
            neighbor.material == Material.REINFORCEMENT
        }
        val reinforcementSupportBonus =
            if (cell.material == Material.CONCRETE && reinforcementNeighborCount > 0) {
                reinforcementBonus
            } else {
                0
            }
        val exposedFaceCount = 4 - bondedNeighborCount

        return cell.baseStrength +
            bondedNeighborCount * supportBonus +
            sameMaterialBondCount * materialBonus +
            reinforcementSupportBonus -
            exposedFaceCount * exposedFacePenalty
    }

    fun effectiveStrengths(board: Board): Map<GridPosition, Int> =
        board.occupiedPositions().associateWith { position ->
            effectiveStrengthAt(board, position)
        }
}
