package com.arcvitals;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Point2D;

// A half-ring centred on the player: a fixed 180-degree stroked semicircle at radius
// baseGap + index*(thickness+spacing), filled by an angular pie-wedge. Left and right halves of the
// same nesting level compose one full ring around the player. Ignores size and curve; the sweep is
// always 180 degrees per side. Fill maths mirror ArcGeometry so every fill style and pattern works.
final class RingGeometry implements Geometry {

    private final Shape body;
    private final double centerX;
    private final double centerY;
    private final double radius;
    private final int thickness;
    private final double topAngle;
    private final double bottomAngle;

    RingGeometry(int cx, int cy, int thickness, int baseGap, int spacing,
                 int index, boolean leftSide, boolean flatEnds) {
        double r = baseGap + index * (double) (thickness + spacing);
        double startAngle = 90.0;                   // top tip
        double sweep = leftSide ? 180.0 : -180.0;   // left half sweeps west, right half east
        Arc2D arc = new Arc2D.Double(cx - r, cy - r, r * 2.0, r * 2.0, startAngle, sweep, Arc2D.OPEN);
        int cap = flatEnds ? BasicStroke.CAP_BUTT : BasicStroke.CAP_ROUND;
        this.body = new BasicStroke(thickness, cap, BasicStroke.JOIN_ROUND).createStrokedShape(arc);
        this.centerX = cx;
        this.centerY = cy;
        this.radius = r;
        this.thickness = thickness;
        this.topAngle = startAngle;             // 90
        this.bottomAngle = startAngle + sweep;  // left 270, right -90
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
