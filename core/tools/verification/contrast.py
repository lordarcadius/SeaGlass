#!/usr/bin/env python3
"""Measures text and icon contrast on an adb screenshot against the pixels behind each element.

Usage: contrast.py <screenshot.png> <uiautomator.xml> [--density 3.0]
Prints one JSON object per measured node; nodes clipped to under 12 px tall (a row cut by its list) are skipped. Text counts as large (3:1) when its ink is at least as tall as 18 sp text, otherwise it needs 4.5:1. Text is measured against the pixels in a ring just outside its bounds,
icons (nodes with a content description and no text) against the pixels around the glyph inside the control.
The reported ratio is the 5th percentile of the per-pixel contrast ratios, as the spec defines it.
"""
import json
import re
import sys
import xml.etree.ElementTree as ET

import numpy as np
from PIL import Image


def to_linear(channel):
    c = channel / 255.0
    return np.where(c <= 0.04045, c / 12.92, ((c + 0.055) / 1.055) ** 2.4)


def luminance(pixels):
    rgb = to_linear(pixels[..., :3].astype(np.float64))
    return 0.2126 * rgb[..., 0] + 0.7152 * rgb[..., 1] + 0.0722 * rgb[..., 2]


def ratio(a, b):
    hi, lo = np.maximum(a, b), np.minimum(a, b)
    return (hi + 0.05) / (lo + 0.05)


def parse_bounds(text):
    m = re.match(r"\[(\d+),(\d+)\]\[(\d+),(\d+)\]", text)
    return tuple(int(v) for v in m.groups()) if m else None


def measure(lum, inner, background):
    """inner: (x0,y0,x1,y1) glyph box; background: 1-D array of the luminance of the pixels behind it."""
    x0, y0, x1, y1 = inner
    glyph = lum[y0:y1, x0:x1].ravel()
    if background.size < 8 or glyph.size < 4:
        return None
    reference = np.median(background)
    distance = np.abs(glyph - reference)
    count = max(3, int(glyph.size * 0.01))
    foreground = float(np.mean(glyph[np.argsort(distance)[-count:]]))
    ratios = ratio(foreground, background)
    return {
        "foreground": round(foreground, 4),
        "background": round(float(reference), 4),
        "ratio_p5": round(float(np.percentile(ratios, 5)), 2),
        "ratio_median": round(float(np.median(ratios)), 2),
    }


def ink_height(lum, bounds, reference):
    """Vertical extent in pixels of the rows that contain text pixels (strongly different from the background)."""
    x0, y0, x1, y1 = bounds
    region = np.abs(lum[y0:y1, x0:x1] - reference)
    rows = np.where(region.max(axis=1) >= 0.5 * region.max())[0]
    return int(rows[-1] - rows[0] + 1) if rows.size else 0


def tighten(lum, bounds, reference):
    """Shrinks the box horizontally to the columns that hold ink, since a node can be wider than its text."""
    x0, y0, x1, y1 = bounds
    region = np.abs(lum[y0:y1, x0:x1] - reference)
    columns = np.where(region.max(axis=0) >= 0.5 * region.max())[0]
    if columns.size == 0:
        return bounds
    return (x0 + int(columns[0]), y0, x0 + int(columns[-1]) + 1, y1)


def side_background(lum, bounds, pad, width):
    """Pixels in strips just left and right of the middle of the bounds: the glass or surface the text sits on."""
    x0, y0, x1, y1 = bounds
    margin = int((y1 - y0) * 0.2)
    y0, y1 = y0 + margin, y1 - margin
    strips = []
    if x0 - pad - width >= 0:
        strips.append(lum[y0:y1, x0 - pad - width:x0 - pad].ravel())
    if x1 + pad + width <= lum.shape[1]:
        strips.append(lum[y0:y1, x1 + pad:x1 + pad + width].ravel())
    return np.concatenate(strips) if strips else np.array([])


def ring_background(lum, inner, outer):
    ox0, oy0, ox1, oy1 = outer
    x0, y0, x1, y1 = inner
    mask = np.ones((oy1 - oy0, ox1 - ox0), dtype=bool)
    mask[y0 - oy0:y1 - oy0, x0 - ox0:x1 - ox0] = False
    return lum[oy0:oy1, ox0:ox1][mask]


def main():
    image_path, xml_path = sys.argv[1], sys.argv[2]
    density = float(sys.argv[sys.argv.index("--density") + 1]) if "--density" in sys.argv else 3.0
    img = np.asarray(Image.open(image_path).convert("RGB"))
    height, width = img.shape[:2]
    lum = luminance(img)
    top_safe = int(30 * density)
    for node in ET.parse(xml_path).iter("node"):
        bounds = parse_bounds(node.get("bounds", ""))
        if not bounds or node.get("enabled") == "false":
            continue
        x0, y0, x1, y1 = bounds
        if x1 - x0 < 12 or y1 - y0 < 12 or y0 < top_safe or x1 > width or y1 > height or x0 < 0:
            continue
        text = node.get("text", "")
        desc = node.get("content-desc", "")
        w, h = x1 - x0, y1 - y0
        ligature = bool(text) and re.fullmatch(r"[a-z0-9_]+", text) is not None and abs(w - h) <= 8
        narrow = bool(text) and not ligature and w < 14 * density
        if text and not ligature and not narrow:
            pad, strip = max(3, int(2 * density)), max(4, int(3 * density))
            background = side_background(lum, bounds, pad, strip)
            if background.size >= 8:
                bounds = tighten(lum, bounds, float(np.median(background)))
                background = side_background(lum, bounds, pad, strip)
            result = measure(lum, bounds, background)
            kind = "text"
            large = result is not None and ink_height(lum, bounds, float(np.median(background))) >= 0.7 * 18 * density
        elif ligature or narrow:
            result = measure(lum, bounds, ring_background(lum, bounds, (max(0, x0 - (4 if narrow else 12)), max(0, y0 - (4 if narrow else 12)), min(width, x1 + (4 if narrow else 12)), min(height, y1 + (4 if narrow else 12)))))
            kind = "icon"
            large = True
        elif desc and node.get("clickable") != "false" or (desc and (x1 - x0) <= 90 * density):
            w, h = x1 - x0, y1 - y0
            side = min(w, h)
            cx, cy = (x0 + x1) // 2, (y0 + y1) // 2
            inner_half = int(side * 0.27)
            outer_half = int(side * 0.45)
            inner = (cx - inner_half, cy - inner_half, cx + inner_half, cy + inner_half)
            outer = (cx - outer_half, cy - outer_half, cx + outer_half, cy + outer_half)
            if outer[0] < 0 or outer[1] < 0 or outer[2] > width or outer[3] > height or side < 24 * density:
                continue
            result = measure(lum, inner, ring_background(lum, inner, outer))
            kind = "icon"
            large = True
        else:
            continue
        if result is None:
            continue
        threshold = 3.0 if (kind == "icon" or large) else 4.5
        print(json.dumps({
            "kind": kind, "label": text or desc, "bounds": bounds, "threshold": threshold,
            "pass": result["ratio_p5"] >= threshold, **result,
        }))


if __name__ == "__main__":
    main()
