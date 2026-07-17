package com.arcvitals;

import java.awt.Color;

// Small colour maths shared by the fill styles, kept in one place so lighten/scale behave
// identically everywhere.
final class BarColors {

    private BarColors() {
    }

    // Moves each channel a fraction f (0..1) toward white; alpha preserved.
    static Color lighten(Color c, double f) {
        return new Color(
            clamp((int) Math.round(c.getRed() + (255 - c.getRed()) * f)),
            clamp((int) Math.round(c.getGreen() + (255 - c.getGreen()) * f)),
            clamp((int) Math.round(c.getBlue() + (255 - c.getBlue()) * f)),
            c.getAlpha());
    }

    // Multiplies each channel by f (darken for f < 1); alpha preserved.
    static Color scale(Color c, double f) {
        return new Color(
            clamp((int) Math.round(c.getRed() * f)),
            clamp((int) Math.round(c.getGreen() * f)),
            clamp((int) Math.round(c.getBlue() * f)),
            c.getAlpha());
    }

    private static int clamp(int v) {
        if (v < 0) {
            return 0;
        }
        return v > 255 ? 255 : v;
    }
}
