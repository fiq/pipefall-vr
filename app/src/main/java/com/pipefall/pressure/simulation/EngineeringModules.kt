package com.pipefall.pressure.simulation

object EngineeringModules {
    val all: List<Module> = listOf(
        module(
            type = ModuleType.CONCRETE_SLAB,
            rows = listOf(
                "XX",
                "XX",
            ),
            cellForSymbol = { concrete(baseStrength = 12) },
        ),
        module(
            type = ModuleType.REINFORCED_BEAM,
            rows = listOf("XXX"),
            cellForSymbol = { concrete(baseStrength = 11) },
        ),
        module(
            type = ModuleType.SHORT_PILLAR,
            rows = listOf(
                "X",
                "X",
                "X",
            ),
            cellForSymbol = { concrete(baseStrength = 10) },
        ),
        module(
            type = ModuleType.BUTTRESS,
            rows = listOf(
                "X.",
                "XX",
            ),
            cellForSymbol = { concrete(baseStrength = 10) },
        ),
        module(
            type = ModuleType.CORNER_RETAINING_SECTION,
            rows = listOf(
                "XX",
                "X.",
            ),
            cellForSymbol = { concrete(baseStrength = 10) },
        ),
        module(
            type = ModuleType.DRAIN_BLOCK,
            rows = listOf("CDC"),
            cellForSymbol = { symbol ->
                when (symbol) {
                    'C' -> concrete(baseStrength = 8)
                    'D' -> Cell(Material.DRAIN)
                    else -> error("Unsupported symbol $symbol")
                }
            },
        ),
        module(
            type = ModuleType.SPILLWAY,
            rows = listOf("XXXX"),
            cellForSymbol = { Cell(Material.SPILLWAY) },
        ),
        module(
            type = ModuleType.REINFORCEMENT_CAGE,
            rows = listOf("XX"),
            cellForSymbol = { Cell(Material.REINFORCEMENT) },
        ),
        module(
            type = ModuleType.PRESSURE_RELIEF_CHAMBER,
            rows = listOf(
                "XX",
                ".X",
            ),
            cellForSymbol = {
                concrete(
                    baseStrength = 6,
                    function = CellFunction.PRESSURE_RELIEF,
                )
            },
        ),
        module(
            type = ModuleType.INSPECTION_SHAFT,
            rows = listOf(
                "X",
                "X",
            ),
            cellForSymbol = { Cell(Material.SERVICE_SHAFT) },
        ),
    )

    private fun concrete(
        baseStrength: Int,
        function: CellFunction? = null,
    ): Cell = Cell(
        material = Material.CONCRETE,
        baseStrength = baseStrength,
        function = function,
    )

    private fun module(
        type: ModuleType,
        rows: List<String>,
        cellForSymbol: (Char) -> Cell,
    ): Module {
        val height = rows.size
        val cells = rows.flatMapIndexed { rowIndex, row ->
            val y = height - rowIndex - 1
            row.mapIndexedNotNull { x, symbol ->
                if (symbol == '.') {
                    null
                } else {
                    ModuleCell(
                        offset = GridPosition(x, y),
                        cell = cellForSymbol(symbol),
                    )
                }
            }
        }
        return Module(type, cells)
    }
}
