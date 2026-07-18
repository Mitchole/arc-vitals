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

    @Test
    public void leftOrientationMatchesLeftSideBoolean() {
        ArcCenterline viaBool = new ArcCenterline(100, 100, 150, 12, 88, 7, 120, 0, true);
        ArcCenterline viaEnum = new ArcCenterline(100, 100, 150, 12, 88, 7, 120, 0, Orientation.LEFT);
        assertEquals(viaBool.centerX, viaEnum.centerX, 1e-9);
        assertEquals(viaBool.centerY, viaEnum.centerY, 1e-9);
        assertEquals(viaBool.startAngle, viaEnum.startAngle, 1e-9);
        assertEquals(viaBool.topAngle, viaEnum.topAngle, 1e-9);
        assertEquals(viaBool.bottomAngle, viaEnum.bottomAngle, 1e-9);
    }

    @Test
    public void topOrientationBowsUpFromCentre() {
        ArcCenterline top = new ArcCenterline(100, 100, 150, 12, 88, 7, 120, 0, Orientation.TOP);
        // Centre angle is 90 (straight up); the circle centre is directly above/below cx.
        assertEquals(100.0, top.centerX, 1e-9);
        // Belly (topmost point, angle 90) sits gap px above cy: centerY - radius == cy - gap.
        assertEquals(100.0 - 88.0, top.centerY - top.radius, 1e-6);
        // Sweep is centred on 90.
        assertEquals(90.0, (top.startAngle + top.sweepDegrees / 2.0), 1e-9);
    }

    @Test
    public void bottomOrientationBowsDownFromCentre() {
        ArcCenterline bot = new ArcCenterline(100, 100, 150, 12, 88, 7, 120, 0, Orientation.BOTTOM);
        assertEquals(100.0, bot.centerX, 1e-9);
        // Belly (bottommost point, angle 270) sits gap px below cy: centerY + radius == cy + gap.
        assertEquals(100.0 + 88.0, bot.centerY + bot.radius, 1e-6);
        assertEquals(270.0, (bot.startAngle + bot.sweepDegrees / 2.0), 1e-9);
    }
}
