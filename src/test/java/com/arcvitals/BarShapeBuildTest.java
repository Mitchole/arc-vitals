package com.arcvitals;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

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
}
