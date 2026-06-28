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
