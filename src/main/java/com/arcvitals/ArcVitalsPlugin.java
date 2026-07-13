package com.arcvitals;

import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
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
    private Client client;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private ArcVitalsOverlay overlay;

    @Inject
    private CombatTracker combatTracker;

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

    @Subscribe
    public void onHitsplatApplied(HitsplatApplied event) {
        Player local = client.getLocalPlayer();
        if (local == null) {
            return;
        }
        Actor actor = event.getActor();
        if (actor == local || actor == local.getInteracting()) {
            combatTracker.recordCombat(client.getTickCount());
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() == GameState.LOGIN_SCREEN) {
            combatTracker.reset();
        }
    }
}
