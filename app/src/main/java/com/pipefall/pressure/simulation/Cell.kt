package com.pipefall.pressure.simulation

data class Cell(
    val material: Material,
    val baseStrength: Int = material.baseStrength,
    val function: CellFunction? = material.defaultFunction,
    val bondedNeighbors: Set<GridPosition> = emptySet(),
    val state: StructuralState = StructuralState.STABLE,
) {
    init {
        require(baseStrength >= 0) { "baseStrength must be non-negative" }
    }
}
