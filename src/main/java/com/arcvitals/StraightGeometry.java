package com.arcvitals;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

// Vertical-capsule geometry: a straight bar with no bow. The curve parameter is ignored.
// Nested bars step further from centre by (thickness + spacing) per index.
final class StraightGeometry implements Geometry {

    private final double centerX;
    private final double centerY;
    private final int size;
    private final int thickness;
    private final Shape body;

    StraightGeometry(int cx, int cy, int size, int thickness, int baseGap, int spacing,
                     int index, boolean leftSide, boolean flatEnds) {
        this.centerX = leftSide
            ? (cx - baseGap - index * (double) (thickness + spacing))
            : (cx + baseGap + index * (double) (thickness + spacing));
        this.centerY = cy;
        this.size = size;
        this.thickness = thickness;
        double yTop = cy - size / 2.0;
        double yBottom = cy + size / 2.0;
        Line2D line = new Line2D.Double(centerX, yTop, centerX, yBottom);
        int cap = flatEnds ? BasicStroke.CAP_BUTT : BasicStroke.CAP_ROUND;
        this.body = new BasicStroke(thickness, cap, BasicStroke.JOIN_ROUND).createStrokedShape(line);
    }

    @Override
    public Shape body() {
        return body;
    }

    @Override
    public Shape centerline() {
        return new Line2D.Double(centerX, centerY - size / 2.0, centerX, centerY + size / 2.0);
    }

    @Override
    public int thickness() {
        return thickness;
    }

    @Override
    public Area fillRegion(double lo, double hi, FillDirection dir) {
        if (hi <= lo) {
            return new Area();
        }
        double yTop = centerY - size / 2.0;
        double yBottom = centerY + size / 2.0;
        double loY;
        double hiY;
        if (dir == FillDirection.BOTTOM_UP) {
            double a = yBottom - lo * size;
            double b = yBottom - hi * size;
            loY = Math.min(a, b);
            hiY = Math.max(a, b);
        } else {
            double a = yTop + lo * size;
            double b = yTop + hi * size;
            loY = Math.min(a, b);
            hiY = Math.max(a, b);
        }
        // Pad past the true tips so the round/flat cap fills cleanly.
        double pad = thickness / 2.0 + 0.5;
        if (lo <= 0.0) {
            if (dir == FillDirection.BOTTOM_UP) {
                hiY += pad;
            } else {
                loY -= pad;
            }
        }
        if (hi >= 1.0) {
            if (dir == FillDirection.BOTTOM_UP) {
                loY -= pad;
            } else {
                hiY += pad;
            }
        }
        Rectangle2D band = new Rectangle2D.Double(centerX - thickness, loY, thickness * 2.0, hiY - loY);
        Area area = new Area(body);
        area.intersect(new Area(band));
        return area;
    }

    @Override
    public double[] pointAt(double f) {
        return new double[]{centerX, (centerY + size / 2.0) - f * size};
    }

    @Override
    public double[] normalAt(double f) {
        return new double[]{1.0, 0.0};
    }
}
