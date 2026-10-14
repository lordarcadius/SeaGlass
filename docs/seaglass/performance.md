# Performance pass

Why the app was slow, what changed, how it was measured, and what is still unverified.

## Test device

Primebook 3: MediaTek MT8189 (Mali-G57 MC2), Android 15, 1920×1080 at 180 dpi, **one display mode, 60 Hz**. It is a weak GPU, so it is a fair worst case for GPU cost, but it cannot show 90, 120, 144 or 165 Hz behaviour. Everything below is 60 Hz.

Builds are the `benchmark` variant (R8 on, baseline profile from the libraries) after `cmd package compile -m speed`, launched full screen. Numbers come from `dumpsys gfxinfo` frame statistics (`tools/perf/gfx.sh`, `stages.py`): the last 120 frames of each run, three runs per scenario. Run-to-run noise on this device is about ±3 ms of GPU time.

## What was wrong

1. **Every route rendered at 60 fps forever.** One shared clock drove the 8 s light drift and was read in every glass surface's draw, so every surface redrew each frame even when nothing moved. Idle frames over 5 s: about 310 on Home, Library, Catalogue and Demos.
2. **One blur per glass element.** Haze blurs the backdrop once per element, about 16 GPU render passes each (measured with Perfetto op counts: 2 passes with no glass, 18 with one, 66 with four, 136 with eight). The cost scaled with the number of glass elements on screen.
3. **Blurred decoration replayed every frame.** Drop shadow, caustic, contact shadow, inset shadows and the rim use blur mask filters; they were recorded layers that HWUI replayed, blur included, each frame.

The reference repository (github.com/mweinbach/SeaGlass) was read in full and gave no technique this build lacked on the core path: it captures one full-resolution backdrop per stage, runs per-surface blur-mask draws uncached, redraws on an always-on tilt clock, and has no benchmarks. Its "C++" is one 79-line header; there is no native implementation or NDK build. It has no license, so nothing was copied.

## What changed

- **Light drift is opt-in** (`Animate light drift` in the controls, `drift` launch extra, off by default). The Motion lab card always animates. Idle frames are now 0 on every static route.
- **Shared backdrop blur** (`material/BackdropBlur.kt`, `GlassBackdrop.kt`). `GlassBackdrop` records its backdrop (and its optional `layer`) into GraphicsLayers. Each distinct blur radius and colour matrix is built once: sources drawn at 1/1 to 1/8 scale (about 4.5 px of blur at the reduced size), blurred by a `RenderEffect`, cached in an offscreen layer. Each glass element draws a crop with `drawLayer(translate, scale)`; its offset is tracked on each placement. Cost no longer depends on the number of elements. Glass inside `layer` still sees only the backdrop below it. Haze is removed.
- **Cached decoration.** Shadow, caustic/contact and the static inset/rim/grain layers are `CompositingStrategy.Offscreen`, so their blurs run once. Shadow and oval layers are padded by their blur bleed because an offscreen layer clips what it draws outside its bounds.
- **Highest refresh rate requested** (`MainActivity`): `preferredDisplayModeId` set to the fastest mode at the current resolution. Not verifiable here (the test device has one mode).
- Tools: `tools/perf` (`gfx.sh`, `stages.py`, `trace.sh`, `analyze.py`, `shots.sh`, `diff.py`, `idle-frames.sh`), a `perf/stress` route (N glass surfaces over a photo, optional moving backdrop), launch extra `off` to disable glass layers.

## Results (Primebook, 60 Hz, 3 runs each)

| Scenario | Before (Haze) | After |
| --- | --- | --- |
| Buttons page scroll (`catalogue/c1`) | 51–65% janky frames, GPU p50 39–45 ms (p90 76), UI p50 18–20 ms | 3.5–4.5% janky, GPU p50 18.8–19.1 ms (p90 27–31), UI p50 1.3 ms |
| Photo library scroll (`screens/library`) | 5.1–6.3% janky, GPU p50 19.4–20.2 ms | 1.1–2.0% janky, GPU p50 13.8–19.2 ms |
| Home, idle 5 s | 310–315 frames | 0 frames |
| 8 glass surfaces over a moving photo | GPU p50 25.6 ms (single run) | 14.4–14.6 ms |
| 16 glass surfaces over a moving photo | GPU p50 47.8 ms (single run) | 19.2 ms (single run) |

Fidelity: 22 routes in light and dark were captured with the Haze build and the new build and compared pixel by pixel (`tools/perf/diff.py`). Mean absolute difference was under 1.7/255 on every screen except those where an unrelated system notification covered the window; the remaining differences are slow background animation.

## What is left

- On this GPU the Buttons page still has a 90th percentile near 27–31 ms GPU, and the library about 22–26 ms. The p50 is under the 16.7 ms budget only on the library, and a 120 Hz budget (8.3 ms) is out of reach for this chip. Floor with every glass layer off: about 13 ms on the Buttons page and 6 ms on the library, which is page content and not glass.
- Each glass surface still draws about 3 offscreen layers; entering the viewport rasterises them (a one-time cost per surface) and a long list of glass rows pays it as rows appear. A single shader pass per surface is the next lever if that shows in a trace.
- A shared low-resolution capture across blur variants was tried and reverted: no measurable change on the library.

## Not verified

- Anything at 90, 120, 144 or 165 Hz, and any flagship GPU. The numbers above are 60 Hz on a mid-range tablet-class GPU.
- Whether the system grants the fastest mode (it can refuse in battery saver or a user refresh-rate setting).
- Memory: offscreen layers cost texture memory per surface and per variant (blurred layers are at most 1/1 to 1/8 of the backdrop; at most 10 variants per backdrop).
- Windows other than the main one (dialogs, sheets, popups) do not blur, as before.
