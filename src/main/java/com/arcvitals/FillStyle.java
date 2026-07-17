package com.arcvitals;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Area;

// The fill-style axis: how the filled portion of a bar is painted. Public because it is a
// config return type. `base` is the base fill Paint (a solid colour in Phase 1; a tinted texture
// once patterns land) and `color` is the bar's solid colour, used to derive highlights.
public enum FillStyle {

    SMOOTH("Smooth") {
        @Override
        void paint(Graphics2D g, Geometry geo, FillDirection dir, double fraction, Paint base, Color color) {
            double frac = clamp01(fraction);
            if (frac <= 0.0) {
                return;
            }
            g.setPaint(base);
            g.fill(geo.fillRegion(0.0, frac, dir));
        }
    },

    GLOSS("Glossy") {
        @Override
        void paint(Graphics2D g, Geometry geo, FillDirection dir, double fraction, Paint base, Color color) {
            double frac = clamp01(fraction);
            if (frac <= 0.0) {
                return;
            }
            Area region = geo.fillRegion(0.0, frac, dir);
            g.setPaint(base);
            g.fill(region);
            Shape oldClip = g.getClip();
            Stroke oldStroke = g.getStroke();
            g.clip(region);
            int t = geo.thickness();
            Shape cl = geo.centerline();
            strokeCenterline(g, cl, t * 0.82f, color);
            strokeCenterline(g, cl, t * 0.5f, BarColors.lighten(color, 0.26));
            strokeCenterline(g, cl, t * 0.24f, BarColors.lighten(color, 0.6));
            g.setStroke(oldStroke);
            g.setClip(oldClip);
        }
    },

    GRADIENT("Gradient") {
        @Override
        void paint(Graphics2D g, Geometry geo, FillDirection dir, double fraction, Paint base, Color color) {
            double frac = clamp01(fraction);
            if (frac <= 0.0) {
                return;
            }
            double[] anchor = geo.pointAt(dir == FillDirection.BOTTOM_UP ? 0.0 : 1.0);
            double[] far = geo.pointAt(dir == FillDirection.BOTTOM_UP ? 1.0 : 0.0);
            Color bright = BarColors.lighten(color, 0.35);
            Color dark = BarColors.scale(color, 0.5);
            if (Math.hypot(anchor[0] - far[0], anchor[1] - far[1]) < 1.0) {
                g.setPaint(base);
                g.fill(geo.fillRegion(0.0, frac, dir));
                return;
            }
            GradientPaint gp = new GradientPaint(
                (float) anchor[0], (float) anchor[1], bright,
                (float) far[0], (float) far[1], dark);
            g.setPaint(gp);
            g.fill(geo.fillRegion(0.0, frac, dir));
        }
    },

    SEGMENTED("Segmented") {
        @Override
        void paint(Graphics2D g, Geometry geo, FillDirection dir, double fraction, Paint base, Color color) {
            double frac = clamp01(fraction);
            if (frac <= 0.0) {
                return;
            }
            g.setPaint(base);
            int segs = 14;
            double cell = 1.0 / segs;
            double onFrac = 0.72; // fraction of each cell that is a pip; the rest is a gap
            for (int i = 0; i < segs; i++) {
                double a = i * cell;
                if (a >= frac) {
                    break;
                }
                double b = Math.min(a + cell * onFrac, frac);
                if (b > a) {
                    g.fill(geo.fillRegion(a, b, dir));
                }
            }
        }
    },

    GLOW("Glow") {
        @Override
        void paint(Graphics2D g, Geometry geo, FillDirection dir, double fraction, Paint base, Color color) {
            SMOOTH.paint(g, geo, dir, fraction, base, color); // replaced in Task 9
        }
    },

    NOTCHED("Notched") {
        @Override
        void paint(Graphics2D g, Geometry geo, FillDirection dir, double fraction, Paint base, Color color) {
            SMOOTH.paint(g, geo, dir, fraction, base, color); // replaced in Task 10
        }
    };

    private final String label;

    FillStyle(String label) {
        this.label = label;
    }

    abstract void paint(Graphics2D g, Geometry geo, FillDirection dir, double fraction, Paint base, Color color);

    @Override
    public String toString() {
        return label;
    }

    // Draws the centreline with a round-capped stroke of the given width and colour.
    static void strokeCenterline(Graphics2D g, Shape centerline, float width, Color c) {
        g.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(c);
        g.draw(centerline);
    }

    static double clamp01(double v) {
        if (v < 0.0) {
            return 0.0;
        }
        return v > 1.0 ? 1.0 : v;
    }
}
