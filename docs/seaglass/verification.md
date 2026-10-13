# Verification (M8 to M12)

What was measured, how, and what failed. Everything here ran on emulators; see "Not verified" at the end.

## Final state (M12)

- Coverage: 25 routes in 4 modes × 2 themes (Home, the eight catalogue pages, the Motion, Materials and Accessibility labs, the six screens, the seven demos), 192 captures on the phone frame (and the tablet screen on the `Pixel_Tablet` AVD): **5424 measurements, 100 below threshold** (`verification/measurements.csv`; later captures replace earlier ones of the same route, mode and theme; catalogue and lab pages are measured on their first viewport only).
- The 100 break down as follows. None is a failure observed on screen that was left unexplained.
  - 40 measurement artifacts on narrow glyphs inside small beads (the count beads of the Smart home scope bar and the tablet rail, the "T" avatar): the bead's white gloss highlight falls inside the glyph's box, so the 5th percentile reads about 1:1 while the median reads 5.5 to 6.7.
  - 36 on elements cut off by their container or the screen edge, so the node's box includes pixels from outside the element: the last Inbox rows in the Accessibility lab frames (12), the "Press keeps the outline" line at the bottom of C0 (8), and the ring around the small clear lens in a field (8); and 8 for the "Disabled" row of C1, which is dry glass at 38% content by design (disabled controls are exempt from the contrast rule).
  - 16 real but accepted or marginal findings: the Weather overlay card and hourly labels in dark Standard (3.5 to 3.8 against 4.5, the top-left specular pool of dark clear glass; Medium, High and Reduced pass), the Music mini player's title where a bright cover passes under its left end (2.7 and 4.3, dark Standard; 3.8 in Medium), the "Email" label above a field in Medium and Reduced (4.3 to 4.4), and the "Glass" label on the Materials lab specimen in dark Standard (4.0).
- Every other measured element passes: text 4.5:1 (3:1 for large text), icons 3:1, at the 5th percentile of the pixels behind it.
- Re-run after the final changes: the layer sweep and the macrobenchmark (below). The M1 gate on a real device is still open.

## Setup

- Phone frames: the `Pixel_9_Pro_XL` AVD (API 35) with the display set to 412×915 dp (`wm size 1236x2745`, 480 dpi) for the first captures, and the `Pixel_Tablet` AVD (API 35, 2560×1600 at 320 dpi, which is 1280×800 dp) for the tablet screen, the benchmarks and the re-captures of the call and library screens (phone frame set the same way). The phone AVD ran out of storage during the benchmark, so the rest moved to the tablet AVD.
- Launch extras make every capture repeatable (documented in `MainActivity`): `am start -n com.floydwiz.seaglasssample/.MainActivity --es route screens/library --es theme Dark --es contrast High --ez rt true`. `tools/verification/capture.sh` runs the matrix and `contrast.py` measures each capture; `summarize.py` merges the results into [`verification/measurements.csv`](verification/measurements.csv).
- Matrix: 6 screens (In call, Photo library, Inbox, Assistant, Settings, Tablet mail) × 4 modes (Standard, Medium contrast, High contrast, Reduce transparency) × 2 themes (light, dark) = 48 captures. Contact sheets for each screen and theme are in `verification/` (`<screen>-<theme>.jpg`, modes in the order Standard, Medium, High, Reduce transparency).

## Method

- `adb exec-out screencap` for the pixels and `uiautomator dump` for the elements. Every text node is measured; every glyph-only symbol (text node whose text is a Material Symbols ligature) and every labelled control is measured as an icon.
- Text is measured against the pixels in strips just to the left and right of its box; icons against the pixels in a ring around the glyph inside the control. The foreground is the most extreme few pixels of the glyph. The reported number is the 5th percentile of the per-pixel contrast ratios (WCAG relative luminance), as the spec defines it.
- Thresholds: 4.5:1 for text, 3:1 for icons and for text whose rendered ink is as tall as 18 sp text.
- Calibration: on the library screen the title in the standard modes reads 16.37:1 (light) and 14.36:1 (dark), the accent text "See all" reads 6.12:1 and 10.90:1; the token colours give exactly those values. The search placeholder reads 8.9:1 (light) and 10.9:1 (dark) in Standard, 16.4:1 and 14.4:1 in High contrast, 7.2:1 and 7.3:1 with reduced transparency.
- The method needed three corrections before it was trusted, all in the script and not in the app: the first version measured a wide text box against pixels above and below it (outside the glass), took a single average of the glyph (too low for thin text), and read the box height as the font size (which let an 11 sp tab label count as large text). Those showed up as failures that were not defects and one real defect that was hiding behind the wrong threshold.

## Results

1296 measurements (1096 text, 200 icons): 3 below threshold.

| | Lowest 5th-percentile ratio | Median |
| --- | --- | --- |
| Text | 1.72 (see below) | 11.26 |
| Icons | 3.03 | 10.68 |

Per screen the lowest values are: Settings 6.45, Library 4.15, Assistant 3.76, In call 3.17, Tablet mail 3.03 (all of them icons on dark glass in the standard or medium modes) and Inbox 1.72 (the failure below). The full table per screen, mode and theme is in the CSV (`screen, mode, theme, kind, label, bounds, ratios, threshold, pass`).

### Failures

- **Inbox, dark, standard / medium / high contrast: preview text "Look at this one, perfectly frosted…" (1.76, 1.72 and 2.15 against 3:1; median 10.7).** Not fixed, explained: in the unscrolled list the last rows run under the floating Compose button, and the measured strips fall on its glow. That is content passing under floating glass, the intended behaviour; it disappears as soon as the list scrolls (there is bottom padding for the button) and does not occur with reduced transparency, where the button is opaque.
- Fixed during the work:
  - **In call, dark standard**: lens symbols (2.6 and 2.7 against 3:1) and the chip text (2.8 against 4.5). The screen's scrim and photo dimming were too light for dark clear glass, which lifts its backdrop by 1.55×. The scrim is now 72% / 42% / 70% of the surface colour (top, middle, bottom) and the photo is dimmed to 0.4 in dark, and adaptive tint is on for the controls (see below).
  - **Library, dark, standard / medium / high**: the selected tab label "Library" (3.8, 3.9 and 4.2 against 4.5). Adaptive tint now measures luminance after the finish's own brightness lift, which makes the tab lens and bar take the protective tint over the photos (now 4.9).
  - Two measurement artifacts, listed in Method.

## Re-run after M10

While building the demos, glass placed inside the `backdrop` slot of `GlassBackdrop` (list content with milk rows, filled sliders and bubbles) turned out to lose its backdrop body, because Haze lets an effect inside a source blur only sources below it. Milk rows were transparent and liquid fills pale. `GlassBackdrop` now has a `layer` slot (a second source above the backdrop) and the Inbox, Assistant and Library screens moved their lists into it. Re-running the matrix for those three screens plus the Gallery, Todo and File manager demos (6 screens × 4 modes × 2 themes = 48 captures, 1160 measurements, phone frame, tablet AVD): **0 below threshold**. The Inbox preview line that failed under the Compose button's glow (above) no longer fails, because the button's glow is now drawn by a glass with a body. The first-pass numbers above, and `measurements.csv`, are from before this change; the screens that did not move (In call, Settings, Tablet mail) are unaffected.

## Demos (M10 and M11)

The seven demos were captured with the same matrix (7 demos × 4 modes × 2 themes, phone frame on the tablet AVD; 1160 measurements for Gallery, Todo and File manager together with the Inbox, Assistant and Library re-run, and 944 for Music, Weather, Smart home and Notes). Findings:

- Fixed: liquid bar labels that fell on the well (1.4:1); the floating controls lens on bright photos in dark (now an ink lens); the Weather sky on dark themes (it was a bright sky behind light text; it is now mixed 92% into the surface); the Music mini player over bright cover thumbnails (adaptive tint now counts the finish's white fill and reads the 95th percentile); the Notes editor's share lens hidden behind the controls lens.
- Measurement script changes while doing this (calibration still reproduces the token ratios): text boxes are tightened to their ink before the side strips are taken (a list row's title node is wider than its text and its strips fell on neighbouring text), nodes narrower than 12 px are skipped, and narrow glyphs use a 4 px ring.
- Remaining, standard and medium modes only, dark theme:
  - **Weather** (standard dark): the place name and the hourly labels on the clear overlay card and items measure 3.5 to 3.8 at the 5th percentile (median 4.7 to 5.3) against 4.5. The text sits in the top-left specular pool of the clear glass; High contrast, Reduced and Medium pass. Not fixed: it is the dark clear recipe over a saturated sky, as on the board.
  - **Music** (standard and medium dark): the mini player's title measures 2.7 and 3.8 where a bright cover thumbnail passes under its left end; the artist line 4.3. The tint is now partly engaged (it was not) but does not reach full strength for a small bright patch.
  - **Smart home**: the single-digit count beads on the scope bar read 1.0 to 1.4 at the 5th percentile in every mode, a measurement limit: the bead's white gloss highlight is inside the digit's box, and the median is 5.5 to 6.7. Not a defect that was observed on screen.

## Wiring checked live

- **System contrast**: `adb shell settings put secure contrast_level 1.0` while the app was running switched the library to High contrast (outlined bar, darker text) without a restart, and `0.0` switched it back. Below 0.25 is Standard, below 0.75 Medium and above that High.
- **Reduce transparency when blur is off**: `adb shell wm disable-blur` fails for the shell user on this image, so `adb shell settings put global disable_window_blurs 1` was used; `dumpsys window` then reports `mBlurEnabled=false` and the running app swapped to solid fills with outlines, and back on `0`. "Reduce when system blur is off" is its own control-sheet switch (on by default), and the in-app "Reduce transparency" switch and the `rt` extra always win. The emulator reports blur as enabled, so the automatic mode does not alter normal captures.
- **Colour scheme**: `SeaGlassTheme` now applies the contrast level to the Material colour scheme as well as to the glass recipes: High moves the accent to tone 30 / 90, makes secondary text primary text and strengthens outlines; Medium strengthens the outline.
- **Adaptive tint**: `GlassBackdrop` records a 1/8-size copy of its backdrop only while an adaptive surface is registered (surfaces inside `LocalAdaptiveTint`, or `adaptive = true`), reads it back at most every 300 ms and only after the backdrop redrew, and each surface reads the 75th-percentile luminance of its own bounds. The tint fades in from 0.4 to 0.55 (in steps of 0.25, smoothed over 400 ms so scrolling does not make it flicker) and uses the recipe's adaptive numbers (minimum tint 0.3, brightness lift 1.25 / 1.15 down to 0.9, 60% smoke on dark clear glass). On the library screen the dark tab bar and lenses now adapt by themselves over the bright photos and stay at zero over dark content (`verification/adaptive-tint-library.png`: before the first sample, then after it).
- **Audit rules**: placeholders use the full-strength secondary text colour (measured above); symbols on photos sit in an ink bead (the heart and duration chip on library tiles); the light-theme liquid is the tone 40 to 60 mix of the recipe (about T45) with white content; wells keep their 1.5 dp lip in every mode. No change was needed.

## Performance

- Layer sweep after the sampler and contrast changes (tablet AVD, phone-sized display, 1344×2992): glass costs +3.7 ms p50 and +4.4 ms p95 over the no-glass baseline (M3: +3.9 and +4.7), 0% missed deadlines. The first row ("all layers") of that run read 16.8 ms in every percentile, an artifact of the first measurement window, so the comparison uses the "no shadow" row that differs from it by one layer.
- Macrobenchmark `:benchmark` (`LibraryScrollBenchmark`, dark theme, 3 iterations, `FrameTimingMetric`, the `benchmark` build type with R8 off and `suppressErrors=EMULATOR`), phone-sized display on the tablet AVD. The result files are in `verification/`:

  | | Frames | Frame CPU p50 / p90 / p95 / p99 (ms) | Overrun p50 / p95 (ms) |
  | --- | --- | --- | --- |
  | Glass on | 1077 | 17.9 / 18.9 / 32.5 / 33.0 | +1.5 / +18.1 |
  | Reduced transparency | 156 | 3.5 / 5.7 / 7.0 / 8.3 | −10.6 / −7.3 |

  The glass run renders 1077 frames over the same gestures because the light drift redraws continuously; the reduced-transparency run only draws while scrolling. The emulator's GPU makes the absolute glass numbers poor evidence (its no-glass JankStats baseline is already about 19 to 20 ms per frame at 60 Hz, above the 16.7 ms budget), so they say how much more glass costs and not whether it holds 60 fps.
- The benchmark first failed with `ENOSPC` because the phone AVD's data partition was 98% full; the Perfetto trace of one iteration is about 400 MB. It was moved to the tablet AVD and the traces were deleted after each run.

## Final performance numbers (M12, emulator)

- Layer sweep, tablet AVD at 1344×2992: all layers off 19.7 ms p50 / 20.6 p95; glass with all layers about 24.2 p50 / 26.7 p95 (+4.5 and +6.1), 0% missed deadlines in every row. The first "all layers" row reads 16.8 ms again, a warm-up artifact. M1 to M8 read +3.5 to +3.9 p50 and +4.4 to +4.8 p95, so the cost of the full stack has grown slightly (the tinted glass inside lists now blurs for real, see "Re-run after M10").
- Macrobenchmark, library scroll in dark with glass, five runs of the shipped build (3 iterations each) and one with the adaptive sampler off: frame CPU p50 between 18.0 and 32.4 ms, p90 between 34.2 and 34.9, p95 between 34.7 and 49.2, p99 between 54 and 240 (the sampler-off run read 18.2 / 33.3 / 34.8 / 136). Reduced transparency: p50 3.7, p90 6.0, p95 7.0, p99 8.2. The M8 run of the same test read 17.9 / 18.9 / 32.5 / 33.0 with glass. The emulator's frames land either on one vsync (about 17 ms) or two (about 33 ms), so the median flips between runs; the tail moved up after glass inside lists started to blur, and turning the sampler off did not change it, so the sampler is not the cause. The cause was not isolated further, and it cannot be judged on an emulator whose no-glass baseline is already above 16.7 ms.
- Lever, if a physical phone shows it: `HazePerformanceMode.Performance`, drop the blur of glass inside lists (milk rows are 86 to 90% opaque, their blur is barely visible), or skip it while scrolling.

## Not verified

- A physical device. The M1 performance gate (JankStats p95 below 16 ms at 60 Hz on a real phone) is still open: on the emulator even the no-glass baseline is over 16 ms, so it can neither pass nor fail there. If a phone fails it, the levers listed in `progress.md` apply.
- Contrast measured on the other catalogue pages, on screen states other than each screen's default (library selection, voice mode, open menus and sheets, keyboard open) and with dynamic colour on; the matrix covers the six screens in their first state.
- Contrast of focus rings and state indicators (the exception noted in the spec, the off switch's pearl at 1.4:1 in standard light, was not measured because switch knobs have no accessibility node); the High contrast focus ring thickness is checked by eye on the buttons page only.
- TalkBack, switch access and keyboard traversal.
- API 31 to 34 behaviour (contrast listener needs API 34; below it the level stays Standard by design).
- The spec prose for adaptive tint (white at 25% in light themes) is broader than the reference recipes, which only change dark finishes; the recipes were kept and light themes have no adaptive effect.
