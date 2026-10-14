#!/usr/bin/env bash
# Counts the frames an app renders while a static route sits untouched.
# Usage: idle-frames.sh <route> [seconds] [extra am start args...]
# A route that is truly idle renders about 0 frames; each frame here is wasted work.
set -euo pipefail
ROUTE="$1"; SECONDS_IDLE="${2:-5}"; shift || true; shift || true
PKG=com.floydwiz.seaglasssample
adb shell am force-stop $PKG
adb shell am start -n $PKG/.MainActivity --es route "$ROUTE" --es theme Light --es contrast Standard "$@" >/dev/null
sleep 4
adb shell dumpsys gfxinfo $PKG reset >/dev/null
sleep "$SECONDS_IDLE"
adb shell dumpsys gfxinfo $PKG | awk -v route="$ROUTE" '/Total frames rendered/{f=$4} /Janky frames:/{j=$3} /50th percentile/{p50=$3} /90th percentile/{p90=$3} END{printf "%-22s frames=%s janky=%s p50=%s p90=%s\n", route, f, j, p50, p90}'
