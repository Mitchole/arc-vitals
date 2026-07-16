package com.arcvitals;

import java.awt.Color;
import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.api.gameval.VarPlayerID;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VitalTest {
    @Test
    public void currentAndMaxReadTheRightClientValues() {
        Client client = mock(Client.class);
        when(client.getBoostedSkillLevel(Skill.HITPOINTS)).thenReturn(50);
        when(client.getRealSkillLevel(Skill.HITPOINTS)).thenReturn(99);
        when(client.getBoostedSkillLevel(Skill.PRAYER)).thenReturn(43);
        when(client.getRealSkillLevel(Skill.PRAYER)).thenReturn(70);
        when(client.getVarpValue(VarPlayerID.SA_ENERGY)).thenReturn(750);
        when(client.getEnergy()).thenReturn(8800);

        assertEquals(50, Vital.HITPOINTS.current(client));
        assertEquals(99, Vital.HITPOINTS.max(client));
        assertEquals(43, Vital.PRAYER.current(client));
        assertEquals(70, Vital.PRAYER.max(client));
        assertEquals(75, Vital.SPECIAL_ATTACK.current(client)); // 750 / 10
        assertEquals(100, Vital.SPECIAL_ATTACK.max(client));
        assertEquals(88, Vital.RUN_ENERGY.current(client));     // 8800 / 100
        assertEquals(100, Vital.RUN_ENERGY.max(client));
    }

    @Test
    public void configAccessorsDelegateToTheMatchingGetter() {
        ArcVitalsConfig config = mock(ArcVitalsConfig.class);
        when(config.hpEnabled()).thenReturn(true);
        when(config.hpColor()).thenReturn(Color.RED);
        when(config.hpThreshold()).thenReturn(30);
        when(config.hpSide()).thenReturn(Side.LEFT);
        when(config.runEnabled()).thenReturn(false);
        when(config.runSide()).thenReturn(Side.RIGHT);

        assertTrue(Vital.HITPOINTS.enabled(config));
        assertEquals(Color.RED, Vital.HITPOINTS.color(config));
        assertEquals(30, Vital.HITPOINTS.threshold(config));
        assertEquals(Side.LEFT, Vital.HITPOINTS.side(config));
        assertFalse(Vital.RUN_ENERGY.enabled(config));
        assertEquals(Side.RIGHT, Vital.RUN_ENERGY.side(config));
    }

    @Test
    public void restoreStatNamesMatchGameStats() {
        assertEquals(Skill.HITPOINTS.getName(), Vital.HITPOINTS.restoreStatName());
        assertEquals(Skill.PRAYER.getName(), Vital.PRAYER.restoreStatName());
        assertEquals("Run Energy", Vital.RUN_ENERGY.restoreStatName());
        assertNull(Vital.SPECIAL_ATTACK.restoreStatName());
    }

    @Test
    public void debugPercentReadsPerVitalSlider() {
        ArcVitalsConfig config = mock(ArcVitalsConfig.class);
        when(config.debugHpPercent()).thenReturn(25);
        when(config.debugPrayerPercent()).thenReturn(60);
        when(config.debugSpecPercent()).thenReturn(75);
        when(config.debugRunPercent()).thenReturn(90);

        assertEquals(25, Vital.HITPOINTS.debugPercent(config));
        assertEquals(60, Vital.PRAYER.debugPercent(config));
        assertEquals(75, Vital.SPECIAL_ATTACK.debugPercent(config));
        assertEquals(90, Vital.RUN_ENERGY.debugPercent(config));
    }
}
