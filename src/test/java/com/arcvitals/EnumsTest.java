package com.arcvitals;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class EnumsTest {
    @Test
    public void enumLabels() {
        assertEquals("Per bar", AlertMode.PER_BAR.toString());
        assertEquals("Whole HUD", AlertMode.WHOLE_HUD.toString());
        assertEquals("Bottom up", FillDirection.BOTTOM_UP.toString());
        assertEquals("Current / Max", ValueDisplay.CURRENT_MAX.toString());
        assertEquals("Off", PrayerVisibility.OFF.toString());
        assertEquals("Prayer bar", PrayerVisibility.PRAYER_BAR.toString());
        assertEquals("Whole HUD", PrayerVisibility.WHOLE_HUD.toString());
        assertEquals("None", HpStatus.NONE.toString());
        assertEquals("Poisoned", HpStatus.POISONED.toString());
        assertEquals("Venomed", HpStatus.VENOMED.toString());
        assertEquals("Leaf", BarShape.LEAF.toString());
        assertEquals("Horn", BarShape.HORN.toString());
    }
}
