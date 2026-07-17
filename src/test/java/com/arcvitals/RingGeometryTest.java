package com.arcvitals;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RingGeometryTest {

    private static Geometry left() {
        return new RingGeometry(200, 200, 12, 70, 4, 0, true, false);
    }

    private static Geometry right() {
        return new RingGeometry(200, 200, 12, 70, 4, 0, false, false);
    }

    @Test
    public void leftHalfSitsLeftOfCentre() {
        Rectangle2D b = left().body().getBounds2D();
        assertTrue("belly bulges left past the gap", b.getMinX() < 200 - 70);
        assertTrue("tips reach the centre column only", b.getMaxX() <= 200 + 12);
    }

    @Test
    public void rightHalfSitsRightOfCentre() {
        Rectangle2D b = right().body().getBounds2D();
        assertTrue(b.getMaxX() > 200 + 70);
        assertTrue(b.getMinX() >= 200 - 12);
    }

    @Test
    public void bottomTipBelowTopTipOnTheCentreColumn() {
        double[] bottom = left().pointAt(0.0);
        double[] top = left().pointAt(1.0);
        assertEquals(200.0, bottom[0], 0.5);
        assertEquals(200.0, top[0], 0.5);
        assertTrue(bottom[1] > top[1]);
    }

    @Test
    public void fillRegionEmptyAtZeroWidth() {
        assertTrue(left().fillRegion(0.0, 0.0, FillDirection.BOTTOM_UP).isEmpty());
        assertTrue(left().fillRegion(0.7, 0.3, FillDirection.BOTTOM_UP).isEmpty());
    }

    @Test
    public void higherFractionFillsMoreArea() {
        int half = fillCount(left().fillRegion(0.0, 0.5, FillDirection.BOTTOM_UP));
        int full = fillCount(left().fillRegion(0.0, 1.0, FillDirection.BOTTOM_UP));
        assertTrue(half < full);
    }

    @Test
    public void bottomUpFillsBottomTipNotTop() {
        Area a = left().fillRegion(0.0, 0.35, FillDirection.BOTTOM_UP);
        assertTrue(a.contains(left().pointAt(0.05)[0], left().pointAt(0.05)[1]));
        assertFalse(a.contains(left().pointAt(0.95)[0], left().pointAt(0.95)[1]));
    }

    @Test
    public void topDownFillsTopTipNotBottom() {
        Area a = left().fillRegion(0.0, 0.35, FillDirection.TOP_DOWN);
        assertTrue(a.contains(left().pointAt(0.95)[0], left().pointAt(0.95)[1]));
        assertFalse(a.contains(left().pointAt(0.05)[0], left().pointAt(0.05)[1]));
    }

    private static int fillCount(Area a) {
        BufferedImage img = new BufferedImage(500, 500, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.WHITE);
        g.fill(a);
        g.dispose();
        int n = 0;
        for (int x = 0; x < 500; x++) {
            for (int y = 0; y < 500; y++) {
                if ((img.getRGB(x, y) >>> 24) != 0) {
                    n++;
                }
            }
        }
        return n;
    }
}
