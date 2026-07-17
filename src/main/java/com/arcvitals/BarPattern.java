package com.arcvitals;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.MultipleGradientPaint;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

// The pattern axis: the base texture painted under a bar's fill. Each non-NONE value builds a
// small, tileable, opaque tile tinted to the bar's colour (shades via BarColors); the caller wraps
// it in a TexturePaint. NONE has no tile and signals a solid fill. Public because it is a config
// return type (a package-private @ConfigItem enum trips IllegalAccessError via the config proxy).
public enum BarPattern {

    NONE("None"),

    BRUSHED("Brushed metal") {
        @Override
        BufferedImage tile(Color base) {
            int w = 3, h = 18;
            BufferedImage t = newTile(w, h);
            Graphics2D g = tileG(t);
            g.setColor(base);
            g.fillRect(0, 0, w, h);
            for (int y = 0; y < h; y++) {
                double n = frac(Math.sin(y * 12.9898) * 43758.5453);
                Color shade = n > 0.5 ? BarColors.lighten(base, 0.06 + n * 0.16)
                    : BarColors.scale(base, 0.80 + n * 0.10);
                g.setColor(shade);
                g.fillRect(0, y, w, 1);
            }
            g.setColor(BarColors.lighten(base, 0.55));
            g.fillRect(0, 3, w, 1);
            g.fillRect(0, 12, w, 1);
            g.dispose();
            return t;
        }
    },

    WEAVE("Carbon weave") {
        @Override
        BufferedImage tile(Color base) {
            int s = 8, half = 4;
            BufferedImage t = newTile(s, s);
            Graphics2D g = tileG(t);
            g.setColor(base);
            g.fillRect(0, 0, s, s);
            Color hi = BarColors.lighten(base, 0.30);
            Color lo = BarColors.scale(base, 0.60);
            cell(g, 0, 0, half, hi, lo);
            cell(g, half, half, half, hi, lo);
            cell(g, half, 0, half, lo, hi);
            cell(g, 0, half, half, lo, hi);
            g.setColor(BarColors.scale(base, 0.42));
            g.setStroke(new BasicStroke(1f));
            g.drawLine(half, 0, half, s);
            g.drawLine(0, half, s, half);
            g.dispose();
            return t;
        }
    },

    RIVETS("Riveted plate") {
        @Override
        BufferedImage tile(Color base) {
            int s = 16;
            BufferedImage t = newTile(s, s);
            Graphics2D g = tileG(t);
            g.setColor(BarColors.scale(base, 0.80));
            g.fillRect(0, 0, s, s);
            dome(g, s / 2f, s / 2f, base);
            dome(g, 0, 0, base);
            dome(g, s, 0, base);
            dome(g, 0, s, base);
            dome(g, s, s, base);
            g.dispose();
            return t;
        }
    },

    SCALES("Dragon scale") {
        @Override
        BufferedImage tile(Color base) {
            int sw = 16, vstep = 9, h = vstep * 2;
            BufferedImage t = newTile(sw, h);
            Graphics2D g = tileG(t);
            g.setColor(BarColors.scale(base, 0.55));
            g.fillRect(0, 0, sw, h);
            for (int r = -1; r <= 2; r++) {
                int y = r * vstep;
                int xoff = (Math.floorMod(r, 2) == 1) ? sw / 2 : 0;
                for (int x = -sw; x <= sw * 2; x += sw) {
                    scallop(g, x + xoff, y, sw, vstep, base);
                }
            }
            g.dispose();
            return t;
        }
    },

    RUNE("Rune etch") {
        @Override
        BufferedImage tile(Color base) {
            int w = 40, h = 18;
            BufferedImage t = newTile(w, h);
            Graphics2D g = tileG(t);
            g.setColor(BarColors.scale(base, 0.85));
            g.fillRect(0, 0, w, h);
            // one row of two distinct glyphs, centred vertically so vertical tiling does not
            // slice a glyph on thin/real bars (the fix over the 40x40 prototype tile).
            glyph(g, 11, 10, 0, BarColors.scale(base, 0.45));
            glyph(g, 10, 9, 0, BarColors.lighten(base, 0.55));
            glyph(g, 31, 10, 1, BarColors.scale(base, 0.45));
            glyph(g, 30, 9, 1, BarColors.lighten(base, 0.55));
            g.dispose();
            return t;
        }
    },

    MESH("Tech mesh") {
        @Override
        BufferedImage tile(Color base) {
            int s = 8;
            BufferedImage t = newTile(s, s);
            Graphics2D g = tileG(t);
            g.setColor(base);
            g.fillRect(0, 0, s, s);
            g.setColor(BarColors.lighten(base, 0.42));
            g.setStroke(new BasicStroke(1f));
            g.drawLine(0, s, s, 0);
            g.drawLine(0, 0, s, s);
            g.setColor(BarColors.lighten(base, 0.62));
            g.fillRect(0, 0, 1, 1);
            g.dispose();
            return t;
        }
    };

    private final String label;

    BarPattern(String label) {
        this.label = label;
    }

    // The tinted, tileable base texture; null for NONE (solid fill).
    BufferedImage tile(Color base) {
        return null;
    }

    @Override
    public String toString() {
        return label;
    }

    // ---- shared tile helpers ----

    private static BufferedImage newTile(int w, int h) {
        return new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    }

    private static Graphics2D tileG(BufferedImage img) {
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        return g;
    }

    private static double frac(double x) {
        return x - Math.floor(x);
    }

    private static void cell(Graphics2D g, int x, int y, int n, Color a, Color b) {
        g.setPaint(new GradientPaint(x, y, a, x + n, y + n, b));
        g.fillRect(x, y, n, n);
    }

    private static void dome(Graphics2D g, float cx, float cy, Color base) {
        float r = 4.6f;
        float[] fr = {0f, 0.6f, 1f};
        Color[] cols = {BarColors.lighten(base, 0.55), base, BarColors.scale(base, 0.50)};
        RadialGradientPaint rg = new RadialGradientPaint(
            new Point2D.Float(cx, cy), r, new Point2D.Float(cx - r * 0.35f, cy - r * 0.35f),
            fr, cols, MultipleGradientPaint.CycleMethod.NO_CYCLE);
        g.setPaint(rg);
        g.fill(new Ellipse2D.Float(cx - r, cy - r, 2 * r, 2 * r));
    }

    private static void scallop(Graphics2D g, int x, int y, int sw, int vstep, Color base) {
        Path2D p = new Path2D.Double();
        p.moveTo(x, y);
        p.quadTo(x, y + vstep, x + sw / 2.0, y + vstep);
        p.quadTo(x + sw, y + vstep, x + sw, y);
        p.closePath();
        g.setPaint(new GradientPaint(0, y, BarColors.scale(base, 0.55), 0, y + vstep,
            BarColors.lighten(base, 0.35)));
        g.fill(p);
        g.setColor(BarColors.scale(base, 0.40));
        g.setStroke(new BasicStroke(1f));
        g.draw(p);
    }

    private static void glyph(Graphics2D g, int x, int y, int k, Color col) {
        g.setColor(col);
        g.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        Path2D p = new Path2D.Double();
        if (k == 0) {
            // angular "F" rune
            p.moveTo(x - 4, y - 5);
            p.lineTo(x - 4, y + 5);
            p.moveTo(x - 4, y - 5);
            p.lineTo(x + 4, y - 5);
            p.moveTo(x - 4, y);
            p.lineTo(x + 2, y);
        } else {
            // cross rune
            p.moveTo(x - 5, y - 5);
            p.lineTo(x + 5, y + 5);
            p.moveTo(x + 5, y - 5);
            p.lineTo(x - 5, y + 5);
        }
        g.draw(p);
    }
}
