    var TABS = [['home', 'Home'], ['photo_library', 'Photos'], ['explore', 'Explore'], ['person', 'You']];
    function edge(th, w, h) {
      var g = th === 'dark' ? 'rgba(9,12,14,0.55), rgba(9,12,14,0)' : 'rgba(255,255,255,0.6), rgba(255,255,255,0)';
      var mk = 'linear-gradient(180deg, #000 40%, transparent)';
      return Wd({ theme: th, m: 'none', w: w, h: h, rad: '24px 24px 0 0', live: false, bx: 'background: linear-gradient(180deg, ' + g + '); backdrop-filter: blur(14px) saturate(160%); -webkit-backdrop-filter: blur(14px) saturate(160%); -webkit-mask-image: ' + mk + '; mask-image: ' + mk + ';' }, 0, 0);
    }
    function tabbar(th, mode, x, y) {
      var TW = 76, BH = 62, BW = TABS.length * TW + 8, els = [Wd({ theme: th, m: 'clear', w: BW, h: BH, live: false }, x, y)];
      if (mode === 'drag') els.push(Wd({ theme: th, m: 'lens', w: 100, h: 74, radius: 37, live: false }, x + 4 + 44, y - 6));
      else els.push(Wd({ theme: th, m: 'frost', w: TW, h: BH - 8, radius: (BH - 8) / 2, live: false }, x + 4, y + 4));
      TABS.forEach(function (t, i) {
        var sel = i === 0 && mode !== 'drag', mag = mode === 'drag' && i === 1;
        els.push(Wd({ theme: th, m: 'none', w: TW, h: BH - 8, dir: 'column', gap: mag ? 3 : 2, lead: t[0], is: mag ? 29 : 24, fill: sel || mag, ic: mag ? acc(th) : null, label: t[1], fs: mag ? 12.5 : 11, fw: 650, fg: sel ? onc(th) : ONS[th], live: false, st: mode === 'focus' && i === 2 ? 'focus' : 'rest' }, x + 4 + i * TW + (mag ? 8 : 0), y + 4));
      });
      els.push(Wd({ theme: th, m: 'clear', w: BH, h: BH, shape: 'lens', lead: 'search', is: 26 }, x + BW + 10, y));
      return els;
    }
    function tabMin(th, x, y) {
      var els = [Wd({ theme: th, m: 'clear', w: 56, h: 56, shape: 'lens', lead: 'home', is: 26, fill: true, ic: acc(th) }, x, y)];
      els.push(Wd({ theme: th, m: 'clear', w: 236, h: 56, live: false }, x + 66, y));
      els.push(Ph(x + 76, y + 10, 36, 36, { r: 18, size: 300, bp: '-150px -40px' }));
      els.push(Tx('Uploading 3 photos', T(14, 650, ONS[th]), x + 122, y + 10), Tx('Shore walk · 2 left', T(12, 500, ONSV[th]), x + 122, y + 30));
      els.push(Bx('width: 22px; height: 22px; border-radius: 50%; background: conic-gradient(' + acc(th) + ' 0 66%, ' + ca(ONS[th], 0.14) + ' 66% 100%); -webkit-mask: radial-gradient(closest-side, transparent 64%, #000 66%); mask: radial-gradient(closest-side, transparent 64%, #000 66%);', x + 66 + 200, y + 17));
      els.push(Wd({ theme: th, m: 'clear', w: 56, h: 56, shape: 'lens', lead: 'search', is: 26 }, x + 312, y));
      return els;
    }
    function scene(th, h, bp, inner) { return cell([phBg(th, 0, 0, 412, h, 24, bp)].concat(inner), { stack: true, sw: 412, sh: h, left: true }); }
    function cluster(th, x, y, icons) {
      var els = [Wd({ theme: th, m: 'clear', w: 44 * icons.length + 4, h: 44, live: false }, x, y)];
      icons.forEach(function (ic, i) { els.push(Ic(ic, 22, ONS[th], false, x + 2 + i * 44 + 11, y + 11)); });
      return els;
    }
    function tool(th, x, y, press) {
      var IC = ['crop', 'tune', 'auto_fix_high', 'filter_vintage', 'text_fields'], els = [Wd({ theme: th, m: 'clear', w: IC.length * 48 + 8, h: 56, live: false }, x, y)];
      IC.forEach(function (ic, i) {
        var ix = x + 4 + i * 48;
        if (i === 1) els.push(Wd({ theme: th, m: 'frost', w: 48, h: 48, shape: 'lens', lead: ic, is: 24, fill: true }, ix, y + 4));
        else if (press && i === 3) els.push(Wd({ theme: th, m: 'lens', w: 58, h: 58, shape: 'lens', lead: ic, is: 28, live: false }, ix - 5, y - 1));
        else els.push(Ic(ic, 24, ONS[th], false, ix + 12, y + 16));
      });
      els.push(Wd({ theme: th, m: 'glass', w: 56, h: 56, shape: 'lens', lead: 'check', is: 28, iw: 600 }, x + IC.length * 48 + 18, y));
      return els;
    }
    function vtool(th, x, y) {
      var IC = ['brush', 'ink_eraser', 'format_color_fill', 'gesture'], els = [Wd({ theme: th, m: 'clear', w: 56, h: IC.length * 48 + 8, radius: 28, live: false }, x, y)];
      IC.forEach(function (ic, i) {
        if (i === 0) els.push(Wd({ theme: th, m: 'frost', role: 'honey', w: 48, h: 48, shape: 'lens', lead: ic, is: 24, fill: true }, x + 4, y + 4));
        else els.push(Ic(ic, 24, ONS[th], false, x + 16, y + 16 + i * 48));
      });
      return els;
    }
    function rail(th, x, y) {
      var els = [Wd({ theme: th, m: 'clear', w: 80, h: 404, radius: 40, live: false }, x, y), Wd({ theme: th, m: 'glass', w: 56, h: 56, shape: 'pebble', lead: 'edit', is: 26, fill: true }, x + 12, y + 14)];
      [['inbox', 'Inbox'], ['send', 'Sent'], ['draft', 'Drafts'], ['delete', 'Trash']].forEach(function (it, i) {
        var iy = y + 104 + i * 72, sel = i === 0;
        if (sel) els.push(Wd({ theme: th, m: 'frost', w: 56, h: 32, radius: 16, lead: it[0], is: 22, fill: true, live: false }, x + 12, iy));
        else els.push(Wd({ theme: th, m: 'none', w: 56, h: 32, radius: 16, lead: it[0], is: 22, fg: ONS[th], st: i === 2 ? 'hover' : 'rest', live: false }, x + 12, iy));
        els.push(Tx(it[1], T(12, sel ? 700 : 550, ONS[th], 'width: 80px; text-align: center;'), x, iy + 36));
      });
      els.push(Wd({ theme: th, m: 'bead', role: 'seapink', w: 24, h: 16, radius: 8, label: '12', fs: 10, fw: 700, live: false }, x + 50, y + 98));
      return els;
    }
    function sidebar(th, x, y) {
      var els = [Wd({ theme: th, m: 'milk', w: 272, h: 404, radius: 32, live: false }, x, y)];
      els.push(Tx('Mail', T(22, 720, ONS[th]), x + 24, y + 22));
      els.push(Wd({ theme: th, m: 'glass', w: 136, h: 48, lead: 'edit', fill: true, is: 20, label: 'Compose', fs: 15 }, x + 16, y + 62));
      [['inbox', 'Inbox', '12'], ['star', 'Starred', ''], ['send', 'Sent', ''], ['draft', 'Drafts', '2'], ['delete', 'Trash', '']].forEach(function (it, i) {
        var iy = y + 128 + i * 50, sel = i === 0, col = sel ? onc(th) : ONS[th];
        if (sel) els.push(Wd({ theme: th, m: 'frost', w: 248, h: 46, radius: 23, live: false }, x + 12, iy));
        els.push(Ic(it[0], 22, sel ? col : ONSV[th], sel, x + 28, iy + 12), Tx(it[1], T(15, sel ? 650 : 500, col), x + 64, iy + 13));
        if (it[2]) els.push(Tx(it[2], T(13, 650, sel ? col : ONSV[th], 'width: 40px; text-align: right;'), x + 206, iy + 14));
      });
      return els;
    }
    function scope(th, x, y) {
      var tabs = ['All', 'Unread', 'Flagged', 'Files'], xs = x + 4, els = [], ws = tabs.map(function (t) { return tw(t, 14) + 32 + (t === 'Flagged' ? 26 : 0); });
      var W0 = ws.reduce(function (a, b) { return a + b; }, 0) + 8;
      els.push(Wd({ theme: th, m: 'clear', w: W0, h: 44, live: false }, x, y));
      tabs.forEach(function (t, i) {
        if (i === 0) els.push(Wd({ theme: th, m: 'frost', w: ws[0], h: 36, radius: 18, label: t, fs: 14, live: false }, xs, y + 4));
        else els.push(Wd({ theme: th, m: 'none', w: ws[i], h: 36, label: t, fs: 14, fg: ONS[th], live: false, justify: t === 'Flagged' ? 'flex-start' : 'center', pad: t === 'Flagged' ? 16 : 0 }, xs, y + 4));
        if (t === 'Flagged') els.push(Wd({ theme: th, m: 'bead', role: 'seapink', w: 18, h: 18, shape: 'round', label: '3', fs: 10.5, fw: 700, live: false }, xs + ws[i] - 30, y + 13));
        xs += ws[i];
      });
      return els;
    }
    function dots(th, x, y, n, cur) {
      var W0 = (n - 1) * 14 + 20 + 24, els = [Wd({ theme: th, m: 'well', w: W0, h: 22, radius: 11, live: false }, x, y)], xx = x + 12;
      for (var i = 0; i < n; i++) {
        if (i === cur) { els.push(Wd({ theme: th, m: 'pearl', w: 20, h: 8, radius: 4, live: false }, xx, y + 7)); xx += 26; }
        else { els.push(Bx('width: 7px; height: 7px; border-radius: 4px; background: ' + (th === 'dark' ? 'rgba(255,255,255,0.4)' : 'rgba(255,255,255,0.85)') + '; box-shadow: 0 1px 2px rgba(0,0,0,0.2);', xx, y + 7.5)); xx += 14; }
      }
      return els;
    }

    var secA = {
      title: 'Tab bar', grade: 'Clear capsule · Frost lens',
      note: 'The tab bar floats 14 dp above the bottom edge as a clear capsule, with search in its own lens beside it. The current tab is a frost lens. Dragging turns the lens clear and stretches it between tabs, magnifying whatever is under it; on scroll the bar condenses into two lenses around an accessory capsule.',
      hasLabels: true, hasHeads: false, heads: [], cols: '120px minmax(0, 1fr)', panelCols: 'repeat(2, minmax(0, 1fr))',
      panels: panels(function (th) {
        return [
          { label: 'Rest', sub: 'clear + frost lens', h: 132, cells: [scene(th, 132, '-80px -380px', tabbar(th, 'rest', 14, 56))] },
          { label: 'Dragging', sub: 'lens turns clear\nand magnifies', h: 132, cells: [scene(th, 132, '-80px -380px', tabbar(th, 'drag', 14, 56))] },
          { label: 'Minimized', sub: 'on scroll down', h: 132, cells: [scene(th, 132, '-80px -380px', tabMin(th, 14, 62))] },
          { label: 'Focus', sub: 'keyboard', h: 132, cells: [scene(th, 132, '-80px -380px', tabbar(th, 'focus', 14, 56))] }
        ];
      })
    };
    function large(th) { return cell([Wd({ theme: th, m: 'clear', w: 44, h: 44, shape: 'lens', lead: 'arrow_back', is: 22 }, 14, 10)].concat(cluster(th, 306, 10, ['search', 'more_horiz']), [Tx('Library', T(34, 760, ONS[th], 'letter-spacing: -0.4px;'), 18, 64), Tx('2,481 photos · 38 albums', T(14, 500, ONSV[th]), 20, 108)]), { stack: true, sw: 412, sh: 132, left: true }); }
    function small(th) { return scene(th, 132, '-200px -60px', [edge(th, 412, 84), Wd({ theme: th, m: 'clear', w: 44, h: 44, shape: 'lens', lead: 'arrow_back', is: 22 }, 14, 10), Tx('Library', T(17, 680, ONS[th], 'width: 412px; text-align: center;'), 0, 21)].concat(cluster(th, 306, 10, ['search', 'more_horiz']))); }
    function selbar(th) { return scene(th, 132, '-420px -40px', [edge(th, 412, 84), Wd({ theme: th, m: 'clear', w: 44, h: 44, shape: 'lens', lead: 'close', is: 22 }, 14, 10), Tx('3 selected', T(17, 680, ONS[th], 'width: 412px; text-align: center;'), 0, 21), Wd({ theme: th, m: 'clear', w: 44, h: 44, shape: 'lens', lead: 'delete', is: 22 }, 298, 10), Wd({ theme: th, m: 'glass', w: 44, h: 44, shape: 'lens', lead: 'ios_share', is: 22 }, 352, 10)]); }
    var secB = {
      title: 'Top bars', grade: 'No fill · Clear lenses · Edge diffusion',
      note: 'Top bars have no container. Actions are clear lenses or a clear cluster; the title is plain type. Once content scrolls under, a diffusion edge blurs and lightens it, fading to nothing over 84 dp, instead of a hard bar with a divider.',
      hasLabels: true, hasHeads: false, heads: [], cols: '120px minmax(0, 1fr)', panelCols: 'repeat(2, minmax(0, 1fr))',
      panels: panels(function (th) {
        return [
          { label: 'Large title', sub: 'at top of page', h: 132, cells: [large(th)] },
          { label: 'Scrolled', sub: 'edge diffusion', h: 132, cells: [small(th)] },
          { label: 'Selection', sub: 'glass primary', h: 132, cells: [selbar(th)] }
        ];
      })
    };
    var secC = {
      title: 'Floating toolbar', grade: 'Clear capsule · Frost tool · Glass confirm',
      note: 'Tools share a clear capsule; the active tool is a frost lens and the confirm action is a separate glass lens. Pressing a tool condenses a clear lens that swells past the capsule edge.',
      hasLabels: true, hasHeads: false, heads: [], cols: '120px minmax(0, 1fr)', panelCols: 'repeat(2, minmax(0, 1fr))',
      panels: panels(function (th) {
        return [
          { label: 'Rest', h: 104, cells: [scene(th, 104, '-260px -480px', tool(th, 47, 32, false))] },
          { label: 'Pressed', sub: 'Filters', h: 104, cells: [scene(th, 104, '-260px -480px', tool(th, 47, 32, true))] }
        ];
      })
    };
    var secD = {
      title: 'Rail, sidebar and vertical toolbar', grade: 'Clear rail · Milk sidebar',
      note: 'On tablets the rail is a clear capsule floating 16 dp from the edge with the pebble action on top; the current destination is a frost lens. The expanded sidebar is content, so it is a milk pane with a frost row. Vertical tool strips are clear capsules.',
      hasLabels: false, hasHeads: false, heads: [], cols: 'minmax(0, 1fr)', panelCols: 'repeat(2, minmax(0, 1fr))',
      panels: panels(function (th) {
        var els = [phBg(th, 0, 0, 696, 436, 28, '-40px -120px', 820)].concat(rail(th, 16, 16), sidebar(th, 112, 16), vtool(th, 624, 16));
        return [{ h: 436, cells: [cell(els, { stack: true, sw: 696, sh: 436, left: true })] }];
      })
    };
    var secE = {
      title: 'Scope bar and page dots', grade: 'Clear · Frost · Well · Pearl',
      note: 'Scope bars filter a single list: a clear capsule with a frost lens on the active scope and beads for counts. Page dots sit in a small well; the current page is a stretched pearl.',
      hasLabels: false, hasHeads: false, heads: [], cols: 'minmax(0, 1fr)', panelCols: 'repeat(2, minmax(0, 1fr))',
      panels: panels(function (th) {
        return [{ h: 120, cells: [scene(th, 120, '-120px -560px', scope(th, 14, 14).concat(dots(th, 160, 84, 5, 1)))] }];
      })
    };
    return { sections: fin([secA, secB, secC, secD, secE]) };
