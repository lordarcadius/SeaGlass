    function line(th, txt, col, caret, w, fs) {
      var kids = [{ label: txt, ls: T(fs || 16, 450, col) }];
      if (caret) kids.push({ w: 2, h: 22, r: '1px', ind: 'background: ' + acc(th) + ';' });
      return { theme: th, m: 'none', w: w, h: 24, justify: 'flex-start', gap: 1, kids: kids, live: false };
    }
    function field(th, kind, o) {
      o = o || {};
      var FW = o.w || 320, FH = 52, els = [], dis = kind === 'disabled', err = kind === 'error', foc = kind === 'focus';
      var role = err ? 'seapink' : 'aqua', pink = acc(th, 'seapink');
      var lbl = dis ? ca(ONS[th], 0.38) : (err ? pink : (foc ? acc(th) : ONSV[th]));
      els.push(Tx(o.label || 'Email', T(13, 600, lbl), 6, 0));
      var st = foc ? 'focus' : (kind === 'hover' ? 'hover' : (dis ? 'disabled' : 'rest'));
      els.push(Wd({ theme: th, m: 'well', role: role, w: FW, h: FH, radius: 18, st: st, tint: foc || err, live: false, bx: err ? 'box-shadow: 0 0 0 1.5px ' + (th === 'dark' ? P.seapink.t80 : P.seapink.t60) + ';' : '' }, 0, 22));
      els.push(Ic(o.icon || 'mail', 20, dis ? ca(ONS[th], 0.38) : (err ? pink : ONSV[th]), false, 16, 38));
      var val = kind === 'empty' || kind === 'hover' ? null : (o.value || (err ? 'maya@tidepool' : 'maya@tidepool.co'));
      var tcol = dis ? ca(ONS[th], 0.38) : (val ? ONS[th] : ONSV[th]);
      els.push(Wd(line(th, val || 'name@example.com', tcol, foc, FW - 96), 48, 36));
      if (kind === 'filled' || foc) els.push(Wd({ theme: th, m: 'clear', w: 26, h: 26, shape: 'round', lead: 'close', is: 16, fg: ONSV[th] }, FW - 39, 35));
      if (err) els.push(Ic('error', 20, pink, true, FW - 36, 38));
      var sup = err ? 'Add the domain, like .co or .com' : 'Only used for sign-in';
      els.push(Tx(sup, T(12.5, 500, dis ? ca(ONS[th], 0.38) : (err ? pink : ca(ONSV[th], 0.85))), 6, 80));
      return cell(els, { stack: true, sw: FW, sh: 98, left: true });
    }
    function area(th) {
      var els = [Tx('Note', T(13, 600, ONSV[th]), 6, 0), Wd({ theme: th, m: 'well', w: 360, h: 128, radius: 22, live: false }, 0, 22)];
      els.push(Tx('Low tide at 6:40. Meet by the north rocks and bring the blue jar for the small pieces.', T(16, 450, ONS[th], 'white-space: normal; width: 320px; line-height: 1.45;'), 18, 36));
      els.push(Tx('86 / 280', T(12, 600, ca(ONSV[th], 0.8), 'width: 80px; text-align: right;'), 262, 124));
      els.push(Tx('Shown to everyone on the walk', T(12.5, 500, ca(ONSV[th], 0.85)), 6, 156));
      return cell(els, { stack: true, sw: 360, sh: 176, left: true });
    }
    function search(th, foc) {
      var els = [Wd({ theme: th, m: 'well', w: 340, h: 46, radius: 23, st: foc ? 'focus' : 'rest', tint: foc, live: false }, 0, 0), Ic('search', 22, ONSV[th], false, 14, 12)];
      if (foc) {
        els.push(Wd(line(th, 'tide', ONS[th], true, 220), 46, 11));
        els.push(Wd({ theme: th, m: 'clear', w: 26, h: 26, shape: 'round', lead: 'close', is: 16, fg: ONSV[th] }, 304, 10));
        els.push(Wd({ theme: th, m: 'plain', w: 84, h: 44, label: 'Cancel', fs: 15, fg: acc(th) }, 352, 1));
      } else {
        els.push(Tx('Search mail', T(16, 450, ONSV[th]), 46, 13));
        els.push(Ic('mic', 22, ONSV[th], false, 304, 12));
      }
      return cell(els, { stack: true, sw: 440, sh: 46, left: true });
    }
    function floating(th) {
      var els = [phBg(th, 0, 0, 570, 112, 24, '-60px -330px'), Wd({ theme: th, m: 'clear', w: 380, h: 52, live: false }, 20, 40)];
      els.push(Ic('search', 22, ONS[th], false, 38, 55), Tx('Search photos', T(16, 500, ONSV[th]), 70, 54), Ic('mic', 22, ONS[th], false, 364, 55));
      els.push(Wd({ theme: th, m: 'clear', w: 52, h: 52, shape: 'lens', lead: 'tune', is: 24 }, 412, 40));
      return cell(els, { stack: true, sw: 570, sh: 112, left: true });
    }
    function prompt(th) {
      var els = [phBg(th, 0, 0, 570, 132, 24, '-240px -120px')];
      els.push(Wd({ theme: th, m: 'clear', w: 176, h: 40, live: false }, 20, 10), Ph(26, 16, 28, 28, { r: 14, size: 260, bp: '-36px -150px' }), Tx('Shore walk.jpg', T(13, 600, ONS[th]), 62, 20), Ic('close', 16, ONSV[th], false, 168, 22));
      els.push(Wd({ theme: th, m: 'clear', w: 460, h: 60, live: false }, 20, 60));
      els.push(Ic('add', 24, ONS[th], false, 38, 78), Tx('Ask about this photo', T(16, 450, ONSV[th]), 74, 79), Ic('mic', 22, ONS[th], false, 372, 79));
      els.push(Wd({ theme: th, m: 'glass', w: 44, h: 44, shape: 'lens', lead: 'arrow_upward', is: 24, iw: 600 }, 428, 68));
      return cell(els, { stack: true, sw: 570, sh: 132, left: true });
    }
    function listening(th) {
      var els = [phBg(th, 0, 0, 570, 100, 24, '-420px -260px'), Wd({ theme: th, m: 'glass', role: 'lavender', w: 460, h: 60, live: false }, 20, 20)];
      var fg = onc(th, 'lavender'), hs = [8, 14, 22, 30, 18, 26, 36, 24, 14, 20, 32, 22, 12, 18, 26, 16, 10, 6];
      els.push(Ic('close', 22, fg, false, 38, 39));
      hs.forEach(function (h, i) { els.push(Bx('width: 4px; height: ' + h + 'px; border-radius: 2px; background: ' + ca(fg, 0.85) + ';', 82 + i * 8, 50 - h / 2)); });
      els.push(Tx('Listening', T(15, 600, fg), 236, 40));
      els.push(Wd({ theme: th, m: 'pearl', w: 44, h: 44, shape: 'lens', lead: 'stop', fill: true, is: 22, fg: P.lavender.t40 }, 428, 28));
      return cell(els, { stack: true, sw: 570, sh: 100, left: true });
    }
    function datePicker(th, range) {
      var PW = 340, PH = 384, x0 = 16, cw = 44, els = [Wd({ theme: th, m: 'milk', w: PW, h: PH, radius: 32, live: false }, 0, 0)];
      els.push(Tx('October 2026', T(17, 650, ONS[th]), 24, 25), Ic('expand_more', 20, ONSV[th], false, 140, 27));
      els.push(Wd({ theme: th, m: 'plain', w: 36, h: 36, shape: 'lens', lead: 'chevron_left', is: 22, fg: ONS[th] }, PW - 100, 18), Wd({ theme: th, m: 'plain', w: 36, h: 36, shape: 'lens', lead: 'chevron_right', is: 22, fg: ONS[th] }, PW - 60, 18));
      ['S', 'M', 'T', 'W', 'T', 'F', 'S'].forEach(function (d, i) { els.push(Tx(d, T(12.5, 650, ca(ONSV[th], 0.8), 'width: 44px; text-align: center;'), x0 + i * cw, 66)); });
      function pos(d) { var k = 4 + d - 1; return [x0 + (k % 7) * cw, 88 + Math.floor(k / 7) * 44]; }
      if (range) { var a = pos(20), b = pos(24); els.push(Wd({ theme: th, m: 'well', role: 'aqua', tint: true, w: b[0] - a[0] + 40, h: 40, radius: 20, live: false }, a[0] + 2, a[1] + 2)); }
      for (var d = 1; d <= 31; d++) {
        var p = pos(d), sel = range ? (d === 20 || d === 24) : d === 16, today = d === 13;
        if (sel) { els.push(Wd({ theme: th, m: 'glass', w: 40, h: 40, shape: 'lens', label: String(d), fs: 15, fw: 650 }, p[0] + 2, p[1] + 2)); continue; }
        var inR = range && d > 20 && d < 24;
        els.push(Tx(String(d), T(15, today ? 750 : (inR ? 600 : 450), today ? acc(th) : ONS[th], 'width: 44px; text-align: center;'), p[0], p[1] + 12));
        if (today) els.push(Bx('width: 5px; height: 5px; border-radius: 3px; background: ' + acc(th) + ';', p[0] + 20, p[1] + 34));
      }
      els.push(Wd({ theme: th, m: 'plain', w: 84, h: 40, label: 'Cancel', fs: 15, fg: acc(th) }, PW - 196, 326), Wd({ theme: th, m: 'glass', w: 84, h: 40, label: 'Done', fs: 15 }, PW - 104, 326));
      return els;
    }
    function wheel(th) {
      var PW = 340, PH = 232, els = [Wd({ theme: th, m: 'milk', w: PW, h: PH, radius: 32, live: false }, 0, 0)];
      els.push(Wd({ theme: th, m: 'frost', w: PW - 40, h: 40, radius: 20, live: false }, 20, 96));
      var cols = [[90, ['5', '6', '7', '8', '9']], [170, ['20', '25', '30', '35', '40']], [250, ['', '', 'AM', 'PM', '']]];
      cols.forEach(function (c) {
        c[1].forEach(function (v, i) {
          var k = i - 2, ak = Math.abs(k);
          var col = k === 0 ? onc(th) : ca(ONSV[th], ak === 1 ? 0.75 : 0.38);
          var fs = k === 0 ? 22 : (ak === 1 ? 18 : 15);
          els.push(Tx(v, T(fs, k === 0 ? 650 : 500, col, 'width: 64px; text-align: center; line-height: 1;'), c[0] - 32, 116 + k * 40 - fs / 2));
        });
      });
      els.push(Tx(':', T(22, 650, onc(th), 'line-height: 1;'), 127, 104));
      return els;
    }

    var FS = [['empty', 'Empty'], ['hover', 'Hover'], ['focus', 'Focus'], ['filled', 'Filled'], ['error', 'Error'], ['disabled', 'Disabled']];
    var FSS = { empty: 'well · label above', hover: 'well darkens 7%', focus: 'tinted well + halo', filled: 'clear lens to clear', error: 'sea pink tint + ring', disabled: 'dry glass' };
    var secA = {
      title: 'Text fields', grade: 'Well',
      note: 'A field is an 18 dp well 52 dp tall, with its label outside and above so nothing floats or overlaps. Focus tints the well with the accent and adds the halo; error tints it sea pink with a 1.5 dp ring and swaps the supporting text. The clear button is a 26 dp clear lens.',
      hasLabels: true, hasHeads: false, heads: [], cols: '130px minmax(0, 1fr)', panelCols: 'repeat(2, minmax(0, 1fr))',
      panels: panels(function (th) {
        var rows = FS.map(function (f) { return { label: f[1], sub: FSS[f[0]], h: 98, cells: [field(th, f[0])] }; });
        rows.push({ label: 'Text area', sub: 'well · radius 22', h: 176, cells: [area(th)] });
        return rows;
      })
    };
    var secB = {
      title: 'Search and prompt', grade: 'Well in content · Clear when floating',
      note: 'Search inside content is a well. Search that floats over content is a clear capsule with its own lens beside it. The prompt bar is the same clear capsule with a glass send lens and an attachment capsule above it; while listening, the whole bar turns to tinted glass with a live waveform.',
      hasLabels: true, hasHeads: false, heads: [], cols: '130px minmax(0, 1fr)', panelCols: 'repeat(2, minmax(0, 1fr))',
      panels: panels(function (th) {
        return [
          { label: 'Search', sub: 'well', h: 46, cells: [search(th, false)] },
          { label: 'Search · focus', h: 46, cells: [search(th, true)] },
          { label: 'Floating', sub: 'clear + lens', h: 112, cells: [floating(th)] },
          { label: 'Prompt bar', sub: 'clear + glass send', h: 132, cells: [prompt(th)] },
          { label: 'Listening', sub: 'lavender glass', h: 100, cells: [listening(th)] }
        ];
      })
    };
    var secC = {
      title: 'Pickers', grade: 'Milk pane · Glass lens · Frost band',
      note: 'Pickers are milk panes with 32 dp corners. The chosen date is a glass lens; today is an accent numeral with a bead. A range is a tinted well joining two lenses. The time wheel has a frost band, with values fading and shrinking as they turn away from it.',
      hasLabels: false, hasHeads: false, heads: [], cols: 'minmax(0, 1fr)', panelCols: 'repeat(2, minmax(0, 1fr))',
      panels: panels(function (th) {
        var chips = [Wd({ theme: th, m: 'clear', w: 150, h: 40, lead: 'event', is: 20, ic: acc(th), label: 'Fri, Oct 16', fs: 15, gap: 8 }, 0, 0), Wd({ theme: th, m: 'clear', w: 124, h: 40, lead: 'schedule', is: 20, ic: acc(th), label: '7:30 AM', fs: 15, gap: 8 }, 0, 52), Wd({ theme: th, m: 'clear', w: 124, h: 40, lead: 'schedule', is: 20, ic: acc(th), label: '7:30 AM', fs: 15, gap: 8, st: 'focus' }, 0, 104)];
        return [
          { h: 384, cells: [cell(datePicker(th, false).concat(shift(datePicker(th, true), 356, 0)), { stack: true, sw: 696, sh: 384, left: true })] },
          { h: 232, cells: [cell(wheel(th).concat(shift(chips, 372, 40), [Tx('Compact pickers are clear capsules that open the pane.', T(12.5, 500, ca(ONSV[th], 0.9), 'white-space: normal; width: 260px;'), 372, 196)]), { stack: true, sw: 696, sh: 232, left: true })] }
        ];
      })
    };
    return { sections: fin([secA, secB, secC]) };
