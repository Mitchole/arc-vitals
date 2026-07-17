package com.arcvitals;

import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

// A curved bar whose stroke width tapers along its length per a TaperProfile (Leaf or Horn). It
// shares the arc centreline with ArcGeometry, but its body is a filled variable-width Path2D rather
// than a constant-width stroked capsule. Fill, centreline, and tick maths mirror ArcGeometry so
// every fill style and pattern works unchanged. flatEnds does not apply (the profile shapes the tips).
final class TaperedGeometry implements Geometry {

    private static final int SAMPLES = 96;

    private final Shape body;
    private final double centerX;
    private final double centerY;
    private final double radius;
    private final int thickness;
    private final double topAngle;
    private final double bottomAngle;

    TaperedGeometry(int cx, int cy, int size, int thickness, int baseGap, int spacing,
                    int curveDegrees, int index, boolean leftSide, TaperProfile profile) {
        ArcCenterline c = new ArcCenterline(cx, cy, size, thickness, baseGap, spacing,
            curveDegrees, index, leftSide);
        this.centerX = c.centerX;
        this.centerY = c.centerY;
        this.radius = c.radius;
        this.thickness = thickness;
        this.topAngle = c.topAngle;
        this.bottomAngle = c.bottomAngle;
        this.body = buildBody(profile);
    }

    private Shape buildBody(TaperProfile profile) {
        double halfT = thickness / 2.0;
        double extent = topAngle - bottomAngle;
        Path2D.Double p = new Path2D.Double();
        // Outer edge, f: 0 -> 1.
        for (int i = 0; i <= SAMPLES; i++) {
            double f = i / (double) SAMPLES;
            double[] pn = pointNormal(bottomAngle + extent * f);
            double h = halfT * clampScale(profile.halfScale(f));
            double x = pn[0] + pn[2] * h;
            double y = pn[1] + pn[3] * h;
            if (i == 0) {
                p.moveTo(x, y);
            } else {
                p.lineTo(x, y);
            }
        }
        // Inner edge, f: 1 -> 0.
        for (int i = SAMPLES; i >= 0; i--) {
            double f = i / (double) SAMPLES;
            double[] pn = pointNormal(bottomAngle + extent * f);
            double h = halfT * clampScale(profile.halfScale(f));
            p.lineTo(pn[0] - pn[2] * h, pn[1] - pn[3] * h);
        }
        p.closePath();
        return p;
    }

    private static double clampScale(double s) {
        if (s < 0.0) {
            return 0.0;
        }
        return s > 1.0 ? 1.0 : s;
    }

    // {x, y, nx, ny}: on-screen point on the centreline arc at the given Arc2D angle, plus the
    // outward unit normal. Reads from an Arc2D start point to avoid flipped-Y sign mistakes.
    private double[] pointNormal(double angleDeg) {
        Arc2D probe = new Arc2D.Double(centerX - radius, centerY - radius, radius * 2.0, radius * 2.0,
            angleDeg, 0.0, Arc2D.OPEN);
        Point2D pt = probe.getStartPoint();
        double dx = pt.getX() - centerX;
        double dy = pt.getY() - centerY;
        double len = Math.hypot(dx, dy);
        if (len < 1e-6) {
            return new double[]{pt.getX(), pt.getY(), 1.0, 0.0};
        }
        return new double[]{pt.getX(), pt.getY(), dx / len, dy / len};
    }

    @Override
    public Shape body() {
        return body;
    }

    @Override
    public Shape centerline() {
        double extent = topAngle - bottomAngle;
        return new Arc2D.Double(centerX - radius, centerY - radius, radius * 2.0, radius * 2.0,
            bottomAngle, extent, Arc2D.OPEN);
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
        double anchorAngle;
        double fullExtent;
        if (dir == FillDirection.BOTTOM_UP) {
            anchorAngle = bottomAngle;
            fullExtent = topAngle - bottomAngle;
        } else {
            anchorAngle = topAngle;
            fullExtent = bottomAngle - topAngle;
        }
        double sign = Math.signum(fullExtent);
        if (sign == 0.0) {
            return new Area();
        }
        double padDeg = Math.toDegrees((thickness / 2.0) / radius) + 0.5;
        double startDeg = anchorAngle + fullExtent * lo;
        double endDeg = anchorAngle + fullExtent * hi;
        if (lo <= 0.0) {
            startDeg -= sign * padDeg;
        }
        if (hi >= 1.0) {
            endDeg += sign * padDeg;
        }
        double r = radius + thickness;
        Arc2D wedge = new Arc2D.Double(centerX - r, centerY - r, r * 2.0, r * 2.0,
            startDeg, endDeg - startDeg, Arc2D.PIE);
        Area area = new Area(body);
        area.intersect(new Area(wedge));
        return area;
    }

    @Override
    public double[] pointAt(double f) {
        double angle = bottomAngle + (topAngle - bottomAngle) * f;
        Arc2D probe = new Arc2D.Double(centerX - radius, centerY - radius, radius * 2.0, radius * 2.0,
            angle, 0.0, Arc2D.OPEN);
        Point2D p = probe.getStartPoint();
        return new double[]{p.getX(), p.getY()};
    }

    @Override
    public double[] normalAt(double f) {
        double[] p = pointAt(f);
        double dx = p[0] - centerX;
        double dy = p[1] - centerY;
        double len = Math.hypot(dx, dy);
        if (len < 1e-6) {
            return new double[]{1.0, 0.0};
        }
        return new double[]{dx / len, dy / len};
    }
}
