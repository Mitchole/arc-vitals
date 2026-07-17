package com.arcvitals;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class OverrideResolveTest {

    @Test
    public void shapeInheritReturnsGlobal() {
        assertEquals(BarShape.ARC, ShapeOverride.INHERIT.resolve(BarShape.ARC));
        assertEquals(BarShape.STRAIGHT, ShapeOverride.INHERIT.resolve(BarShape.STRAIGHT));
    }

    @Test
    public void shapeOverrideBeatsGlobal() {
        assertEquals(BarShape.ARC, ShapeOverride.ARC.resolve(BarShape.STRAIGHT));
        assertEquals(BarShape.STRAIGHT, ShapeOverride.STRAIGHT.resolve(BarShape.ARC));
    }

    @Test
    public void fillInheritReturnsGlobal() {
        for (FillStyle global : FillStyle.values()) {
            assertEquals(global, FillStyleOverride.INHERIT.resolve(global));
        }
    }

    @Test
    public void fillOverrideBeatsGlobal() {
        assertEquals(FillStyle.SMOOTH, FillStyleOverride.SMOOTH.resolve(FillStyle.GLOW));
        assertEquals(FillStyle.GLOSS, FillStyleOverride.GLOSS.resolve(FillStyle.SMOOTH));
        assertEquals(FillStyle.GRADIENT, FillStyleOverride.GRADIENT.resolve(FillStyle.SMOOTH));
        assertEquals(FillStyle.SEGMENTED, FillStyleOverride.SEGMENTED.resolve(FillStyle.SMOOTH));
        assertEquals(FillStyle.GLOW, FillStyleOverride.GLOW.resolve(FillStyle.SMOOTH));
        assertEquals(FillStyle.NOTCHED, FillStyleOverride.NOTCHED.resolve(FillStyle.SMOOTH));
    }
}
