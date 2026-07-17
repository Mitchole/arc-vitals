package com.arcvitals;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Point2D;

// The curved-capsule geometry: a stroked circular arc, filled by an angular pie-wedge.
// Angles are Arc2D degrees (0 = east, positive counterclockwise on screen).
final class ArcGeometry implements Geometry {

    private final Shape capsule;
    private final double centerX;
    private final double centerY;
    private final double radius;
    private final int thickness;
    private final double topAngle;
    private final double bottomAngle;

    ArcGeometry(int cx, int cy, int size, int thickness, int baseGap, int spacing,
                int curveDegrees, int index, boolean leftSide, boolean flatEnds) {
        ArcCenterline c = new ArcCenterline(cx, cy, size, thickness, baseGap, spacing,
            curveDegrees, index, leftSide);
        double boundX = c.centerX - c.radius;
        double boundY = c.centerY - c.radius;
        double diameter = c.radius * 2.0;
        Arc2D arc = new Arc2D.Double(boundX, boundY, diameter, diameter, c.startAngle, c.sweepDegrees, Arc2D.OPEN);

        int cap = flatEnds ? BasicStroke.CAP_BUTT : BasicStroke.CAP_ROUND;
        this.capsule = new BasicStroke(thickness, cap, BasicStroke.JOIN_ROUND).createStrokedShape(arc);
        this.centerX = c.centerX;
        this.centerY = c.centerY;
        this.radius = c.radius;
        this.thickness = thickness;
        this.topAngle = c.topAngle;
        this.bottomAngle = c.bottomAngle;
    }

    @Override
    public Shape body() {
        return capsule;
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
        // Angular width (degrees) of the stroke's end cap at this radius.
        double padDeg = Math.toDegrees((thickness / 2.0) / radius) + 0.5;
        double startDeg = anchorAngle + fullExtent * lo;
        double endDeg = anchorAngle + fullExtent * hi;
        if (lo <= 0.0) {
            startDeg -= sign * padDeg;
        }
        if (hi >= 1.0) {
            endDeg += sign * padDeg;
        }
        double r = radius + thickness; // safely beyond the capsule's outer edge
        Arc2D wedge = new Arc2D.Double(centerX - r, centerY - r, r * 2.0, r * 2.0,
            startDeg, endDeg - startDeg, Arc2D.PIE);
        Area area = new Area(capsule);
        area.intersect(new Area(wedge));
        return area;
    }

    @Override
    public double[] pointAt(double f) {
        double angle = bottomAngle + (topAngle - bottomAngle) * f;
        // Read the on-screen point from an Arc2D start point to avoid flipped-Y sign mistakes.
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
