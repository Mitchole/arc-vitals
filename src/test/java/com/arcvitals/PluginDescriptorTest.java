package com.arcvitals;

import java.util.Arrays;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.itemstats.ItemStatPlugin;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PluginDescriptorTest {
    @Test
    public void descriptorNameAndDescription() {
        PluginDescriptor d = ArcVitalsPlugin.class.getAnnotation(PluginDescriptor.class);
        assertEquals("Arc Vitals", d.name());
        assertEquals("An IceHUD inspired Vitals HUD", d.description());
    }

    @Test
    public void declaresItemStatsDependency() {
        // The restore preview injects ItemStatChangesService, which Item Stats binds privately.
        // Without this dependency the plugin's injector cannot resolve it and fails to load.
        PluginDependency[] deps = ArcVitalsPlugin.class.getAnnotationsByType(PluginDependency.class);
        boolean dependsOnItemStats = Arrays.stream(deps).anyMatch(d -> d.value() == ItemStatPlugin.class);
        assertTrue("ArcVitalsPlugin must declare @PluginDependency(ItemStatPlugin.class)", dependsOnItemStats);
    }
}
