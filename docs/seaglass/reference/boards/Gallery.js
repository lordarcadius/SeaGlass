    function edgeTop(th, h) {
      if (K.getMode() === 'rt') return Bx('width: 412px; height: ' + (h - 14) + 'px; background: ' + bgCol(th) + '; box-shadow: 0 1px 0 ' + ca(ONS[th], 0.12) + ';', 0, 0);
      var g = th === 'dark' ? 'rgba(15,20,22,0.82), rgba(15,20,22,0)' : 'rgba(244,250,253,0.85), rgba(244,250,253,0)', mk = 'linear-gradient(180deg, #000 55%, transparent)';
      return Bx('width: 412px; height: ' + h + 'px; background: linear-gradient(180deg, ' + g + '); backdrop-filter: blur(14px) saturate(160%); -webkit-backdrop-filter: blur(14px) saturate(160%); -webkit-mask-image: ' + mk + '; mask-image: ' + mk + ';', 0, 0);
    }
    function seg(th, items, segW, sel, x, y) {
      var els = [Wd({ theme: th, m: 'well', w: items.length * segW + 8, h: 40, radius: 20, live: false }, x, y), Wd({ theme: th, m: 'frost', w: segW, h: 32, radius: 16, live: false }, x + 4 + sel * segW, y + 4)];
      items.forEach(function (t, i) { els.push(Wd({ theme: th, m: 'none', w: segW, h: 32, label: t, fs: 14, fg: i === sel ? onc(th) : ONSV[th], live: false }, x + 4 + i * segW, y + 4)); });
      return els;
    }
    var TILES = [0, -2, 1, 5, 3, -1, 2, 8, -4, 9, 7, 13, -6, 4, 11, 10, -3, 12];
    function grid(th, y0, start, rows, sel) {
      var els = [];
      for (var r = 0; r < rows; r++) for (var c = 0; c < 3; c++) {
        var k = start + r * 3 + c, x = 12 + c * 131, y = y0 + r * 131;
        els.push(img(TILES[k % TILES.length], x, y, 127, 127, 10));
        if (k === 1) els.push(Wd({ theme: th, m: 'ink', w: 46, h: 22, label: '0:14', fs: 11.5, live: false }, x + 75, y + 6));
        if (k === 4 && !sel) els.push(Wd({ theme: th, m: 'ink', w: 28, h: 28, shape: 'round', lead: 'favorite', is: 16, fill: true, fg: th === 'dark' ? P.seapink.t40 : P.seapink.t80, live: false }, x + 6, y + 93));
        if (sel) {
          var on = sel.indexOf(k) >= 0;
          if (on) els.push(Bx('width: 127px; height: 127px; border-radius: 10px; box-shadow: inset 0 0 0 3px ' + acc(th) + '; background: ' + (th === 'dark' ? 'rgba(0,0,0,0.18)' : 'rgba(255,255,255,0.16)') + ';', x, y));
          els.push(on ? Wd({ theme: th, m: 'glass', w: 26, h: 26, shape: 'round', lead: 'check', is: 18, iw: 700, live: false, adapt: true }, x + 95, y + 95) : Wd({ theme: th, m: 'clear', w: 24, h: 24, shape: 'round', live: false, adapt: true }, x + 96, y + 96));
        }
      }
      return els;
    }
    function head(th, t, s, y, more) {
      var els = [Tx(t, T(17, 700, ONS[th]), 16, y), Tx(s, T(13, 500, ONSV[th]), 16, y + 24)];
      if (more) els.push(Tx('See all', T(14, 650, acc(th), 'width: 80px; text-align: right;'), 316, y + 3));
      return els;
    }
    function tabbar(th, x, y) {
      var TB = [['photo_library', 'Library'], ['photo_album', 'Albums'], ['auto_awesome', 'Memories']], TW = 92, BW = TB.length * TW + 8, els = [Wd({ theme: th, m: 'clear', w: BW, h: 62, live: false, adapt: true }, x, y), Wd({ theme: th, m: 'frost', w: TW, h: 54, radius: 27, live: false, adapt: true }, x + 4, y + 4)];
      TB.forEach(function (t, i) { els.push(Wd({ theme: th, m: 'none', w: TW, h: 54, dir: 'column', gap: 2, lead: t[0], is: 24, fill: i === 0, label: t[1], fs: 11, fw: 650, fg: i === 0 ? onc(th) : ONS[th], live: false }, x + 4 + i * TW, y + 4)); });
      els.push(lensBtn(th, 'clear', 62, 'search', x + BW + 10, y, { adapt: true }));
      return els;
    }
    function browse(th) {
      var els = [base(th)].concat(status(th));
      els.push(Wd({ theme: th, m: 'clear', w: 80, h: 38, label: 'Select', fs: 14.5, fg: ONS[th] }, 262, 52), Wd({ theme: th, m: 'clear', w: 40, h: 40, shape: 'round', live: false }, 352, 51), avatar(2, 356, 55, 32));
      els.push(Tx('Library', T(34, 760, ONS[th], 'letter-spacing: -0.4px;'), 20, 94), Tx('2,481 photos · 38 videos', T(14, 500, ONSV[th]), 21, 138));
      els = els.concat(seg(th, ['Years', 'Months', 'Days', 'All'], 92, 2, 18, 174), head(th, 'Saturday, 10 Oct', 'Shore walk · 24 items', 232, true), grid(th, 284, 0, 3), head(th, 'Wednesday, 7 Oct', 'Blue hour · 12 items', 690, true), grid(th, 742, 9, 2));
      els = els.concat(tabbar(th, 28, 823));
      els.push(handle(th));
      return frame(els);
    }
    function select(th) {
      var els = [base(th)].concat(head(th, 'Saturday, 10 Oct', 'Shore walk · 3 of 24 selected', 112), grid(th, 164, 0, 4, [1, 3, 4]), head(th, 'Wednesday, 7 Oct', 'Blue hour · 12 items', 700), grid(th, 752, 12, 2, []));
      els.push(edgeTop(th, 118));
      els = els.concat(status(th));
      els.push(Wd({ theme: th, m: 'plain', w: 84, h: 40, label: 'Cancel', fs: 15.5, fg: acc(th) }, 6, 50), center('3 selected', T(17, 690, ONS[th]), 59), Wd({ theme: th, m: 'clear', w: 100, h: 38, label: 'Select all', fs: 14.5, fg: ONS[th] }, 298, 51));
      var TL = ['favorite', 'library_add', 'download', 'delete'], x = 62, y = 823;
      els.push(Wd({ theme: th, m: 'clear', w: TL.length * 52 + 8, h: 62, live: false, adapt: true }, x, y));
      TL.forEach(function (ic, i) { els.push(Ic(ic, 25, ic === 'delete' ? acc(th, 'seapink') : ONS[th], false, x + 4 + i * 52 + 13.5, y + 18.5)); });
      els.push(lensBtn(th, 'glass', 62, 'ios_share', x + TL.length * 52 + 18, y, { adapt: true }));
      els.push(handle(th));
      return frame(els);
    }
    var sec = phoneSec('Library', 'Clear tab bar · Well tabs · Clear toolbar · Glass share', 'Browsing: the top bar has no container, Years / Months / Days / All are a segmented well with a frost lens, and the clear tab bar with its separate search lens floats over the grid. Selecting: the tab bar is replaced by a clear toolbar with share as the one glass action, selected photos get a 3 dp accent ring and a glass check lens, and an edge diffusion keeps the scrolled top readable.', ['LIGHT · BROWSE', 'LIGHT · SELECT', 'DARK · BROWSE', 'DARK · SELECT'], [browse('light'), select('light'), browse('dark'), select('dark')]);
    return { sections: fin([sec]) };
