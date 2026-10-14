    var TW = 1280, TH = 800;
    var MAIL = [
      ['Maya Ortiz', 'Shore walk photos', 'Uploaded 24 from Saturday, the cobalt one is unreal.', '9:41', 0, 1],
      ['Jun Park', 'Tide tables for Saturday', 'Low at 6:40, high at 12:52. Meet by the north rocks?', '8:12', 3, 1],
      ['North Rocks Club', 'Meetup moved to 7:00', 'Parking fills up early this week, so plan ahead.', '7:30', 'kelly', 1],
      ['Ines Silva', 'Re: the blue jar pieces', 'I sorted them by colour, there are more amber than blue.', 'Yesterday', 5, 0],
      ['Theo Grant', 'Album invite: Blue hour', 'Theo shared an album with you. 12 photos, 3 videos.', 'Yesterday', 2, 0],
      ['Sam Lee', 'Found a cobalt piece!', 'Look at this one, perfectly frosted on both sides.', 'Mon', 9, 0],
      ['Tidepool Weekly', 'Five beaches for winter glass', 'Storm season brings the best finds to the surface.', 'Sun', 'lavender', 0]
    ];
    function rail(th) {
      var els = [Wd({ theme: th, m: 'clear', w: 80, h: 690, radius: 40, live: false }, 16, 62), Wd({ theme: th, m: 'glass', w: 56, h: 56, rad: RAD.pebble(), lead: 'edit', is: 26, fill: true }, 28, 78)];
      [['inbox', 'Inbox'], ['star', 'Starred'], ['send', 'Sent'], ['draft', 'Drafts'], ['archive', 'Archive'], ['delete', 'Trash']].forEach(function (it, i) {
        var y = 168 + i * 74, sel = i === 0;
        els.push(sel ? Wd({ theme: th, m: 'frost', w: 56, h: 32, radius: 16, lead: it[0], is: 22, fill: true, live: false }, 28, y) : Wd({ theme: th, m: 'none', w: 56, h: 32, radius: 16, lead: it[0], is: 22, fg: ONS[th], live: false }, 28, y));
        els.push(Tx(it[1], T(12, sel ? 700 : 550, ONS[th], 'width: 80px; text-align: center;'), 16, y + 36));
      });
      els.push(Wd({ theme: th, m: 'bead', role: 'seapink', w: 24, h: 16, radius: 8, label: '3', fs: 10, fw: 700, live: false }, 66, 162));
      els.push(Wd({ theme: th, m: 'plain', w: 48, h: 48, shape: 'lens', lead: 'settings', is: 22, fg: ONSV[th] }, 32, 690));
      return els;
    }
    function list(th) {
      var els = [Tx('Inbox', T(28, 760, ONS[th], 'letter-spacing: -0.3px;'), 128, 64)];
      els.push(Wd({ theme: th, m: 'well', w: 360, h: 44, radius: 22, live: false }, 124, 110), Ic('search', 20, ONSV[th], false, 138, 122), Tx('Search mail', T(15, 450, ONSV[th]), 168, 122));
      els.push(Wd({ theme: th, m: 'glass', w: 50 + tw('Unread', 14), h: 34, lead: 'check', is: 18, iw: 700, label: 'Unread', fs: 14, gap: 6, pad: '0 14px 0 12px' }, 124, 166), Wd({ theme: th, m: 'well', w: 28 + tw('Flagged', 14), h: 34, label: 'Flagged', fs: 14, pad: 14, fg: ONSV[th], live: false }, 232, 166), Wd({ theme: th, m: 'well', w: 28 + tw('Attachments', 14), h: 34, label: 'Attachments', fs: 14, pad: 14, fg: ONSV[th], live: false }, 320, 166));
      els.push(Wd({ theme: th, m: 'frost', w: 368, h: 80, radius: 24, live: false }, 120, 214));
      MAIL.forEach(function (r, i) {
        var y = 214 + i * 84, un = !!r[5], sel = i === 0, c1 = sel ? onc(th) : ONS[th], c2 = sel ? ca(onc(th), 0.78) : ONSV[th];
        if (un && !sel) els.push(Wd({ theme: th, m: 'bead', w: 8, h: 8, shape: 'round', live: false }, 116, y + 34));
        if (typeof r[4] === 'number') els.push(avatar(r[4], 132, y + 16, 44));
        else els.push(Wd({ theme: th, m: 'bead', role: r[4], w: 44, h: 44, shape: 'round', label: r[0].charAt(0), fs: 17, fw: 700, live: false }, 132, y + 16));
        els.push(Tx(r[0], T(15, un ? 700 : 550, c1, 'width: 200px; overflow: hidden; text-overflow: ellipsis;'), 188, y + 12), Tx(r[3], T(12.5, un ? 700 : 500, sel ? c1 : (un ? acc(th) : ONSV[th]), 'width: 80px; text-align: right;'), 396, y + 14));
        els.push(Tx(r[1], T(14, un ? 650 : 500, c1, 'width: 280px; overflow: hidden; text-overflow: ellipsis;'), 188, y + 33), Tx(r[2], T(13, 450, c2, 'width: 280px; overflow: hidden; text-overflow: ellipsis;'), 188, y + 54));
        if (i > 0 && i < MAIL.length - 1) els.push(Bx('width: 296px; height: 1px; background: ' + ca(ONS[th], th === 'dark' ? 0.1 : 0.07) + ';', 188, y + 83));
      });
      return els;
    }
    function detail(th) {
      var x0 = 512, els = [Wd({ theme: th, m: 'milk', w: 752, h: 722, radius: 32, live: false }, x0, 62)];
      els.push(Tx('Shore walk photos', T(24, 730, ONS[th], 'letter-spacing: -0.2px;'), x0 + 32, 90));
      els.push(Wd({ theme: th, m: 'clear', w: 136, h: 44, live: false }, x0 + 592, 84));
      ['archive', 'delete', 'more_vert'].forEach(function (ic, i) { els.push(Ic(ic, 22, ONS[th], false, x0 + 592 + 2 + i * 44 + 11, 95)); });
      els.push(avatar(0, x0 + 32, 140, 44), Tx('Maya Ortiz', T(15.5, 700, ONS[th]), x0 + 88, 142), Tx('to me, Jun · Today 9:41', T(13, 500, ONSV[th]), x0 + 88, 164));
      els.push(Tx('Hi both, I uploaded 24 photos from Saturday’s walk. The cobalt one near the north rocks is unreal, and Jun, your amber piece came out perfectly frosted. Want to make a shared album before the next meetup?', T(15.5, 450, ONS[th], 'white-space: normal; width: 680px; line-height: 1.6;'), x0 + 32, 208));
      [9, 5, 3, 1].forEach(function (k, i) { els.push(img(k, x0 + 32 + i * 172, 316, 160, 120, 18)); });
      els.push(Wd({ theme: th, m: 'clear', w: 236, h: 40, lead: 'photo_library', is: 18, ic: acc(th), label: 'Shore walk · 24 photos', fs: 14, gap: 8 }, x0 + 32, 452), Wd({ theme: th, m: 'clear', w: 124, h: 40, lead: 'download', is: 18, ic: acc(th), label: 'Save all', fs: 14, gap: 8 }, x0 + 280, 452));
      els.push(Bx('width: 688px; height: 1px; background: ' + ca(ONS[th], 0.08) + ';', x0 + 32, 520));
      els.push(Tx('Jun Park replied · 9:52', T(13, 600, ONSV[th]), x0 + 32, 538), Tx('Yes! Let’s call it Cobalt finds. I can bring the blue jar on Saturday.', T(15, 450, ONS[th], 'white-space: normal; width: 680px; line-height: 1.55;'), x0 + 32, 562));
      els.push(Wd({ theme: th, m: 'clear', w: 688, h: 58, live: false }, x0 + 32, 706));
      els.push(Ic('reply', 22, ONSV[th], false, x0 + 52, 724), Tx('Reply to Maya and Jun', T(15.5, 450, ONSV[th]), x0 + 86, 724));
      els.push(Wd({ theme: th, m: 'plain', w: 44, h: 44, shape: 'lens', lead: 'attach_file', is: 22, fg: ONS[th] }, x0 + 572, 713), Wd({ theme: th, m: 'plain', w: 44, h: 44, shape: 'lens', lead: 'mood', is: 22, fg: ONS[th] }, x0 + 616, 713), lensBtn(th, 'glass', 44, 'send', x0 + 668, 713, { fill: true }));
      return els;
    }
    function tablet(th) {
      var els = [base(th, null, TW, TH)].concat(status(th, null, TW), rail(th), list(th), detail(th), [handle(th, null, TW, TH)]);
      return frame(els, TW, TH, 32);
    }
    var sec = { title: 'Mail on a tablet', grade: 'Clear rail · Frost row · Milk detail · Clear reply bar', note: 'The navigation rail floats 16 dp from the edge as a clear capsule, with the pebble compose action on top and a frost lens on the current folder. The list sits on the page; the open message is a frost row. The reading pane is a milk pane with 32 dp corners, and the reply field inside it is a clear capsule ending in a glass send lens, inset so its corners stay concentric.', hasLabels: false, hasHeads: false, heads: [], cols: '1280px', gap: 12, panelCols: 'minmax(0, 1fr)',
      panels: [{ bg: 'bg-none', fg: '#191C22', title: 'TABLET · 1280 × 800 DP · LIGHT, THEN DARK', rows: [{ h: TH, cells: [tablet('light')] }, { h: 36, cells: [] }, { h: TH, cells: [tablet('dark')] }] }] };
    return { sections: fin([sec]) };
