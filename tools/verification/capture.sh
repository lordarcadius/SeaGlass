#!/usr/bin/env bash
# Captures screens in every contrast mode and theme and measures contrast.
# Usage: capture.sh <output-dir> [screens...]   (screens default to the six Phase 4 routes)
# Needs adb, a running device and the debug build installed; launch extras are documented in MainActivity.
set -euo pipefail
OUT="$1"; shift
SCREENS=("${@:-screens/incall screens/library screens/inbox screens/assistant screens/settings screens/tablet}")
read -r -a SCREENS <<< "${SCREENS[*]}"
HERE="$(cd "$(dirname "$0")" && pwd)"
DENSITY="${DENSITY:-3.0}"
mkdir -p "$OUT"
for screen in "${SCREENS[@]}"; do
  for mode in standard medium high rt; do
    for theme in Light Dark; do
      name="$(echo "$screen" | tr '/' '-')-$mode-$theme"
      extras=(--es route "$screen" --es theme "$theme")
      case "$mode" in
        standard) extras+=(--es contrast Standard) ;;
        medium) extras+=(--es contrast Medium) ;;
        high) extras+=(--es contrast High) ;;
        rt) extras+=(--es contrast Standard --ez rt true) ;;
      esac
      adb shell am force-stop com.floydwiz.seaglasssample
      adb shell am start -n com.floydwiz.seaglasssample/.MainActivity "${extras[@]}" >/dev/null
      sleep "${SETTLE:-4}"
      adb exec-out screencap -p > "$OUT/$name.png"
      adb shell uiautomator dump /sdcard/sg.xml >/dev/null 2>&1
      adb exec-out cat /sdcard/sg.xml > "$OUT/$name.xml"
      python3 "$HERE/contrast.py" "$OUT/$name.png" "$OUT/$name.xml" --density "$DENSITY" > "$OUT/$name.jsonl"
      echo "$name: $(wc -l < "$OUT/$name.jsonl") measured, $(grep -c '"pass": false' "$OUT/$name.jsonl" || true) below threshold"
    done
  done
done
