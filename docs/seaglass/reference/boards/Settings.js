    function swt(th, on, x, y) {
      var els = [Wd({ theme: th, m: 'well', w: 52, h: 32, radius: 16, live: false }, x, y)];
      if (on) els.push(Wd({ theme: th, m: 'fill', w: 48, h: 28, radius: 14, live: false }, x + 2, y + 2));
      els.push(Wd({ theme: th, m: 'pearl', w: 26, h: 26, shape: 'round' }, x + (on ? 23 : 3), y + 3));
      return els;
    }
    function tile(th, role, ic, x, y) { return Wd({ theme: th, m: 'glass', role: role, w: 32, h: 32, radius: 11, lead: ic, is: 19, fill: true, live: false }, x, y); }
    function hr(th, x, y, w) { return Bx('width: ' + w + 'px; height: 1px; background: ' + ca(ONS[th], th === 'dark' ? 0.1 : 0.08) + ';', x, y); }
    function cap(th, t, y) { return Tx(t, T(12.5, 700, ONSV[th], 'letter-spacing: 0.8px;'), 32, y); }
    function rail(th, x, y, L, v, role) {
      var cx = x + Math.round(L * v);
      return [Wd({ theme: th, m: 'well', w: L, h: 8, radius: 4, live: false }, x, y), Wd({ theme: th, m: 'fill', role: role, w: Math.round(L * v), h: 8, radius: 4, live: false }, x, y), Wd({ theme: th, m: 'pearl', w: 30, h: 20, radius: 10 }, cx - 15, y - 6)];
    }
    function settings(th) {
      var dark = th === 'dark', els = [base(th)].concat(status(th));
      els.push(lensBtn(th, 'clear', 40, 'arrow_back', 16, 51, { fg: ONS[th] }), lensBtn(th, 'clear', 40, 'search', 356, 51, { fg: ONS[th] }));
      els.push(Tx('Sound & display', T(30, 760, ONS[th], 'letter-spacing: -0.3px;'), 20, 96));
      els.push(cap(th, 'DISPLAY', 152), Wd({ theme: th, m: 'milk', w: 380, h: 216, radius: 26, live: false }, 16, 174));
      els.push(tile(th, 'honey', 'light_mode', 32, 192), Tx('Brightness', T(15.5, 550, ONS[th]), 76, 198), Tx('68%', T(14, 600, ONSV[th], 'width: 60px; text-align: right;'), 320, 199));
      els.push(Wd({ theme: th, m: 'well', w: 348, h: 40, radius: 20, live: false }, 32, 228), Wd({ theme: th, m: 'fill', role: 'honey', w: 237, h: 40, radius: 20, live: false }, 32, 228), Ic('brightness_low', 20, onFill(th, 'honey'), true, 44, 238), Ic('brightness_high', 20, ONSV[th], false, 348, 238));
      els.push(hr(th, 76, 278, 304), tile(th, 'cobalt', 'brightness_auto', 32, 290), Tx('Adaptive brightness', T(15.5, 550, ONS[th]), 76, 296));
      els = els.concat(swt(th, true, 328, 290));
      els.push(hr(th, 76, 334, 304), tile(th, 'lavender', 'dark_mode', 32, 346), Tx('Dark theme', T(15.5, 550, ONS[th]), 76, 352));
      els = els.concat(swt(th, dark, 328, 346));
      els.push(cap(th, 'VOLUME', 410), Wd({ theme: th, m: 'milk', w: 380, h: 228, radius: 26, live: false }, 16, 432));
      [['aqua', 'music_note', 'Media', 0.62], ['seapink', 'ring_volume', 'Ring', 0.4], ['kelly', 'alarm', 'Alarm', 0.8]].forEach(function (r, i) {
        var y = 438 + i * 72;
        els.push(tile(th, r[0], r[1], 32, y + 20), Tx(r[2], T(15, 550, ONS[th]), 76, y + 10), Tx(Math.round(r[3] * 100) + '%', T(13, 600, ONSV[th], 'width: 60px; text-align: right;'), 320, y + 11));
        els = els.concat(rail(th, 76, y + 44, 300, r[3], r[0]));
        if (i < 2) els.push(hr(th, 76, y + 71, 304));
      });
      els.push(cap(th, 'SOUNDS', 680), Wd({ theme: th, m: 'milk', w: 380, h: 180, radius: 26, live: false }, 16, 702));
      [['cornflower', 'notifications', 'Ringtone', 'Low tide'], ['seafoam', 'vibration', 'Vibration', 'Gentle'], ['neutral', 'do_not_disturb_on', 'Do not disturb', '']].forEach(function (r, i) {
        var y = 708 + i * 56;
        els.push(tile(th, r[0], r[1], 32, y + 12), Tx(r[2], T(15.5, 550, ONS[th]), 76, y + 18));
        if (r[3]) els.push(Tx(r[3], T(14.5, 500, ONSV[th], 'width: 100px; text-align: right;'), 250, y + 19), Ic('chevron_right', 20, ONSV[th], false, 358, y + 19));
        else els = els.concat(swt(th, false, 328, y + 12));
        if (i < 2) els.push(hr(th, 76, y + 56, 304));
      });
      els.push(handle(th));
      return frame(els);
    }
    var sec = phoneSec('Sound and display', 'Milk lists · Glass tiles · Well + Fill sliders · Switches', 'Settings are inset milk panes with tinted glass icon tiles. Brightness is a pill slider whose liquid takes the honey tint of its tile; volume rows use rail sliders with pearl thumbs, each filled in its row colour. Switches are wells with liquid and a pearl; Dark theme is on in the dark screen.', ['LIGHT', 'DARK'], [settings('light'), settings('dark')]);
    return { sections: fin([sec]) };
