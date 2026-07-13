package com.arcvitals;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.EnumMap;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.MenuEntry;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.itemstats.Effect;
import net.runelite.client.plugins.itemstats.ItemStatChangesService;
import net.runelite.client.plugins.itemstats.StatChange;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

public class ArcVitalsOverlay extends Overlay {

    private final Client client;
    private final ArcVitalsConfig config;
    private final ItemStatChangesService itemStatService;
    private final CombatTracker combatTracker;

    @Inject
    ArcVitalsOverlay(Client client, ArcVitalsConfig config, ItemStatChangesService itemStatService,
                     CombatTracker combatTracker) {
        this.client = client;
        this.config = config;
        this.itemStatService = itemStatService;
        this.combatTracker = combatTracker;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D g) {
        if (client.getGameState() != GameState.LOGGED_IN || client.getLocalPlayer() == null) {
            return null;
        }
        if (!CombatTracker.shouldShow(config.hideOutOfCombat(), client.getTickCount(),
                combatTracker.getLastCombatTick(), config.hideOutOfCombatDelay())) {
            return null;
        }

        EnumMap<Vital, BarState> states = new EnumMap<>(Vital.class);
        boolean anyLow = false;
        for (Vital v : Vital.values()) {
            if (!v.enabled(config)) {
                continue;
            }
            BarState s = BarState.of(v.current(client), v.max(client), v.threshold(config));
            states.put(v, s);
            if (s.low) {
                anyLow = true;
            }
        }
        if (states.isEmpty()) {
            return null;
        }

        int cx = centreX();
        int cy = centreY();

        drawSide(g, states, Side.LEFT, true, anyLow, cx, cy);
        drawSide(g, states, Side.RIGHT, false, anyLow, cx, cy);
        return null;
    }

    private void drawSide(Graphics2D g, EnumMap<Vital, BarState> states, Side side, boolean leftSide,
                          boolean anyLow, int cx, int cy) {
        int index = 0;
        for (Vital v : Vital.values()) {
            BarState s = states.get(v);
            if (s == null || v.side(config) != side) {
                continue;
            }
            int gap = BarLayout.gapForIndex(config.gap(), config.thickness(), config.barSpacing(), index);
            drawVital(g, v, s, leftSide, anyLow, gap, cx, cy);
            index++;
        }
    }

    private void drawVital(Graphics2D g, Vital v, BarState self, boolean leftSide, boolean anyLow,
                           int gap, int cx, int cy) {
        int current = self.current;
        int max = self.max;
        float alpha = BarState.opacity(self.low, anyLow, config.alertMode(), config.baseOpacity(), config.alertOpacity());
        Color base = v.color(config);
        Color fill = BarState.warn(self, config.warnColorEnabled()) ? config.warnColor() : base;

        int restore = (config.showRestorePreview() && v.restoreStatName() != null)
            ? restoreFor(v.restoreStatName()) : 0;

        Composite oldComposite = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, clamp01(alpha)));

        double previewFraction = (restore > 0) ? BarState.previewFraction(current, max, restore) : 0.0;
        Color previewColor = (restore > 0) ? lighten(fill) : null;

        Color outline = config.showOutline() ? config.outlineColor() : null;
        ArcBar.draw(g, cx, cy, config.size(), config.thickness(), gap, config.curve(),
            leftSide, config.fillDirection(), self.fraction, fill, config.trackColor(),
            config.flatEnds(), outline, config.outlineWidth(),
            previewFraction, previewColor);

        String txt = ValueText.format(current, max, config.valueDisplay());
        if (!txt.isEmpty()) {
            g.setFont(FontManager.getRunescapeSmallFont());
            FontMetrics fm = g.getFontMetrics();
            int tipX = leftSide ? (cx - gap) : (cx + gap);
            int tx = tipX - fm.stringWidth(txt) / 2;
            int ty = cy + config.size() / 2 + fm.getHeight();
            g.setColor(Color.BLACK);
            g.drawString(txt, tx + 1, ty + 1);
            g.setColor(fill);
            g.drawString(txt, tx, ty);
        }

        g.setComposite(oldComposite);
    }

    private int restoreFor(String statName) {
        MenuEntry[] menu = client.getMenu().getMenuEntries();
        if (menu.length == 0) {
            return 0;
        }
        Widget w = menu[menu.length - 1].getWidget();
        if (w == null || w.getId() != InterfaceID.Inventory.ITEMS) {
            return 0;
        }
        Effect effect = itemStatService.getItemStatChanges(w.getItemId());
        if (effect == null) {
            return 0;
        }
        for (StatChange c : effect.calculate(client).getStatChanges()) {
            if (c.getTheoretical() != 0 && c.getStat().getName().equals(statName)) {
                return c.getTheoretical();
            }
        }
        return 0;
    }

    private static Color lighten(Color c) {
        int r = c.getRed() + (255 - c.getRed()) / 2;
        int g = c.getGreen() + (255 - c.getGreen()) / 2;
        int b = c.getBlue() + (255 - c.getBlue()) / 2;
        return new Color(r, g, b, 150);
    }

    private int centreX() {
        int vpW = client.getViewportWidth();
        int vpX = client.getViewportXOffset();
        if (vpW <= 0) {
            vpW = client.getCanvasWidth();
            vpX = 0;
        }
        return vpX + vpW / 2 + config.offsetX();
    }

    private int centreY() {
        int vpH = client.getViewportHeight();
        int vpY = client.getViewportYOffset();
        if (vpH <= 0) {
            vpH = client.getCanvasHeight();
            vpY = 0;
        }
        return vpY + vpH / 2 + config.offsetY();
    }

    private static float clamp01(float v) {
        if (v < 0f) {
            return 0f;
        }
        return v > 1f ? 1f : v;
    }
}
