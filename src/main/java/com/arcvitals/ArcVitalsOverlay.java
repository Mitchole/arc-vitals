package com.arcvitals;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.MenuEntry;
import net.runelite.api.Prayer;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.game.SpriteManager;
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
    private final SpriteManager spriteManager;
    private final HudDragController dragController;

    private static final Vital[] VITALS = Vital.values();
    private static final Prayer[] PRAYERS = Prayer.values();
    private final EnumMap<Vital, BarState> states = new EnumMap<>(Vital.class);

    // Per-vital displayed fill fraction, eased toward the real fraction each frame. Cleared whenever
    // nothing is drawn (logged out, HUD hidden, no bars) so a bar snaps rather than sweeping from a
    // stale value when it reappears. lastFrameNanos = 0 means "no previous frame" -> no easing this frame.
    private final EnumMap<Vital, Double> displayed = new EnumMap<>(Vital.class);
    private long lastFrameNanos;

    // Loaded prayer-tab sprites keyed by sprite id. A null value means "requested, still loading";
    // populated on the client thread by the getSpriteAsync callback, read on the client thread in
    // render, so no synchronisation is needed.
    private final Map<Integer, BufferedImage> spriteCache = new HashMap<>();

    private final Map<Long, Geometry> geometryCache = new HashMap<>();
    private final PatternPaints patternPaints = new PatternPaints();
    private int cacheCx = Integer.MIN_VALUE;
    private int cacheCy;
    private int cacheSize;
    private int cacheThickness;
    private int cacheGap;
    private int cacheBarSpacing;
    private int cacheCurve;
    private boolean cacheFlatEnds;

    private static final int ICON_BG_PAD = 3;
    private static final Color DRAG_OUTLINE_COLOR = new Color(255, 150, 0);

    // Mirror the @Range on offsetX/offsetY in ArcVitalsConfig; the annotation values cannot be read
    // at runtime without reflection (banned in src/main).
    private static final int MAIN_OFFSET_MIN = -500;
    private static final int MAIN_OFFSET_MAX = 500;

    // Running union of the geometry drawn for the current unit, in canvas pixels. Reset to null
    // before a unit is drawn, grown by addBounds as each bar and the prayer-icon row draw, then read
    // off into the per-frame bounds map.
    private Rectangle accum;

    @Inject
    ArcVitalsOverlay(Client client, ArcVitalsConfig config, ItemStatChangesService itemStatService,
                     CombatTracker combatTracker, SpriteManager spriteManager, HudDragController dragController) {
        this.client = client;
        this.config = config;
        this.itemStatService = itemStatService;
        this.combatTracker = combatTracker;
        this.spriteManager = spriteManager;
        this.dragController = dragController;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.UNDER_WIDGETS);
    }

    @Override
    public Dimension render(Graphics2D g) {
        if (client.getGameState() != GameState.LOGGED_IN || client.getLocalPlayer() == null) {
            dragController.setTargets(Collections.emptyList());
            displayed.clear();
            lastFrameNanos = 0L;
            return null;
        }
        boolean debug = config.debugEnabled();
        Visibility visibility = debug ? Visibility.FULL : resolveVisibility();
        if (visibility == Visibility.HIDDEN) {
            dragController.setTargets(Collections.emptyList());
            displayed.clear();
            lastFrameNanos = 0L;
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
            dragController.setTargets(Collections.emptyList());
            displayed.clear();
            lastFrameNanos = 0L;
            return null;
        }

        long now = System.nanoTime();
        long dtMillis = lastFrameNanos == 0L ? 0L : Math.min(100L, (now - lastFrameNanos) / 1_000_000L);
        lastFrameNanos = now;

        StatsChanges hovered = config.showRestorePreview() ? resolveHovered() : null;

        HpStatus hpStatus = debug ? config.debugPoisonState() : HpStatus.of(client.getVarpValue(VarPlayerID.POISON));

        int cx = centreX();
        int cy = centreY();
        refreshGeometryCache(cx, cy);

        Map<String, Rectangle> frameBounds = new HashMap<>();

        accum = null;
        drawSide(g, states, Side.LEFT, true, anyLow, cx, cy, hovered, hpStatus, dtMillis);
        drawSide(g, states, Side.RIGHT, false, anyLow, cx, cy, hovered, hpStatus, dtMillis);

        // Forget vitals not drawn this frame so they snap (not sweep) when next shown.
        displayed.keySet().retainAll(states.keySet());

        if (config.showPrayerIcons()) {
            drawPrayerIcons(g, cx, cy);
        }
        if (accum != null) {
            frameBounds.put("main", accum);
        }

        List<DragTarget> targets = new ArrayList<>();
        Rectangle main = frameBounds.get("main");
        if (main != null) {
            targets.add(new DragTarget("main", main, config.offsetX(), config.offsetY(),
                MAIN_OFFSET_MIN, MAIN_OFFSET_MAX, "offsetX", "offsetY"));
        }
        dragController.setTargets(targets);

        String outlined = dragController.outlinedId();
        if (outlined != null) {
            Rectangle b = frameBounds.get(outlined);
            if (b != null) {
                drawDragOutline(g, b);
            }
        }
        return null;
    }

    // Draws a centred row of icons for the currently-active prayers at the bottom of the HUD. The row
    // anchors to the arc bottom (cy + size/2) plus the offset slider, draws at the resting base opacity,
    // and reserves a slot per active prayer so positions stay put while sprites stream in. Sprites load
    // asynchronously; a slot whose sprite has not arrived yet is skipped and fills in on a later frame.
    private void drawPrayerIcons(Graphics2D g, int cx, int cy) {
        java.util.List<Integer> spriteIds = PrayerIcon.activeSpriteIds(client);
        if (spriteIds.isEmpty()) {
            return;
        }
        int iconSize = config.prayerIconSize();
        int anchorY = cy + config.size() / 2 + config.prayerIconOffset();
        PrayerIconLayout layout = PrayerIconLayout.of(spriteIds.size(), iconSize,
            config.prayerIconSpacing(), cx, anchorY, ICON_BG_PAD);
        addBounds(new Rectangle(layout.backgroundX(), layout.backgroundY(),
            layout.backgroundWidth(), layout.backgroundHeight()));

        Composite oldComposite = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, clamp01(config.baseOpacity() / 100f)));

        if (config.prayerIconBackground()) {
            g.setColor(config.prayerIconBackgroundColor());
            g.fillRoundRect(layout.backgroundX(), layout.backgroundY(),
                layout.backgroundWidth(), layout.backgroundHeight(), 6, 6);
        }

        for (int i = 0; i < layout.count(); i++) {
            int id = spriteIds.get(i);
            BufferedImage img = spriteCache.get(id);
            if (img != null) {
                g.drawImage(img, layout.iconX(i), layout.y(), iconSize, iconSize, null);
            } else if (!spriteCache.containsKey(id)) {
                spriteCache.put(id, null);
                spriteManager.getSpriteAsync(id, 0, sprite -> spriteCache.put(id, sprite));
            }
        }

        g.setComposite(oldComposite);
    }

    private void drawSide(Graphics2D g, EnumMap<Vital, BarState> states, Side side, boolean leftSide,
                          boolean anyLow, int cx, int cy, StatsChanges hovered, HpStatus hpStatus, long dtMillis) {
        int index = 0;
        for (Vital v : VITALS) {
            BarState s = states.get(v);
            if (s == null || v.side(config) != side) {
                continue;
            }
            int gap = BarLayout.gapForIndex(config.gap(), config.thickness(), config.barSpacing(), index);
            drawVital(g, v, s, leftSide, anyLow, gap, cx, cy, index, hovered, hpStatus, dtMillis);
            index++;
        }
    }

    private void drawVital(Graphics2D g, Vital v, BarState self, boolean leftSide, boolean anyLow,
                           int gap, int cx, int cy, int index, StatsChanges hovered, HpStatus hpStatus,
                           long dtMillis) {
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
        addBounds(geo.body().getBounds());
        Paint basePaint = patternPaints.resolve(v.pattern(config), fill);
        double shown = animatedFraction(v, self.fraction, dtMillis);
        BarRenderer.draw(g, geo, v.fillStyle(config), config.fillDirection(), shown, basePaint, fill,
            config.segments(), config.trackColor(), outline, config.outlineWidth(), previewFraction, previewColor);

        String txt = ValueText.format(current, max, config.valueDisplay());
        if (!txt.isEmpty()) {
            g.setFont(FontManager.getRunescapeSmallFont());
            FontMetrics fm = g.getFontMetrics();
            int[] anchor = BarLayout.labelAnchor(shape, cx, cy, config.size(), gap, config.thickness(),
                index, fm.getHeight(), leftSide);
            int tx = anchor[0] - fm.stringWidth(txt) / 2;
            int ty = anchor[1];
            g.setColor(Color.BLACK);
            g.drawString(txt, tx + 1, ty + 1);
            g.setColor(fill);
            g.drawString(txt, tx, ty);
        }

        g.setComposite(oldComposite);
    }

    // Eases the drawn fill fraction toward the real one. First sighting of a vital (or animation off)
    // snaps; otherwise a drop uses drainGlideMs and a gain uses restoreGlideMs. Stores the result so
    // the next frame continues from it.
    private double animatedFraction(Vital v, double target, long dtMillis) {
        Double prev = displayed.get(v);
        double shown;
        if (prev == null || !config.smoothMotion()) {
            shown = target;
        } else {
            int glide = target < prev ? config.drainGlideMs() : config.restoreGlideMs();
            shown = BarAnimator.step(prev, target, dtMillis, glide);
        }
        displayed.put(v, shown);
        return shown;
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
        int offset = dragController.isDragging("main") ? dragController.liveOffsetX() : config.offsetX();
        return vpX + vpW / 2 + offset;
    }

    private int centreY() {
        int vpH = client.getViewportHeight();
        int vpY = client.getViewportYOffset();
        if (vpH <= 0) {
            vpH = client.getCanvasHeight();
            vpY = 0;
        }
        int offset = dragController.isDragging("main") ? dragController.liveOffsetY() : config.offsetY();
        return vpY + vpH / 2 + offset;
    }

    // Grows the running accumulator by one drawn element. Rectangle.union returns a fresh rectangle,
    // so a published snapshot is never mutated after the fact.
    private void addBounds(Rectangle r) {
        accum = (accum == null) ? r : accum.union(r);
    }

    // Faint outline plus a small move handle around the HUD, shown while Alt is held over it or while
    // it is being dragged. Traces the grabbable region (the published bounds grown by GRAB_PAD) and
    // draws at a fixed alpha so it stays visible regardless of the HUD's resting opacity.
    private void drawDragOutline(Graphics2D g, Rectangle b) {
        int pad = HudDragController.GRAB_PAD;
        int x = b.x - pad;
        int y = b.y - pad;
        int w = b.width + 2 * pad;
        int h = b.height + 2 * pad;
        Composite old = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.55f));
        g.setColor(DRAG_OUTLINE_COLOR);
        g.drawRoundRect(x, y, w, h, 8, 8);
        int hx = x + w / 2;
        int hy = y + 6;
        g.drawLine(hx - 4, hy, hx + 4, hy);
        g.drawLine(hx, hy - 4, hx, hy + 4);
        g.setComposite(old);
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
