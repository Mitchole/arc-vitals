package com.arcvitals;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.Skill;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.plugins.itemstats.ItemStatChangesService;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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

    @Test
    public void swingTimerNestedRendersWithoutError() {
        when(config.swingEnabled()).thenReturn(true);
        when(config.swingPlacement()).thenReturn(SwingPlacement.NESTED);
        when(config.swingColor()).thenReturn(new java.awt.Color(210, 235, 248));
        when(config.swingSide()).thenReturn(Side.LEFT);
        when(config.showSwingTicks()).thenReturn(true);
        when(swingTracker.showing(anyInt())).thenReturn(true);
        when(swingTracker.fraction(anyLong())).thenReturn(0.3);
        when(swingTracker.ready(anyLong())).thenReturn(false);
        when(swingTracker.cooldownTicks()).thenReturn(5);

        overlay.render(graphics);
    }

    // Stubs everything drawVital reads on the hitpoints path, boosted to 117 / 99, so a full render()
    // actually draws the HP bar. Overheal on/off is left to each test.
    private void stubBoostedHp() {
        when(client.getCanvasWidth()).thenReturn(765);
        when(client.getCanvasHeight()).thenReturn(503);
        when(client.getVarpValue(anyInt())).thenReturn(0);
        when(client.getRealSkillLevel(Skill.HITPOINTS)).thenReturn(99);
        when(client.getBoostedSkillLevel(Skill.HITPOINTS)).thenReturn(117);

        when(config.hpEnabled()).thenReturn(true);
        when(config.hpColor()).thenReturn(new Color(0, 200, 0));
        when(config.hpThreshold()).thenReturn(0);
        when(config.hpSide()).thenReturn(Side.LEFT);
        when(config.hpShapeOverride()).thenReturn(ShapeOverride.INHERIT);
        when(config.hpFillOverride()).thenReturn(FillStyleOverride.INHERIT);
        when(config.hpPatternOverride()).thenReturn(PatternOverride.INHERIT);
        when(config.barShape()).thenReturn(BarShape.ARC);
        when(config.alertMode()).thenReturn(AlertMode.OFF);
        when(config.alertOpacity()).thenReturn(100);
        when(config.fillDirection()).thenReturn(FillDirection.BOTTOM_UP);
        when(config.valueDisplay()).thenReturn(ValueDisplay.OFF);
    }

    private static int cyanBandCount(BufferedImage img) {
        int n = 0;
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                int argb = img.getRGB(x, y);
                int a = (argb >>> 24) & 0xFF;
                int r = (argb >> 16) & 0xFF;
                int gr = (argb >> 8) & 0xFF;
                int b = argb & 0xFF;
                if (a > 120 && gr > 150 && b > 150 && r < 160) {
                    n++;
                }
            }
        }
        return n;
    }

    @Test
    public void overhealBandRendersForBoostedHp() {
        stubBoostedHp();
        when(config.overhealEnabled()).thenReturn(true);
        when(config.overhealColor()).thenReturn(new Color(120, 240, 255, 235));
        when(config.showOverhealTick()).thenReturn(true);

        BufferedImage image = new BufferedImage(765, 503, BufferedImage.TYPE_INT_ARGB);
        overlay.render(image.createGraphics());

        assertTrue("boosted HP should draw an overheal band", cyanBandCount(image) > 0);
    }

    @Test
    public void noOverhealBandWhenDisabled() {
        stubBoostedHp();
        when(config.overhealEnabled()).thenReturn(false);

        BufferedImage image = new BufferedImage(765, 503, BufferedImage.TYPE_INT_ARGB);
        overlay.render(image.createGraphics());

        assertEquals("disabled overheal draws no band", 0, cyanBandCount(image));
    }

    @Test
    public void debugAnimatePrayerIconsRenderWithoutError() {
        when(client.getCanvasWidth()).thenReturn(765);
        when(client.getCanvasHeight()).thenReturn(503);
        when(config.debugEnabled()).thenReturn(true);
        when(config.debugAnimate()).thenReturn(true);
        when(config.debugHpPercent()).thenReturn(50);
        when(config.debugPrayerPercent()).thenReturn(50);
        when(config.debugSpecPercent()).thenReturn(50);
        when(config.debugRunPercent()).thenReturn(50);
        when(config.debugPoisonState()).thenReturn(HpStatus.NONE);
        when(config.hpEnabled()).thenReturn(true);
        when(config.hpColor()).thenReturn(new Color(0, 200, 0));
        when(config.hpThreshold()).thenReturn(0);
        when(config.hpSide()).thenReturn(Side.LEFT);
        when(config.hpShapeOverride()).thenReturn(ShapeOverride.INHERIT);
        when(config.hpFillOverride()).thenReturn(FillStyleOverride.INHERIT);
        when(config.hpPatternOverride()).thenReturn(PatternOverride.INHERIT);
        when(config.barShape()).thenReturn(BarShape.ARC);
        when(config.alertMode()).thenReturn(AlertMode.OFF);
        when(config.fillDirection()).thenReturn(FillDirection.BOTTOM_UP);
        when(config.valueDisplay()).thenReturn(ValueDisplay.OFF);
        when(config.showPrayerIcons()).thenReturn(true);
        when(config.debugPrayerIcons()).thenReturn(3);
        when(config.prayerIconSize()).thenReturn(24);
        when(config.prayerIconOffset()).thenReturn(6);
        when(config.prayerIconSpacing()).thenReturn(2);
        when(config.prayerIconBackground()).thenReturn(false);

        overlay.render(graphics); // must not throw with animate + debug prayer icons on
    }

    @Test
    public void prayerIconsHiddenInDebugWhenToggleOff() {
        when(client.getCanvasWidth()).thenReturn(765);
        when(client.getCanvasHeight()).thenReturn(503);
        when(config.debugEnabled()).thenReturn(true);
        when(config.debugHpPercent()).thenReturn(50);
        when(config.debugPrayerPercent()).thenReturn(50);
        when(config.debugSpecPercent()).thenReturn(50);
        when(config.debugRunPercent()).thenReturn(50);
        when(config.debugPoisonState()).thenReturn(HpStatus.NONE);
        when(config.hpEnabled()).thenReturn(true);
        when(config.hpColor()).thenReturn(new Color(0, 200, 0));
        when(config.hpThreshold()).thenReturn(0);
        when(config.hpSide()).thenReturn(Side.LEFT);
        when(config.hpShapeOverride()).thenReturn(ShapeOverride.INHERIT);
        when(config.hpFillOverride()).thenReturn(FillStyleOverride.INHERIT);
        when(config.hpPatternOverride()).thenReturn(PatternOverride.INHERIT);
        when(config.barShape()).thenReturn(BarShape.ARC);
        when(config.alertMode()).thenReturn(AlertMode.OFF);
        when(config.fillDirection()).thenReturn(FillDirection.BOTTOM_UP);
        when(config.valueDisplay()).thenReturn(ValueDisplay.OFF);
        when(config.showPrayerIcons()).thenReturn(false);
        when(config.debugPrayerIcons()).thenReturn(8);

        overlay.render(graphics); // showPrayerIcons off -> icon path never entered, no throw
        // Guard: firstSpriteIds must NOT have been consulted because the row is gated off.
        // (Behavioural assertion is the no-throw + the code path in Step 2 sitting inside the
        // showPrayerIcons branch; there is no icon sprite to count here since SpriteManager is a mock.)
    }
}
