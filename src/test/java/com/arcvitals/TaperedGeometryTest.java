package com.arcvitals;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TaperedGeometryTest {

    private static Geometry leaf() {
        return new TaperedGeometry(200, 200, 140, 12, 70, 4, 110, 0, true, TaperProfile.LEAF);
    }

    private static Geometry horn() {
        return new TaperedGeometry(200, 200, 140, 12, 70, 4, 110, 0, true, TaperProfile.HORN);
    }

    @Test
    public void fillRegionEmptyAtZeroWidth() {
        assertTrue(leaf().fillRegion(0.0, 0.0, FillDirection.BOTTOM_UP).isEmpty());
        assertTrue(leaf().fillRegion(0.6, 0.4, FillDirection.BOTTOM_UP).isEmpty());
    }

    @Test
    public void higherFractionFillsMoreArea() {
        int half = fillCount(leaf().fillRegion(0.0, 0.5, FillDirection.BOTTOM_UP));
        int full = fillCount(leaf().fillRegion(0.0, 1.0, FillDirection.BOTTOM_UP));
        assertTrue(half < full);
    }

    @Test
    public void bottomUpFillsBottomTipNotTop() {
        Area a = leaf().fillRegion(0.0, 0.35, FillDirection.BOTTOM_UP);
        assertTrue(a.contains(leaf().pointAt(0.1)[0], leaf().pointAt(0.1)[1]));
        assertFalse(a.contains(leaf().pointAt(0.9)[0], leaf().pointAt(0.9)[1]));
    }

    @Test
    public void topDownFillsTopTipNotBottom() {
        Area a = leaf().fillRegion(0.0, 0.35, FillDirection.TOP_DOWN);
        assertTrue(a.contains(leaf().pointAt(0.9)[0], leaf().pointAt(0.9)[1]));
        assertFalse(a.contains(leaf().pointAt(0.1)[0], leaf().pointAt(0.1)[1]));
    }

    @Test
    public void leafIsFatInMiddleThinAtTips() {
        Geometry g = leaf();
        Shape body = g.body();
        double off = 12 * 0.45; // ~ 90% of half-thickness
        // A point pushed nearly a half-thickness off the centreline is inside at the middle...
        double[] mid = g.pointAt(0.5);
        double[] mn = g.normalAt(0.5);
        assertTrue(body.contains(mid[0] + mn[0] * off, mid[1] + mn[1] * off));
        // ...but outside near a tip, where the leaf has tapered to a point.
        double[] tip = g.pointAt(0.03);
        double[] tn = g.normalAt(0.03);
        assertFalse(body.contains(tip[0] + tn[0] * off, tip[1] + tn[1] * off));
    }

    @Test
    public void hornIsThickAtBottomThinAtTop() {
        Geometry g = horn();
        Shape body = g.body();
        double off = 12 * 0.45;
        double[] low = g.pointAt(0.08);
        double[] ln = g.normalAt(0.08);
        assertTrue("thick at the bottom (full) end", body.contains(low[0] + ln[0] * off, low[1] + ln[1] * off));
        double[] high = g.pointAt(0.92);
        double[] hn = g.normalAt(0.92);
        assertFalse("thin at the top (draining) tip", body.contains(high[0] + hn[0] * off, high[1] + hn[1] * off));
    }

    @Test
    public void leafAndHornBodiesDiffer() {
        assertTrue(fillCount(new Area(leaf().body())) != fillCount(new Area(horn().body())));
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
