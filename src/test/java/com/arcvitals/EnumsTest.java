package com.arcvitals;

import net.runelite.api.Skill;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class EnumsTest {
    @Test
    public void vitalMapsToSkill() {
        assertEquals(Skill.HITPOINTS, Vital.HITPOINTS.skill());
        assertEquals(Skill.PRAYER, Vital.PRAYER.skill());
        assertEquals("HP", Vital.HITPOINTS.label());
    }

    @Test
    public void enumLabels() {
        assertEquals("Per bar", AlertMode.PER_BAR.toString());
        assertEquals("Whole HUD", AlertMode.WHOLE_HUD.toString());
        assertEquals("Bottom up", FillDirection.BOTTOM_UP.toString());
        assertEquals("Current / Max", ValueDisplay.CURRENT_MAX.toString());
    }
}
