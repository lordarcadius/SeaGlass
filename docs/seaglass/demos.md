# Demo apps

Seven small apps that use `:seaglass` the way a real product would. Each is a flow under `app/.../demos/<name>`.

Rules numbered as in spec section 1: **R1** wet acts, dry rests · **R2** raised acts, sunken takes · **R3** selection lifts · **R4** milk holds, ink passes (glass never on glass) · **R5** a lens is not a second sheet · **R6** press keeps the outline · **R7** focus glows · **R8** no sharp corners.

## Shared conventions

- Glass appears only on actions and floating chrome. Content sits on milk or on the plain surface. Passing messages (toasts) are ink.
- Floating glass over photos or gradients turns the adaptive tint on.
- Imagery: the bundled photo cropped per item (`CROPS` in `boards/_screen.js`) and the six `GRADS` gradients; avatars are photo crops or bead initials.
- All names, titles and places are made up (Maya Ortiz, "Shore walk", "Tide tables", …). No brands, logos or product names.
- Role colour is meaning, not decoration: sea pink only for destructive or error; aqua is the default.
- Every demo has a milk empty state, loading as the glass pebble, and works in Standard / Medium / High / Reduce transparency.

---

## 1. Gallery

| Screen | Contents |
| --- | --- |
| Albums | Large title; 2-column grid of album cards (milk r 28, inset photo r 20, caption on the plain surface); floating tab bar (Photos, Albums, Memories) + search lens |
| Photo grid | Large title; segmented Years / Months / Days / All; 127 dp tiles, 4 dp gaps, r 10 (`Gallery.js`); edge-diffused top bar |
| Viewer | Full-screen photo; floating clear toolbar (share, favourite, info, delete); info sheet (milk, r 36) with date, place, size |
| Select | Long-press enters; accent ring + glass check lens on selected tiles; clear toolbar with a glass share lens |
| Memories | Carousel of memory cards; page dots; tap opens a viewer |

| Component | Rule that justifies it |
| --- | --- |
| Segmented control (well + frost lens) | R3: the chosen range lifts out of the well |
| Photo tiles with selected ring + glass check lens | R3: selection lifts. The board's r 10 is below the R8 floor of 12 dp for a 127 dp tile; the board value wins and is ported as-is |
| Floating clear tab bar and toolbars over photos | R4: floating chrome is clear glass; adaptive tint over bright photos |
| Glass share lens in the select toolbar | R1, R2: the one action that does something is the only glass inside the clear toolbar (R5: it is a lens in the bar, not a second sheet) |
| Info sheet and album cards | R4: content on milk |
| Carousel with clear caption and clear arrow lens | R4: captions over imagery are clear; page dots sit in a well (R2) |

## 2. Todo

| Screen | Contents |
| --- | --- |
| Lists | Tab bar of lists (Today, Planned, Shopping, …); navigation rail on tablet; progress ring per list in the list header |
| Tasks | Milk inset list; check pebble, title, due date; filter chips (All, Open, Done, High) above the list; extended FAB "New task" with FAB menu (Task, List, Reminder) |
| Add task | Bottom sheet: text field (title), date field opening the date picker, priority segmented control (Low / Medium / High), glass Add + plain Cancel pair |
| Swipe | Row swipes reveal glass lens actions under the milk row: complete (kelly) right, delete (sea pink) left; ink toast with Undo |

| Component | Rule |
| --- | --- |
| Tasks on milk | R4 |
| Check pebble = Checkbox with lens shape, 28 dp | R3: unchecked rests in a well; checked raises into glass with a check |
| Filter chips | R3: unchosen chips in wells, chosen lifts to glass |
| Priority segmented control | R3 |
| Add-task sheet fields | R2: fields are wells; sheet is milk; confirm is glass (R1) |
| Date picker opened as its own milk dialog over a scrim | R4: milk on milk is allowed; it is not stacked inside the sheet |
| Extended FAB + FAB menu | The one floating primary action (glass); menu items are frost, rising 40 ms apart |
| Progress ring | Liquid fill on a well track (R2 + fill finish) |
| Tab bar (rail on tablet) | Floating chrome is clear; the current list is a frost lens (R3) |
| Ink toast | R4: passing messages are ink |

## 3. File manager

| Screen | Contents |
| --- | --- |
| Storage | Overview ring (used / total) with legend; liquid bars per category (Images, Video, Audio, Documents, Archives, Apps) |
| Browse | Breadcrumb scope bar; segmented List / Grid; file rows or tiles; context menu on long-press |
| Select | Multi-select; clear floating toolbar (copy, move, share, delete) with a glass confirm lens |
| Search | Well search with filter chips (type, size, date); results; milk empty state ("Nothing washed up") |

Glass type tiles (32 dp, r 11, glass finish) use role colours by file type: folders honey, images lavender, video cornflower, audio seafoam, documents aqua, archives kelly, apps cobalt. Sea pink is reserved for delete.

| Component | Rule |
| --- | --- |
| Rows on an inset milk list, tiles as milk cards | R4 |
| Glass type tiles on milk | Glass may sit on milk (R4); colour carries meaning |
| Scope bar (clear capsule, frost lens, count beads) | R3: current folder is the frost lens; beads count items |
| List / Grid segmented control | R3 |
| Context menu (milk, lifted preview, destructive row sea pink) | R4, R2 |
| Multi-select toolbar | Floating clear chrome; glass confirm lens (R5) |
| Storage ring and liquid bars | Fill finish on well tracks; ring is the 56 dp progress ring scaled up |
| Search field and chips | R2 (well) and R3 (chip lifts when on) |

## 4. Music player

| Screen | Contents |
| --- | --- |
| Library | Segmented Songs / Albums / Artists; inset milk list with cover thumbnails (photo crops); floating tab bar (Library, Search, Radio) |
| Now playing | Full-bleed blurred album art; pill progress slider; clear transport lenses (shuffle, previous, next, repeat); glass play FAB; volume pill; ink toast on queue changes |
| Mini player | Accessory capsule in the tab bar: cover, title, play lens. Sits above the bar when expanded and between the current-tab lens and search lens when minimized |

| Component | Rule |
| --- | --- |
| Library list on milk | R4 |
| Pill progress slider and volume pill | R2 + fill finish; the pearl thumb becomes a lens while dragged |
| Clear transport lenses over art | Floating chrome over imagery is clear, adaptive tint on |
| Glass play FAB (80 dp pebble) | R1: the single primary action. The pebble shape is FAB-only, so play is a FAB, not a bespoke button |
| Mini player as tab bar accessory | R5: it is part of the bar's clear glass, not a second sheet |
| Press on transport lenses | R6: scale 0.96, silhouette kept |

## 5. Weather

| Screen | Contents |
| --- | --- |
| Now | Full-bleed sky gradient that changes with conditions (clear, cloudy, rain, dusk); clear overlay card with temperature and summary; hourly carousel; daily list on milk; time scrubber pill slider |
| Locations | Inset milk list of places with mini conditions; add via extended FAB |
| Detail | Milk panes for wind, humidity, UV, sunrise; ring progress for UV |

Documented interpretation: the brief asks for glass cards over the sky. The sky gradient is the imagery, so the current-conditions card and hourly items are overlay captions (C5 overlay = clear), floating over imagery with the adaptive tint, never stacked on other glass (R4). Long-form data, the daily list and the detail panes, stays on milk.

| Component | Rule |
| --- | --- |
| Clear overlay card and hourly items over the sky | Overlay captions over imagery are clear; adaptive tint on |
| Daily list on milk | R4 |
| Time scrubber pill slider | R2 + fill finish; lens while dragged |
| Conditions switcher (debug chips) | R3 |
| Add-location extended FAB | Single glass primary action |

## 6. Smart home

| Screen | Contents |
| --- | --- |
| Home | Scope bar of rooms (Living, Kitchen, Bedroom, Studio) with counts of devices on; device tile grid; scene picker row |
| Device | Tile expands to a milk sheet with a large vertical pill slider (brightness, speed) and settings rows |
| Scenes | Scene chips (Evening, Away, Movie, Sleep); ink toast on each change ("Studio lights 60%") |

| Component | Rule |
| --- | --- |
| Rooms as scope tabs (clear capsule, frost lens) | R3: current room is the frost lens |
| Device tiles on milk, r 28 | R4 |
| Switch on each tile | R2/R3: off rests in a well, on lifts to liquid glass |
| Vertical pill sliders (64×168, symbol inside the liquid) | R2 + fill finish |
| Scene picker as filter chips | R3 |
| Ink toast on every change | R4: passing messages are ink |
| Disabled (offline) devices | R1: dry finish, content 38% |

## 7. Notes

| Screen | Contents |
| --- | --- |
| List | Large title; well search; inset milk list of notes (title, snippet, date); extended FAB "New note" |
| Editor | Plain surface with title and body; floating clear format cluster (bold, italic, list, checklist); checklist items with checkboxes; keyboard-aware |
| Share | Bottom sheet (milk r 36): target grid of glass icon tiles with made-up targets (Message, Mail, Copy link, Print, Save to file); plain Cancel |

| Component | Rule |
| --- | --- |
| Note rows on inset milk list | R4 |
| Search well | R2 |
| Format cluster: one clear capsule of 40 dp items, active items are frost lenses | R3 (several can be active); R5: lenses, not second sheets |
| Checklist checkboxes | R3 |
| Share sheet on milk with glass tiles | Glass on milk (R4) |
| Extended FAB | Single glass primary action |

---

## Build notes

- Demos use only `:seaglass` components. If a demo needs a component that does not exist, add it to `:seaglass` following the board geometry, not to `:app`.
- Sample data is in-memory (`data/`), seeded deterministically, so screenshots are reproducible.
- Each demo has an "About this demo" milk sheet reachable from its top bar that lists the components used and the rules above, so the demo documents itself.
