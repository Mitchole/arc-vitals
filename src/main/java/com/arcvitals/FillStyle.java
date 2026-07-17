package com.arcvitals;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;

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
            SMOOTH.paint(g, geo, dir, fraction, base, color); // replaced in Task 7
        }
    },

    GRADIENT("Gradient") {
        @Override
        void paint(Graphics2D g, Geometry geo, FillDirection dir, double fraction, Paint base, Color color) {
            SMOOTH.paint(g, geo, dir, fraction, base, color); // replaced in Task 6
        }
    },

    SEGMENTED("Segmented") {
        @Override
        void paint(Graphics2D g, Geometry geo, FillDirection dir, double fraction, Paint base, Color color) {
            SMOOTH.paint(g, geo, dir, fraction, base, color); // replaced in Task 8
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
