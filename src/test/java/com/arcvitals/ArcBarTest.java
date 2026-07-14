package com.arcvitals;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ArcBarTest {
    private static final Color TRACK = new Color(0, 0, 0, 130);
    private static final Color OUTLINE = new Color(0, 0, 0, 180);
    private static final Color FILL = Color.GREEN;

    // ---- geometry ----

    @Test
    public void leftGeometryHasExpectedAnglesAndCentre() {
        ArcBar.Geometry geo = ArcBar.geometry(200, 200, 140, 12, 70, 4, 110, 0, true, true);
        assertEquals(125.0, geo.topAngle, 0.5);
        assertEquals(235.0, geo.bottomAngle, 0.5);
        assertEquals(200.0, geo.centerY, 0.001);
        assertEquals(12, geo.thickness);
        assertTrue(geo.radius > 0);
        assertTrue(geo.centerX > 200);
        assertNotNull(geo.capsule);
        assertTrue(geo.capsule.getBounds2D().getWidth() > 0);
        assertTrue(geo.capsule.getBounds2D().getHeight() > 0);
    }

    @Test
    public void rightGeometryMirrorsTheAngles() {
        ArcBar.Geometry geo = ArcBar.geometry(200, 200, 140, 12, 70, 4, 110, 0, false, true);
        assertEquals(55.0, geo.topAngle, 0.5);
        assertEquals(-55.0, geo.bottomAngle, 0.5);
        assertTrue(geo.centerX < 200);
    }

    @Test
    public void nestedBarsAreEqualHeightAndDoNotOverlap() {
        Shape inner = ArcBar.geometry(200, 200, 140, 12, 70, 4, 110, 0, true, true).capsule;
        Shape outer = ArcBar.geometry(200, 200, 140, 12, 70, 4, 110, 1, true, true).capsule;
        assertEquals(inner.getBounds2D().getHeight(), outer.getBounds2D().getHeight(), 5.0);
        assertFalse(fillsOverlap(inner, outer));
    }

    // ---- draw: smoke ----

    @Test
    public void flatEndsWithOutlineAndPreviewPaintsPixels() {
        BufferedImage img = new BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        ArcBar.Geometry left = ArcBar.geometry(200, 200, 140, 12, 70, 4, 110, 0, true, true);
        ArcBar.Geometry right = ArcBar.geometry(200, 200, 140, 12, 70, 4, 110, 0, false, true);
        ArcBar.draw(g, left, FillDirection.BOTTOM_UP, 0.5,
            FILL, TRACK, OUTLINE, 1, 0.8, new Color(200, 255, 200, 120));
        ArcBar.draw(g, right, FillDirection.BOTTOM_UP, 0.5,
            Color.CYAN, TRACK, OUTLINE, 1, 0.8, new Color(200, 255, 255, 120));
        g.dispose();
        assertTrue(hasVisiblePixel(img));
    }

    @Test
    public void roundEndsNoOutlineNoPreviewStillPaints() {
        BufferedImage img = new BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        ArcBar.Geometry geo = ArcBar.geometry(200, 200, 140, 12, 70, 4, 110, 0, true, false);
        ArcBar.draw(g, geo, FillDirection.BOTTOM_UP, 0.5, FILL, TRACK, null, 0, 0.0, null);
        g.dispose();
        assertTrue(hasVisiblePixel(img));
    }

    @Test
    public void fullBarWithFlatEndsDoesNotThrow() {
        BufferedImage img = new BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        ArcBar.Geometry geo = ArcBar.geometry(200, 200, 140, 12, 70, 4, 110, 0, true, true);
        ArcBar.draw(g, geo, FillDirection.BOTTOM_UP, 1.0, FILL, TRACK, OUTLINE, 1, 0.0, null);
        g.dispose();
        assertTrue(hasVisiblePixel(img));
    }

    // ---- draw: angular behavior ----

    @Test
    public void bottomUpFillsBottomTipNotTopTip() {
        BufferedImage img = drawLeft(FillDirection.BOTTOM_UP, 0.35);
        assertTrue("bottom tip should be filled", greenAround(img, 166, 268));
        assertFalse("top tip should be empty", greenAround(img, 166, 132));
    }

    @Test
    public void topDownFillsTopTipNotBottomTip() {
        BufferedImage img = drawLeft(FillDirection.TOP_DOWN, 0.35);
        assertTrue("top tip should be filled", greenAround(img, 166, 132));
        assertFalse("bottom tip should be empty", greenAround(img, 166, 268));
    }

    @Test
    public void higherFractionFillsMorePixels() {
        int quarter = greenCount(drawLeft(FillDirection.BOTTOM_UP, 0.25));
        int half = greenCount(drawLeft(FillDirection.BOTTOM_UP, 0.5));
        int threeQuarter = greenCount(drawLeft(FillDirection.BOTTOM_UP, 0.75));
        int full = greenCount(drawLeft(FillDirection.BOTTOM_UP, 1.0));
        assertTrue(quarter < half);
        assertTrue(half < threeQuarter);
        assertTrue(threeQuarter <= full);
    }

    @Test
    public void emptyBarPaintsNoFill() {
        assertEquals(0, greenCount(drawLeft(FillDirection.BOTTOM_UP, 0.0)));
    }

    @Test
    public void fullBarFillsBothTips() {
        BufferedImage img = drawLeft(FillDirection.BOTTOM_UP, 1.0);
        assertTrue(greenAround(img, 166, 268));
        assertTrue(greenAround(img, 166, 132));
    }

    // ---- helpers ----

    private static BufferedImage drawLeft(FillDirection dir, double fraction) {
        BufferedImage img = new BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        ArcBar.Geometry geo = ArcBar.geometry(200, 200, 140, 12, 70, 4, 110, 0, true, true);
        ArcBar.draw(g, geo, dir, fraction, FILL, TRACK, null, 0, 0.0, null);
        g.dispose();
        return img;
    }

    // True if any near-pure-green pixel exists in the 12x12 box centred on (cx, cy).
    private static boolean greenAround(BufferedImage img, int cx, int cy) {
        for (int x = cx - 6; x <= cx + 6; x++) {
            for (int y = cy - 6; y <= cy + 6; y++) {
                if (x < 0 || y < 0 || x >= img.getWidth() || y >= img.getHeight()) {
                    continue;
                }
                if (isGreen(img.getRGB(x, y))) {
                    return true;
                }
            }
        }
        return false;
    }

    private static int greenCount(BufferedImage img) {
        int n = 0;
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                if (isGreen(img.getRGB(x, y))) {
                    n++;
                }
            }
        }
        return n;
    }

    // Fill is pure green; track is translucent black. Require a strong green, weak red/blue.
    private static boolean isGreen(int argb) {
        int a = (argb >>> 24) & 0xFF;
        int r = (argb >> 16) & 0xFF;
        int gr = (argb >> 8) & 0xFF;
        int b = argb & 0xFF;
        return a > 200 && gr > 180 && r < 80 && b < 80;
    }

    private static boolean fillsOverlap(Shape a, Shape b) {
        BufferedImage ia = mask(a);
        BufferedImage ib = mask(b);
        for (int x = 0; x < 400; x++) {
            for (int y = 0; y < 400; y++) {
                if ((ia.getRGB(x, y) >>> 24) != 0 && (ib.getRGB(x, y) >>> 24) != 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private static BufferedImage mask(Shape s) {
        BufferedImage img = new BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.WHITE);
        g.fill(s);
        g.dispose();
        return img;
    }

    private static boolean hasVisiblePixel(BufferedImage img) {
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                if ((img.getRGB(x, y) >>> 24) != 0) {
                    return true;
                }
            }
        }
        return false;
    }
}
