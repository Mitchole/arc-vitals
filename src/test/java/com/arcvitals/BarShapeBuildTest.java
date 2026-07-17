package com.arcvitals;

import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class BarShapeBuildTest {

    @Test
    public void leafBuildsTaperedGeometry() {
        Geometry g = BarShape.LEAF.build(200, 200, 140, 12, 70, 4, 110, 0, true, true);
        assertTrue(g instanceof TaperedGeometry);
    }

    @Test
    public void hornBuildsTaperedGeometry() {
        Geometry g = BarShape.HORN.build(200, 200, 140, 12, 70, 4, 110, 0, true, true);
        assertTrue(g instanceof TaperedGeometry);
    }

    @Test
    public void leafAndHornProduceDifferentBodies() {
        Geometry leaf = BarShape.LEAF.build(200, 200, 140, 12, 70, 4, 110, 0, true, true);
        Geometry horn = BarShape.HORN.build(200, 200, 140, 12, 70, 4, 110, 0, true, true);
        assertTrue(!leaf.body().getBounds2D().equals(horn.body().getBounds2D()));
    }

    @Test
    public void leafTapersAtBottomTipHornIsThickThere() {
        Geometry leaf = BarShape.LEAF.build(200, 200, 140, 12, 70, 4, 110, 0, true, true);
        Geometry horn = BarShape.HORN.build(200, 200, 140, 12, 70, 4, 110, 0, true, true);
        double off = 12 * 0.45;
        // Leaf tapers to a point at the bottom tip: a near-half-thickness offset there is outside.
        double[] lp = leaf.pointAt(0.05);
        double[] ln = leaf.normalAt(0.05);
        assertFalse(leaf.body().contains(lp[0] + ln[0] * off, lp[1] + ln[1] * off));
        // Horn is thick at the bottom tip: the same offset is inside.
        double[] hp = horn.pointAt(0.05);
        double[] hn = horn.normalAt(0.05);
        assertTrue(horn.body().contains(hp[0] + hn[0] * off, hp[1] + hn[1] * off));
    }
}
