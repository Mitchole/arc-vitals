package com.arcvitals;

// Frame-rate-independent easing for a bar's displayed fill fraction. Each frame the overlay steps
// the displayed value toward the real target with an exponential ease-out whose settle time is set
// by glideMs (roughly the time to reach ~95% of the change). Splitting one frame delta into several
// smaller deltas converges to the same value, so the motion looks identical at any frame rate.
final class BarAnimator {

    // 5e-4 of a full bar is well under a pixel on any real bar height, so settling here reads as done.
    private static final double EPSILON = 5e-4;

    private BarAnimator() {
    }

    // displayed  current on-screen fraction (0..1)
    // target     real fraction to ease toward (0..1)
    // dtMillis   time since the previous frame, in milliseconds
    // glideMs    settle time; <= 0 means no animation (jump straight to target)
    static double step(double displayed, double target, long dtMillis, int glideMs) {
        if (glideMs <= 0) {
            return target;
        }
        double diff = target - displayed;
        if (Math.abs(diff) < EPSILON) {
            return target;
        }
        if (dtMillis <= 0) {
            return displayed;
        }
        double tau = glideMs / 3.0; // e^-3 ~= 0.05, so ~95% settled by glideMs
        double alpha = 1.0 - Math.exp(-dtMillis / tau);
        double next = displayed + diff * alpha;
        // Never cross the target; settle exactly once within EPSILON of it.
        if (diff > 0.0) {
            if (next > target - EPSILON) {
                return target;
            }
        } else if (next < target + EPSILON) {
            return target;
        }
        return next;
    }
}
