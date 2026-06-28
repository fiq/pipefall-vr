package com.pipefall.pressure.simulation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WaterSystemTest {
    @Test
    fun defaultWaterStateStartsEmpty() {
        val state = WaterState()

        assertEquals(0, state.height)
        assertEquals(0, state.tickRemainder)
    }

    @Test
    fun waterCarriesPartialTicksBeforeRiseInterval() {
        val water = WaterSystem(ticksPerRise = 3)

        val state = water.advance(WaterState(), ticks = 2)

        assertEquals(WaterState(height = 0, tickRemainder = 2), state)
        assertFalse(water.isAtTop(state))
    }

    @Test
    fun waterRisesWhenIntervalCompletes() {
        val water = WaterSystem(ticksPerRise = 3)
        val partial = WaterState(height = 0, tickRemainder = 2)

        val state = water.advance(partial, ticks = 1)

        assertEquals(WaterState(height = 1, tickRemainder = 0), state)
    }

    @Test
    fun waterCanRiseMultipleStepsInOneAdvance() {
        val water = WaterSystem(ticksPerRise = 2, risePerStep = 2, maxHeight = 20)

        val state = water.advance(WaterState(height = 3, tickRemainder = 1), ticks = 7)

        assertEquals(WaterState(height = 11, tickRemainder = 0), state)
    }

    @Test
    fun waterClampsAtTopAndDiscardsRemainder() {
        val water = WaterSystem(ticksPerRise = 2, maxHeight = 5)

        val state = water.advance(WaterState(height = 4, tickRemainder = 1), ticks = 5)

        assertEquals(WaterState(height = 5, tickRemainder = 0), state)
        assertTrue(water.isAtTop(state))
    }

    @Test
    fun zeroTicksLeaveWaterUnchanged() {
        val water = WaterSystem(ticksPerRise = 2)
        val state = WaterState(height = 3, tickRemainder = 1)

        assertEquals(state, water.advance(state, ticks = 0))
    }

    @Test(expected = IllegalArgumentException::class)
    fun negativeTicksFail() {
        WaterSystem().advance(WaterState(), ticks = -1)
    }

    @Test(expected = IllegalArgumentException::class)
    fun invalidRiseIntervalFails() {
        WaterSystem(ticksPerRise = 0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun unnormalizedStateRemainderFails() {
        WaterSystem(ticksPerRise = 3).advance(WaterState(tickRemainder = 3))
    }
}
