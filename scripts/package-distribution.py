#!/usr/bin/env python3
"""Create a reviewable APK/source/Hub folder without publishing or reading credentials."""
import argparse
import hashlib
import json
import subprocess
import zipfile
from pathlib import Path

root = Path(__file__).resolve().parents[1]
parser = argparse.ArgumentParser()
parser.add_argument('--bundled-apk', type=Path, required=True)
parser.add_argument('--small-apk', type=Path, required=True)
parser.add_argument('--output', type=Path, required=True)
args = parser.parse_args()
assert not args.output.exists(), 'Use a fresh output directory'
args.output.mkdir(parents=True)


def digest(path):
    value = hashlib.sha256()
    with path.open('rb') as source:
        while chunk := source.read(1024 * 1024):
            value.update(chunk)
    return value.hexdigest()


def copy(source, target):
    with source.open('rb') as reader, target.open('wb') as writer:
        while chunk := reader.read(1024 * 1024):
            writer.write(chunk)


for source, name in [(args.bundled_apk, 'Bonsai-Local-0.2.2-arm64-Q2_0-bundled-preview.apk'),
                     (args.small_apk, 'Bonsai-Local-0.2.2-arm64-small-preview.apk')]:
    copy(source, args.output / name)

# Explicit application source tree; no ignored build/model/history/experiment data.
paths = [root / name for name in ['README.md', 'BUILD.md', 'ARCHITECTURE.md', 'TEST_PLAN.md', 'LICENSES.md', 'PROGRESS.md',
                                  'LICENSE', 'dependencies.json', '.gitignore', '.gitattributes', 'build.gradle.kts', 'settings.gradle.kts',
                                  'gradle.properties', 'gradlew', 'requirements-publication.txt', 'requirements-publication-checks.txt', 'android-app/build.gradle.kts', 'android-app/gradle.lockfile']]
for directory in ['android-app/src', 'inference', 'model-manager', 'benchmark', 'scripts', 'docs', 'gradle', 'tests', 'publication']:
    paths.extend(p for p in (root / directory).rglob('*') if p.is_file() and '__pycache__' not in p.parts)
upstream = root / 'third_party/llama.cpp'
if not (upstream / '.git').exists() and (upstream / 'SOURCE_REVISION').is_file():
    revision = (upstream / 'SOURCE_REVISION').read_text().strip()
    tracked = []
    for line in (upstream / 'SOURCE_SHA256SUMS').read_text().splitlines():
        expected, name = line.split('  ', 1)
        path = upstream / name
        assert path.resolve().is_relative_to(upstream.resolve()), 'Unsafe native source path'
        assert path.is_file() and not path.is_symlink(), 'Invalid native source file'
        assert digest(path) == expected, 'Native source snapshot checksum mismatch: ' + name
        tracked.append(name)
else:
    revision = subprocess.check_output(['git', '-C', str(upstream), 'rev-parse', 'HEAD'], text=True).strip()
    assert not subprocess.check_output(['git', '-C', str(upstream), 'status', '--porcelain'], text=True).strip()
    tracked = subprocess.check_output(['git', '-C', str(upstream), 'ls-files', '-z']).decode().split('\0')
assert revision == '5ae2e169eac202d2f065606539c3ad6ab8b51333'
source_zip = args.output / 'Bonsai-Local-0.2.2-source.zip'
with zipfile.ZipFile(source_zip, 'w', compression=zipfile.ZIP_DEFLATED, compresslevel=6) as archive:
    for path in sorted(set(paths)):
        assert not path.is_symlink(), f'Unexpected source link: {path}'
        relative = path.relative_to(root)
        info = zipfile.ZipInfo('BonsaiLocal/' + relative.as_posix(), date_time=(2026, 10, 16, 0, 0, 0))
        info.external_attr = (0o100755 if path.stat().st_mode & 0o111 else 0o100644) << 16
        info.compress_type = zipfile.ZIP_DEFLATED
        archive.writestr(info, path.read_bytes())
    native_checksums = []
    for name in tracked:
        if not name:
            continue
        path = upstream / name
        assert path.is_file() and not path.is_symlink(), f'Unexpected upstream link: {name}'
        info = zipfile.ZipInfo('BonsaiLocal/third_party/llama.cpp/' + name, date_time=(2026, 10, 16, 0, 0, 0))
        info.external_attr = (0o100755 if path.stat().st_mode & 0o111 else 0o100644) << 16
        info.compress_type = zipfile.ZIP_DEFLATED
        data = path.read_bytes()
        archive.writestr(info, data)
        native_checksums.append(hashlib.sha256(data).hexdigest() + "  " + name + "\n")
# Track the native source pin in an archive even without a .git directory.
with zipfile.ZipFile(source_zip, 'a', compression=zipfile.ZIP_DEFLATED) as archive:
    for name, data in [('SOURCE_REVISION', revision + '\n'), ('SOURCE_SHA256SUMS', ''.join(native_checksums))]:
        info = zipfile.ZipInfo('BonsaiLocal/third_party/llama.cpp/' + name, date_time=(2026, 10, 16, 0, 0, 0))
        info.external_attr = 0o100644 << 16
        info.compress_type = zipfile.ZIP_DEFLATED
        archive.writestr(info, data)

for name in ['BUILD.md', 'ARCHITECTURE.md', 'TEST_PLAN.md', 'LICENSES.md', 'PROGRESS.md', 'LICENSE', 'dependencies.json']:
    copy(root / name, args.output / name)
for path in (root / 'docs/notices').glob('*.txt'):
    copy(path, args.output / path.name)
copy(root / 'docs/HF_APP_CARD.md', args.output / 'README.md')
inventory = []
for path in sorted(args.output.iterdir()):
    if path.name in ['artifact-inventory.json', 'SHA256SUMS']:
        continue
    size = path.stat().st_size
    inventory.append({'file': path.name, 'bytes': size, 'MB_decimal': round(size / 1_000_000, 3),
                      'MiB_binary': round(size / 1048576, 3), 'sha256': digest(path)})
(args.output / 'artifact-inventory.json').write_text(json.dumps({'scope': 'Files, not installed storage or phone RAM', 'artifacts': inventory}, indent=2) + '\n')
(args.output / 'SHA256SUMS').write_text(''.join(f'{row["sha256"]}  {row["file"]}\n' for row in inventory))
print('Prepared', args.output, 'with', len(inventory), 'files; nothing uploaded')
