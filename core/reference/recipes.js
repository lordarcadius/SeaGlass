  kit() {
    var P = {
      seafoam:    { name: 'Seafoam', on: '#002115', t20: '#003826', t30: '#005138', t40: '#006C4C', t60: '#3EA17C', t70: '#5BBD96', t80: '#78D9B0', t85: '#86E7BE', t90: '#94F5CB', t95: '#B9FFDF' },
      cobalt:     { name: 'Cobalt', on: '#001356', t20: '#002487', t30: '#0037B9', t40: '#2B52D1', t60: '#6889FF', t70: '#90A7FF', t80: '#B6C4FF', t85: '#C9D3FF', t90: '#DCE1FF', t95: '#EFF0FF' },
      aqua:       { name: 'Aqua', on: '#001F2A', t20: '#003545', t30: '#004D63', t40: '#006783', t60: '#009DC5', t70: '#28B9E4', t80: '#55D5FF', t85: '#8EDFFF', t90: '#B7EAFF', t95: '#DDF4FF' },
      honey:      { name: 'Honey amber', on: '#281800', t20: '#442C00', t30: '#614000', t40: '#805600', t60: '#BF8616', t70: '#DDA032', t80: '#FBBB4C', t85: '#FFCC77', t90: '#FFDDAA', t95: '#FFEED6' },
      lavender:   { name: 'Lavender', on: '#2B0053', t20: '#431A6C', t30: '#5A3384', t40: '#734B9E', t60: '#A87ED5', t70: '#C499F2', t80: '#DDB7FF', t85: '#E7C9FF', t90: '#F1DBFF', t95: '#FAECFF' },
      kelly:      { name: 'Kelly green', on: '#012105', t20: '#00390A', t30: '#005313', t40: '#006E1F', t60: '#44A34D', t70: '#60BF65', t80: '#7BDC7E', t85: '#88EA8A', t90: '#96F997', t95: '#C5FFC1' },
      seapink:    { name: 'Sea pink', on: '#410009', t20: '#680015', t30: '#8A1829', t40: '#AB323E', t60: '#ED636C', t70: '#FF888F', t80: '#FFB3B6', t85: '#FFC7C9', t90: '#FFDADB', t95: '#FFECEC' },
      cornflower: { name: 'Cornflower', on: '#001943', t20: '#002D6C', t30: '#14448D', t40: '#345DA7', t60: '#6A90DE', t70: '#84AAFA', t80: '#ACC6FF', t85: '#C1D4FF', t90: '#D7E2FF', t95: '#ECF0FF' },
      neutral:    { name: 'Neutral', on: '#171C1F', t20: '#2C3134', t30: '#42474A', t40: '#5A5F62', t60: '#8C9194', t70: '#A7ACAF', t80: '#C2C7CA', t85: '#D1D5D8', t90: '#DFE3E6', t95: '#EDF1F4' }
    };
    function hx(h) { h = h.replace('#', ''); return [parseInt(h.substr(0, 2), 16), parseInt(h.substr(2, 2), 16), parseInt(h.substr(4, 2), 16)]; }
    function mix(a, b, t) { var x = hx(a), y = hx(b), o = '#'; for (var i = 0; i < 3; i++) { var v = Math.round(x[i] + (y[i] - x[i]) * t); o += (v < 16 ? '0' : '') + v.toString(16); } return o; }
    function ca(h, a) { var c = hx(h); return 'rgba(' + c[0] + ',' + c[1] + ',' + c[2] + ',' + (Math.round(Math.min(a, 1) * 1000) / 1000) + ')'; }
    function wa(a) { return 'rgba(255,255,255,' + (Math.round(Math.min(a, 1) * 1000) / 1000) + ')'; }
    function r1(n) { return Math.round(n * 10) / 10; }
    function px(n) { return r1(n) + 'px'; }
    var RAD = {
      round: function (w, h) { return px(Math.min(w, h) / 2); },
      capsule: function (w, h) { var r = Math.min(w, h) / 2; return px(r) + ' ' + px(r * 1.04) + ' ' + px(r * 0.98) + ' ' + px(r * 1.02) + ' / ' + px(r * 0.98) + ' ' + px(r * 1.03) + ' ' + px(r) + ' ' + px(r * 1.04); },
      pebble: function () { return '42% 38% 40% 45% / 40% 44% 38% 43%'; },
      lens: function () { return '50% 48% 52% 50% / 48% 52% 49% 51%'; },
      tumble: function () { return '60% 40% 55% 45% / 48% 56% 44% 52%'; },
      shard: function () { return '24% 64% 38% 58% / 58% 30% 66% 36%'; },
      egg: function () { return '50% 50% 47% 53% / 62% 60% 40% 38%'; }
    };
    function bf(b, s, br) { var v = 'blur(' + b + 'px) saturate(' + s + '%) brightness(' + br + ')'; return ' backdrop-filter: ' + v + '; -webkit-backdrop-filter: ' + v + ';'; }
    function lg(a, b, c) { return ' background: linear-gradient(180deg, ' + a + ' 0%, ' + b + ' 52%, ' + c + ' 100%);'; }
    var ONS = { light: '#171C1F', dark: '#DFE3E6' }, ONSV = { light: '#40484C', dark: '#C0C8CC' }, OUT = { light: '#70787D', dark: '#8A9296' };
    var MODE = { v: 'std' }, ONSV0 = { light: ONSV.light, dark: ONSV.dark };
    function setMode(md) { MODE.v = md || 'std'; var hi = MODE.v === 'high'; ONSV.light = hi ? ONS.light : ONSV0.light; ONSV.dark = hi ? ONS.dark : ONSV0.dark; }
    function getMode() { return MODE.v; }
    var RTS = { light: { milk: '#FFFFFF', well: '#DFE3E6', clear: '#FFFFFF', pearl: '#FFFFFF', ink: '#2C3134', dry: '#E4E9EC' }, dark: { milk: '#262B2D', well: '#313537', clear: '#353A3D', pearl: '#DFE3E6', ink: '#DFE3E6', dry: '#1B2023' } };
    var BGL = 'repeating-linear-gradient(0deg,rgba(25,28,34,.05) 0 2px,transparent 2px 16px),radial-gradient(34% 40% at 10% 20%,rgba(111,211,247,.5),transparent 70%),radial-gradient(30% 40% at 90% 24%,rgba(254,179,179,.45),transparent 70%),radial-gradient(40% 40% at 62% 96%,rgba(241,190,109,.42),transparent 70%),radial-gradient(36% 40% at 34% 78%,rgba(137,214,181,.45),transparent 70%),#EEF2F8';
    var BGD = 'repeating-linear-gradient(0deg,rgba(227,230,238,.04) 0 2px,transparent 2px 16px),radial-gradient(34% 40% at 10% 20%,rgba(45,82,209,.45),transparent 70%),radial-gradient(30% 40% at 90% 24%,rgba(168,126,213,.36),transparent 70%),radial-gradient(40% 40% at 62% 96%,rgba(2,77,98,.55),transparent 70%),radial-gradient(36% 40% at 34% 78%,rgba(190,134,25,.2),transparent 70%),#090C12';
    function halo(c, dark, hi) {
      if (hi) return 'box-shadow: 0 0 0 3px ' + (dark ? c.t90 : c.t30) + ', 0 0 0 5px ' + (dark ? '#000000' : '#FFFFFF') + ', 0 0 18px 4px ' + ca(c.t60, 0.45) + ';';
      return dark
        ? 'box-shadow: 0 0 0 1.5px ' + ca(c.t80, 0.95) + ', 0 0 14px 3px ' + ca(c.t60, 0.6) + ', 0 0 34px 10px ' + ca(c.t60, 0.25) + ';'
        : 'box-shadow: 0 0 0 1.5px ' + ca(c.t60, 0.85) + ', 0 0 14px 3px ' + ca(c.t60, 0.42) + ', 0 0 34px 10px ' + ca(c.t70, 0.22) + ';';
    }
    function mat(m, o) {
      var dark = o.theme === 'dark', h = o.h, c = P[o.role || 'aqua'];
      var lvl = o.contrast === 'high' ? 2 : (o.contrast === 'med' ? 1 : 0);
      var ta = [0, 0.15, 0.32][lvl], gs = [1, 0.6, 0.3][lvl], ls = [1, 0.7, 0.4][lvl];
      var R = 'border-radius: ' + o.rad + ';', hs = Math.min(h, 120), hz = Math.min(h, 360);
      var G = dark ? mix(c.t80, '#DFFFFF', 0.25) : mix(c.t95, '#EAFFFF', 0.55);
      var out = { shadow: 'display: none;', shadowShape: R, caustic: 'display: none;', contact: 'display: none;', body: R, light: 'display: none;', grain: 'display: none;', vol: 'display: none;', rim: 'display: none;' };
      var delay = ' animation-delay: -' + r1(((o.seed || 1) * 1.7) % 8) + 's;';
      function sh(blur, ty, sx, sy, col) { out.shadow = 'filter: blur(' + r1(blur) + 'px); transform: translateY(' + r1(ty) + 'px) scale(' + sx + ', ' + sy + ');'; out.shadowShape = R + ' background: ' + col + ';'; }
      function gr(a) { if (!lvl) out.grain = 'opacity: ' + a + ';'; }
      function caus(col, l, bl) { out.caustic = 'left: ' + l + '%; right: ' + l + '%; bottom: ' + px(-hs * 0.14) + '; height: ' + px(hs * 0.17) + '; border-radius: 50%; background: radial-gradient(closest-side, ' + col + ', rgba(255,255,255,0)); filter: blur(' + bl + 'px);'; }
      function cont(l, a) { out.contact = 'left: ' + l + '%; right: ' + l + '%; bottom: -2px; height: ' + Math.min(8, Math.round(hs * 0.2) + 2) + 'px; border-radius: 50%; background: ' + a + '; filter: blur(3px);'; }
      function rim(stops) { out.rim = 'background: linear-gradient(180deg, ' + stops + ');'; }
      if (m === 'none' || m === 'plain') return out;
      function ring(col) { out.rim = 'background: ' + col + ';'; }
      if (o.rt) {
        var rb = m === 'lens' ? 'clear' : m, S = RTS[o.theme], bg, line = null;
        if (rb === 'glass') bg = dark ? mix(c.t30, c.t40, 0.4) : mix(c.t80, c.t90, 0.5);
        else if (rb === 'frost') bg = dark ? mix(c.t20, c.t30, 0.5) : c.t90;
        else if (rb === 'fill' || rb === 'bead') bg = dark ? c.t80 : c.t40;
        else if (rb === 'well' && o.tint) bg = dark ? mix(S.well, c.t30, 0.5) : mix(S.well, c.t90, 0.6);
        else if (rb === 'pearl' && dark) bg = '#2C3134';
        else bg = S[rb] || S.clear;
        if (rb === 'clear') line = ca(OUT[o.theme], 0.75);
        if (rb === 'pearl') line = OUT[o.theme];
        if (rb === 'well') line = OUT[o.theme];
        if (rb === 'milk') line = dark ? 'rgba(255,255,255,0.08)' : 'rgba(23,28,31,0.08)';
        out.body += ' background: ' + bg + ';';
        var vs = [];
        if (rb !== 'well' && rb !== 'dry') vs.push('inset 0 1px 0 ' + wa(dark ? 0.08 : 0.5));
        if (line) vs.push('inset 0 0 0 1px ' + line);
        if (vs.length) out.vol = 'box-shadow: ' + vs.join(', ') + ';';
        if (rb !== 'well' && rb !== 'dry' && rb !== 'fill') sh(rb === 'milk' ? 18 : 5, rb === 'milk' ? 8 : 2, 0.98, 0.98, dark ? 'rgba(0,0,0,0.5)' : 'rgba(20,30,40,' + (rb === 'milk' ? 0.12 : 0.2) + ')');
        return out;
      }
      var bo = 0;
      if (m === 'lens') { m = 'clear'; bo = 1; }
      if (m === 'glass') {
        if (o.adapt && dark && ta < 0.3) ta = 0.3;
        out.body += dark ? lg(ca(c.t40, 0.52 + ta), ca(c.t30, 0.38 + ta), ca(c.t30, 0.3 + ta)) + bf(20, 180, o.adapt ? 0.9 : 1.25) : lg(ca(c.t80, 0.66 + ta), ca(mix(c.t80, c.t90, 0.5), 0.5 + ta), ca(c.t90, 0.4 + ta)) + bf(18, 170, 1.04);
        gr(dark ? 0.12 : 0.18);
        out.light = (dark
          ? 'background: radial-gradient(62% 58% at 50% 68%, ' + ca(c.t70, 0.4 * ls) + ', ' + ca(c.t70, 0) + ' 100%), radial-gradient(55% 45% at 28% 6%, ' + wa(0.2 * ls) + ', rgba(255,255,255,0) 100%);'
          : 'background: radial-gradient(68% 62% at 26% 4%, ' + wa(0.62 * ls) + ', rgba(255,255,255,0) 100%), radial-gradient(52% 42% at 72% 100%, ' + ca(G, 0.55 * ls) + ', ' + ca(G, 0) + ' 100%);') + delay;
        out.vol = 'box-shadow: ' + (dark
          ? 'inset 0 ' + px(-hs * 0.2) + ' ' + px(hs * 0.28) + ' ' + px(-hs * 0.1) + ' ' + ca(G, 0.42 * gs) + ', inset 0 ' + px(hs * 0.12) + ' ' + px(hs * 0.2) + ' ' + px(-hs * 0.1) + ' rgba(255,255,255,0.14), inset 0 0 ' + px(hs * 0.22) + ' rgba(0,0,0,0.32)'
          : 'inset 0 ' + px(-hs * 0.2) + ' ' + px(hs * 0.28) + ' ' + px(-hs * 0.1) + ' ' + ca(G, 0.75 * gs) + ', inset 0 ' + px(hs * 0.14) + ' ' + px(hs * 0.22) + ' ' + px(-hs * 0.12) + ' ' + ca(c.t40, 0.2) + ', inset 0 0 ' + px(hs * 0.18) + ' ' + ca(c.t60, 0.2)) + ';';
        rim(wa(dark ? 0.6 : 0.95) + ' 0%, ' + wa(dark ? 0.08 : 0.25) + ' 34%, ' + ca(dark ? c.t70 : c.t60, dark ? 0.22 : 0.16) + ' 66%, ' + ca(G, (dark ? 0.6 : 0.85) * gs) + ' 100%');
        if (dark) { sh(hs * 0.24 + 5, hs * 0.08, 0.96, 0.9, ca(c.t60, 0.5)); caus(ca(c.t80, 0.32 * gs), 26, 5); cont(14, 'rgba(0,0,0,0.5)'); }
        else { sh(hs * 0.16 + 5, hs * 0.24, 0.9, 0.86, ca(c.t60, 0.55)); caus(ca(mix(c.t80, '#FFFFFF', 0.3), 0.55 * gs), 24, 4); cont(12, 'rgba(20,24,34,0.22)'); }
        return out;
      }
      if (m === 'frost') {
        if (o.adapt && dark && ta < 0.3) ta = 0.3;
        out.body += dark ? lg(ca(c.t40, 0.46 + ta), ca(c.t30, 0.36 + ta), ca(c.t30, 0.32 + ta)) + bf(14, 160, o.adapt ? 0.9 : 1.15) : lg(ca(mix(c.t80, c.t90, 0.6), 0.6 + ta), ca(c.t90, 0.5 + ta), ca(c.t90, 0.44 + ta)) + bf(14, 150, 1.03);
        gr(dark ? 0.1 : 0.15);
        out.light = 'background: radial-gradient(70% 60% at 30% 0%, ' + wa((dark ? 0.16 : 0.5) * ls) + ', rgba(255,255,255,0) 100%);' + delay;
        out.vol = 'box-shadow: inset 0 ' + px(-hs * 0.16) + ' ' + px(hs * 0.24) + ' ' + px(-hs * 0.1) + ' ' + ca(G, (dark ? 0.3 : 0.6) * gs) + ';';
        rim(wa(dark ? 0.38 : 0.85) + ' 0%, ' + wa(dark ? 0.04 : 0.15) + ' 45%, ' + ca(G, dark ? 0.2 : 0.4) + ' 100%');
        sh(hs * 0.14 + 4, hs * 0.14, 0.92, 0.88, ca(c.t60, dark ? 0.42 : 0.32));
        return out;
      }
      if (m === 'clear') {
        if (dark) {
          var dk = lvl === 2 ? 0.86 : (lvl === 1 ? 0.6 : 0);
          if (o.adapt && !dk) dk = 0.6;
          out.body += dk ? lg('rgba(44,49,52,' + dk + ')', 'rgba(36,41,44,' + dk + ')', 'rgba(32,37,40,' + dk + ')') + bf(16, 150, 1) : lg(wa(0.2 + bo * 0.1), wa(0.1 + bo * 0.08), wa(0.12 + bo * 0.08)) + bf(16, 200, 1.55 + bo * 0.1);
          gr(0.05);
          out.light = 'background: radial-gradient(120% 70% at 50% -20%, ' + wa(0.18 * ls) + ', rgba(255,255,255,0) 60%), radial-gradient(40% 30% at 28% 12%, ' + wa(0.22 * ls) + ', rgba(255,255,255,0) 100%), radial-gradient(70% 60% at 50% 125%, rgba(140,210,255,' + r1(0.22 * ls * 100) / 100 + '), rgba(140,210,255,0) 100%);' + delay;
          out.vol = 'box-shadow: inset 0 1px 0.5px rgba(255,255,255,0.45), inset 0 -1px 0.5px rgba(170,220,255,' + r1(0.3 * gs * 100) / 100 + '), inset 0 0 ' + px(hs * 0.28) + ' rgba(0,0,0,0.22), inset 0 ' + px(-hs * 0.18) + ' ' + px(hs * 0.25) + ' ' + px(-hs * 0.12) + ' rgba(150,215,255,' + r1(0.12 * gs * 100) / 100 + ');';
          out.rim = 'background: linear-gradient(135deg, rgba(255,255,255,0.95) 0%, rgba(255,255,255,0.3) 16%, rgba(255,255,255,0) 38%, rgba(255,255,255,0) 64%, rgba(255,255,255,0.18) 82%, rgba(190,232,255,' + r1(0.8 * gs * 100) / 100 + ') 100%);';
          sh(hs * 0.2 + 5, hs * 0.12, 0.94, 0.88, 'rgba(0,0,0,0.5)'); cont(14, 'rgba(0,0,0,0.5)');
        } else {
          out.body += lg(wa(0.6 + ta + bo * 0.12), 'rgba(250,253,255,' + (0.4 + ta + bo * 0.1) + ')', 'rgba(236,248,255,' + (0.32 + ta + bo * 0.1) + ')') + bf(16, 180, 1.06 + bo * 0.04);
          gr(0.12);
          out.light = 'background: radial-gradient(70% 60% at 26% 0%, ' + wa(0.85 * ls) + ', rgba(255,255,255,0) 100%), radial-gradient(56% 46% at 74% 100%, rgba(236,252,255,' + r1(0.7 * ls * 100) / 100 + '), rgba(236,252,255,0) 100%);' + delay;
          out.vol = 'box-shadow: inset 0 ' + px(-hs * 0.2) + ' ' + px(hs * 0.28) + ' ' + px(-hs * 0.1) + ' rgba(228,250,255,' + r1(0.9 * gs * 100) / 100 + '), inset 0 ' + px(hs * 0.14) + ' ' + px(hs * 0.22) + ' ' + px(-hs * 0.12) + ' rgba(40,70,90,0.1), inset 0 0 ' + px(hs * 0.18) + ' rgba(40,70,90,0.1);';
          rim('rgba(255,255,255,1) 0%, rgba(255,255,255,0.35) 34%, rgba(80,105,120,0.16) 66%, rgba(236,252,255,0.95) 100%');
          sh(hs * 0.16 + 5, hs * 0.22, 0.9, 0.86, 'rgba(40,70,90,0.28)'); caus(wa(0.7 * gs), 24, 4); cont(12, 'rgba(20,24,34,0.16)');
        }
        if (lvl === 2) ring(OUT[o.theme]);
        return out;
      }
      if (m === 'pearl') {
        if (dark) {
          out.body += lg('rgba(242,246,248,0.97)', 'rgba(224,232,236,0.96)', 'rgba(206,216,221,0.96)') + bf(8, 140, 1);
          out.vol = 'box-shadow: inset 0 ' + px(-hs * 0.18) + ' ' + px(hs * 0.24) + ' ' + px(-hs * 0.1) + ' rgba(160,182,192,0.7), inset 0 0 0 0.5px rgba(0,0,0,0.2);';
          rim('rgba(255,255,255,1) 0%, rgba(255,255,255,0.3) 40%, rgba(150,170,180,0.4) 100%');
          sh(hs * 0.16 + 3, hs * 0.12 + 1, 0.92, 0.9, 'rgba(0,0,0,0.6)');
        } else {
          out.body += lg('#FFFFFF', 'rgba(252,254,255,0.98)', 'rgba(240,247,250,0.97)') + bf(8, 140, 1.02);
          out.vol = 'box-shadow: inset 0 ' + px(-hs * 0.18) + ' ' + px(hs * 0.24) + ' ' + px(-hs * 0.1) + ' rgba(206,230,240,0.9), inset 0 0 0 0.5px rgba(30,50,60,0.08);';
          rim('rgba(255,255,255,1) 0%, rgba(255,255,255,0.4) 40%, rgba(190,210,220,0.55) 100%');
          sh(hs * 0.14 + 3, hs * 0.12 + 1, 0.92, 0.9, 'rgba(20,40,55,0.3)');
        }
        cont(16, dark ? 'rgba(0,0,0,0.45)' : 'rgba(20,24,34,0.18)');
        if (lvl === 2 && dark) { out.body = R + ' background: #2C3134;'; out.vol = 'box-shadow: inset 0 0 0 1.5px ' + OUT.dark + ';'; return out; }
        if (lvl) ring(OUT[o.theme]);
        return out;
      }
      if (m === 'fill') {
        if (dark) {
          out.body += lg(ca(c.t60, 0.92), ca(mix(c.t60, c.t40, 0.4), 0.86), ca(c.t40, 0.82)) + bf(12, 170, 1.15);
          out.light = 'background: radial-gradient(90% 80% at 30% 0%, ' + wa(0.3 * ls) + ', rgba(255,255,255,0) 100%), radial-gradient(70% 60% at 70% 100%, ' + ca(c.t80, 0.4 * ls) + ', ' + ca(c.t80, 0) + ' 100%);' + delay;
          out.vol = 'box-shadow: inset 0 1px 0 rgba(255,255,255,0.4), inset 0 ' + px(-hs * 0.22) + ' ' + px(hs * 0.3) + ' ' + px(-hs * 0.1) + ' ' + ca(G, 0.5 * gs) + ';';
          sh(hs * 0.4 + 4, hs * 0.1, 0.96, 0.85, ca(c.t60, 0.7));
        } else {
          out.body += lg(ca(mix(c.t40, c.t60, 0.42), 0.96), ca(mix(c.t40, c.t60, 0.3), 0.95), ca(mix(c.t40, c.t60, 0.18), 0.95)) + bf(12, 170, 1);
          out.light = 'background: radial-gradient(90% 80% at 30% 0%, ' + wa(0.26 * ls) + ', rgba(255,255,255,0) 100%);' + delay;
          out.vol = 'box-shadow: inset 0 1px 0 rgba(255,255,255,0.45), inset 0 ' + px(-hs * 0.22) + ' ' + px(hs * 0.3) + ' ' + px(-hs * 0.1) + ' ' + ca(c.t70, 0.45 * gs) + ';';
          sh(hs * 0.4 + 4, hs * 0.15, 0.96, 0.8, ca(c.t60, 0.5));
        }
        rim(wa(dark ? 0.45 : 0.6) + ' 0%, rgba(255,255,255,0) 45%');
        gr(dark ? 0.08 : 0.1);
        if (lvl === 2) { out.body = R + ' background: ' + (dark ? c.t80 : c.t40) + ';'; out.light = 'display: none;'; }
        return out;
      }
      if (m === 'well') {
        if (dark) {
          out.body += ' background: ' + (o.tint ? 'linear-gradient(180deg, ' + ca(c.t40, 0.32) + ', ' + ca(c.t40, 0.2) + '), ' : '') + 'linear-gradient(180deg, rgba(0,0,0,' + (0.4 + ta * 0.4) + ') 0%, rgba(0,0,0,' + (0.28 + ta * 0.4) + ') 62%, rgba(255,255,255,0.05) 100%);' + bf(10, 130, 0.9);
          gr(0.08);
          out.vol = 'box-shadow: inset 0 ' + px(hs * 0.05 + 1.5) + ' ' + px(hs * 0.12 + 3) + ' rgba(0,0,0,0.6), inset 0 -1.5px 0 rgba(255,255,255,0.46), inset 0 0 0 1px rgba(255,255,255,0.05);';
        } else {
          out.body += ' background: ' + (o.tint ? 'linear-gradient(180deg, ' + ca(c.t80, 0.28) + ', ' + ca(c.t90, 0.18) + '), ' : '') + 'linear-gradient(180deg, rgba(22,38,48,' + (0.12 + ta * 0.3) + ') 0%, rgba(22,38,48,' + (0.06 + ta * 0.3) + ') 62%, rgba(255,255,255,0.2) 100%);' + bf(10, 130, 1.02);
          gr(0.12);
          out.vol = 'box-shadow: inset 0 1.5px 0 rgba(16,32,42,0.42), inset 0 ' + px(hs * 0.05 + 1.5) + ' ' + px(hs * 0.12 + 3) + ' rgba(16,32,42,0.2), inset 0 -1px 0 rgba(255,255,255,0.9), inset 0 0 0 1px rgba(16,32,42,0.06);';
        }
        if (lvl) ring(ca(OUT[o.theme], lvl === 2 ? 1 : 0.55));
        return out;
      }
      if (m === 'milk') {
        if (dark) {
          out.body += lg('rgba(48,54,58,0.9)', 'rgba(38,43,46,0.88)', 'rgba(33,38,41,0.88)') + bf(30, 150, 1);
          gr(0.05);
          rim('rgba(255,255,255,0.3) 0%, rgba(255,255,255,0.06) ' + Math.max(4, r1(1600 / h)) + '%, rgba(255,255,255,0) 40%, rgba(255,255,255,0.04) 100%');
          out.vol = 'box-shadow: inset 0 0 0 0.5px rgba(255,255,255,0.05);';
          sh(hz * 0.08 + 12, hz * 0.04 + 8, 0.97, 0.97, 'rgba(0,0,0,0.55)');
        } else {
          out.body += lg('rgba(255,255,255,0.9)', 'rgba(250,253,254,0.86)', 'rgba(244,249,251,0.86)') + bf(30, 160, 1.03);
          gr(0.07);
          rim('rgba(255,255,255,1) 0%, rgba(255,255,255,0.4) ' + Math.max(4, r1(1600 / h)) + '%, rgba(255,255,255,0) 40%, rgba(255,255,255,0) 80%, rgba(200,218,226,0.45) 100%');
          out.vol = 'box-shadow: inset 0 0 0 0.5px rgba(30,50,60,0.06);';
          sh(hz * 0.08 + 12, hz * 0.04 + 8, 0.97, 0.97, 'rgba(30,55,75,0.2)');
        }
        if (lvl === 2) ring(ca(OUT[o.theme], 0.7));
        return out;
      }
      if (m === 'ink') {
        if (dark) {
          out.body += lg('rgba(240,244,246,0.95)', 'rgba(229,235,238,0.93)', 'rgba(221,228,231,0.93)') + bf(24, 150, 1.02);
          rim('rgba(255,255,255,1) 0%, rgba(255,255,255,0) 40%');
          sh(hs * 0.2 + 8, hs * 0.15 + 4, 0.95, 0.9, 'rgba(0,0,0,0.55)');
        } else {
          out.body += lg('rgba(42,48,52,0.92)', 'rgba(33,38,41,0.9)', 'rgba(29,34,37,0.9)') + bf(24, 150, 1);
          rim('rgba(255,255,255,0.32) 0%, rgba(255,255,255,0) 40%');
          sh(hs * 0.2 + 8, hs * 0.15 + 4, 0.95, 0.9, 'rgba(20,30,40,0.32)');
        }
        gr(0.05);
        return out;
      }
      if (m === 'dry') {
        if (dark) {
          out.body += lg('rgba(84,90,94,0.4)', 'rgba(74,80,84,0.34)', 'rgba(68,74,78,0.32)') + bf(8, 30, 1);
          gr(0.18);
          rim('rgba(255,255,255,0.14) 0%, rgba(255,255,255,0) 45%');
          out.vol = 'box-shadow: inset 0 0 0 1px rgba(255,255,255,0.04);';
        } else {
          out.body += lg('rgba(226,231,234,0.74)', 'rgba(219,225,228,0.66)', 'rgba(213,220,223,0.64)') + bf(8, 30, 1.02);
          gr(0.3);
          rim('rgba(255,255,255,0.75) 0%, rgba(255,255,255,0) 45%');
          out.vol = 'box-shadow: inset 0 0 0 1px rgba(30,45,55,0.06);';
        }
        return out;
      }
      if (m === 'bead') {
        out.body += ' background: radial-gradient(120% 120% at 30% 10%, ' + (dark ? c.t80 + ' 0%, ' + c.t70 : mix(c.t40, c.t60, 0.25) + ' 0%, ' + c.t40 + ' 60%, ' + mix(c.t40, c.t30, 0.5)) + ' 100%);';
        out.vol = 'box-shadow: inset 0 1px 1px ' + wa(dark ? 0.6 : 0.5) + ', inset 0 -1.5px 2px ' + ca(c.on, dark ? 0.2 : 0.3) + ';';
        sh(3, 1.5, 0.9, 0.9, dark ? 'rgba(0,0,0,0.5)' : ca(c.t40, 0.45));
        return out;
      }
      return out;
    }
    function FG(m, c, th) {
      var dark = th === 'dark';
      if (m === 'glass' || m === 'frost') return dark ? c.t95 : c.on;
      if (m === 'fill') return dark ? c.on : '#FFFFFF';
      if (m === 'pearl') return '#171C1F';
      if (m === 'ink') return dark ? '#171C1F' : '#EEF3F5';
      if (m === 'bead') return dark ? c.on : '#FFFFFF';
      if (m === 'dry') return ca(ONS[th], 0.38);
      return ONS[th];
    }
    function icon(size, fill, color, wght) { return 'font-size: ' + size + 'px; font-variation-settings: "FILL" ' + (fill ? 1 : 0) + ', "wght" ' + (wght || 500) + ', "opsz" ' + Math.max(20, Math.min(48, size)) + ';' + (color ? ' color: ' + color + ';' : ''); }
    function K(k) {
      return {
        box: 'flex-direction: ' + (k.dir || 'row') + '; gap: ' + (k.gap != null ? k.gap : 4) + 'px;' + (k.w ? ' width: ' + k.w + 'px; flex: none;' : '') + (k.h ? ' height: ' + k.h + 'px;' : '') + (k.pad ? ' padding: ' + k.pad + ';' : '') + (k.r ? ' border-radius: ' + k.r + ';' : '') + (k.ind ? ' ' + k.ind : '') + (k.flex ? ' flex: ' + k.flex + ';' : '') + (k.x ? ' ' + k.x : ''),
        iconBox: k.ib || '',
        icon: k.icon || '', hasIcon: !!k.icon, iconStyle: icon(k.is || 24, !!k.fill, k.color || null),
        label: k.label || '', hasLabel: !!k.label, labelStyle: (k.ls || 'font-size: 14px; font-weight: 600;') + (k.lc ? ' color: ' + k.lc + ';' : '')
      };
    }
    function W(o) {
      var th = o.theme || 'light', dark = th === 'dark', m = o.m || 'glass', st = o.st || 'rest', role = o.role || 'aqua', c = P[role];
      var w = o.w, h = o.h, dis = st === 'disabled';
      var rad = o.rad || (o.radius != null ? px(o.radius) : RAD[o.shape || 'capsule'](w, h));
      var mm = m;
      if (dis && m !== 'none' && m !== 'plain' && o.dry !== false) mm = 'dry';
      if (st === 'press' && m === 'plain' && o.bloom !== false) mm = 'clear';
      var cm = o.contrast || (MODE.v === 'med' || MODE.v === 'high' ? MODE.v : null);
      var b = mat(mm, { theme: th, role: role, w: w, h: h, rad: rad, seed: o.seed || (w * 3 + h), contrast: cm, tint: o.tint, rt: MODE.v === 'rt', adapt: o.adapt });
      var fg = o.fg || FG(m, c, th);
      if (dis) fg = ca(ONS[th], 0.38);
      var raised = !!{ glass: 1, frost: 1, clear: 1, lens: 1, pearl: 1, fill: 1, bead: 1 }[mm];
      var wrap = 'width: ' + w + 'px; height: ' + h + 'px;';
      var layer = 'display: none;';
      if (st === 'hover') {
        if (raised) { wrap += ' translate: 0 -2px;'; b.shadow += ' translate: 0 3px; scale: 1.05;'; layer = 'background: radial-gradient(90% 80% at 50% 0%, ' + wa(dark ? 0.14 : 0.32) + ', rgba(255,255,255,0) 100%);'; }
        else layer = 'background: ' + (o.hoverCol || fg) + '; opacity: 0.07;';
      }
      if (st === 'focus' || o.focus) { b.body += ' ' + halo(c, dark, cm === 'high'); if (raised) layer = 'background: radial-gradient(90% 80% at 50% 0%, ' + wa(dark ? 0.12 : 0.26) + ', rgba(255,255,255,0) 100%);'; }
      if (st === 'press') {
        wrap += ' scale: ' + (o.pressScale || 0.96) + ';';
        if (raised) { b.shadow += ' translate: 0 -3px; scale: 0.9;'; layer = 'background: radial-gradient(75% 95% at 50% 55%, ' + wa(dark ? 0.22 : 0.42) + ', rgba(255,255,255,0) 100%);'; }
        else layer = 'background: ' + fg + '; opacity: 0.12;';
      }
      if (o.bx) b.body += ' ' + o.bx;
      if (o.layer) layer = o.layer;
      var pad = o.pad == null ? '0' : (typeof o.pad === 'number' ? '0 ' + o.pad + 'px' : o.pad);
      return {
        cls: (raised && !dis && o.live !== false ? 'sg-r ' : '') + (o.cls || ''),
        wrap: wrap + (o.wx ? ' ' + o.wx : ''), hint: w + 'px,' + h + 'px',
        shadow: b.shadow, shadowShape: b.shadowShape, caustic: b.caustic, contact: b.contact, body: b.body, light: b.light, grain: b.grain, vol: b.vol, rim: b.rim, layer: layer,
        content: 'color: ' + fg + '; justify-content: ' + (o.justify || 'center') + '; gap: ' + (o.gap != null ? o.gap : 8) + 'px; padding: ' + pad + '; flex-direction: ' + (o.dir || 'row') + '; font-size: ' + (o.fs || 14) + 'px; font-weight: ' + (o.fw || 600) + '; letter-spacing: ' + (o.lsp != null ? o.lsp : 0.1) + 'px;' + (o.cx ? ' ' + o.cx : ''),
        hasLead: !!o.lead, lead: o.lead || '', iconStyle: icon(o.is || 20, !!o.fill, o.ic || null, o.iw),
        hasLabel: !!o.label, label: o.label || '', labelStyle: o.ls || '',
        hasTrail: !!o.trail, trail: o.trail || '', trailStyle: icon(o.ts || o.is || 20, !!o.tfill, o.tc || null),
        kids: (o.kids || []).map(K)
      };
    }
    function at(x, y, extra) { return (x != null ? 'position: absolute; left: ' + x + 'px; top: ' + y + 'px; ' : '') + (extra || ''); }
    function Wd(o, x, y, cls) { var s = W(o); return { isW: true, w: s, cls: cls || '', pos: at(x, y) }; }
    function Tx(t, st, x, y) { return { isT: true, t: t, pos: at(x, y, 'white-space: nowrap; line-height: 1.3; ' + (st || '')) }; }
    function Ic(n, size, color, fill, x, y, extra) { return { isI: true, t: n, pos: at(x, y, icon(size, fill, color) + (extra ? ' ' + extra : '')) }; }
    function Bx(st, x, y, cls) { return { isB: true, cls: cls || '', pos: at(x, y, st) }; }
    function Ph(x, y, w, h, o) {
      o = o || {};
      var size = Math.max(o.size || 720, w, h), bp = o.bp || 'center', mm = /^(-?[\d.]+)px (-?[\d.]+)px$/.exec(bp);
      if (mm) bp = Math.max(-(size - w), Math.min(0, +mm[1])) + 'px ' + Math.max(-(size - h), Math.min(0, +mm[2])) + 'px';
      return { isP: true, cls: 'photo' + (o.dim ? ' photo-dim' : ''), alt: o.alt || 'Sea glass pebbles on white sand', pos: at(x, y, 'width: ' + w + 'px; height: ' + h + 'px; border-radius: ' + (typeof o.r === 'string' ? o.r : (o.r != null ? o.r : 0) + 'px') + '; background-size: ' + size + 'px; background-position: ' + bp + ';' + (o.x ? ' ' + o.x : '')) };
    }
    function cell(els, o) {
      o = o || {};
      var s = o.stack ? 'position: relative; flex: none; width: ' + o.sw + 'px; height: ' + o.sh + 'px;' + (o.left ? '' : ' margin: 0 auto;') : 'display: flex; align-items: ' + (o.align || 'center') + '; justify-content: ' + (o.justify || 'center') + '; gap: ' + (o.gap != null ? o.gap : 12) + 'px; flex-direction: ' + (o.dir || 'row') + '; flex-wrap: ' + (o.nowrap ? 'nowrap' : 'wrap') + ';';
      return { els: els, style: s + (o.x ? ' ' + o.x : '') };
    }
    function panels(fn, o) {
      o = o || {};
      return [{ bg: o.bgL || 'bg-light', fg: '#191C22', title: o.tL || 'LIGHT', rows: fn('light') }, { bg: o.bgD || 'bg-dark', fg: '#E3E6EE', title: o.tD || 'DARK', rows: fn('dark') }];
    }
    function T(fs, fw, color, x) { return 'font-size: ' + fs + 'px; font-weight: ' + fw + ';' + (color ? ' color: ' + color + ';' : '') + (x ? ' ' + x : ''); }
    function tw(s, fs) { return Math.round(s.length * fs * 0.56); }
    function phBg(th, x, y, w, h, r, bp, size) { return Ph(x, y, w, h, { r: r, bp: bp, dim: th === 'dark', size: size }); }
    function Menu(th, x, y, w, items, o) {
      o = o || {};
      var role = o.role || 'aqua', rh = 44, pad = 8, h = pad * 2, els = [];
      items.forEach(function (it) { h += it.sep ? 9 : rh; });
      els.push(Wd({ theme: th, m: 'milk', w: w, h: h, radius: o.r || 24, live: false }, x, y));
      var yy = y + pad;
      items.forEach(function (it) {
        if (it.sep) { els.push(Bx('width: ' + (w - 32) + 'px; height: 1px; background: ' + ca(ONS[th], 0.1) + ';', x + 16, yy + 4)); yy += 9; return; }
        if (it.hl) els.push(Wd({ theme: th, m: 'frost', role: role, w: w - 12, h: rh - 4, radius: (rh - 4) / 2, live: false }, x + 6, yy + 2));
        var col = it.danger ? (th === 'dark' ? P.seapink.t80 : P.seapink.t40) : (it.hl ? (th === 'dark' ? P[role].t95 : P[role].on) : ONS[th]);
        if (it.icon) els.push(Ic(it.icon, 20, it.danger || it.hl ? col : ONSV[th], !!it.fill, x + 18, yy + 12));
        els.push(Tx(it.label, T(15, 500, col), x + (it.icon ? 50 : 20), yy + 12));
        if (it.trail) els.push(Tx(it.trail, T(13, 500, ONSV[th], 'width: 90px; text-align: right;'), x + w - 110, yy + 13));
        yy += rh;
      });
      return { els: els, h: h };
    }
    return { P: P, W: W, Wd: Wd, Tx: Tx, Ic: Ic, Bx: Bx, Ph: Ph, phBg: phBg, Menu: Menu, T: T, tw: tw, cell: cell, panels: panels, ca: ca, wa: wa, mix: mix, px: px, r1: r1, RAD: RAD, ONS: ONS, ONSV: ONSV, OUT: OUT, BGL: BGL, BGD: BGD, halo: halo, icon: icon, setMode: setMode, getMode: getMode };
  }
