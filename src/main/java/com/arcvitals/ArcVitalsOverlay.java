package com.arcvitals;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.MenuEntry;
import net.runelite.api.Prayer;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.itemstats.Effect;
import net.runelite.client.plugins.itemstats.ItemStatChangesService;
import net.runelite.client.plugins.itemstats.StatsChanges;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

public class ArcVitalsOverlay extends Overlay {

    private final Client client;
    private final ArcVitalsConfig config;
    private final ItemStatChangesService itemStatService;
    private final CombatTracker combatTracker;

    private static final Vital[] VITALS = Vital.values();
    private static final Prayer[] PRAYERS = Prayer.values();
    private final EnumMap<Vital, BarState> states = new EnumMap<>(Vital.class);

    private final Map<Long, Geometry> geometryCache = new HashMap<>();
    private int cacheCx = Integer.MIN_VALUE;
    private int cacheCy;
    private int cacheSize;
    private int cacheThickness;
    private int cacheGap;
    private int cacheBarSpacing;
    private int cacheCurve;
    private boolean cacheFlatEnds;

    @Inject
    ArcVitalsOverlay(Client client, ArcVitalsConfig config, ItemStatChangesService itemStatService,
                     CombatTracker combatTracker) {
        this.client = client;
        this.config = config;
        this.itemStatService = itemStatService;
        this.combatTracker = combatTracker;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.UNDER_WIDGETS);
    }

    @Override
    public Dimension render(Graphics2D g) {
        if (client.getGameState() != GameState.LOGGED_IN || client.getLocalPlayer() == null) {
            return null;
        }
        boolean debug = config.debugEnabled();
        Visibility visibility = debug ? Visibility.FULL : resolveVisibility();
        if (visibility == Visibility.HIDDEN) {
            return null;
        }

        states.clear();
        boolean anyLow = false;
        for (Vital v : VITALS) {
            if (!v.enabled(config)) {
                continue;
            }
            if (visibility == Visibility.PRAYER_ONLY && v != Vital.PRAYER) {
                continue;
            }
            int max = v.max(client);
            int current = debug ? Math.round(max * v.debugPercent(config) / 100f) : v.current(client);
            BarState s = BarState.of(current, max, v.threshold(config));
            states.put(v, s);
            if (s.low) {
                anyLow = true;
            }
        }
        if (states.isEmpty()) {
            return null;
        }

        StatsChanges hovered = config.showRestorePreview() ? resolveHovered() : null;

        HpStatus hpStatus = debug ? config.debugPoisonState() : HpStatus.of(client.getVarpValue(VarPlayerID.POISON));

        int cx = centreX();
        int cy = centreY();
        refreshGeometryCache(cx, cy);

        drawSide(g, states, Side.LEFT, true, anyLow, cx, cy, hovered, hpStatus);
        drawSide(g, states, Side.RIGHT, false, anyLow, cx, cy, hovered, hpStatus);
        return null;
    }

    private void drawSide(Graphics2D g, EnumMap<Vital, BarState> states, Side side, boolean leftSide,
                          boolean anyLow, int cx, int cy, StatsChanges hovered, HpStatus hpStatus) {
        int index = 0;
        for (Vital v : VITALS) {
            BarState s = states.get(v);
            if (s == null || v.side(config) != side) {
                continue;
            }
            int gap = BarLayout.gapForIndex(config.gap(), config.thickness(), config.barSpacing(), index);
            drawVital(g, v, s, leftSide, anyLow, gap, cx, cy, index, hovered, hpStatus);
            index++;
        }
    }

    private void drawVital(Graphics2D g, Vital v, BarState self, boolean leftSide, boolean anyLow,
                           int gap, int cx, int cy, int index, StatsChanges hovered, HpStatus hpStatus) {
        int current = self.current;
        int max = self.max;
        float alpha = BarState.opacity(self.low, anyLow, config.alertMode(), config.baseOpacity(), config.alertOpacity());
        Color base = v.color(config);
        if (v == Vital.HITPOINTS) {
            base = HpStatus.resolve(hpStatus, config.hpPoisonRecolor(), base, config.hpPoisonColor(), config.hpVenomColor());
        }
        Color fill = BarState.warn(self, config.warnColorEnabled()) ? config.warnColor() : base;

        int restore = (hovered != null && v.restoreStatName() != null)
            ? RestorePreview.forStat(hovered, v.restoreStatName()) : 0;

        Composite oldComposite = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, clamp01(alpha)));

        double previewFraction = (restore > 0) ? BarState.previewFraction(current, max, restore) : 0.0;
        Color previewColor = (restore > 0) ? lighten(fill) : null;

        Color outline = config.showOutline() ? config.outlineColor() : null;
        BarShape shape = v.shape(config);
        long cacheKey = ((long) shape.ordinal() << 8) | ((long) index << 1) | (leftSide ? 1 : 0);
        Geometry geo = geometryCache.computeIfAbsent(cacheKey,
            k -> shape.build(cx, cy, config.size(), config.thickness(), config.gap(), config.barSpacing(),
                config.curve(), index, leftSide, config.flatEnds()));
        BarRenderer.draw(g, geo, v.fillStyle(config), config.fillDirection(), self.fraction, fill,
            config.trackColor(), outline, config.outlineWidth(), previewFraction, previewColor);

        String txt = ValueText.format(current, max, config.valueDisplay());
        if (!txt.isEmpty()) {
            g.setFont(FontManager.getRunescapeSmallFont());
            FontMetrics fm = g.getFontMetrics();
            int tipX = leftSide ? (cx - gap) : (cx + gap);
            int tx = tipX - fm.stringWidth(txt) / 2;
            int ty = BarLayout.labelBaselineY(cy, config.size(), fm.getHeight(), index);
            g.setColor(Color.BLACK);
            g.drawString(txt, tx + 1, ty + 1);
            g.setColor(fill);
            g.drawString(txt, tx, ty);
        }

        g.setComposite(oldComposite);
    }

    // Resolves the item currently hovered in the inventory to its stat changes, or null when
    // nothing eligible is hovered. Mirrors RuneLite's ItemStatOverlay: skip while a right-click
    // menu is open, and only consider inventory items.
    private StatsChanges resolveHovered() {
        if (client.isMenuOpen()) {
            return null;
        }
        MenuEntry[] menu = client.getMenu().getMenuEntries();
        if (menu.length == 0) {
            return null;
        }
        Widget w = menu[menu.length - 1].getWidget();
        if (w == null || w.getId() != InterfaceID.Inventory.ITEMS) {
            return null;
        }
        Effect effect = itemStatService.getItemStatChanges(w.getItemId());
        if (effect == null) {
            return null;
        }
        return effect.calculate(client);
    }

    private Visibility resolveVisibility() {
        boolean hide = config.hideOutOfCombat();
        PrayerVisibility mode = config.showWhilePraying();
        boolean praying = hide && mode != PrayerVisibility.OFF && anyPrayerActive();
        return CombatTracker.resolve(hide, client.getTickCount(), combatTracker.getLastCombatTick(),
            config.hideOutOfCombatDelay(), mode, praying);
    }

    // isPrayerActive is deprecated for misreading deadeye/mystic vigour through
    // their base prayers; iterating every Prayer covers those via their own entries.
    @SuppressWarnings("deprecation")
    private boolean anyPrayerActive() {
        for (Prayer p : PRAYERS) {
            if (client.isPrayerActive(p)) {
                return true;
            }
        }
        return false;
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

    // Clears the cached per-bar geometry whenever any input to it changes.
    // cx/cy are included because the geometry is positioned absolutely.
    private void refreshGeometryCache(int cx, int cy) {
        int size = config.size();
        int thickness = config.thickness();
        int gap = config.gap();
        int spacing = config.barSpacing();
        int curve = config.curve();
        boolean flat = config.flatEnds();
        if (cx != cacheCx || cy != cacheCy || size != cacheSize || thickness != cacheThickness
            || gap != cacheGap || spacing != cacheBarSpacing || curve != cacheCurve
            || flat != cacheFlatEnds) {
            geometryCache.clear();
            cacheCx = cx;
            cacheCy = cy;
            cacheSize = size;
            cacheThickness = thickness;
            cacheGap = gap;
            cacheBarSpacing = spacing;
            cacheCurve = curve;
            cacheFlatEnds = flat;
        }
    }

    private static float clamp01(float v) {
        if (v < 0f) {
            return 0f;
        }
        return v > 1f ? 1f : v;
    }
}
