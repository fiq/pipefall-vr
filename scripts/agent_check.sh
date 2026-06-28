#!/usr/bin/env bash
set -euo pipefail

required_docs=(
  "GAME_SPEC.md"
  "ARCHITECTURE.md"
  "TODO.md"
  "FAILURES.md"
  "BLOG.md"
  "AGENTS.md"
)

for doc in "${required_docs[@]}"; do
  test -f "$doc"
done

git diff --check

if test -d app/src/main/java/com/pipefall/pressure/simulation; then
  if rg -n "^(import android\\.|import com\\.meta\\.|import com\\.oculus\\.|import org\\.khronos\\.)" \
    app/src/main/java/com/pipefall/pressure/simulation \
    app/src/test/java/com/pipefall/pressure/simulation 2>/dev/null; then
    echo "Simulation code must not import Android, Meta, Oculus, or OpenXR APIs." >&2
    exit 1
  fi
fi

if rg -n -i "(tetromino|line clearing|line-clear|seven canonical|tetris scoring|tetris progression)" \
  app/src/main app/src/test 2>/dev/null; then
  echo "Forbidden Tetris-like implementation language found in source or tests." >&2
  exit 1
fi

if rg -n "(SECRET|TOKEN|PASSWORD|PRIVATE|BEGIN [A-Z ]*KEY|sdk\\.dir|/home/raf)" \
  -g '!scripts/agent_check.sh' \
  -g '!local.properties' \
  -g '!.android-sdk/**' \
  -g '!build/**' \
  -g '!app/build/**' \
  -g '!.gradle/**'; then
  echo "Sensitive or machine-local content found." >&2
  exit 1
fi

echo "agent_check passed"
