package com.arcvitals;

// Per-bar shape choice. INHERIT falls back to the global bar shape; every other value overrides it.
// Public because it is a config return type.
public enum ShapeOverride {
    INHERIT("Inherit", null),
    ARC("Curved", BarShape.ARC),
    STRAIGHT("Straight", BarShape.STRAIGHT),
    LEAF("Leaf", BarShape.LEAF),
    HORN("Horn", BarShape.HORN),
    RING("Ring", BarShape.RING);

    private final String label;
    private final BarShape target;

    ShapeOverride(String label, BarShape target) {
        this.label = label;
        this.target = target;
    }

    // INHERIT carries a null target and falls back to the global shape; every other value overrides
    // it. The constant declaration is the whole mapping, so a new shape cannot be half-added.
    BarShape resolve(BarShape global) {
        return target != null ? target : global;
    }

    @Override
    public String toString() {
        return label;
    }
}
