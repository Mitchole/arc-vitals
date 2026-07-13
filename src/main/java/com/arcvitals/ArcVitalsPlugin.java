package com.arcvitals;

import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
    name = "Arc Vitals",
    description = "An IceHUD inspired Vitals HUD",
    tags = {"hud", "health", "hitpoints", "prayer", "overlay", "bars", "vitals"}
)
public class ArcVitalsPlugin extends Plugin {

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private ArcVitalsOverlay overlay;

    @Provides
    ArcVitalsConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(ArcVitalsConfig.class);
    }

    @Override
    protected void startUp() {
        overlayManager.add(overlay);
    }

    @Override
    protected void shutDown() {
        overlayManager.remove(overlay);
    }
}
