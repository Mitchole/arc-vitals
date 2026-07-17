package com.arcvitals;

import java.awt.Color;
import java.awt.Paint;
import java.awt.TexturePaint;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class PatternPaintsTest {

    private static final Color GREEN = new Color(0, 180, 0);

    @Test
    public void noneResolvesToTheSolidColour() {
        PatternPaints paints = new PatternPaints();
        assertSame(GREEN, paints.resolve(BarPattern.NONE, GREEN));
    }

    @Test
    public void patternResolvesToATexturePaint() {
        PatternPaints paints = new PatternPaints();
        Paint p = paints.resolve(BarPattern.MESH, GREEN);
        assertTrue(p instanceof TexturePaint);
    }

    @Test
    public void sameKeyIsCached() {
        PatternPaints paints = new PatternPaints();
        Paint first = paints.resolve(BarPattern.SCALES, GREEN);
        Paint again = paints.resolve(BarPattern.SCALES, GREEN);
        assertSame(first, again);
    }

    @Test
    public void differentColourGetsADifferentPaint() {
        PatternPaints paints = new PatternPaints();
        Paint green = paints.resolve(BarPattern.SCALES, GREEN);
        Paint red = paints.resolve(BarPattern.SCALES, new Color(200, 40, 40));
        assertNotSame(green, red);
    }

    @Test
    public void differentPatternGetsADifferentPaint() {
        PatternPaints paints = new PatternPaints();
        Paint mesh = paints.resolve(BarPattern.MESH, GREEN);
        Paint scales = paints.resolve(BarPattern.SCALES, GREEN);
        assertNotSame(mesh, scales);
    }
}
