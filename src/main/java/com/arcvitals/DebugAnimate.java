package com.arcvitals;

// A synthetic oscillating percentage for the debug "Animate" preview: a triangle wave in [LOW, HIGH]
// with a per-bar phase offset so the bars do not sweep in lockstep. Pure and time-driven off a passed-
// in millisecond value (no Date/random), so the overlay stays the only place that reads the clock.
final class DebugAnimate {

    static final int LOW = 20;
    static final int HIGH = 95;
    static final long PERIOD_MS = 4000L;
    private static final long PHASE_STEP_MS = 700L;

    private DebugAnimate() {
    }

    static int percent(long nowMillis, int phaseIndex) {
        long t = Math.floorMod(nowMillis + phaseIndex * PHASE_STEP_MS, PERIOD_MS);
        double half = PERIOD_MS / 2.0;
        double tri = t < half ? t / half : (PERIOD_MS - t) / half; // ramps 0 -> 1 -> 0
        return (int) Math.round(LOW + (HIGH - LOW) * tri);
    }
}
