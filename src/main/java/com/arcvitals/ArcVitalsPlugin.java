package com.arcvitals;

import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.InteractingChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.MouseManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.itemstats.ItemStatPlugin;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
    name = "Arc Vitals",
    description = "An IceHUD inspired Vitals HUD",
    tags = {"hud", "health", "hitpoints", "prayer", "overlay", "bars", "vitals"}
)
@PluginDependency(ItemStatPlugin.class)
public class ArcVitalsPlugin extends Plugin {

    @Inject
    private Client client;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private ArcVitalsOverlay overlay;

    @Inject
    private CombatTracker combatTracker;

    @Inject
    private MouseManager mouseManager;

    @Inject
    private ArcVitalsMouseListener mouseListener;

    @Inject
    private TargetTracker targetTracker;

    @Provides
    ArcVitalsConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(ArcVitalsConfig.class);
    }

    @Override
    protected void startUp() {
        overlayManager.add(overlay);
        mouseManager.registerMouseListener(mouseListener);
    }

    @Override
    protected void shutDown() {
        overlayManager.remove(overlay);
        mouseManager.unregisterMouseListener(mouseListener);
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
    public void onInteractingChanged(InteractingChanged event) {
        if (event.getSource() == client.getLocalPlayer()) {
            targetTracker.onInteracting(event.getTarget(), client.getTickCount());
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        Player local = client.getLocalPlayer();
        targetTracker.onGameTick(local == null ? null : local.getInteracting(), client.getTickCount());
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() == GameState.LOGIN_SCREEN) {
            combatTracker.reset();
            targetTracker.reset();
        }
    }
}
