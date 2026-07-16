package com.arcvitals;

import java.awt.Color;

public enum HpStatus {
    NONE("None"),
    POISONED("Poisoned"),
    VENOMED("Venomed");

    private final String label;

    HpStatus(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }

    // Varp semantics per the vanilla HP orb: >= 1,000,000 is venom, positive is
    // poison, negative values are poison-immunity ticks.
    static HpStatus of(int poisonVarp) {
        if (poisonVarp >= 1_000_000) {
            return VENOMED;
        }
        return poisonVarp > 0 ? POISONED : NONE;
    }

    static Color resolve(HpStatus status, boolean enabled, Color base, Color poison, Color venom) {
        if (!enabled) {
            return base;
        }
        switch (status) {
            case VENOMED:
                return venom;
            case POISONED:
                return poison;
            case NONE:
            default:
                return base;
        }
    }
}
