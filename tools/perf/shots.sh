#!/usr/bin/env bash
# Screenshots a list of routes in light and dark, for before/after pixel comparison with diff.py.
# Usage: shots.sh <output-dir> <route>...   (set ANDROID_SERIAL when several devices are attached)
set -euo pipefail
OUT="$1"; shift
PKG=com.floydwiz.seaglasssample
mkdir -p "$OUT"
for route in "$@"; do
  for theme in Light Dark; do
    adb shell am force-stop "$PKG"
    adb shell am start --windowingMode 1 -n "$PKG/.MainActivity" --es route "$route" --es theme "$theme" --es contrast Standard >/dev/null
    sleep "${SETTLE:-4}"
    adb exec-out screencap -p > "$OUT/$(echo "$route" | tr '/' '-')-$theme.png"
  done
done
