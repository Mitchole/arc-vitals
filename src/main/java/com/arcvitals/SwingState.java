package com.arcvitals;

// Pure timing maths for the swing timer. The fill is driven off wall-clock milliseconds since the
// last detected swing (600ms per game tick), so it is smooth sub-tick with no easing. showing() is
// the combat-linger gate: the timer stays up for lingerTicks after the last swing, then hides. No
// AWT, no Client.
final class SwingState {

    private static final long MS_PER_TICK = 600;

    private SwingState() {
    }

    static double fraction(long elapsedMs, int cooldownTicks) {
        if (cooldownTicks <= 0) {
            return 1.0;
        }
        if (elapsedMs <= 0) {
            return 0.0;
        }
        double f = elapsedMs / (cooldownTicks * (double) MS_PER_TICK);
        return f > 1.0 ? 1.0 : f;
    }

    static boolean ready(long elapsedMs, int cooldownTicks) {
        return elapsedMs >= cooldownTicks * MS_PER_TICK;
    }

    static boolean showing(int currentTick, int lastSwingTick, int lingerTicks) {
        int elapsed = currentTick - lastSwingTick;
        return elapsed >= 0 && elapsed <= lingerTicks;
    }
}
