---
license: apache-2.0
language:
- en
tags:
- android
- offline
- arm64
- gguf
- llama-cpp
- bonsai
- application
---

# Bonsai Local — offline Bonsai 2 2B on Android

**Run Bonsai 2 2B locally on your Android phone — with no account or cloud inference.**

Bonsai Local is an installable Android application by GDA Labs. This is an unofficial runner for [PrismML's official Ternary Bonsai 2 2B](https://huggingface.co/prism-ml/Ternary-Bonsai-2-2B-gguf), not a newly trained model or an online inference service. PrismML affiliation/endorsement is not implied.

## Why this project

The practical demonstration is offline inference on the **Redmi Note 13 5G with 6 GB physical RAM**, with owner-reported 4.51 tokens/s, 2.8 s TTFT and 832 MiB peak process memory. See the scope and validation section for unrecorded settings and attribution. This is an early Android application integrating PrismML's runtime, not a claim of world-first Android engine support.

## Download and start

| Download | APK size | Model provisioning |
| --- | ---: | --- |
| **Bonsai-Local-0.2.3-arm64-Q2_0-bundled-preview.apk** | **575.6 MB** | Official Q2_0 included; recommended for getting started |
| Bonsai-Local-0.2.3-arm64-small-preview.apk | 28.9 MB | Import a local compatible GGUF separately |

Sizes use decimal MB. Choose **Bonsai-Local-0.2.3-arm64-Q2_0-bundled-preview.apk** under Files and versions. It includes the official Q2_0 model, so no separate model download is required after installation.

1. Install the APK on an ARM64 Android phone (Android 9 / API 28 or later).
2. Open Bonsai Local. Initial local copying and SHA-256 verification can take time.
3. Tap **Load model & start** on Chat. Import is unnecessary for the bundled APK.
4. Chat on the phone. Airplane mode, Wi-Fi off and USB disconnected can be used to verify independence from a network/PC.

The **small-preview.apk** requires separately importing a local official GGUF from Models. Downloading/installing/provisioning happens before offline use. The app itself has no INTERNET, Bluetooth or location permission and never starts a local HTTP server. No account, analytics, ads, cloud inference or remote logging is included.

Bonsai Local installs independently of MeshMind. Data/model copies are separate. The UI provides Chat, Models and Settings, with streaming/Stop, explicit load/unload, SHA-256 import verification, local encrypted history, background cancellation, memory-efficient mode, 2K/4K context and generation controls. The UI is English only, with light/dark themes. Multilingual model accuracy has not been established.

## Scope and validation

Text-only CPU inference. Official Q2_0 is the initial default; pinned manifests for PQ2_0/PTQ1_0 are available for external import. No vision, mesh messenger, Vulkan acceleration or edited/uncensored weights are included.

### Owner-reported offline phone observation

The owner reports running the app offline on a **Xiaomi Redmi Note 13 5G**. Android 14 and 6 GB physical RAM were previously reported for this device.

| Metric | Reported value |
| --- | ---: |
| Generation speed | 4.51 tokens/s |
| Time to first token | 2.8 s |
| Peak process memory | 832 MiB |
| Configured output limit | 256 tokens |

These are owner-reported observations, not an independently reproduced benchmark or an average across runs. App version, exact model format, prompt, context length, thread count, thinking mode, run count and raw logs have not yet been confirmed. Peak process memory is a process high-water mark, not model-only memory or total device memory use. These numbers do not establish accuracy or performance on other phones.

Long replies reached the 256-token output limit. The current preview defaults to 512 and exposes 256/512/1024 choices on Chat, without unloading the model. A higher limit can still be reached; prompt and output must fit the context window. This change has not yet been tested on the owner's phone.

### Inherited host evidence

The unchanged native core inherits 3 exact rendered-prompt outputs matching the official desktop reference, 34 Hadamard cases and 450 ternary operation cases. These checks are separate from the owner's phone report.

Build and packaging validation is recorded per version in PROGRESS.md:

- ARM64 Debug and Release builds succeeded.
- Debug and Release JVM suites each passed 15 tests.
- Android lint reported 0 errors and 10 warnings in each checked variant.
- Both distributed APKs passed signature, package/JNI, permission and 16 KiB alignment checks.
- The exported source passed extraction, native-file checksums and Android CMake configuration checks.

**Still pending:** installation/UI/inference testing of this new package on a physical phone, Android instrumentation execution, production signing and a complete independent rebuild comparison. See PROGRESS.md and the validation files for details. No standard MMLU/MGSM score, multilingual parity, independently reproduced phone performance, universal RAM support, thermal or battery result is claimed. Generated translations may be inaccurate. Verify outputs before relying on them.

## Files, storage and source

`artifact-inventory.json` gives exact bytes, decimal MB, binary MiB and SHA-256. `SHA256SUMS` verifies the distributed files. Model-bundled installation stores the APK plus a private model copy and installer/native/history overhead; APK size is not installed size or RAM use.

`Bonsai-Local-0.2.3-source.zip` contains the standalone app, build scripts, locked dependencies and the pinned native source snapshot. Corresponding source is provided here; access to a private GitHub repository is not required to build. See BUILD.md for repeatable commands. Independently byte-identical APK rebuilds are not yet established.

## How inference works

The APK embeds the pinned, unmodified PrismML llama.cpp fork, compiled with Android NDK for ARM64 CPU execution. Kotlin/Compose calls an application-specific JNI layer and C++ adapter, then the upstream inference APIs directly. There is no local HTTP server. Ternary operators, Hadamard transforms, hybrid attention and applicable ARM NEON kernels come from PrismML's backend; tokenization and prompt formatting use the model's embedded tokenizer and chat template. This app does not contain an independently developed tensor engine, and Vulkan/GPU acceleration is not enabled.

## Provenance and licenses

Official model revision: `683f7be2629342a50987627b40881e54761a2430`.
Q2_0 SHA-256: `c2893fa6f951daee8b7263d5f49b455e57989ef56417a22125b03566dc560650`.
PrismML runtime commit: `5ae2e169eac202d2f065606539c3ad6ab8b51333`.

- App: Apache-2.0 from 0.2.3 (LICENSE, NOTICE and corresponding source ZIP).
- Runtime: MIT (llama-MIT.txt).
- Official model weights: Apache-2.0, derived from Qwen3.5-2B; retain Bonsai-model-Apache-2.0.txt and Bonsai-model-NOTICE.txt. Model attribution and license are retained independently of the app.

This experimental preview is a non-debuggable release build, signed with the Android debug identity for testing. Production signing and a new-package device validation remain pending. It is not independently security-audited. Android Keystore encryption protects stored history within its limits; unlocked or compromised devices can expose data. No claim of perfect privacy or suitability for high-risk users is made.
