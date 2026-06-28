package com.pipefall.pressure.simulation

class WaterSystem(
    val ticksPerRise: Int = DEFAULT_TICKS_PER_RISE,
    val risePerStep: Int = 1,
    val maxHeight: Int = Board.DEFAULT_HEIGHT,
) {
    init {
        require(ticksPerRise > 0) { "ticksPerRise must be positive" }
        require(risePerStep > 0) { "risePerStep must be positive" }
        require(maxHeight >= 0) { "maxHeight must be non-negative" }
    }

    fun isAtTop(state: WaterState): Boolean =
        state.height >= maxHeight

    fun advance(state: WaterState, ticks: Int = 1): WaterState {
        require(ticks >= 0) { "ticks must be non-negative" }
        require(state.height <= maxHeight) { "state height must not exceed maxHeight" }
        require(state.tickRemainder < ticksPerRise) {
            "state tickRemainder must be less than ticksPerRise"
        }

        if (ticks == 0) {
            return state
        }

        if (isAtTop(state)) {
            return state.copy(height = maxHeight, tickRemainder = 0)
        }

        val totalTicks = state.tickRemainder.toLong() + ticks.toLong()
        val rises = totalTicks / ticksPerRise.toLong()
        val remainder = (totalTicks % ticksPerRise.toLong()).toInt()
        val rawHeight = state.height.toLong() + rises * risePerStep.toLong()
        val nextHeight = minOf(maxHeight.toLong(), rawHeight).toInt()

        return WaterState(
            height = nextHeight,
            tickRemainder = if (nextHeight == maxHeight) 0 else remainder,
        )
    }

    companion object {
        const val DEFAULT_TICKS_PER_RISE: Int = 4
    }
}
