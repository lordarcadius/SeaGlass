# Sea Glass — Compose prototype

This project is a Jetpack Compose prototype of **Sea Glass**, a design language that extends Material 3 Expressive with a frosted, diffusing sea-glass material. The goal is a showcase app that demonstrates every component, material finish, state, animation and accessibility mode from the design canvas, running on a real device.

## Source of truth

Read these before writing code. Do not invent values that the docs already define.

| File | What it holds |
| --- | --- |
| `docs/seaglass/spec.md` | The full design spec: principles, colour, the eleven finishes, states, shapes, motion, every component, the screens, accessibility modes. |
| `docs/seaglass/reference/README.md` | How to read the reference code and how each CSS layer maps to a Compose API. **Read this before porting any recipe.** |
| `docs/seaglass/reference/recipes.js` | Exact recipe for every finish (`mat()`), every state (`W()`), the focus halo and the contrast modes. The numbers here win over prose. |
| `docs/seaglass/reference/boards/*.js` | Exact geometry of every component specimen (C0–C7) and every screen (412×915 phone, 1280×800 tablet). |
| `docs/seaglass/reference/animations.css` | Keyframes and durations for drift, morph, comet, bob and indeterminate progress. |
| `docs/seaglass/generated/*.kt` | HCT tonal palettes and static light/dark colour schemes. Move them into the design-system module and rename the package; never hand-edit colour values. |
| `docs/seaglass/assets/` | `grain.png` (noise tile, 200 dp repeat) and `seaglass_photo.jpg` (sample imagery for the showcase). |
| `docs/seaglass/milestones.md` | The build plan. Work one milestone at a time. |

If you need to see what a board looks like, ask me for a screenshot of that board from the canvas.

## Architecture

- `:seaglass` — Android library module: theme, tokens, palettes, shapes, `Modifier.seaGlass(...)`, all components. No app code.
- `:app` — the showcase: catalogue (one destination per board C0–C7), the Phase 4 screens as working flows, and a control sheet (light/dark, contrast mode, reduce transparency, seed colour, dynamic colour, per-layer toggles, FPS overlay).
- Package layout inside `:seaglass`: `color/`, `shape/`, `material/` (finishes and the glass modifier), `motion/`, `components/<group>/`, `theme/`.
- Backdrop blur: **Haze** (`hazeSource` on the content, `hazeEffect` on each glass element). Everything else uses standard Compose APIs. See the mapping table in `reference/README.md`.
- Shape morphs: `androidx.graphics:graphics-shapes`, or animate the eight elliptical radii directly.
- Lens magnification: AGSL `RuntimeShader` on API 33+, falling back to scaling the content under the lens.

## Stack and versions

- Check `gradle/libs.versions.toml` and the existing build files first; keep the project's AGP, Kotlin and compileSdk unless something below forces a bump.
- Compose UI **1.9 or later** is required (for `Modifier.dropShadow` and `Modifier.innerShadow`). Use the newest stable Compose BOM that satisfies this.
- Material 3 **1.4 or later** (Expressive APIs, `MotionScheme`).
- Haze: newest stable release. Check its README for the current API before using it; do not rely on memory of older versions.
- Glass blur needs API 31+. If the project's minSdk is lower, glass falls back to the reduce-transparency recipe below API 31.
- Icons: Material Symbols Rounded (variable font with FILL, wght, opsz axes) bundled in `res/font`, drawn through a small `SymbolIcon` composable. If the font can't be downloaded, fall back to `Icons.Rounded` and tell me.
- Type: Roboto Flex (bundled variable font or downloadable fonts).

## Conventions

- Keep code comments minimal: only where the logic is genuinely non-obvious, short and without filler. Larger or complex functions get a brief bullet-point summary above them instead of inline comments throughout.
- Tokens are named `seaGlass.*` in the spec; in Kotlin they live in a `SeaGlassTokens` object tree with the same structure (for example `SeaGlassTokens.Shadow.Tint.Opacity`).
- Every component takes a `role: TonalTones` (defaulting from a `LocalGlassRole`), an `InteractionSource`, and reads contrast and reduce-transparency mode from composition locals. Components never hardcode colours.
- No Google logos, product names or brand marks anywhere in the app or sample content.
- Previews: one `@Preview` per component in light and dark at minimum.

## Verify every change

1. `./gradlew :seaglass:compileDebugKotlin :app:assembleDebug` must pass. Fix all errors before moving on.
2. If a device or emulator is connected (`adb devices`), install, launch and capture:
   `adb install -r app/build/outputs/apk/debug/app-debug.apk`, then
   `adb exec-out screencap -p > screenshots/<name>.png`, then look at the PNG yourself and compare it with the spec. The `/sg-check` command does this.
3. Note anything you could not verify (for example blur on an API 30 emulator) in your summary instead of claiming it works.

## Working style

- One milestone per session. Plan first, then implement, then verify, then commit with a short message.
- When a value is missing from the docs, choose the closest one that exists, say which you chose, and keep going.
- Keep the showcase honest: if something is approximated (for example the lens magnification fallback), label it in the UI's info text.
