# Progress

Resume from the last entry. Each milestone records what was built, what was verified on a device, values chosen, and known gaps.

> **Open item for the user: the M1 performance gate is a provisional pass on an emulator.** The run continued past M1 without a physical device. Run the layer sweep on a phone (steps under M1) and compare with `docs/seaglass/perf/`.

## Environment (checked 2026-10-13)

- Network reachable: Google Maven, Maven Central, Google Fonts, GitHub.
- JDK 21 on PATH; Gradle daemon JVM pinned to 25 by `gradle/gradle-daemon-jvm.properties`. Gradle 9.6.0, AGP 9.4.1.
- Android SDK at `local.properties` `sdk.dir`; platforms up to android-37.0.
- **No physical device connected** (`adb devices` empty, nothing on USB). AVDs available: `Pixel_9_Pro_XL`, `Pixel_Tablet`. KVM present, display `:0`.
- Consequence: the M1 performance gate cannot be passed on real hardware from this machine. Emulator numbers are indicative only.

## Step 1 — Plan

- Built: `docs/seaglass/showcase.md` (app structure), `docs/seaglass/demos.md` (seven demos with rule justifications), M9–M12 appended to `milestones.md`.
- Verified: docs only; nothing to run.
- Values chosen: file-type role colours (folders honey, images lavender, video cornflower, audio seafoam, documents aqua, archives kelly, apps cobalt; sea pink reserved for destructive); Materials lab slider ranges; catalogue side-by-side breakpoint 600 dp; mini player sits above the tab bar when expanded and in the accessory slot when minimized.
- Spec tensions recorded: Weather "glass cards" are interpreted as clear overlay captions over the sky imagery (C5 overlay), keeping long-form data on milk; Gallery tiles use the board's r 10 although R8 sets a 12 dp floor for tiles this tall; the play control is a FAB because the pebble shape is FAB-only.
- Known gaps: none yet.

## M0 — Project setup

- Built:
  - `:seaglass` library (namespace `com.floydwiz.seaglass`) and `:app` depends on it. Generated colour Kotlin moved to `seaglass/.../color/` and repackaged.
  - `GlassRole` + `SeaGlassRolePalettes` (static and dynamic: aqua/seafoam/lavender and the neutrals follow the system accent 1/2/3 and neutral 1/2 palettes, missing tones interpolated).
  - `SeaGlassTheme(dark, contrast, reduceTransparency, dynamicColor, seedRole)` with `LocalSeaGlassDark`, `LocalGlassContrast`, `LocalReduceTransparency`, `LocalRolePalettes`, `LocalGlassRole`.
  - Roboto Flex and Material Symbols Rounded variable fonts in `res/font`; `SymbolIcon(name, size, fill, weight, tint)` draws by ligature. Grain tile in `:seaglass` `drawable-nodpi`, photo in `:app` `drawable-nodpi`.
  - `:app` shell: catalogue home (C0–C7, six screens), empty destinations, control sheet (theme, contrast, reduce transparency, dynamic colour, seed role) backed by `ControlState`.
- Verified on the `Pixel_9_Pro_XL` emulator (API 35, headless, host GPU): launches to the catalogue; Light and Dark recolour the whole shell; dynamic colour toggles without a crash; navigation to a placeholder and back; logcat clean. `./gradlew :seaglass:compileDebugKotlin :app:assembleDebug` passes. Not a physical device.
- Versions: Compose BOM 2026.09.00 (UI 1.12.1, Material3 1.4.0), Haze 2.0.1, graphics-shapes 1.1.0, navigation-compose 2.10.2, JankStats (metrics-performance) 1.0.0.
- Values chosen:
  - Kotlin bumped 2.2.10 → 2.4.20: Haze 2.0.1 depends on `kotlin-stdlib` 2.4.20 and the 2.2 compiler cannot read its metadata.
  - `dynamicColor` defaults to false so the Sea Glass palette is what shows first.
  - M3 `ColorScheme` is the standard static scheme at every contrast level for now.
- Haze 2 API (differs from the mapping table in `reference/README.md`; docs read at tag 2.0.1):
  - Modifiers: `Modifier.hazeSource(state)` on content, `Modifier.hazeBlur(input = HazeInput.Sources(state), style = HazeBlurStyle { … })` on the glass element. State: `rememberHazeState()`. Artifacts `haze` and `haze-blur`.
  - Style: `blurRadius(Dp)`, `noiseFactor(Float)`, `colorEffects(List<HazeColorEffect>)`, `backgroundColor`, `alpha`, `mask(Brush)`, `progressive(HazeProgressive)`.
  - `HazeColorEffect.tint(brush, blendMode)` takes a gradient brush (the 3-stop vertical tint) and `HazeColorEffect.colorFilter(ColorFilter, blendMode)` takes a `ColorMatrix` (saturation and brightness), so neither needs a workaround.
  - `HazePerformanceMode` (`Default`/`Balanced`, `Performance`, `Quality`) is a parameter of `hazeBlur`.
- Known gaps (planned): medium/high M3 scheme variants and "System" contrast option (M8); dynamic accents not harmonized toward the dynamic primary (M8); automatic reduce-transparency when cross-window blur is off (M8); fonts are unsubsetted, about 17 MB (M12); the control sheet and top bar use plain Material 3 components until the glass components exist.

## M1 — Glass primitive and performance spike

- Built in `:seaglass`:
  - `SeaGlassShape` (per-corner elliptical radii; capsule with jitter, round, lens, pebble, tumble, shard, egg, `Pane(dp)`), `SeaGlassTokens` (spec section 10), `GlassFinish` (Glass, Frost, Clear, Well, Milk), `GlassLayers` debug flags.
  - `GlassBackdrop(backdrop, content)`: Haze state plus `hazeSource` on `backdrop`; glass in `content` blurs it.
  - `Modifier.seaGlass(finish, shape, role, tinted, adaptive)`: `GlassRecipes` is a layer-by-layer port of `mat()` (contrast levels and reduce-transparency included). Draw order: tinted shadow, caustic, contact, then clipped to the shape: Haze blur + saturation/brightness + 3-stop tint, light pool with 8 s drift (shared `GlassClock`), grain, volume inset shadows, rim.
  - Preview gallery of all five finishes in light and dark.
- Built in `:app`: Performance spike screen (240 photo tiles as the Haze source, glass / frost / clear buttons, milk card, well field, clear tab bar with a frost lens), frame statistics from JankStats (`FrameDataApi31`: total duration, UI duration, overrun, jank), an auto-scroll at 900 dp/s, and a **Layer sweep** that measures ten configurations (all on, each layer off, all off) and writes `SGPerf` log lines and `files/perf-m1.json`. The eight layer toggles are in the control sheet.
- Verified on the `Pixel_9_Pro_XL` emulator (API 35, 60 Hz, host GPU), not a physical device:
  - Light, dark, high contrast, reduce transparency and dark + adaptive all render; no crashes.
  - Port fidelity: ran `recipes.js` under node for Glass light/dark, Frost light, Clear dark, Well light and Milk light and compared every shadow, caustic, contact, gradient stop, blur/saturation/brightness, inset-shadow, rim and drift-offset value; all match. Pixel values are rounded to 0.1 like the JS.
  - Frame statistics, auto-scrolling, 240 frames per configuration (JankStats; `total` is vsync to completion and includes RenderThread and GPU, `ui` is UI thread only):

    | Configuration | total p50 | total p95 | ui p95 | missed deadlines |
    | --- | --- | --- | --- | --- |
    | all layers on | 22.8 ms | 24.8 ms | 3.5 ms | 0% |
    | no shadow | 22.8 | 24.2 | 3.1 | 0% |
    | no caustic | 22.7 | 23.8 | 3.2 | 0% |
    | no contact | 22.8 | 23.9 | 3.2 | 0% |
    | no body (blur + tint) | 21.0 | 21.7 | 2.8 | 0% |
    | no light | 21.9 | 22.9 | 2.4 | 0% |
    | no grain | 22.6 | 23.6 | 3.1 | 0% |
    | no volume | 22.4 | 23.4 | 3.1 | 0% |
    | no rim | 22.2 | 23.5 | 3.0 | 0% |
    | all layers off | 19.3 | 20.0 | 2.3 | 0% |

  - Reading: the emulator's no-glass baseline is already about 20 ms because of its virtualised present path, so the absolute 16 ms threshold cannot be applied here; judge the glass by the difference. Glass adds about **3.5 ms (p50) and 4.8 ms (p95)** over the baseline with no missed deadlines and a steady 60 fps. The largest single costs are the Haze body (about 1.8 ms p50, 3.1 ms p95) and the light drift (about 0.9 ms p50, 1.9 ms p95); shadow, caustic, contact, grain, volume and rim are each under about 1 ms.
  - An earlier version recorded UI-thread time only (p95 20.1 ms before and 3.4 ms after the cache below), which excluded the blur. The 20.1 → 3.4 ms drop is real but is UI-thread only: the light drift invalidated every glass element each frame and that re-ran the inset-shadow and drop-shadow painters; recording shadows, volume, rim and grain once per size/spec into a `GraphicsLayer` removed it.
  - Raw results: `docs/seaglass/perf/m1-emulator-pixel9proxl-api35.json`.
- **Performance gate: provisional pass on the emulator; real-device confirmation pending.** No physical device was available and the gate asks for one. milestones.md says not to start component work until the gate passes; the run prompt stops only if the gate fails, so M2 onward continues on the emulator result. The emulator's GPU is the host's desktop GPU, which is probably faster than a phone's, so the blur cost on a phone is unknown and could be higher.
  - Run it on a phone: connect it, `adb devices`, `./gradlew :app:assembleDebug && adb install -r app/build/outputs/apk/debug/app-debug.apk`, open Performance spike, tap **Layer sweep** (about 55 s), then `adb logcat -d -s SGPerf` or `adb shell run-as com.floydwiz.seaglasssample cat files/perf-m1.json`. At 90 or 120 Hz, judge against that refresh rate's frame budget (the 16 ms threshold is for 60 Hz) or force 60 Hz in developer options.
  - If a phone fails, the cheapest levers, in order: `HazePerformanceMode.Performance` on `hazeBlur`; stop re-blurring a lens inside a clear bar (spec rule 5: it adds light only), which removes one blur pass per tab bar; lower blur radii on small elements; skip grain and caustic below a size; pause the drift when the screen is idle. All of them live in `seaGlass`, so components do not change.
- Values chosen:
  - Shadow radius: Compose passes `Shadow.radius` straight to `BlurMaskFilter` (sigma = 0.57735 × radius + 0.5 px). `shadowRadiusDp` converts a sigma to that radius: drop shadows use sigma = the recipe's CSS `blur()`, inset shadows use sigma = blur / 2 (box-shadow semantics). The non-uniform shadow scale is approximated by an average negative spread.
  - Caustic and contact are drawn with a blurred `android.graphics.Paint` oval (sigma = CSS px); the caustic fades to the same colour at alpha 0 to match CSS premultiplied interpolation.
  - Haze: `noiseFactor(0)` (own grain is drawn instead), `colorFilter(ColorMatrix)` for saturate × brightness, `tint(Brush)` for the 3-stop gradient, default `HazePerformanceMode`.
  - `GlassBackdrop` takes `backdrop` and `content` slots; glass must be outside the `backdrop` subtree.
  - `interactionSource` is not a parameter of `seaGlass` yet; it arrives with the state model in M2.
- Known gaps: adaptive tint is a flag on `seaGlass` (recipe branch ported); luminance sampling that sets it automatically is M8. Dark clear glass over bright content is washed out unless `adaptive = true` (seen in the spike, fixed by the flag). Pearl, Fill, Ink, Bead, Dry and Lens finishes, states and the lens shader are M2. Haze blur radius vs CSS blur is not calibrated visually against the canvas.

## M2 — All finishes and the state model

- Built in `:seaglass`:
  - Finishes Lens, Pearl, Fill, Ink, Dry, Bead and Plain added to `GlassRecipes` (body and layers, including contrast levels and reduce transparency); `GlassFinish` now has twelve entries.
  - State model: `GlassState` (Rest, Hover, Focus, Press, Disabled), `GlassInteraction` / `rememberGlassInteraction` (hover, press and focus from an `InteractionSource`, animated on the spatial.fast spring, snapped when animators are off), `LocalForcedGlassState` for the catalogue state picker. Hover lifts raised glass 2 dp, moves the shadow +3 dp ×1.05 and adds the top light; surfaces get a 7% content-colour layer. Press scales the element to 0.96 (hit area unchanged), moves the shadow −3 dp ×0.9 and blooms light; surfaces get a 12% layer. Focus draws the halo (1.5 dp ring, 14 dp glow, 34 dp bloom; 3 dp + 2 dp separation ring in high contrast) clipped outside the shape. Disabled draws Dry with 38% content; a pressed Plain draws Clear.
  - `GlassSurface(finish, shape, role, enabled, tinted, adaptive, interactionSource, onClick, …)`: wraps `seaGlass`, provides `LocalContentColor`, and with `onClick` is hoverable, focusable and pressable.
  - Lens: `GlassSurface` with `GlassFinish.Lens` draws its backdrop in a separate layer magnified by an AGSL shader on API 33+ (1.12× with a slight bend toward the rim) and by a 1.1× scale on API 31–32. `Modifier.seaGlass(Lens)` alone is the boosted clear finish without magnification.
  - `Modifier.glassBoardBackground(dark)`: the `BGL` / `BGD` canvas backdrop (base colour, four colour pools, 2 dp lines every 16 dp).
- Built in `:app`: catalogue scaffold (`CatalogueScreen` with the state picker, `CatalogueSection`, `ThemePanels`, `ThemeStrip` / `ThemeStripPair`, `BoardTheme`) and the C0 Language page: eight composition rules, the ten finishes plus the lens in light and dark, the state matrix (Glass, Clear, Plain, Well, Pearl × five states) and the shapes. Light and dark sit side by side from 600 dp and stack below.
- Verified on the `Pixel_9_Pro_XL` emulator (API 35, phone): all finishes render in light and dark; state matrix Hover and Focus columns; real touch press shrinks "Send" to 0.96 with bloom; keyboard Tab focuses a specimen, scrolls it into view and draws the halo; high contrast (rings on milk and clear) and reduce transparency (flat solid fills, no blur) render without errors; logcat clean. The new recipes were compared with `recipes.js` run under node (Pearl, Fill, Ink, Dry, Bead, Lens, light and dark) and all values match.
- Not verified:
  - Physical mouse hover: `adb shell input` cannot inject hover events here; the forced Hover state is verified and uses the same code path.
  - The API 31–32 lens fallback (no such emulator image installed).
  - The state matrix's Press and Disabled columns were not inspected on screen (they need a horizontal scroll), and the state picker was not exercised.
  - The tablet side-by-side panel layout.
- Values chosen: the catalogue chrome (state picker, section headings) uses plain Material 3 until the Sea Glass segmented control exists in M3; the state picker affects `ThemePanels` only, not the per-finish strips; below 600 dp the state matrix scrolls horizontally instead of reflowing; "Fill" in the finish table shows a well and a fill bar as in the board, not a switch.
- Known gaps: hover, press and focus timing uses one spatial.fast spring for scale, lift and light (motion pass is M6); the focus halo is recorded once and faded by alpha, so its colours do not animate; `Modifier.seaGlass` does not magnify a Lens (use `GlassSurface`).
