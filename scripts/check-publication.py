#!/usr/bin/env python3
"""Check local release pages at desktop and phone widths in Chromium."""
import argparse
import json
from pathlib import Path
from playwright.sync_api import sync_playwright

root = Path(__file__).resolve().parents[1]
parser = argparse.ArgumentParser()
parser.add_argument('--chromium', required=True)
parser.add_argument('--website', type=Path)
parser.add_argument('--output', type=Path, required=True)
args = parser.parse_args()
args.output.mkdir(parents=True, exist_ok=True)
pages = [('space', root / 'publication/space/index.html')]
if args.website:
    pages.append(('website', args.website))
results = []
with sync_playwright() as p:
    browser = p.chromium.launch(executable_path=args.chromium, headless=True,
                               args=['--no-sandbox', '--disable-dev-shm-usage'])
    for label, path in pages:
        for width, height in [(1440, 1000), (390, 844), (360, 800)]:
            page = browser.new_page(viewport={'width': width, 'height': height})
            page.route('https://fonts.googleapis.com/**', lambda route: route.abort())
            page.route('https://fonts.gstatic.com/**', lambda route: route.abort())
            errors = []
            page.on('pageerror', lambda error: errors.append(str(error)))
            page.goto(path.resolve().as_uri(), wait_until='networkidle')
            dimensions = page.evaluate('({width:innerWidth,scroll:document.documentElement.scrollWidth,lang:document.documentElement.lang})')
            assert dimensions['scroll'] <= dimensions['width'], (label, dimensions)
            assert dimensions['lang'] == 'en'
            assert not errors, errors
            if label == 'website':
                assert page.locator('.projects-grid .project-title').first.inner_text() == 'Bonsai Local'
                assert page.locator('.featured-project').is_visible()
            else:
                assert page.locator('a.primary').get_attribute('href').startswith('https://github.com/gdalabs/bonsai-local-android/')
            page.screenshot(path=str(args.output / f'{label}-{width}.png'), full_page=True)
            results.append({'page': label, 'viewport': width, 'horizontal_overflow': False, 'page_errors': errors})
            page.close()
    result = {'chromium': browser.version, 'results': results}
    browser.close()
(args.output / 'browser-checks.json').write_text(json.dumps(result, indent=2) + '\n')
print(json.dumps(result))
