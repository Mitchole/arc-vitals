package com.arcvitals;

// The shared circular-arc centreline maths for curved bar shapes. Given the layout parameters it
// computes the circle centre, radius, sweep, and the top/bottom tip angles (Arc2D degrees; 0 = east,
// positive counterclockwise on screen). ArcGeometry and TaperedGeometry build from this so the
// arithmetic lives in exactly one place. Package-private: not a config return type.
//
// Orientation LEFT/RIGHT are the vitals' vertical crescents (centre angle 180/0, circle centre
// offset in X). TOP/BOTTOM are horizontal crescents (centre angle 90/270, circle centre offset in Y)
// used by the swing timer. topAngle/bottomAngle are assigned so FillDirection.BOTTOM_UP fills
// left/bottom -> right/top for every orientation.
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
        this(cx, cy, size, thickness, baseGap, spacing, curveDegrees, index,
            leftSide ? Orientation.LEFT : Orientation.RIGHT);
    }

    ArcCenterline(int cx, int cy, int size, int thickness, int baseGap, int spacing,
                  int curveDegrees, int index, Orientation orientation) {
        double half = Math.toRadians(curveDegrees) / 2.0;
        double sinHalf = Math.sin(half);
        if (sinHalf < 1e-4) {
            sinHalf = 1e-4;
        }
        double baseRadius = (size / 2.0) / sinHalf;
        double r = baseRadius + index * (double) (thickness + spacing);

        double sinSweepHalf = Math.min(1.0, (size / 2.0) / r);
        double sweep = Math.toDegrees(Math.asin(sinSweepHalf) * 2.0);

        double circleCenterX = cx;
        double circleCenterY = cy;
        double centreAngle;
        switch (orientation) {
            case LEFT:
                centreAngle = 180.0;
                circleCenterX = cx - baseGap + baseRadius;
                break;
            case RIGHT:
                centreAngle = 0.0;
                circleCenterX = cx + baseGap - baseRadius;
                break;
            case TOP:
                centreAngle = 90.0;
                circleCenterY = cy - baseGap + baseRadius;
                break;
            case BOTTOM:
                centreAngle = 270.0;
                circleCenterY = cy + baseGap - baseRadius;
                break;
            default:
                throw new IllegalStateException("Unhandled orientation: " + orientation);
        }
        double start = centreAngle - (sweep / 2.0);
        double end = centreAngle + (sweep / 2.0);

        this.centerX = circleCenterX;
        this.centerY = circleCenterY;
        this.radius = r;
        this.startAngle = start;
        this.sweepDegrees = sweep;

        // Assign the f=0 (bottom) and f=1 (top) tips so BOTTOM_UP fills left/bottom -> right/top.
        switch (orientation) {
            case LEFT:
                this.topAngle = start;
                this.bottomAngle = end;
                break;
            case RIGHT:
                this.topAngle = end;
                this.bottomAngle = start;
                break;
            case TOP:
                this.bottomAngle = end;   // left tip (larger angle)
                this.topAngle = start;    // right tip
                break;
            case BOTTOM:
                this.bottomAngle = start; // left tip (lower-left)
                this.topAngle = end;      // right tip
                break;
            default:
                throw new IllegalStateException("Unhandled orientation: " + orientation);
        }
    }
}
