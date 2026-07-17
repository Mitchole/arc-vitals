package com.arcvitals;

// The shape axis: which bar geometry to build. Public because it is a config return type.
public enum BarShape {
    ARC("Curved") {
        @Override
        Geometry build(int cx, int cy, int size, int thickness, int baseGap, int spacing,
                       int curveDegrees, int index, boolean leftSide, boolean flatEnds) {
            return new ArcGeometry(cx, cy, size, thickness, baseGap, spacing,
                curveDegrees, index, leftSide, flatEnds);
        }
    },
    STRAIGHT("Straight") {
        @Override
        Geometry build(int cx, int cy, int size, int thickness, int baseGap, int spacing,
                       int curveDegrees, int index, boolean leftSide, boolean flatEnds) {
            return new StraightGeometry(cx, cy, size, thickness, baseGap, spacing,
                index, leftSide, flatEnds);
        }
    };

    private final String label;

    BarShape(String label) {
        this.label = label;
    }

    abstract Geometry build(int cx, int cy, int size, int thickness, int baseGap, int spacing,
                            int curveDegrees, int index, boolean leftSide, boolean flatEnds);

    @Override
    public String toString() {
        return label;
    }
}
