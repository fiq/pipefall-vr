package com.pipefall.pressure.renderer

import com.pipefall.pressure.simulation.Board
import com.pipefall.pressure.simulation.SimulationState

class BoardRenderer(
    private val meshFactory: MeshFactory = MeshFactory(),
) {
    val cubeMesh: IntArray = meshFactory.createUnitCube()
    val prismMesh: IntArray = meshFactory.createUnitPrism()

    fun draw(state: SimulationState, viewportWidth: Int, viewportHeight: Int) {
        if (viewportWidth <= 0 || viewportHeight <= 0) {
            return
        }

        val board: Board = state.board
        board.occupiedCount
        state.activeModule?.module?.width
        state.water.height
    }
}
