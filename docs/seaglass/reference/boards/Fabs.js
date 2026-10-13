    var RAD = K.RAD;
    function st(els, w, h) { return cell(els, { stack: true, sw: w, sh: h }); }
    function fab(th, size, s, role) { return Wd({ theme: th, m: 'glass', role: role || 'aqua', w: size, h: size, rad: RAD.pebble(), lead: 'edit', is: Math.round(size * 0.42), fill: true, st: s }); }
    function efab(th, s, m) { return Wd({ theme: th, m: m || 'glass', w: 40 + 24 + 8 + tw('Compose', 16), h: 56, lead: 'edit', is: 24, fill: true, label: 'Compose', fs: 16, gap: 8, pad: 20, st: s }); }
    var FSS = { rest: 'enabled', hover: 'lift 2dp · light +', focus: 'halo', press: 'scale 0.96 · bloom', disabled: 'dry glass' };
    var secA = {
      title: 'Floating action button', grade: 'Sea Glass pebble',
      note: 'The FAB is the only pebble-shaped glass in a layout: an organic silhouette with the full light, volume and caustic stack. 56, 80 and 96 dp sizes; the extended FAB is a 56 dp capsule. It shares the button state model, so press keeps the pebble outline instead of morphing to a sharper shape.',
      hasLabels: true, hasHeads: true, heads: ['', '56', '80', '96', 'Extended'], cols: '90px repeat(4, minmax(0, 1fr))', panelCols: 'repeat(2, minmax(0, 1fr))',
      panels: panels(function (th) {
        var rows = ST.map(function (s) { return { label: STN[s], sub: FSS[s], h: 100, cells: [cell([fab(th, 56, s)]), cell([fab(th, 80, s)]), cell([fab(th, 96, s)]), cell([efab(th, s)])] }; });
        rows.push({ label: 'Roles', sub: 'seed colour', h: 72, cells: [cell([fab(th, 56, 'rest', 'seafoam'), fab(th, 56, 'rest', 'lavender')]), cell([fab(th, 56, 'rest', 'honey'), fab(th, 56, 'rest', 'seapink')]), cell([fab(th, 56, 'rest', 'cobalt')]), cell([efab(th, 'rest', 'frost')])] });
        return rows;
      })
    };
    var MAIL = [['M', 'Maya Ortiz', 'Shore walk photos', '9:41', 'lavender'], ['J', 'Jun Park', 'Tide tables for Saturday', '8:12', 'aqua'], ['I', 'Ines Silva', 'Re: the blue jar pieces', 'Yesterday', 'honey'], ['T', 'Theo Grant', 'Album invite: Blue hour', 'Yesterday', 'seafoam'], ['N', 'North Rocks Club', 'Meetup moved to 7:00', 'Mon', 'kelly'], ['S', 'Sam Lee', 'Found a cobalt piece!', 'Sun', 'cobalt']];
    var SW = 412, SH = 600;
    function inbox(th) {
      var els = [Bx('width: ' + SW + 'px; height: ' + SH + 'px; border-radius: 32px; background: ' + (th === 'dark' ? '#0F1417' : '#F6FAFD') + '; box-shadow: inset 0 0 0 1px ' + ca(ONS[th], 0.06) + ';', 0, 0)];
      els.push(Tx('Inbox', T(30, 760, ONS[th], 'letter-spacing: -0.3px;'), 20, 22));
      els.push(Wd({ theme: th, m: 'clear', w: 44, h: 44, shape: 'lens', lead: 'search', is: 22 }, SW - 64, 20));
      MAIL.forEach(function (r, i) {
        var y = 84 + i * 76;
        els.push(Wd({ theme: th, m: 'bead', role: r[4], w: 40, h: 40, shape: 'round', label: r[0], fs: 15, fw: 700, live: false }, 20, y + 10));
        els.push(Tx(r[1], T(15, i < 2 ? 700 : 550, ONS[th]), 74, y + 10), Tx(r[2], T(14, 450, ONSV[th]), 74, y + 34), Tx(r[3], T(12.5, i < 2 ? 700 : 500, i < 2 ? acc(th) : ONSV[th], 'width: 80px; text-align: right;'), SW - 100, y + 12));
        if (i < MAIL.length - 1) els.push(Bx('width: ' + (SW - 94) + 'px; height: 1px; background: ' + ca(ONS[th], 0.07) + ';', 74, y + 75));
      });
      return els;
    }
    function veil(th) { return Bx('width: ' + SW + 'px; height: ' + SH + 'px; border-radius: 32px; background: ' + (th === 'dark' ? 'rgba(0,0,0,0.38)' : 'rgba(236,244,247,0.45)') + '; backdrop-filter: blur(6px) saturate(120%); -webkit-backdrop-filter: blur(6px) saturate(120%);', 0, 0); }
    var ITEMS = [['reply', 'Reply'], ['forward', 'Forward'], ['archive', 'Archive'], ['event', 'New event'], ['sticky_note_2', 'Note']];
    function items(th, upto, small) {
      var els = [];
      ITEMS.forEach(function (it, i) {
        if (i >= upto) return;
        var w = 20 + 22 + 10 + tw(it[1], 16) + 22, y = SH - 16 - 56 - 14 - 52 - i * 62;
        var o = { theme: th, m: 'frost', w: w, h: 52, lead: it[0], is: 22, fill: true, label: it[1], fs: 16, gap: 10, pad: '0 22px 0 20px', st: i === 1 && !small ? 'hover' : 'rest' };
        var e = Wd(o, SW - 16 - w, y);
        if (small) e.pos += ' scale: ' + (i === 0 ? 0.92 : 0.7) + '; transform-origin: right bottom;' + (i === 0 ? '' : ' opacity: 0.55;');
        els.push(e);
      });
      return els;
    }
    var secB = {
      title: 'Floating action menu', grade: 'Pebble → lens · Frost cascade',
      note: 'Opening melts the pebble into a round glass close lens while frost capsules rise from it in a cascade, each 40 ms behind the last on spatial.fast. The content behind is veiled with a light blur rather than a dark scrim, so the menu still reads as glass over the screen.',
      hasLabels: false, hasHeads: false, heads: [], cols: 'minmax(0, 1fr)', panelCols: 'minmax(0, 1fr)',
      panels: panels(function (th) {
        var closed = inbox(th).concat([Wd({ theme: th, m: 'glass', w: 136, h: 56, lead: 'edit', is: 24, fill: true, label: 'Compose', fs: 16, gap: 8, pad: 20 }, SW - 16 - 136, SH - 16 - 56)]);
        var opening = inbox(th).concat([veil(th)], items(th, 2, true), [Wd({ theme: th, m: 'glass', w: 60, h: 58, rad: RAD.egg(), lead: 'close', is: 26, iw: 600 }, SW - 16 - 60, SH - 16 - 58)]);
        var open = inbox(th).concat([veil(th)], items(th, 5, false), [Wd({ theme: th, m: 'glass', w: 56, h: 56, shape: 'lens', lead: 'close', is: 26, iw: 600, st: 'press' }, SW - 16 - 56, SH - 16 - 56)]);
        var cap = function (t, x) { return Tx(t, T(12, 650, th === 'dark' ? '#E3E6EE' : '#191C22', 'letter-spacing: 0.8px; opacity: 0.8;'), x, SH + 12); };
        var all = closed.concat(shift(opening, 436, 0), shift(open, 872, 0), [cap('CLOSED', 0), cap('OPENING · 120 MS', 436), cap('OPEN', 872)]);
        return [{ h: SH + 30, cells: [cell(all, { stack: true, sw: 1284, sh: SH + 30, left: true })] }];
      })
    };
    return { sections: fin([secA, secB]) };
