#!/usr/bin/env bash
# Frame statistics from dumpsys gfxinfo for one route.
# Usage: gfx.sh <route> <idle|scroll> [extra am start args...]   (set ANDROID_SERIAL when several devices are attached)
set -euo pipefail
ROUTE="$1"; MODE="$2"; shift 2 || true
PKG=${PKG:-com.floydwiz.seaglasssample}
read -r W H < <(adb shell wm size | tail -1 | sed -E 's/.*: ([0-9]+)x([0-9]+).*/\1 \2/')
X=$((W / 2)); Y1=$((H * 8 / 10)); Y2=$((H * 25 / 100))
adb shell am force-stop "$PKG"
adb shell am start --windowingMode 1 -n "$PKG/.MainActivity" --es route "$ROUTE" --es theme Light --es contrast Standard "$@" >/dev/null
sleep 4
adb shell dumpsys gfxinfo "$PKG" reset >/dev/null
case "$MODE" in
  idle) sleep 5 ;;
  scroll) for _ in 1 2 3 4 5 6; do adb shell input swipe $X $Y1 $X $Y2 300; sleep 0.2; adb shell input swipe $X $Y2 $X $Y1 300; sleep 0.2; done ;;
esac
DUMP="$(mktemp)"; trap 'rm -f "$DUMP"' EXIT
adb shell dumpsys gfxinfo "$PKG" framestats > "$DUMP"
grep -E "Total frames rendered|Janky frames:|50th percentile|90th percentile|99th percentile|50th gpu|90th gpu|Number Slow UI|Number Slow issue" "$DUMP" | tr -s ' ' | tr '\n' ';'
echo
python3 "$(dirname "$0")/stages.py" < "$DUMP"
