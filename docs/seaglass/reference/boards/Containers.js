    function swt(th, on, x, y, role) {
      var els = [Wd({ theme: th, m: 'well', w: 52, h: 32, radius: 16, live: false }, x, y)];
      if (on) els.push(Wd({ theme: th, m: 'fill', role: role || 'aqua', w: 48, h: 28, radius: 14, live: false }, x + 2, y + 2));
      els.push(Wd({ theme: th, m: 'pearl', w: 26, h: 26, shape: 'round' }, x + (on ? 23 : 3), y + 3));
      return els;
    }
    function hr(th, x, y, w) { return Bx('width: ' + w + 'px; height: 1px; background: ' + ca(ONS[th], th === 'dark' ? 0.1 : 0.08) + ';', x, y); }
    function para(t, th, w, x, y, fs) { return Tx(t, T(fs || 14, 450, ONSV[th], 'white-space: normal; width: ' + w + 'px; line-height: 1.45;'), x, y); }

    function cardText(th) {
      return [Wd({ theme: th, m: 'milk', w: 290, h: 196, radius: 28, live: false }, 0, 0), Wd({ theme: th, m: 'frost', w: 40, h: 40, shape: 'lens', lead: 'waves', is: 22, live: false }, 20, 20),
        Tx('Tide tables', T(18, 690, ONS[th]), 20, 72), para('Low 6:40 · High 12:52. Swell 0.6 m from the south-west.', th, 250, 20, 100),
        Wd({ theme: th, m: 'plain', w: 70, h: 40, label: 'Later', fs: 15, fg: acc(th) }, 112, 142), Wd({ theme: th, m: 'glass', w: 84, h: 40, label: 'Open', fs: 15 }, 188, 142)];
    }
    function cardSwitch(th) {
      return [Wd({ theme: th, m: 'milk', w: 290, h: 196, radius: 28, live: false }, 0, 0), Ph(16, 16, 56, 56, { r: 18, size: 360, bp: '-120px -60px' }),
        Tx('Morning alerts', T(17, 690, ONS[th]), 86, 22), Tx('When the tide drops below 0.4 m', T(13, 500, ONSV[th]), 86, 46), hr(th, 16, 90, 258),
        Tx('Notify me', T(15, 550, ONS[th]), 20, 112)].concat(swt(th, true, 222, 106), [hr(th, 16, 150, 258), Tx('Sound', T(15, 550, ONS[th]), 20, 162), Tx('Shell', T(14, 500, ONSV[th], 'width: 60px; text-align: right;'), 186, 163), Ic('chevron_right', 20, ONSV[th], false, 252, 163)]);
    }
    function cardMedia(th) {
      return [Wd({ theme: th, m: 'milk', w: 290, h: 268, radius: 28, live: false }, 0, 0), Ph(8, 8, 274, 170, { r: 20, bp: '-40px -40px' }),
        Wd({ theme: th, m: 'clear', w: 40, h: 40, shape: 'lens', lead: 'favorite', is: 22, fill: true, ic: acc(th, 'seapink') }, 232, 18),
        Tx('Shore walk', T(17, 690, ONS[th]), 20, 192), Tx('24 photos · Saturday', T(13.5, 500, ONSV[th]), 20, 218),
        Wd({ theme: th, m: 'plain', w: 40, h: 40, shape: 'lens', lead: 'more_horiz', is: 22, fg: ONSV[th] }, 236, 196)];
    }
    function cardOverlay(th) {
      return [Ph(0, 0, 290, 268, { r: 28, bp: '-360px -300px', dim: th === 'dark' }), Wd({ theme: th, m: 'clear', w: 274, h: 72, radius: 20, live: false }, 8, 188),
        Tx('Blue hour', T(16, 690, ONS[th]), 24, 202), Tx('12 photos · 3 min', T(13, 500, ONSV[th]), 24, 226),
        Wd({ theme: th, m: 'glass', w: 48, h: 48, shape: 'lens', lead: 'play_arrow', is: 28, fill: true }, 222, 200)];
    }
    function cardTint(th) {
      var fg = onc(th, 'seafoam'), els = [Wd({ theme: th, m: 'frost', role: 'seafoam', w: 290, h: 150, radius: 28, live: false }, 0, 0),
        Tx('This week', T(13, 650, ca(fg, 0.8)), 20, 18), Tx('14', T(44, 760, fg, 'letter-spacing: -1px; line-height: 1;'), 20, 44), Tx('pieces found', T(14, 550, fg), 82, 66), Tx('4 more than last week', T(13, 500, ca(fg, 0.8)), 20, 108)];
      [3, 1, 4, 2, 0, 3, 1].forEach(function (v, i) { var h = 14 + v * 12; els.push(Bx('width: 10px; height: ' + h + 'px; border-radius: 5px; background: ' + ca(fg, i === 6 ? 0.85 : 0.3) + ';', 180 + i * 14, 126 - h)); });
      return els;
    }
    function cardSelected(th) {
      var els = [Wd({ theme: th, m: 'milk', w: 290, h: 150, radius: 28, live: false, bx: 'box-shadow: 0 0 0 2px ' + acc(th) + ';' }, 0, 0)];
      [0, 1, 2].forEach(function (i) { els.push(Ph(12 + i * 90, 12, 86, 86, { r: ['16px 8px 8px 16px', '8px', '8px 16px 16px 8px'][i], bp: (-100 - i * 180) + 'px -' + (220 + i * 60) + 'px', size: 600 })); });
      els.push(Tx('Low tide finds', T(15, 690, ONS[th]), 16, 110), Tx('3 photos', T(13, 500, ONSV[th]), 140, 111));
      els.push(Wd({ theme: th, m: 'glass', w: 30, h: 30, shape: 'round', lead: 'check', is: 20, iw: 700 }, 248, 108));
      return els;
    }
    var secA = {
      title: 'Cards', grade: 'Milk pane · glass action',
      note: 'Cards are milk panes with 28 dp corners and a lit top edge. Anything inset keeps concentric corners: an 8 dp inset photo gets 20 dp. Emphasis cards use tinted frost instead of milk. Selection adds a 2 dp accent ring and a glass check lens rather than a heavier container.',
      hasLabels: true, hasHeads: false, heads: [], cols: '100px minmax(0, 1fr)', panelCols: 'repeat(2, minmax(0, 1fr))',
      panels: panels(function (th) {
        return [
          { label: 'Milk', sub: 'text · controls', h: 196, cells: [cell(cardText(th).concat(shift(cardSwitch(th), 302, 0)), { stack: true, sw: 592, sh: 196, left: true })] },
          { label: 'Media', sub: 'inset · overlay', h: 268, cells: [cell(cardMedia(th).concat(shift(cardOverlay(th), 302, 0)), { stack: true, sw: 592, sh: 268, left: true })] },
          { label: 'Emphasis', sub: 'frost · selected', h: 150, cells: [cell(cardTint(th).concat(shift(cardSelected(th), 302, 0)), { stack: true, sw: 592, sh: 150, left: true })] }
        ];
      })
    };

    var SET = [['wifi', 'Wi-Fi', 'Shoreline', 'aqua', 'v'], ['bluetooth', 'Bluetooth', 'On', 'cobalt', 'v'], ['notifications', 'Notifications', '', 'seapink', 'c'], ['dark_mode', 'Dark theme', '', 'lavender', 's1'], ['battery_saver', 'Battery saver', '', 'kelly', 's0']];
    function setList(th) {
      var LW = 340, rh = 56, els = [Wd({ theme: th, m: 'milk', w: LW, h: SET.length * rh + 12, radius: 26, live: false }, 0, 0)];
      SET.forEach(function (r, i) {
        var y = 6 + i * rh;
        els.push(Wd({ theme: th, m: 'glass', role: r[3], w: 32, h: 32, radius: 11, lead: r[0], is: 19, fill: true, live: false }, 16, y + 12));
        els.push(Tx(r[1], T(15.5, 550, ONS[th]), 62, y + 17));
        if (r[4] === 'v') els.push(Tx(r[2], T(14.5, 500, ONSV[th], 'width: 100px; text-align: right;'), LW - 146, y + 18), Ic('chevron_right', 20, ONSV[th], false, LW - 38, y + 18));
        if (r[4] === 'c') els.push(Ic('chevron_right', 20, ONSV[th], false, LW - 38, y + 18));
        if (r[4] === 's1' || r[4] === 's0') els = els.concat(swt(th, r[4] === 's1', LW - 68, y + 12, 'aqua'));
        if (i < SET.length - 1) els.push(hr(th, 62, y + rh, LW - 78));
      });
      return els;
    }
    function stateList(th) {
      var LW = 340, rh = 56, S = ['Rest', 'Hover', 'Pressed', 'Selected', 'Disabled'], I = ['inbox', 'label', 'schedule', 'star', 'archive'], els = [Wd({ theme: th, m: 'milk', w: LW, h: S.length * rh + 12, radius: 26, live: false }, 0, 0)];
      S.forEach(function (s, i) {
        var y = 6 + i * rh, dis = s === 'Disabled', sel = s === 'Selected';
        if (s === 'Hover') els.push(Wd({ theme: th, m: 'none', w: LW - 12, h: rh - 4, st: 'hover', fg: ONS[th], live: false }, 6, y + 2));
        if (s === 'Pressed') els.push(Wd({ theme: th, m: 'well', w: LW - 12, h: rh - 4, live: false }, 6, y + 2));
        if (sel) els.push(Wd({ theme: th, m: 'frost', w: LW - 12, h: rh - 4, live: false }, 6, y + 2));
        var col = dis ? ca(ONS[th], 0.38) : (sel ? onc(th) : ONS[th]);
        els.push(Ic(I[i], 22, dis ? col : (sel ? col : ONSV[th]), sel, 22, y + 17), Tx(s, T(15.5, sel ? 650 : 550, col), 60, y + 10), Tx(['state.rest', 'layer 7%', 'well dip', 'frost row', 'content 38%'][i], T(12, 500, dis ? col : (sel ? ca(col, 0.8) : ONSV[th]), "font-family: 'Roboto Mono', monospace;"), 60, y + 31));
        if (sel) els.push(Ic('check', 22, col, false, LW - 42, y + 17));
        if (i < S.length - 1 && !sel && S[i + 1] !== 'Selected' && s !== 'Hover' && S[i + 1] !== 'Pressed') els.push(hr(th, 60, y + rh, LW - 76));
      });
      return els;
    }
    var secB = {
      title: 'Inset lists', grade: 'Milk pane · glass icon tiles · frost row',
      note: 'Lists are inset milk panes with 26 dp corners and hairlines that start at the text. Leading tiles are small tinted glass with 11 dp corners. Rows react without lifting: hover adds a 7% layer, press dips the row into a well, and selection fills it with a frost capsule.',
      hasLabels: false, hasHeads: false, heads: [], cols: 'minmax(0, 1fr)', panelCols: 'repeat(2, minmax(0, 1fr))',
      panels: panels(function (th) { return [{ h: 292, cells: [cell(setList(th).concat(shift(stateList(th), 356, 0)), { stack: true, sw: 696, sh: 292, left: true })] }]; })
    };

    function scrim(th) { return Bx('width: 412px; height: 460px; border-radius: 28px; background: ' + (th === 'dark' ? 'rgba(0,0,0,0.35)' : 'rgba(12,28,36,0.22)') + ';', 0, 0); }
    function alertScene(th) {
      var x = 46, y = 80, els = [phBg(th, 0, 0, 412, 460, 28, '-60px -100px'), scrim(th), Wd({ theme: th, m: 'milk', w: 320, h: 300, radius: 34, live: false }, x, y)];
      els.push(Wd({ theme: th, m: 'glass', role: 'seapink', w: 52, h: 52, shape: 'lens', lead: 'delete', is: 26, fill: true }, x + 134, y + 24));
      els.push(Tx('Delete 3 photos?', T(20, 720, ONS[th], 'width: 320px; text-align: center;'), x, y + 92));
      els.push(Tx('They move to Trash and are removed for good after 30 days.', T(14.5, 450, ONSV[th], 'white-space: normal; width: 260px; text-align: center; line-height: 1.45;'), x + 30, y + 124));
      els.push(Wd({ theme: th, m: 'glass', role: 'seapink', w: 272, h: 50, label: 'Delete', fs: 16 }, x + 24, y + 186), Wd({ theme: th, m: 'plain', w: 272, h: 46, label: 'Cancel', fs: 16, fg: acc(th) }, x + 24, y + 242));
      return els;
    }
    function sheetScene(th) {
      var x = 8, y = 134, els = [phBg(th, 0, 0, 412, 460, 28, '-300px -20px'), scrim(th), Wd({ theme: th, m: 'milk', w: 396, h: 318, radius: 36, live: false }, x, y)];
      els.push(Wd({ theme: th, m: 'well', w: 40, h: 6, radius: 3, live: false }, x + 178, y + 8));
      els.push(Tx('Share', T(18, 720, ONS[th]), x + 22, y + 26), Wd({ theme: th, m: 'clear', w: 34, h: 34, shape: 'lens', lead: 'close', is: 20, fg: ONSV[th] }, x + 396 - 52, y + 20));
      ['Maya', 'Jun', 'Ines', 'Theo'].forEach(function (n, i) {
        var ax = x + 26 + i * 92;
        els.push(Ph(ax, y + 70, 52, 52, { r: 26, size: 420, bp: (-40 - i * 90) + 'px -' + (60 + i * 70) + 'px' }), Tx(n, T(12.5, 600, ONS[th], 'width: 80px; text-align: center;'), ax - 14, y + 128));
      });
      els.push(hr(th, x + 20, y + 158, 356));
      [['link', 'Copy link'], ['photo_album', 'Add to album'], ['download', 'Save to device']].forEach(function (r, i) {
        var ry = y + 166 + i * 46;
        if (i === 1) els.push(Wd({ theme: th, m: 'well', w: 372, h: 42, live: false }, x + 12, ry + 2));
        els.push(Ic(r[0], 22, ONSV[th], false, x + 28, ry + 12), Tx(r[1], T(15.5, 550, ONS[th]), x + 64, ry + 13));
      });
      return els;
    }
    function menuScene(th) {
      var els = [phBg(th, 0, 0, 412, 460, 28, '-200px -360px', 900), Bx('width: 412px; height: 460px; border-radius: 28px; backdrop-filter: blur(10px); -webkit-backdrop-filter: blur(10px); background: ' + (th === 'dark' ? 'rgba(0,0,0,0.3)' : 'rgba(255,255,255,0.18)') + ';', 0, 0)];
      els.push(Bx('width: 300px; height: 200px; border-radius: 24px; background: rgba(20,40,55,0.35); filter: blur(22px);', 56, 52), Ph(56, 32, 300, 200, { r: 24, bp: '-120px -200px' }));
      els = els.concat(Menu(th, 56, 246, 236, [{ icon: 'content_copy', label: 'Copy' }, { icon: 'ios_share', label: 'Share' }, { icon: 'favorite', label: 'Favorite', hl: true, fill: true }, { sep: true }, { icon: 'delete', label: 'Delete', danger: true }]).els);
      return els;
    }
    var secC = {
      title: 'Alerts, sheets and menus', grade: 'Milk pane over scrim',
      note: 'Modal surfaces are milk panes that float with a margin: alerts at 34 dp corners, sheets inset 8 dp with 36 dp corners and a well grabber, menus at 24 dp beside a lifted preview. Buttons inside are full-width capsules; the highlighted menu row is a frost capsule.',
      hasLabels: false, hasHeads: false, heads: [], cols: 'minmax(0, 1fr)', panelCols: 'minmax(0, 1fr)',
      panels: panels(function (th) {
        return [{ h: 460, cells: [cell(alertScene(th).concat(shift(sheetScene(th), 436, 0), shift(menuScene(th), 872, 0)), { stack: true, sw: 1284, sh: 460, left: true })] }];
      })
    };
    function carousel(th) {
      var ws = [300, 184, 100, 44], x = 0, els = [];
      ws.forEach(function (w, i) { els.push(Ph(x, 0, w, 210, { r: i === 3 ? 20 : 28, bp: (-60 - i * 160) + 'px -' + (40 + i * 120) + 'px' })); x += w + 8; });
      els.push(Wd({ theme: th, m: 'clear', w: 126, h: 36, label: 'Shore walk', lead: 'photo_library', is: 18, fs: 14, gap: 6, live: false }, 14, 14));
      els.push(Wd({ theme: th, m: 'clear', w: 44, h: 44, shape: 'lens', lead: 'chevron_right', is: 24 }, 242, 83));
      var dx = 110;
      els.push(Wd({ theme: th, m: 'well', w: 80, h: 20, radius: 10, live: false }, dx, 176));
      [0, 1, 2, 3].forEach(function (i) { if (i === 0) els.push(Wd({ theme: th, m: 'pearl', w: 18, h: 7, radius: 3.5, live: false }, dx + 10, 182.5)); else els.push(Bx('width: 7px; height: 7px; border-radius: 4px; background: rgba(255,255,255,0.85); box-shadow: 0 1px 2px rgba(0,0,0,0.2);', dx + 26 + i * 12, 182.5)); });
      return els;
    }
    var secD = {
      title: 'Carousel', grade: 'Content · Clear controls',
      note: 'Carousel items are content, so they stay unglazed with 28 dp corners that tighten to 20 dp as items shrink off-screen. Controls laid over them are clear: a caption capsule, a lens arrow and page dots in a small well.',
      hasLabels: false, hasHeads: false, heads: [], cols: 'minmax(0, 1fr)', panelCols: 'repeat(2, minmax(0, 1fr))',
      panels: panels(function (th) { return [{ h: 210, cells: [cell(carousel(th), { stack: true, sw: 660, sh: 210, left: true })] }]; })
    };
    return { sections: fin([secA, secB, secC, secD]) };
