package com.arcvitals;

import net.runelite.api.Skill;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class VitalTest {
    @Test
    public void restoreStatNamesMatchGameStats() {
        assertEquals(Skill.HITPOINTS.getName(), Vital.HITPOINTS.restoreStatName());
        assertEquals(Skill.PRAYER.getName(), Vital.PRAYER.restoreStatName());
        assertEquals("Run Energy", Vital.RUN_ENERGY.restoreStatName());
        assertNull(Vital.SPECIAL_ATTACK.restoreStatName());
    }

    @Test
    public void labels() {
        assertEquals("Hitpoints", Vital.HITPOINTS.label());
        assertEquals("Special attack", Vital.SPECIAL_ATTACK.label());
        assertEquals("Run energy", Vital.RUN_ENERGY.label());
    }
}
