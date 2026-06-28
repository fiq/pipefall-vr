package com.pipefall.pressure.simulation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ModuleGeneratorTest {
    @Test
    fun allEngineeringModulesMatchRequiredCatalog() {
        assertEquals(
            ModuleType.entries.toList(),
            EngineeringModules.all.map { it.type },
        )
    }

    @Test
    fun allEngineeringModulesHaveValidConnectedCellCounts() {
        EngineeringModules.all.forEach { module ->
            assertTrue(
                "${module.type.displayName} has invalid cell count",
                module.cells.size in Module.MIN_CELLS..Module.MAX_CELLS,
            )
            assertEquals(
                "${module.type.displayName} should expose unique cells",
                module.cells.size,
                module.cellMap().size,
            )
        }
    }

    @Test
    fun concreteSlabUsesTwoByTwoConcreteShape() {
        val slab = EngineeringModules.all.first { it.type == ModuleType.CONCRETE_SLAB }

        assertEquals(2, slab.width)
        assertEquals(2, slab.height)
        assertEquals(
            setOf(
                GridPosition(0, 0),
                GridPosition(1, 0),
                GridPosition(0, 1),
                GridPosition(1, 1),
            ),
            slab.cellMap().keys,
        )
        assertTrue(slab.cells.all { it.cell.material == Material.CONCRETE })
    }

    @Test
    fun drainBlockHasWeakDrainInCenter() {
        val drainBlock = EngineeringModules.all.first { it.type == ModuleType.DRAIN_BLOCK }
        val cells = drainBlock.cellMap()

        assertEquals(Material.CONCRETE, cells.getValue(GridPosition(0, 0)).material)
        assertEquals(Material.DRAIN, cells.getValue(GridPosition(1, 0)).material)
        assertEquals(CellFunction.DRAIN, cells.getValue(GridPosition(1, 0)).function)
        assertEquals(Material.CONCRETE, cells.getValue(GridPosition(2, 0)).material)
    }

    @Test
    fun generatorUsesSeededDeterministicCycle() {
        val generator = ModuleGenerator(seed = 8)

        assertEquals(ModuleType.PRESSURE_RELIEF_CHAMBER, generator.moduleAt(0).type)
        assertEquals(ModuleType.INSPECTION_SHAFT, generator.moduleAt(1).type)
        assertEquals(ModuleType.CONCRETE_SLAB, generator.moduleAt(2).type)
        assertEquals(
            listOf(
                ModuleType.PRESSURE_RELIEF_CHAMBER,
                ModuleType.INSPECTION_SHAFT,
                ModuleType.CONCRETE_SLAB,
                ModuleType.REINFORCED_BEAM,
            ),
            generator.sequence(4).map { it.type },
        )
    }
}
