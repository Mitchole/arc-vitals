package com.arcvitals;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Stroke;

// Draws one bar: track, then the fill style over the fill region, then the restore preview, then
// the outline. The resolved base Paint (a solid colour, or a tinted pattern texture) is fed into
// style.paint via the FillContext.
final class BarRenderer {

    private BarRenderer() {
    }

    static void draw(Graphics2D g, Geometry geo, FillStyle style, FillDirection dir, double fraction,
                     Paint base, Color color, int segments, Color track, Color outlineColor, int outlineWidth,
                     double previewFraction, Color previewColor) {
        Object oldAa = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        Stroke oldStroke = g.getStroke();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Track: the whole body.
        g.setColor(track);
        g.fill(geo.body());

        double frac = BarColors.clamp01(fraction);
        style.paint(g, geo, dir, frac, new FillContext(base, color, segments));

        // Preview: from the current level up to the projected level.
        double prev = BarColors.clamp01(previewFraction);
        if (previewColor != null && prev > frac) {
            g.setColor(previewColor);
            g.fill(geo.fillRegion(frac, prev, dir));
        }

        // Outline: a border around the whole body perimeter, including the ends. The BasicStroke is
        // rebuilt each frame rather than cached: it is a handful of small objects per bar, far cheaper
        // than the shape fills around it, so caching them would add state for no measurable gain.
        if (outlineColor != null && outlineWidth > 0) {
            g.setColor(outlineColor);
            g.setStroke(new BasicStroke(outlineWidth));
            g.draw(geo.body());
        }

        g.setStroke(oldStroke);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            oldAa == null ? RenderingHints.VALUE_ANTIALIAS_DEFAULT : oldAa);
    }
}
