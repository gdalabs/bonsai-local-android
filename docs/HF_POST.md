# Bonsai Local: Fully Offline Ternary Bonsai 2 2B Inference on Android

**GDA Labs · October 17, 2026**

Bonsai Local brings PrismML's Ternary Bonsai 2 2B to Android as an installable, fully offline AI chat app.

The result we want to highlight is practical: it runs on a **Redmi Note 13 5G with 6 GB of physical RAM**, without cloud inference. The device owner reports successful offline operation, including airplane-mode use.

Initial observations reported from this phone:

- Generation speed: **4.51 tokens/s**
- Time to first token: **2.8 seconds**
- Peak process memory: **832 MiB**
- Configured output limit during the observation: **256 tokens**

These are owner-reported observations, not an independently reproduced or multi-run benchmark. The exact app version, model format, prompt, context length, thread count and thermal conditions were not recorded with this report. Peak process memory is not model-only memory or a device RAM requirement.

PrismML announced the 2B model on **October 15, 2026**. Within days, we had an Android application and a real-device offline report. This is an early Android implementation; we make no world-first or earliest-port claim.

Under the hood, Bonsai Local uses a pinned, unmodified PrismML llama.cpp fork, compiled for ARM64 with Android NDK. GDA Labs built the Kotlin/Compose application, direct JNI/C++ integration, local model management, streaming controls and encrypted local history. The model-specific ternary operators, Hadamard transforms and hybrid attention come from PrismML's backend.

No local HTTP server, account, cloud inference, telemetry or INTERNET permission is required by the app.

The current preview includes an English interface, local GGUF import and adjustable output limits. The bundled APK includes official **Q2_0** weights; that download format should not be interpreted as confirmation of the model format used in the earlier phone observation. Model formats and file size are separate from APK size, process memory and installed storage.

Application code is Apache-2.0 starting with 0.2.3. PrismML llama.cpp remains MIT, and official model weights remain Apache-2.0 with upstream notices.

This is a working preview with development signing. The latest build and its output-limit controls still need device validation; multilingual accuracy, battery use and thermal behavior remain to be measured.

Thank you to PrismML and the llama.cpp contributors for the model and inference foundation.

Original model: https://huggingface.co/prism-ml/Ternary-Bonsai-2-2B-gguf

Official release announcement: https://prismml.com/news/bonsai-2-family

Source code and APK releases: https://github.com/gdalabs/bonsai-local-android

Project page: https://huggingface.co/GDALabs/Bonsai-Local-Android

#Bonsai2 #Android #OnDeviceAI #LocalLLM #TernaryAI #OpenSourceAI
