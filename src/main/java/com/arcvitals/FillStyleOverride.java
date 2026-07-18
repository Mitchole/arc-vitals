package com.arcvitals;

// Per-bar fill-style choice. INHERIT falls back to the global fill style; every other value overrides it.
// Public because it is a config return type.
public enum FillStyleOverride {
    INHERIT("Inherit", null),
    SMOOTH("Smooth", FillStyle.SMOOTH),
    GLOSS("Glossy", FillStyle.GLOSS),
    GRADIENT("Gradient", FillStyle.GRADIENT),
    SEGMENTED("Segmented", FillStyle.SEGMENTED),
    GLOW("Glow", FillStyle.GLOW),
    NOTCHED("Notched", FillStyle.NOTCHED);

    private final String label;
    private final FillStyle target;

    FillStyleOverride(String label, FillStyle target) {
        this.label = label;
        this.target = target;
    }

    // INHERIT carries a null target and falls back to the global fill style; every other value
    // overrides it. The constant declaration is the whole mapping, so a new style cannot be half-added.
    FillStyle resolve(FillStyle global) {
        return target != null ? target : global;
    }

    @Override
    public String toString() {
        return label;
    }
}
