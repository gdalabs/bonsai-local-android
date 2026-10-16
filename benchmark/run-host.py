#!/usr/bin/env python3
"""Record actual host runs. These numbers are never Android performance claims."""
import argparse
import hashlib
import json
import pathlib
import platform
import subprocess
from datetime import datetime, timezone

parser = argparse.ArgumentParser()
parser.add_argument("model", type=pathlib.Path)
parser.add_argument("--binary", type=pathlib.Path, default=pathlib.Path("build/runtime-host/meshmind-bench"))
parser.add_argument("--output", type=pathlib.Path, required=True)
args = parser.parse_args()
fixture = json.loads(pathlib.Path("benchmark/prompts.json").read_text())
digest = hashlib.sha256()
with args.model.open("rb") as source:
    while chunk := source.read(1024 * 1024):
        digest.update(chunk)
results = []
for prompt in fixture["prompts"]:
    completed = subprocess.run([str(args.binary), str(args.model), prompt, str(fixture["settings"]["max_tokens"])], capture_output=True, text=True)
    records = [line for line in completed.stderr.splitlines() if line.startswith('{"load_ms"')]
    results.append({"prompt": prompt, "exit_code": completed.returncode, "output": completed.stdout,
                    "metrics": json.loads(records[-1]) if records else None,
                    "error_tail": completed.stderr.splitlines()[-5:] if completed.returncode else []})
report = {"platform": "host-only " + platform.system() + " " + platform.machine(),
          "timestamp_utc": datetime.now(timezone.utc).isoformat(), "model_sha256": digest.hexdigest(),
          "settings": fixture["settings"], "results": results}
args.output.parent.mkdir(parents=True, exist_ok=True)
args.output.write_text(json.dumps(report, ensure_ascii=False, indent=2) + "\n")
if any(item["exit_code"] or item["metrics"] is None for item in results):
    raise SystemExit("One or more actual inference runs failed; inspect report")
print("Recorded", len(results), "actual host inference runs")
