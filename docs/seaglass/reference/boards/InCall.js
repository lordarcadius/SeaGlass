    function call(th) {
      var dark = th === 'dark', fg = dark ? '#EEF3F5' : ONS[th], hi = K.getMode() === 'high', fg2 = dark ? (hi ? fg : 'rgba(238,243,245,0.8)') : (hi ? fg : '#2A3135');
      var els = [base(th), Ph(-60, -60, 532, 1035, { size: 1100, bp: '-160px -40px', x: 'filter: blur(34px) saturate(1.5) brightness(' + (dark ? 0.45 : 1.08) + ');' })];
      els.push(Bx('width: 412px; height: 915px; background: ' + (dark ? 'linear-gradient(180deg, rgba(8,12,14,0.55), rgba(8,12,14,0.2) 45%, rgba(8,12,14,0.7))' : 'linear-gradient(180deg, rgba(244,250,253,0.62), rgba(244,250,253,0.28) 45%, rgba(244,250,253,0.66))') + ';', 0, 0));
      els = els.concat(status(th, fg));
      els.push(Wd({ theme: th, m: 'clear', w: 164, h: 34, lead: 'graphic_eq', is: 18, ic: acc(th, 'kelly'), label: 'HD voice · 04:27', fs: 13.5, gap: 6, fg: fg, live: false }, 124, 60));
      els.push(Wd({ theme: th, m: 'clear', w: 152, h: 152, shape: 'round', live: false }, 130, 128), avatar(0, 140, 138, 132));
      els.push(center('Maya Ortiz', T(32, 650, fg, 'letter-spacing: -0.3px;'), 300), center('Mobile · Shore walk group', T(15, 500, fg2), 344));
      var C = [['mic_off', 'Mute'], ['dialpad', 'Keypad'], ['volume_up', 'Speaker'], ['person_add', 'Add call'], ['videocam', 'Video'], ['pause', 'Hold']];
      C.forEach(function (c, i) {
        var cx = [96, 206, 316][i % 3], y = i < 3 ? 470 : 604, on = c[1] === 'Speaker';
        els.push(lensBtn(th, on ? 'glass' : 'clear', 78, c[0], cx - 39, y, { fill: on, fg: on ? null : fg }));
        els.push(Tx(c[1], T(13.5, 600, fg, 'width: 110px; text-align: center;'), cx - 55, y + 88));
      });
      els.push(lensBtn(th, 'clear', 58, 'chat', 34, 784, { fg: fg }));
      els.push(Wd({ theme: th, m: 'glass', role: 'seapink', w: 180, h: 72, lead: 'call_end', is: 34, fill: true }, 116, 777));
      els.push(lensBtn(th, 'clear', 58, 'more_horiz', 320, 784, { fg: fg }));
      els.push(handle(th, dark ? 'rgba(238,243,245,0.6)' : null));
      return frame(els);
    }
    var sec = phoneSec('Active call', 'Clear lenses · Glass toggle · Sea pink glass', 'The caller’s photo becomes a blurred, saturated backdrop so every control is glass over colour. Call controls are 78 dp clear lenses; the active one (Speaker) turns to tinted glass with a filled symbol. Ending the call is the single strongest object on screen: a sea pink glass capsule, with message and more as smaller clear lenses on either side.', ['LIGHT', 'DARK'], [call('light'), call('dark')]);
    return { sections: fin([sec]) };
