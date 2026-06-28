# Pressure

Pressure is a Meta Quest VR puzzle prototype about building a dam while water rises behind it.

The prototype asks one question:

> Is building a stronger dam against rising pressure genuinely fun?

This is not a production game. The current priority is a fast, deterministic, testable first milestone.

## Current State

The repo currently contains:

- Project spec and architecture documents.
- A minimal Kotlin Android Gradle scaffold.
- A Quest-oriented launch activity placeholder.
- A short development blog.

The game simulation and renderer are not implemented yet.

## Ralph Loop

Every implementation loop starts by reading:

1. `GAME_SPEC.md`
2. `ARCHITECTURE.md`
3. `TODO.md`
4. `FAILURES.md`

Then implement exactly one TODO item, build, fix compile errors, run tests, update docs, commit, and stop.

Each loop also updates `BLOG.md` with a short publishable note about what changed and why.

## Build

Use the Gradle wrapper:

```sh
./gradlew assembleDebug
./gradlew test
```

Android SDK location should come from a normal local Android setup or ignored `local.properties`. Do not commit machine-specific SDK paths.

On NixOS, use the flake:

```sh
nix develop --command gradle --no-daemon test assembleDebug
```

An optional Android build container is available:

```sh
docker build -f containers/android-build.Dockerfile -t pressure-android-build .
```

## Technology Direction

- Kotlin
- Android
- Meta XR SDK
- OpenXR
- OpenGL ES 3.2
- Gradle
- JUnit

Prefer Meta SDKs for Quest-specific VR integration where they make sense, without letting those APIs leak into the deterministic simulation layer.

Do not use Godot, Unity, Unreal, libGDX, or Flutter.

## Safety

Do not commit secrets, keys, tokens, local SDK paths, build outputs, APKs, or generated caches.
