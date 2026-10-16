# Build Bonsai Local

JDK 17, SDK 36, build-tools 36.0.0, NDK 28.2.13676358, CMake 3.31.6; pinned dependencies in dependencies.json, Gradle locks and verification metadata.

```sh
export ANDROID_HOME=/path/to/android-sdk
scripts/fetch-upstream.sh
./gradlew --no-daemon :android-app:assembleDebug :android-app:assembleDebugAndroidTest :android-app:testDebugUnitTest :android-app:lintDebug
python3 scripts/check-apk.py android-app/build/outputs/apk/debug/android-app-debug.apk
```

Model-bundled APK:

```sh
python3 scripts/fetch-model.py Q2_0 /absolute/path/bonsai-Q2_0.gguf
./gradlew --no-daemon -PbonsaiBundledModel=/absolute/path/bonsai-Q2_0.gguf -PbonsaiBundledFormat=Q2_0 :android-app:assembleRelease
python3 scripts/check-apk.py --bundled --release android-app/build/outputs/apk/release/android-app-release.apk
```

Archive the small APK before building the bundled variant: they share the output path and package ID. The official checksum/size is checked before packaging. `gguf` assets are uncompressed; first launch copies the model into private storage. Budget for APK, private model copy and installer overhead. Installed storage must be measured separately from APK bytes.

Distribution previews use non-debuggable release builds signed with Android's debug signing identity for evaluation. Build a small preview with `./gradlew :android-app:assembleRelease`. A durable production signing identity and independently repeated build comparison remain pending; no signing keys are included. Do not call it a production-signed release. Never commit keys or credentials.

Host checks remain available through `scripts/build-host.sh`, `build/runtime-host/meshmind-runtime-test` and the benchmark scripts. Native target names retained from MeshMind are internal; no messenger code is compiled. The application has no INTERNET, Bluetooth, location or broad storage permission.
