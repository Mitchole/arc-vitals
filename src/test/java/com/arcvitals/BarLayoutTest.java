package com.arcvitals;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

    @Test
    public void labelAnchorMatchesLegacyForNonRingShapes() {
        for (BarShape shape : new BarShape[]{BarShape.ARC, BarShape.STRAIGHT, BarShape.LEAF, BarShape.HORN}) {
            for (int index = 0; index < 3; index++) {
                for (boolean left : new boolean[]{true, false}) {
                    int gap = BarLayout.gapForIndex(70, 12, 4, index);
                    int[] a = BarLayout.labelAnchor(shape, 200, 200, 140, gap, 12, index, 14, left);
                    assertEquals(left ? 200 - gap : 200 + gap, a[0]);
                    assertEquals(BarLayout.labelBaselineY(200, 140, 14, index), a[1]);
                }
            }
        }
    }

    @Test
    public void ringLabelSitsBeyondBellyOnTheBulgeSide() {
        int gap = BarLayout.gapForIndex(70, 12, 4, 0); // 70
        int[] leftAnchor = BarLayout.labelAnchor(BarShape.RING, 200, 200, 140, gap, 12, 0, 14, true);
        int[] rightAnchor = BarLayout.labelAnchor(BarShape.RING, 200, 200, 140, gap, 12, 0, 14, false);
        assertTrue("left label sits left of the left belly", leftAnchor[0] < 200 - gap);
        assertTrue("right label sits right of the right belly", rightAnchor[0] > 200 + gap);
    }

    @Test
    public void ringLabelsStaggerByNestingLevel() {
        int gap0 = BarLayout.gapForIndex(70, 12, 4, 0);
        int gap1 = BarLayout.gapForIndex(70, 12, 4, 1);
        int[] inner = BarLayout.labelAnchor(BarShape.RING, 200, 200, 140, gap0, 12, 0, 14, true);
        int[] outer = BarLayout.labelAnchor(BarShape.RING, 200, 200, 140, gap1, 12, 1, 14, true);
        assertTrue("concentric labels never collide", Math.abs(outer[1] - inner[1]) >= 14);
    }
}
