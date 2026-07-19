package com.arcvitals;

import java.awt.Color;

// Pure maths for the over-max ("overheal") band. current > max happens for hitpoints boosted past the
// real level by a Saradomin brew; the game caps every other tracked stat at its max, so they never
// trigger it. All values derive from the live current/max -- no game reads live here.
final class OverhealState {

    private OverhealState() {
    }

    // True when the live value exceeds the real maximum.
    static boolean boosted(int current, int max) {
        return max > 0 && current > max;
    }

    // The over-max slice as a fraction of the arc, measured from the full end: (current - max) / current.
    // Always in [0, 1) so the band can never recolour the whole bar. 0 when not boosted or max <= 0.
    static double overBand(int current, int max) {
        if (max <= 0 || current <= max) {
            return 0.0;
        }
        return (double) (current - max) / current;
    }

    // The band boundary as a geometric fraction (f = 0 bottom tip, f = 1 top tip) for placing the tick.
    // fillRegion measures [lo, hi] from the fill-anchored end, so the boundary sits at (1 - overBand)
    // in dir-relative terms; converting to geometric coordinates flips it for TOP_DOWN.
    static double tickGeometricFraction(double overBand, FillDirection dir) {
        double boundary = 1.0 - overBand;
        return dir == FillDirection.BOTTOM_UP ? boundary : 1.0 - boundary;
    }

    // A bright, near-opaque variant of the band colour for the real-max tick: one third band, two
    // thirds white, so it reads on any bar colour while keeping a hint of the band's hue.
    static Color tickColor(Color band) {
        int r = (band.getRed() + 255 * 2) / 3;
        int g = (band.getGreen() + 255 * 2) / 3;
        int b = (band.getBlue() + 255 * 2) / 3;
        return new Color(r, g, b, 235);
    }
}
