package com.pipefall.pressure.debug

import com.pipefall.pressure.simulation.GridPosition

/**
 * Formats a [StatisticsSnapshot] and frame rate into text lines for a debug overlay.
 *
 * Stays pure Kotlin with no Android imports so the formatted output remains
 * unit-testable. Heatmaps render as compact character grids where each occupied
 * cell shows a single intensity character; empty board positions appear as dots.
 *
 * Intensity characters:
 * - `.` — no cell at this position
 * - `0` — cell present but value is zero
 * - `1`–`9` — value clamped to a single digit
 * - `#` — value of ten or above
 */
class Overlay {
    fun render(snapshot: StatisticsSnapshot, fps: Int): List<String> {
        val lines = mutableListOf<String>()
        lines.add("FPS $fps")
        lines.add("Water ${snapshot.waterHeight}")
        lines.add("Max pressure ${snapshot.maxPressure}")
        lines.add("Cracked ${snapshot.crackedCount}")
        lines.add("Failed ${snapshot.failedCount}")
        lines.add("Cells ${snapshot.occupiedCount}")
        lines.add("Ticks ${snapshot.ticksElapsed}")
        if (snapshot.gameOver) {
            lines.add("GAME OVER")
        }
        lines.add("Pressure heatmap:")
        lines.addAll(heatmapLines(snapshot.pressureHeatmap))
        lines.add("Support heatmap:")
        lines.addAll(heatmapLines(snapshot.supportHeatmap))
        return lines
    }

    private fun heatmapLines(heatmap: Map<GridPosition, Int>): List<String> {
        if (heatmap.isEmpty()) {
            return listOf("(empty)")
        }
        val maxX = heatmap.keys.maxOf { it.x }
        val maxY = heatmap.keys.maxOf { it.y }

        return (maxY downTo 0).map { y ->
            (0..maxX).joinToString("") { x ->
                val value = heatmap[GridPosition(x, y)]
                if (value != null) intensityChar(value).toString() else "."
            }
        }
    }

    private fun intensityChar(value: Int): Char = when {
        value <= 0 -> '0'
        value in 1..9 -> '0' + value
        else -> '#'
    }
}
