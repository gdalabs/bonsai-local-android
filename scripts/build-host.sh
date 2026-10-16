#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
scripts/fetch-upstream.sh
cmake -S inference -B build/runtime-host -DCMAKE_BUILD_TYPE=Release
cmake --build build/runtime-host --parallel "${MESHMIND_BUILD_JOBS:-2}"
