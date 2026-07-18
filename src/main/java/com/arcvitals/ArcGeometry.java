package com.arcvitals;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.Arc2D;

// The curved-capsule geometry: a stroked circular arc, filled by an angular pie-wedge.
// Angles are Arc2D degrees (0 = east, positive counterclockwise on screen). The fill/centreline/
// point/normal maths live in CurvedGeometry, shared with the Tapered and Ring shapes; this class
// supplies only its own stroked-capsule body.
final class ArcGeometry extends CurvedGeometry {

    private final Shape capsule;

    ArcGeometry(int cx, int cy, int size, int thickness, int baseGap, int spacing,
                int curveDegrees, int index, boolean leftSide, boolean flatEnds) {
        this(cx, cy, size, thickness, baseGap, spacing, curveDegrees, index,
            leftSide ? Orientation.LEFT : Orientation.RIGHT, flatEnds);
    }

    ArcGeometry(int cx, int cy, int size, int thickness, int baseGap, int spacing,
                int curveDegrees, int index, Orientation orientation, boolean flatEnds) {
        this(new ArcCenterline(cx, cy, size, thickness, baseGap, spacing, curveDegrees, index, orientation),
            thickness, flatEnds);
    }

    private ArcGeometry(ArcCenterline c, int thickness, boolean flatEnds) {
        super(c.centerX, c.centerY, c.radius, thickness, c.topAngle, c.bottomAngle);
        Arc2D arc = new Arc2D.Double(c.centerX - c.radius, c.centerY - c.radius,
            c.radius * 2.0, c.radius * 2.0, c.startAngle, c.sweepDegrees, Arc2D.OPEN);
        int cap = flatEnds ? BasicStroke.CAP_BUTT : BasicStroke.CAP_ROUND;
        this.capsule = new BasicStroke(thickness, cap, BasicStroke.JOIN_ROUND).createStrokedShape(arc);
    }

    @Override
    public Shape body() {
        return capsule;
    }
}
