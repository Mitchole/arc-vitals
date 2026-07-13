package com.arcvitals;

public enum ValueDisplay {
    OFF("Off"),
    CURRENT_MAX("Current / Max"),
    PERCENT("Percent"),
    BOTH("Both");

    private final String label;

    ValueDisplay(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
