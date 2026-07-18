package com.arcvitals;

import javax.inject.Singleton;
import net.runelite.api.Actor;

// Tracks the local player's current combat target for the target bar, with a short linger after
// combat so the bar does not flicker between attacks. Mirrors RuneLite's Opponent Information, but
// tick-based (like CombatTracker) for testability. All calls happen on the client thread.
@Singleton
public class TargetTracker {

    // How many idle game ticks (no interaction) to keep showing the last target. ~5s at 0.6s/tick.
    static final int LINGER_TICKS = 8;

    private Actor target;
    private int lastSeenTick;

    // From InteractingChanged when the local player is the source. A non-null opponent becomes the
    // current target; a null opponent is ignored here (the linger clear runs on the game tick).
    // This deliberately overlaps onGameTick's non-null branch: it exists only to pick up a new target
    // mid-tick rather than waiting for the next GameTick, so it is kept despite the small redundancy.
    void onInteracting(Actor opponent, int tick) {
        if (opponent != null) {
            target = opponent;
            lastSeenTick = tick;
        }
    }

    // Once per game tick. Keeps the target fresh while still interacting; otherwise clears it once the
    // linger window has elapsed.
    void onGameTick(Actor interactingNow, int tick) {
        if (interactingNow != null) {
            target = interactingNow;
            lastSeenTick = tick;
        } else if (target != null && tick - lastSeenTick > LINGER_TICKS) {
            target = null;
        }
    }

    // The target to draw, or null.
    Actor current() {
        return target;
    }

    void reset() {
        target = null;
    }
}
