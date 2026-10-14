# Performance pass

Why the app was slow, what changed, how it was measured, and what is still unverified.

## Test device

Primebook 3: MediaTek MT8189 (Mali-G57 MC2), Android 15, 1920×1080 at 180 dpi, **one display mode, 60 Hz**. It is a weak GPU, so it is a fair worst case for GPU cost, but it cannot show 90, 120, 144 or 165 Hz behaviour. Everything below is 60 Hz.

Builds are the `benchmark` variant (R8 on, baseline profile from the libraries) after `cmd package compile -m speed`, launched full screen. Numbers come from `dumpsys gfxinfo` frame statistics (`tools/perf/gfx.sh`, `stages.py`): the last 120 frames of each run, three runs per scenario. Run-to-run noise on this device is about ±3 ms of GPU time.

## What was wrong

1. **Every route rendered at 60 fps forever.** One shared clock drove the 8 s light drift and was read in every glass surface's draw, so every surface redrew each frame even when nothing moved. Idle frames over 5 s: about 310 on Home, Library, Catalogue and Demos.
2. **One blur per glass element.** Haze blurs the backdrop once per element, about 16 GPU render passes each (measured with Perfetto op counts: 2 passes with no glass, 18 with one, 66 with four, 136 with eight). The cost scaled with the number of glass elements on screen.
3. **Decoration caches rebuilt on every recomposition.** The `drawWithCache` modifiers for shadow, caustic, contact and the inset/rim layers were recreated with a new lambda on every recomposition, so Compose rebuilt their caches each time (677 rebuilds in six library swipes on the emulator). Glass inside scrolling lists recomposes every frame (adaptive tint reads its bounds), so each scroll frame re-recorded and, once the layers were offscreen, re-rasterised every surface's decoration.
4. **Blurred decoration replayed every frame.** Drop shadow, caustic, contact shadow, inset shadows and the rim use blur mask filters; they were recorded layers that HWUI replayed, blur included, each frame.

The reference repository (github.com/mweinbach/SeaGlass) was read in full and gave no technique this build lacked on the core path: it captures one full-resolution backdrop per stage, runs per-surface blur-mask draws uncached, redraws on an always-on tilt clock, and has no benchmarks. Its "C++" is one 79-line header; there is no native implementation or NDK build. It has no license, so nothing was copied.

## What changed

- **Light drift is opt-in** (`Animate light drift` in the controls, `drift` launch extra, off by default). The Motion lab card always animates. Idle frames are now 0 on every static route.
- **Shared backdrop blur** (`material/BackdropBlur.kt`, `GlassBackdrop.kt`). `GlassBackdrop` records its backdrop (and its optional `layer`) into GraphicsLayers. Each distinct blur radius and colour matrix is built once: sources drawn at 1/1 to 1/8 scale (about 4.5 px of blur at the reduced size), blurred by a `RenderEffect`, cached in an offscreen layer. Each glass element draws a crop with `drawLayer(translate, scale)`; its offset is tracked on each placement. Cost no longer depends on the number of elements. A variant lives only while an element holds its key, so the Materials lab blur slider frees the layers it leaves behind (an earlier cap of 10 variants froze the blur after a few slider steps). Glass inside `layer` still sees only the backdrop below it. Haze is removed.
- **Stable modifiers.** `seaGlass` remembers the frame, inner and blur modifiers keyed on their real inputs, so the caches rebuild only when something they depend on changes (155 rebuilds in the same six swipes, which are rows entering the list). Library UI-thread time per frame fell from about 6–7 ms to about 2.3 ms.
- **Cached decoration.** Shadow, caustic/contact and the static inset/rim/grain layers are `CompositingStrategy.Offscreen`, so their blurs run once. Shadow and oval layers are padded by their blur bleed because an offscreen layer clips what it draws outside its bounds.
- **Highest refresh rate requested** (`MainActivity`): `preferredDisplayModeId` set to the fastest mode at the current resolution. Not verifiable here (the test device has one mode).
- Tools: `tools/perf` (`gfx.sh`, `stages.py`, `trace.sh`, `analyze.py`, `shots.sh`, `diff.py`, `idle-frames.sh`), a `perf/stress` route (N glass surfaces over a photo, optional moving backdrop), launch extra `off` to disable glass layers.

## Results (Primebook, 60 Hz, 3 runs each)

"Before" is commit d52091d (Haze blur, uncached decoration, light drift on by default). "After" changes three things together: the blur, the decoration cache and the drift default, so the table does not attribute the gain to each. Single runs on the Buttons page before the decoration cache existed: turning the glass body off took GPU p50 from 68 to 24 ms, and turning decoration off took it from 68 to 32 ms.

| Scenario | Before (Haze) | After |
| --- | --- | --- |
| Buttons page scroll (`catalogue/c1`) | 51–65% janky frames, GPU p50 39–45 ms (p90 76), UI p50 18–20 ms | 4.4–4.6% janky, GPU p50 19.3–19.7 ms (p90 30–35), UI p50 1.4–1.5 ms |
| Photo library scroll (`screens/library`) | 5.1–6.3% janky, GPU p50 19.4–20.2 ms, UI p50 6.6–7.1 ms | 0.7–1.7% janky, GPU p50 14.3–15.4 ms (p90 22–25), UI p50 2.2–2.4 ms |
| Home, idle 5 s | 310–315 frames | 0 frames |
| 8 glass surfaces over a moving photo | GPU p50 25.6 ms (single run) | 14.4–14.6 ms |
| 16 glass surfaces over a moving photo | GPU p50 47.8 ms (single run) | 19.2 ms (single run) |

Fidelity, still images: 22 routes in light and dark were captured with the Haze build and the new build and compared pixel by pixel (`tools/perf/diff.py`). Mean absolute difference was under 1.7/255 on every screen except those where an unrelated system notification covered the window; the remaining differences are slow background animation.

Fidelity, motion (tablet emulator, new build; the top and bottom edge fades also compared with the Haze build): the photo library mid-scroll, the tab-bar lens mid-drag, a Todo row half-swiped, the Todo FAB menu veil, the Assistant's masked top and bottom edge diffusion, and the control sheet (a separate window, so its glass takes the no-backdrop path). No blur detached from its surface and nothing crashed. Not exercised: an alert dialog, and the edge fade over scrolled content (the Assistant chat was too short to scroll).

## What is left

- On this GPU the Buttons page still has a 90th percentile near 30–35 ms GPU and the library about 22–25 ms. The library p50 is under the 16.7 ms budget; the Buttons page is not, and a 120 Hz budget (8.3 ms) is out of reach for this chip. Floor with every glass layer off: about 13.5 ms on the Buttons page and 6.2 ms on the library, which is page content and not glass.
- Share of the remaining glass cost, measured on the current build (3 runs each, GPU p50 / p90): Buttons page all on 18.9–19.9 / 28–31 ms, decoration off 17.9–18.8 / 23–25, every glass layer off 13.5 / 15.5 (earlier build). Library all on 13.0–14.7 / 20.5–23, decoration off 12.4–12.6 / 15.4–15.9, every layer off 6.2 / 7. So decoration is about 1–1.5 ms at the median and about 5–6 ms at the 90th percentile, which is rows and specimens entering view and rasterising their offscreen layers. On the Buttons page the body or the decoration alone each recovered only about 0.5 ms of the roughly 5 ms that removing both recovers (earlier build): a fixed per-surface cost whose cause is not isolated. A trace of `catalogue/c1` checking path ops and passes against the stress route (0 path ops, 9 passes) would show whether non-rounded-rectangle clip outlines cause it; not done.
- Render passes with the current build (tablet emulator, moving backdrop): 2 with no glass, 9 with 8 surfaces, 9 with 16 (Haze: 66 with 4, 136 with 8). Each surface adds about 6.6 texture draws and 4 rounded-rect draws and no passes. On the Primebook the stress test costs about 0.5–0.6 ms of GPU per 150×56 dp surface, largely blending the padded shadow layers; tighter padding (2σ instead of 3σ) is the cheap lever.
- Merging each surface's decoration layers into one AGSL shader pass was evaluated and not done. It would remove the offscreen layers that cause the 5–6 ms tail above, but it means re-deriving the inset-shadow and rim maths analytically and gating it on API 33, with a fidelity risk, for a gain that is only visible in the tail on this GPU. It is the next lever if a flagship still misses its budget while scrolling lists of glass.
- Memory: GPU memory after scrolling is higher on pages with many separate backdrops: 147 MB against 76 MB on `catalogue/c5`, 122 against 68 MB on `catalogue/c4` (each specimen panel has its own backdrop and each surface has offscreen layers). The library, one backdrop, went from 64 to 55 MB.
- A shared low-resolution capture across blur variants was tried and reverted: no measurable change on the library.
- Baseline profile: `benchmark/.../BaselineProfileGenerator.kt` runs on a device (it took 24 minutes at the default iteration count; now capped at 3), but the profile it records from the R8 build has obfuscated names and cannot be used as is. To ship one, generate it on an unminified variant and put the result in `app/src/main/baseline-prof.txt`; AGP maps it to the minified names. Not done, so first launches after a sideload run through the JIT. The measurements above were taken after `cmd package compile -m speed`.

## Not verified

- Anything at 90, 120, 144 or 165 Hz, and any flagship GPU. The numbers above are 60 Hz on a mid-range tablet-class GPU.
- Whether the system grants the fastest mode (it can refuse in battery saver or a user refresh-rate setting).
- Windows other than the main one (dialogs, sheets, popups) do not blur, as before; an alert dialog was not exercised after the change.
- Cold-start and first-scroll behaviour without a baseline profile.
