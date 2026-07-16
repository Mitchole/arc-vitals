package com.arcvitals;

import javax.inject.Singleton;

@Singleton
public class CombatTracker {

    // A tick far below any real game tick, so "no combat yet" reads as out of combat
    // and never overflows the elapsed subtraction.
    private static final int NO_COMBAT = -1_000_000;

    private int lastCombatTick = NO_COMBAT;

    void recordCombat(int tick) {
        lastCombatTick = tick;
    }

    void reset() {
        lastCombatTick = NO_COMBAT;
    }

    int getLastCombatTick() {
        return lastCombatTick;
    }

    static Visibility resolve(boolean hideOutOfCombat, int currentTick, int lastCombatTick, int delaySeconds,
                              PrayerVisibility prayerMode, boolean anyPrayerActive) {
        if (!hideOutOfCombat) {
            return Visibility.FULL;
        }
        int elapsed = currentTick - lastCombatTick;
        // getTickCount() restarts lower after a world hop / login; a stale tick is not combat.
        if (elapsed >= 0) {
            int delayTicks = (int) Math.round(delaySeconds / 0.6);
            if (elapsed <= delayTicks) {
                return Visibility.FULL;
            }
        }
        if (anyPrayerActive) {
            switch (prayerMode) {
                case WHOLE_HUD:
                    return Visibility.FULL;
                case PRAYER_BAR:
                    return Visibility.PRAYER_ONLY;
                case OFF:
                default:
                    break;
            }
        }
        return Visibility.HIDDEN;
    }
}
