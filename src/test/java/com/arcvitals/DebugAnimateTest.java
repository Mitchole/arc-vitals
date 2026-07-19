package com.arcvitals;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class DebugAnimateTest {

    @Test
    public void staysWithinLowHigh() {
        for (long t = 0; t <= DebugAnimate.PERIOD_MS; t += 137) {
            int p = DebugAnimate.percent(t, 0);
            assertTrue("p=" + p, p >= DebugAnimate.LOW && p <= DebugAnimate.HIGH);
        }
    }

    @Test
    public void hitsLowAtStartAndHighAtHalfPeriod() {
        assertEquals(DebugAnimate.LOW, DebugAnimate.percent(0, 0));
        assertEquals(DebugAnimate.HIGH, DebugAnimate.percent(DebugAnimate.PERIOD_MS / 2, 0));
    }

    @Test
    public void oscillatesOverTime() {
        assertNotEquals(DebugAnimate.percent(0, 0), DebugAnimate.percent(DebugAnimate.PERIOD_MS / 2, 0));
    }

    @Test
    public void phaseOffsetsBars() {
        // Different bars are out of phase, so at t=0 they are not all identical.
        assertNotEquals(DebugAnimate.percent(0, 0), DebugAnimate.percent(0, 1));
    }
}
