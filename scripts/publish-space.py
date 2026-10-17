#!/usr/bin/env python3
"""Publish a verified static Space using standard Hub authentication."""
import argparse
import hashlib
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument('folder', type=Path)
parser.add_argument('--public', action='store_true')
parser.add_argument('--check-only', action='store_true')
args = parser.parse_args()
repo = 'GDALabs/Bonsai-Local-Android'
for line in (args.folder / 'SHA256SUMS').read_text().splitlines():
    expected, name = line.split('  ', 1)
    path = args.folder / name
    assert path.resolve().parent == args.folder.resolve() and not path.is_symlink()
    value = hashlib.sha256()
    with path.open('rb') as stream:
        while chunk := stream.read(1024 * 1024):
            value.update(chunk)
    assert value.hexdigest() == expected, 'Checksum mismatch: ' + name
required = {'README.md', 'index.html', 'LICENSE', 'NOTICE', 'LICENSES.md', 'device-report.json',
            'Bonsai-Local-0.2.3-source.zip', 'Bonsai-Local-0.2.3-arm64-Q2_0-bundled-preview.apk'}
assert all((args.folder / name).is_file() for name in required)
assert 'sdk: static' in (args.folder / 'README.md').read_text()
assert 'license: apache-2.0' in (args.folder / 'README.md').read_text(), 'Wrong application license metadata'
allowed = {line.split('  ', 1)[1] for line in (args.folder / 'SHA256SUMS').read_text().splitlines()}
assert {path.name for path in args.folder.iterdir()} == allowed | {'SHA256SUMS', 'artifact-inventory.json'}
print('Space files and checksums verified')
if args.check_only:
    raise SystemExit(0)
from huggingface_hub import HfApi
from huggingface_hub.errors import RepositoryNotFoundError
api = HfApi()
try:
    info = api.whoami()
except Exception:
    raise SystemExit('Hub authentication unavailable. Use standard hf auth login outside chat, then retry.')
assert info.get('name', '').lower() == 'gdalabs' or any(o.get('name', '').lower() == 'gdalabs' for o in info.get('orgs', [])), 'No reported GDALabs namespace membership'
private = not args.public
try:
    existing = api.repo_info(repo, repo_type='space')
except RepositoryNotFoundError:
    existing = None
if existing is not None:
    assert existing.private == private, 'Existing visibility differs; refusing to change it silently'
api.create_repo(repo, repo_type='space', space_sdk='static', private=private, exist_ok=True)
api.upload_folder(repo_id=repo, repo_type='space', folder_path=args.folder, commit_message='Publish Bonsai Local Android preview and source')
print('Uploaded https://huggingface.co/spaces/' + repo)
