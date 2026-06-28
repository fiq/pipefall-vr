package com.pipefall.pressure.simulation

enum class ModuleType(
    val displayName: String,
) {
    CONCRETE_SLAB("Concrete Slab"),
    REINFORCED_BEAM("Reinforced Beam"),
    SHORT_PILLAR("Short Pillar"),
    BUTTRESS("Buttress"),
    CORNER_RETAINING_SECTION("Corner Retaining Section"),
    DRAIN_BLOCK("Drain Block"),
    SPILLWAY("Spillway"),
    REINFORCEMENT_CAGE("Reinforcement Cage"),
    PRESSURE_RELIEF_CHAMBER("Pressure Relief Chamber"),
    INSPECTION_SHAFT("Inspection Shaft"),
}
