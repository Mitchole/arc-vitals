package com.arcvitals;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ArcBarTest {
    private static final Color TRACK = new Color(0, 0, 0, 130);
    private static final Color OUTLINE = new Color(0, 0, 0, 180);

    @Test
    public void capsuleHasPositiveBounds() {
        Shape capsule = ArcBar.capsule(200, 200, 140, 12, 70, 110, true, true);
        assertNotNull(capsule);
        Rectangle2D b = capsule.getBounds2D();
        assertTrue(b.getWidth() > 0);
        assertTrue(b.getHeight() > 0);
    }

    @Test
    public void flatEndsWithOutlineAndPreviewPaintsPixels() {
        BufferedImage img = new BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        Shape left = ArcBar.capsule(200, 200, 140, 12, 70, 110, true, true);
        Shape right = ArcBar.capsule(200, 200, 140, 12, 70, 110, false, true);
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
        Shape cap = ArcBar.capsule(200, 200, 140, 12, 70, 110, true, false);
        ArcBar.draw(g, cap, 200, 140, 12, FillDirection.BOTTOM_UP, 0.5,
            Color.GREEN, TRACK, null, 0, 0.0, null);
        g.dispose();
        assertTrue(hasVisiblePixel(img));
    }

    @Test
    public void fullBarWithFlatEndsDoesNotThrow() {
        BufferedImage img = new BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        Shape cap = ArcBar.capsule(200, 200, 140, 12, 70, 110, true, true);
        ArcBar.draw(g, cap, 200, 140, 12, FillDirection.BOTTOM_UP, 1.0,
            Color.GREEN, TRACK, OUTLINE, 1, 0.0, null);
        g.dispose();
        assertTrue(hasVisiblePixel(img));
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
