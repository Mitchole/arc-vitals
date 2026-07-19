package com.arcvitals;

import java.awt.Color;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OverhealStateTest {

    @Test
    public void boostedOnlyWhenCurrentExceedsMax() {
        assertTrue(OverhealState.boosted(117, 99));
        assertFalse(OverhealState.boosted(99, 99));
        assertFalse(OverhealState.boosted(50, 99));
        assertFalse(OverhealState.boosted(5, 0));
    }

    @Test
    public void overBandIsSelfScaledSlice() {
        assertEquals(18.0 / 117.0, OverhealState.overBand(117, 99), 1e-9);
        assertEquals(0.0, OverhealState.overBand(99, 99), 1e-9);
        assertEquals(0.0, OverhealState.overBand(50, 99), 1e-9);
        assertEquals(0.0, OverhealState.overBand(10, 0), 1e-9); // max <= 0 guard
    }

    @Test
    public void overBandStaysBelowOne() {
        // Even an extreme boost never fills the whole arc.
        assertTrue(OverhealState.overBand(1000, 1) < 1.0);
    }

    @Test
    public void tickGeometricFractionFlipsWithDirection() {
        assertEquals(0.8, OverhealState.tickGeometricFraction(0.2, FillDirection.BOTTOM_UP), 1e-9);
        assertEquals(0.2, OverhealState.tickGeometricFraction(0.2, FillDirection.TOP_DOWN), 1e-9);
    }

    @Test
    public void tickColorBlendsTowardWhiteAtHighAlpha() {
        Color t = OverhealState.tickColor(new Color(120, 240, 255));
        assertEquals(210, t.getRed());   // (120 + 510) / 3
        assertEquals(250, t.getGreen()); // (240 + 510) / 3
        assertEquals(255, t.getBlue());  // (255 + 510) / 3
        assertEquals(235, t.getAlpha());
    }
}
