#!/usr/bin/env bash
# Screenshots a list of routes in light and dark, for before/after pixel comparison with diff.py.
# Usage: shots.sh <output-dir> <route>...   (set ANDROID_SERIAL when several devices are attached)
# Wakes the screen only when it is asleep (a key event can switch Compose to keyboard focus mode and draw focus rings) and retries a route until the app owns the window focus, so a sleeping display or a system popup does not end up in the capture.
set -euo pipefail
OUT="$1"; shift
PKG=com.floydwiz.seaglasssample
mkdir -p "$OUT"
for route in "$@"; do
  for theme in Light Dark; do
    file="$OUT/$(echo "$route" | tr '/' '-')-$theme.png"
    for attempt in 1 2 3 4; do
      adb shell dumpsys power | grep -q 'mWakefulness=Awake' || { adb shell input keyevent KEYCODE_WAKEUP; sleep 2; }
      adb shell am force-stop "$PKG"
      adb shell am start --windowingMode 1 -n "$PKG/.MainActivity" --es route "$route" --es theme "$theme" --es contrast Standard >/dev/null
      sleep "${SETTLE:-4}"
      adb shell dumpsys window | grep -q "mCurrentFocus.*$PKG" && break
      echo "retry $attempt: $PKG not focused for $file" >&2
    done
    adb exec-out screencap -p > "$file"
  done
done
