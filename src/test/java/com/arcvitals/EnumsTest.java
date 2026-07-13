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
    }
}
