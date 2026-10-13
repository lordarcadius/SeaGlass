    function aurora(th, k) {
      var L = P.lavender, A = P.aqua, d = th === 'dark';
      return Bx('width: 412px; height: 915px; background: radial-gradient(70% ' + (k ? 55 : 38) + '% at 18% 100%, ' + ca(d ? L.t40 : L.t80, d ? 0.5 : 0.6) + ', transparent 72%), radial-gradient(70% ' + (k ? 50 : 34) + '% at 88% 96%, ' + ca(d ? A.t30 : A.t80, d ? 0.6 : 0.5) + ', transparent 72%)' + (k ? ', radial-gradient(50% 30% at 50% 50%, ' + ca(d ? L.t30 : L.t90, d ? 0.6 : 0.8) + ', transparent 70%)' : '') + ';', 0, 0);
    }
    function para(th, t, x, y, w, st) { return Tx(t, T(15.5, 450, ONS[th], 'white-space: normal; width: ' + w + 'px; line-height: 1.5;' + (st || '')), x, y); }
    function bubble(th, t, y, h) {
      var w = 268;
      return [Wd({ theme: th, m: 'frost', w: w, h: h, rad: '22px 22px 10px 22px', live: false }, 396 - w, y), Tx(t, T(15.5, 500, onc(th), 'white-space: normal; width: ' + (w - 36) + 'px; line-height: 1.45;'), 396 - w + 18, y + 12)];
    }
    function orbIcon(th, y) { return Wd({ theme: th, m: 'glass', role: 'lavender', w: 30, h: 30, rad: RAD.pebble(), lead: 'auto_awesome', is: 17, fill: true, live: false }, 16, y); }
    function topbar(th, t, l, r) {
      return [lensBtn(th, 'clear', 40, l, 16, 51, { fg: ONS[th] }), Tx(t, T(17, 690, ONS[th], 'width: 412px; text-align: center;'), 0, 60), lensBtn(th, 'clear', 40, r, 356, 51, { fg: ONS[th] })];
    }
    function chat(th) {
      var els = [base(th), aurora(th, false)].concat(status(th), topbar(th, 'Assistant', 'menu', 'edit_square'));
      els.push(center('Today 9:41', T(12.5, 600, ONSV[th]), 108));
      els = els.concat(bubble(th, 'Which pieces from Saturday are cobalt?', 136, 70));
      els.push(orbIcon(th, 222), Tx('Assistant', T(13.5, 700, ONS[th]), 54, 228));
      els.push(para(th, 'I found 4 cobalt pieces in Shore walk. The deepest blue one was taken at 6:52 near the north rocks.', 16, 262, 376));
      [9, 10, 3, 1].forEach(function (k, i) { els.push(img(k, 16 + i * 96, 344, 88, 88, 16)); });
      els.push(Wd({ theme: th, m: 'clear', w: 150, h: 36, lead: 'photo_album', is: 18, ic: acc(th), label: 'Make an album', fs: 14, gap: 6 }, 16, 448), Wd({ theme: th, m: 'clear', w: 140, h: 36, lead: 'map', is: 18, ic: acc(th), label: 'Show on map', fs: 14, gap: 6 }, 174, 448));
      ['content_copy', 'thumb_up', 'thumb_down', 'refresh'].forEach(function (ic, i) { els.push(Ic(ic, 19, ca(ONSV[th], 0.85), false, 20 + i * 36, 500)); });
      els = els.concat(bubble(th, 'Make a cobalt album and share it with Maya', 540, 70));
      els.push(orbIcon(th, 626), Tx('Assistant', T(13.5, 700, ONS[th]), 54, 632));
      els.push(para(th, 'Done. Cobalt finds has 4 photos and Maya can view it.', 16, 664, 376));
      els.push(Wd({ theme: th, m: 'milk', w: 320, h: 84, radius: 24, live: false }, 16, 712), img(9, 28, 724, 60, 60, 12), Tx('Cobalt finds', T(15.5, 690, ONS[th]), 102, 731), Tx('4 photos · Shared with Maya', T(13, 500, ONSV[th]), 102, 755), Ic('chevron_right', 22, ONSV[th], false, 302, 743));
      var mk = 'linear-gradient(0deg, #000 50%, transparent)';
      if (K.getMode() === 'rt') els.push(Bx('width: 412px; height: 110px; background: linear-gradient(0deg, ' + bgCol(th) + ' 55%, ' + ca(bgCol(th), 0) + ');', 0, 805));
      else els.push(Bx('width: 412px; height: 140px; background: linear-gradient(0deg, ' + (th === 'dark' ? 'rgba(15,20,22,0.7), rgba(15,20,22,0)' : 'rgba(244,250,253,0.7), rgba(244,250,253,0)') + '); backdrop-filter: blur(12px); -webkit-backdrop-filter: blur(12px); -webkit-mask-image: ' + mk + '; mask-image: ' + mk + ';', 0, 775));
      els.push(Wd({ theme: th, m: 'clear', w: 380, h: 60, live: false }, 16, 821));
      els.push(Wd({ theme: th, m: 'plain', w: 44, h: 44, shape: 'lens', lead: 'add', is: 24, fg: ONS[th] }, 24, 829), Tx('Ask anything', T(16, 450, ONSV[th]), 74, 840));
      els.push(Wd({ theme: th, m: 'plain', w: 44, h: 44, shape: 'lens', lead: 'mic', is: 22, fg: ONS[th] }, 290, 829), lensBtn(th, 'glass', 44, 'graphic_eq', 340, 829, { role: 'lavender' }));
      els.push(handle(th));
      return frame(els);
    }
    function voice(th) {
      var L = P.lavender, d = th === 'dark', els = [base(th), aurora(th, true)].concat(status(th), topbar(th, 'Voice', 'expand_more', 'subtitles'));
      els.push(center('Which of these pieces would', T(25, 620, ONS[th], 'letter-spacing: -0.2px;'), 196), center('make the best pendant…', T(25, 620, ca(ONS[th], 0.58), 'letter-spacing: -0.2px;'), 230));
      [300, 236].forEach(function (s, i) { els.push(Bx('width: ' + s + 'px; height: ' + s + 'px; border-radius: 50%; box-shadow: 0 0 0 1px ' + wa(d ? 0.14 : 0.7) + ', 0 0 40px 6px ' + ca(L.t60, d ? 0.22 - i * 0.05 : 0.18 - i * 0.04) + ', inset 0 0 30px ' + ca(L.t70, d ? 0.15 : 0.2) + ';', 206 - s / 2, 476 - s / 2)); });
      els.push(Wd({ theme: th, m: 'glass', role: 'lavender', w: 180, h: 180, rad: RAD.lens(), cls: 'sg-morph', live: false }, 116, 386));
      [16, 30, 46, 30, 16].forEach(function (h, i) { els.push(Bx('width: 6px; height: ' + h + 'px; border-radius: 3px; background: ' + ca(onc(th, 'lavender'), 0.85) + ';', 182 + i * 11, 476 - h / 2)); });
      els.push(center('Listening', T(15, 700, acc(th, 'lavender')), 612), center('Tap the orb to interrupt', T(13, 500, ONSV[th]), 636));
      var C = [['mic_off', 'Mute', 'clear'], ['videocam', 'Camera', 'clear'], ['keyboard', 'Type', 'clear'], ['close', 'End', 'glass']];
      C.forEach(function (c, i) {
        var cx = 74 + i * 88;
        els.push(lensBtn(th, c[2], 64, c[0], cx - 32, 754, c[2] === 'glass' ? { role: 'seapink', iw: 600 } : { fg: ONS[th] }));
        els.push(Tx(c[1], T(12.5, 600, ONS[th], 'width: 80px; text-align: center;'), cx - 40, 828));
      });
      els.push(handle(th));
      return frame(els);
    }
    var sec = phoneSec('Assistant with prompt bar and voice', 'Clear prompt bar · Lavender glass voice · Milk result card', 'The prompt bar is a clear capsule floating over a diffusion edge: add and dictate are plain lenses that condense on press, and voice mode is the one glass lens, tinted lavender. Your messages are frost bubbles; answers are plain text with a lavender pebble mark, clear suggestion capsules and a milk result card. Voice mode turns the screen into an aurora around a tumbling lavender glass orb, with mute, camera and type as clear lenses and End as sea pink glass.', ['LIGHT · CHAT', 'LIGHT · VOICE', 'DARK · CHAT', 'DARK · VOICE'], [chat('light'), voice('light'), chat('dark'), voice('dark')]);
    return { sections: fin([sec]) };
