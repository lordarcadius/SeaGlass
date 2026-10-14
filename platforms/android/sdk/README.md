# Sea Glass for Android

Sea Glass is a frosted, diffusing glass material and a set of Jetpack Compose components built on Material 3 Expressive.

| Module | Namespace | Contents |
| --- | --- | --- |
| `:tokens` | `com.floydwiz.seaglass.tokens` | Generated design tokens: palettes, colour schemes, type scale, dimensions, shapes, shadows. Source: `core/tokens`. |
| `:seaglass` | `com.floydwiz.seaglass` | Theme, the glass material (`Modifier.seaGlass`, `GlassSurface`, `GlassBackdrop`) and the components. Depends on `:tokens`. |
| `:bom` | | Pins the two modules to one version. |

This folder is its own Gradle project; open it in Android Studio to work on the SDK. Run `./gradlew test` here for the unit tests.

Requirements: minSdk 31, compileSdk 37, Compose BOM 2026.09.00 or newer, Kotlin 2.4.

## Getting the SDK

The SDK is not published to any repository. Use it in one of two ways.

**As an included build** (this repository): the sample project includes this one, and Gradle substitutes the SDK modules for their coordinates.

```kotlin
// settings.gradle.kts
includeBuild("../sdk")

// app/build.gradle.kts
dependencies {
    implementation("com.floydwiz.seaglass:seaglass:1.0.0-alpha01")
}
```

**As AAR files**: run `./gradlew collectSdkAars` from this folder. It writes `build/sdk-aar/tokens-1.0.0-alpha01.aar` and `build/sdk-aar/seaglass-1.0.0-alpha01.aar`. Copy both to the app. An AAR carries no dependency metadata, so the app also needs:

```kotlin
dependencies {
    implementation(files("libs/tokens-1.0.0-alpha01.aar", "libs/seaglass-1.0.0-alpha01.aar"))
    implementation(platform("androidx.compose:compose-bom:2026.09.00"))
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.graphics:graphics-shapes:1.1.0")
    implementation("androidx.core:core-ktx")
}
```

`./gradlew -Pseaglass.fromAar=true :app:assembleDebug` in `platforms/android/sample` builds the sample app this way, to test the AAR setup.

When the SDK is published, the coordinates will be `com.floydwiz.seaglass:{tokens,seaglass,bom}:1.0.0-alpha01`, used as a BOM:

```kotlin
dependencies {
    implementation(platform("com.floydwiz.seaglass:bom:1.0.0-alpha01"))
    implementation("com.floydwiz.seaglass:seaglass")
}
```

## Minimal setup

`GlassBackdrop` marks its `backdrop` as the content the glass blurs; glass elements in its content blur what is behind them.

```kotlin
@Composable
fun Hello() {
    SeaGlassTheme(dark = isSystemInDarkTheme()) {
        GlassBackdrop(
            modifier = Modifier.fillMaxSize(),
            backdrop = { Image(painterResource(R.drawable.photo), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop) },
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                GlassButton(label = "Continue", onClick = { })
            }
        }
    }
}
```

Imports: `com.floydwiz.seaglass.theme.SeaGlassTheme`, `com.floydwiz.seaglass.material.GlassBackdrop`, `com.floydwiz.seaglass.components.buttons.GlassButton`.

`SeaGlassTheme` also takes `contrast`, `reduceTransparency`, `dynamicColor`, `seedRole` and `lightDrift` (the light pool animates only when it is on). Blur needs API 31; with `reduceTransparency` glass draws solid fills.

## Lab API

Hooks for exploring the material are marked `@SeaGlassLabApi`: `GlassLayers` / `LocalGlassLayers` (a switch per layer), `GlassOverrides` / `LocalGlassOverrides` (recipe overrides), `GlassClockScope` (restarts the light drift), `GlassRecipeInfo` (recipe values) and `SeaGlassTokens.Motion.slowdown`. The rendering code reads them, so they stay in the SDK; they are public, need an explicit opt-in, and can change without notice. The sample app opts in with `kotlin { compilerOptions { optIn.add("com.floydwiz.seaglass.SeaGlassLabApi") } }` and applies the layer switches with `LabTheme`, which provides `LocalGlassLayers` around `SeaGlassTheme`.
