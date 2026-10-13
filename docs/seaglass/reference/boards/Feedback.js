    var RAD = K.RAD;
    function st(els, w, h) { return cell(els, { stack: true, sw: w, sh: h, left: true }); }
    function inkFg(th) { return th === 'dark' ? '#171C1F' : '#EEF3F5'; }
    function inkAcc(th, role) { return th === 'dark' ? P[role || 'aqua'].t40 : P[role || 'aqua'].t80; }
    function ringMask(size, thick) { var r = size / 2, k = Math.round((1 - thick / r) * 1000) / 10; return ' -webkit-mask: radial-gradient(closest-side, transparent ' + k + '%, #000 ' + (k + 1) + '%); mask: radial-gradient(closest-side, transparent ' + k + '%, #000 ' + (k + 1) + '%);'; }
    function ring(th, size, pct, thick, x, y, spin) {
      var c = P.aqua, a = th === 'dark' ? c.t70 : c.t60, b = th === 'dark' ? c.t80 : c.t70, track = th === 'dark' ? 'rgba(0,0,0,0.42)' : 'rgba(22,38,48,0.12)';
      var els = [Bx('width: ' + size + 'px; height: ' + size + 'px; border-radius: 50%; background: ' + track + '; box-shadow: inset 0 1.5px 3px ' + (th === 'dark' ? 'rgba(0,0,0,0.5)' : 'rgba(16,32,42,0.18)') + ';' + ringMask(size, thick), x, y)];
      if (spin) { els.push(Bx('width: ' + size + 'px; height: ' + size + 'px; border-radius: 50%; background: conic-gradient(from 0deg, ' + ca(a, 0) + ' 0 45%, ' + b + ' 100%);' + ringMask(size, thick), x, y, 'sg-spin')); return els; }
      els.push(Bx('width: ' + size + 'px; height: ' + size + 'px; border-radius: 50%; background: conic-gradient(' + a + ' 0, ' + b + ' ' + pct + '%, transparent ' + pct + '%);' + ringMask(size, thick), x, y));
      var R = size / 2 - thick / 2, ang = pct / 100 * Math.PI * 2;
      els.push(Bx('width: ' + thick + 'px; height: ' + thick + 'px; border-radius: 50%; background: ' + a + ';', x + size / 2 - thick / 2, y));
      els.push(Bx('width: ' + thick + 'px; height: ' + thick + 'px; border-radius: 50%; background: ' + b + '; box-shadow: 0 0 6px ' + ca(c.t60, 0.6) + ';', Math.round((x + size / 2 + R * Math.sin(ang) - thick / 2) * 10) / 10, Math.round((y + size / 2 - R * Math.cos(ang) - thick / 2) * 10) / 10));
      return els;
    }
    function bar(th, x, y, w, h, pct, o) {
      o = o || {};
      var els = [Wd({ theme: th, m: 'well', w: w, h: h, radius: h / 2, live: false }, x, y)];
      if (o.indet) els.push(Wd({ theme: th, m: 'fill', w: w, h: h, radius: h / 2, live: false }, x, y, 'sg-seg'));
      else els.push(Wd({ theme: th, m: 'fill', w: Math.max(h, Math.round(w * pct)), h: h, radius: h / 2, live: false }, x, y));
      return els;
    }

    var secA = {
      title: 'Loading', grade: 'Glass pebble · Bead',
      note: 'The loading indicator is a glass pebble tumbling through lens, shard, egg and tumble silhouettes on a 2.4 s alternate loop, its light drifting as it turns. Inside busy surfaces it sits in a clear lens; for inline waits three beads bob in sequence. Reduced motion holds the lens shape and pulses opacity instead.',
      hasLabels: true, hasHeads: false, heads: [], cols: '110px minmax(0, 1fr)', panelCols: 'repeat(2, minmax(0, 1fr))',
      panels: panels(function (th) {
        var live = [Wd({ theme: th, m: 'glass', w: 56, h: 56, rad: RAD.lens(), cls: 'sg-morph', live: false }, 0, 8), Wd({ theme: th, m: 'clear', w: 72, h: 72, shape: 'lens', live: false }, 96, 0), Wd({ theme: th, m: 'glass', w: 38, h: 38, rad: RAD.lens(), cls: 'sg-morph', live: false }, 113, 17)];
        ['sg-bob', 'sg-bob2', 'sg-bob3'].forEach(function (k, i) { live.push(Wd({ theme: th, m: 'bead', w: 12, h: 12, shape: 'round', live: false }, 208 + i * 20, 30, k)); });
        live.push(Tx('Finding matches', T(14, 600, ONSV[th]), 278, 26));
        var fr = [];
        ['lens', 'shard', 'egg', 'tumble'].forEach(function (s, i) { fr.push(Wd({ theme: th, m: 'glass', w: 48, h: 48, rad: RAD[s](), live: false }, i * 84, 0), Tx(s, T(11.5, 500, ONSV[th], "font-family: 'Roboto Mono', monospace; width: 64px; text-align: center;"), i * 84 - 8, 58)); });
        return [
          { label: 'Live', sub: 'alone · contained · beads', h: 72, cells: [st(live, 420, 72)] },
          { label: 'Keyframes', sub: '0 · 33 · 66 · 100%', h: 76, cells: [st(fr, 320, 76)] }
        ];
      })
    };
    var secB = {
      title: 'Progress', grade: 'Well + Fill',
      note: 'Progress pours lit liquid into a well. Linear tracks are 8 dp; the liquid variant is 28 dp and carries its label. Rings are wells with rounded caps and a lighter leading edge; the indeterminate ring is a comet that fades to nothing at its tail.',
      hasLabels: true, hasHeads: false, heads: [], cols: '110px minmax(0, 1fr)', panelCols: 'repeat(2, minmax(0, 1fr))',
      panels: panels(function (th) {
        var lin = [Tx('Uploading 12 of 20', T(13, 600, ONS[th]), 0, 0), Tx('62%', T(13, 650, ONSV[th], 'width: 60px; text-align: right;'), 300, 0)].concat(bar(th, 0, 24, 360, 8, 0.62));
        var ind = [Tx('Preparing album', T(13, 600, ONS[th]), 0, 0)].concat(bar(th, 0, 24, 360, 8, 0, { indet: true }));
        var liq = bar(th, 0, 0, 360, 30, 0.62).concat([Tx('Uploading', T(13.5, 650, onFill(th)), 14, 6), Tx('12 of 20', T(13.5, 650, ONS[th], 'width: 80px; text-align: right;'), 266, 6)]);
        var rg = ring(th, 56, 62, 7, 0, 4).concat([Tx('62', T(15, 700, ONS[th], 'width: 56px; text-align: center;'), 0, 23)], ring(th, 56, 0, 7, 84, 4, true), [Wd({ theme: th, m: 'clear', w: 64, h: 64, shape: 'lens', live: false }, 168, 0)], ring(th, 42, 40, 6, 179, 11), ring(th, 22, 75, 4, 264, 21), [Tx('Syncing', T(14, 600, ONSV[th]), 294, 22)]);
        return [
          { label: 'Linear', h: 40, cells: [st(lin, 360, 40)] },
          { label: 'Indeterminate', h: 40, cells: [st(ind, 360, 40)] },
          { label: 'Liquid', sub: '30 dp · labelled', h: 32, cells: [st(liq, 360, 32)] },
          { label: 'Ring', sub: 'determinate · comet\ncontained · inline', h: 66, cells: [st(rg, 380, 66)] }
        ];
      })
    };
    function toast(th) {
      return [Wd({ theme: th, m: 'ink', w: 380, h: 52, live: false }, 0, 0), Ic('check_circle', 22, inkAcc(th), true, 16, 15), Tx('Saved to Shore walk', T(15, 550, inkFg(th)), 48, 15), Wd({ theme: th, m: 'plain', w: 72, h: 40, label: 'Undo', fs: 15, fg: inkAcc(th) }, 302, 6)];
    }
    function toast2(th) {
      return [Wd({ theme: th, m: 'ink', w: 380, h: 68, radius: 26, live: false }, 0, 0), Ic('cloud_off', 22, inkAcc(th, 'seapink'), false, 18, 23), Tx('Couldn’t upload 2 photos', T(15, 600, inkFg(th)), 52, 13), Tx('Waiting for a connection', T(13, 500, ca(inkFg(th), 0.75)), 52, 36), Wd({ theme: th, m: 'plain', w: 72, h: 40, label: 'Retry', fs: 15, fg: inkAcc(th) }, 300, 14)];
    }
    function toast3(th) {
      return [phBg(th, 0, 0, 570, 96, 24, '-400px -500px'), Wd({ theme: th, m: 'clear', w: 380, h: 56, live: false }, 20, 20), Ph(30, 30, 36, 36, { r: 18, size: 360, bp: '-200px -120px' }), Tx('Maya shared 3 photos', T(15, 600, ONS[th]), 76, 38), Wd({ theme: th, m: 'glass', w: 68, h: 38, label: 'View', fs: 14 }, 322, 29)];
    }
    var BN = [['aqua', 'info', 'Backup paused on mobile data', 'Resume'], ['honey', 'warning', 'Storage is 92% full', 'Manage'], ['seapink', 'error', 'Two albums failed to sync', 'Retry'], ['kelly', 'check_circle', 'All 2,481 photos backed up', 'View']];
    function banner(th, b) {
      var fg = onc(th, b[0]);
      return [Wd({ theme: th, m: 'frost', role: b[0], w: 460, h: 52, live: false }, 0, 0), Ic(b[1], 22, fg, true, 16, 15), Tx(b[2], T(15, 550, fg), 48, 15), Wd({ theme: th, m: 'plain', w: 84, h: 40, label: b[3], fs: 14.5, fw: 700, fg: fg, role: b[0] }, 370, 6)];
    }
    var secC = {
      title: 'Toasts and banners', grade: 'Ink · Clear over media · Tinted frost',
      note: 'Toasts are ink panes: dark on light themes, pale on dark, so they read as passing above everything. Over photos a toast becomes clear glass. Persistent banners are tinted frost in the role colour of their meaning, with the action as plain text on the same tint.',
      hasLabels: true, hasHeads: false, heads: [], cols: '110px minmax(0, 1fr)', panelCols: 'repeat(2, minmax(0, 1fr))',
      panels: panels(function (th) {
        var rows = [
          { label: 'Toast', sub: 'ink', h: 52, cells: [st(toast(th), 380, 52)] },
          { label: 'Two-line', h: 68, cells: [st(toast2(th), 380, 68)] },
          { label: 'Over media', sub: 'clear', h: 96, cells: [st(toast3(th), 570, 96)] }
        ];
        BN.forEach(function (b, i) { rows.push({ label: ['Info', 'Warning', 'Error', 'Success'][i], sub: b[0] + ' frost', h: 52, cells: [st(banner(th, b), 460, 52)] }); });
        return rows;
      })
    };
    function beads(th) {
      var els = [Wd({ theme: th, m: 'clear', w: 48, h: 48, shape: 'lens', lead: 'notifications', is: 24 }, 0, 6), Wd({ theme: th, m: 'bead', role: 'seapink', w: 11, h: 11, shape: 'round', live: false }, 33, 8)];
      els.push(Wd({ theme: th, m: 'clear', w: 48, h: 48, shape: 'lens', lead: 'mail', is: 24 }, 72, 6), Wd({ theme: th, m: 'bead', role: 'seapink', w: 20, h: 20, shape: 'round', label: '3', fs: 11, fw: 700, live: false }, 104, 2));
      els.push(Wd({ theme: th, m: 'bead', role: 'seapink', w: 9, h: 9, shape: 'round', live: false }, 168, 26), Wd({ theme: th, m: 'bead', role: 'seapink', w: 20, h: 20, shape: 'round', label: '8', fs: 11, fw: 700, live: false }, 192, 20), Wd({ theme: th, m: 'bead', role: 'seapink', w: 36, h: 20, label: '99+', fs: 11, fw: 700, live: false }, 228, 20), Wd({ theme: th, m: 'bead', role: 'kelly', w: 44, h: 20, label: 'New', fs: 11, fw: 700, live: false }, 280, 20), Wd({ theme: th, m: 'bead', role: 'aqua', w: 46, h: 20, label: 'Beta', fs: 11, fw: 700, live: false }, 340, 20));
      return els;
    }
    function tips(th) {
      var els = [Wd({ theme: th, m: 'ink', w: 116, h: 32, label: 'Add photos', fs: 13, live: false }, 0, 8), Wd({ theme: th, m: 'clear', w: 48, h: 48, shape: 'lens', lead: 'add_photo_alternate', is: 24, st: 'hover' }, 34, 54)];
      els.push(Wd({ theme: th, m: 'milk', w: 300, h: 148, radius: 24, live: false }, 150, 0), Tx('Smart albums', T(15.5, 700, ONS[th]), 170, 16), Tx('Photos from the same beach and hour are grouped for you. Rename or merge them any time.', T(13.5, 450, ONSV[th], 'white-space: normal; width: 260px; line-height: 1.45;'), 170, 42), Wd({ theme: th, m: 'plain', w: 104, h: 36, label: 'Learn more', fs: 14, fg: acc(th) }, 160, 104));
      return els;
    }
    var secD = {
      title: 'Beads and tooltips', grade: 'Bead · Ink · Milk',
      note: 'Counts and status marks are beads: small solid glass drops with a gloss highlight, never larger than 20 dp tall. Plain tooltips are ink capsules; rich tooltips are milk panes with a plain action.',
      hasLabels: true, hasHeads: false, heads: [], cols: '110px minmax(0, 1fr)', panelCols: 'repeat(2, minmax(0, 1fr))',
      panels: panels(function (th) {
        return [
          { label: 'Beads', sub: 'dot 9–11 · count 20', h: 60, cells: [st(beads(th), 400, 60)] },
          { label: 'Tooltips', sub: 'ink · rich milk', h: 148, cells: [st(tips(th), 460, 148)] }
        ];
      })
    };
    return { sections: fin([secA, secB, secC, secD]) };
