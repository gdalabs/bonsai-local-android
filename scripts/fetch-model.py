#!/usr/bin/env python3
"""Build-host provisioning only. The Android application never downloads models."""
import argparse
import hashlib
import json
import pathlib
import urllib.request

parser = argparse.ArgumentParser()
parser.add_argument("format", choices=["Q2_0", "PQ2_0", "PTQ1_0"])
parser.add_argument("destination", type=pathlib.Path)
args = parser.parse_args()
root = pathlib.Path(__file__).resolve().parent.parent
manifest = json.loads((root / f"model-manager/manifests/bonsai-{args.format}.json").read_text())
url = ("https://huggingface.co/prism-ml/Ternary-Bonsai-2-2B-gguf/resolve/"
       f"{manifest['version']}/Ternary-Bonsai-2-2B-{args.format}.gguf")
args.destination.parent.mkdir(parents=True, exist_ok=True)
partial = args.destination.with_suffix(".partial")
digest = hashlib.sha256()
count = 0
try:
    with urllib.request.urlopen(url, timeout=60) as response, partial.open("wb") as output:
        while chunk := response.read(1024 * 1024):
            count += len(chunk)
            if count > manifest["size_bytes"]:
                raise ValueError("Download exceeded expected size")
            digest.update(chunk)
            output.write(chunk)
    if count != manifest["size_bytes"] or digest.hexdigest() != manifest["sha256"]:
        raise ValueError("Official model checksum/size mismatch")
    partial.replace(args.destination)
finally:
    partial.unlink(missing_ok=True)
print(f"Verified {args.format}: {count} bytes, SHA-256 {digest.hexdigest()}")
