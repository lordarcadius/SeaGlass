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
