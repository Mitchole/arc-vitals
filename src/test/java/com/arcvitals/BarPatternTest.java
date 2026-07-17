package com.arcvitals;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.lang.reflect.Modifier;
import org.junit.Test;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class BarPatternTest {

    private static final Color GREEN = new Color(0, 180, 0);

    @Test
    public void enumIsPublicForConfigProxy() {
        assertTrue(Modifier.isPublic(BarPattern.class.getModifiers()));
    }

    @Test
    public void noneHasNoTile() {
        assertNull(BarPattern.NONE.tile(GREEN));
    }

    @Test
    public void everyPatternTilesAndKeepsTheHue() {
        for (BarPattern p : BarPattern.values()) {
            if (p == BarPattern.NONE) {
                continue;
            }
            BufferedImage t = p.tile(GREEN);
            assertNotNull(p + " must produce a tile", t);
            assertTrue(p + " tile has size", t.getWidth() > 0 && t.getHeight() > 0);
            long r = 0, g = 0, b = 0;
            int n = t.getWidth() * t.getHeight();
            for (int x = 0; x < t.getWidth(); x++) {
                for (int y = 0; y < t.getHeight(); y++) {
                    int argb = t.getRGB(x, y);
                    r += (argb >> 16) & 0xFF;
                    g += (argb >> 8) & 0xFF;
                    b += argb & 0xFF;
                }
            }
            assertTrue(p + " green tint dominates (not grey)", g / n > r / n && g / n > b / n);
        }
    }

    @Test
    public void tilesAreDeterministic() {
        for (BarPattern p : BarPattern.values()) {
            if (p == BarPattern.NONE) {
                continue;
            }
            BufferedImage a = p.tile(GREEN);
            BufferedImage b = p.tile(GREEN);
            int[] pa = a.getRGB(0, 0, a.getWidth(), a.getHeight(), null, 0, a.getWidth());
            int[] pb = b.getRGB(0, 0, b.getWidth(), b.getHeight(), null, 0, b.getWidth());
            assertArrayEquals(p + " tile must be deterministic", pa, pb);
        }
    }
}
