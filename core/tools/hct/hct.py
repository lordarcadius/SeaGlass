"""Port of the CAM16 / HCT colour math from material-color-utilities (Apache 2.0).

- Cam16 forward/inverse with the library's default viewing conditions
- Hct.from(hue, chroma, tone) using the library's original J/chroma bisection solver
- TonalPalette.tone(t) = Hct.from(hue, chroma, t)
"""
import math

SRGB_TO_XYZ = [[0.41233895, 0.35762064, 0.18051042], [0.2126, 0.7152, 0.0722], [0.01932141, 0.11916382, 0.95034478]]
XYZ_TO_SRGB = [[3.2413774792388685, -1.5376652402851851, -0.49885366846268053], [-0.9691452513005321, 1.8758853451067872, 0.04156585530208713], [0.05562093689691305, -0.20395524564742123, 1.0571799111220335]]
WHITE = [95.047, 100.0, 108.883]


def linearized(c):
    n = c / 255.0
    return (n / 12.92 if n <= 0.040449936 else ((n + 0.055) / 1.055) ** 2.4) * 100.0


def delinearized(c):
    n = c / 100.0
    d = n * 12.92 if n <= 0.0031308 else 1.055 * n ** (1.0 / 2.4) - 0.055
    return max(0, min(255, round(d * 255.0)))


def lab_f(t):
    e, k = 216.0 / 24389.0, 24389.0 / 27.0
    return t ** (1.0 / 3.0) if t > e else (k * t + 16.0) / 116.0


def lab_invf(ft):
    e, k = 216.0 / 24389.0, 24389.0 / 27.0
    f3 = ft ** 3
    return f3 if f3 > e else (116.0 * ft - 16.0) / k


def y_from_lstar(l):
    return 100.0 * lab_invf((l + 16.0) / 116.0)


def lstar_from_y(y):
    return lab_f(y / 100.0) * 116.0 - 16.0


def rgb_of(argb):
    return [(argb >> 16) & 255, (argb >> 8) & 255, argb & 255]


def argb_from_rgb(r, g, b):
    return (255 << 24) | (r << 16) | (g << 8) | b


def xyz_from_argb(argb):
    lin = [linearized(c) for c in rgb_of(argb)]
    return [sum(SRGB_TO_XYZ[i][j] * lin[j] for j in range(3)) for i in range(3)]


def argb_from_xyz(x, y, z):
    lin = [XYZ_TO_SRGB[i][0] * x + XYZ_TO_SRGB[i][1] * y + XYZ_TO_SRGB[i][2] * z for i in range(3)]
    return argb_from_rgb(*[delinearized(c) for c in lin])


def lstar_from_argb(argb):
    return lstar_from_y(xyz_from_argb(argb)[1])


def argb_from_lstar(l):
    c = delinearized(y_from_lstar(l))
    return argb_from_rgb(c, c, c)


class VC:
    def __init__(self):
        adapting = (200.0 / math.pi) * y_from_lstar(50.0) / 100.0
        bg_lstar, surround = 50.0, 2.0
        x, y, z = WHITE
        rW = x * 0.401288 + y * 0.650173 + z * -0.051461
        gW = x * -0.250268 + y * 1.204414 + z * 0.045854
        bW = x * -0.002079 + y * 0.048952 + z * 0.953127
        f = 0.8 + surround / 10.0
        c = 0.59 + (0.69 - 0.59) * ((f - 0.9) * 10.0) if f >= 0.9 else 0.525 + (0.59 - 0.525) * ((f - 0.8) * 10.0)
        d = f * (1.0 - (1.0 / 3.6) * math.exp((-adapting - 42.0) / 92.0))
        d = max(0.0, min(1.0, d))
        self.nc = f
        self.rgbD = [d * (100.0 / rW) + 1.0 - d, d * (100.0 / gW) + 1.0 - d, d * (100.0 / bW) + 1.0 - d]
        k = 1.0 / (5.0 * adapting + 1.0)
        k4 = k ** 4
        k4F = 1.0 - k4
        self.fl = k4 * adapting + 0.1 * k4F * k4F * (5.0 * adapting) ** (1.0 / 3.0)
        self.n = y_from_lstar(bg_lstar) / WHITE[1]
        self.z = 1.48 + math.sqrt(self.n)
        self.nbb = 0.725 / self.n ** 0.2
        self.ncb = self.nbb
        fac = [(self.fl * self.rgbD[i] * [rW, gW, bW][i] / 100.0) ** 0.42 for i in range(3)]
        rgbA = [400.0 * f_ / (f_ + 27.13) for f_ in fac]
        self.aw = (2.0 * rgbA[0] + rgbA[1] + 0.05 * rgbA[2]) * self.nbb
        self.c = c
        self.fLRoot = self.fl ** 0.25


V = VC()


class Cam16:
    def __init__(self, hue, chroma, j, q, m, s, jstar, astar, bstar):
        self.hue, self.chroma, self.j, self.q, self.m, self.s = hue, chroma, j, q, m, s
        self.jstar, self.astar, self.bstar = jstar, astar, bstar

    @staticmethod
    def from_argb(argb):
        x, y, z = xyz_from_argb(argb)
        rC = 0.401288 * x + 0.650173 * y - 0.051461 * z
        gC = -0.250268 * x + 1.204414 * y + 0.045854 * z
        bC = -0.002079 * x + 0.048952 * y + 0.953127 * z
        D = [V.rgbD[0] * rC, V.rgbD[1] * gC, V.rgbD[2] * bC]
        A = []
        for d in D:
            af = (V.fl * abs(d) / 100.0) ** 0.42
            A.append(math.copysign(1, d) * 400.0 * af / (af + 27.13) if d != 0 else 0.0)
        rA, gA, bA = A
        a = (11.0 * rA - 12.0 * gA + bA) / 11.0
        b = (rA + gA - 2.0 * bA) / 9.0
        u = (20.0 * rA + 20.0 * gA + 21.0 * bA) / 20.0
        p2 = (40.0 * rA + 20.0 * gA + bA) / 20.0
        hue = math.degrees(math.atan2(b, a))
        hue = hue + 360.0 if hue < 0 else (hue - 360.0 if hue >= 360 else hue)
        hr = math.radians(hue)
        ac = p2 * V.nbb
        j = 100.0 * (ac / V.aw) ** (V.c * V.z)
        q = (4.0 / V.c) * math.sqrt(j / 100.0) * (V.aw + 4.0) * V.fLRoot
        hp = hue + 360.0 if hue < 20.14 else hue
        eHue = 0.25 * (math.cos(math.radians(hp) + 2.0) + 3.8)
        p1 = 50000.0 / 13.0 * eHue * V.nc * V.ncb
        t = p1 * math.hypot(a, b) / (u + 0.305)
        alpha = t ** 0.9 * (1.64 - 0.29 ** V.n) ** 0.73
        c = alpha * math.sqrt(j / 100.0)
        m = c * V.fLRoot
        s = 50.0 * math.sqrt((alpha * V.c) / (V.aw + 4.0))
        jstar = (1.0 + 100.0 * 0.007) * j / (1.0 + 0.007 * j)
        mstar = 1.0 / 0.0228 * math.log1p(0.0228 * m)
        return Cam16(hue, c, j, q, m, s, jstar, mstar * math.cos(hr), mstar * math.sin(hr))

    @staticmethod
    def from_jch(j, c, h):
        q = (4.0 / V.c) * math.sqrt(j / 100.0) * (V.aw + 4.0) * V.fLRoot
        m = c * V.fLRoot
        alpha = c / math.sqrt(j / 100.0)
        s = 50.0 * math.sqrt((alpha * V.c) / (V.aw + 4.0))
        hr = math.radians(h)
        jstar = (1.0 + 100.0 * 0.007) * j / (1.0 + 0.007 * j)
        mstar = 1.0 / 0.0228 * math.log1p(0.0228 * m)
        return Cam16(h, c, j, q, m, s, jstar, mstar * math.cos(hr), mstar * math.sin(hr))

    def distance(self, o):
        d = math.sqrt((self.jstar - o.jstar) ** 2 + (self.astar - o.astar) ** 2 + (self.bstar - o.bstar) ** 2)
        return 1.41 * d ** 0.63

    def viewed(self):
        alpha = 0.0 if (self.chroma == 0 or self.j == 0) else self.chroma / math.sqrt(self.j / 100.0)
        t = (alpha / (1.64 - 0.29 ** V.n) ** 0.73) ** (1.0 / 0.9)
        hr = math.radians(self.hue)
        eHue = 0.25 * (math.cos(hr + 2.0) + 3.8)
        ac = V.aw * (self.j / 100.0) ** (1.0 / V.c / V.z)
        p1 = eHue * (50000.0 / 13.0) * V.nc * V.ncb
        p2 = ac / V.nbb
        hs, hc = math.sin(hr), math.cos(hr)
        gamma = 23.0 * (p2 + 0.305) * t / (23.0 * p1 + 11.0 * t * hc + 108.0 * t * hs)
        a, b = gamma * hc, gamma * hs
        rA = (460.0 * p2 + 451.0 * a + 288.0 * b) / 1403.0
        gA = (460.0 * p2 - 891.0 * a - 261.0 * b) / 1403.0
        bA = (460.0 * p2 - 220.0 * a - 6300.0 * b) / 1403.0
        out = []
        for i, x in enumerate((rA, gA, bA)):
            base = max(0.0, 27.13 * abs(x) / (400.0 - abs(x)))
            cc = math.copysign(1, x) * (100.0 / V.fl) * base ** (1.0 / 0.42)
            out.append(cc / V.rgbD[i])
        rF, gF, bF = out
        x = 1.86206786 * rF - 1.01125463 * gF + 0.14918677 * bF
        y = 0.38752654 * rF + 0.62144744 * gF - 0.00897398 * bF
        z = -0.01584150 * rF - 0.03412294 * gF + 1.04996444 * bF
        return argb_from_xyz(x, y, z)


def find_cam_by_j(hue, chroma, tone):
    low, high, best_dl, best_de, best = 0.0, 100.0, 1000.0, 1000.0, None
    while abs(low - high) > 0.01:
        mid = low + (high - low) / 2
        clipped = Cam16.from_jch(mid, chroma, hue).viewed()
        cl = lstar_from_argb(clipped)
        dl = abs(tone - cl)
        if dl < 0.2:
            cam = Cam16.from_argb(clipped)
            de = cam.distance(Cam16.from_jch(cam.j, cam.chroma, hue))
            if de <= 1.0 and de <= best_de:
                best_dl, best_de, best = dl, de, (cam, clipped)
        if best_dl == 0 and best_de == 0:
            break
        if cl < tone:
            low = mid
        else:
            high = mid
    return best


def hct_argb(hue, chroma, tone):
    if chroma < 1.0 or round(tone) <= 0 or round(tone) >= 100:
        return argb_from_lstar(tone)
    hue = hue % 360.0
    high, mid, low, first, answer = chroma, chroma, 0.0, True, None
    while abs(low - high) >= 0.4:
        pa = find_cam_by_j(hue, mid, tone)
        if first:
            if pa is not None:
                return pa[1]
            first = False
            mid = low + (high - low) / 2
            continue
        if pa is None:
            high = mid
        else:
            answer, low = pa, mid
        mid = low + (high - low) / 2
    return argb_from_lstar(tone) if answer is None else answer[1]


def hexs(argb):
    return '#%02X%02X%02X' % tuple(rgb_of(argb))


def parse(h):
    h = h.lstrip('#')
    return argb_from_rgb(int(h[0:2], 16), int(h[2:4], 16), int(h[4:6], 16))


def hct_of(h):
    a = parse(h)
    cam = Cam16.from_argb(a)
    return cam.hue, cam.chroma, lstar_from_argb(a)


def tone_hex(hue, chroma, tone):
    return hexs(hct_argb(hue, chroma, tone))


if __name__ == '__main__':
    h, c, t = hct_of('#6750A4')
    print('6750A4 hct', round(h, 2), round(c, 2), round(t, 2))
    cc = max(48.0, c)
    print('primary', {k: tone_hex(h, cc, k) for k in (10, 20, 30, 40, 80, 90, 95, 99)})
    print('secondary', {k: tone_hex(h, 16, k) for k in (10, 40, 80, 90)})
    print('neutral', {k: tone_hex(h, 4, k) for k in (10, 90, 99)})
