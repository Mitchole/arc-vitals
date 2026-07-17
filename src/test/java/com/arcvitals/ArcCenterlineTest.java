package com.arcvitals;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ArcCenterlineTest {

    @Test
    public void sweepEqualsCurveAtInnermostRing() {
        // At index 0 the sweep the bar actually subtends equals the curve setting.
        ArcCenterline c = new ArcCenterline(200, 200, 140, 12, 70, 4, 110, 0, true);
        assertEquals(110.0, c.sweepDegrees, 1e-6);
        assertEquals(200.0, c.centerY, 1e-6);
    }

    @Test
    public void leftTipAnglesStraddleWest() {
        ArcCenterline c = new ArcCenterline(200, 200, 140, 12, 70, 4, 110, 0, true);
        assertEquals(125.0, c.topAngle, 1e-6);    // 180 - 110/2
        assertEquals(235.0, c.bottomAngle, 1e-6); // 180 + 110/2
    }

    @Test
    public void rightTipAnglesStraddleEast() {
        ArcCenterline c = new ArcCenterline(200, 200, 140, 12, 70, 4, 110, 0, false);
        assertEquals(55.0, c.topAngle, 1e-6);
        assertEquals(-55.0, c.bottomAngle, 1e-6);
    }

    @Test
    public void outerRingsHaveLargerRadius() {
        ArcCenterline inner = new ArcCenterline(200, 200, 140, 12, 70, 4, 110, 0, true);
        ArcCenterline outer = new ArcCenterline(200, 200, 140, 12, 70, 4, 110, 1, true);
        assertTrue(outer.radius > inner.radius);
    }
}
