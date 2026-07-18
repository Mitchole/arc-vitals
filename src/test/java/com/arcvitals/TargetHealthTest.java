package com.arcvitals;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TargetHealthTest {

    @Test
    public void visibleOnlyWhenScalePositive() {
        assertFalse(TargetHealth.visible(0));
        assertFalse(TargetHealth.visible(-1));
        assertTrue(TargetHealth.visible(30));
    }

    @Test
    public void fractionIsRatioOverScaleClamped() {
        assertEquals(0.75, TargetHealth.fraction(3, 4), 1e-9);
        assertEquals(1.0, TargetHealth.fraction(30, 30), 1e-9);
        assertEquals(0.0, TargetHealth.fraction(0, 30), 1e-9);
    }

    @Test
    public void fractionIsZeroWhenScaleUnknown() {
        assertEquals(0.0, TargetHealth.fraction(5, 0), 1e-9);
        assertEquals(0.0, TargetHealth.fraction(5, -1), 1e-9);
    }

    @Test
    public void fractionClampsAboveOne() {
        assertEquals(1.0, TargetHealth.fraction(40, 30), 1e-9);
    }

    @Test
    public void percentRounds() {
        assertEquals(75, TargetHealth.percent(3, 4));
        assertEquals(100, TargetHealth.percent(30, 30));
        assertEquals(0, TargetHealth.percent(0, 30));
        assertEquals(50, TargetHealth.percent(15, 30));
    }
}
