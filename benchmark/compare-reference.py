#!/usr/bin/env python3
"""Compare local CPU output using byte-identical rendered chat prompts."""
import argparse
import json
import pathlib
import subprocess
import tempfile

parser = argparse.ArgumentParser()
parser.add_argument("model", type=pathlib.Path)
parser.add_argument("--reference", type=pathlib.Path, required=True)
parser.add_argument("--binary", type=pathlib.Path, default=pathlib.Path("build/runtime-host/meshmind-bench"))
parser.add_argument("--output", type=pathlib.Path, required=True)
parser.add_argument("--prompt-index", type=int, choices=range(3))
args = parser.parse_args()
fixture = json.loads(pathlib.Path("benchmark/prompts.json").read_text())
results = []
with tempfile.TemporaryDirectory(prefix="meshmind-reference-") as directory:
    for index, prompt in enumerate(fixture["prompts"]):
        if args.prompt_index is not None and index != args.prompt_index:
            continue
        rendered_file = pathlib.Path(directory) / "prompt.txt"
        native = subprocess.run([str(args.binary), str(args.model), prompt, "64", "0", str(rendered_file)], capture_output=True, text=True, timeout=900)
        if native.returncode:
            raise SystemExit(f"Native run failed: {native.stderr[-1000:]}")
        rendered = rendered_file.read_text(encoding="utf-8")
        # llama-completion -f removes a trailing newline. -p via argv preserves it.
        reference = subprocess.run([str(args.reference), "-m", str(args.model), "-ngl", "0", "-c", "2048",
            "-b", "64", "-ub", "64", "-t", "2", "-n", "64", "--temp", "0", "--top-p", "0.8",
            "--top-k", "20", "--presence-penalty", "1.5", "--repeat-penalty", "1", "--seed", "42",
            "--no-conversation", "--no-display-prompt", "-p", rendered], capture_output=True, text=True, timeout=900)
        native_text = native.stdout.strip()
        reference_text = reference.stdout.replace("[end of text]", "").strip()
        results.append({"prompt_index": index, "prompt": prompt, "rendered_prompt": rendered,
            "native_exit": native.returncode, "reference_exit": reference.returncode,
            "native_output": native_text, "reference_output": reference_text,
            "equal_normalized_text": reference.returncode == 0 and native_text == reference_text})
        print("Prompt", index, "MATCH" if results[-1]["equal_normalized_text"] else "DIFF", flush=True)
report = {"scope": "Host CPU normalized text comparison, not full-logit equivalence or phone validation",
          "settings": fixture["settings"], "results": results}
args.output.parent.mkdir(parents=True, exist_ok=True)
args.output.write_text(json.dumps(report, ensure_ascii=False, indent=2) + "\n")
if not all(result["equal_normalized_text"] for result in results):
    raise SystemExit("Reference output mismatch; inspect the report")
