# Bonsai Local progress

## 2026-10-16 — independent app extraction and redesign

User requested extracting only MeshMind's Bonsai 2 2B Android runtime as an APK-distributable app for Hugging Face, and supplied namespace GDALabs.

Implemented an independent source/build root with package org.gdalabs.bonsailocal. Core runtime remains byte-identical; JNI package exports and Kotlin model interfaces are renamed. No messenger or derivative-model experiment is included. Redesigned Chat/Models/Settings, English/Japanese labels, system light/dark palette, installed-versus-loaded states, one-step bundled start, secondary import, confirmation for deletion/reset, streaming/Stop and readable optional metrics. Existing MeshMind package/data/release are unchanged.

Pinned official Q2_0/PQ2_0/PTQ1_0 manifests, backend, Gradle locks/verification metadata retained. Official model NOTICE/LICENSE fetched at the pinned revision. Standalone source packaging and Hugging Face app card are being prepared. Small APK, tests, lint and ARM64 native build are running; no success is claimed here before results are recorded.

Device/new UI validation remains pending. The earlier user-reported Redmi Note 13 5G airplane-mode success applies to MeshMind, not automatically to this package. No new benchmark or broad model-accuracy claim is made. Preview uses debug signing; production release signing is pending.

## 2026-10-17 — fresh checks

Independent ARM64 debug/test APK build and latest UI compilation succeeded. Fresh Debug JVM suite: 15 tests, 0 failures/errors. Debug lint: 0 errors, 10 warnings. The small non-debuggable Release APK and Release lint also built successfully. Packaged standalone application ID/JNI export set, ARM64, 16 KiB ELF/ZIP alignment, no network/Bluetooth/location/broad storage permissions and required license assets passed inspection; small APK signature verification passed.

The native core and engine interface were compared against the parent: runtime.cpp/runtime.h byte-identical, JNI and engine interface differ only in package names. This establishes inherited source identity, not new-device output equivalence. Device instrumentation remains unexecuted. Instrumented history tests now use an isolated directory and key alias.

Official Q2_0 model-bundled Release APK and fresh Release JVM checks are running. Preview Release builds are non-debuggable but use the existing Android development signing identity. No production signing claim is made. Hub standard authentication is absent; no HF upload has occurred.

### Final preview APKs

Small and bundled non-debuggable Release builds succeeded. Fresh Release JVM suite: 15/15 pass, 0 failures/errors. Release lint: 0 errors, 10 warnings. The updated isolated instrumentation tests compile; device execution remains pending.

Both distribution APKs pass packaged package/JNI, ARM64, 16 KiB alignment, required license assets, no INTERNET/Bluetooth/location/broad storage permissions and `apksigner verify` checks. Bundled Q2_0 matches the exact pinned official checksum/size/manifest.

- Bundled Release preview: **575,632,610 bytes = 575.633 MB**.
- Small Release preview: **28,942,476 bytes = 28.942 MB**.
- Bundled APK + private official model copy: 1,122,322,114 bytes before installer/native/history overhead; computed, not measured installed storage.

App source captured at `f750fef`; native source identity and detailed results are in tests/results. Preview uses the existing Android development signing identity, not a production release key. Hugging Face target is GDALabs/Bonsai-Local-Android; standard client authentication is absent, so no upload was attempted. Source/card/APK folder is prepared for publication and private trial download. New-package phone/UI testing remains pending.

### Export validation and publication gate

The source ZIP was independently extracted: CRC/path checks, all vendored native-source SHA-256 checks and Android CMake configuration **passed**. Missing Git build-info warnings are expected for a source snapshot without .git. No complete independent APK rebuild/comparison or new-package device test was performed. Detailed source validation and distribution inventory are retained in tests/results and the prepared Hub folder.

A flat Hub folder and one downloadable HF-upload ZIP are prepared for GDALabs/Bonsai-Local-Android. Both APKs, source, app card, notices, exact sizes/hashes and build/test records are included. Standard Hub client authentication is absent; no Hub repository/upload was created by this session. Private GitHub trial distribution is complete: https://github.com/gdalabs/MeshMind/releases/tag/bonsai-local-v0.2.0-preview . All six assets report uploaded; GitHub APK/source digests match local checksums. The HF-upload ZIP also passed full member SHA-256/CRC verification. Hub publication remains pending authentication.


## 2026-10-17 — Hub publication copy

Updated docs/HF_APP_CARD.md with an English publication-ready introduction, APK choice/size table, installation instructions and explicit fresh-versus-inherited validation. No new performance or accuracy results are claimed. This is repository description copy, not a separate article. The previously checksummed release/upload ZIPs remain unchanged; use the latest card as the Hub README when publishing. Hub upload is still pending authentication.


## 2026-10-17 — English-only public presentation

At owner request, removed Japanese UI translations and system-language switching. App and current publication copy are English only, with no creator nationality/location claim. Version 0.2.1 uses versionCode 3 so it can update the prior preview. Historical test inputs are preserved as Unicode-escaped JSON; upstream license/source attribution is unchanged. Existing 0.2.0 archives remain historical artifacts. Fresh APK rebuild/checks and updated distribution are pending below.

0.2.1 results: both Release APKs built; Release JVM 15/15 passed; small Release lint 0 errors/10 warnings. Both APKs passed signature/permissions/JNI/ARM64/16 KiB checks, bundled official Q2_0 checksum verified. Small 28,926,088 bytes; bundled 575,616,226 bytes. No new-device tests or performance measurements. See tests/results/english-only-0.2.1.json. Updated private distribution and HF preparation use 0.2.1; historical 0.2.0 remains unchanged.


## 2026-10-17 — owner phone report and output budget

Owner reports offline Xiaomi Redmi Note 13 5G operation: 4.51 tokens/s, 2.8 s TTFT, 832 MiB peak process memory, configured output limit 256. Version/model/settings/raw logs remain unconfirmed; requested clarification. This is a reported observation, not an independently reproduced benchmark. Recorded in benchmark/redmi-note-13-user-report.json and the Hub card.

0.2.2 raises the Kotlin generation default to 512, adds Chat output choices 256/512/1024, and permits changing only the output budget without unloading the model. Retains the length warning with an actionable explanation; clears stale prior-run metrics at new generation. No automatic continuation or context/native runtime change. New builds/checks pending below. Model quality and truncated-output completion are not guaranteed by raising a cap.

0.2.2 validation: both Release APK builds succeeded; Release JVM 15/15; small Release lint 0 errors/10 warnings. Both APK signatures, package/JNI/ARM64/16 KiB/permissions passed; bundled official model checksum passed. New controls have not been tested on the physical phone. Native core unchanged. See tests/results/output-budget-0.2.2.json.


## 2026-10-17 — public release preparation

The owner authorized a dedicated public GitHub repository, a public HF announcement/Space and a Bonsai Local listing on gdalabs.dev. Lead with the Redmi Note 13 5G report, not proposed Xiaomi 13 Ultra figures. Rechecked exact-model prior art: no earlier native Android APK was verified in inspected HF results; browser exact-2B and older/different-size Android projects exist. No priority ranking is claimed.

Prepared an English announcement, static Space page and checksum-verifying preparation/publishing scripts. HF standard authentication remains absent. Exported independent application source with pinned vendored native source; no private MeshMind git history, model-editing experiments or first-party binary weights included. Updated source packaging to work from both a native Git checkout and a verified SOURCE_REVISION/SOURCE_SHA256SUMS snapshot. Public repository and site deployment results are recorded separately after completion.


Public distribution completed: gdalabs/bonsai-local-android (PUBLIC, initial cbbc96b), release v0.2.2-preview with six verified uploaded assets. APK hashes match tested 0.2.2 artifacts. New clone native checksums passed; app/native sources match the tested source. Both local static pages passed browser checks at 1440/390/360. gdalabs.dev was deployed through existing Cloudflare Pages project gdalabs-website from website commit af1ac1b; production HTTP 200 confirmed the upper-left link and first card. HF Post is only a public draft and Space is prepared, not uploaded: standard client authentication absent. See docs/HF_PUBLICATION_STATUS.md and tests/results/publication-browser.json. No new Android build or phone measurement was performed for this publication-only change.


## 2026-10-17 — Apache-2.0 application release

At the owner's explicit request, changed GDA Labs application licensing to Apache-2.0 for 0.2.3. Added application NOTICE and license assets; preserved PrismML MIT, model Apache-2.0 and model NOTICE byte-for-byte. Updated APK notice checks, repository/HF/static-page metadata and distribution tooling. Native inference and Kotlin behavior are unchanged; only version and license assets change in the APK. Historical GPL releases remain unchanged. Fresh APK builds and packaging validation are pending below.

0.2.3 checks: small and bundled Release builds passed; Release JVM 15/15; small lint 0 errors/10 warnings. Both APKs passed updated Apache license/NOTICE/no-stale-GPL, source-version, signature, package/JNI, permissions, ARM64 and 16 KiB checks. Official bundled model SHA verified; signing identity matches 0.2.2. No inference changes or new phone measurements. See tests/results/apache-release-0.2.3.json.


## Public name

The owner selected **Bonsai 2 2B Local for Android** as the formal project name. The installed application remains **Bonsai Local**. Existing GitHub and HF repository URLs remain stable. This documentation-only change does not alter the tested APKs.

The owner renamed the HF model repository to https://huggingface.co/GDALabs/Bonsai-2-2B-Local-Android . Publication links and the model-repository uploader now target that exact identifier. GitHub remains bonsai-local-android.
