package com.arcvitals;

public enum PrayerVisibility {
    OFF("Off"),
    PRAYER_BAR("Prayer bar"),
    WHOLE_HUD("Whole HUD");

    private final String label;

    PrayerVisibility(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
