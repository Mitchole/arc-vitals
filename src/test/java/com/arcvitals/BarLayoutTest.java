package com.arcvitals;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class BarLayoutTest {
    @Test
    public void innermostIsBaseGap() {
        assertEquals(70, BarLayout.gapForIndex(70, 12, 4, 0));
    }

    @Test
    public void eachStepAddsThicknessPlusSpacing() {
        assertEquals(86, BarLayout.gapForIndex(70, 12, 4, 1));
        assertEquals(102, BarLayout.gapForIndex(70, 12, 4, 2));
    }

    @Test
    public void zeroSpacingStacksByThickness() {
        assertEquals(94, BarLayout.gapForIndex(70, 12, 0, 2));
    }
}
