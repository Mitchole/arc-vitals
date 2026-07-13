package com.arcvitals;

import net.runelite.client.plugins.PluginDescriptor;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class PluginDescriptorTest {
    @Test
    public void descriptorNameAndDescription() {
        PluginDescriptor d = ArcVitalsPlugin.class.getAnnotation(PluginDescriptor.class);
        assertEquals("Arc Vitals", d.name());
        assertEquals("An IceHUD inspired Vitals HUD", d.description());
    }
}
