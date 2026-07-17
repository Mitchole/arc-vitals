package com.arcvitals;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;

// Draws one bar: track, then the fill style over the fill region, then the restore preview, then
// the outline. The Phase-2 pattern axis will feed a tinted Paint into style.paint; today it is the
// solid fill colour.
final class BarRenderer {

    private BarRenderer() {
    }

    static void draw(Graphics2D g, Geometry geo, FillStyle style, FillDirection dir, double fraction,
                     Color fill, Color track, Color outlineColor, int outlineWidth,
                     double previewFraction, Color previewColor) {
        Object oldAa = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        Stroke oldStroke = g.getStroke();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Track: the whole body.
        g.setColor(track);
        g.fill(geo.body());

        double frac = clamp01(fraction);
        style.paint(g, geo, dir, frac, fill, fill);

        // Preview: from the current level up to the projected level.
        double prev = clamp01(previewFraction);
        if (previewColor != null && prev > frac) {
            g.setColor(previewColor);
            g.fill(geo.fillRegion(frac, prev, dir));
        }

        // Outline: a border around the whole body perimeter, including the ends.
        if (outlineColor != null && outlineWidth > 0) {
            g.setColor(outlineColor);
            g.setStroke(new BasicStroke(outlineWidth));
            g.draw(geo.body());
        }

        g.setStroke(oldStroke);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            oldAa == null ? RenderingHints.VALUE_ANTIALIAS_DEFAULT : oldAa);
    }

    private static double clamp01(double v) {
        if (v < 0.0) {
            return 0.0;
        }
        return v > 1.0 ? 1.0 : v;
    }
}
