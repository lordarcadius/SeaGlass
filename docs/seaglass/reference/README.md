# Reading the reference code

The design canvas was built in HTML/CSS. The JavaScript in this folder generated every specimen on it, so it contains the exact numbers. Port the numbers, not the CSS.

## Files

- `recipes.js` — the `kit()` function used by every board.
  - `P` — palette tones per role (`on` = T10, `t20` … `t95`). These match `generated/SeaGlassPalettes.kt`.
  - `mat(m, o)` — the recipe for each finish (`glass`, `frost`, `clear`, `lens`, `pearl`, `fill`, `well`, `milk`, `ink`, `dry`, `bead`), per theme, per contrast level, and for reduce transparency (`o.rt`). Returns CSS for nine layers (see below).
  - `W(o)` — builds one element: picks the finish, applies the state (`rest`, `hover`, `focus`, `press`, `disabled`), the focus `halo()`, content colour (`FG()`), padding, icon and label.
  - `halo(c, dark, hi)` — focus ring and glow.
  - `RAD` — the organic shapes (capsule jitter, lens, pebble, tumble, shard, egg).
  - `setMode()` — `std`, `med`, `high` or `rt`; drives contrast levels and reduce transparency.
- `prelude.js` — helpers prepended to each board script (`acc`, `onc`, `onFill`, `shift`).
- `boards/*.js` — one file per canvas board. Each builds specimens with:
  - `W(o)` / `Wd(o, x, y)` — an element (`Wd` places it absolutely at x, y in dp).
  - `Tx(text, style, x, y)` — text. `Ic(name, size, color, fill, x, y)` — a Material Symbol. `Bx(css, x, y)` — a plain box. `Ph(...)` / `img(...)` — a photo crop or gradient image.
  - Option keys on `W`: `m` finish, `w`/`h` size, `radius`/`rad`/`shape` corners, `st` state, `role` palette, `lead`/`label`/`trail` content, `is` icon size, `fs` font size, `fw` weight, `pad` horizontal padding, `gap`, `fill` filled icon, `tint` (wells), `adapt` (adaptive tint), `focus`.
  - All sizes are dp; font sizes are sp.
- `animations.css` — keyframes and timings for the light drift, loading morph, comet spinner, bead bob and indeterminate progress, plus hover/press transitions.
- `hct.py` — the HCT port used to generate the palettes (validated against the M3 baseline palette to within one RGB unit). Use it if you need a tone that isn't in the generated set.

## Layer order (back to front) and the Compose equivalent

| Layer in `mat()` | What it is | Compose |
| --- | --- | --- |
| `shadow` + `shadowShape` | The element's shape, filled with a tinted colour, blurred, offset down and scaled | `Modifier.dropShadow(shape, Shadow(radius = blur, color, offset = DpOffset(0, ty), spread = negative to mimic the scale))` |
| `caustic` | Soft elliptical glow just below the bottom edge (light passing through) | `drawBehind { drawOval(Brush.radialGradient(...)) }` below the element, blurred via `graphicsLayer { renderEffect = BlurEffect }` on a separate layer, or a pre-softened radial gradient |
| `contact` | Small dark ellipse where the element meets the surface | Same technique as caustic |
| `body` background-blur + background | `backdrop-filter: blur(b) saturate(s) brightness(br)` then a three-stop vertical tint gradient at 0 / 52 / 100% | `Modifier.hazeEffect(state, HazeStyle(blurRadius = b.dp, tints = …, noiseFactor = …))`. Haze tints are applied over the blurred backdrop; express the three-stop gradient as a `HazeTint` brush if supported, otherwise a `drawWithContent` vertical gradient on top. Saturation and brightness: a `ColorMatrix` `RenderEffect` if you write your own pipeline; with Haze, approximate brightness by a white/black tint and skip saturation unless Haze exposes it |
| `light` | Radial gradients (specular pool); `sgDrift` animates it ±5% over 8 s, alternate | `drawWithContent` + `Brush.radialGradient`, offset by a `rememberInfiniteTransition` float |
| `grain` | `grain.png` tiled at 200 dp, at the given opacity | `ShaderBrush(ImageShader(bitmap, TileMode.Repeated, TileMode.Repeated))` drawn at alpha, or Haze `noiseFactor` |
| `vol` | `box-shadow: inset …` lines | `Modifier.innerShadow(shape, Shadow(...))`, one call per inset shadow, in the listed order |
| `rim` | A 1.5 dp ring (mask trick) filled with a gradient; vertical for most finishes, 135° for dark clear | `drawWithContent { drawOutline(outline, brush, style = Stroke(1.5.dp.toPx())) }` inset by 0.75 dp |
| `layer` (from `W`) | State overlay: hover / focus top light, press bloom, or a content-coloured layer at 7% / 12% | `drawWithContent` gradient or solid rect at alpha, clipped to the shape |

CSS `rgba(r,g,b,a)` maps to `Color(r, g, b, (a * 255).toInt())`. `ca(hex, a)` in the JS is "this palette colour at alpha a". `mix(a, b, t)` is a linear sRGB mix (`lerp(Color(a), Color(b), t)`).

Height-relative values use `hs = min(h, 120)` and, for panes, `hz = min(h, 360)`.

## States (from `W`)

- **Hover** (raised finishes): translate −2 dp, shadow +3 dp and ×1.05, top light layer. Surfaces: content colour at 7%.
- **Focus**: `halo()` — 1.5 dp tint ring hugging the shape, a 14 dp glow and a 34 dp bloom. High contrast: 3 dp ring plus a 2 dp separation ring.
- **Press**: scale 0.96 (shape unchanged), shadow −3 dp ×0.9, radial bloom. Surfaces: content colour at 12%. `plain` turns into `clear` while pressed.
- **Disabled**: the `dry` finish; content at 38% of onSurface.

## Things that only approximate in CSS

- Lens magnification while dragging: CSS just used a brighter clear finish. Implement the real 1.12× magnification with an AGSL `RuntimeShader` on API 33+.
- Adaptive tint (`o.adapt`): the canvas applied it by hand where the backdrop was known to be bright. In the app, sample the backdrop luminance and fade the tint in above 0.4.
- Edge diffusion under top bars: CSS used a masked backdrop blur. In Compose use Haze with a vertical gradient mask (Haze supports masks / progressive blur).
