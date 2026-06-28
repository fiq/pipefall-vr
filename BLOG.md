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
