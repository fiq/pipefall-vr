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

## Loop 12: A Room For The Renderer

The Android side now has a real shell instead of a loose placeholder. `QuestActivity` is responsible for the things Android must own: full-screen mode, keep-awake behavior, lifecycle re-entry, and a launch surface that Quest can start. The new `QuestShellView` is deliberately quiet: title, status, dark background, and no gameplay authority.

That model choice is important. The activity is not the game. It is the room the renderer will walk into.

```text
QuestActivity
  -> Android window and lifecycle
  -> QuestShellView placeholder
  -> future renderer host

Simulation
  -> still pure Kotlin
  -> still independent of Android

## Loop 13: Controller Events Reach The Renderer

This loop wired the first real Quest input path instead of leaving controller support as a stub. The renderer view now listens for joystick and gamepad motion, rotates on a pressed button, and treats trigger pulls as hard drops. Thumbstick input is debounced at the renderer boundary so holding a direction does not spray commands every frame.

That matters because the prototype is still trying to stay deterministic. The simulation continues to own the game rules, while Android only translates controller events into explicit commands. The loop also tightened focus handling so the Quest surface is ready to receive controller input when the activity resumes.

## Loop 14: Water Becomes Visible

The next pass added the rising reservoir as a translucent fill behind the board. The first attempt tried to keep water inside `BoardRenderer`, but the local size guard caught that the file had started owning too much. That was the right failure: water is a separate renderer concern, so it now lives in `WaterRenderer` and the board renderer goes back to handling board geometry and structural cells.

The result is still deliberately simple. A reusable quad represents the water plane, the fill height follows the deterministic water state, and the board surface stays slightly translucent so the reservoir actually reads from the front-facing Quest camera. The change keeps the simulation untouched and preserves the clean boundary between state and rendering.
```

This gives the next loop a clean target: verify the current Meta XR and OpenXR Android setup before installing real headset plumbing. The shell is now ready to host that work without tempting the simulation to learn about Android.

## Loop 13: The Shell Checks Out

The Quest launcher shell now matches the current Meta Horizon OS manifest guidance instead of just being a generic Android activity. The manifest gained the release-build pieces Meta calls out for Quest apps: headtracking declared with version `1`, `installLocation="auto"`, `excludeFromRecents="true"`, the OpenXR launch category, and supported-device metadata. I also moved the app target to SDK 34 while keeping compile SDK 35 so the build stays current without drifting outside the documented immersive release range.

That change is intentionally small, but it matters. The project now has a launcher that is shaped like a Quest app rather than merely running on an Android device. The actual Meta XR and OpenXR runtime plumbing is still for the next stage, but the Android shell no longer needs to guess at the release-side contract.

```text
manifest -> store-friendly Quest shell
build    -> target 34, compile 35
view     -> Android host only
```

The next loop can now start on renderer setup with the confidence that the platform-facing envelope is already doing the right boring things.

## Loop 14: Clearing A Frame

The renderer skeleton is intentionally plain, but it finally establishes the OpenGL side of the boundary. `QuestRenderView` now owns the `GLSurfaceView`, `OpenXRRenderer` clears the frame and records viewport state, and the board/input helpers exist as separate renderer types rather than being stuffed into the activity.

That separation matters more than the visuals at this stage. The activity hosts the app shell. The renderer owns the GL surface. Simulation still stays out of Android code. This is the first loop that gives the project a concrete place to hang the actual board, cells, water plane, and controller input without blurring the layers.

```text
Activity -> window + lifecycle
Render   -> GL surface + clear frame
Board    -> future draw path
Input    -> future controller mapping
```

The frame is still mostly empty, but the project now has the structural spine needed for the next set of rendering loops.

## Loop 15: The Board Gets A Shadow

The first visible object is not a cell. It is the thing that makes the cells matter: a fixed floating board, two and a half meters out in front of the player, centered on the 12 by 20 grid. That choice keeps the prototype honest. Before the dam can crack, the player needs a surface that reads as infrastructure instead of a blank stage.

I kept the geometry pure on purpose. `MeshFactory` now emits a board surface and grid as plain data, while `BoardRenderer` turns that data into a perspective draw call. That split is small but useful: the model stays testable, the renderer stays disposable, and the shape of the board is encoded where future cell rendering can reuse it.

```text
world anchor
    |
    v
fixed board surface
    |
    v
grid cells waiting for modules
```

The board is still empty, which is the right kind of empty. It now looks like the place where a dam will be built, not just the place where a dam could have been.

## Loop 16: The Board Takes Load

The next step is where the board stops being a sign and starts being a structure. Locked cells now render as simple 3D prisms, with material-based colors and a small extrusion off the board plane so they read as blocks rather than flat decals.

That model decision matters because it keeps the dam legible at a glance. Concrete can look like concrete, steel can look like steel, and the board surface stays visually separate from the actual load-bearing geometry. The renderer still owns the draw path, but the board data now has visible volume instead of only coordinates.

```text
surface -> background plane
grid    -> layout reference
cells   -> actual dam mass
```

The next loop can now focus on the active descending module without having to invent the idea of a built dam first.

## Loop 17: The Module Descends

The player now has something to push around in space: the active engineering module renders as a hovering 3D shape above the board, using the same cell mesh as the locked structure but lifted and tinted so it reads as temporary work-in-progress.

That is a useful boundary because the simulation still owns the module state and the renderer only turns that state into something visible. It is the first time the prototype shows both the permanent dam and the thing that is about to become part of it.

```text
locked cells -> built dam
active module -> next addition
hover offset  -> not yet locked
```

The board now shows the difference between structure and intent, which is the right visual language for the next input loop.

## Loop 18: The Dam Remembers Its Wounds

This loop had a small philosophical problem hiding inside it: how do you render a failed cell when the simulation already threw it away?

The failure system does not leave corpses. When a block fails, `FailureSystem` removes it from the board entirely. There is no `StructuralState.FAILED` cell sitting on the grid waiting to be drawn, because by the time the renderer sees the state, the cell is gone. That is the right simulation behaviour, but it is a awkward rendering behaviour. A block that simply vanishes does not read as collapse. It reads as a typo.

So the loop crossed the sim/render boundary on purpose, but carefully. `SimulationState` now carries a `recentlyFailedPositions` set, populated by diffing the board's cell keys before and after failure resolution. The simulation still owns the rules and stays pure Kotlin with no Android imports. It just also remembers, for one tick, which positions disappeared. The renderer can flash those ghosts red before they are forgotten.

```text
before resolve:  { (0,0), (0,1) }
after resolve:   { }
recentlyFailed:  { (0,0), (0,1) }  -> renderer flashes red
next command:     recentlyFailed cleared
```

Cracked cells were the easier half. They already exist on the board with `state = CRACKED`, so the renderer just needed to notice. `BoardRenderer` now darkens cracked cells to 70% of their material colour, and a new `CrackRenderer` draws three crossing dark line segments across the front face. The crack geometry is deliberately cheap: a few `GL_LINES`, no textures, no shaders doing clever things. It reads as damage at a glance, which is all a Quest-class prototype needs.

The `CrackRenderer` follows the `WaterRenderer` precedent. When `BoardRenderer` started growing past its comfort zone, the crack and flash pass moved into its own type rather than bloating the board/cell renderer. That keeps the size guard honest and the responsibilities clean: board draws structure, water draws the reservoir, cracks draw the damage.

This is also the first loop driven by glm-5.2 as the working model. The shape of the work, the discipline of reading the docs first, and the habit of running the smallest relevant test before declaring victory all held up. The dam now has a memory of its own failures, even if only for a single tick, and that is enough to make collapse feel like something happened rather than nothing.

```text
cracked cell  -> darkened + crack lines
failed cell   -> removed, then flashed red for one tick
simulation    -> still pure, still deterministic
```

The next loop can turn toward the debug overlay, where the dam's internal numbers finally become visible to the player instead of only to the tests.

## Loop 19: The Dam Shows Its Working

The debug overlay has been a line in the spec since the beginning: FPS, water height, max pressure, cracked count, failed count, support heatmap, pressure heatmap. But an overlay is two problems wearing one hat. First you need the numbers in a form the renderer can trust. Only then do you need the pixels.

This loop did the first half. The new `debug` package owns a `StatisticsSnapshot` and a `Statistics` producer that turns a `SimulationState` into that snapshot. It stays pure Kotlin with no Android imports, because the whole point of the simulation boundary is that the dam's internal accounting should be inspectable from a unit test, not gated behind a headset.

```text
SimulationState -> Statistics -> StatisticsSnapshot
                                  water height
                                  max pressure
                                  cracked count
                                  failed count
                                  pressure heatmap
                                  support heatmap
```

The interesting wrinkle was the failed count. The failure system does not leave failed cells lying around on the board; it removes them. So there is no `StructuralState.FAILED` cell to count at snapshot time. Instead `failedCount` reads `recentlyFailedPositions`, the one-tick ghost set that the last loop added precisely so the renderer could flash collapse before forgetting it. That keeps the snapshot honest about what the simulation actually knows: a cell failed this step, and now it is gone.

The heatmaps are recomputed from the board using the same default `PressureSystem` and `SupportSystem` the simulation already trusts. That is deliberately not clever. The debug numbers should never disagree with the rules, so they ask the same rules the rules ask.

The overlay itself is still for the next loop. But the dam now has a clean, testable seam where the numbers live, and the renderer can read them without learning anything about pressure or support maths.

## Loop 20: The Headset Says No, Then Says Why

The first real Quest 3 install was a short loop with a sharp lesson attached. The APK packaged and installed cleanly, but the app died the instant it tried to stand up. The dropbox told the story plainly: a `NullPointerException` inside `QuestActivity.configureFullscreen`, called from `onCreate`, before the window had a decor view to ask for an insets controller.

```text
onCreate:
  requestWindowFeature(NO_TITLE)
  renderView = QuestRenderView(this)
  configureFullscreen()   <- window.insetsController -> DecorView is null
  setContentView(renderView)
```

The order was simply wrong. `window.insetsController` resolves through the window's `DecorView`, and that view is created lazily by `setContentView` (or `getDecorView`). Calling `configureFullscreen` first meant asking a window that had not yet been furnished for an insets controller that did not yet exist. The fix was a two-line reorder: set the content view, then configure fullscreen. `requestWindowFeature` stays first because it must precede `setContentView`.

```text
onCreate:
  requestWindowFeature(NO_TITLE)
  renderView = QuestRenderView(this)
  setContentView(renderView)   <- decor view now exists
  configureFullscreen()        <- insets controller is real
```

The humbling part was verification. Every adb-driven launch — `am start`, `monkey` — was intercepted by Horizon OS before `onCreate` could run, because the OS requires active controllers for a VR activity and the controllers were asleep. So the fix could be confirmed by build and by matching the exact root cause, but not by watching the process survive on device from the command line. That is the honest state: the crash is addressed at its source, and the next in-headset run with controllers awake is the real proof.

This is the kind of bug that only a device can surface. The unit tests pass, the build is green, and the APK is well-formed, but none of that catches an activity that asks its window for furniture before moving any in. The dam now has its first device-driven fix, even if the device is still gatekeeping the final confirmation.

A note on the model driving this loop. Loop 18 was the first glm-5.2 loop, and it held its discipline: read the docs first, run the smallest test, stop at one TODO. This loop tested a different muscle — device debugging under adb — and glm-5.2 handled it without scope creep. It pulled the real crash trace from `dumpsys dropbox` rather than guessing at the renderer, matched the stack frame to the exact line, and made the smallest reorder that fixes the root cause instead of patching around it. Compared with earlier loops, the shape of the work is the same small Ralph loop, but the diagnosis is more deliberate: evidence from the device before the edit, not a hypothesis dressed up as a fix.
