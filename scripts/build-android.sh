#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
: "${ANDROID_HOME:?Set ANDROID_HOME to an Android SDK with platform 36 and NDK 28.2.13676358}"
scripts/fetch-upstream.sh
./gradlew --no-daemon :android-app:assembleDebug :android-app:testDebugUnitTest
python3 scripts/check-apk.py android-app/build/outputs/apk/debug/android-app-debug.apk
