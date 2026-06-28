# Pressure TODO

## Process

For every Ralph loop:

1. Read `GAME_SPEC.md`.
2. Read `ARCHITECTURE.md`.
3. Read `TODO.md`.
4. Read `FAILURES.md`.
5. Implement exactly one TODO item.
6. Build.
7. Fix compile errors.
8. Run tests.
9. Fix test failures.
10. Update `TODO.md`.
11. Update `FAILURES.md`.
12. Commit.
13. Stop.

## Done

- [x] Bootstrap `GAME_SPEC.md`, `ARCHITECTURE.md`, `TODO.md`, and `FAILURES.md`.

## Next

- [ ] Scaffold minimal Kotlin Android Gradle project for Quest-targeted app builds.

## Backlog

- [ ] Add pure Kotlin simulation package with board, cell, material, and grid position types.
- [ ] Add engineering module definitions and deterministic module generator.
- [ ] Add module rotation tests.
- [ ] Add board collision and lock rules.
- [ ] Add collision tests.
- [ ] Add deterministic water height update.
- [ ] Add pressure system and pressure tests.
- [ ] Add support system and support tests.
- [ ] Add failure system with cascade resolution.
- [ ] Add failure tests.
- [ ] Add simulation step orchestration.
- [ ] Add simulation tests for spawn, move, rotate, hard drop, lock, water, pressure, support, crack, failure, cascade, and game over.
- [ ] Add Android Quest activity shell.
- [ ] Verify Meta XR SDK/OpenXR Android setup against current official Meta documentation.
- [ ] Add OpenGL ES 3.2 renderer skeleton.
- [ ] Render fixed floating construction board.
- [ ] Render locked board cells as simple 3D geometry.
- [ ] Render active descending engineering module.
- [ ] Wire Quest controller thumbstick, rotate button, and trigger drop inputs.
- [ ] Render water plane behind the wall.
- [ ] Render cracked and failed cell states.
- [ ] Add debug statistics snapshot.
- [ ] Add debug overlay for FPS, water height, max pressure, cracked count, failed count, support heatmap, and pressure heatmap.
- [ ] Add game over state rendering.
- [ ] Run on Meta Quest and record device notes.

