package com.arcvitals;

// The shared circular-arc centreline maths for curved bar shapes. Given the layout parameters it
// computes the circle centre, radius, sweep, and the top/bottom tip angles (Arc2D degrees; 0 = east,
// positive counterclockwise on screen). ArcGeometry and TaperedGeometry both build from this so the
// arithmetic lives in exactly one place. Package-private: not a config return type.
final class ArcCenterline {

    final double centerX;
    final double centerY;
    final double radius;
    final double startAngle;
    final double sweepDegrees;
    final double topAngle;
    final double bottomAngle;

    ArcCenterline(int cx, int cy, int size, int thickness, int baseGap, int spacing,
                  int curveDegrees, int index, boolean leftSide) {
        double half = Math.toRadians(curveDegrees) / 2.0;
        double sinHalf = Math.sin(half);
        if (sinHalf < 1e-4) {
            sinHalf = 1e-4;
        }
        double baseRadius = (size / 2.0) / sinHalf;
        double circleCenterX = leftSide ? (cx - baseGap + baseRadius) : (cx + baseGap - baseRadius);
        double r = baseRadius + index * (double) (thickness + spacing);

        double sinSweepHalf = Math.min(1.0, (size / 2.0) / r);
        double sweep = Math.toDegrees(Math.asin(sinSweepHalf) * 2.0);
        double centreAngle = leftSide ? 180.0 : 0.0;
        double start = centreAngle - (sweep / 2.0);
        double end = centreAngle + (sweep / 2.0);

        this.centerX = circleCenterX;
        this.centerY = cy;
        this.radius = r;
        this.startAngle = start;
        this.sweepDegrees = sweep;
        // On the left the smaller angle (start) is the top tip; on the right it is the bottom tip.
        this.topAngle = leftSide ? start : end;
        this.bottomAngle = leftSide ? end : start;
    }
}
