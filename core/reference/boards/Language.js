    var RAD = K.RAD, BG = { light: K.BGL, dark: K.BGD };
    function txt(t, x) { return cell([Tx(t, T(13.5, 450, '#40484C', 'white-space: normal; line-height: 1.45;' + (x || '')))], { justify: 'flex-start' }); }
    function spec(th, els) { return cell(els, { justify: 'flex-start', gap: 14, nowrap: true, x: 'background: ' + BG[th] + '; border-radius: 18px; height: 68px; padding: 0 16px; box-sizing: border-box;' }); }
    function nudge(e, ml) { e.pos += ' margin-left: ' + ml + 'px;'; return e; }
    var MATS = [
      ['Glass', 'seaGlass.material.glass', 'Primary action, FAB, checked and on states, send.', function (th) { return [Wd({ theme: th, m: 'glass', w: 104, h: 44, lead: 'send', fill: true, label: 'Send', fs: 15 }), Wd({ theme: th, m: 'glass', w: 44, h: 44, shape: 'lens', lead: 'favorite', fill: true, is: 22 })]; }, 'RenderEffect blur 18 dp + ColorMatrix saturation 1.7 (Haze below API 31), tint verticalGradient, three innerShadow layers, gradient Stroke rim, tinted dropShadow. Standard API.'],
      ['Frost', 'seaGlass.material.frost', 'Secondary action, active tab and tool lens, banners, emphasis cards.', function (th) { return [Wd({ theme: th, m: 'frost', w: 104, h: 44, lead: 'bookmark', fill: true, label: 'Save', fs: 15 }), Wd({ theme: th, m: 'frost', w: 44, h: 44, shape: 'lens', lead: 'tune', is: 22 })]; }, 'Blur 14 dp with a lighter tint, top-only rim, soft tinted dropShadow. Standard API.'],
      ['Clear', 'seaGlass.material.clear', 'Floating bars, lenses over imagery, tertiary buttons.', function (th) { return [Wd({ theme: th, m: 'clear', w: 104, h: 44, lead: 'ios_share', label: 'Share', fs: 15, fg: acc(th) }), Wd({ theme: th, m: 'clear', w: 44, h: 44, shape: 'lens', lead: 'more_horiz', is: 22 })]; }, 'Blur 16 dp, white gradient 60 → 32%, specular radialGradient, gradient Stroke rim. Standard API.'],
      ['Pearl', 'seaGlass.material.pearl', 'Knobs, thumbs and page markers at rest.', function (th) { return [Wd({ theme: th, m: 'pearl', w: 28, h: 28, shape: 'round' }), Wd({ theme: th, m: 'pearl', w: 34, h: 22, radius: 11 }), Wd({ theme: th, m: 'pearl', w: 20, h: 8, radius: 4 })]; }, 'Opaque white verticalGradient, innerShadow base tint, small dropShadow. Standard API.'],
      ['Fill', 'seaGlass.material.fill', 'Liquid inside switches, sliders and progress.', function (th) { return [Wd({ theme: th, m: 'well', w: 58, h: 34, radius: 17, live: false }), Wd({ theme: th, m: 'fill', w: 120, h: 12, radius: 6, live: false })]; }, 'Tinted gradient, 1 dp top highlight, glow dropShadow with blur 0.4 × height. Standard API.'],
      ['Well', 'seaGlass.material.well', 'Fields, tracks, unchosen options, pressed rows.', function (th) { return [Wd({ theme: th, m: 'well', w: 150, h: 40, radius: 18, lead: 'search', is: 18, label: 'Search', fs: 14, fw: 500, justify: 'flex-start', pad: 12, gap: 8, fg: ONSV[th], live: false }), Wd({ theme: th, m: 'well', w: 26, h: 26, radius: 9, live: false })]; }, 'Modifier.innerShadow from the top (Compose 1.9), 1 dp bottom lip highlight, light blur. Standard API.'],
      ['Milk', 'seaGlass.material.milk', 'Cards, lists, sheets, alerts, menus, pickers.', function (th) { return [Wd({ theme: th, m: 'milk', w: 168, h: 52, radius: 20, label: 'Tide tables', fs: 14, justify: 'flex-start', pad: 16, live: false })]; }, 'Surface colour at 86–90% over a 30 dp RenderEffect or Haze blur, gradient top rim, large soft shadow. Standard API.'],
      ['Ink', 'seaGlass.material.ink', 'Toasts and plain tooltips, inverse of the theme.', function (th) { return [Wd({ theme: th, m: 'ink', w: 132, h: 40, lead: 'check_circle', fill: true, is: 18, label: 'Saved', fs: 14, live: false })]; }, 'Inverse milk: dark at 90% on light themes, pale at 93% on dark themes. Standard API.'],
      ['Bead', 'seaGlass.material.bead', 'Badges, counts, avatar initials, status dots.', function (th) { return [Wd({ theme: th, m: 'bead', role: 'seapink', w: 20, h: 20, shape: 'round', label: '3', fs: 11, fw: 700, live: false }), Wd({ theme: th, m: 'bead', role: 'seapink', w: 36, h: 20, label: '99+', fs: 11, fw: 700, live: false }), Wd({ theme: th, m: 'bead', role: 'lavender', w: 36, h: 36, shape: 'round', label: 'M', fs: 14, fw: 700, live: false })]; }, 'radialGradient fill with innerShadow gloss and a 3 dp shadow. Standard API.'],
      ['Dry', 'seaGlass.material.dry', 'Every disabled control, whatever its wet material.', function (th) { return [Wd({ theme: th, m: 'glass', st: 'disabled', w: 104, h: 44, lead: 'send', label: 'Send', fs: 15 }), Wd({ theme: th, m: 'glass', st: 'disabled', w: 44, h: 44, shape: 'lens', lead: 'favorite', is: 22 })]; }, 'Blur 8 dp, ColorMatrix saturation 0.3, grain ShaderBrush (ImageShader) at 30%, no shadow or light. Standard API.'],
      ['Lens', 'seaGlass.effect.lens', 'Thumb, knob or tab marker while dragged; magnifies what is beneath.', function (th) { return [Wd({ theme: th, m: 'well', w: 150, h: 10, radius: 5, live: false }), nudge(Wd({ theme: th, m: 'fill', w: 96, h: 10, radius: 5, live: false }), -164), nudge(Wd({ theme: th, m: 'lens', w: 50, h: 36, radius: 18, live: false }), -39)]; }, 'AGSL RuntimeShader with 1.12× magnification and edge refraction (API 33+); below 33 the content under the lens scales 1.1×. Shader.']
    ];
    var secA = {
      title: 'Ten finishes of one glass, plus the lens', grade: 'seaGlass.material.*',
      note: 'Each finish is a fixed recipe of the Phase 1 layer stack, so components never invent their own effects. Wet finishes (glass, frost, clear, pearl, fill, bead) are lit and glossy; well and milk are surfaces; ink is the inverse surface; dry replaces any of them when disabled.',
      hasLabels: true, hasHeads: true, heads: ['', 'Used for', 'Light', 'Dark', 'Compose implementation'], cols: '170px 240px 270px 270px minmax(0, 1fr)', panelCols: 'minmax(0, 1fr)',
      panels: [{ bg: 'bg-paper', fg: '#191C22', title: 'MATERIALS', rows: MATS.map(function (mt) { return { label: mt[0], sub: mt[1], h: 68, cells: [txt(mt[2]), spec('light', mt[3]('light')), spec('dark', mt[3]('dark')), txt(mt[4], ' font-size: 12.5px; color: #4A5160;')] }; }) }]
    };
    var STL = { rest: 'Rest', hover: 'Hover', focus: 'Focus', press: 'Press', disabled: 'Disabled' };
    function sRow(th, name, fn) { return { label: name, h: 54, cells: ST.map(function (s) { return cell([Wd(fn(th, s))]); }) }; }
    var secB = {
      title: 'One state model', grade: 'seaGlass.state.*',
      note: 'Hover lifts raised glass 2 dp and brightens its light pool; surfaces get a 7% layer instead. Focus is the halo: a tint rim, glow and bloom hugging the shape. Press scales to 0.96 and blooms light from the centre, keeping the silhouette. A plain button condenses clear glass only while pressed. Disabled is dry glass.',
      hasLabels: true, hasHeads: true, heads: ['', 'Rest', 'Hover', 'Focus', 'Press', 'Disabled'], cols: '70px repeat(5, minmax(0, 1fr))', panelCols: 'repeat(2, minmax(0, 1fr))',
      panels: panels(function (th) {
        return [
          sRow(th, 'Glass', function (t, s) { return { theme: t, m: 'glass', w: 92, h: 42, label: 'Send', lead: 'send', fill: true, is: 18, fs: 14, gap: 6, st: s }; }),
          sRow(th, 'Clear', function (t, s) { return { theme: t, m: 'clear', w: 92, h: 42, label: 'Share', fs: 14, fg: acc(t), st: s }; }),
          sRow(th, 'Plain', function (t, s) { return { theme: t, m: 'plain', w: 92, h: 42, label: 'Later', fs: 14, fg: acc(t), st: s }; }),
          sRow(th, 'Well', function (t, s) { return { theme: t, m: 'well', w: 104, h: 40, radius: 16, lead: 'search', is: 18, label: 'Search', fs: 13.5, fw: 500, justify: 'flex-start', pad: 10, gap: 6, fg: ONSV[t], tint: s === 'focus', st: s }; }),
          sRow(th, 'Pearl', function (t, s) { return { theme: t, m: 'pearl', w: 30, h: 30, shape: 'round', st: s }; })
        ];
      })
    };
    function shapeCell(th, els, cap, w, h) { return cell(els.concat([Tx(cap, T(12.5, 550, th === 'dark' ? '#C0C8CC' : '#40484C', 'white-space: pre-line; width: 130px; text-align: center; line-height: 1.4;'), 0, h + 14)]), { stack: true, sw: 130, sh: h + 48 }); }
    var secC = {
      title: 'Shape', grade: 'seaGlass.shape.*',
      note: 'Controls are capsules (radius = height / 2 with a ±4% hand-tumbled irregularity), lenses or, for the FAB only, pebbles. Containers are panes from 24 to 36 dp. Anything inset keeps concentric corners. Nothing 36 dp or taller goes below 12 dp; small marks such as 26 dp checkboxes use a third of their height.',
      hasLabels: false, hasHeads: false, heads: [], cols: 'repeat(5, minmax(0, 1fr))', panelCols: 'repeat(2, minmax(0, 1fr))',
      panels: panels(function (th) {
        return [{ h: 150, cells: [
          shapeCell(th, [Wd({ theme: th, m: 'glass', w: 120, h: 44, label: 'Capsule', fs: 14 }, 5, 30)], 'Capsule\nlabelled controls', 120, 88),
          shapeCell(th, [Wd({ theme: th, m: 'clear', w: 60, h: 60, shape: 'lens', lead: 'more_horiz', is: 26 }, 35, 22)], 'Lens\nicon-only, knobs', 60, 88),
          shapeCell(th, [Wd({ theme: th, m: 'glass', role: 'seafoam', w: 76, h: 76, rad: RAD.pebble(), lead: 'edit', fill: true, is: 32 }, 27, 8)], 'Pebble\nFAB only', 76, 88),
          shapeCell(th, [Wd({ theme: th, m: 'milk', w: 120, h: 84, radius: 28, live: false }, 5, 2)], 'Pane\n24 · 28 · 32 · 36 dp', 84, 88),
          shapeCell(th, [Wd({ theme: th, m: 'milk', w: 120, h: 84, radius: 28, live: false }, 5, 2), Wd({ theme: th, m: 'frost', w: 104, h: 44, radius: 20, label: 'r 20', fs: 13, live: false }, 13, 34)], 'Concentric\ninner = 28 − 8 inset', 84, 88)
        ] }];
      })
    };
    var RULES = [
      ['01', 'Wet acts, dry rests', 'Enabled glass is glossy and lit. Disabled glass dries to a matte, desaturated frost with content at 38%. Nothing else signals disabled.'],
      ['02', 'Raised acts, sunken takes', 'Glass, frost, clear and pearl stand off the surface and are pressed. Wells sit below it and are typed or dragged in.'],
      ['03', 'Selection lifts', 'Unchosen options rest in wells; choosing one raises it into glass. The current tab or tool is a frost lens.'],
      ['04', 'Milk holds, ink passes', 'Content lives on milk panes and passing messages on ink. Glass may sit on milk, but never on other glass.'],
      ['05', 'A lens is not a second sheet', 'A lens inside a clear bar is the same glass, thickened. It adds light, never a second shadow or rim stack.'],
      ['06', 'Press keeps the outline', 'Press scales to 0.96 and blooms light from inside. Silhouettes only morph for real state changes such as open and close.'],
      ['07', 'Focus glows', 'A 1.5 dp tint rim hugs the shape with no gap, inside a 14 dp glow and a 34 dp bloom. High contrast thickens the rim to 3 dp.'],
      ['08', 'No sharp corners', 'Capsules, lenses and pebbles for controls, 24–36 dp panes for content, concentric corners when inset. Nothing 36 dp or taller goes below 12 dp.']
    ];
    function rule(r) { return cell([Tx(r[0], T(12, 650, '#006783', "font-family: 'Roboto Mono', monospace;")), Tx(r[1], T(16, 700, '#191C22')), Tx(r[2], T(13.5, 450, '#40484C', 'white-space: normal; line-height: 1.5;'))], { dir: 'column', align: 'flex-start', justify: 'flex-start', gap: 6, x: 'height: 100%; padding: 4px 8px 0 0;' }); }
    var secD = {
      title: 'Composition rules', grade: 'Applies to every component',
      note: 'Eight rules that replace the M3E component anatomy. Where a component on the following boards looks unusual, one of these is the reason.',
      hasLabels: false, hasHeads: false, heads: [], cols: 'repeat(4, minmax(0, 1fr))', panelCols: 'minmax(0, 1fr)',
      panels: [{ bg: 'bg-paper', fg: '#191C22', title: 'RULES', rows: [{ h: 118, cells: RULES.slice(0, 4).map(rule) }, { h: 118, cells: RULES.slice(4).map(rule) }] }]
    };
    return { sections: fin([secD, secA, secB, secC]) };
