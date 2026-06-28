# Agent Instructions

## Start Here

Before any implementation work, read these files in order:

1. `GAME_SPEC.md`
2. `ARCHITECTURE.md`
3. `TODO.md`
4. `FAILURES.md`

Then implement exactly one TODO item, build, run tests, update `TODO.md`, update `FAILURES.md`, update `BLOG.md`, commit, and stop.

## Product Guardrails

- Preserve the civil engineering fantasy.
- Do not turn Pressure into Tetris.
- Do not add tetrominoes, line clearing, canonical Tetris pieces, Tetris scoring, or Tetris progression.
- Keep gameplay deterministic.
- Keep simulation independent from Android, rendering, OpenXR, and Meta SDK APIs.
- Prefer simple, inspectable rules over realism.
- Prefer Meta SDKs for Quest-specific VR integration where they make sense, while preserving the simulation/rendering boundary.

## Scope Guardrails

- Optimize for completing one Ralph loop in about an hour.
- Make appropriate decisions where details are ambiguous, but keep them aligned with the game spec.
- Add polish only when it directly helps test the core loop.
- Do not continue to the next gameplay feature after finishing the current TODO.
- Use subagents for bounded sidecar work when it helps finish the loop faster without splitting one file or responsibility across multiple agents.
- Prefer the Nix flake on NixOS: `nix develop --command bash -lc 'scripts/agent_check.sh && gradle --no-daemon lintDebug test assembleDebug'`.
- Use the build container when it helps reproduce CI locally, but do not let container polish delay a Ralph loop.

## Testing Trophy

- Agent/static checks: run `scripts/agent_check.sh` to catch boundary violations, local paths, sensitive content, and forbidden Tetris-like implementation drift.
- Unit tests: put most simulation confidence here because the simulation is deterministic and pure Kotlin.
- Build/lint: run Android lint and `assembleDebug` to prove packaging health.
- Device checks: use Quest runs for VR behavior once OpenXR/Meta SDK integration exists.

## Secrets And Local Files

- Never commit secrets, tokens, credentials, signing keys, keystores, local SDK paths, APKs, or build outputs.
- `local.properties` and `.android-sdk/` are local-only and ignored.
- If a command prints a secret-like value, do not paste it into docs, commits, PRs, or chat.

## Git

- Do not revert changes you did not make.
- Stage explicit files when the worktree is mixed.
- Push only after checking the staged diff.
