package com.arcvitals;

final class BarState {

    final int current;
    final int max;
    final double fraction;
    final boolean low;

    private BarState(int current, int max, double fraction, boolean low) {
        this.current = current;
        this.max = max;
        this.fraction = fraction;
        this.low = low;
    }

    static BarState of(int current, int max, int thresholdPct) {
        double frac;
        if (max <= 0) {
            frac = 0.0;
        } else {
            frac = (double) current / max;
            if (frac < 0.0) {
                frac = 0.0;
            } else if (frac > 1.0) {
                frac = 1.0;
            }
        }
        boolean low = frac < (thresholdPct / 100.0);
        return new BarState(current, max, frac, low);
    }

    static float opacity(BarState self, BarState other, AlertMode mode, int basePct, int alertPct) {
        boolean alert;
        switch (mode) {
            case PER_BAR:
                alert = self.low;
                break;
            case WHOLE_HUD:
                alert = self.low || other.low;
                break;
            case OFF:
            default:
                alert = false;
                break;
        }
        int pct = alert ? alertPct : basePct;
        if (pct < 0) {
            pct = 0;
        } else if (pct > 100) {
            pct = 100;
        }
        return pct / 100f;
    }

    static boolean warn(BarState self, boolean warnColorEnabled) {
        return warnColorEnabled && self.low;
    }

    static double previewFraction(int current, int max, int restore) {
        if (max <= 0) {
            return 0.0;
        }
        double f = (double) (current + restore) / max;
        if (f < 0.0) {
            return 0.0;
        }
        return f > 1.0 ? 1.0 : f;
    }
}
