# Pressure Failures

This file records build, test, device, design, and process failures encountered during Ralph loops. Keep entries factual and brief.

## Current Known Issues

- Meta XR SDK/OpenXR runtime integration has not yet been added.
- No Quest device run has been performed.
- Foundational board, module catalog, rotation, collision, water, pressure, support, failure, simulation tests, and Android activity shell exist, but renderer integration and device validation are still pending.

## Failure Log

### 2026-06-28 - Bootstrap Documentation

- Context: Repository was empty with no commits.
- Action: Created required planning documents before writing code.
- Build: Not run because no Gradle or Android project exists yet.
- Tests: Not run because no test project exists yet.
- Follow-up: Next Ralph loop should scaffold the minimal Kotlin Android Gradle project.

### 2026-06-28 - Android Scaffold

- Context: Added a minimal Kotlin Android Gradle app scaffold and Quest-oriented activity placeholder.
- Issue: Running the cached Gradle 8.11.1 distribution directly failed before configuration because `libnative-platform.so` could not load.
- Resolution: Used Nix-provided Gradle 8.14.4 to generate the checked-in Gradle wrapper.
- Issue: `./gradlew --no-daemon test assembleDebug` could not write a lock file under the sandboxed existing Gradle wrapper cache.
- Resolution: Verified the NixOS path with the flake-provided `gradle --no-daemon test assembleDebug` command instead.
- Issue: Offline wrapper generation initially failed because Gradle needed uncached Kotlin 2.0.21 buildscript dependencies.
- Resolution: Reran wrapper generation with network access through Nix/Gradle, then continued with the checked-in wrapper.
- Issue: First flake verification used the ignored local SDK shim from `local.properties`, then failed on missing Android SDK Build-Tools 34 licenses.
- Resolution: Removed the local shim from the verification path and pinned Android build tools to 35.0.1.
- Verification: `nix develop --command gradle --no-daemon test assembleDebug` passed. Unit test tasks were `NO-SOURCE` because simulation tests do not exist yet.
- Added: Nix flake, optional Android build container recipe, and GitHub Actions Android pipeline.
- Device: Quest run not attempted in this loop.

### 2026-06-28 - Simulation Foundations

- Context: Added pure Kotlin `simulation` types for `Board`, `Cell`, `Material`, structural state, cell function, and grid position.
- Added: Basic board unit tests covering dimensions, immutable placement, bounds checks, removal, and deterministic face bonds.
- Added: `scripts/agent_check.sh` to catch missing docs, whitespace issues, simulation API boundary violations, forbidden clone-like source language, and obvious local/sensitive content.
- Verification: `nix develop --command bash -lc 'scripts/agent_check.sh && gradle --no-daemon lintDebug test assembleDebug'` passed.
- Device: Quest run not attempted in this loop.

### 2026-06-28 - Engineering Module Catalog

- Context: Added named engineering module definitions and deterministic module generator.
- Added: Module catalog includes concrete slab, reinforced beam, short pillar, buttress, corner retaining section, drain block, spillway, reinforcement cage, pressure relief chamber, and inspection shaft.
- Added: Module catalog tests cover required ordering, connected cell counts, slab shape, drain center material/function, and seeded deterministic generation.
- Verification: `nix develop --command bash -lc 'scripts/agent_check.sh && gradle --no-daemon lintDebug test assembleDebug'` passed.
- Device: Quest run not attempted in this loop.

### 2026-06-28 - Module Rotation

- Context: Added deterministic clockwise module rotation and focused rotation tests.
- Added: Rotation tests cover beam orientation, buttress normalization, drain-cell preservation, and four-turn identity for every engineering module.
- Verification: `nix develop --command bash -lc 'scripts/agent_check.sh && gradle --no-daemon lintDebug test assembleDebug'` passed.
- Device: Quest run not attempted in this loop.

### 2026-06-28 - Collision And Lock Rules

- Context: Added board-level module collision checks and lock rules.
- Added: Collision tests cover empty placement, board bounds, locked-cell overlap, immutable locking, bond recalculation, and lock rejection.
- Added: SRP/code-smell guardrails to `ARCHITECTURE.md`, `AGENTS.md`, and `scripts/agent_check.sh`.
- Added: `scripts/pressure_check.sh` and `skills/pressure-ralph/SKILL.md` to reduce repeated loop instructions.
- Verification: `nix develop --command scripts/pressure_check.sh` passed.
- Device: Quest run not attempted in this loop.

### 2026-06-28 - Deterministic Water Height

- Context: Added a pure Kotlin water state and water rise system for deterministic height progression.
- Added: Water tests cover empty initial state, partial tick carry, single and multi-rise advances, top clamping, zero-tick no-op behavior, and invalid input rejection.
- Verification: `nix develop --command scripts/pressure_check.sh` passed.
- Device: Quest run not attempted in this loop.

### 2026-06-28 - Pressure System

- Context: Added a pure Kotlin pressure system that derives deterministic pressure from water height and cell row.
- Added: Pressure tests cover zero pressure above the waterline, depth-based pressure growth, board snapshots for occupied cells, and invalid input rejection.
- Verification: `nix develop --command scripts/pressure_check.sh` passed.
- Device: Quest run not attempted in this loop.

### 2026-06-28 - Support System

- Context: Added a pure Kotlin support system that derives effective strength from base strength, bonds, same-material contacts, reinforcement adjacency, and exposed faces.
- Added: Support tests cover isolated-cell penalties, bonded support growth, snapshot calculation for occupied cells, and invalid input rejection.
- Verification: `nix develop --command scripts/pressure_check.sh` passed.
- Device: Quest run not attempted in this loop.

### 2026-06-28 - Failure System

- Context: Added a pure Kotlin failure system that marks cracked cells, removes failed cells, and cascades until the board stabilizes.
- Added: Failure tests cover cracking, removal at the fail threshold, cascade recomputation after a removal, and invalid input rejection.
- Verification: `nix develop --command scripts/pressure_check.sh` passed.
- Device: Quest run not attempted in this loop.

### 2026-06-28 - Simulation Orchestration

- Context: Added an immutable `SimulationState`, a command model, and a pure orchestration layer that routes `TickWater` and `Step` through water and failure systems.
- Added: Simulation tests cover stepping water into failure resolution, tick-only water advances, and top-of-board game over detection.
- Verification: `nix develop --command scripts/pressure_check.sh` passed.
- Device: Quest run not attempted in this loop.

### 2026-06-28 - Simulation Command Surface

- Context: Added an active-module simulation model with deterministic spawn, left/right movement, rotation, and hard-drop locking.
- Added: Simulation tests cover spawn placement, movement and rotation, hard-drop locking, water-only ticks, failure cascades, and game over at the water top.
- Verification: `nix develop --command scripts/pressure_check.sh` passed.
- Device: Quest run not attempted in this loop.

### 2026-06-28 - Android Quest Activity Shell

- Context: Added a dedicated Android shell view for the Quest launch activity.
- Added: `QuestActivity` now owns full-screen keep-awake Android window behavior and hosts `QuestShellView` as the renderer placeholder.
- Verification: `nix develop --command scripts/pressure_check.sh` passed.
- Device: Quest run not attempted in this loop.

### 2026-06-28 - Meta Quest Shell Verification

- Context: Verified the Android shell against the current Meta Horizon OS manifest guidance for Quest release builds.
- Added: `AndroidManifest.xml` now includes `android.hardware.vr.headtracking` with version `1`, `installLocation="auto"`, `excludeFromRecents="true"`, `com.oculus.intent.category.VR`, and `com.oculus.supportedDevices` metadata. `app/build.gradle.kts` now targets SDK 34 while still compiling against SDK 35.
- Verification: `nix develop --command scripts/pressure_check.sh` passed.
- Device: Quest run not attempted in this loop.
