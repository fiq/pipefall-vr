#!/usr/bin/env bash
set -euo pipefail

scripts/agent_check.sh
gradle --no-daemon lintDebug test assembleDebug
