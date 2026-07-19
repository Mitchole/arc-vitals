package com.arcvitals;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BarRendererTest {

    private static final Color TRACK = new Color(0, 0, 0, 130);
    private static final Color OUTLINE = new Color(0, 0, 0, 180);
    private static final Color FILL = new Color(0, 200, 0);
    private static final Color OVER = new Color(120, 240, 255);
    private static final Color TICK = new Color(255, 0, 0); // deliberately distinct for pixel-counting

    private static BufferedImage drawLeft(FillDirection dir, double frac) {
        return draw(dir, frac, 0.0, null, null);
    }

    private static BufferedImage draw(FillDirection dir, double frac, double overBand,
                                      Color overColor, Color overTick) {
        BufferedImage img = new BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        Geometry geo = BarShape.ARC.build(200, 200, 140, 12, 70, 4, 110, 0, true, true);
        BarRenderer.draw(g, geo, FillStyle.SMOOTH, dir, frac, FILL, FILL, 14, TRACK, OUTLINE, 1,
            0.0, null, overBand, overColor, overTick);
        g.dispose();
        return img;
    }

    @Test
    public void paintsVisiblePixels() {
        assertTrue(hasVisible(drawLeft(FillDirection.BOTTOM_UP, 0.5)));
    }

    @Test
    public void higherFractionFillsMoreGreen() {
        assertTrue(greenCount(drawLeft(FillDirection.BOTTOM_UP, 0.25))
            < greenCount(drawLeft(FillDirection.BOTTOM_UP, 0.75)));
    }

    @Test
    public void emptyBarPaintsTrackButNoFill() {
        BufferedImage img = drawLeft(FillDirection.BOTTOM_UP, 0.0);
        assertTrue("track should still paint", hasVisible(img));
        assertFalse("no fill at 0%", greenCount(img) > 0);
    }

    @Test
    public void overhealBandPaintsBandColour() {
        BufferedImage img = draw(FillDirection.BOTTOM_UP, 1.0, 0.2, OVER, null);
        assertTrue("overheal band should paint band-colour pixels", cyanCount(img) > 0);
    }

    @Test
    public void noBandWhenOverBandZero() {
        BufferedImage img = draw(FillDirection.BOTTOM_UP, 1.0, 0.0, OVER, null);
        assertFalse("no band when overBand is 0", cyanCount(img) > 0);
    }

    @Test
    public void noBandWhenColourNull() {
        BufferedImage img = draw(FillDirection.BOTTOM_UP, 1.0, 0.2, null, null);
        assertFalse("no band without a band colour", cyanCount(img) > 0);
    }

    @Test
    public void tickPaintsWhenTickColourGiven() {
        BufferedImage img = draw(FillDirection.BOTTOM_UP, 1.0, 0.2, OVER, TICK);
        assertTrue("tick should paint its own colour", redCount(img) > 0);
    }

    @Test
    public void noTickWhenTickColourNull() {
        BufferedImage img = draw(FillDirection.BOTTOM_UP, 1.0, 0.2, OVER, null);
        assertFalse("no tick without a tick colour", redCount(img) > 0);
    }

    private static boolean hasVisible(BufferedImage img) {
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                if ((img.getRGB(x, y) >>> 24) != 0) {
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
                int argb = img.getRGB(x, y);
                int a = (argb >>> 24) & 0xFF;
                int r = (argb >> 16) & 0xFF;
                int gr = (argb >> 8) & 0xFF;
                int b = argb & 0xFF;
                if (a > 200 && gr > 150 && r < 90 && b < 90) {
                    n++;
                }
            }
        }
        return n;
    }

    private static int cyanCount(BufferedImage img) {
        int n = 0;
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                int argb = img.getRGB(x, y);
                int a = (argb >>> 24) & 0xFF;
                int r = (argb >> 16) & 0xFF;
                int gr = (argb >> 8) & 0xFF;
                int b = argb & 0xFF;
                // cyan-ish: strong green + blue, weak red (distinguishes the band from the green fill)
                if (a > 150 && gr > 150 && b > 150 && r < 160) {
                    n++;
                }
            }
        }
        return n;
    }

    private static int redCount(BufferedImage img) {
        int n = 0;
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                int argb = img.getRGB(x, y);
                int a = (argb >>> 24) & 0xFF;
                int r = (argb >> 16) & 0xFF;
                int gr = (argb >> 8) & 0xFF;
                int b = argb & 0xFF;
                if (a > 150 && r > 180 && gr < 90 && b < 90) {
                    n++;
                }
            }
        }
        return n;
    }
}
