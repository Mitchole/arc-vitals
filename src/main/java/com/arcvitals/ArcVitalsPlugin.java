package com.arcvitals;

import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@PluginDescriptor(
    name = "Arc Vitals",
    description = "An IceHUD inspired Vitals HUD",
    tags = {"hud", "health", "hitpoints", "prayer", "overlay", "bars", "vitals"}
)
public class ArcVitalsPlugin extends Plugin {

    @Override
    protected void startUp() {
        // Overlay registration is wired in a later task.
    }

    @Override
    protected void shutDown() {
    }
}
