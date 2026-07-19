package com.arcvitals;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Line2D;

// Draws one bar: track, then the fill style over the fill region, then the restore preview, then
// the outline. The resolved base Paint (a solid colour, or a tinted pattern texture) is fed into
// style.paint via the FillContext.
final class BarRenderer {

    private BarRenderer() {
    }

    static void draw(Graphics2D g, Geometry geo, FillStyle style, FillDirection dir, double fraction,
                     Paint base, Color color, int segments, Color track, Color outlineColor, int outlineWidth,
                     double previewFraction, Color previewColor,
                     double overBand, Color overColor, Color overTickColor) {
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

        // Overheal band: the over-max slice at the full end, drawn over the fill. fillRegion measures
        // from the fill-anchored end, so [1 - overBand, 1] is always the full end regardless of dir.
        if (overColor != null && overBand > 0) {
            g.setColor(overColor);
            g.fill(geo.fillRegion(1.0 - overBand, 1.0, dir));
        }

        // Outline: a border around the whole body perimeter, including the ends. The BasicStroke is
        // rebuilt each frame rather than cached: it is a handful of small objects per bar, far cheaper
        // than the shape fills around it, so caching them would add state for no measurable gain.
        if (outlineColor != null && outlineWidth > 0) {
            g.setColor(outlineColor);
            g.setStroke(new BasicStroke(outlineWidth));
            g.draw(geo.body());
        }

        // Overheal tick: a short line across the bar at the real-max boundary, drawn last so it reads
        // on top of the band and outline.
        if (overTickColor != null && overBand > 0) {
            double tf = OverhealState.tickGeometricFraction(overBand, dir);
            double[] p = geo.pointAt(tf);
            double[] n = geo.normalAt(tf);
            double half = geo.thickness() / 2.0 + 1.5;
            g.setColor(overTickColor);
            g.setStroke(new BasicStroke(2f));
            g.draw(new Line2D.Double(p[0] - n[0] * half, p[1] - n[1] * half,
                p[0] + n[0] * half, p[1] + n[1] * half));
        }

        g.setStroke(oldStroke);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            oldAa == null ? RenderingHints.VALUE_ANTIALIAS_DEFAULT : oldAa);
    }
}
