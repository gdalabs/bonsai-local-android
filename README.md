# Bonsai Local for Android

An independent, fully offline Android app by **GDA Labs**, using official **PrismML Ternary Bonsai 2 2B**. Download an ARM64 APK, load the bundled model, and chat on your phone. No account, internet permission, inference server, analytics or Bluetooth component is included.

This is an unofficial application, not a new trained model and not endorsed by PrismML. It is extracted from MeshMind's working AI runtime; the messenger and model-editing experiments are outside this source tree.

## Downloads and source

- [APK releases](https://github.com/gdalabs/bonsai-local-android/releases)
- [Source and issues](https://github.com/gdalabs/bonsai-local-android)
- [GDA Labs](https://gdalabs.dev/)

The bundled APK includes official Q2_0 weights. App size and model size differ. Corresponding source is available in this repository and in the release source archive.

## First use

1. Install the **model-bundled preview APK**. Allow Android's package installer to install it if prompted.
2. Open Bonsai Local and wait for local model verification/copying.
3. On Chat, tap **Load model & start**. No Import operation is needed for the bundled APK.
4. Ask a question. Try airplane mode with Wi-Fi off and USB disconnected.

The small APK requires a separately provisioned official GGUF. Open Models → Import or replace a model → choose Q2_0 and select the GGUF. Custom compatible models require their matching checksum manifest. Unsupported formats fail clearly.

Bonsai Local has a separate package ID (`org.gdalabs.bonsailocal`) and can coexist with MeshMind. Histories and model files are separate; private app data is not migrated automatically.

## App

- Chat: streamed responses, Stop, conversation reset confirmation, selectable text and optional readable session metrics.
- Models: installed/loaded states, explicit load/unload, deletion confirmation and secondary import flow.
- Settings: 2K/4K context, threads, memory-efficient mode and advanced temperature/top-p/top-k/output/thinking controls.
- English interface, system light/dark theme and accessible text-based controls. UI language does not establish model accuracy in that language.
- History encrypted locally with Android Keystore; backgrounding stops generation. Models are verified by SHA-256 and size.

## Evidence and limits

The C++ inference core is byte-identical to the validated MeshMind core. Inherited host evidence includes 3 exact-prompt outputs matching the official desktop release, 34 Hadamard tests and 450 ternary operation tests. These are marked inherited in `tests/results/`. The owner reported the earlier MeshMind APK working in airplane mode on a Redmi Note 13 5G, Android 14, 6 GB physical RAM. This is not a new-package instrumentation result or phone benchmark.

Fresh standalone build/check results are recorded in PROGRESS.md. No multilingual parity, standard accuracy benchmark, universal RAM compatibility, battery/thermal result or byte-identical independent build is claimed. Model answers can be wrong; generated translations may be inaccurate. This app is text-only and does not load the vision projector.

See [BUILD.md](BUILD.md), [ARCHITECTURE.md](ARCHITECTURE.md), [TEST_PLAN.md](TEST_PLAN.md), [LICENSES.md](LICENSES.md) and [Hugging Face publication](docs/HUGGING_FACE.md).


## How inference works

Bonsai Local embeds the pinned PrismML llama.cpp fork, compiled with Android NDK for ARM64 CPU execution. The upstream checkout is not modified. Kotlin/Compose calls a small application-specific JNI layer and C++ runtime adapter, which call the upstream llama.cpp APIs directly. Model loading, prompt formatting, streaming, cancellation and process metrics are integrated into the app without a local HTTP server.

PrismML's backend implements the model-specific ternary operators, Hadamard transforms and hybrid attention. The embedded model tokenizer and chat template are used. ARM CPU kernels, including applicable NEON paths, come from the backend; the app does not implement a separate tensor inference engine. Vulkan/GPU acceleration is not enabled. See inference/CMakeLists.txt, inference/jni.cpp and inference/runtime.cpp.

## Long responses and output limits

The output-token limit is a generation budget, not a character limit. Reaching it stops generation even if the answer is unfinished. Version 0.2.2 defaults to 512 tokens and offers 256/512/1024 controls on Chat. The output budget can be changed without unloading the model and takes effect on the next reply. Other memory/context settings still require unloading. A user can ask the model to continue, but this is a new conversational turn, not an exact decoder resume. Prompt and output must fit the context window. A larger budget permits more work and does not guarantee a complete or accurate answer.


## Contributing

The goal of this first release is a usable, inspectable Android starting point. Contributions are welcome for reproducible ARM64 performance measurements, CPU optimizations, battery/thermal testing, context management and accessibility. Please report the app version, model format/checksum, phone/OS, context, threads, sampling settings, prompt, output count and warm/cold conditions. Keep message history and personal data out of reports. Separate correctness checks from speed measurements; preserve the PrismML source pin and record patches with tests. See TEST_PLAN.md and PROGRESS.md for unverified areas.


## License

Application code: **Apache-2.0**, copyright 2026 GDA Labs, starting with 0.2.3. See LICENSE, NOTICE and RELICENSING.md. PrismML llama.cpp remains MIT; official Bonsai model weights remain Apache-2.0 with upstream notices. Versions through 0.2.2 keep their historical GPL-3.0-only application license. No upstream component is relicensed by this change.
