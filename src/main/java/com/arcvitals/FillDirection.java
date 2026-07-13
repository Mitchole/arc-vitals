package com.arcvitals;

enum FillDirection {
    BOTTOM_UP("Bottom up"),
    TOP_DOWN("Top down");

    private final String label;

    FillDirection(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
