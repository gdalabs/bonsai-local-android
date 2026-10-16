#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
revision=5ae2e169eac202d2f065606539c3ad6ab8b51333
destination=third_party/llama.cpp
if [[ ! -d "$destination/.git" && -f "$destination/SOURCE_REVISION" ]]; then
    [[ "$(cat "$destination/SOURCE_REVISION")" == "$revision" ]] || { echo 'Wrong source snapshot revision' >&2; exit 1; }
    (cd "$destination" && sha256sum --check --quiet SOURCE_SHA256SUMS)
    echo "Verified vendored PrismML source snapshot $revision"
    exit 0
fi
if [[ ! -d "$destination/.git" ]]; then
    mkdir -p third_party
    git init "$destination"
    git -C "$destination" remote add origin https://github.com/PrismML-Eng/llama.cpp.git
    git -C "$destination" fetch --depth 1 origin "$revision"
    git -C "$destination" checkout --detach FETCH_HEAD
fi
[[ "$(git -C "$destination" rev-parse HEAD)" == "$revision" ]] || { echo 'Wrong upstream revision' >&2; exit 1; }
[[ -z "$(git -C "$destination" status --porcelain)" ]] || { echo 'Upstream has local modifications' >&2; exit 1; }
echo "Verified PrismML revision $revision"
