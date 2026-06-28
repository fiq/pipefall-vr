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
12. Update `BLOG.md`.
13. Commit.
14. Stop.

## Done

- [x] Bootstrap `GAME_SPEC.md`, `ARCHITECTURE.md`, `TODO.md`, and `FAILURES.md`.
- [x] Scaffold minimal Kotlin Android Gradle project for Quest-targeted app builds.
- [x] Add `README.md`, `AGENTS.md`, and `BLOG.md` repo guardrails.
- [x] Add pure Kotlin simulation package with board, cell, material, and grid position types.
- [x] Add lightweight agentic/static check harness and testing trophy CI command.
- [x] Add engineering module definitions and deterministic module generator.
- [x] Add module rotation tests.
- [x] Add board collision and lock rules.
- [x] Add collision tests.
- [x] Add SRP and code-smell guardrails for future feature loops.
- [x] Add repo-local Pressure Ralph skill and compact validation script.
- [x] Add deterministic water height update.
- [x] Add pressure system and pressure tests.
- [x] Add support system and support tests.
- [x] Add failure system with cascade resolution.
- [x] Add failure tests.
- [x] Add simulation step orchestration.
- [x] Add simulation tests for spawn, move, rotate, hard drop, lock, water, pressure, support, crack, failure, cascade, and game over.
- [x] Add Android Quest activity shell.
- [x] Add OpenGL ES 3.2 renderer skeleton.
- [x] Render fixed floating construction board.
- [x] Render locked board cells as simple 3D geometry.

## Next

- [ ] Render active descending engineering module.

## Backlog

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
