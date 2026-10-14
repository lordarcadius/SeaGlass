    var SW = 412, SH = 915, RAD = K.RAD;
    function bgCol(th) { return th === 'dark' ? '#0F1417' : '#F6FAFD'; }
    function frame(els, w, h, r) { return cell(els, { stack: true, sw: w || SW, sh: h || SH, left: true, x: 'border-radius: ' + (r || 44) + 'px; overflow: hidden; box-shadow: 0 40px 80px -30px rgba(20,40,60,0.45), 0 0 0 1px rgba(20,30,40,0.1);' }); }
    function base(th, bg, w, h) { return Bx('width: ' + (w || SW) + 'px; height: ' + (h || SH) + 'px; background: ' + (bg || bgCol(th)) + ';', 0, 0); }
    function status(th, col, w) {
      col = col || ONS[th]; var r = (w || SW) - 98;
      return [Tx('9:41', T(15, 650, col), 30, 15), Ic('signal_cellular_alt', 17, col, true, r, 16), Ic('wifi', 17, col, true, r + 22, 16), Ic('battery_full', 17, col, true, r + 44, 16, 'rotate: 90deg;')];
    }
    function handle(th, col, w, h) { return Bx('width: 108px; height: 4px; border-radius: 2px; background: ' + (col || ca(ONS[th], 0.5)) + ';', ((w || SW) - 108) / 2, (h || SH) - 14); }
    var CROPS = [[130, 100], [520, 130], [780, 270], [250, 620], [950, 430], [1030, 770], [330, 350], [250, 920], [670, 930], [1050, 110], [1000, 1150], [430, 1130], [720, 50], [630, 600]];
    var GRADS = [
      'linear-gradient(180deg, #F9D9B4 0%, #F1A980 42%, #8E7FB0 78%, #4B5C8F 100%)',
      'radial-gradient(130% 80% at 50% 115%, #006783 0%, #28B9E4 45%, #DDF4FF 100%)',
      'linear-gradient(160deg, #B9FFDF 0%, #5BBD96 58%, #005138 100%)',
      'radial-gradient(90% 70% at 30% 20%, #FFEED6 0%, #FBBB4C 50%, #805600 100%)',
      'linear-gradient(200deg, #F1DBFF 0%, #A87ED5 55%, #2B0053 100%)',
      'linear-gradient(180deg, #B7EAFF 0%, #8EDFFF 48%, #FBBB4C 49%, #DDA032 100%)'
    ];
    function crop(i, w, h, z) { var c = CROPS[i % CROPS.length], S = Math.round((z || 5.2) * Math.max(w, h)); S = Math.max(S, w, h); return { size: S, bp: Math.round(w / 2 - c[0] * S / 1200) + 'px ' + Math.round(h / 2 - c[1] * S / 1200) + 'px' }; }
    function img(i, x, y, w, h, r, z) {
      if (i < 0) { var g = GRADS[(-i - 1) % GRADS.length]; return Bx('width: ' + w + 'px; height: ' + h + 'px; border-radius: ' + (typeof r === 'string' ? r : (r || 0) + 'px') + '; background: ' + g + ';', x, y); }
      var c = crop(i, w, h, z);
      return Ph(x, y, w, h, { r: r || 0, size: c.size, bp: c.bp });
    }
    function avatar(i, x, y, d) { var c = crop(i, d, d, 4.2); return Ph(x, y, d, d, { r: d / 2, size: c.size, bp: c.bp }); }
    function lensBtn(th, m, d, ic, x, y, o) { return Wd(ext({ theme: th, m: m, w: d, h: d, shape: 'lens', lead: ic, is: Math.round(d * 0.46) }, o), x, y); }
    function center(t, st, y, w) { return Tx(t, st + ' width: ' + (w || SW) + 'px; text-align: center;', 0, y); }
    function phoneSec(title, grade, note, heads, frames, panelTitle) {
      return { title: title, grade: grade, note: note, hasLabels: false, hasHeads: true, heads: heads, cols: 'repeat(' + frames.length + ', 412px)', gap: 48, panelCols: 'minmax(0, 1fr)',
        panels: [{ bg: 'bg-none', fg: '#191C22', title: panelTitle || 'PHONE · 412 × 915 DP', rows: [{ h: SH, cells: frames }] }] };
    }
