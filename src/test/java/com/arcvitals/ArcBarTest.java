package com.arcvitals;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ArcBarTest {
    private static final Color TRACK = new Color(0, 0, 0, 130);
    private static final Color OUTLINE = new Color(0, 0, 0, 180);

    @Test
    public void capsuleHasPositiveBounds() {
        Shape capsule = ArcBar.capsule(200, 200, 140, 12, 70, 4, 110, 0, true, true);
        assertNotNull(capsule);
        Rectangle2D b = capsule.getBounds2D();
        assertTrue(b.getWidth() > 0);
        assertTrue(b.getHeight() > 0);
    }

    @Test
    public void flatEndsWithOutlineAndPreviewPaintsPixels() {
        BufferedImage img = new BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        Shape left = ArcBar.capsule(200, 200, 140, 12, 70, 4, 110, 0, true, true);
        Shape right = ArcBar.capsule(200, 200, 140, 12, 70, 4, 110, 0, false, true);
        ArcBar.draw(g, left, 200, 140, 12, FillDirection.BOTTOM_UP, 0.5,
            Color.GREEN, TRACK, OUTLINE, 1, 0.8, new Color(200, 255, 200, 120));
        ArcBar.draw(g, right, 200, 140, 12, FillDirection.BOTTOM_UP, 0.5,
            Color.CYAN, TRACK, OUTLINE, 1, 0.8, new Color(200, 255, 255, 120));
        g.dispose();
        assertTrue(hasVisiblePixel(img));
    }

    @Test
    public void roundEndsNoOutlineNoPreviewStillPaints() {
        BufferedImage img = new BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        Shape cap = ArcBar.capsule(200, 200, 140, 12, 70, 4, 110, 0, true, false);
        ArcBar.draw(g, cap, 200, 140, 12, FillDirection.BOTTOM_UP, 0.5,
            Color.GREEN, TRACK, null, 0, 0.0, null);
        g.dispose();
        assertTrue(hasVisiblePixel(img));
    }

    @Test
    public void fullBarWithFlatEndsDoesNotThrow() {
        BufferedImage img = new BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        Shape cap = ArcBar.capsule(200, 200, 140, 12, 70, 4, 110, 0, true, true);
        ArcBar.draw(g, cap, 200, 140, 12, FillDirection.BOTTOM_UP, 1.0,
            Color.GREEN, TRACK, OUTLINE, 1, 0.0, null);
        g.dispose();
        assertTrue(hasVisiblePixel(img));
    }

    @Test
    public void nestedBarsAreEqualHeightAndDoNotOverlap() {
        // Two bars nested on the same side: inner (index 0) and outer (index 1).
        Shape inner = ArcBar.capsule(200, 200, 140, 12, 70, 4, 110, 0, true, true);
        Shape outer = ArcBar.capsule(200, 200, 140, 12, 70, 4, 110, 1, true, true);
        // Equal height: nested bars keep the same vertical extent (concentric, not simply enlarged).
        assertEquals(inner.getBounds2D().getHeight(), outer.getBounds2D().getHeight(), 5.0);
        // Concentric spacing: the filled bands never overlap. The old horizontal-offset model
        // overlapped at the top and bottom ends.
        assertFalse(fillsOverlap(inner, outer));
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

    @Test
    public void leftGeometryHasExpectedAnglesAndCentre() {
        ArcBar.Geometry geo = ArcBar.geometry(200, 200, 140, 12, 70, 4, 110, 0, true, true);
        // Left bar centred on the 9 o'clock direction (180 deg), sweep 110 deg.
        assertEquals(125.0, geo.topAngle, 0.5);     // 180 - 55
        assertEquals(235.0, geo.bottomAngle, 0.5);  // 180 + 55
        assertEquals(200.0, geo.centerY, 0.001);
        assertEquals(12, geo.thickness);
        assertTrue(geo.radius > 0);
        assertTrue(geo.centerX > 200);              // circle centre is right of the tips
        assertNotNull(geo.capsule);
        assertTrue(geo.capsule.getBounds2D().getWidth() > 0);
        assertTrue(geo.capsule.getBounds2D().getHeight() > 0);
    }

    @Test
    public void rightGeometryMirrorsTheAngles() {
        ArcBar.Geometry geo = ArcBar.geometry(200, 200, 140, 12, 70, 4, 110, 0, false, true);
        // Right bar centred on the 3 o'clock direction (0 deg).
        assertEquals(55.0, geo.topAngle, 0.5);      // 0 + 55
        assertEquals(-55.0, geo.bottomAngle, 0.5);  // 0 - 55
        assertTrue(geo.centerX < 200);              // circle centre is left of the tips
    }

    @Test
    public void capsuleDelegatesToGeometry() {
        Shape viaCapsule = ArcBar.capsule(200, 200, 140, 12, 70, 4, 110, 0, true, true);
        Shape viaGeometry = ArcBar.geometry(200, 200, 140, 12, 70, 4, 110, 0, true, true).capsule;
        assertEquals(viaGeometry.getBounds2D(), viaCapsule.getBounds2D());
    }
}
