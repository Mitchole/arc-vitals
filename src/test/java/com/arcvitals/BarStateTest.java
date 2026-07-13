package com.arcvitals;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BarStateTest {
    private static final double D = 1e-6;

    @Test
    public void fractionNormal() {
        BarState s = BarState.of(50, 100, 30);
        assertEquals(0.5, s.fraction, D);
        assertFalse(s.low);
    }

    @Test
    public void fractionClampsWhenBoosted() {
        BarState s = BarState.of(150, 100, 30);
        assertEquals(1.0, s.fraction, D);
        assertFalse(s.low);
    }

    @Test
    public void fractionSafeWhenMaxZero() {
        BarState s = BarState.of(5, 0, 30);
        assertEquals(0.0, s.fraction, D);
        assertTrue(s.low);
    }

    @Test
    public void lowIsStrictlyBelowThreshold() {
        assertTrue(BarState.of(29, 100, 30).low);
        assertFalse(BarState.of(30, 100, 30).low); // exactly at threshold is not low
        assertFalse(BarState.of(31, 100, 30).low);
    }

    @Test
    public void opacityPerBarUsesOnlySelf() {
        BarState low = BarState.of(10, 100, 30);
        BarState high = BarState.of(90, 100, 30);
        assertEquals(1.0f, BarState.opacity(low, high, AlertMode.PER_BAR, 60, 100), 1e-4f);
        assertEquals(0.6f, BarState.opacity(high, low, AlertMode.PER_BAR, 60, 100), 1e-4f);
    }

    @Test
    public void opacityWholeHudUsesEither() {
        BarState low = BarState.of(10, 100, 30);
        BarState high = BarState.of(90, 100, 30);
        assertEquals(1.0f, BarState.opacity(high, low, AlertMode.WHOLE_HUD, 60, 100), 1e-4f);
        assertEquals(0.6f, BarState.opacity(high, high, AlertMode.WHOLE_HUD, 60, 100), 1e-4f);
    }

    @Test
    public void opacityOffAlwaysBase() {
        BarState low = BarState.of(10, 100, 30);
        assertEquals(0.6f, BarState.opacity(low, low, AlertMode.OFF, 60, 100), 1e-4f);
    }

    @Test
    public void opacityClampsPercent() {
        BarState high = BarState.of(90, 100, 30);
        assertEquals(1.0f, BarState.opacity(high, high, AlertMode.OFF, 150, 100), 1e-4f);
        assertEquals(0.0f, BarState.opacity(high, high, AlertMode.OFF, -20, 100), 1e-4f);
    }

    @Test
    public void warnOnlyWhenEnabledAndLow() {
        BarState low = BarState.of(10, 100, 30);
        BarState high = BarState.of(90, 100, 30);
        assertTrue(BarState.warn(low, true));
        assertFalse(BarState.warn(low, false));
        assertFalse(BarState.warn(high, true));
    }
}
