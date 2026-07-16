package com.arcvitals;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class CombatTrackerTest {

    private static Visibility resolve(boolean hide, int tick, int lastCombat, int delay,
                                      PrayerVisibility mode, boolean praying) {
        return CombatTracker.resolve(hide, tick, lastCombat, delay, mode, praying);
    }

    @Test
    public void offAlwaysShows() {
        assertEquals(Visibility.FULL, resolve(false, 100, -999999, 5, PrayerVisibility.OFF, false));
    }

    @Test
    public void inCombatThisTickShows() {
        assertEquals(Visibility.FULL, resolve(true, 100, 100, 5, PrayerVisibility.OFF, false));
    }

    @Test
    public void withinDelayShows() {
        // 5s / 0.6 = 8.33 -> 8 ticks
        assertEquals(Visibility.FULL, resolve(true, 108, 100, 5, PrayerVisibility.OFF, false));
    }

    @Test
    public void pastDelayHides() {
        assertEquals(Visibility.HIDDEN, resolve(true, 109, 100, 5, PrayerVisibility.OFF, false));
    }

    @Test
    public void zeroDelayHidesNextTick() {
        assertEquals(Visibility.FULL, resolve(true, 100, 100, 0, PrayerVisibility.OFF, false));
        assertEquals(Visibility.HIDDEN, resolve(true, 101, 100, 0, PrayerVisibility.OFF, false));
    }

    @Test
    public void negativeElapsedAfterHopHides() {
        assertEquals(Visibility.HIDDEN, resolve(true, 5, 100, 5, PrayerVisibility.OFF, false));
    }

    @Test
    public void prayerKeepsWholeHudVisible() {
        assertEquals(Visibility.FULL, resolve(true, 200, 100, 5, PrayerVisibility.WHOLE_HUD, true));
    }

    @Test
    public void prayerKeepsPrayerBarOnly() {
        assertEquals(Visibility.PRAYER_ONLY, resolve(true, 200, 100, 5, PrayerVisibility.PRAYER_BAR, true));
    }

    @Test
    public void prayerIgnoredWhenModeOff() {
        assertEquals(Visibility.HIDDEN, resolve(true, 200, 100, 5, PrayerVisibility.OFF, true));
    }

    @Test
    public void noPrayerStaysHidden() {
        assertEquals(Visibility.HIDDEN, resolve(true, 200, 100, 5, PrayerVisibility.PRAYER_BAR, false));
    }

    @Test
    public void combatWinsOverPrayerOnlyMode() {
        // Inside the combat window the whole HUD shows even in prayer-bar-only mode.
        assertEquals(Visibility.FULL, resolve(true, 104, 100, 5, PrayerVisibility.PRAYER_BAR, true));
    }

    @Test
    public void prayerAppliesAfterWorldHop() {
        // Negative elapsed is not combat, but an active prayer still shows the bar.
        assertEquals(Visibility.PRAYER_ONLY, resolve(true, 5, 100, 5, PrayerVisibility.PRAYER_BAR, true));
    }

    @Test
    public void defaultSentinelReadsAsOutOfCombat() {
        CombatTracker t = new CombatTracker();
        assertEquals(Visibility.HIDDEN, resolve(true, 100, t.getLastCombatTick(), 5, PrayerVisibility.OFF, false));
    }

    @Test
    public void recordAndResetTrackTick() {
        CombatTracker t = new CombatTracker();
        t.recordCombat(50);
        assertEquals(50, t.getLastCombatTick());
        assertEquals(Visibility.FULL, resolve(true, 55, t.getLastCombatTick(), 5, PrayerVisibility.OFF, false));
        t.reset();
        assertEquals(Visibility.HIDDEN, resolve(true, 55, t.getLastCombatTick(), 5, PrayerVisibility.OFF, false));
    }
}
