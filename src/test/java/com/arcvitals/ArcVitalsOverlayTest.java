package com.arcvitals;

import net.runelite.api.Client;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.plugins.itemstats.ItemStatChangesService;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class ArcVitalsOverlayTest {

    private ArcVitalsOverlay newOverlay() {
        return new ArcVitalsOverlay(
            mock(Client.class),
            mock(ArcVitalsConfig.class),
            mock(ItemStatChangesService.class),
            mock(CombatTracker.class),
            mock(SpriteManager.class));
    }

    @Test
    public void rendersOverThePlayerButUnderInterfaces() {
        assertEquals(OverlayLayer.UNDER_WIDGETS, newOverlay().getLayer());
    }

    @Test
    public void keepsDynamicPositioning() {
        assertEquals(OverlayPosition.DYNAMIC, newOverlay().getPosition());
    }
}
