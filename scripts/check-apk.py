#!/usr/bin/env python3
"""Check the packaged permission set and native ABI, not just source manifests."""
import argparse
import os
import pathlib
import re
import subprocess
import struct
import zipfile
import json
import hashlib

parser = argparse.ArgumentParser()
parser.add_argument("apk", type=pathlib.Path)
parser.add_argument("--release", action="store_true", help="Require a non-debuggable application")
parser.add_argument("--bundled", action="store_true", help="Require and verify an official bundled model")
args = parser.parse_args()
sdk = pathlib.Path(os.environ["ANDROID_HOME"])
aapt = sdk / "build-tools/36.0.0/aapt2"
permissions = subprocess.check_output([str(aapt), "dump", "permissions", str(args.apk)], text=True)
assert "android.permission.INTERNET" not in permissions, permissions
assert not any(p in permissions for p in ["android.permission.BLUETOOTH", "android.permission.ACCESS_FINE_LOCATION", "android.permission.MANAGE_EXTERNAL_STORAGE"]), permissions
badging = subprocess.check_output([str(aapt), "dump", "badging", str(args.apk)], text=True)
assert "package: name='org.gdalabs.bonsailocal'" in badging, badging[:500]
if args.release:
    assert "application-debuggable" not in badging, "Release APK is debuggable"
with zipfile.ZipFile(args.apk) as notices:
    assert all("assets/" + name in notices.namelist() for name in ["GPL-3.0.txt", "Bonsai-model-NOTICE.txt", "Bonsai-model-Apache-2.0.txt", "llama-MIT.txt"]), "Missing license notices"
with zipfile.ZipFile(args.apk) as apk:
    libs = [n for n in apk.namelist() if n.startswith("lib/") and n.endswith(".so")]
    assert "lib/arm64-v8a/libmeshmind.so" in libs, libs
    assert all(n.startswith("lib/arm64-v8a/") for n in libs), libs
    models = [n for n in apk.namelist() if n.endswith(".gguf")]
    if args.bundled:
        assert models == ["assets/bundled-model.gguf"], models
        metadata = json.loads(apk.read("assets/bundled-model.json"))
        official = json.loads((pathlib.Path(__file__).resolve().parents[1] / "model-manager" / "manifests" / ("bonsai-" + metadata["gguf_format"] + ".json")).read_text())
        assert metadata == official, "Bundled manifest differs from pinned official manifest"
        assert apk.getinfo(models[0]).file_size == metadata["size_bytes"]
        digest = hashlib.sha256()
        with apk.open(models[0]) as source:
            while chunk := source.read(1024 * 1024):
                digest.update(chunk)
        assert digest.hexdigest() == metadata["sha256"], "Bundled GGUF checksum mismatch"
    else:
        assert not models, models
    core = apk.read("lib/arm64-v8a/libmeshmind.so")
    for method in ["Create", "Load", "Generate", "Cancel", "Prepare", "Reset", "Unload", "Close"]:
        assert ("Java_org_gdalabs_bonsailocal_BonsaiEngine_native" + method).encode() in core, method
    assert b"Java_org_meshmind_app_" not in core, "Stale parent JNI library"
    for name in libs:
        elf = apk.read(name)
        assert elf[:4] == b"\x7fELF" and int.from_bytes(elf[18:20], "little") == 183, name
        offset = struct.unpack_from("<Q", elf, 32)[0]
        entry_size, count = struct.unpack_from("<HH", elf, 54)
        for index in range(count):
            header = offset + index * entry_size
            if struct.unpack_from("<I", elf, header)[0] == 1:
                assert struct.unpack_from("<Q", elf, header + 48)[0] >= 16384, name
subprocess.run([str(sdk / "build-tools/36.0.0/zipalign"), "-c", "-P", "16", "4", str(args.apk)], check=True)
print("PASS: standalone package/JNI; no network/Bluetooth/location/storage permission; ARM64 ELF; 16 KiB ELF/ZIP alignment; " + ("verified bundled model" if args.bundled else "no bundled model"))
