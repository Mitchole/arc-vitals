package com.arcvitals;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;

final class ArcBar {

    private ArcBar() {
    }

    static void draw(Graphics2D g, int cx, int cy, int size, int thickness, int gap, int curveDegrees,
                     boolean leftSide, FillDirection dir, double fraction, Color fill, Color track,
                     boolean flatEnds, Color outlineColor, int outlineWidth,
                     double previewFraction, Color previewColor) {
        double half = Math.toRadians(curveDegrees) / 2.0;
        double sinHalf = Math.sin(half);
        if (sinHalf < 1e-4) {
            sinHalf = 1e-4;
        }
        double radius = (size / 2.0) / sinHalf;
        double circleCenterX = leftSide ? (cx - gap + radius) : (cx + gap - radius);
        double boundX = circleCenterX - radius;
        double boundY = cy - radius;
        double diameter = radius * 2.0;
        double centreAngle = leftSide ? 180.0 : 0.0;
        double startAngle = centreAngle - (curveDegrees / 2.0);
        Arc2D arc = new Arc2D.Double(boundX, boundY, diameter, diameter, startAngle, curveDegrees, Arc2D.OPEN);

        int cap = flatEnds ? BasicStroke.CAP_BUTT : BasicStroke.CAP_ROUND;
        Shape capsule = new BasicStroke(thickness, cap, BasicStroke.JOIN_ROUND).createStrokedShape(arc);

        Object oldAa = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        Stroke oldStroke = g.getStroke();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Track: the whole capsule.
        g.setColor(track);
        g.fill(capsule);

        double frac = clamp01(fraction);
        // Current fill: from the anchored end up to the current level.
        fillBand(g, capsule, cy, size, thickness, dir, 0.0, frac, fill);

        // Preview: from the current level up to the projected level.
        double prev = clamp01(previewFraction);
        if (previewColor != null && prev > frac) {
            fillBand(g, capsule, cy, size, thickness, dir, frac, prev, previewColor);
        }

        // Outline: a border around the whole capsule perimeter, including the ends.
        if (outlineColor != null && outlineWidth > 0) {
            g.setColor(outlineColor);
            g.setStroke(new BasicStroke(outlineWidth));
            g.draw(capsule);
        }

        g.setStroke(oldStroke);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            oldAa == null ? RenderingHints.VALUE_ANTIALIAS_DEFAULT : oldAa);
    }

    // Fills the capsule within the vertical band covering fractions [lo, hi] of the bar height,
    // measured from the anchored end. Pads by the stroke thickness at the very ends so the flat/round
    // caps fill cleanly; the boundary between two adjacent bands (lo>0 or hi<1) stays a flat cut.
    private static void fillBand(Graphics2D g, Shape capsule, int cy, int size, int thickness,
                                 FillDirection dir, double lo, double hi, Color color) {
        if (hi <= lo) {
            return;
        }
        int loPx = (int) Math.round(size * lo);
        int hiPx = (int) Math.round(size * hi);
        int pad = thickness;
        int bandTop;
        int bandBottom;
        if (dir == FillDirection.BOTTOM_UP) {
            bandBottom = (cy + size / 2 - loPx) + (lo <= 0.0 ? pad : 0);
            bandTop = (cy + size / 2 - hiPx) - (hi >= 1.0 ? pad : 0);
        } else {
            bandTop = (cy - size / 2 + loPx) - (lo <= 0.0 ? pad : 0);
            bandBottom = (cy - size / 2 + hiPx) + (hi >= 1.0 ? pad : 0);
        }
        Rectangle2D b = capsule.getBounds2D();
        int clipX = (int) Math.floor(b.getX() - thickness);
        int clipW = (int) Math.ceil(b.getWidth() + thickness * 2);
        Shape oldClip = g.getClip();
        g.clipRect(clipX, bandTop, clipW, bandBottom - bandTop);
        g.setColor(color);
        g.fill(capsule);
        g.setClip(oldClip);
    }

    private static double clamp01(double v) {
        if (v < 0.0) {
            return 0.0;
        }
        return v > 1.0 ? 1.0 : v;
    }
}
