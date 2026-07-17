package com.arcvitals;

// Per-bar shape choice. INHERIT falls back to the global bar shape; every other value overrides it.
// Public because it is a config return type.
public enum ShapeOverride {
    INHERIT("Inherit"),
    ARC("Curved"),
    STRAIGHT("Straight"),
    LEAF("Leaf"),
    HORN("Horn"),
    RING("Ring");

    private final String label;

    ShapeOverride(String label) {
        this.label = label;
    }

    BarShape resolve(BarShape global) {
        switch (this) {
            case ARC:
                return BarShape.ARC;
            case STRAIGHT:
                return BarShape.STRAIGHT;
            case LEAF:
                return BarShape.LEAF;
            case HORN:
                return BarShape.HORN;
            case RING:
                return BarShape.RING;
            default:
                return global;
        }
    }

    @Override
    public String toString() {
        return label;
    }
}
