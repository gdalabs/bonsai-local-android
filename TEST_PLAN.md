# Standalone validation

## Executable checks

- `./gradlew :android-app:testDebugUnitTest`: model manifest, checksum, GGUF import and UTF-8 stream cases.
- `./gradlew :android-app:assembleDebug :android-app:assembleDebugAndroidTest :android-app:lintDebug`.
- `python3 scripts/check-apk.py [--bundled] APK`: packaged permission/ABI/alignment, JNI package and bundled model integrity checks.
- `apksigner verify APK`.

Inherited host evidence is explicitly named `inherited-*` in tests/results; it is not reported as a new run. Retained native core source hashes establish whether it is unchanged.

## Phone checklist (new package)

Install org.gdalabs.bonsailocal beside MeshMind. Check English/Japanese and light/dark modes, font scaling, keyboard/navigation insets and long chat scrolling. For the bundled APK, first launch should show local preparation, then Chat should offer Load model & start without requiring import. Load and generate synthetic text with radios off and USB disconnected. Test Stop, background cancellation, New chat confirmation, reset/repeat, restart, unload/reload and low-memory conditions. Record exact output, model hash, settings, time to first token, decode count/rate and peak process RSS. Do not collect private conversations.

The small APK should show a clear no-model state and allow local SAF import without broad storage permission. Wrong files/checksums/manifests must fail without activation. Delete model and conversation actions require confirmation. Loaded and installed states must be distinct.

Optional developer tests: install the matching test APK then run `adb shell am instrument -w org.gdalabs.bonsailocal.test/androidx.test.runner.AndroidJUnitRunner`. Model inference tests additionally require imported official Q2_0 and `-e runModelTests true`. No attached device/new-package instrumentation pass is claimed until actually executed.
