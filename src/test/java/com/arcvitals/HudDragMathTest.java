package com.arcvitals;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HudDragMathTest {

    @Test
    public void hitsInsideTheRectangle() {
        assertTrue(HudDragMath.hits(100, 100, 40, 60, 0, 120, 130));
    }

    @Test
    public void missesOutsideTheRectangle() {
        assertFalse(HudDragMath.hits(100, 100, 40, 60, 0, 10, 10));
    }

    @Test
    public void padExpandsTheHitArea() {
        // px = 146 is 6px past the right edge (x + w = 140): outside with pad 0, inside with pad 8.
        assertFalse(HudDragMath.hits(100, 100, 40, 60, 0, 146, 130));
        assertTrue(HudDragMath.hits(100, 100, 40, 60, 8, 146, 130));
    }

    @Test
    public void zeroAreaBoundsNeverHit() {
        assertFalse(HudDragMath.hits(100, 100, 0, 0, 50, 100, 100));
    }

    @Test
    public void axisOffsetAddsTheDelta() {
        // grab offset 20, cursor moved +30 -> 50
        assertEquals(50, HudDragMath.axisOffset(20, 200, 230, -500, 500));
    }

    @Test
    public void axisOffsetClampsToMax() {
        assertEquals(500, HudDragMath.axisOffset(490, 200, 260, -500, 500));
    }

    @Test
    public void axisOffsetClampsToMin() {
        assertEquals(-500, HudDragMath.axisOffset(-490, 200, 140, -500, 500));
    }
}
