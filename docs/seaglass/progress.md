# Progress

Resume from the last entry. Each milestone records what was built, what was verified on a device, values chosen, and known gaps.

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
- Built in `:app`: Performance spike screen (240 photo tiles as the Haze source, glass / frost / clear buttons, milk card, well field, clear tab bar with a frost lens), frame statistics from JankStats (UI frame durations, jank flag) plus `FrameMetrics.GPU_DURATION`, an auto-scroll at 900 dp/s, and a **Layer sweep** that measures ten configurations (all on, each layer off, all off) and writes `SGPerf` log lines and `files/perf-m1.json`. The eight layer toggles are in the control sheet.
- Verified on the `Pixel_9_Pro_XL` emulator (API 35, 60 Hz, host GPU), not a physical device:
  - Light, dark, high contrast, reduce transparency and dark + adaptive all render; no crashes.
  - Port fidelity: ran `recipes.js` under node for Glass light/dark, Frost light, Clear dark, Well light and Milk light and compared every shadow, caustic, contact, gradient stop, blur/saturation/brightness, inset-shadow, rim and drift-offset value; all match. Pixel values are rounded to 0.1 like the JS.
  - Frame statistics, all layers on, auto-scrolling (JankStats UI frame duration):

    | Build | p50 | p95 | p99 | jank |
    | --- | --- | --- | --- | --- |
    | First version | 13.1 ms | 20.1 ms | 23.7 ms | 0% |
    | Static layers cached in a `GraphicsLayer` | 2.3 ms | 3.4 ms | 4.0 ms | 0% |

  - Cost by layer (p95, cached build): all on 3.4 · no shadow 3.3 · no caustic 3.2 · no contact 3.2 · no body (blur+tint) 2.8 · no light 2.4 · no grain 3.0 · no volume 3.1 · no rim 3.0 · all off 2.3. Light (drift redraw) and the Haze body are the largest, about 1 ms and 0.6 ms.
  - The first version was slow because the light drift invalidated every glass element each frame and that re-ran the inset-shadow and drop-shadow painters. Recording shadows, volume, rim and grain once per size/spec into a `GraphicsLayer` removed it.
  - Raw results: `docs/seaglass/perf/m1-emulator-pixel9proxl-api35.json`.
- **Performance gate: not passed, not failed.** No physical device was available, and the gate requires one. The `gpuP95` column is about 18 ms in every configuration including "all off", so on the emulator it measures the photo grid, not glass; the emulator cannot tell us the GPU cost of the blur. Run on a phone: connect it, `adb devices`, `./gradlew :app:assembleDebug && adb install -r app/build/outputs/apk/debug/app-debug.apk`, open Performance spike, tap **Layer sweep** (about 55 s), then `adb logcat -d -s SGPerf` or `adb shell run-as com.floydwiz.seaglasssample cat files/perf-m1.json`.
- Values chosen:
  - Drop shadow: Compose `Shadow.radius` is set to 2× the recipe's CSS `blur()` (assumes box-shadow semantics, sigma = radius / 2) and the non-uniform shadow scale is approximated by an average negative spread. Not calibrated against the canvas.
  - Caustic and contact are drawn with a blurred `android.graphics.Paint` oval (sigma = CSS px); the caustic fades to the same colour at alpha 0 to match CSS premultiplied interpolation.
  - Haze: `noiseFactor(0)` (own grain is drawn instead), `colorFilter(ColorMatrix)` for saturate × brightness, `tint(Brush)` for the 3-stop gradient, default `HazePerformanceMode`.
  - `GlassBackdrop` takes `backdrop` and `content` slots; glass must be outside the `backdrop` subtree.
  - `interactionSource` is not a parameter of `seaGlass` yet; it arrives with the state model in M2.
- Known gaps: adaptive tint is a flag on `seaGlass` (recipe branch ported); luminance sampling that sets it automatically is M8. Dark clear glass over bright content is washed out unless `adaptive = true` (seen in the spike, fixed by the flag). Pearl, Fill, Ink, Bead, Dry and Lens finishes, states and the lens shader are M2. Haze blur radius vs CSS blur is not calibrated visually. Clear/frost over very busy backdrops may need a lower `HazePerformanceMode` on weak GPUs; decide after the real-device run.
