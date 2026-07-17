package com.arcvitals;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import java.lang.reflect.Modifier;
import static org.junit.Assert.assertTrue;

public class OverrideResolveTest {

    @Test
    public void shapeInheritReturnsGlobal() {
        for (BarShape global : BarShape.values()) {
            assertEquals(global, ShapeOverride.INHERIT.resolve(global));
        }
    }

    @Test
    public void shapeOverrideBeatsGlobal() {
        assertEquals(BarShape.ARC, ShapeOverride.ARC.resolve(BarShape.STRAIGHT));
        assertEquals(BarShape.STRAIGHT, ShapeOverride.STRAIGHT.resolve(BarShape.ARC));
        assertEquals(BarShape.LEAF, ShapeOverride.LEAF.resolve(BarShape.ARC));
        assertEquals(BarShape.HORN, ShapeOverride.HORN.resolve(BarShape.ARC));
        assertEquals(BarShape.RING, ShapeOverride.RING.resolve(BarShape.ARC));
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

    @Test
    public void patternInheritReturnsGlobal() {
        for (BarPattern global : BarPattern.values()) {
            assertEquals(global, PatternOverride.INHERIT.resolve(global));
        }
    }

    @Test
    public void patternOverrideBeatsGlobal() {
        assertEquals(BarPattern.NONE, PatternOverride.NONE.resolve(BarPattern.MESH));
        assertEquals(BarPattern.BRUSHED, PatternOverride.BRUSHED.resolve(BarPattern.NONE));
        assertEquals(BarPattern.WEAVE, PatternOverride.WEAVE.resolve(BarPattern.NONE));
        assertEquals(BarPattern.RIVETS, PatternOverride.RIVETS.resolve(BarPattern.NONE));
        assertEquals(BarPattern.SCALES, PatternOverride.SCALES.resolve(BarPattern.NONE));
        assertEquals(BarPattern.RUNE, PatternOverride.RUNE.resolve(BarPattern.NONE));
        assertEquals(BarPattern.MESH, PatternOverride.MESH.resolve(BarPattern.NONE));
    }

    @Test
    public void patternOverrideEnumIsPublic() {
        assertTrue(Modifier.isPublic(PatternOverride.class.getModifiers()));
    }
}
