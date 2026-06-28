package com.pipefall.pressure.simulation

data class ActiveModuleState(
    val module: Module,
    val origin: GridPosition,
) {
    fun translated(deltaX: Int, deltaY: Int): ActiveModuleState =
        copy(origin = origin.translated(deltaX, deltaY))

    fun rotatedClockwise(): ActiveModuleState =
        copy(module = module.rotatedClockwise())
}
