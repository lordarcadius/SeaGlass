# Showcase structure

The `:app` module is one app with eight areas behind a home screen. Everything is built from `:seaglass` components; `:app` holds only layout, sample content and the control sheet.

Navigation is a single `NavHost` (navigation-compose) in one activity. Every route is wrapped by `ShowcaseScaffold`, which installs `SeaGlassTheme`, the backdrop, the floating control sheet and the FPS overlay.

```
Home
├── Materials lab        materials
├── Components           catalogue/c0 … c7
├── Motion lab           motion
├── Accessibility lab    a11y
├── Screens              screens/{incall, library, inbox, assistant, settings, tablet}
└── Demos                demos/{gallery, todo, files, music, weather, home, notes}
```

Package layout in `:app`: `showcase/{home,materials,catalogue,motion,a11y,screens}`, `demos/<name>`, `control/` (sheet, state), `data/` (made-up sample content, photo crops, gradients).

## Home

- Full-bleed `seaglass_photo.jpg` hero with a large title and one-line description.
- One entry card per area (six cards: Materials, Components, Motion, Accessibility, Screens, Demos). Each card is a **clear pane** (r 28) floating over the photo with the adaptive tint on, a symbol and a title, because it is a tappable action over imagery. Symbols sit directly on the clear pane; no second glass inside it (rule 4).
- Tablet: two-column grid of the same cards. Phone: one column, scrolling over the fixed photo.

## Floating control sheet (every page)

- Trigger: a clear lens, top-right, in the top-bar cluster on every route.
- Sheet: milk bottom sheet (r 36). Changes apply live to the page behind it.
- Controls and what they drive:

| Control | Widget | State it drives |
| --- | --- | --- |
| Theme | segmented System / Light / Dark | `SeaGlassTheme(dark)` |
| Contrast | segmented System / Standard / Medium / High | `SeaGlassTheme(contrast)` (System reads `UiModeManager.getContrast()` on API 34+) |
| Reduce transparency | switch | `SeaGlassTheme(reduceTransparency)`; also forced on when cross-window blur is off |
| Dynamic colour | switch | `SeaGlassTheme(dynamicColor)` |
| Seed role | row of eight filter chips (aqua, seafoam, lavender, sea pink, cobalt, honey, kelly, cornflower) | default `LocalGlassRole` |
| Layers | eight switches: shadow, caustic, contact, body (blur + tint), light, grain, volume, rim | `LocalLayerFlags` read by `Modifier.seaGlass` |
| FPS overlay | switch | JankStats readout (median, p95, jank %) pinned top-left |

- State lives in one `ControlState` holder (DataStore-backed so it survives restarts) exposed as a composition local. Layer flags and FPS overlay are debug aids and say so in the sheet's info text.

## Materials lab

- Finish picker: eleven chips (Glass, Frost, Clear, Lens, Pearl, Fill, Well, Milk, Ink, Dry, Bead).
- Backdrop picker: segmented Photo / Gradient / Text / Solid. Photo = the bundled photo; Gradient = `BGL` / `BGD` from `recipes.js`; Text = a dense paragraph, to show blur reading through; Solid = surface colour.
- Stage: three specimens of the chosen finish (capsule, lens, 168×96 pane) over the backdrop.
- Sliders (milk control panel under the stage, all `:seaglass` sliders), each an override applied on top of the recipe:

| Slider | Range | Overrides |
| --- | --- | --- |
| Blur | 0–40 dp | `bf()` blur radius |
| Tint | −0.30 … +0.30 | tint opacity delta (same slot as the contrast `ta`) |
| Grain | 0–0.40 | grain opacity |
| Light | 0–1.5× | light pool scale (`ls`) |
| Rim | 0–1.5× | rim stop alpha scale |
| Shadow | 0–2× | shadow, caustic and contact scale |

- Per-layer toggles reuse `LocalLayerFlags` locally (scoped to the stage, independent of the global ones). A reset button restores the recipe.
- Info text names the recipe layers in use and which values are overridden.

## Component catalogue (C0–C7)

- One page per board: C0 Language, C1 Buttons, C2 Selection, C3 Input and pickers, C4 Navigation, C5 Containers, C6 Feedback, C7 Floating actions. Sections, order and specimen geometry mirror `reference/boards/<Board>.js`.
- `CataloguePage` provides: a light panel and a dark panel (own `SeaGlassTheme` scope, board backdrops `BGL` / `BGD`), side by side from 600 dp window width and stacked below; a **state picker** (segmented Rest / Hover / Focus / Press / Disabled) that forces the state on every specimen that supports it through `LocalForcedState`; section title, grade (token name) and note text from the board.
- Specimens stay interactive when the picker is on Rest.
- Built in M2 (scaffold, C0) and reused by M3–M5.

## Motion lab

- One card per choreography in spec section 6, each with a stage and a **Replay** button: press / hover / focus, switch, slider, segmented lens, tab bar lens and minimize, FAB menu, top bar edge diffusion, loading morph, comet, bead bob, indeterminate progress, light drift.
- **Slow motion** toggle (header): multiplies every duration by 4 through `LocalMotionScale` (springs get stiffness ÷ 16, timed tweens duration × 4). Applies to the lab only.
- A banner appears when the system animator duration scale is 0: loops stop and transitions become crossfades; the lab shows what the reduced version looks like.

## Accessibility lab

- Mode grid: the Inbox screen rendered four times (Standard, Medium, High, Reduce transparency), each inside its own composition-local scope so all four show at once. 2×2 on tablet, a pager with a mode tab bar on phone.
- Adaptive tint strip: a floating clear bar over a bright backdrop and over a dark backdrop, each with adaptive tint on and off, with the sampled luminance (0–1) and threshold (0.4) read out.
- Rule checklist from spec section 9 (placeholders, symbols on photos, liquid tone, well lip) with a small specimen per rule.

## Screens

- The Phase 4 screens as working flows: In call, Photo library (browse and select), Inbox (with Compose menu), Assistant (chat and voice mode), Settings, Tablet mail.
- Tablet mail is chosen by window size class (rail + list + reading pane at Expanded). On phones a "Tablet preview" action renders the layout in a fixed 1280×800 dp box scaled to fit.

## Demos

Seven apps, specified in `demos.md`: Gallery, Todo, File manager, Music player, Weather, Smart home, Notes. Each is a self-contained flow under `demos/<name>` with its own bottom navigation, launched from the Demos list.

## Cross-cutting rules

- Components never hardcode colours; tokens, role palettes and composition locals only.
- Every component has light and dark `@Preview`s.
- Every screen and demo works in all four accessibility modes and respects the animator duration scale.
- Anything approximated is labelled in the UI's info text (lens magnification fallback, Haze saturation, below API 31 reduce-transparency fallback).
- Made-up names and content; the bundled photo plus generated gradients for imagery; no real brands or logos.
