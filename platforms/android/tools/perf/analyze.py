#!/usr/bin/env python3
"""Summarizes main-thread and RenderThread frame work from a Perfetto trace of the Sea Glass app."""
import sys
from perfetto.trace_processor import TraceProcessor

PKG = "com.floydwiz.seaglasssample"


def q(tp, sql):
    return list(tp.query(sql))


def percentile(values, p):
    values = sorted(values)
    return values[int((len(values) - 1) * p)] if values else 0.0


def main():
    path, name = sys.argv[1], sys.argv[2]
    tp = TraceProcessor(trace=path)
    upid = q(tp, f"select upid from process where name = '{PKG}' limit 1")
    if not upid:
        print(name, "no app process in trace")
        return
    upid = upid[0].upid
    threads = {r.utid: (r.name, r.tid) for r in q(tp, f"select utid, name, tid from thread where upid = {upid}")}
    main_utid = next((u for u, (n, t) in threads.items() if n == PKG[-15:] or t == q(tp, f"select pid from process where upid={upid}")[0].pid), None)
    rt_utid = next((u for u, (n, _) in threads.items() if n == "RenderThread"), None)

    def slices(utid, like):
        rows = q(tp, f"select s.name, s.dur from slice s join thread_track tt on s.track_id = tt.id where tt.utid = {utid} and s.name like '{like}'")
        return [r.dur / 1e6 for r in rows]

    def line(label, values):
        if not values:
            print(f"  {label:<34} none")
            return
        print(f"  {label:<34} n={len(values):<5} p50={percentile(values, .5):6.2f} p90={percentile(values, .9):6.2f} p99={percentile(values, .99):6.2f} max={max(values):6.2f} total={sum(values):8.1f} ms")

    print(f"== {name}")
    if main_utid is not None:
        line("main: Choreographer#doFrame", slices(main_utid, "Choreographer#doFrame%"))
        line("main: Compose:recompose", slices(main_utid, "Compose:recompose"))
        line("main: Compose:doFrame/Frame", slices(main_utid, "androidx.compose.ui.platform.AndroidComposeView:%"))
        line("main: draw / Record View#draw()", slices(main_utid, "Record View#draw()"))
        line("main: layout/measure (Compose)", slices(main_utid, "Compose:%layout%"))
    if rt_utid is not None:
        line("render: DrawFrame", slices(rt_utid, "DrawFrame%"))
        line("render: flush commands", slices(rt_utid, "flush commands"))
        line("render: eglSwapBuffers", slices(rt_utid, "eglSwapBuffers"))
        line("render: Texture upload", slices(rt_utid, "Texture upload%"))
    frames = q(tp, f"select count(*) c from actual_frame_timeline_slice where upid = {upid}")
    print(f"  frames produced (timeline): {frames[0].c}")
    nframes = max(1, len(slices(rt_utid, "DrawFrame%"))) if rt_utid is not None else 1
    names = {
        "render passes (OpsTask::onExecute)": "auto skgpu::ganesh::OpsTask::onExecute%",
        "FillRectOp": "FillRectOp", "FillRRectOp": "FillRRectOp", "TextureOp": "TextureOp", "path ops": "%PathOp",
        "text ops": "AtlasTextOp", "blur/shader (GrBlur*,RRect blur)": "%Blur%",
    }
    parts = []
    for label, like in names.items():
        count = q(tp, f"select count(*) c from slice s join thread_track tt on s.track_id=tt.id join thread t using(utid) where t.upid={upid} and s.name like '{like}'")[0].c
        parts.append(f"{label}={count / nframes:.1f}")
    print("  per frame: " + ", ".join(parts))


if __name__ == "__main__":
    main()
