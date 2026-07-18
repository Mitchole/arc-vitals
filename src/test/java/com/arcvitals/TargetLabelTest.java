package com.arcvitals;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class TargetLabelTest {

    @Test
    public void nameAndPercentJoinsBoth() {
        assertEquals("Zulrah  74%", TargetLabel.NAME_AND_PERCENT.format("Zulrah", 74));
    }

    @Test
    public void nameOnlyDropsThePercent() {
        assertEquals("Zulrah", TargetLabel.NAME_ONLY.format("Zulrah", 74));
    }

    @Test
    public void percentOnlyDropsTheName() {
        assertEquals("74%", TargetLabel.PERCENT_ONLY.format("Zulrah", 74));
    }

    @Test
    public void labelsRenderForConfigDropdown() {
        assertEquals("Name and percent", TargetLabel.NAME_AND_PERCENT.toString());
        assertEquals("Name only", TargetLabel.NAME_ONLY.toString());
        assertEquals("Percent only", TargetLabel.PERCENT_ONLY.toString());
    }
}
