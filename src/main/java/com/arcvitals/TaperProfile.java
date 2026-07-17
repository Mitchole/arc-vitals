package com.arcvitals;

// How a TaperedGeometry's half-thickness scales along its length. f = 0 is the bottom tip, f = 1 the
// top tip (independent of fill direction). Returns a scale in [0,1] applied to half the stroke width.
// Package-private: not a config return type.
enum TaperProfile {
    // Fat in the middle, tapering to a point at both tips (symmetric).
    LEAF {
        @Override
        double halfScale(double f) {
            return Math.sin(Math.PI * f);
        }
    },
    // Thick at the bottom (full) end, tapering to a narrow tip at the top (asymmetric).
    HORN {
        @Override
        double halfScale(double f) {
            return 0.16 + 0.84 * (1.0 - f);
        }
    };

    abstract double halfScale(double f);
}
