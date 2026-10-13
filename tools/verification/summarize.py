#!/usr/bin/env python3
"""Merges the .jsonl files written by capture.sh into one CSV and prints a per-screen table.

Usage: summarize.py <out.csv> <dir> [<dir> ...]   (later directories replace earlier ones for the same capture)
"""
import csv
import glob
import json
import os
import sys
from collections import defaultdict


def main():
    out, dirs = sys.argv[1], sys.argv[2:]
    captures = {}
    for directory in dirs:
        for path in sorted(glob.glob(os.path.join(directory, "*.jsonl"))):
            captures[os.path.basename(path)[:-6]] = path
    rows = []
    for name, path in sorted(captures.items()):
        _, screen, mode, theme = name.split("-")
        for line in open(path):
            r = json.loads(line)
            rows.append({
                "screen": screen, "mode": mode, "theme": theme, "kind": r["kind"], "label": r["label"],
                "bounds": "x".join(str(v) for v in r["bounds"]), "foreground": r["foreground"], "background": r["background"],
                "ratio_p5": r["ratio_p5"], "ratio_median": r["ratio_median"], "threshold": r["threshold"], "pass": r["pass"],
            })
    with open(out, "w", newline="") as f:
        writer = csv.DictWriter(f, fieldnames=list(rows[0].keys()))
        writer.writeheader()
        writer.writerows(rows)
    table = defaultdict(lambda: [0, 0, 99.0])
    for r in rows:
        cell = table[(r["screen"], r["mode"], r["theme"])]
        cell[0] += 1
        cell[1] += 0 if r["pass"] else 1
        cell[2] = min(cell[2], r["ratio_p5"])
    print(len(rows), "measurements,", sum(not r["pass"] for r in rows), "below threshold")
    print("screen | mode | theme | measured | below | lowest p5")
    for (screen, mode, theme), (n, bad, low) in sorted(table.items()):
        print(f"{screen} | {mode} | {theme} | {n} | {bad} | {low}")


if __name__ == "__main__":
    main()
