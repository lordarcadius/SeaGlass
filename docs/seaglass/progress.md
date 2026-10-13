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
