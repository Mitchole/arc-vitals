package com.arcvitals;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class ArcBarTest {
    @Test
    public void drawPaintsPixelsAndDoesNotThrow() {
        BufferedImage img = new BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        ArcBar.draw(g, 200, 200, 140, 12, 70, 110, true, FillDirection.BOTTOM_UP, 0.5, Color.GREEN, new Color(0, 0, 0, 130));
        ArcBar.draw(g, 200, 200, 140, 12, 70, 110, false, FillDirection.BOTTOM_UP, 0.5, Color.CYAN, new Color(0, 0, 0, 130));
        g.dispose();
        assertTrue("expected some non-transparent pixels", hasVisiblePixel(img));
    }

    @Test
    public void emptyBarStillDrawsTrackWithoutThrowing() {
        BufferedImage img = new BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        ArcBar.draw(g, 200, 200, 140, 12, 70, 110, true, FillDirection.BOTTOM_UP, 0.0, Color.GREEN, new Color(0, 0, 0, 130));
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
