package com.arcvitals;

import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Point2D;

// Shared base for the curved bar geometries introduced in Phase 3 (Tapered and Ring). It owns the
// angular-wedge fill maths and the arc-derived centreline / point / normal, all of which are
// identical between those two shapes. Subclasses supply only their own body() outline and set the
// shared circle fields via the constructor. ArcGeometry deliberately does NOT extend this: the spec
// freezes ArcGeometry to guarantee byte-identical curved-shape output, so it keeps its own copy.
// Angles are Arc2D degrees (0 = east, positive counterclockwise on screen); f = 0 is the bottom tip
// and f = 1 the top tip, independent of fill direction.
abstract class CurvedGeometry implements Geometry {

    final double centerX;
    final double centerY;
    final double radius;
    final int thickness;
    final double topAngle;
    final double bottomAngle;

    CurvedGeometry(double centerX, double centerY, double radius, int thickness,
                   double topAngle, double bottomAngle) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
        this.thickness = thickness;
        this.topAngle = topAngle;
        this.bottomAngle = bottomAngle;
    }

    // The full outline (tapered band, half-ring capsule, ...). Filled for the track, stroked for the
    // outline, and the intersection target for every fill region.
    @Override
    public abstract Shape body();

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
        double r = radius + thickness; // safely beyond the body's outer edge
        Arc2D wedge = new Arc2D.Double(centerX - r, centerY - r, r * 2.0, r * 2.0,
            startDeg, endDeg - startDeg, Arc2D.PIE);
        Area area = new Area(body());
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
