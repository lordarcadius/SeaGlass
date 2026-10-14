# Values still written by hand in the Android SDK

`core/tokens/tokens.json` is exported from the design system, so these values cannot be added to it from this repository. Each one is hard-coded in the SDK because the export has no token for it. To move one into the tokens, add it in the design system, re-export `tokens.json` and run `./gradlew :sdk:tokens:generateSeaGlassTokens`.

| Group | Values | Where |
| --- | --- | --- |
| Hover | shadow drop 3 dp; light alpha 0.32 (light) and 0.14 (dark) | `SeaGlassTokens.State.Hover` |
| Press | shadow rise 3 dp; bloom alpha 0.42 (light) and 0.22 (dark) | `SeaGlassTokens.State.Press` |
| Grain | tile 200 dp; dark-theme opacities (Glass 0.12, Frost 0.10, Clear 0.05, Milk 0.05, Dry 0.18); Well 0.08 / 0.12, Fill 0.08 / 0.10, Ink 0.05 | `SeaGlassTokens.Grain`, `GlassRecipes` |
| Light drift | 8 s, x 0.05, y 0.03 | `SeaGlassTokens.Light` |
| Contrast | tint boost 0 / 0.15 / 0.32; transmission 1 / 0.6 / 0.3; light scale 1 / 0.7 / 0.4 | `SeaGlassTokens.Contrast` |
| Fill tones | light 45; dark 60 (top) and 40 (bottom) | `SeaGlassTokens.Fill` |
| Veils | context menu, FAB menu and progress ring track, light and dark (6 colours) | `SeaGlassTokens.Veil` |
| Reduce transparency | solid fills for milk, well, clear, pearl, ink and dry, light and dark (12 colours) | `SeaGlassTokens.ReduceTransparency` |
| Motion | spatial fast 0.6 / 800, default 0.8 / 380, slow 0.8 / 200; effects no-bounce 3800 / 1600 / 800 (damping ratio / stiffness) | `SeaGlassTokens.Motion` |
| Adaptive tint | dark minimum 0.3 for Glass and Frost | `GlassRecipes` |
| Recipes | per-finish saturation and brightness, shadows, caustics, insets, rims, light pools | `GlassRecipes` |
| Lens | fallback magnification 1.1 below API 33; edge bend 0.08 | `GlassLens` |
| Edge diffusion | blur 14 dp, saturation 1.6, default height 84 dp, tint colours | `GlassEdgeDiffusion` |
| Components | switch knob 28 dp, slider thumb 34 x 22 dp, tab width 76 dp and other sizes without a token | `components/` |
