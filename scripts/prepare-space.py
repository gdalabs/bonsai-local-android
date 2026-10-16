#!/usr/bin/env python3
"""Prepare a static Hub page and verified release mirrors without publishing."""
import argparse
import hashlib
import json
import shutil
from pathlib import Path

root = Path(__file__).resolve().parents[1]
parser = argparse.ArgumentParser()
parser.add_argument('distribution', type=Path)
parser.add_argument('output', type=Path)
args = parser.parse_args()
assert args.distribution.is_dir()
assert not args.output.exists(), 'Use a new output directory'
for line in (args.distribution / 'SHA256SUMS').read_text().splitlines():
    expected, name = line.split('  ', 1)
    source = args.distribution / name
    assert source.parent == args.distribution and not source.is_symlink()
    value = hashlib.sha256()
    with source.open('rb') as stream:
        while chunk := stream.read(1024 * 1024):
            value.update(chunk)
    assert value.hexdigest() == expected, 'Distribution mismatch: ' + name
shutil.copytree(args.distribution, args.output)
for name in ['README.md', 'index.html']:
    shutil.copy2(root / 'publication/space' / name, args.output / name)
shutil.copy2(root / 'benchmark/redmi-note-13-user-report.json', args.output / 'device-report.json')
shutil.copy2(root / 'docs/HF_POST.md', args.output / 'ANNOUNCEMENT.md')
rows = []
for path in sorted(args.output.iterdir()):
    if path.name in ['artifact-inventory.json', 'SHA256SUMS']:
        continue
    assert path.is_file() and not path.is_symlink()
    value = hashlib.sha256()
    with path.open('rb') as stream:
        while chunk := stream.read(1024 * 1024):
            value.update(chunk)
    rows.append({'file': path.name, 'bytes': path.stat().st_size, 'sha256': value.hexdigest()})
(args.output / 'artifact-inventory.json').write_text(json.dumps({'scope': 'Static page and release files; no cloud inference', 'artifacts': rows}, indent=2) + '\n')
(args.output / 'SHA256SUMS').write_text(''.join(row['sha256'] + '  ' + row['file'] + '\n' for row in rows))
print('Prepared static Space:', args.output, '; nothing uploaded')
