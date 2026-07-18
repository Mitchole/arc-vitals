package com.arcvitals;

// The label + toString() pattern here recurs across the config enums (AlertMode, FillDirection,
// ValueDisplay, ...) so each shows a friendly name in the dropdown. It is not worth abstracting:
// a Java enum cannot inherit the field or constructor from a shared base, so any "shared" version
// still needs the field and constructor copied into every enum and saves nothing.
public enum Side {
    LEFT("Left"),
    RIGHT("Right");

    private final String label;

    Side(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
