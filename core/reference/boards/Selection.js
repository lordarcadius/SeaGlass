    function sw(th, on, st, role) {
      role = role || 'aqua';
      var TW = 58, TH = 34, dis = st === 'disabled', els = [];
      els.push(Wd({ theme: th, m: 'well', role: role, w: TW, h: TH, radius: 17, st: st === 'press' ? 'rest' : st, live: false }, 0, 0));
      if (on) { var f = Wd({ theme: th, m: 'fill', role: role, w: TW - 4, h: TH - 4, radius: 15, live: false, dry: false, st: dis ? 'disabled' : 'rest' }, 2, 2); if (dis) f.pos += ' opacity: 0.4;'; els.push(f); }
      if (st === 'press') els.push(Wd({ theme: th, m: 'lens', w: 44, h: 38, radius: 19, live: false }, on ? TW - 42 : -2, -2));
      else els.push(Wd({ theme: th, m: 'pearl', w: 28, h: 28, shape: 'round', st: st === 'focus' ? 'rest' : st }, on ? TW - 31 : 3, 3));
      return cell(els, { stack: true, sw: TW, sh: TH });
    }
    function chk(th, kind, st) {
      var role = kind === 'error' ? 'seapink' : 'aqua', dis = st === 'disabled';
      if (kind === 'on' || kind === 'mixed') return cell([Wd({ theme: th, m: 'glass', role: role, w: 26, h: 26, radius: 9, st: st, lead: kind === 'on' ? 'check' : 'remove', is: 20, iw: 700 }, 0, 0)], { stack: true, sw: 26, sh: 26 });
      var ring = kind === 'error' && !dis && st !== 'focus' ? 'box-shadow: 0 0 0 1.5px ' + (th === 'dark' ? P.seapink.t80 : P.seapink.t60) + ';' : '';
      return cell([Wd({ theme: th, m: 'well', role: role, w: 26, h: 26, radius: 9, st: st, tint: kind === 'error', bx: ring }, 0, 0)], { stack: true, sw: 26, sh: 26 });
    }
    function radio(th, on, st) {
      if (!on) return cell([Wd({ theme: th, m: 'well', w: 26, h: 26, shape: 'round', st: st }, 0, 0)], { stack: true, sw: 26, sh: 26 });
      var dot = Wd({ theme: th, m: 'pearl', w: 10, h: 10, shape: 'round', st: st === 'disabled' ? 'disabled' : 'rest', live: false }, 8, 8);
      if (st === 'hover') dot.pos += ' translate: 0 -2px;';
      if (st === 'press') dot.pos += ' scale: 0.96;';
      return cell([Wd({ theme: th, m: 'glass', w: 26, h: 26, shape: 'round', st: st }, 0, 0), dot], { stack: true, sw: 26, sh: 26 });
    }
    function seg(th, items, segW, sel, mode, hov) {
      var n = items.length, TW = n * segW + 8, dis = mode === 'disabled', els = [];
      els.push(Wd({ theme: th, m: 'well', w: TW, h: 40, radius: 20, st: mode === 'focus' ? 'focus' : (dis ? 'disabled' : 'rest'), live: false }, 0, 0));
      if (mode === 'press') els.push(Wd({ theme: th, m: 'lens', w: segW + 14, h: 46, radius: 23, live: false }, 4 + sel * segW - 7, -3));
      else els.push(Wd({ theme: th, m: 'frost', role: 'aqua', w: segW, h: 32, radius: 16, st: dis ? 'disabled' : 'rest', live: false }, 4 + sel * segW, 4));
      items.forEach(function (it, i) {
        var on = i === sel && mode !== 'press' && !dis;
        var col = dis ? ca(ONS[th], 0.38) : (on ? onc(th) : (i === sel ? ONS[th] : ONSV[th]));
        var big = mode === 'press' && i === sel;
        els.push(Wd({ theme: th, m: 'none', w: segW, h: 32, lead: it[0], fill: on, is: big ? 21 : 18, label: it[1], fs: big ? 15.5 : 14, gap: 6, fg: col, st: hov === i ? 'hover' : 'rest', live: false }, 4 + i * segW, 4));
      });
      return cell(els, { stack: true, sw: TW, sh: 40, left: true });
    }
    var bead = 'background: radial-gradient(120% 120% at 30% 10%, #A87ED5, #734B9E 72%); box-shadow: inset 0 1px 1px rgba(255,255,255,0.5), inset 0 -1.5px 2px rgba(44,6,72,0.3);';
    function chip(th, kind, st) {
      var o;
      if (kind === 'off') o = { theme: th, m: 'well', w: 28 + tw('Low tide', 14), h: 34, label: 'Low tide', fs: 14, pad: 14, fg: ONSV[th] };
      else if (kind === 'on') o = { theme: th, m: 'glass', w: 26 + 24 + tw('Low tide', 14), h: 34, lead: 'check', is: 18, iw: 700, label: 'Low tide', fs: 14, gap: 6, pad: '0 14px 0 12px' };
      else if (kind === 'input') o = { theme: th, m: 'frost', w: 4 + 26 + 16 + tw('Maya Ortiz', 14) + 22 + 6, h: 34, justify: 'flex-start', gap: 0, pad: '0 6px 0 4px', kids: [
        { w: 26, h: 26, r: '50%', ind: bead, label: 'M', ls: 'font-size: 12px; font-weight: 700;', lc: '#FFFFFF' },
        { label: 'Maya Ortiz', ls: 'font-size: 14px; font-weight: 600;', x: 'margin: 0 8px;' },
        { w: 22, h: 22, r: '50%', ind: th === 'dark' ? 'background: rgba(255,255,255,0.14); box-shadow: inset 0 1px 0 rgba(255,255,255,0.25);' : 'background: rgba(255,255,255,0.6); box-shadow: inset 0 1px 0 #FFFFFF;', icon: 'close', is: 15 }
      ] };
      else o = { theme: th, m: 'clear', w: 26 + 24 + tw('Summarize', 14), h: 34, lead: 'auto_awesome', is: 18, ic: st === 'disabled' ? null : acc(th), fill: true, label: 'Summarize', fs: 14, gap: 6, pad: '0 14px 0 12px' };
      o.st = st;
      return cell([Wd(o)]);
    }
    function rail(th, val, o) {
      o = o || {};
      var L = 300, x0 = 10, y0 = o.drag ? 60 : 26, els = [], dis = o.st === 'disabled';
      els.push(Wd({ theme: th, m: 'well', w: L, h: 10, radius: 5, st: dis ? 'disabled' : 'rest', live: false }, x0, y0 - 5));
      var a = o.range ? o.range[0] : 0, b = o.range ? o.range[1] : val;
      var f = Wd({ theme: th, m: 'fill', w: Math.max(10, Math.round((b - a) * L)), h: 10, radius: 5, live: false, dry: false, st: dis ? 'disabled' : 'rest' }, x0 + Math.round(a * L), y0 - 5);
      if (dis) f.pos += ' opacity: 0.38;';
      els.push(f);
      if (o.steps) for (var i = 0; i <= o.steps; i++) { var sx = x0 + Math.round(i * L / o.steps) + (i === 0 ? 3 : (i === o.steps ? -3 : 0)); els.push(Bx('width: 4px; height: 4px; border-radius: 2px; background: ' + (i / o.steps <= b + 0.001 ? wa(0.9) : ca(ONS[th], 0.3)) + ';', sx - 2, y0 - 2)); }
      var th2 = o.range ? [a, b] : [val];
      th2.forEach(function (t, i) {
        var cx = x0 + Math.round(t * L);
        if (o.drag && i === th2.length - 1) {
          els.push(Wd({ theme: th, m: 'lens', w: 54, h: 38, radius: 19, live: false }, cx - 27, y0 - 19));
          els.push(Wd({ theme: th, m: 'ink', w: 48, h: 30, radius: 15, label: String(Math.round(t * 100)), fs: 14, live: false }, cx - 24, y0 - 60));
        } else els.push(Wd({ theme: th, m: 'pearl', w: 34, h: 22, radius: 11, st: dis ? 'disabled' : (o.focus && i === 0 ? 'focus' : 'rest') }, cx - 17, y0 - 11));
      });
      return cell(els, { stack: true, sw: 320, sh: y0 + 26, left: true });
    }
    function pill(th, val, vert, st, ic) {
      var w = vert ? 64 : 300, h = vert ? 168 : 48, r = vert ? 28 : 24, els = [];
      els.push(Wd({ theme: th, m: 'well', w: w, h: h, radius: r, st: st === 'focus' ? 'focus' : 'rest', live: false }, 0, 0));
      if (vert) { var fh = Math.round(h * val); els.push(Wd({ theme: th, m: 'fill', w: w, h: fh, radius: r, live: false }, 0, h - fh)); els.push(Ic(ic, 26, onFill(th), true, 19, h - 42)); }
      else { var fw = Math.round(w * val); els.push(Wd({ theme: th, m: 'fill', w: fw, h: h, radius: r, live: false }, 0, 0)); els.push(Ic(ic, 22, onFill(th), true, 16, 13)); els.push(Tx(Math.round(val * 100) + '%', T(13, 650, ONS[th], 'width: 60px; text-align: right;'), w - 76, 15)); }
      if (st === 'press') els[els.length - (vert ? 2 : 3)].pos += ' scale: 1.02;';
      return { els: els, w: w, h: h };
    }
    function stepper(th, press) {
      var els = [Wd({ theme: th, m: 'clear', w: 136, h: 44, live: false }, 0, 0), Wd({ theme: th, m: 'plain', w: 44, h: 44, shape: 'lens', lead: 'remove', is: 22, fg: ONS[th], live: false }, 0, 0), Tx('4', T(17, 650, ONS[th], 'width: 48px; text-align: center;'), 44, 11)];
      els.push(Wd({ theme: th, m: 'plain', w: 44, h: 44, shape: 'lens', lead: 'add', is: 22, fg: ONS[th], st: press ? 'press' : 'rest', pressScale: 1.1, live: false }, 92, 0));
      return els;
    }

    var SWS = { rest: 'well · pearl knob', hover: 'knob lifts', focus: 'halo on track', press: 'knob → clear lens', disabled: 'dry glass' };
    var secA = {
      title: 'Switch', grade: 'Well · Fill · Pearl',
      note: 'A sunken well holds a pearl. Turning on pours lit liquid into the well. While the finger is down the pearl swells into a clear lens that magnifies the liquid beneath it, then settles back on release with spatial.fast.',
      hasLabels: true, hasHeads: true, heads: ['', 'Off', 'On', 'On · seafoam'], cols: '110px repeat(3, minmax(0, 1fr))', panelCols: 'repeat(2, minmax(0, 1fr))',
      panels: panels(function (th) { return ST.map(function (st) { return { label: STN[st], sub: SWS[st], h: 46, cells: [sw(th, false, st), sw(th, true, st), sw(th, true, st, 'seafoam')] }; }); })
    };
    var secB = {
      title: 'Check and radio', grade: 'Well → Glass',
      note: 'Unchecked controls are wells; checking lifts them into glass with a heavy check. Checks use a 9 dp radius on 26 dp so they read as soft squares, never sharp. A selected radio is a glass lens holding a pearl.',
      hasLabels: true, hasHeads: true, heads: ['', 'Off', 'On', 'Mixed', 'Error', 'Radio off', 'Radio on'], cols: '90px repeat(6, minmax(0, 1fr))', panelCols: 'repeat(2, minmax(0, 1fr))',
      panels: panels(function (th) { return ST.map(function (st) { return { label: STN[st], h: 40, cells: [chk(th, 'off', st), chk(th, 'on', st), chk(th, 'mixed', st), chk(th, 'error', st), radio(th, false, st), radio(th, true, st)] }; }); })
    };
    var TXT = [[null, 'Day'], [null, 'Week'], [null, 'Month'], [null, 'Year']], ICT = [['photo', 'Photos'], ['photo_album', 'Albums'], ['place', 'Places']], ICO = [['grid_view', null], ['view_agenda', null], ['view_list', null], ['map', null]];
    var secC = {
      title: 'Segmented control', grade: 'Well + Frost lens',
      note: 'Options sit in one shared well; the chosen one is a frost lens. Dragging or pressing turns the lens clear and lets it bulge past the well edge, magnifying the label, so the selection feels like a droplet sliding along a groove.',
      hasLabels: true, hasHeads: false, heads: [], cols: '110px minmax(0, 1fr)', panelCols: 'repeat(2, minmax(0, 1fr))',
      panels: panels(function (th) {
        return [
          { label: 'Text', h: 46, cells: [seg(th, TXT, 80, 1, 'rest')] },
          { label: 'Icon + text', h: 46, cells: [seg(th, ICT, 108, 0, 'rest')] },
          { label: 'Icons', h: 46, cells: [seg(th, ICO, 56, 0, 'rest')] },
          { label: 'Hover', sub: 'unselected segment', h: 46, cells: [seg(th, TXT, 80, 1, 'rest', 2)] },
          { label: 'Focus', h: 46, cells: [seg(th, TXT, 80, 1, 'focus')] },
          { label: 'Press', sub: 'lens magnifies', h: 50, cells: [seg(th, TXT, 80, 2, 'press')] },
          { label: 'Disabled', h: 46, cells: [seg(th, TXT, 80, 1, 'disabled')] }
        ];
      })
    };
    var CH = [['off', 'Filter · off', 'well'], ['on', 'Filter · on', 'glass'], ['input', 'Input', 'frost · avatar bead'], ['suggest', 'Suggestion', 'clear']];
    var secD = {
      title: 'Chips', grade: 'Well · Glass · Frost · Clear',
      note: 'Filter chips follow selection-lifts: a sunken well when off, tinted glass with a check when on. Input chips are frost capsules with an avatar bead and a small clear remove lens. Suggestion chips are clear glass with an accent symbol.',
      hasLabels: true, hasHeads: true, heads: ['', 'Rest', 'Hover', 'Focus', 'Press', 'Disabled'], cols: '150px repeat(5, minmax(0, 1fr))', panelCols: 'minmax(0, 1fr)',
      panels: panels(function (th) { return CH.map(function (c) { return { label: c[1], sub: c[2], h: 44, cells: ST.map(function (st) { return chip(th, c[0], st); }) }; }); })
    };
    var secE = {
      title: 'Sliders and stepper', grade: 'Well · Fill · Pearl → Clear lens',
      note: 'The track is a 10 dp well filled with lit liquid. The thumb is a pearl capsule at rest and becomes a clear lens with an ink value bubble while dragged. Pill sliders put the symbol inside the liquid for volume and brightness; the stepper is a clear capsule whose pressed side condenses a lens.',
      hasLabels: true, hasHeads: false, heads: [], cols: '110px minmax(0, 1fr)', panelCols: 'repeat(2, minmax(0, 1fr))',
      panels: panels(function (th) {
        var ph = pill(th, 0.62, false, 'rest', 'volume_up'), pv = pill(th, 0.62, true, 'rest', 'light_mode'), pv2 = pill(th, 0.34, true, 'focus', 'volume_up');
        return [
          { label: 'Rail', sub: 'well 10 · pearl 34×22', h: 52, cells: [rail(th, 0.62)] },
          { label: 'Focus', h: 52, cells: [rail(th, 0.62, { focus: true })] },
          { label: 'Dragging', sub: 'clear lens + ink bubble', h: 86, cells: [rail(th, 0.62, { drag: true })] },
          { label: 'Range', h: 52, cells: [rail(th, 0, { range: [0.24, 0.7] })] },
          { label: 'Stepped', sub: '5 stops', h: 52, cells: [rail(th, 0.6, { steps: 5 })] },
          { label: 'Disabled', h: 52, cells: [rail(th, 0.62, { st: 'disabled' })] },
          { label: 'Pill', sub: 'volume · brightness', h: 168, cells: [cell(ph.els.concat(shift(pv.els, 324, 0), shift(pv2.els, 408, 0)), { stack: true, sw: 472, sh: 168, left: true })] },
          { label: 'Stepper', sub: 'rest · pressed +', h: 48, cells: [cell(stepper(th, false).concat(shift(stepper(th, true), 160, 0)), { stack: true, sw: 300, sh: 44, left: true })] }
        ];
      })
    };
    return { sections: fin([secA, secB, secC, secD, secE]) };
