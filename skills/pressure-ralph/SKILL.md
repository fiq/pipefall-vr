---
name: pressure-ralph
description: Use for repeated Pressure VR Ralph loops in this repo: read loop docs, implement one TODO, run the standard check, update docs/blog, commit, and push.
---

# Pressure Ralph Loop

Use this skill when continuing Pressure VR implementation in this repository.

## Loop

1. Read `GAME_SPEC.md`, `ARCHITECTURE.md`, `TODO.md`, and `FAILURES.md`.
2. Implement exactly one TODO item unless the user explicitly changes scope.
3. Keep simulation pure Kotlin with no Android, Meta, Oculus, OpenXR, or rendering imports.
4. Preserve the civil engineering fantasy; do not implement clone-like falling-block rules.
5. Run `nix develop --command scripts/pressure_check.sh`.
6. Update `TODO.md`, `FAILURES.md`, and `BLOG.md`.
7. Commit and push `main`.

## Defaults

- Prefer immutable simulation values and explicit commands.
- Keep one reason to change per production type.
- Add focused JUnit tests for deterministic simulation rules.
- Use subagents only for bounded sidecar work that does not block the critical path.
