package com.pipefall.pressure.simulation

import org.junit.Assert.assertEquals
import org.junit.Test

class RotationTests {
    @Test
    fun horizontalBeamRotatesIntoVerticalPillarShape() {
        val beam = EngineeringModules.all.first { it.type == ModuleType.REINFORCED_BEAM }

        val rotated = beam.rotatedClockwise()

        assertEquals(1, rotated.width)
        assertEquals(3, rotated.height)
        assertEquals(
            setOf(
                GridPosition(0, 0),
                GridPosition(0, 1),
                GridPosition(0, 2),
            ),
            rotated.cellMap().keys,
        )
    }

    @Test
    fun buttressClockwiseRotationKeepsStairShapeNormalized() {
        val buttress = EngineeringModules.all.first { it.type == ModuleType.BUTTRESS }

        val rotated = buttress.rotatedClockwise()

        assertEquals(
            setOf(
                GridPosition(0, 0),
                GridPosition(1, 0),
                GridPosition(1, 1),
            ),
            rotated.cellMap().keys,
        )
    }

    @Test
    fun drainBlockRotationPreservesDrainCell() {
        val drainBlock = EngineeringModules.all.first { it.type == ModuleType.DRAIN_BLOCK }

        val rotated = drainBlock.rotatedClockwise()

        assertEquals(Material.DRAIN, rotated.cellMap().getValue(GridPosition(0, 1)).material)
        assertEquals(CellFunction.DRAIN, rotated.cellMap().getValue(GridPosition(0, 1)).function)
    }

    @Test
    fun fourClockwiseRotationsReturnToOriginalShapeAndCells() {
        EngineeringModules.all.forEach { module ->
            val rotated = module.rotatedClockwise(turns = 4)

            assertEquals(module.type, rotated.type)
            assertEquals(module.width, rotated.width)
            assertEquals(module.height, rotated.height)
            assertEquals(module.cellMap(), rotated.cellMap())
        }
    }
}
