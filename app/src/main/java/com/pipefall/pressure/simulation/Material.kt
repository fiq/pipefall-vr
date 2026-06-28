package com.pipefall.pressure.simulation

enum class Material(
    val baseStrength: Int,
    val defaultFunction: CellFunction? = null,
) {
    CONCRETE(baseStrength = 10),
    STEEL(baseStrength = 8),
    DRAIN(baseStrength = 3, defaultFunction = CellFunction.DRAIN),
    SPILLWAY(baseStrength = 6, defaultFunction = CellFunction.SPILLWAY),
    REINFORCEMENT(baseStrength = 5, defaultFunction = CellFunction.REINFORCEMENT),
    SERVICE_SHAFT(baseStrength = 2, defaultFunction = CellFunction.INSPECTION_SHAFT),
}
