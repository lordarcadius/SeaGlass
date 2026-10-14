#!/usr/bin/env bash
# Records a Perfetto trace while a scripted interaction runs, then summarizes frame work.
# Usage: trace.sh <name> <route> <interaction: idle|scroll|taps> [extra am start args...]
# Needs adb, a device with the app installed; writes perf-out/<name>.pb and prints a summary.
set -euo pipefail
NAME="$1"; ROUTE="$2"; MODE="$3"; shift 3 || true
PKG=com.floydwiz.seaglasssample
HERE="$(cd "$(dirname "$0")" && pwd)"
read -r W H < <(adb shell wm size | tail -1 | sed -E 's/.*: ([0-9]+)x([0-9]+).*/\1 \2/')
X=$((W / 2)); Y1=$((H * 8 / 10)); Y2=$((H * 25 / 100))
OUT="$HERE/../../perf-out"; mkdir -p "$OUT"
adb shell am force-stop $PKG
adb shell am start --windowingMode 1 -n $PKG/.MainActivity --es route "$ROUTE" --es theme Light --es contrast Standard "$@" >/dev/null
sleep 5
adb shell rm -f /data/misc/perfetto-traces/sg.pb
adb shell "perfetto --background --txt -c - -o /data/misc/perfetto-traces/sg.pb" >/dev/null <<CFG
buffers: { size_kb: 65536 fill_policy: RING_BUFFER }
data_sources: { config { name: "linux.ftrace" ftrace_config {
  ftrace_events: "sched/sched_switch" ftrace_events: "sched/sched_wakeup" ftrace_events: "power/cpu_frequency"
  atrace_categories: "gfx" atrace_categories: "view" atrace_categories: "input" atrace_categories: "am" atrace_categories: "wm" atrace_categories: "res" atrace_categories: "sm"
  atrace_apps: "$PKG" } } }
data_sources: { config { name: "android.surfaceflinger.frametimeline" } }
data_sources: { config { name: "linux.process_stats" process_stats_config { scan_all_processes_on_start: true } } }
duration_ms: 12000
CFG
sleep 2
case "$MODE" in
  idle) sleep 8 ;;
  scroll) for i in 1 2 3 4 5 6 7 8; do adb shell input swipe $X $Y1 $X $Y2 350; sleep 0.25; adb shell input swipe $X $Y2 $X $Y1 350; sleep 0.25; done ;;
  *) sleep 8 ;;
esac
sleep 5
adb pull /data/misc/perfetto-traces/sg.pb "$OUT/$NAME.pb" >/dev/null
python3 "$HERE/analyze.py" "$OUT/$NAME.pb" "$NAME"
