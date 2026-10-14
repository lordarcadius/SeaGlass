# Build plan

> Written before the monorepo layout: `:app` now lives in the Gradle project `platforms/android/sample`, `:seaglass` in `platforms/android/sdk`, and the files this document names under `docs/seaglass/` moved to `core/` and `platforms/android/docs/` (see the root `CLAUDE.md`).

Run one milestone per Claude Code session: start each in plan mode, approve the plan, let it implement and verify, then commit and `/clear` before the next. Paste the prompt under each heading, or run `/sg-milestone <number>`.

Each milestone ends with something you can run and look at. Milestone 1 is a go/no-go on performance; don't start the component work until it passes.

---

## M0 — Project setup

> Read CLAUDE.md, core/spec/spec.md and core/reference/README.md. Set up the project for the Sea Glass prototype:
> - Add a `:seaglass` Android library module and make `:app` depend on it.
> - Add dependencies: Compose BOM (Compose UI 1.9+), Material 3 1.4+, Haze, androidx.graphics:graphics-shapes, navigation-compose, and androidx.metrics (JankStats). Look up current stable versions; don't guess.
> - Move docs/seaglass/generated/*.kt into `:seaglass` under `color/`, rename the package to match the module namespace.
> - Bundle Roboto Flex and Material Symbols Rounded (variable) in `:seaglass` `res/font`, and write `SymbolIcon(name, size, fill, weight, tint)`.
> - Copy core/assets into `:seaglass` (grain) and `:app` (photo) drawables.
> - Create `SeaGlassTheme(dark, contrast, reduceTransparency, dynamicColor)` with composition locals for these and for the current role palette.
> - In `:app`, build the shell: a catalogue home listing C0–C7 and the screens (empty destinations for now) and a control sheet (theme, contrast, reduce transparency, dynamic colour, seed role).
> Acceptance: `./gradlew :app:assembleDebug` passes; the app launches to the catalogue; switching theme in the control sheet recolours it.

## M1 — Glass primitive and performance spike

> Implement the core of `:seaglass`:
> - `SeaGlassShape` with per-corner elliptical radii and factories for capsule (with jitter), lens, pebble, tumble, shard, egg and rounded panes.
> - `SeaGlassTokens` (spec section 10) with values from reference/recipes.js.
> - `GlassBackdrop { }` that installs a Haze state and marks its content as the source.
> - `Modifier.seaGlass(finish, shape, role, interactionSource)` for Glass, Frost, Clear, Milk and Well first, porting `mat()` layer by layer using the table in reference/README.md: shadow, caustic, contact, body (blur + tint), light pool with the 8 s drift, grain, volume inner shadows, rim.
> - A debug flag per layer, driven by the control sheet, so each layer can be turned off (like the Phase 1 anatomy board).
> - A spike screen: the sample photo repeated in a scrolling grid, a floating clear tab bar, and a row of glass, frost and clear buttons, with a frame-time overlay (JankStats).
> Acceptance: builds; on a real device the spike screen scrolls without visible jank. Report median and 95th-percentile frame times with all layers on, and which layers cost most. If it is not smooth, stop and propose options before continuing.

## M2 — All finishes and the state model

> Finish the material system:
> - Add Pearl, Fill, Ink, Bead, Dry and Lens finishes from `mat()`.
> - Implement the state model from `W()` and spec section 4: hover lift and light, focus halo (and the high-contrast ring), press scale 0.96 with bloom (shape unchanged), disabled → Dry with 38% content, and Plain → Clear while pressed.
> - Lens magnification: AGSL RuntimeShader on API 33+ that magnifies 1.12× with a slight edge refraction; below 33, scale the content under the lens 1.1×.
> - Build the C0 Language page in the catalogue: rules, the eleven finishes in light and dark, the state matrix and the shapes (reference/boards/Language.js).
> Acceptance: the C0 page matches the board's layout; every finish renders in both themes; press, hover (with a mouse) and keyboard focus work on the specimens.

## M3 — Buttons and selection

> Build the components on boards C1 and C2 (reference/boards/Buttons.js, Selection.js, spec section 7): GlassButton in five styles and four sizes, icon buttons and toggles, Cluster, SplitButton with its milk menu, button pairs; Switch, Checkbox, Radio, SegmentedControl, the four chip types, Slider (rail, range, stepped, pill, vertical pill), Stepper. Include each component's drag or press lens behaviour. Add catalogue pages C1 and C2 that mirror the boards, in light and dark.
> Acceptance: all specimens on both boards exist and are interactive; switch, slider and segmented lens behaviours work by touch.

## M4 — Input, pickers and navigation

> Build boards C3 and C4 (reference/boards/Inputs.js, Navigation.js): GlassTextField with label above and all states, TextArea, search well, floating search, PromptBar (with attachment and listening state), DatePicker (single and range), TimeWheel, compact picker chips; GlassTabBar (with drag lens and minimize-on-scroll), top bars (large, scrolled with edge diffusion, selection), FloatingToolbar (horizontal and vertical), NavigationRail, Sidebar, ScopeBar, PageDots. Add catalogue pages C3 and C4.
> Acceptance: typing, focus halo and error state work; the tab bar lens can be dragged between tabs and the bar minimizes when a list scrolls.

## M5 — Containers, feedback and floating actions

> Build boards C5, C6 and C7 (reference/boards/Containers.js, Feedback.js, Fabs.js): milk cards (text, media, overlay, emphasis, selected), inset lists with all row states, Alert, BottomSheet, Menu and context menu with lifted preview, Carousel; LoadingPebble (morph), contained loader, bead dots, linear / liquid / ring / comet progress, Toast (ink, two-line, clear over media), Banner (four roles), Bead badges, Tooltip and RichTooltip; FAB in three sizes, ExtendedFab, FabMenu. Add catalogue pages C5–C7.
> Acceptance: every specimen exists; the alert, sheet, menu and FAB menu open and close.

## M6 — Motion pass

> Go through spec section 6 and make every choreography match: springs from the token table, press / hover / focus timing, switch knob swell, slider lens and bubble, segmented and tab lens stretch and settle, tab bar minimize, FAB menu melt and 40 ms cascade, top bar edge diffusion, loading morph keyframes, comet, bead bob, light drift. Respect the system animator duration scale: at 0, use crossfades and stop the loops.
> Acceptance: a "Motion" catalogue page plays each choreography on demand; turning animations off in developer options removes loops and morphs.

## M7 — Screens

> Build the Phase 4 screens as working flows using only `:seaglass` components (reference/boards/InCall.js, Gallery.js, Inbox.js, Assistant.js, Settings.js, Tablet.js; spec section 8): In call, Photo library (browse and select), Inbox (with Compose menu), Assistant (chat and voice mode), Settings, and the tablet mail layout chosen by window size class (rail + list + reading pane). Use the sample photo for imagery and made-up names; no real brands.
> Acceptance: each screen matches its board in light and dark on a 412 dp-wide device; the tablet layout appears on a large-screen emulator.

## M8 — Accessibility and verification

> Implement spec section 9: medium and high contrast (read UiModeManager.getContrast() on API 34+, plus the control sheet override), reduce transparency (control sheet, and automatic when cross-window blur is off or below API 31), adaptive tint by sampling backdrop luminance under floating glass, and the audit rules (placeholders, symbols on photos, liquid tone, well lip). Then verify: capture every screen in every mode and theme with adb, check text and icon contrast on the captures (4.5:1 text, 3:1 icons and state indicators, measured against the pixels behind), and add a Macrobenchmark that scrolls the photo library with glass on.
> Acceptance: a short report in core/spec/verification.md with the measured results and any failures fixed or explained.

## M9 — Showcase labs

> Read platforms/android/docs/showcase.md. Build the showcase areas around the components from M2–M8:
> - Home: full-bleed photo hero with a clear glass entry card per area (adaptive tint on), and the `ShowcaseScaffold` that wraps every route.
> - Floating control sheet on every page: theme, contrast, reduce transparency, dynamic colour, seed role, the eight layer toggles and an FPS overlay (JankStats), backed by one persisted `ControlState`.
> - Materials lab: all eleven finishes over a switchable backdrop (photo, gradient, text, solid) with sliders for blur, tint, grain, light, rim and shadow, plus a toggle per layer scoped to the stage.
> - Motion lab: promote the M6 page so every choreography in spec section 6 has its own card with Replay, and add the slow-motion toggle (`LocalMotionScale`).
> - Accessibility lab: the Inbox screen in Standard / Medium / High / Reduce transparency at once, plus adaptive tint over bright and dark content with the sampled luminance shown.
> Acceptance: every lab is reachable from Home; each slider and toggle changes the stage live; the control sheet opens on every route; slow motion visibly slows each choreography; the four accessibility modes render side by side.

## M10 — Demos part 1

> Read platforms/android/docs/demos.md. Build the Gallery, Todo and File manager demos as working flows using only `:seaglass` components, with made-up content and the bundled photo and generated gradients for imagery:
> - Gallery: albums grid, photo grid with Years / Months / Days / All, full-screen viewer with floating clear toolbar and info sheet, multi-select with clear toolbar, Memories carousel.
> - Todo: lists in a tab bar (rail on tablet), tasks on milk with check pebbles, swipe to complete or delete, filter chips, add-task sheet with text field, date picker and priority segmented control, progress ring per list, extended FAB with menu.
> - File manager: storage overview with ring and liquid bars, breadcrumb scope bar, list / grid segmented control, file rows with glass type tiles, multi-select toolbar, context menu, search with filter chips, empty state.
> - Add the Demos list under Home and an "About this demo" sheet per demo.
> Acceptance: each demo's flows work end to end in light and dark on a phone, Todo uses the rail on a tablet, and none puts glass on glass.

## M11 — Demos part 2

> Read platforms/android/docs/demos.md. Build the Music player, Weather, Smart home and Notes demos:
> - Music player: library, now playing over blurred album art with pill progress slider, clear transport lenses and glass play FAB, volume pill, mini player as the tab bar accessory.
> - Weather: full-bleed sky gradient that changes with conditions, clear overlay cards, hourly carousel, daily list on milk, time scrubber pill slider.
> - Smart home: rooms as scope tabs, device tiles with switches and vertical pill sliders, scene picker, ink toast on each change.
> - Notes: inset list, editor with a floating clear format cluster, checklist items, share sheet.
> Acceptance: each demo's flows work end to end in light and dark; the mini player moves between expanded and minimized tab bar states; weather changes with the conditions switcher.

## M12 — Polish and final verification

> - Run `/sg-check` over every route and demo in light and dark, in all four accessibility modes, on phone and tablet where a device is available; fix visible differences from the spec.
> - Confirm every component has light and dark previews, no component hardcodes a colour, and every approximation is labelled in the UI.
> - Re-run the Macrobenchmark and the contrast checks from M8 across the new screens and demos; add any new failures and fixes to core/spec/verification.md.
> - Update platforms/android/docs/progress.md with a final summary: what exists, what was verified on a device, what was not, known gaps.
> Acceptance: `./gradlew :seaglass:compileDebugKotlin :app:assembleDebug` passes; verification.md covers the new areas; progress.md ends with the final summary.
