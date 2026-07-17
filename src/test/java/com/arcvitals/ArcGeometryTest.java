package com.arcvitals;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ArcGeometryTest {

    private static Geometry left() {
        return new ArcGeometry(200, 200, 140, 12, 70, 4, 110, 0, true, true);
    }

    @Test
    public void fillRegionIsEmptyAtZeroWidth() {
        Geometry geo = left();
        assertTrue(geo.fillRegion(0.0, 0.0, FillDirection.BOTTOM_UP).isEmpty());
        assertTrue(geo.fillRegion(0.5, 0.5, FillDirection.BOTTOM_UP).isEmpty());
        assertTrue(geo.fillRegion(0.6, 0.4, FillDirection.BOTTOM_UP).isEmpty());
    }

    @Test
    public void fullRegionCoversMostOfTheBody() {
        Geometry geo = left();
        int body = fillCount(new Area(geo.body()));
        int full = fillCount(geo.fillRegion(0.0, 1.0, FillDirection.BOTTOM_UP));
        assertTrue("full fill should cover most of the body", full >= body * 0.9);
    }

    @Test
    public void higherFractionFillsMoreArea() {
        Geometry geo = left();
        int quarter = fillCount(geo.fillRegion(0.0, 0.25, FillDirection.BOTTOM_UP));
        int half = fillCount(geo.fillRegion(0.0, 0.5, FillDirection.BOTTOM_UP));
        int threeQuarter = fillCount(geo.fillRegion(0.0, 0.75, FillDirection.BOTTOM_UP));
        assertTrue(quarter < half);
        assertTrue(half < threeQuarter);
    }

    @Test
    public void bottomUpFillsBottomTipNotTop() {
        Area a = left().fillRegion(0.0, 0.35, FillDirection.BOTTOM_UP);
        assertTrue("bottom tip filled", contains(a, left().pointAt(0.05)));
        assertFalse("top tip empty", contains(a, left().pointAt(0.95)));
    }

    @Test
    public void topDownFillsTopTipNotBottom() {
        Area a = left().fillRegion(0.0, 0.35, FillDirection.TOP_DOWN);
        assertTrue("top tip filled", contains(a, left().pointAt(0.95)));
        assertFalse("bottom tip empty", contains(a, left().pointAt(0.05)));
    }

    // ---- helpers ----

    private static boolean contains(Area a, double[] p) {
        return a.contains(p[0], p[1]);
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
