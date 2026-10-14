    var SZ = { S: { h: 36, pad: 14, fs: 14, is: 18, gap: 6 }, M: { h: 44, pad: 18, fs: 15, is: 20, gap: 8 }, L: { h: 56, pad: 24, fs: 17, is: 22, gap: 10 }, XL: { h: 72, pad: 30, fs: 20, is: 26, gap: 12 } };
    var STS = { rest: 'enabled', hover: 'lift 2dp · light +', focus: 'halo, no outline', press: 'scale 0.96 · bloom', disabled: 'dry glass · 38%' };
    function btn(th, m, role, sz, st, label, lead, x) {
      var s = SZ[sz], w = s.pad * 2 + (lead ? s.is + s.gap : 0) + tw(label, s.fs);
      var o = { theme: th, m: m, role: role, w: w, h: s.h, st: st, label: label, lead: lead, is: s.is, fs: s.fs, gap: s.gap, pad: s.pad, fill: m === 'glass' || m === 'frost' };
      if (m === 'clear' || m === 'plain') o.fg = acc(th, role);
      return ext(o, x);
    }
    function lens(th, m, role, size, st, ic, x) {
      var o = { theme: th, m: m, role: role, w: size, h: size, shape: 'lens', st: st, lead: ic, is: Math.round(size * 0.48), fill: m === 'glass' };
      if (m === 'plain') o.fg = ONSV[th];
      return ext(o, x);
    }

    var STY = [['glass', 'aqua', 'Send', 'send'], ['frost', 'aqua', 'Save', 'bookmark'], ['clear', 'aqua', 'Share', 'ios_share'], ['plain', 'aqua', 'Later', 'schedule'], ['glass', 'seapink', 'Delete', 'delete']];
    var secA = {
      title: 'Styles and states', grade: 'Glass · Frost · Clear · Plain · Destructive',
      note: 'Glass is the one primary action in a view. Frost is secondary: same tint, less light. Clear is neutral glass with an accent label, for actions over imagery and inside floating bars. Plain has no material at rest; when pressed a clear lens condenses under the finger. Destructive is Glass in the sea pink role.',
      hasLabels: true, hasHeads: true, heads: ['', 'Glass', 'Frost', 'Clear', 'Plain', 'Destructive'], cols: '150px repeat(5, minmax(0, 1fr))', panelCols: 'minmax(0, 1fr)',
      panels: panels(function (th) { return ST.map(function (st) { return { label: STN[st], sub: STS[st], h: 60, cells: STY.map(function (v) { return cell([Wd(btn(th, v[0], v[1], 'M', st, v[2], v[3]))]); }) }; }); })
    };

    var SIZES = ['S', 'M', 'L', 'XL'], SUB = { S: 'h 36 · 14 sp', M: 'h 44 · 15 sp', L: 'h 56 · 17 sp', XL: 'h 72 · 20 sp' }, RH = { S: 48, M: 56, L: 68, XL: 86 };
    var secB = {
      title: 'Sizes and silhouettes', grade: 'Sea Glass',
      note: 'Capsules for labelled buttons, lenses for icon-only buttons, pebbles for the single most expressive action. Every size keeps a 48 dp touch target. There is no square button; the tightest corner is the 12 dp seam of a split button.',
      hasLabels: true, hasHeads: true, heads: ['', 'Capsule', 'Lens', 'Pebble'], cols: '110px repeat(3, minmax(0, 1fr))', panelCols: 'repeat(2, minmax(0, 1fr))',
      panels: panels(function (th) {
        return SIZES.map(function (sz) {
          var s = SZ[sz];
          return { label: sz, sub: SUB[sz], h: RH[sz], cells: [
            cell([Wd(btn(th, 'glass', 'aqua', sz, 'rest', 'Send', 'send'))]),
            cell([Wd(lens(th, 'glass', 'aqua', s.h, 'rest', 'send'))]),
            cell([Wd(lens(th, 'glass', 'seafoam', s.h, 'rest', 'add', { shape: 'pebble', is: Math.round(s.h * 0.52) }))])
          ] };
        });
      })
    };

    var ICS = [['glass', 'aqua', 'favorite'], ['frost', 'aqua', 'bookmark'], ['clear', 'aqua', 'more_horiz'], ['plain', 'aqua', 'close']];
    var secC = {
      title: 'Icon buttons and toggles', grade: 'Lens',
      note: 'Icon buttons are 48 dp lenses. A toggle starts as clear glass with an outlined symbol and becomes tinted glass with a filled symbol when on, so selection reads through both colour and form.',
      hasLabels: true, hasHeads: true, heads: ['', 'Glass', 'Frost', 'Clear', 'Plain', 'Off', 'On'], cols: '90px repeat(6, minmax(0, 1fr))', panelCols: 'repeat(2, minmax(0, 1fr))',
      panels: panels(function (th) {
        return ST.map(function (st) {
          var cs = ICS.map(function (v) { return cell([Wd(lens(th, v[0], v[1], 48, st, v[2]))]); });
          cs.push(cell([Wd(lens(th, 'clear', 'seapink', 48, st, 'favorite', { fill: false }))]));
          cs.push(cell([Wd(lens(th, 'glass', 'seapink', 48, st, 'favorite', { fill: true }))]));
          return { label: STN[st], h: 62, cells: cs };
        });
      })
    };

    function cluster(th, x, y, icons, sel, press) {
      var n = icons.length, w = 44 * n + 4, els = [Wd({ theme: th, m: 'clear', w: w, h: 48, live: false }, x, y)];
      icons.forEach(function (ic, i) {
        var ix = x + 4 + i * 44, iy = y + 4;
        if (i === sel) els.push(Wd({ theme: th, m: 'frost', role: 'aqua', w: 40, h: 40, shape: 'lens', lead: ic, is: 22, fill: true }, ix, iy));
        else if (i === press) els.push(Wd({ theme: th, m: 'plain', st: 'press', w: 48, h: 48, shape: 'lens', lead: ic, is: 26, fg: ONS[th], pressScale: 1 }, ix - 4, iy - 4));
        else els.push(Ic(ic, 22, ONS[th], false, ix + 9, iy + 9));
      });
      return els;
    }
    function split(th, m, role, x, y, open, st) {
      var fg = m === 'clear' ? acc(th, role) : null;
      return [
        Wd({ theme: th, m: m, role: role, w: 106, h: 44, rad: '22px 12px 12px 22px', label: 'Reply', lead: 'reply', is: 20, fs: 15, gap: 6, pad: '0 14px 0 16px', fg: fg, st: st }, x, y),
        Wd({ theme: th, m: m, role: role, w: open ? 44 : 40, h: 44, rad: open ? '22px' : '12px 22px 22px 12px', lead: open ? 'expand_less' : 'expand_more', is: 22, fg: fg, st: open ? 'focus' : 'rest' }, x + 109, y)
      ];
    }
    var secD = {
      title: 'Clusters, split buttons and pairs', grade: 'Clear holder · Glass action',
      note: 'Related actions share one clear capsule; the active one is a frost lens inside it, and pressing another condenses a clear lens that bulges past the edge. Split buttons are two pieces of the same glass with a 3 dp seam and 12 dp inner corners; opening rounds the trailing piece into a lens and drops a milk menu.',
      hasLabels: true, hasHeads: false, heads: [], cols: '110px minmax(0, 1fr)', panelCols: 'repeat(2, minmax(0, 1fr))',
      panels: panels(function (th) {
        var c1 = [phBg(th, 0, 0, 580, 92, 24, '-40px -300px')].concat(cluster(th, 20, 22, ['format_bold', 'format_italic', 'format_underlined'], 0, -1), cluster(th, 184, 22, ['undo', 'redo'], -1, 1), [Wd({ theme: th, m: 'clear', w: 48, h: 48, shape: 'lens', lead: 'more_horiz', is: 24 }, 300, 22)]);
        var c2 = [phBg(th, 0, 0, 580, 92, 24, '-300px -420px')].concat(split(th, 'glass', 'aqua', 20, 24, false), split(th, 'frost', 'aqua', 200, 24, false), split(th, 'clear', 'aqua', 380, 24, false));
        var mn = Menu(th, 20, 76, 214, [{ icon: 'reply_all', label: 'Reply all' }, { icon: 'forward', label: 'Forward', hl: true }, { icon: 'schedule', label: 'Reply later' }]);
        var c3 = [phBg(th, 0, 0, 580, 232, 24, '-120px -40px')].concat(split(th, 'glass', 'aqua', 20, 20, true), mn.els);
        var c4 = [Wd(btn(th, 'plain', 'aqua', 'M', 'rest', 'Not now')), Wd(btn(th, 'glass', 'aqua', 'M', 'rest', 'Allow')), Bx('width: 24px;'), Wd(btn(th, 'clear', 'aqua', 'M', 'rest', 'Cancel')), Wd(btn(th, 'glass', 'seapink', 'M', 'rest', 'Delete', 'delete'))];
        var c5 = [Wd(btn(th, 'glass', 'aqua', 'L', 'rest', 'Continue', null, { w: 300 }), 0, 0), Wd(btn(th, 'clear', 'aqua', 'L', 'rest', 'Maybe later', null, { w: 300, h: 50 }), 0, 66)];
        return [
          { label: 'Cluster', sub: 'clear holder\nfrost lens = active', h: 92, cells: [cell(c1, { stack: true, sw: 580, sh: 92, left: true })] },
          { label: 'Split', sub: 'glass · frost · clear', h: 92, cells: [cell(c2, { stack: true, sw: 580, sh: 92, left: true })] },
          { label: 'Split · open', sub: 'lens + milk menu', h: 232, cells: [cell(c3, { stack: true, sw: 580, sh: 232, left: true })] },
          { label: 'Pairs', sub: 'dismiss + confirm', h: 56, cells: [cell(c4, { justify: 'flex-start' })] },
          { label: 'Stacked', sub: 'sheet and dialog footer', h: 118, cells: [cell(c5, { stack: true, sw: 300, sh: 118, left: true })] }
        ];
      })
    };
    return { sections: fin([secA, secB, secC, secD]) };
