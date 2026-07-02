package com.pipefall.pressure.debug

import com.pipefall.pressure.simulation.GridPosition
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OverlayTest {
    private val overlay = Overlay()

    @Test
    fun rendersScalarLinesForEmptySnapshot() {
        val snapshot = StatisticsSnapshot(
            waterHeight = 0,
            maxPressure = 0,
            crackedCount = 0,
            failedCount = 0,
            occupiedCount = 0,
            gameOver = false,
            ticksElapsed = 0,
            pressureHeatmap = emptyMap(),
            supportHeatmap = emptyMap(),
        )

        val lines = overlay.render(snapshot, fps = 60)

        assertEquals("FPS 60", lines[0])
        assertEquals("Water 0", lines[1])
        assertEquals("Max pressure 0", lines[2])
        assertEquals("Cracked 0", lines[3])
        assertEquals("Failed 0", lines[4])
        assertEquals("Cells 0", lines[5])
        assertEquals("Ticks 0", lines[6])
        assertTrue(lines.none { it == "GAME OVER" })
    }

    @Test
    fun rendersGameOverLineWhenGameOver() {
        val snapshot = StatisticsSnapshot(
            waterHeight = 20,
            maxPressure = 0,
            crackedCount = 0,
            failedCount = 0,
            occupiedCount = 0,
            gameOver = true,
            ticksElapsed = 99,
            pressureHeatmap = emptyMap(),
            supportHeatmap = emptyMap(),
        )

        val lines = overlay.render(snapshot, fps = 30)

        assertTrue(lines.contains("GAME OVER"))
        assertEquals("Ticks 99", lines[6])
    }

    @Test
    fun rendersScalarValuesFromSnapshot() {
        val snapshot = StatisticsSnapshot(
            waterHeight = 7,
            maxPressure = 5,
            crackedCount = 3,
            failedCount = 2,
            occupiedCount = 12,
            gameOver = false,
            ticksElapsed = 42,
            pressureHeatmap = emptyMap(),
            supportHeatmap = emptyMap(),
        )

        val lines = overlay.render(snapshot, fps = 90)

        assertEquals("Water 7", lines[1])
        assertEquals("Max pressure 5", lines[2])
        assertEquals("Cracked 3", lines[3])
        assertEquals("Failed 2", lines[4])
        assertEquals("Cells 12", lines[5])
        assertEquals("Ticks 42", lines[6])
    }

    @Test
    fun rendersEmptyHeatmapAsEmptyLabel() {
        val snapshot = StatisticsSnapshot(
            waterHeight = 0,
            maxPressure = 0,
            crackedCount = 0,
            failedCount = 0,
            occupiedCount = 0,
            gameOver = false,
            ticksElapsed = 0,
            pressureHeatmap = emptyMap(),
            supportHeatmap = emptyMap(),
        )

        val lines = overlay.render(snapshot, fps = 0)

        val pressureIndex = lines.indexOf("Pressure heatmap:")
        val supportIndex = lines.indexOf("Support heatmap:")
        assertTrue(pressureIndex >= 0)
        assertTrue(supportIndex > pressureIndex)
        assertEquals("(empty)", lines[pressureIndex + 1])
        assertEquals("(empty)", lines[supportIndex + 1])
    }

    @Test
    fun rendersPressureHeatmapWithIntensityCharacters() {
        val pressureHeatmap = mapOf(
            GridPosition(0, 0) to 5,
            GridPosition(1, 0) to 12,
            GridPosition(0, 1) to 0,
        )
        val snapshot = StatisticsSnapshot(
            waterHeight = 5,
            maxPressure = 12,
            crackedCount = 0,
            failedCount = 0,
            occupiedCount = 3,
            gameOver = false,
            ticksElapsed = 0,
            pressureHeatmap = pressureHeatmap,
            supportHeatmap = emptyMap(),
        )

        val lines = overlay.render(snapshot, fps = 0)

        val pressureIndex = lines.indexOf("Pressure heatmap:")
        // Row y=1 is rendered first (top to bottom), then y=0.
        // Position (1,1) is not in the heatmap, so it renders as a dot.
        assertEquals("0.", lines[pressureIndex + 1])
        assertEquals("5#", lines[pressureIndex + 2])
    }

    @Test
    fun rendersSupportHeatmapWithIntensityCharacters() {
        val supportHeatmap = mapOf(
            GridPosition(0, 0) to 3,
            GridPosition(1, 0) to 9,
            GridPosition(2, 0) to 10,
        )
        val snapshot = StatisticsSnapshot(
            waterHeight = 0,
            maxPressure = 0,
            crackedCount = 0,
            failedCount = 0,
            occupiedCount = 3,
            gameOver = false,
            ticksElapsed = 0,
            pressureHeatmap = emptyMap(),
            supportHeatmap = supportHeatmap,
        )

        val lines = overlay.render(snapshot, fps = 0)

        val supportIndex = lines.indexOf("Support heatmap:")
        assertEquals("39#", lines[supportIndex + 1])
    }
}
