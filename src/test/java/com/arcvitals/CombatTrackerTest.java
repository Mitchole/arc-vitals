package com.arcvitals;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CombatTrackerTest {
    @Test
    public void offAlwaysShows() {
        assertTrue(CombatTracker.shouldShow(false, 100, -999999, 5));
    }

    @Test
    public void inCombatThisTickShows() {
        assertTrue(CombatTracker.shouldShow(true, 100, 100, 5));
    }

    @Test
    public void withinDelayShows() {
        // 5s / 0.6 = 8.33 -> 8 ticks
        assertTrue(CombatTracker.shouldShow(true, 108, 100, 5));
    }

    @Test
    public void pastDelayHides() {
        assertFalse(CombatTracker.shouldShow(true, 109, 100, 5));
    }

    @Test
    public void zeroDelayHidesNextTick() {
        assertTrue(CombatTracker.shouldShow(true, 100, 100, 0));
        assertFalse(CombatTracker.shouldShow(true, 101, 100, 0));
    }

    @Test
    public void negativeElapsedAfterHopHides() {
        assertFalse(CombatTracker.shouldShow(true, 5, 100, 5));
    }

    @Test
    public void defaultSentinelReadsAsOutOfCombat() {
        CombatTracker t = new CombatTracker();
        assertFalse(CombatTracker.shouldShow(true, 100, t.getLastCombatTick(), 5));
    }

    @Test
    public void recordAndResetTrackTick() {
        CombatTracker t = new CombatTracker();
        t.recordCombat(50);
        assertEquals(50, t.getLastCombatTick());
        assertTrue(CombatTracker.shouldShow(true, 55, t.getLastCombatTick(), 5));
        t.reset();
        assertFalse(CombatTracker.shouldShow(true, 55, t.getLastCombatTick(), 5));
    }
}
