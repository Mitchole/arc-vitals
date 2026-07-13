package com.arcvitals;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class ValueTextTest {
    @Test
    public void off() {
        assertEquals("", ValueText.format(72, 99, ValueDisplay.OFF));
    }

    @Test
    public void currentMax() {
        assertEquals("72/99", ValueText.format(72, 99, ValueDisplay.CURRENT_MAX));
    }

    @Test
    public void percentRounds() {
        assertEquals("73%", ValueText.format(72, 99, ValueDisplay.PERCENT));
    }

    @Test
    public void both() {
        assertEquals("72/99 (73%)", ValueText.format(72, 99, ValueDisplay.BOTH));
    }

    @Test
    public void percentSafeWhenMaxZero() {
        assertEquals("0%", ValueText.format(5, 0, ValueDisplay.PERCENT));
    }

    @Test
    public void percentCanExceedHundredWhenBoosted() {
        assertEquals("106%", ValueText.format(105, 99, ValueDisplay.PERCENT));
    }
}
