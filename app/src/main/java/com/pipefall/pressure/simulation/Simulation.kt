package com.pipefall.pressure.simulation

class Simulation(
    private val moduleGenerator: ModuleGenerator = ModuleGenerator(),
    private val waterSystem: WaterSystem = WaterSystem(),
    private val failureSystem: FailureSystem = FailureSystem(),
) {
    fun apply(
        state: SimulationState,
        command: SimulationCommand,
    ): SimulationState =
        when (command) {
            SimulationCommand.Spawn ->
                spawn(state)

            SimulationCommand.MoveLeft ->
                move(state, deltaX = -1)

            SimulationCommand.MoveRight ->
                move(state, deltaX = 1)

            SimulationCommand.RotateClockwise ->
                rotate(state)

            SimulationCommand.HardDrop ->
                hardDrop(state)

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

    fun spawn(state: SimulationState): SimulationState {
        if (state.gameOver || state.activeModule != null) {
            return state
        }

        val module = moduleGenerator.moduleAt(state.nextSpawnIndex)
        val origin = spawnOriginFor(state.board, module)

        return if (state.board.canPlace(module, origin)) {
            state.copy(
                activeModule = ActiveModuleState(module = module, origin = origin),
            )
        } else {
            state.copy(gameOver = true)
        }
    }

    private fun move(
        state: SimulationState,
        deltaX: Int,
    ): SimulationState {
        val activeModule = state.activeModule ?: return state
        val moved = activeModule.translated(deltaX, 0)
        return if (state.board.canPlace(moved.module, moved.origin)) {
            state.copy(activeModule = moved)
        } else {
            state
        }
    }

    private fun rotate(state: SimulationState): SimulationState {
        val activeModule = state.activeModule ?: return state
        val rotated = activeModule.rotatedClockwise()
        return if (state.board.canPlace(rotated.module, rotated.origin)) {
            state.copy(activeModule = rotated)
        } else {
            state
        }
    }

    private fun hardDrop(state: SimulationState): SimulationState {
        val activeModule = state.activeModule ?: return state
        if (!state.board.canPlace(activeModule.module, activeModule.origin)) {
            return state.copy(gameOver = true)
        }

        var landing = activeModule.origin
        while (state.board.canPlace(activeModule.module, landing.translated(0, -1))) {
            landing = landing.translated(0, -1)
        }

        return state.copy(
            board = state.board.lock(activeModule.module, landing),
            activeModule = null,
            nextSpawnIndex = state.nextSpawnIndex + 1,
        )
    }

    private fun spawnOriginFor(board: Board, module: Module): GridPosition =
        GridPosition(
            x = (board.width - module.width) / 2,
            y = board.height - module.height,
        )
}
