package com.arcvitals;

import javax.inject.Singleton;

// Holds the swing-timer state between the plugin (which detects a swing on the client thread and
// records it) and the overlay (which reads the fill each frame). Mirrors TargetTracker/CombatTracker:
// tick-based linger, plain fields, all calls on the client/render threads. The fill is nanosecond
// wall-clock based (via SwingState) so it is smooth sub-tick.
@Singleton
public class SwingTracker {

    // How many idle game ticks to keep the timer up after the last swing. ~5s at 0.6s/tick.
    static final int LINGER_TICKS = 8;

    // A tick far below any real game tick, so "no swing yet" reads as not showing.
    private static final int NO_SWING = -1_000_000;

    private int cooldownTicks;
    private long swingNanos;
    private int lastSwingTick = NO_SWING;
    private boolean hasSwung;

    void onSwing(int cooldownTicks, int tick, long nanos) {
        this.cooldownTicks = cooldownTicks;
        this.lastSwingTick = tick;
        this.swingNanos = nanos;
        this.hasSwung = true;
    }

    void reset() {
        hasSwung = false;
        lastSwingTick = NO_SWING;
    }

    boolean showing(int currentTick) {
        return hasSwung && SwingState.showing(currentTick, lastSwingTick, LINGER_TICKS);
    }

    double fraction(long nowNanos) {
        return SwingState.fraction(elapsedMs(nowNanos), cooldownTicks);
    }

    boolean ready(long nowNanos) {
        return SwingState.ready(elapsedMs(nowNanos), cooldownTicks);
    }

    int cooldownTicks() {
        return cooldownTicks;
    }

    private long elapsedMs(long nowNanos) {
        return (nowNanos - swingNanos) / 1_000_000L;
    }
}
