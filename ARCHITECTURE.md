# Pressure Architecture

## Ralph Loop Rule

Every implementation task must start by reading:

1. `GAME_SPEC.md`
2. `ARCHITECTURE.md`
3. `TODO.md`
4. `FAILURES.md`

Then implement exactly one TODO item, build, fix compile errors, run tests, fix test failures, update `TODO.md`, update `FAILURES.md`, update `BLOG.md`, commit, and stop.

## Design Goals

- Keep gameplay deterministic.
- Keep simulation free of Android, Quest, OpenXR, and rendering dependencies.
- Keep rendering replaceable.
- Keep input translation separate from game rules.
- Make every simulation rule testable with plain JUnit.
- Keep work increments small enough for one Ralph loop.

## Project Layout

Target Android project layout:

```text
settings.gradle.kts
build.gradle.kts
app/
  build.gradle.kts
  src/main/
    AndroidManifest.xml
    java/com/pipefall/pressure/
      QuestActivity.kt
      renderer/
      debug/
  src/test/
    java/com/pipefall/pressure/
      simulation/
```

Simulation code lives under:

```text
app/src/main/java/com/pipefall/pressure/simulation/
```

Renderer code lives under:

```text
app/src/main/java/com/pipefall/pressure/renderer/
```

Debug UI code lives under:

```text
app/src/main/java/com/pipefall/pressure/debug/
```

## Package Boundaries

### simulation

Owns deterministic game state and rules.

Expected classes:

- `Board`
- `Cell`
- `GridPosition`
- `Material`
- `Module`
- `ModuleCell`
- `ModuleGenerator`
- `PressureSystem`
- `SupportSystem`
- `FailureSystem`
- `GameRules`
- `Simulation`

Rules:

- No Android imports.
- No rendering imports.
- No Meta XR or OpenXR imports.
- No wall-clock time access inside core logic. Pass elapsed ticks or commands in from outside.
- No randomness without an injected deterministic seed.

### renderer

Owns Quest and OpenGL rendering.

Expected classes:

- `QuestActivity`
- `OpenXRRenderer`
- `BoardRenderer`
- `MeshFactory`
- `InputController`

Rules:

- Reads immutable render snapshots or public simulation state.
- Does not own gameplay rules.
- Does not mutate simulation except through explicit input commands.

### debug

Owns debug overlay and statistics.

Expected classes:

- `Overlay`
- `Statistics`

Rules:

- Debug rendering may depend on Android or Compose.
- Debug data comes from simulation snapshots.

### tests

Expected test groups:

- `RotationTests`
- `CollisionTests`
- `SupportTests`
- `PressureTests`
- `FailureTests`
- `SimulationTests`

## Simulation Model

The board is a fixed 12 by 20 grid. Empty cells contain no material. Placed cells contain deterministic structural data.

The active module is separate from locked board cells until it locks.

Recommended state split:

- `Board`: locked cells only.
- `ActiveModuleState`: module definition, position, rotation.
- `Simulation`: board, active module, water height, game over state, counters.

## Determinism

Simulation updates should be command driven:

```text
MoveLeft
MoveRight
RotateClockwise
HardDrop
TickWater
Step
```

Tests should be able to build a board, apply commands, and assert exact results.

## Rendering Approach

Use simple static meshes and batched rendering where practical:

- One cube/prism mesh family for normal cells.
- Cheap material colors.
- Minimal transparent water plane behind the board.
- Crack overlays can be simple dark line geometry or a darker cracked variant mesh.

Avoid:

- Expensive fragment shaders.
- Runtime mesh complexity.
- Per-frame allocation in render loops.

## Quest Integration

Quest-specific code should stay in the Android app and renderer layers. Meta XR SDK, OpenXR session setup, controller polling, and OpenGL ES surface handling must not leak into simulation.

Prefer Meta SDKs for Quest-specific VR capabilities where they make sense, especially headset lifecycle, controller input, platform performance guidance, and compatibility with Meta's recommended project structure. Keep OpenXR/OpenGL boundaries explicit and do not use Meta APIs as a shortcut inside deterministic simulation logic.

Before implementing Quest integration, verify current Meta XR SDK and OpenXR Android setup against official Meta documentation.

## Build And Test Expectations

Each loop should run the narrowest useful verification:

- `./gradlew test` once a Gradle project exists.
- `./gradlew assembleDebug` once Android scaffolding exists.
- Add focused tests before or with risky simulation changes.

If a command cannot run because the project is not scaffolded yet, record that in `FAILURES.md`.
