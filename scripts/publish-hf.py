#!/usr/bin/env python3
"""Upload a checked distribution through standard Hub authentication; never print keys."""
import argparse
import hashlib
from pathlib import Path
from huggingface_hub import HfApi

parser = argparse.ArgumentParser()
parser.add_argument('folder', type=Path)
parser.add_argument('--repo', default='GDALabs/Bonsai-Local-Android')
parser.add_argument('--public', action='store_true', help='Explicitly publish publicly; default is a private preview')
args = parser.parse_args()
assert args.repo == 'GDALabs/Bonsai-Local-Android', 'Review the script before changing the publication destination'
assert args.folder.is_dir()
for line in (args.folder / 'SHA256SUMS').read_text().splitlines():
    expected, name = line.split('  ', 1)
    path = args.folder / name
    assert path.resolve().parent == args.folder.resolve() and not path.is_symlink()
    digest = hashlib.sha256()
    with path.open('rb') as source:
        while chunk := source.read(1024 * 1024):
            digest.update(chunk)
    assert digest.hexdigest() == expected, 'Distribution checksum mismatch: ' + name
for name in ['README.md', 'Bonsai-Local-0.2.3-source.zip', 'Bonsai-Local-0.2.3-arm64-Q2_0-bundled-preview.apk', 'LICENSES.md', 'LICENSE', 'NOTICE']:
    assert (args.folder / name).is_file(), 'Missing distribution file: ' + name
assert 'license: apache-2.0' in (args.folder / 'README.md').read_text(), 'Wrong application license metadata'
api = HfApi()
try:
    info = api.whoami()
except Exception:
    raise SystemExit('Hugging Face authentication is unavailable. Use standard `hf auth login` locally, then retry. Do not paste credentials into source or chat.')
allowed = info.get('name', '').lower() == 'gdalabs' or any(o.get('name', '').lower() == 'gdalabs' for o in info.get('orgs', []))
assert allowed, 'Authenticated account has no reported GDALabs namespace membership'
api.create_repo(repo_id=args.repo, repo_type='model', private=not args.public, exist_ok=True)
assert api.model_info(args.repo).private == (not args.public), 'Existing visibility differs; choose the matching explicit flag before upload'
result = api.upload_folder(repo_id=args.repo, repo_type='model', folder_path=str(args.folder), commit_message='Release Bonsai Local Android experimental preview with corresponding source')
print('Uploaded checked application distribution: https://huggingface.co/' + args.repo)
