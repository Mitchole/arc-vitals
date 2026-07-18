package com.arcvitals;

// Per-bar pattern choice. INHERIT falls back to the global bar pattern; every other value overrides
// it. Public because it is a config return type.
public enum PatternOverride {
    INHERIT("Inherit", null),
    NONE("None", BarPattern.NONE),
    BRUSHED("Brushed metal", BarPattern.BRUSHED),
    WEAVE("Carbon weave", BarPattern.WEAVE),
    RIVETS("Riveted plate", BarPattern.RIVETS),
    SCALES("Dragon scale", BarPattern.SCALES),
    RUNE("Rune etch", BarPattern.RUNE),
    MESH("Tech mesh", BarPattern.MESH);

    private final String label;
    private final BarPattern target;

    PatternOverride(String label, BarPattern target) {
        this.label = label;
        this.target = target;
    }

    // INHERIT carries a null target and falls back to the global pattern; every other value overrides
    // it. The constant declaration is the whole mapping, so a new pattern cannot be half-added.
    BarPattern resolve(BarPattern global) {
        return target != null ? target : global;
    }

    @Override
    public String toString() {
        return label;
    }
}
