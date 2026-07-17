package com.arcvitals;

// Per-bar fill-style choice. INHERIT falls back to the global fill style; every other value overrides it.
// Public because it is a config return type.
public enum FillStyleOverride {
    INHERIT("Inherit"),
    SMOOTH("Smooth"),
    GLOSS("Glossy"),
    GRADIENT("Gradient"),
    SEGMENTED("Segmented"),
    GLOW("Glow"),
    NOTCHED("Notched");

    private final String label;

    FillStyleOverride(String label) {
        this.label = label;
    }

    FillStyle resolve(FillStyle global) {
        switch (this) {
            case SMOOTH:
                return FillStyle.SMOOTH;
            case GLOSS:
                return FillStyle.GLOSS;
            case GRADIENT:
                return FillStyle.GRADIENT;
            case SEGMENTED:
                return FillStyle.SEGMENTED;
            case GLOW:
                return FillStyle.GLOW;
            case NOTCHED:
                return FillStyle.NOTCHED;
            default:
                return global;
        }
    }

    @Override
    public String toString() {
        return label;
    }
}
