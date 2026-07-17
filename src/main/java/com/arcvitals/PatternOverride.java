package com.arcvitals;

// Per-bar pattern choice. INHERIT falls back to the global bar pattern; every other value overrides
// it. Public because it is a config return type.
public enum PatternOverride {
    INHERIT("Inherit"),
    NONE("None"),
    BRUSHED("Brushed metal"),
    WEAVE("Carbon weave"),
    RIVETS("Riveted plate"),
    SCALES("Dragon scale"),
    RUNE("Rune etch"),
    MESH("Tech mesh");

    private final String label;

    PatternOverride(String label) {
        this.label = label;
    }

    BarPattern resolve(BarPattern global) {
        switch (this) {
            case NONE:
                return BarPattern.NONE;
            case BRUSHED:
                return BarPattern.BRUSHED;
            case WEAVE:
                return BarPattern.WEAVE;
            case RIVETS:
                return BarPattern.RIVETS;
            case SCALES:
                return BarPattern.SCALES;
            case RUNE:
                return BarPattern.RUNE;
            case MESH:
                return BarPattern.MESH;
            default:
                return global;
        }
    }

    @Override
    public String toString() {
        return label;
    }
}
