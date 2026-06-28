package com.pipefall.pressure.simulation

class Simulation(
    private val waterSystem: WaterSystem = WaterSystem(),
    private val failureSystem: FailureSystem = FailureSystem(),
) {
    fun apply(
        state: SimulationState,
        command: SimulationCommand,
    ): SimulationState =
        when (command) {
            is SimulationCommand.TickWater ->
                advanceWater(state, command.ticks, resolveFailures = false)

            is SimulationCommand.Step ->
                advanceWater(state, command.ticks, resolveFailures = true)
        }

    fun step(
        state: SimulationState,
        ticks: Int = 1,
    ): SimulationState =
        apply(state, SimulationCommand.Step(ticks))

    fun tickWater(
        state: SimulationState,
        ticks: Int = 1,
    ): SimulationState =
        apply(state, SimulationCommand.TickWater(ticks))

    private fun advanceWater(
        state: SimulationState,
        ticks: Int,
        resolveFailures: Boolean,
    ): SimulationState {
        require(ticks >= 0) { "ticks must be non-negative" }
        if (ticks == 0 || state.gameOver) {
            return state
        }

        val nextWater = waterSystem.advance(state.water, ticks)
        val nextBoard =
            if (resolveFailures) {
                failureSystem.resolve(state.board, nextWater.height)
            } else {
                state.board
            }
        val nextGameOver = state.gameOver || waterSystem.isAtTop(nextWater)

        return state.copy(
            board = nextBoard,
            water = nextWater,
            gameOver = nextGameOver,
            ticksElapsed = state.ticksElapsed + ticks,
        )
    }
}
