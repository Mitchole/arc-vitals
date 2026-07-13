package com.arcvitals;

enum AlertMode {
    PER_BAR("Per bar"),
    WHOLE_HUD("Whole HUD"),
    OFF("Off");

    private final String label;

    AlertMode(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
