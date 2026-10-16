#!/usr/bin/env python3
"""Record measured artifact bytes and hashes without claiming installed size."""
import argparse
import hashlib
import json
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument("directory", type=Path)
args = parser.parse_args()
records = []
for path in sorted(args.directory.glob("*.apk")):
    digest = hashlib.sha256()
    with path.open("rb") as source:
        while chunk := source.read(1024 * 1024):
            digest.update(chunk)
    size = path.stat().st_size
    records.append({"file": path.name, "bytes": size, "MB_decimal": round(size / 1_000_000, 3),
                    "MiB_binary": round(size / 1048576, 3), "sha256": digest.hexdigest()})
assert records, "No APK artifacts found"
(args.directory / "artifact-inventory.json").write_text(json.dumps({"scope": "APK files, not installed storage or phone RAM", "artifacts": records}, indent=2) + "\n")
(args.directory / "SHA256SUMS").write_text("".join(f'{item["sha256"]}  {item["file"]}\n' for item in records))
print(json.dumps(records, indent=2))
