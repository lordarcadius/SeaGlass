---
description: Build, install, screenshot and visually check the Sea Glass showcase
---

1. From `platforms/android`, run `./gradlew :sdk:seaglass:compileDebugKotlin :sample:assembleDebug`. If it fails, fix the errors and run it again until it passes.
2. Run `adb devices`. If no device or emulator is connected, stop here and say so.
3. Install and launch: `adb install -r platforms/android/sample/build/outputs/apk/debug/sample-debug.apk`, then start the main activity.
4. Navigate to the screen or catalogue page relevant to the current work ($ARGUMENTS if given). Use `adb shell input` taps or a debug deep link to get there.
5. Capture light and dark: `mkdir -p platforms/android/screenshots && adb exec-out screencap -p > platforms/android/screenshots/<page>-<theme>.png`.
6. Open each screenshot and compare it with core/spec/spec.md and the matching file in core/reference/boards/: layout and sizes, finishes (blur, tint, rim, shadow), content colours, corner shapes. List concrete differences and fix them.
7. Report what you checked, what still differs, and anything that could not be verified on this device.
