# Changelog

## 1.0.0-alpha01

First split of the Android SDK into `:sdk:tokens`, `:sdk:seaglass` and `:sdk:bom`.

- Tokens: palettes, colour schemes, type scale, dimensions, shapes and shadows are generated from `core/tokens` into `:sdk:tokens`. The theme, roles and components read them in place of hand-copied values.
- Material 3 colour roles that were left at Material's defaults now use the Sea Glass values: `background`, `onBackground`, `surfaceVariant`, `inverseSurface`, `inverseOnSurface`, `inversePrimary`, `surfaceBright`, `surfaceDim`.
- Letter spacing of `displayLarge` (-0.25 sp), `titleMedium` (0.15 sp) and `bodyMedium` (0.25 sp) follows the tokens.
- Explicit API mode is on in both modules. Rendering internals, interaction state helpers and unused token groups are internal. Controls for labs and debugging are public behind `@SeaGlassLabApi`.
- `SeaGlassTheme` takes no lab types. The glass layer switches are read from `LocalGlassLayers`, which a lab provides around the theme.
- The glass backdrop is one shared, downscaled blur per `GlassBackdrop`, and shadows and insets are cached, so cost no longer grows with the number of glass elements. The light drift is off by default (`lightDrift`).
- Not published to a repository; `./gradlew collectSdkAars` writes the versioned AAR files to `build/sdk-aar`.
