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

    @Test
    public void innermostLabelSitsOneLineBelowBarBottom() {
        // cy + size/2 = 200 + 70 = 270; index 0 adds one font-height (14) -> 284
        assertEquals(284, BarLayout.labelBaselineY(200, 140, 14, 0));
    }

    @Test
    public void eachLabelStacksOneFontHeightLower() {
        assertEquals(298, BarLayout.labelBaselineY(200, 140, 14, 1));
        assertEquals(312, BarLayout.labelBaselineY(200, 140, 14, 2));
    }
}
