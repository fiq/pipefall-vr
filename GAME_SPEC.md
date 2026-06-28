# Pressure Game Spec

## Purpose

Pressure is a playable Meta Quest VR puzzle prototype. It tests one question:

> Is building a stronger dam against rising pressure genuinely fun?

This is not a production game. Optimize for fast implementation, deterministic gameplay, testability, small Ralph loops, clean architecture, and high Quest performance. Do not optimize for polish.

## Technology

- Kotlin
- Android
- Jetpack Compose only for menus or debug UI when useful
- Meta XR SDK
- OpenXR
- OpenGL ES 3.2 preferred
- Gradle
- JUnit

Do not use Godot, Unity, Unreal, libGDX, or Flutter.

## Player View

- The player stands in front of a floating vertical construction board.
- The board is about 2.5 meters away and remains fixed in space.
- The game is played on a deterministic 12 by 20 vertical grid.
- The board represents a cross-section through a dam.
- Water rises behind the wall.
- The player builds the dam from descending engineering modules.

## Core Loop

1. Spawn module.
2. Player moves module.
3. Player rotates module.
4. Player drops module.
5. Module locks.
6. Water rises.
7. Pressure recalculates.
8. Support recalculates.
9. Weak blocks crack.
10. Overstressed blocks fail.
11. Cascading failures resolve.
12. Repeat until game over.

Game over occurs when water breaches the wall or reaches the top.

## Board

- Width: 12 columns.
- Height: 20 rows.
- Grid based.
- Deterministic.
- No physics simulation.

Coordinates:

- `x`: column, left to right.
- `y`: row, bottom to top.

## Water

Do not simulate real fluid dynamics.

Initial deterministic rule:

```text
pressure = max(0, waterHeight - cellY)
```

Water rises by fixed increments on a deterministic timer. Future versions may add pressure relief through drains and spillways, but only after the first milestone loop is playable.

## Cell Data

Every placed cell contains:

- Material
- Base strength
- Optional function
- Bonded neighbours
- Structural state: stable, cracked, or failed

Touching faces form structural bonds.

## Structural Rules

Effective strength:

```text
effectiveStrength =
  baseStrength
  + bondedNeighbourCount * supportBonus
  + sameMaterialBondCount * materialBonus
  + reinforcementBonus
  - exposedFacePenalty
```

The rules must stay simple enough to inspect in tests and debug overlays.

## Failure Rules

If `pressure > effectiveStrength`, the block becomes cracked.

If `pressure > effectiveStrength * 1.5`, the block fails.

When a block fails:

- Remove it.
- Recalculate pressure.
- Recalculate support.
- Continue resolving until no new failures occur.

## Engineering Modules

This is not a Tetris clone. Do not implement tetrominoes, line clearing, the seven canonical pieces, Tetris scoring, or Tetris progression.

Modules are named civil engineering components with 2 to 6 connected cells.

### Concrete Slab

```text
XX
XX
```

Compact reinforced concrete block. Very strong. Excellent for sealing gaps.

### Reinforced Beam

```text
XXX
```

Long reinforced beam. Strong horizontal load transfer.

### Short Pillar

```text
X
X
X
```

Vertical structural support. Transfers pressure downward.

### Buttress

```text
X
XX
```

Stair-shaped support. Strengthens neighbouring blocks. Excellent against high pressure.

### Corner Retaining Section

```text
XX
X.
```

Corner retaining wall component. Strong internal corner support.

### Drain Block

```text
XXX
```

Concrete block containing a central drain. Outer cells are concrete. Center cell is drain material, weak structurally. Can later relieve pressure if connected to an exposed outlet.

### Spillway

```text
XXXX
```

Long overflow channel. Allows controlled release of water. Medium structural strength.

### Reinforcement Cage

```text
XX
```

Steel reinforcement insert. Small. Provides significant support bonus to adjacent concrete.

### Pressure Relief Chamber

```text
XX
.X
```

Concrete chamber with internal void. Absorbs local pressure spikes. Lower structural strength.

### Inspection Shaft

```text
X
X
```

Narrow service shaft. Weak structurally. Reserved for future repair mechanics.

## Input

- Quest controller thumbstick: move active module left or right.
- Controller button: rotate active module.
- Trigger: hard drop active module.
- No hand tracking for the first milestone.

## Rendering

- Fully 3D.
- Simple bevelled cubes or rectangular prisms for cells.
- Connected modules should read as prefabricated structures.
- Concrete: light grey.
- Steel: dark grey.
- Drain: circular pipe opening.
- Spillway: open concrete channel.
- Reinforcement: steel lattice.
- Cracked blocks: visible cracks and color change.
- Water: simple translucent animated plane behind the wall.
- Avoid expensive shaders.
- Maintain Quest framerate.

## Debug Overlay

Display:

- FPS
- Water height
- Maximum pressure
- Cracked block count
- Failed block count
- Support heatmap
- Pressure heatmap

## First Milestone Acceptance

- Builds successfully.
- Runs on Meta Quest.
- Floating 3D board visible.
- Descending engineering modules.
- Player can move, rotate, and drop modules.
- Modules lock into the grid.
- Water rises.
- Pressure recalculates.
- Structural support recalculates.
- Weak structures visibly crack.
- Overstressed structures fail.
- Cascading failures occur.
- Game over works.
- Debug overlay functions.

