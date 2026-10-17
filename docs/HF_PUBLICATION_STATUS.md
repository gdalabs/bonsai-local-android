# Publication status

The owner authorized an English public announcement and a static Hugging Face Space on October 17, 2026. The primary demonstration is the Redmi Note 13 5G, using the reported 4.51 tokens/s, 2.8 s TTFT and 832 MiB process peak. The proposed Xiaomi 13 Ultra figures are not used or reassigned to another device.

## Prepared destinations

- Announcement title: **Bonsai Local: Fully Offline Ternary Bonsai 2 2B Inference on Android**.
- Post body: docs/HF_POST.md. HF Posts URL is not assigned; the post has not been published.
- Static Space: https://huggingface.co/spaces/GDALabs/Bonsai-Local-Android . Planned destination, not deployed.
- Page source: publication/space/index.html and publication/space/README.md.
- Page is for distribution/documentation only. Android inference does not run in the Space.

The standard HF client still reports LocalTokenNotFoundError. No HF post/repository/upload has been created by this session. Establish authentication using standard `hf auth login` outside chat, then use the Space publisher. Do not send access keys in messages.

The draft post links to the planned Space. Publish and verify the Space and its downloads before posting that announcement. No screenshot was supplied in this session, so none is fabricated or labeled as a device capture.

## Checked sources and scope

- PrismML's official announcement dates the 2B/4B/9B release October 15, 2026: https://prismml.com/news/bonsai-2-family .
- Model formats and sizes: https://huggingface.co/prism-ml/Ternary-Bonsai-2-2B-gguf .
- HF static Spaces use sdk: static and app_file: index.html: https://huggingface.co/docs/hub/spaces-sdks-static .
- Existing older Bonsai Android work: https://huggingface.co/livadies/Bonsai-27B-Android-Local .

Limited exact-name Android searches on October 17 found no independently verified earlier exact-model Android APK in the inspected results. This is not an exhaustive survey or proof of precedence. Use “an early Android implementation,” with no ranking claim. PrismML already provides Android-capable runtime code; do not imply GDA Labs originated the underlying engine's Android support.

Public GitHub publication is now authorized for the independent repository https://github.com/gdalabs/bonsai-local-android . Keep MeshMind private. The website project card should link to this dedicated repository after publication.


## Verified publication results

- Dedicated source repository: https://github.com/gdalabs/bonsai-local-android — PUBLIC, initial source commit cbbc96b. Unauthenticated raw README access returned HTTP 200.
- Public APK/source release: https://github.com/gdalabs/bonsai-local-android/releases/tag/v0.2.2-preview — six assets uploaded. Both APK digests match the tested private preview artifacts. Source ZIP contains the corresponding app and the pinned native source snapshot.
- Official website: https://gdalabs.dev/ — deployed through the existing Cloudflare Pages project gdalabs-website, from website commit af1ac1b. Production HTTP 200 confirmed the upper-left link, first project card and dedicated repository URL. GitHub Pages is an additional deployment, not the custom domain's production path.
- Local browser checks: Chromium 155.0.8059.39, Space and website at widths 1440/390/360; all six checks passed with no horizontal overflow or page errors. These are website checks, not Android-device screenshots/tests.
- Independent public-source clone and all native source SHA-256 checks passed. App/native sources match tested 0.2.2; no complete new public-clone APK rebuild was run.
- HF Space upload bundle: release asset Bonsai-Local-0.2.2-HF-Space-upload.zip. Its files/checksums were verified by the publisher's check-only mode. Space and HF Post remain unpublished because standard HF authentication is absent.
- Announcement draft is readable at https://github.com/gdalabs/bonsai-local-android/blob/main/docs/HF_POST.md . This is a public draft, not a published HF Post; the HF Post URL remains unassigned.

The owner created a public model-type application distribution repository and then capitalized its name: https://huggingface.co/GDALabs/Bonsai-2-2B-Local-Android . This is now the actual destination. The static Space path is a separate optional destination. The public model repository initially contains only README.md and .gitattributes, with Apache-2.0 metadata; application files are pending the 0.2.3 upload.


## Apache-2.0 application preview 0.2.3

The owner selected Apache-2.0 for GDA Labs application code. Version 0.2.3 rebuilds both APKs with the application license/NOTICE and removes the old application GPL asset. Runtime MIT and model Apache-2.0/NOTICE remain byte-identical. Earlier 0.2.2 artifacts retain their historical terms. Both builds, 15 Release JVM tests, small lint (0 errors/10 warnings), APK/source version matching and packaged license/signature/permission/JNI/ARM64/16 KiB checks passed. No new phone measurements were performed.

The actual HF model-type repository https://huggingface.co/GDALabs/Bonsai-2-2B-Local-Android was confirmed public with Apache-2.0 metadata and only README.md/.gitattributes before the owner uploads the new files. The primary handoff is Bonsai-Local-0.2.3-HF-upload.zip for that repository; it is not the older Space-specific ZIP. Extract and upload the contents. Standard API authentication in this environment has not been established; the agent has not uploaded files to HF.


## Published Apache preview handoff

Release https://github.com/gdalabs/bonsai-local-android/releases/tag/v0.2.3-preview is public with all six assets uploaded. Both APK server digests match the tested artifacts. The HF upload ZIP includes the approved formal name **Bonsai 2 2B Local for Android**, Apache application notices, corresponding source and links to https://huggingface.co/GDALabs/Bonsai-2-2B-Local-Android . Every enclosed SHA256SUMS entry passed. The HF repository is public with Apache-2.0 metadata, but application files still require manual upload; extract the ZIP and upload its contents. The app label remains Bonsai Local. No new Android-device test or HF Post publication is claimed.
