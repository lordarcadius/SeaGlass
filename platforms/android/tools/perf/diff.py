#!/usr/bin/env python3
"""Compares two screenshot folders from shots.sh: per image mean absolute difference and share of pixels differing by more than 16/255."""
import sys
from pathlib import Path

from PIL import Image, ImageChops

CROP = (0, 106, 1920, 1022)
before, after = Path(sys.argv[1]), Path(sys.argv[2])
for path in sorted(before.glob("*.png")):
    other = after / path.name
    if not other.exists():
        continue
    a = Image.open(path).convert("RGB").crop(CROP)
    b = Image.open(other).convert("RGB").crop(CROP)
    diff = ImageChops.difference(a, b).convert("L")
    histogram = diff.histogram()
    total = a.width * a.height
    mean = sum(i * n for i, n in enumerate(histogram)) / total
    over = sum(histogram[17:]) / total
    print(f"{path.stem:34s} mean={mean:5.2f}  over16={over * 100:5.2f}%")
