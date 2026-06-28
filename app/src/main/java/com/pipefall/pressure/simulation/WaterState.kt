package com.pipefall.pressure.simulation

data class WaterState(
    val height: Int = 0,
    val tickRemainder: Int = 0,
) {
    init {
        require(height >= 0) { "height must be non-negative" }
        require(tickRemainder >= 0) { "tickRemainder must be non-negative" }
    }
}
