# Pressure Dev Log

## Loop 0: Concrete Before Concrete

Pressure started with paperwork, which is exactly as glamorous as dam engineering should be.

No renderer. No controller input. No heroic cube tumbling through space. Just four documents and a rule. Before any code gets to pretend it is a dam, the project needs a dam of its own: spec, architecture, TODOs, and failures.

That matters because Pressure can easily slide into "VR Tetris with water" if the work is not fenced in. The first loop planted the fence posts: civil engineering modules, deterministic pressure, visible structural failure, and small Ralph loops that stop before they sprawl.

```text
spec -> architecture -> todo -> failures
  |          |           |        |
  +----------+-----------+--------+
             keeps the loop honest
```

## Loop 1: Teaching Android To Say Hello

The second loop is the first shovel in the ground: a minimal Kotlin Android app that can become a Quest prototype. The goal is not to render a beautiful dam yet. The goal is to make the build system real, launch a full-screen activity, and leave a clean place for OpenXR and OpenGL ES to move in later.

The local machine made this more interesting than a textbook Android setup. Gradle and the Android SDK are present through cached and Nix-managed pieces, not as one tidy global install. That shaped the decision to keep the committed scaffold portable while using ignored local SDK wiring only for verification.

```text
        future fun
            |
     descending modules
            |
     deterministic sim
            |
     Android/Quest shell
            |
   Gradle build + JUnit tests
            |
        docs first
```

The rationale is simple: keep the game rules pure and inspectable before the headset enters the room. Rendering can be replaced. Controller input can be remapped. The dam logic has to stay readable, because the prototype lives or dies on whether pressure, support, cracking, and collapse feel legible.

The repo also gained the boring machinery that makes future excitement cheaper: a Nix flake for local NixOS development, a GitHub Actions Android build, and a small build-container recipe. None of those make water rise, but they make it harder for the project to quietly drift into "works on my machine" country.

The first real pressure test was not water. It was the Android SDK. The local machine had enough cached pieces to be dangerous, but not enough structure to be trustworthy. The fix was to let Nix describe the toolchain and let Gradle prove the scaffold. `test` has no simulation tests yet, but `assembleDebug` now gives the project a real APK-shaped heartbeat.

## Loop 2: The First Stones

This loop finally put something inside the dam that is not paperwork or build plumbing: cells.

Not exciting cells yet. No water. No cracks. No heroic collapse. Just positions, materials, structural state, and an immutable board that knows what is in bounds and what touches what. It is the smallest useful slice of the simulation: a 12 by 20 place where future modules can become locked infrastructure.

```text
(0,19)                         (11,19)
  +-------------------------------+
  |                               |
  |           dam grid            |
  |                               |
  +-------------------------------+
(0,0)                          (11,0)
```

The important choice was to keep the board boring and pure. Placing a cell returns a new board instead of mutating the old one. Bonds are just touching faces. Failed blocks are not physics objects waiting to happen; they will be deterministic state transitions. That keeps the core game inspectable, which matters more than realism right now.

The project also got a small agentic guardrail: `scripts/agent_check.sh`. It checks that the loop documents still exist, that source whitespace is clean, that simulation code has not imported Android or VR APIs, and that no one has started sneaking Tetris-shaped thinking into the implementation. The testing trophy is now explicit: static checks at the base, unit tests doing the heavy lifting, Android lint and assembly above that, and Quest device runs once there is actual VR behavior to validate.

## Loop 3: Prefab, Not Puzzle Blocks

The dam now has a catalog of things that can fall.

This was the loop where Pressure had to defend its identity. A lazy version of the game would quietly invent abstract blocks and call them concrete. Instead the catalog is named like infrastructure: concrete slab, reinforced beam, short pillar, buttress, retaining corner, drain block, spillway, reinforcement cage, pressure relief chamber, inspection shaft.

```text
slab        beam      buttress    drain

XX          XXX       X.          CDC
XX                    XX
```

The generator is deterministic on purpose. Given the same seed and spawn index, it returns the same module every time. That is not glamorous, but it means a failure cascade can eventually be reproduced from a test instead of remembered from a headset session.

The drain block is the first tiny hint of the future game: its center cell is genuinely a weak drain, not just a differently colored cube. The pressure relief chamber already carries its special function too. Nothing uses those functions yet, but the data model now has places for the dam to become more than a wall.

## Loop 4: Turning Heavy Things

Rotation is where a falling module starts to feel like a thing the player can handle, not just a row in a catalog.

The implementation is intentionally mechanical: rotate the module inside its bounding box, normalize the offsets, preserve the cells. No physics. No pivot drama. No controller nuance yet. Just deterministic shape transformation that can be trusted later by collision and lock rules.

```text
beam clockwise:

XXX    X
       X
       X

buttress clockwise:

X.     .X
XX     XX
```

The useful detail is preservation. A drain remains a drain after rotation. A full four clockwise turns returns every module to the original shape and cells. That sounds obvious, but obvious rules are exactly what the later pressure system needs: the player should blame a breach on a weak dam, not on mysterious geometry.

## Loop 5: The Board Says No

A puzzle game begins to exist when the board can refuse you.

Until now, modules were clean little diagrams. This loop gave them consequences. The board can now ask whether a module at an origin fits, whether it leaves the 12 by 20 construction area, whether it overlaps locked infrastructure, and whether it can be locked into the dam.

```text
empty space      locked cell      collision

....             ..#..            ..#..
.XXX  ok         .XXX  no         .XXX
....             .....            .....
```

Locking is still deterministic and non-physical. A module becomes board cells, the original board remains unchanged, and touching faces become bonds. That bond recalculation is small, but it is the first bridge from "placing pieces" to "building structure."

This loop also tightened the agent harness around code quality. The project now states the SRP rule out loud: one reason to change per production type, one rule family per system, no god classes quietly swallowing the prototype. The harness enforces a coarse Kotlin file-size limit so the codebase starts complaining before a class becomes a dumping ground.

There is also a new shortcut for future loops: `scripts/pressure_check.sh`. It runs the agent guard, Android lint, unit tests, and debug assembly. The repo-local `pressure-ralph` skill captures the repeatable workflow so future agents do not need the whole backstory before doing the next small piece of dam engineering.

## Loop 6: The Reservoir Starts Keeping Time

Water does not need fluid simulation yet. It needs discipline.

This loop added the smallest useful water model: a height, a partial-tick remainder, and a deterministic rule for when the reservoir rises. After a fixed number of ticks, height increases by a fixed amount. If several intervals pass at once, water can rise multiple steps in one advance. If it hits the top of the board, it clamps there and throws away leftover timing state.

```text
ticks:      0 1 2 3 4 5 6 7
height:     0 0 0 1 1 1 2 2
remainder:  0 1 2 0 1 2 0 1
```

That is intentionally austere, because the point is not realism. The point is to create a repeatable rising threat that future pressure rules can read without asking Android time APIs, render timing, or headset state what happened. The water system now behaves like the rest of the simulation should behave: given the same state and the same tick count, it produces the same answer every time.

## Loop 7: Pressure Becomes Measurable

Pressure in this prototype is deliberately simple. A cell experiences pressure equal to how far it sits below the waterline, and nothing else for now.

That gives the next systems something concrete to compare against without dragging in fake fluid behavior or timing side effects. The pressure system can answer for a single cell or snapshot the occupied board, which makes it a clean seam for the later support and failure loops.

```text
waterHeight = 7
cellY       = 2
pressure    = 5
```

This is still the right kind of boring. The game is supposed to be about whether the dam holds, not about whether the pressure math wants attention.

## Loop 8: Support Becomes Structural

Pressure alone is not enough to tell the story of a dam. The next model change makes the structure itself matter.

Support now computes an effective strength for each locked cell from five simple parts: the cell's base strength, how many faces are bonded, how many bonded neighbors share the same material, whether any bonded neighbor is reinforcement, and how many faces are exposed. The important decision is that reinforcement only boosts concrete, which keeps the rule aligned with the engineering fantasy instead of turning every material into a generic buff source.

```text
effectiveStrength =
  baseStrength
  + bondedNeighborCount * supportBonus
  + sameMaterialBondCount * materialBonus
  + concreteReinforcementBonus
  - exposedFaceCount * exposedFacePenalty
```

That keeps the simulation easy to reason about in tests and leaves pressure and failure free to stay separate. The model is still intentionally simple, but it now has enough structure to make future cracking and collapse decisions feel like dam behavior instead of just row math.

## Loop 9: Failure Becomes Deterministic

The failure model now closes the core structural loop. A cell cracks when pressure exceeds effective strength, fails when pressure exceeds one and a half times effective strength, and gets removed when it fails. After each removal, the board is recomputed and the pressure/support snapshot is evaluated again until the cascade stops.

That is the important model change: failure is not a visual effect and not an animation state. It is a deterministic simulation transition on `Cell.state` plus board removal, which keeps the rule testable and makes the eventual simulation step orchestration straightforward.

```text
pressure > strength          -> cracked
pressure > strength * 1.5    -> failed and removed
```

This loop is the first one where the board can now meaningfully degrade under load. That is still simple, but it is finally dam logic instead of just dam accounting.

## Loop 10: Orchestration Finds Its Shape

The simulation layer now has a clean seam between state and action. `SimulationState` is the immutable snapshot, while `Simulation` is the command router that decides whether a tick only advances water or runs the full step pipeline.

That model change matters because it keeps orchestration out of the rules themselves. Water still belongs to `WaterSystem`. Strength still belongs to `SupportSystem`. Collapse still belongs to `FailureSystem`. The new layer just decides when those systems run and in what order.

```text
TickWater -> water advances, board stays intact
Step      -> water advances, then failure resolution runs
```

This is the kind of plumbing that pays rent later. It gives the next loop a place to add spawn, move, rotate, and hard-drop behavior without turning the simulation package into a kitchen sink.

## Loop 11: The Module Gets A Body

The simulation stopped being just a set of board-level rules and became a real turn state. There is now an active module, a spawn index, and deterministic commands for spawn, move, rotate, and hard drop. That means the simulation can describe what the player is currently handling instead of only what is already locked into the dam.

```text
spawn -> center the next prefab at the top
move  -> shift the active module if the board allows it
rotate -> rotate in place if the rotated shape still fits
drop  -> fall until the last legal row, then lock
```

This is the first state model that starts to feel like a game loop rather than just a physics substitute. The board still says no, water still rises on schedule, and failure still resolves deterministically, but now the player-facing piece has a concrete identity all the way through the simulation layer.
