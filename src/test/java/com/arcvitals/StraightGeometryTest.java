package com.arcvitals;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StraightGeometryTest {

    private static Geometry left() {
        return new StraightGeometry(200, 200, 140, 12, 70, 4, 0, true, false);
    }

    @Test
    public void barIsVerticalToLeftOfCentre() {
        double[] bottom = left().pointAt(0.0);
        double[] top = left().pointAt(1.0);
        assertTrue("x constant along a straight bar", Math.abs(bottom[0] - top[0]) < 0.001);
        assertTrue("bar sits left of centre", bottom[0] < 200);
        assertTrue("bottom tip below top tip", bottom[1] > top[1]);
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

    @Test
    public void straightShapeBuildsStraightGeometry() {
        Geometry geo = BarShape.STRAIGHT.build(200, 200, 140, 12, 70, 4, 110, 0, true, false);
        double[] bottom = geo.pointAt(0.0);
        double[] top = geo.pointAt(1.0);
        assertTrue("STRAIGHT ignores curve and stays vertical", Math.abs(bottom[0] - top[0]) < 0.001);
    }

    private static int fillCount(Area a) {
        BufferedImage img = new BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.WHITE);
        g.fill(a);
        g.dispose();
        int n = 0;
        for (int x = 0; x < 400; x++) {
            for (int y = 0; y < 400; y++) {
                if ((img.getRGB(x, y) >>> 24) != 0) {
                    n++;
                }
            }
        }
        return n;
    }
}
