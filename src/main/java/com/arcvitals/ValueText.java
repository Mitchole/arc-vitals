package com.arcvitals;

final class ValueText {

    private ValueText() {
    }

    static String format(int current, int max, ValueDisplay mode) {
        switch (mode) {
            case CURRENT_MAX:
                return current + "/" + max;
            case PERCENT:
                return percent(current, max) + "%";
            case BOTH:
                return current + "/" + max + " (" + percent(current, max) + "%)";
            case OFF:
            default:
                return "";
        }
    }

    private static int percent(int current, int max) {
        if (max <= 0) {
            return 0;
        }
        return (int) Math.round(100.0 * current / max);
    }
}
