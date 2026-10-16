# Exact-model Android publication recheck

Checked October 17, 2026, before this project's dedicated public repository was created.

Public HF API search for Bonsai returned 688 model repositories and 129 Spaces. Candidate names were inspected, and seven relevant exact-2B model cards were read directly. This is a limited discovery search, not a full inspection of every file or a proof of absence.

## Findings

- Official exact-model GGUF: https://huggingface.co/prism-ml/Ternary-Bonsai-2-2B-gguf . This is model distribution, not an APK. The API's repository creation date predates the October 15 announcement; creation date is not treated as a public launch timestamp.
- Exact-2B ONNX browser packages: https://huggingface.co/danyalmalik/Ternary-Bonsai-2-2B-ONNX-WebGPU and https://huggingface.co/Mike0021/Ternary-Bonsai-2-2B-text-ONNX . The inspected cards describe browser/WebGPU execution, not an Android native APK.
- Exact-2B browser vision package: https://huggingface.co/webbrain-one/Ternary-Bonsai-2-2B-ONNX-WebGPU-Vision . Browser smoke tests do not establish a native Android app.
- Browser Space: https://huggingface.co/spaces/webml-community/ternary-bonsai-2-hermes-webgpu . Distinct from a native APK; mobile-browser compatibility was not tested here.
- Existing Bonsai 2 **27B** Android integration: https://huggingface.co/darkmaniac7/TokForge-Bonsai-2-27B-GGUF . Its card explicitly describes Android execution, but a different model size.
- Older Bonsai Android work: https://huggingface.co/livadies/Bonsai-27B-Android-Local .
- The inspected AtomicChat exact-2B repository is a LoRA adapter, not a standalone Android app; MLX/NInfer/repackaging entries are not by themselves APK evidence.

No independently verified earlier native Android APK for this exact Ternary Bonsai 2 2B model was found in the inspected HF results. Generic runners may support it, renamed projects may be missed, and new/unindexed/private work may exist. Do not infer “nobody has released it” or “world first.”

Official release announcement: https://prismml.com/news/bonsai-2-family (October 15, 2026). Preferred description: **an early Android implementation of PrismML's newly announced Ternary Bonsai 2 2B**.
