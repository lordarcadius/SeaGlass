    var MAIL = [
      ['Maya Ortiz', 'Shore walk photos', 'Uploaded 24 from Saturday, the cobalt one is unreal.', '9:41', 0, 1, 'attach'],
      ['Jun Park', 'Tide tables for Saturday', 'Low at 6:40, high at 12:52. Meet by the north rocks?', '8:12', 3, 1, ''],
      ['North Rocks Club', 'Meetup moved to 7:00', 'Parking fills up early this week, so plan ahead.', '7:30', 'kelly', 1, ''],
      ['Ines Silva', 'Re: the blue jar pieces', 'I sorted them by colour, there are more amber than blue.', 'Yesterday', 5, 0, 'star'],
      ['Theo Grant', 'Album invite: Blue hour', 'Theo shared an album with you. 12 photos, 3 videos.', 'Yesterday', 2, 0, ''],
      ['Sam Lee', 'Found a cobalt piece!', 'Look at this one, perfectly frosted on both sides.', 'Mon', 9, 0, 'attach'],
      ['Tidepool Weekly', 'Five beaches for winter glass', 'Storm season brings the best finds to the surface.', 'Sun', 'lavender', 0, '']
    ];
    function chip(th, label, on, x, y) {
      var w = (on ? 50 : 28) + tw(label, 14);
      return on ? Wd({ theme: th, m: 'glass', w: w, h: 34, lead: 'check', is: 18, iw: 700, label: label, fs: 14, gap: 6, pad: '0 14px 0 12px' }, x, y) : Wd({ theme: th, m: 'well', w: w, h: 34, label: label, fs: 14, pad: 14, fg: ONSV[th], live: false }, x, y);
    }
    function inbox(th) {
      var els = [base(th)].concat(status(th));
      els.push(lensBtn(th, 'clear', 40, 'menu', 16, 51, { fg: ONS[th] }), Wd({ theme: th, m: 'clear', w: 40, h: 40, shape: 'round', live: false }, 356, 51), avatar(6, 360, 55, 32));
      els.push(Tx('Inbox', T(34, 760, ONS[th], 'letter-spacing: -0.4px;'), 20, 94), Tx('3 unread · updated just now', T(14, 500, ONSV[th]), 21, 138));
      els.push(Wd({ theme: th, m: 'well', w: 380, h: 46, radius: 23, live: false }, 16, 172), Ic('search', 22, ONSV[th], false, 30, 184), Tx('Search mail', T(16, 450, ONSV[th]), 62, 185), Ic('mic', 22, ONSV[th], false, 362, 184));
      var cx = 16;
      [['Unread', true], ['Flagged', false], ['Attachments', false], ['People', false]].forEach(function (c) { els.push(chip(th, c[0], c[1], cx, 232)); cx += (c[1] ? 50 : 28) + tw(c[0], 14) + 8; });
      MAIL.forEach(function (r, i) {
        var y = 282 + i * 86, un = !!r[5];
        if (un) els.push(Wd({ theme: th, m: 'bead', w: 8, h: 8, shape: 'round', live: false }, 5, y + 32));
        if (typeof r[4] === 'number') els.push(avatar(r[4], 18, y + 14, 44));
        else els.push(Wd({ theme: th, m: 'bead', role: r[4], w: 44, h: 44, shape: 'round', label: r[0].charAt(0), fs: 17, fw: 700, live: false }, 18, y + 14));
        els.push(Tx(r[0], T(15.5, un ? 700 : 550, ONS[th], 'width: 220px; overflow: hidden; text-overflow: ellipsis;'), 74, y + 12));
        els.push(Tx(r[3], T(12.5, un ? 700 : 500, un ? acc(th) : ONSV[th], 'width: 80px; text-align: right;'), 316, y + 14));
        els.push(Tx(r[1], T(14.5, un ? 650 : 500, ONS[th], 'width: 290px; overflow: hidden; text-overflow: ellipsis;'), 74, y + 34));
        els.push(Tx(r[2], T(13.5, 450, ONSV[th], 'width: 290px; overflow: hidden; text-overflow: ellipsis;'), 74, y + 56));
        if (r[6] === 'attach') els.push(Ic('attach_file', 17, ONSV[th], false, 374, y + 56));
        if (r[6] === 'star') els.push(Ic('star', 18, acc(th, 'honey'), true, 374, y + 55));
        if (i < MAIL.length - 1) els.push(Bx('width: 322px; height: 1px; background: ' + ca(ONS[th], th === 'dark' ? 0.1 : 0.07) + ';', 74, y + 85));
      });
      return els;
    }
    var ITEMS = [['edit', 'New message'], ['event', 'Meeting invite'], ['sticky_note_2', 'Note to self'], ['document_scanner', 'Scan a document']];
    function closed(th) {
      var els = inbox(th);
      els.push(Wd({ theme: th, m: 'glass', w: 142, h: 56, lead: 'edit', is: 24, fill: true, label: 'Compose', fs: 16, gap: 8, pad: 20 }, 254, 825), handle(th));
      return frame(els);
    }
    function open(th) {
      var els = inbox(th);
      els.push(K.getMode() === 'rt' ? Bx('width: 412px; height: 915px; background: ' + (th === 'dark' ? 'rgba(0,0,0,0.72)' : 'rgba(236,244,247,0.86)') + ';', 0, 0) : Bx('width: 412px; height: 915px; background: ' + (th === 'dark' ? 'rgba(0,0,0,0.4)' : 'rgba(236,244,247,0.5)') + '; backdrop-filter: blur(6px) saturate(120%); -webkit-backdrop-filter: blur(6px) saturate(120%);', 0, 0));
      ITEMS.forEach(function (it, i) {
        var w = 20 + 22 + 10 + tw(it[1], 16) + 22;
        els.push(Wd({ theme: th, m: 'frost', w: w, h: 52, lead: it[0], is: 22, fill: true, label: it[1], fs: 16, gap: 10, pad: '0 22px 0 20px', st: i === 0 ? 'hover' : 'rest' }, 396 - w, 759 - i * 62));
      });
      els.push(lensBtn(th, 'glass', 56, 'close', 340, 825, { iw: 600 }), handle(th));
      return frame(els);
    }
    var sec = phoneSec('Inbox and compose menu', 'Well search · Chips · Glass FAB → Frost cascade', 'Search is a well because it sits in content; Unread is the one chip lifted into glass. The extended Compose button is the only glass on the list. Opening it veils the list with a light blur and melts the capsule into a round glass close lens, with four frost capsules rising above it, the nearest one highlighted.', ['LIGHT · LIST', 'LIGHT · COMPOSE MENU', 'DARK · LIST', 'DARK · COMPOSE MENU'], [closed('light'), open('light'), closed('dark'), open('dark')]);
    return { sections: fin([sec]) };
