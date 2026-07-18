package com.arcvitals;

// Pure hitpoints maths for the target bar. The target's health comes as a ratio out of a scale
// (Actor.getHealthRatio() / getHealthScale()); scale <= 0 means the target has no visible health bar
// yet (e.g. before your first hit), in which case there is nothing to draw. No AWT, no Client.
final class TargetHealth {

    private TargetHealth() {
    }

    // True when the target has a known health bar to draw.
    static boolean visible(int healthScale) {
        return healthScale > 0;
    }

    // The 0..1 fill fraction, clamped. Returns 0 when the scale is unknown.
    static double fraction(int healthRatio, int healthScale) {
        if (healthScale <= 0) {
            return 0.0;
        }
        double f = healthRatio / (double) healthScale;
        if (f < 0.0) {
            return 0.0;
        }
        return f > 1.0 ? 1.0 : f;
    }

    // The whole-number HP percentage (0..100).
    static int percent(int healthRatio, int healthScale) {
        return (int) Math.round(fraction(healthRatio, healthScale) * 100.0);
    }
}
