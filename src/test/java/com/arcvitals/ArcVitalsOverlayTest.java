package com.arcvitals;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.plugins.itemstats.ItemStatChangesService;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ArcVitalsOverlayTest {

    private Client client;
    private ArcVitalsConfig config;
    private HudDragController dragController;
    private TargetTracker targetTracker;
    private SwingTracker swingTracker;
    private ArcVitalsOverlay overlay;
    private Graphics2D graphics;

    // Sensible non-degenerate layout values so any geometry actually built during render() is real
    // (no NaN from a zero-size arc). Plain Mockito mocks otherwise return 0/false/null for every
    // config method, ignoring the interface's own @ConfigItem defaults.
    @Before
    public void setUp() {
        client = mock(Client.class);
        config = mock(ArcVitalsConfig.class);
        ItemStatChangesService itemStatService = mock(ItemStatChangesService.class);
        CombatTracker combatTracker = mock(CombatTracker.class);
        SpriteManager spriteManager = mock(SpriteManager.class);
        dragController = mock(HudDragController.class);
        targetTracker = mock(TargetTracker.class);
        swingTracker = mock(SwingTracker.class);

        when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
        when(client.getLocalPlayer()).thenReturn(mock(Player.class));

        when(config.size()).thenReturn(150);
        when(config.thickness()).thenReturn(12);
        when(config.gap()).thenReturn(88);
        when(config.barSpacing()).thenReturn(7);
        when(config.curve()).thenReturn(120);
        when(config.barPattern()).thenReturn(BarPattern.NONE);
        when(config.fillStyle()).thenReturn(FillStyle.SMOOTH);
        when(config.segments()).thenReturn(14);
        when(config.trackColor()).thenReturn(new Color(0, 0, 0, 130));
        when(config.baseOpacity()).thenReturn(60);

        overlay = new ArcVitalsOverlay(client, config, itemStatService, combatTracker, spriteManager,
            dragController, targetTracker, swingTracker);

        BufferedImage image = new BufferedImage(765, 503, BufferedImage.TYPE_INT_ARGB);
        graphics = image.createGraphics();
    }

    @Test
    public void rendersOverThePlayerButUnderInterfaces() {
        assertEquals(OverlayLayer.UNDER_WIDGETS, overlay.getLayer());
    }

    @Test
    public void keepsDynamicPositioning() {
        assertEquals(OverlayPosition.DYNAMIC, overlay.getPosition());
    }

    @Test
    public void swingTimerRendersWithoutError() {
        when(config.swingEnabled()).thenReturn(true);
        when(config.swingPlacement()).thenReturn(SwingPlacement.TOP);
        when(config.swingColor()).thenReturn(new java.awt.Color(210, 235, 248));
        when(config.swingSide()).thenReturn(Side.LEFT);
        when(config.showSwingTicks()).thenReturn(true);
        when(config.swingOffsetX()).thenReturn(0);
        when(config.swingOffsetY()).thenReturn(0);
        when(swingTracker.showing(anyInt())).thenReturn(true);
        when(swingTracker.fraction(anyLong())).thenReturn(0.55);
        when(swingTracker.ready(anyLong())).thenReturn(false);
        when(swingTracker.cooldownTicks()).thenReturn(4);

        // Should draw the swing timer without throwing, even with vitals disabled.
        overlay.render(graphics);
    }
}
