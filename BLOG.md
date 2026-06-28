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
