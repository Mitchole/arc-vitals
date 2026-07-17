package com.arcvitals;

import java.awt.Shape;
import java.awt.geom.Path2D;

// A curved bar whose stroke width tapers along its length per a TaperProfile (Leaf or Horn). It
// shares the arc centreline with ArcGeometry (via ArcCenterline) and the fill/point/normal maths
// with RingGeometry (via CurvedGeometry). Its body is a filled variable-width Path2D rather than a
// constant-width stroked capsule. flatEnds does not apply (the profile shapes the tips).
final class TaperedGeometry extends CurvedGeometry {

    private static final int SAMPLES = 96;

    private final Shape body;

    TaperedGeometry(int cx, int cy, int size, int thickness, int baseGap, int spacing,
                    int curveDegrees, int index, boolean leftSide, TaperProfile profile) {
        this(new ArcCenterline(cx, cy, size, thickness, baseGap, spacing, curveDegrees, index, leftSide),
            thickness, profile);
    }

    private TaperedGeometry(ArcCenterline c, int thickness, TaperProfile profile) {
        super(c.centerX, c.centerY, c.radius, thickness, c.topAngle, c.bottomAngle);
        this.body = buildBody(profile);
    }

    private Shape buildBody(TaperProfile profile) {
        double halfT = thickness / 2.0;
        Path2D.Double p = new Path2D.Double();
        // Outer edge, f: 0 -> 1.
        for (int i = 0; i <= SAMPLES; i++) {
            double f = i / (double) SAMPLES;
            double[] pt = pointAt(f);
            double[] n = normalAt(f);
            double h = halfT * clampScale(profile.halfScale(f));
            double x = pt[0] + n[0] * h;
            double y = pt[1] + n[1] * h;
            if (i == 0) {
                p.moveTo(x, y);
            } else {
                p.lineTo(x, y);
            }
        }
        // Inner edge, f: 1 -> 0.
        for (int i = SAMPLES; i >= 0; i--) {
            double f = i / (double) SAMPLES;
            double[] pt = pointAt(f);
            double[] n = normalAt(f);
            double h = halfT * clampScale(profile.halfScale(f));
            p.lineTo(pt[0] - n[0] * h, pt[1] - n[1] * h);
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

    @Override
    public Shape body() {
        return body;
    }
}
