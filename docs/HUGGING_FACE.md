# Hugging Face publication

Primary source and issue tracker: https://github.com/gdalabs/bonsai-local-android . Keep the original MeshMind development repository private.

The owner authorized a public static Space at https://huggingface.co/spaces/GDALabs/Bonsai-Local-Android and an English HF Post. A static Space is a distribution/documentation page, not a cloud inference endpoint or a newly trained model. Follow the official static SDK guide: https://huggingface.co/docs/hub/spaces-sdks-static .

## Prepare and publish

```sh
python3 scripts/prepare-space.py /path/to/checked-release-folder /path/to/new-space-folder
python3 scripts/publish-space.py /path/to/new-space-folder --check-only
python3 -m pip install -r requirements-publication.txt
hf auth login
python3 scripts/publish-space.py /path/to/new-space-folder --public
```

Use standard HF authentication outside chat. Never paste access keys into source or messages. The last authentication check returned LocalTokenNotFoundError; no Space or Post has been uploaded by this session. If an existing Space has a different visibility, the publisher refuses to change it silently.

The page links to the public GitHub repository/releases and also includes a corresponding-source archive, APK mirrors, notices, checksums and the owner-reported device observations. Publish and check these URLs before posting docs/HF_POST.md. HF Posts publication uses the account's supported posting interface; no undocumented posting endpoint is used here.

## Editorial scope

Public presentation is English only, with no creator nationality/location claims. Lead with Redmi Note 13 5G, 6 GB physical RAM and the reported 4.51 tokens/s, 2.8 s TTFT, 832 MiB process peak and 256-token configured cap. Do not substitute draft Xiaomi 13 Ultra numbers. Exact app version/model/settings for the Redmi report remain unrecorded. Model Q2_0/PQ2_0 file size, APK download size and process memory are distinct.

Use “an early Android implementation,” not a world-first or ranked-priority claim. PrismML already provides Android-capable runtime code; GDA Labs integrates that code into an app. Credit ternary/Hadamard/hybrid kernels to upstream. Separate executed host/build checks from owner reports and unexecuted new-APK device tests. Do not advertise multilingual parity, independently measured phone performance, production signing or security review.

See docs/HF_PUBLICATION_STATUS.md for publication status. The earlier model-repository publisher remains available for historical distribution workflows; the current primary destination is a static Space.
