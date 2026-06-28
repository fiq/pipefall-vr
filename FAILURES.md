# Pressure Failures

This file records build, test, device, design, and process failures encountered during Ralph loops. Keep entries factual and brief.

## Current Known Issues

- Meta XR SDK/OpenXR Android setup has not yet been verified against current official Meta documentation.
- No Quest device run has been performed.
- The scaffold builds, but there are no meaningful simulation tests yet.

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
