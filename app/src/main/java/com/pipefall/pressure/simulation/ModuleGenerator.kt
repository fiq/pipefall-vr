package com.pipefall.pressure.simulation

class ModuleGenerator(
    seed: Int = 0,
    private val modules: List<Module> = EngineeringModules.all,
) {
    private val startIndex: Int

    init {
        require(modules.isNotEmpty()) { "modules must not be empty" }
        startIndex = Math.floorMod(seed, modules.size)
    }

    fun moduleAt(spawnIndex: Int): Module {
        require(spawnIndex >= 0) { "spawnIndex must be non-negative" }
        return modules[Math.floorMod(startIndex + spawnIndex, modules.size)]
    }

    fun sequence(count: Int): List<Module> {
        require(count >= 0) { "count must be non-negative" }
        return List(count, ::moduleAt)
    }
}
