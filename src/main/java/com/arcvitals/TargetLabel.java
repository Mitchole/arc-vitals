package com.arcvitals;

// What the target bar's label shows. Public because it is a config return type.
public enum TargetLabel {
    NAME_AND_PERCENT("Name and percent"),
    NAME_ONLY("Name only"),
    PERCENT_ONLY("Percent only");

    private final String label;

    TargetLabel(String label) {
        this.label = label;
    }

    // The label text for the given target name and HP percentage.
    String format(String name, int percent) {
        switch (this) {
            case NAME_ONLY:
                return name;
            case PERCENT_ONLY:
                return percent + "%";
            case NAME_AND_PERCENT:
            default:
                return name + "  " + percent + "%";
        }
    }

    @Override
    public String toString() {
        return label;
    }
}
