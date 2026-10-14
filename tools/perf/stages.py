#!/usr/bin/env python3
"""Per-stage frame time medians/p90 from `dumpsys gfxinfo <pkg> framestats` read on stdin."""
import statistics
import sys

lines = sys.stdin.read().splitlines()
try:
    start = lines.index("---PROFILEDATA---")
except ValueError:
    print("no framestats")
    sys.exit(0)
header = lines[start + 1].rstrip(",").split(",")
col = {name: i for i, name in enumerate(header)}
rows = []
for line in lines[start + 2:]:
    if line.startswith("---"):
        break
    cells = line.rstrip(",").split(",")
    if len(cells) < len(header) or cells[0] != "0":
        continue
    rows.append([int(c) for c in cells])


def ms(row, a, b):
    return (row[col[b]] - row[col[a]]) / 1e6


stages = {
    "ui (vsync→sync queued)": ("Vsync", "SyncQueued"),
    "sync+upload": ("SyncStart", "IssueDrawCommandsStart"),
    "render thread issue": ("IssueDrawCommandsStart", "SwapBuffers"),
    "gpu (issue→gpu done)": ("IssueDrawCommandsStart", "GpuCompleted"),
    "total (intended vsync→complete)": ("IntendedVsync", "FrameCompleted"),
}


def pct(values, p):
    values = sorted(values)
    return values[min(len(values) - 1, int(len(values) * p))]


print(f"frames={len(rows)}")
for name, (a, b) in stages.items():
    values = [ms(r, a, b) for r in rows if r[col[b]] > 0 and r[col[a]] > 0]
    if values:
        print(f"  {name:34s} p50={statistics.median(values):6.1f} p90={pct(values, .9):6.1f} p99={pct(values, .99):6.1f} ms")
