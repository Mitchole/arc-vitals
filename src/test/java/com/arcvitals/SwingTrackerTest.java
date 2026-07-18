package com.arcvitals;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class SwingTrackerTest {

    private static final long MS = 1_000_000L; // nanos per millisecond

    @Test
    public void notShowingBeforeAnySwing() {
        SwingTracker t = new SwingTracker();
        assertFalse(t.showing(100));
    }

    @Test
    public void showsThenLingersThenHides() {
        SwingTracker t = new SwingTracker();
        t.onSwing(4, 100, 0L);
        assertTrue(t.showing(100));
        assertTrue(t.showing(100 + SwingTracker.LINGER_TICKS));
        assertFalse(t.showing(101 + SwingTracker.LINGER_TICKS));
    }

    @Test
    public void fractionAndReadyTrackElapsedNanos() {
        SwingTracker t = new SwingTracker();
        t.onSwing(4, 100, 0L);                 // 4 ticks = 2400ms
        assertEquals(0.0, t.fraction(0L), 1e-9);
        assertEquals(0.5, t.fraction(1200 * MS), 1e-9);
        assertFalse(t.ready(2399 * MS));
        assertTrue(t.ready(2400 * MS));
        assertEquals(4, t.cooldownTicks());
    }

    @Test
    public void resetClearsSwing() {
        SwingTracker t = new SwingTracker();
        t.onSwing(4, 100, 0L);
        t.reset();
        assertFalse(t.showing(100));
    }
}
