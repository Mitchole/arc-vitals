package com.arcvitals;

import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.GameState;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.InteractingChanged;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemStats;
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

    @Inject
    private ItemManager itemManager;

    @Inject
    private SwingTracker swingTracker;

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
            swingTracker.reset();
        }
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged event) {
        Player local = client.getLocalPlayer();
        if (local == null || event.getActor() != local) {
            return;
        }
        if (local.getAnimation() == -1) {
            return;
        }
        if (!isAttackTarget(local.getInteracting(), local)) {
            return;
        }
        int cooldown = SwingTiming.cooldownTicks(weaponAspeed(), rapidSelected());
        swingTracker.onSwing(cooldown, client.getTickCount(), System.nanoTime());
    }

    // Whether the actor the player is interacting with is something the player attacks. Static + pure
    // so the gate is unit-testable without a live client. Non-combat animations (eating, teleporting)
    // still pass this gate when they happen mid-fight; that known false-positive is a smoke item.
    static boolean isAttackTarget(Actor target, Player local) {
        if (target == null || target == local) {
            return false;
        }
        return target instanceof NPC || target instanceof Player;
    }

    // The equipped weapon's base attack speed in ticks; unarmed / unknown = 4.
    private int weaponAspeed() {
        ItemContainer worn = client.getItemContainer(InventoryID.WORN);
        if (worn == null) {
            return 4;
        }
        Item weapon = worn.getItem(EquipmentInventorySlot.WEAPON.getSlotIdx());
        if (weapon == null) {
            return 4;
        }
        ItemStats stats = itemManager.getItemStats(weapon.getId());
        if (stats == null || stats.getEquipment() == null) {
            return 4;
        }
        int aspeed = stats.getEquipment().getAspeed();
        return aspeed > 0 ? aspeed : 4;
    }

    // True when the selected style is ranged Rapid (one tick faster). Best-effort for v1: Rapid is the
    // second style slot (index 1) on standard ranged weapons. rangedWeapon() gates it so melee slot 1
    // (Aggressive) never subtracts a tick. If this proves wrong for a weapon in smoke, returning false
    // here degrades cleanly to base speed. See the design's stance-table risk.
    private boolean rapidSelected() {
        return rangedWeapon() && client.getVarpValue(VarPlayerID.COM_MODE) == 1;
    }

    // Whether the equipped weapon is a ranged category. The COMBAT_WEAPON_CATEGORY varbit values for
    // the ranged categories (bow, crossbow, thrown, chinchompa, gun/blowpipe) are listed here; unknown
    // categories are treated as non-ranged (no Rapid offset). Tune this set in smoke if a ranged weapon
    // is missed.
    private boolean rangedWeapon() {
        int cat = client.getVarbitValue(VarbitID.COMBAT_WEAPON_CATEGORY);
        // 3=bow, 5=chinchompa, 18=crossbow, 19=thrown, 20=gun (blowpipe). See wiki weapon categories.
        return cat == 3 || cat == 5 || cat == 18 || cat == 19 || cat == 20;
    }
}
