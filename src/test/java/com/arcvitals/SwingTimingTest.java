package com.arcvitals;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class SwingTimingTest {

    @Test
    public void baseSpeedUnchangedWithoutRapid() {
        assertEquals(4, SwingTiming.cooldownTicks(4, false));
        assertEquals(6, SwingTiming.cooldownTicks(6, false));
    }

    @Test
    public void rapidSubtractsOneTick() {
        assertEquals(3, SwingTiming.cooldownTicks(4, true));
        assertEquals(2, SwingTiming.cooldownTicks(3, true));
    }

    @Test
    public void neverBelowOneTick() {
        assertEquals(1, SwingTiming.cooldownTicks(1, true));
        assertEquals(1, SwingTiming.cooldownTicks(1, false));
    }
}
