# Architecture

Bonsai Local is an independent Android source/build root. Kotlin/Compose owns Chat, Models and Settings; a ViewModel serializes inference/model operations on a background-priority worker. UI and BLE are not coupled: there is no BLE subsystem in this application.

`LocalLLMEngine` isolates the replaceable model interface. `BonsaiEngine` calls direct JNI exports for package `org.gdalabs.bonsailocal`; there is no HTTP bridge. The model's embedded tokenizer/Jinja template and the pinned PrismML Hadamard/hybrid-attention operators are used. CPU-only ARM64, conservative 2K default context, cancellable generation, context reset, explicit unload and memory-pressure handling.

The native runtime.cpp/runtime.h are preserved byte-for-byte from MeshMind. JNI package names change for the independent app. A development-only ignored symlink may reuse the pinned upstream checkout; a source distribution contains its own upstream snapshot, and `fetch-upstream.sh` can independently provision the exact commit. No parent repository path is required in a distributed build.

The model repository stages/checks local files before activation. Bundled assets follow the same verified import path. ChatStore encrypts history with an app-specific Android Keystore key. Android package/UID separation keeps existing MeshMind data untouched. Imported manifests provide integrity information, not publisher authentication; no signature verifier is claimed.

The compiled app intentionally contains no transport, mesh protocol, derivative-model experiment, telemetry SDK or remote logging path. The source distribution is an app runtime, not a training checkpoint.
