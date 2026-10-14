# Sea Glass — design spec

Sea Glass keeps Material 3 Expressive's foundations (type scale, layout grid, colour roles, motion springs) and replaces its component anatomy with an original language built from one material: frosted sea glass. The glass diffuses rather than refracts. Every corner is round.

Exact numbers live in `../reference/recipes.js` and `../reference/boards/*.js`. This document explains intent and names the parts.

---

## 1. Composition rules

1. **Wet acts, dry rests.** Enabled glass is glossy and lit. Disabled glass dries to a matte, desaturated frost with content at 38%. Nothing else signals disabled.
2. **Raised acts, sunken takes.** Glass, frost, clear and pearl stand off the surface and are pressed. Wells sit below it and are typed or dragged in.
3. **Selection lifts.** Unchosen options rest in wells; choosing one raises it into glass. The current tab or tool is a frost lens.
4. **Milk holds, ink passes.** Content lives on milk panes and passing messages on ink. Glass may sit on milk, never on other glass.
5. **A lens is not a second sheet.** A lens inside a clear bar is the same glass, thickened: it adds light, never a second shadow or rim stack.
6. **Press keeps the outline.** Press scales to 0.96 and blooms light from inside. Silhouettes only morph for real state changes such as open and close.
7. **Focus glows.** A 1.5 dp tint rim hugs the shape with no gap, inside a 14 dp glow and a 34 dp bloom. High contrast thickens the rim to 3 dp.
8. **No sharp corners.** Capsules, lenses and pebbles for controls; 24–36 dp panes for content; concentric corners when inset. Nothing 36 dp or taller goes below 12 dp; small marks use a third of their height.

---

## 2. Colour

Ten HCT tonal palettes, each `TonalPalette.fromInt(seed)`. Values are in `../tokens/palettes.json`.

| Palette | Role | Seed | HCT hue | Chroma |
| --- | --- | --- | --- | --- |
| Aqua | primary | #1FB5E0 | 225.7 | 50.5 |
| Seafoam | secondary | #5EBF98 | 167.9 | 43.5 |
| Lavender | tertiary | #A97FD6 | 309.4 | 48.4 |
| Sea pink | error | #D9545E | 16.8 | 63.8 |
| Cobalt | accent | #2F55D4 | 275.6 | 65.6 |
| Honey amber | accent | #E7A93B | 78.1 | 47.5 |
| Kelly green | accent | #3A9A45 | 147.0 | 58.2 |
| Cornflower | accent | #6F95E3 | 266.6 | 47.3 |
| Neutral | surfaces | aqua hue | 225.7 | 6 |
| Neutral variant | outlines | aqua hue | 225.7 | 8 |

- Static schemes: `../tokens/tokens.json` (M3 role → tone, light and dark).
- Dynamic colour (API 31+): use `dynamicLightColorScheme` / `dynamicDarkColorScheme` for the four main roles and harmonize the accents toward the dynamic primary (`Blend.harmonize` in material-color-utilities).
- Glass is coloured by a whole **role palette** (`TonalTones`), not by a single scheme colour. A glass button in the aqua role reads tones 30–95 of the aqua palette.
- Text: onSurface N10 / N90, onSurfaceVariant NV30 / NV80, outline NV50 / NV60 (light / dark).
- Content on tinted glass: `on` (T10) in light, T95 in dark. Content on liquid fill: white in light, `on` in dark.

---

## 3. Finishes

Each finish is a fixed combination of the layer stack. Components never invent their own effects.

| Finish | Used for | Character |
| --- | --- | --- |
| **Glass** | primary action, FAB, checked / on states, send | Tinted (T80→T90 light, T40→T30 dark), full stack: blur 18–20, lit pool, transmission glow at the bottom, rim, tinted shadow, caustic, contact shadow, grain |
| **Frost** | secondary action, current tab / tool lens, banners, emphasis cards, menu highlight | Lighter tint, blur 14, top-only rim, soft tinted shadow |
| **Clear** | floating bars, lenses over imagery, tertiary buttons, toasts over media | Neutral. Light: white 60→32%. Dark: smoked glass (white 20→10%, saturate 200%, brightness 1.55), diagonal 135° rim highlights, glossy top |
| **Lens** | knob, thumb or tab marker while dragged | Clear, brighter, magnifies what is beneath (1.12× AGSL shader) |
| **Pearl** | knobs, thumbs, page markers at rest | Near-opaque white bead with a soft base tint and small shadow |
| **Fill** | liquid inside switches, sliders, progress | Light: about T45–T48 (deep, for 3:1); dark: T60→T40. Glow shadow 0.4·h |
| **Well** | fields, tracks, unselected options, pressed rows | Sunken: darkened backdrop, inner top shadow, 1.5 dp lip (dark at top in light, lit at bottom in dark). Optional role tint when focused / in error |
| **Milk** | cards, lists, sheets, alerts, menus, pickers | Near-opaque frosted surface (86–90%), blur 30, lit top edge, large soft shadow |
| **Ink** | toasts, plain tooltips | Inverse milk: dark on light theme, pale on dark |
| **Bead** | badges, counts, avatar initials, status dots | Small solid drop: radial gradient (T45→T35 light, T80→T70 dark), gloss, 3 dp shadow |
| **Dry** | every disabled control | Blur 8, saturation 0.3, heavy grain (0.3 light / 0.18 dark), no shadow or light |

Compose mapping per layer: see `../reference/README.md`. Implementation notes per finish:

- Glass, frost, clear, milk, ink, dry: Haze backdrop blur + tints + noise, `innerShadow`, gradient rim stroke, `dropShadow`. Standard API.
- Pearl, bead: opaque gradients + `innerShadow` + `dropShadow`. Standard API.
- Fill: tinted gradient + glow `dropShadow`. Standard API.
- Well: light blur + `innerShadow` from the top + 1.5 dp lip. Standard API.
- Lens magnification: AGSL `RuntimeShader` (API 33+); below 33, scale the content under the lens 1.1×. Shader.
- Adaptive tint: luminance sampling (standard) or a per-pixel luminance ceiling (`RuntimeShader`, API 33+).

---

## 4. States

| State | Raised finishes | Surfaces (well, milk, plain) |
| --- | --- | --- |
| Rest | — | — |
| Hover | lift 2 dp; shadow +3 dp, ×1.05; top light layer (white 32% light / 14% dark) | content colour at 7% |
| Focus | halo (rule 7) + top light layer | halo |
| Press | scale 0.96, shape unchanged; shadow −3 dp, ×0.9; radial bloom (white 42% / 22%) | content colour at 12%; a plain button condenses clear glass |
| Disabled | dry finish, content 38% | dry finish, content 38% |
| Drag | knob / thumb / tab marker becomes a lens | — |

Selected states are per component; they always follow "selection lifts".

---

## 5. Shape

- **Capsule**: r = min(w, h)/2 with a ±4% hand-tumbled jitter per corner (horizontal radii r, 1.04r, 0.98r, 1.02r; vertical 0.98r, 1.03r, r, 1.04r; order top-left, top-right, bottom-right, bottom-left).
- **Lens**: 50% 48% 52% 50% / 48% 52% 49% 51% (horizontal / vertical, percent of width / height).
- **Pebble** (FAB only): 42% 38% 40% 45% / 40% 44% 38% 43%.
- **Tumble, shard, egg**: loading-indicator keyframes (values in `RAD`).
- **Panes**: 24, 28, 32, 36 dp. Inset content keeps concentric corners (inner = outer − inset).
- Compose: a custom `Shape` that builds a `RoundRect` with a separate `CornerRadius(x, y)` per corner. Percentages resolve against width (x) and height (y).

---

## 6. Motion

Springs (M3 Expressive):

| Token | Damping ratio | Stiffness |
| --- | --- | --- |
| spatial.fast | 0.6 | 800 |
| spatial.default | 0.8 | 380 |
| spatial.slow | 0.8 | 200 |
| effects.fast | 1.0 | 3800 |
| effects.default | 1.0 | 1600 |
| effects.slow | 1.0 | 800 |

Timed loops (from `animations.css`): light drift 8 s ease-in-out alternate (±5% x, ±3% y); loading morph 2.4 s alternate through lens → shard → egg → tumble; comet spinner 1.2 s linear; indeterminate progress 1.8 s (clip sweep); bead bob 1.1 s with −0.75 s / −0.4 s phase offsets.

Choreographies:

- **Press / hover / focus**: spatial.fast for scale and lift, effects.fast for light.
- **Switch**: knob slides on spatial.fast; while pressed it swells into a 44×38 lens that overflows the track by 2 dp; liquid pours in on effects.default.
- **Slider**: thumb swells into a 54×38 lens with an ink value bubble rising 41 dp above while dragging.
- **Segmented control / tab bar**: the frost lens follows the finger, turns clear and stretches between segments, bulging past the well or bar edge by 3–6 dp, then settles on spatial.default.
- **Tab bar minimize**: on scroll down, the bar condenses into a current-tab lens + accessory capsule + search lens (spatial.default); expands on scroll up.
- **FAB menu**: content veils (blur 6 + tint), the pebble melts into a round glass close lens (via an egg-shaped midpoint), frost items rise from it 62 dp apart, each 40 ms after the previous, on spatial.fast.
- **Top bar**: edge diffusion fades in as content scrolls under (effects.default).
- **Reduced motion**: no drift, morph, bob or comet; transitions become crossfades.

---

## 7. Components

Sizes in dp. Geometry for every specimen is in `../reference/boards/*.js`.

### C1 Buttons (`Buttons.js`)
- Styles: Glass (primary), Frost (secondary), Clear (accent label, neutral glass), Plain (no material until pressed), Destructive (glass in sea pink).
- Sizes: S 36 / M 44 / L 56 / XL 72 tall; padding 14 / 18 / 24 / 30; label 14 / 15 / 17 / 20 sp; icon 18 / 20 / 22 / 26; gap 6 / 8 / 10 / 12. 48 dp minimum touch target.
- Icon buttons: 48 lens, icon 46% of size. Toggle: off = clear with outlined symbol, on = glass with filled symbol.
- Cluster: one clear capsule (44·n + 4 × 48) holding 40 dp items; the active item is a frost lens; pressing another condenses a 48 dp clear lens.
- Split button: 106×44 + 40×44 glass pieces, 3 dp seam, 12 dp inner corners. Open: trailing piece becomes a 44 dp lens and a milk menu drops (rows 44, radius 24, highlighted row = frost capsule).
- Pairs: plain dismiss + glass confirm; stacked full-width capsules in sheets and dialogs.

### C2 Selection (`Selection.js`)
- Switch: well 58×34 (r 17), liquid 54×30 inset 2, pearl knob 28 inset 3. Pressed: lens 44×38. Focus: halo on the track.
- Checkbox: 26 dp, radius 9. Off = well; on / mixed = glass with a weight-700 check / dash; error = sea pink tinted well with 1.5 dp ring.
- Radio: 26 dp. Off = well; on = glass lens holding a 10 dp pearl.
- Segmented control: well 40 tall (r 20); frost lens 32 tall; segment width 80 (text), 108 (icon + text), 56 (icon). Pressed: clear lens segW + 14 × 46.
- Chips (34 tall): filter off = well; filter on = glass + check; input = frost + 26 dp avatar bead + 22 dp remove lens; suggestion = clear + accent symbol.
- Slider: well track 10 (r 5), liquid fill, pearl thumb 34×22; dragging = 54×38 lens + 48×30 ink bubble. Range: two thumbs. Stepped: 4 dp stop dots. Pill slider 300×48 (r 24) and vertical 64×168 (r 28) with the symbol inside the liquid.
- Stepper: clear capsule 136×44 with plain lens − / +.

### C3 Input and pickers (`Inputs.js`)
- Text field: label above (13 sp / 600), well 52 tall (r 18), leading symbol 20, value 16 sp, clear lens 26 to clear, supporting text 12.5 sp. Focus: role-tinted well + halo. Error: sea pink tint + 1.5 dp ring + error text. Disabled: dry.
- Text area: well radius 22 with counter.
- Search: well 46 tall (r 23) in content; floating search = clear capsule 52 + separate clear lens.
- Prompt bar: clear capsule 60 tall, plain + and mic, glass send lens 44, attachment capsule above. Listening: lavender glass with live waveform.
- Date picker: milk 340×384 (r 32), 44 dp day cells, selected = 40 dp glass lens, today = accent bold numeral + 5 dp bead, range = tinted well band between two lenses.
- Time wheel: milk 340×232, frost band 40 tall; values shrink and fade away from the band.

### C4 Navigation (`Navigation.js`)
- Tab bar: clear capsule 62 tall, floating 14 dp above the bottom edge; tabs 76 wide; current tab = frost lens 54 tall; search in its own 62 dp clear lens.
- Minimized: current-tab lens 56 + accessory clear capsule + search lens.
- Top bar: no container; clear lenses 44 and clear clusters; large title 34 sp / 760; edge diffusion 84 dp.
- Floating toolbar: clear capsule 56 tall, items 48, active = frost lens, confirm = separate 56 dp glass lens.
- Rail (tablet): clear capsule 80 wide (r 40) floating 16 dp from the edge, pebble FAB 56 on top, items = frost 56×32 + 12 sp label.
- Sidebar: milk 272 wide (r 32) with a frost selected row.
- Scope bar: clear capsule 44 tall, frost lens 36, count beads. Page dots: well 22 tall, pearl 20×8 for the current page.

### C5 Containers (`Containers.js`)
- Cards: milk r 28; inset photo r 20 at 8 dp; overlay caption = clear r 20; emphasis = frost; selected = 2 dp accent ring + 30 dp glass check lens.
- Inset lists: milk r 26, rows 56, glass icon tiles 32 (r 11), hairlines from the text. Hover 7% layer, pressed = well dip, selected = frost capsule.
- Alert: milk 320 wide (r 34), 52 dp glass icon lens, full-width capsules (50 confirm / 46 cancel).
- Bottom sheet: milk inset 8 dp, r 36, well grabber 40×6.
- Menu / context menu: milk r 24, rows 44, highlighted row frost, destructive row sea pink.
- Carousel: photos r 28 tightening to 20 as they shrink; clear caption, clear lens arrow, page dots in a well.

### C6 Feedback (`Feedback.js`)
- Loading: 56 dp glass pebble morphing; contained version in a 72 dp clear lens; three 12 dp beads bobbing.
- Progress: well 8 tall + liquid; labelled liquid 30 tall; ring 56 dp, stroke 7, round caps, lighter leading edge; indeterminate ring = comet.
- Toasts: ink 52 tall (two-line: 68, r 26); over media: clear. Banners: frost in the role of their meaning (info aqua, warning honey, error sea pink, success kelly), 52 tall.
- Beads: dot 9–11, count 20, "99+" 36×20. Tooltip: ink 32 tall. Rich tooltip: milk r 24 with a plain action.

### C7 Floating actions (`Fabs.js`)
- FAB: glass pebble 56 / 80 / 96, icon 42% of size. Extended: capsule 56 tall.
- FAB menu: see Motion. Items are frost capsules 52 tall, right-aligned.

---

## 8. Screens (Phase 4)

Phone frame 412×915 dp; tablet 1280×800 dp. Geometry in `../reference/boards/<Screen>.js`.

- **In call** (`InCall.js`): blurred, saturated caller photo as backdrop; 78 dp clear control lenses (Speaker on = glass); sea pink glass end-call capsule 180×72; clear lenses for message and more.
- **Photo library** (`Gallery.js`): browse = large title, segmented Years/Months/Days/All, 127 dp tiles with 4 dp gaps and r 10, floating clear tab bar + search lens. Select = edge diffusion top bar, accent ring + glass check on selected tiles, clear toolbar with a glass share lens. Dark-theme bars use the adaptive tint.
- **Inbox** (`Inbox.js`): well search, filter chips (Unread lifted to glass), 86 dp rows with avatars and beads, extended glass Compose; Compose menu = veil + frost cascade + glass close lens.
- **Assistant** (`Assistant.js`): frost user bubbles, plain-text answers with a lavender pebble mark, clear suggestion capsules, milk result card, clear prompt bar with a lavender glass voice lens over a diffusion edge. Voice mode = aurora background, 180 dp morphing lavender orb, clear lenses for mute / camera / type, sea pink glass end.
- **Settings** (`Settings.js`): milk inset lists, glass icon tiles, honey pill slider for brightness, rail sliders tinted per row, switches.
- **Tablet mail** (`Tablet.js`): clear rail with pebble compose, list with a frost selected row, milk reading pane (r 32) with a clear reply bar ending in a glass send lens.

---

## 9. Accessibility

### Modes

| Mode | What changes (exact values in `mat()` via `lvl` and `o.rt`) |
| --- | --- |
| Standard | as designed |
| Medium contrast | tint +0.15, transmission ×0.6, light ×0.7, no grain; dark clear becomes 60% smoked neutral; wells and pearls get an outline ring (55% / full) |
| High contrast | tint +0.32, transmission ×0.3, light ×0.4, no grain; clear / milk / well / pearl get a 1.5 dp neutral-variant T50 (light) / T60 (dark) ring; liquid becomes solid T40 / T80; on dark themes the knob turns dark (#2C3134) with an outline; onSurfaceVariant text becomes onSurface; accent text moves to T30 / T90; focus ring 3 dp |
| Reduce transparency | no blur, grain, light or caustics; solid fills (glass ≈ T85 / ≈ T34, frost T90 / between T20–T30, surfaces from the neutral palette, see `RTS`), neutral shadows, full-strength outlines on wells, pearls and clear elements; edge diffusion and veils become solid |

### Rules that came out of the audit

- Placeholders use onSurfaceVariant at full strength (never 60–75% opacity).
- Symbols on photos sit in a small ink bead.
- Light-theme liquid is about T45 with white content on it.
- Wells always have the 1.5 dp lip.
- **Adaptive tint**: floating glass over bright content (average backdrop luminance > 0.4) fades in a protective tint — neutral T6 at 60% in dark, white at 25% in light — and drops its brightness lift. Tokens: `seaGlass.adapt.threshold = 0.4`, `seaGlass.adapt.tint`.
- Thresholds: 4.5:1 for text, 3:1 for icons, large text and UI state indicators, measured against the actual pixels behind the element (5th percentile).
- Accepted exception: in standard light, the pearl knob of an off switch is 1.4:1 against its empty track. Medium contrast and above outline it.

### Wiring on Android

- Contrast: `UiModeManager.getContrast()` on API 34+ — below 0.25 standard, up to 0.75 medium, above high. Pass the same level to the colour scheme.
- Reduce transparency: there is no system-wide switch, so offer an in-app setting and also enter this mode when `WindowManager.isCrossWindowBlurEnabled()` reports blur is off (for example in power saving), or below API 31.
- Reduced motion: honour the system animator duration scale (0 = no animation).

---

## 10. Token names

Use these names (as a `SeaGlassTokens` object tree in Kotlin):

- `seaGlass.grade.*`, `seaGlass.frost.{blur,saturation,brightness}`, `seaGlass.tint.{top,bottom}.{tone,opacity}`, `seaGlass.grain.opacity`, `seaGlass.light.{key,core,drift}.*`, `seaGlass.depth.{transmission,topShade,edge}`, `seaGlass.rim.{width,top,bottom}`, `seaGlass.shadow.{tint,offsetY,blur,contact}`, `seaGlass.caustic.{opacity,reach,width}`
- `seaGlass.shape.{capsule,lens,pebble,tumble,shard,egg}`, `seaGlass.shape.pane.{24,28,32,36}`, `seaGlass.shape.min = 12.dp`
- `seaGlass.state.hover.lift = 2.dp`, `seaGlass.state.press.scale = 0.96`, `seaGlass.state.focus.{ring,glow,bloom}`, `seaGlass.state.disabled.contentAlpha = 0.38`
- `seaGlass.contrast.{medium,high}.{tintBoost,transmissionScale,lightScale,outline}`, `seaGlass.reduceTransparency.*`
- `seaGlass.adapt.{threshold,tint}`, `seaGlass.well.lip = 1.5.dp`, `seaGlass.fill.tone`
- `seaGlass.motion.{spatial,effects}.{fast,default,slow}`

Where Phase 2 tokens and Phase 3 recipes differ (hover lift 3 dp vs 2 dp, press scale 0.97 vs 0.96), the Phase 3 values in this document and `recipes.js` win.
