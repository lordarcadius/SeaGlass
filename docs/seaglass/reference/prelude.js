// Helpers prepended to every board script (boards/*.js). K = recipes.js kit().
    var K = this.kit(), P = K.P, W = K.W, Wd = K.Wd, Tx = K.Tx, Ic = K.Ic, Bx = K.Bx, Ph = K.Ph, phBg = K.phBg, Menu = K.Menu, T = K.T, tw = K.tw, cell = K.cell, panels = K.panels, ca = K.ca, wa = K.wa, mix = K.mix, ONS = K.ONS, ONSV = K.ONSV;
    var ST = ['rest', 'hover', 'focus', 'press', 'disabled'], STN = { rest: 'Rest', hover: 'Hover', focus: 'Focus', press: 'Press', disabled: 'Disabled' };
    function acc(th, role) { var p = P[role || 'aqua'], hi = K.getMode() === 'high'; return th === 'dark' ? (hi ? p.t90 : p.t80) : (hi ? p.t30 : p.t40); }
    function onc(th, role) { return th === 'dark' ? P[role || 'aqua'].t95 : P[role || 'aqua'].on; }
    function onFill(th, role) { return th === 'dark' ? P[role || 'aqua'].on : '#FFFFFF'; }
    function ext(o, x) { if (x) for (var k in x) o[k] = x[k]; return o; }
    function fin(secs) { secs.forEach(function (sec) { if (sec.gap == null) sec.gap = 12; sec.panels.forEach(function (pn) { pn.rows.forEach(function (r) { if (r.sub == null) r.sub = ''; if (r.label == null) r.label = ''; }); }); }); return secs; }
    function shift(els, dx, dy) { return els.map(function (e) { var n = {}; for (var k in e) n[k] = e[k]; n.pos = e.pos.replace(/left: (-?[\d.]+)px; top: (-?[\d.]+)px;/, function (m0, a, b) { return 'left: ' + (+a + dx) + 'px; top: ' + (+b + (dy || 0)) + 'px;'; }); return n; }); }
