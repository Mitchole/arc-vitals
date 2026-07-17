package com.arcvitals;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.Arc2D;

// A half-ring centred on the player: a fixed 180-degree stroked semicircle at radius
// baseGap + index*(thickness+spacing). Left and right halves of the same nesting level compose one
// full ring around the player. Ignores size and curve; the sweep is always 180 degrees per side.
// Shares the fill/point/normal maths with TaperedGeometry via CurvedGeometry.
final class RingGeometry extends CurvedGeometry {

    private final Shape body;

    RingGeometry(int cx, int cy, int thickness, int baseGap, int spacing,
                 int index, boolean leftSide, boolean flatEnds) {
        this(cx, cy, thickness, baseGap + index * (double) (thickness + spacing),
            leftSide ? 180.0 : -180.0, flatEnds);
    }

    // r = ring radius; sweep = +180 (left, bulges west) or -180 (right, bulges east). startAngle is
    // the top tip (90); bottomAngle is 90 + sweep (left 270, right -90).
    private RingGeometry(int cx, int cy, int thickness, double r, double sweep, boolean flatEnds) {
        super(cx, cy, r, thickness, 90.0, 90.0 + sweep);
        Arc2D arc = new Arc2D.Double(cx - r, cy - r, r * 2.0, r * 2.0, 90.0, sweep, Arc2D.OPEN);
        int cap = flatEnds ? BasicStroke.CAP_BUTT : BasicStroke.CAP_ROUND;
        this.body = new BasicStroke(thickness, cap, BasicStroke.JOIN_ROUND).createStrokedShape(arc);
    }

    @Override
    public Shape body() {
        return body;
    }
}
