package com.arcvitals;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Arc2D;

final class ArcBar {

    private ArcBar() {
    }

    static void draw(Graphics2D g, int cx, int cy, int size, int thickness, int gap, int curveDegrees,
                     boolean leftSide, FillDirection dir, double fraction, Color fill, Color track) {
        double half = Math.toRadians(curveDegrees) / 2.0;
        double sinHalf = Math.sin(half);
        if (sinHalf < 1e-4) {
            sinHalf = 1e-4;
        }
        // Radius chosen so the arc's chord height equals the configured bar height.
        double radius = (size / 2.0) / sinHalf;
        double circleCenterX = leftSide ? (cx - gap + radius) : (cx + gap - radius);
        double boundX = circleCenterX - radius;
        double boundY = cy - radius;
        double diameter = radius * 2.0;
        double centreAngle = leftSide ? 180.0 : 0.0;
        double startAngle = centreAngle - (curveDegrees / 2.0);
        Arc2D arc = new Arc2D.Double(boundX, boundY, diameter, diameter, startAngle, curveDegrees, Arc2D.OPEN);

        Object oldAa = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        Stroke oldStroke = g.getStroke();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setStroke(new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        g.setColor(track);
        g.draw(arc);

        double frac = fraction;
        if (frac < 0.0) {
            frac = 0.0;
        } else if (frac > 1.0) {
            frac = 1.0;
        }
        int fillH = (int) Math.round(size * frac);
        if (fillH > 0) {
            // The round stroke caps bulge ~thickness/2 past the arc endpoints, so pad the
            // fill band to cover them: always include the anchored-end cap, and add the
            // far-end cap only when the bar is full (a partial bar keeps a flat liquid level).
            boolean full = frac >= 1.0;
            int cap = thickness;
            int bandTop;
            int bandBottom;
            if (dir == FillDirection.BOTTOM_UP) {
                bandTop = (cy + size / 2 - fillH) - (full ? cap : 0);
                bandBottom = cy + size / 2 + cap;
            } else {
                bandTop = cy - size / 2 - cap;
                bandBottom = (cy - size / 2 + fillH) + (full ? cap : 0);
            }
            int clipX = (int) Math.floor(boundX - thickness);
            int clipW = (int) Math.ceil(diameter + thickness * 2);
            Shape oldClip = g.getClip();
            g.clipRect(clipX, bandTop, clipW, bandBottom - bandTop);
            g.setColor(fill);
            g.draw(arc);
            g.setClip(oldClip);
        }

        g.setStroke(oldStroke);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            oldAa == null ? RenderingHints.VALUE_ANTIALIAS_DEFAULT : oldAa);
    }
}
