package com.arcvitals;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VitalStyleTest {

    @Test
    public void inheritUsesGlobalShapeAndFill() {
        ArcVitalsConfig config = mock(ArcVitalsConfig.class);
        when(config.barShape()).thenReturn(BarShape.STRAIGHT);
        when(config.fillStyle()).thenReturn(FillStyle.GLOW);
        when(config.hpShapeOverride()).thenReturn(ShapeOverride.INHERIT);
        when(config.hpFillOverride()).thenReturn(FillStyleOverride.INHERIT);

        assertEquals(BarShape.STRAIGHT, Vital.HITPOINTS.shape(config));
        assertEquals(FillStyle.GLOW, Vital.HITPOINTS.fillStyle(config));
    }

    @Test
    public void perBarOverrideBeatsGlobal() {
        ArcVitalsConfig config = mock(ArcVitalsConfig.class);
        when(config.barShape()).thenReturn(BarShape.ARC);
        when(config.fillStyle()).thenReturn(FillStyle.SMOOTH);
        when(config.prayerShapeOverride()).thenReturn(ShapeOverride.STRAIGHT);
        when(config.prayerFillOverride()).thenReturn(FillStyleOverride.GRADIENT);

        assertEquals(BarShape.STRAIGHT, Vital.PRAYER.shape(config));
        assertEquals(FillStyle.GRADIENT, Vital.PRAYER.fillStyle(config));
    }

    @Test
    public void inheritUsesGlobalPattern() {
        ArcVitalsConfig config = mock(ArcVitalsConfig.class);
        when(config.barPattern()).thenReturn(BarPattern.SCALES);
        when(config.hpPatternOverride()).thenReturn(PatternOverride.INHERIT);
        assertEquals(BarPattern.SCALES, Vital.HITPOINTS.pattern(config));
    }

    @Test
    public void perBarPatternOverrideBeatsGlobal() {
        ArcVitalsConfig config = mock(ArcVitalsConfig.class);
        when(config.barPattern()).thenReturn(BarPattern.NONE);
        when(config.runPatternOverride()).thenReturn(PatternOverride.MESH);
        assertEquals(BarPattern.MESH, Vital.RUN_ENERGY.pattern(config));
    }
}
