package com.arcvitals;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import net.runelite.api.Actor;
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
import net.runelite.client.util.Text;

public class ArcVitalsOverlay extends Overlay {

    private final Client client;
    private final ArcVitalsConfig config;
    private final ItemStatChangesService itemStatService;
    private final CombatTracker combatTracker;
    private final SpriteManager spriteManager;
    private final HudDragController dragController;
    private final TargetTracker targetTracker;
    private final SwingTracker swingTracker;

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

    // The active prayer sprite ids, recomputed only when the game tick advances: prayer state is
    // tick-driven, but render runs every frame, so scanning every prayer per frame is wasted work.
    // clearAndReturn resets the tick so the next drawn frame recomputes rather than trusting a stale list.
    private List<Integer> prayerSpriteIds = Collections.emptyList();
    private int prayerSpriteTick = Integer.MIN_VALUE;

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

    // Cadence of the free-running swing-timer preview in debug (no real weapon speed to size it from).
    // Drives both the loop fill and the tick pips so the preview stays self-consistent.
    private static final int SWING_DEBUG_TICKS = 4;

    // Mirror the @Range on offsetX/offsetY in ArcVitalsConfig; the annotation values cannot be read
    // at runtime without reflection (banned in src/main).
    private static final int MAIN_OFFSET_MIN = -500;
    private static final int MAIN_OFFSET_MAX = 500;

    // Detached bars reach further than the main HUD so they can sit at a screen edge or corner.
    // Mirror the @Range on the per-bar detachX/detachY config items.
    private static final int DETACH_OFFSET_MIN = -2000;
    private static final int DETACH_OFFSET_MAX = 2000;

    // Running union of the geometry drawn for the current unit, in canvas pixels. Reset to null
    // before a unit is drawn, grown by addBounds as each bar and the prayer-icon row draw, then read
    // off into the per-frame bounds map.
    private Rectangle accum;

    // Eased displayed fill for the target bar (null = snap on next draw), plus the actor it was last
    // drawn for so a target switch snaps rather than glides.
    private Double displayedTargetFraction;
    private Actor lastTargetActor;

    // Eased displayed fill for the swing timer (null = snap on next draw).
    private Double displayedSwingFraction;

    @Inject
    ArcVitalsOverlay(Client client, ArcVitalsConfig config, ItemStatChangesService itemStatService,
                     CombatTracker combatTracker, SpriteManager spriteManager, HudDragController dragController,
                     TargetTracker targetTracker, SwingTracker swingTracker) {
        this.client = client;
        this.config = config;
        this.itemStatService = itemStatService;
        this.combatTracker = combatTracker;
        this.spriteManager = spriteManager;
        this.dragController = dragController;
        this.targetTracker = targetTracker;
        this.swingTracker = swingTracker;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.UNDER_WIDGETS);
    }

    @Override
    public Dimension render(Graphics2D g) {
        if (client.getGameState() != GameState.LOGGED_IN || client.getLocalPlayer() == null) {
            return clearAndReturn();
        }
        boolean debug = config.debugEnabled();
        Visibility visibility = debug ? Visibility.FULL : resolveVisibility();
        if (visibility == Visibility.HIDDEN) {
            return clearAndReturn();
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
            int current;
            if (debug) {
                int pct = config.debugAnimate()
                    ? DebugAnimate.percent(System.nanoTime() / 1_000_000L, v.ordinal())
                    : v.debugPercent(config);
                current = Math.round(max * pct / 100f);
            } else {
                current = v.current(client);
            }
            BarState s = BarState.of(current, max, v.threshold(config));
            states.put(v, s);
            if (s.low) {
                anyLow = true;
            }
        }
        Actor targetActor = config.targetBarEnabled() ? targetTracker.current() : null;
        // Debug preview: a synthetic target bar when the feature is on but there is no real target to
        // show one for. Still gated on targetBarEnabled so the toggle stays authoritative in debug too.
        boolean debugTarget = debug && config.targetBarEnabled() && targetActor == null;
        boolean targetBarShowing = debugTarget || (targetActor != null && targetActor.getName() != null
            && TargetHealth.visible(targetActor.getHealthScale()));
        boolean swingShowing = config.swingEnabled() && (swingTracker.showing(client.getTickCount()) || debug);

        if (states.isEmpty() && !targetBarShowing && !swingShowing) {
            return clearAndReturn();
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
        int leftCount = drawSide(g, states, Side.LEFT, true, anyLow, cx, cy, hovered, hpStatus, dtMillis);
        int rightCount = drawSide(g, states, Side.RIGHT, false, anyLow, cx, cy, hovered, hpStatus, dtMillis);

        // Forget vitals not drawn this frame so they snap (not sweep) when next shown.
        displayed.keySet().retainAll(states.keySet());

        if (config.showPrayerIcons()) {
            drawPrayerIcons(g, cx, cy, debug);
        }
        if (swingShowing && config.swingPlacement() == SwingPlacement.NESTED) {
            boolean leftSide = config.swingSide() == Side.LEFT;
            int index = leftSide ? leftCount : rightCount;
            drawSwingNested(g, cx, cy, leftSide, index, dtMillis, debug);
        }
        if (accum != null) {
            frameBounds.put("main", accum);
        }

        drawDetachedBars(g, anyLow, hovered, hpStatus, dtMillis, frameBounds);
        drawTargetBar(g, targetActor, debugTarget, dtMillis, frameBounds);
        if (swingShowing) {
            drawSwingTimer(g, dtMillis, frameBounds, debug);
        }

        List<DragTarget> targets = new ArrayList<>();
        Rectangle main = frameBounds.get("main");
        if (main != null) {
            targets.add(new DragTarget("main", main, config.offsetX(), config.offsetY(),
                MAIN_OFFSET_MIN, MAIN_OFFSET_MAX, "offsetX", "offsetY"));
        }
        for (Vital v : VITALS) {
            if (!v.detached(config)) {
                continue;
            }
            Rectangle b = frameBounds.get(v.name());
            if (b == null) {
                continue;
            }
            targets.add(new DragTarget(v.name(), b, v.detachX(config), v.detachY(config),
                DETACH_OFFSET_MIN, DETACH_OFFSET_MAX, v.detachKeyX(), v.detachKeyY()));
        }
        Rectangle tb = frameBounds.get("target");
        if (tb != null) {
            targets.add(new DragTarget("target", tb, config.targetBarOffsetX(), config.targetBarOffsetY(),
                DETACH_OFFSET_MIN, DETACH_OFFSET_MAX, "targetBarOffsetX", "targetBarOffsetY"));
        }
        Rectangle sw = frameBounds.get("swing");
        if (sw != null && config.swingPlacement() != SwingPlacement.NESTED) {
            targets.add(new DragTarget("swing", sw, config.swingOffsetX(), config.swingOffsetY(),
                DETACH_OFFSET_MIN, DETACH_OFFSET_MAX, "swingOffsetX", "swingOffsetY"));
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

    // Draws nothing this frame: drops the published drag targets and clears the eased fill state so a
    // bar that reappears later snaps to its real value instead of sweeping from a stale one. Returns
    // null for render() to hand straight back.
    private Dimension clearAndReturn() {
        dragController.setTargets(Collections.emptyList());
        displayed.clear();
        displayedTargetFraction = null;
        displayedSwingFraction = null;
        lastFrameNanos = 0L;
        prayerSpriteTick = Integer.MIN_VALUE;
        return null;
    }

    // Draws a centred row of icons for the currently-active prayers at the bottom of the HUD. The row
    // anchors to the arc bottom (cy + size/2) plus the offset slider, draws at the resting base opacity,
    // and reserves a slot per active prayer so positions stay put while sprites stream in. Sprites load
    // asynchronously; a slot whose sprite has not arrived yet is skipped and fills in on a later frame.
    private void drawPrayerIcons(Graphics2D g, int cx, int cy, boolean debug) {
        List<Integer> spriteIds;
        if (debug) {
            // Debug bypasses the tick cache: the preview count can change (slider drag) without a
            // game tick advancing, so a cached list would go stale mid-adjustment.
            spriteIds = PrayerIcon.firstSpriteIds(config.debugPrayerIcons());
        } else {
            int tick = client.getTickCount();
            if (tick != prayerSpriteTick) {
                prayerSpriteIds = PrayerIcon.activeSpriteIds(client);
                prayerSpriteTick = tick;
            }
            spriteIds = prayerSpriteIds;
        }
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

    // Returns the number of bars drawn on this side, so a NESTED swing timer can pick up at the next index.
    private int drawSide(Graphics2D g, EnumMap<Vital, BarState> states, Side side, boolean leftSide,
                         boolean anyLow, int cx, int cy, StatsChanges hovered, HpStatus hpStatus, long dtMillis) {
        int index = 0;
        for (Vital v : VITALS) {
            BarState s = states.get(v);
            if (s == null || v.side(config) != side || v.detached(config)) {
                continue;
            }
            int gap = BarLayout.gapForIndex(config.gap(), config.thickness(), config.barSpacing(), index);
            drawVital(g, v, s, leftSide, anyLow, gap, cx, cy, index, hovered, hpStatus, dtMillis, true);
            index++;
        }
        return index;
    }

    // Draws each detached, visible vital as a lone bar at index 0 around its own centre, recording its
    // bounds under the vital's name so it can be published as its own drag target.
    private void drawDetachedBars(Graphics2D g, boolean anyLow, StatsChanges hovered, HpStatus hpStatus,
                                  long dtMillis, Map<String, Rectangle> frameBounds) {
        for (Vital v : VITALS) {
            BarState s = states.get(v);
            if (s == null || !v.detached(config)) {
                continue;
            }
            int dcx = detachCentreX(v);
            int dcy = detachCentreY(v);
            int gap = BarLayout.gapForIndex(config.gap(), config.thickness(), config.barSpacing(), 0);
            boolean leftSide = v.side(config) == Side.LEFT;
            accum = null;
            drawVital(g, v, s, leftSide, anyLow, gap, dcx, dcy, 0, hovered, hpStatus, dtMillis, false);
            if (accum != null) {
                frameBounds.put(v.name(), accum);
            }
        }
    }

    // Draws the current combat target's HP as a lone bar at its own centre (uncached, index 0),
    // recording its bounds under "target" so it becomes an Alt-draggable DragTarget. Reuses the shared
    // geometry, pattern, fill, and label primitives; it has no alert/warn/poison/restore behaviour and
    // draws at the resting base opacity. Bails (and snaps next time) when there is no valid target and
    // no debug preview to fall back to. debugTarget draws a synthetic bar from debugTargetPercent when
    // Show target bar is on but there is no real target; it has no actor to track, so it skips the
    // actor reads and is treated as stable (no snap-on-actor-change).
    private void drawTargetBar(Graphics2D g, Actor targetActor, boolean debugTarget, long dtMillis,
                               Map<String, Rectangle> frameBounds) {
        int scale = targetActor == null ? 0 : targetActor.getHealthScale();
        String rawName = targetActor == null ? null : targetActor.getName();
        boolean realTarget = targetActor != null && rawName != null && TargetHealth.visible(scale);
        if (!realTarget && !debugTarget) {
            lastTargetActor = null;
            displayedTargetFraction = null;
            return;
        }

        double fraction;
        int percent;
        String name;
        if (realTarget) {
            int ratio = targetActor.getHealthRatio();
            fraction = TargetHealth.fraction(ratio, scale);
            percent = TargetHealth.percent(ratio, scale);
            name = Text.removeTags(rawName);

            // Snap (do not glide) when the target changes.
            if (targetActor != lastTargetActor) {
                displayedTargetFraction = null;
            }
            lastTargetActor = targetActor;
        } else {
            fraction = config.debugTargetPercent() / 100.0;
            percent = config.debugTargetPercent();
            name = "Target";
        }

        int tcx = targetCentreX();
        int tcy = targetCentreY();
        boolean leftSide = config.targetBarSide() == Side.LEFT;
        BarShape shape = config.targetBarShapeOverride().resolve(config.barShape());
        Color fill = config.targetBarColor();
        Color outline = config.showOutline() ? config.outlineColor() : null;

        Composite oldComposite = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, clamp01(config.baseOpacity() / 100f)));

        accum = null;
        Geometry geo = shape.build(tcx, tcy, config.size(), config.thickness(), config.gap(), config.barSpacing(),
            config.curve(), 0, leftSide, config.flatEnds());
        addBounds(geo.body().getBounds());
        Paint basePaint = patternPaints.resolve(config.barPattern(), fill);
        double shown = animatedTargetFraction(fraction, dtMillis);
        BarRenderer.draw(g, geo, config.fillStyle(), config.fillDirection(), shown, basePaint, fill,
            config.segments(), config.trackColor(), outline, config.outlineWidth(), 0.0, null, 0.0, null, null);

        int gap = BarLayout.gapForIndex(config.gap(), config.thickness(), config.barSpacing(), 0);
        drawLabel(g, config.targetBarLabel().format(name, percent), shape, tcx, tcy, gap, 0, leftSide, fill);

        g.setComposite(oldComposite);

        if (accum != null) {
            frameBounds.put("target", accum);
        }
    }

    // Centre for the target bar: viewport centre plus its own offset (or the live drag offset while it
    // is the bar being dragged).
    private int targetCentreX() {
        int offset = dragController.isDragging("target") ? dragController.liveOffsetX() : config.targetBarOffsetX();
        return viewportCentreX() + offset;
    }

    private int targetCentreY() {
        int offset = dragController.isDragging("target") ? dragController.liveOffsetY() : config.targetBarOffsetY();
        return viewportCentreY() + offset;
    }

    // Eases the target bar's displayed fill toward the real fraction, mirroring the vitals' animation:
    // first sight or animation-off snaps; a drop uses drainGlideMs, a gain uses restoreGlideMs.
    private double animatedTargetFraction(double target, long dtMillis) {
        Double prev = displayedTargetFraction;
        double shown;
        if (prev == null || !config.smoothMotion()) {
            shown = target;
        } else {
            int glide = target < prev ? config.drainGlideMs() : config.restoreGlideMs();
            shown = BarAnimator.step(prev, target, dtMillis, glide);
        }
        displayedTargetFraction = shown;
        return shown;
    }

    private int swingCentreX() {
        int offset = dragController.isDragging("swing") ? dragController.liveOffsetX() : config.swingOffsetX();
        return viewportCentreX() + offset;
    }

    private int swingCentreY() {
        int offset = dragController.isDragging("swing") ? dragController.liveOffsetY() : config.swingOffsetY();
        return viewportCentreY() + offset;
    }

    // Draws the attack-cooldown timer as a lone horizontal arc at its own centre (TOP/BOTTOM), or lets
    // drawSwingNested handle the NESTED case. The fill eases toward the tracker's fraction; pips mark
    // each tick; a soft glow shows when ready. Records bounds under "swing" for the Alt-drag.
    private void drawSwingTimer(Graphics2D g, long dtMillis, Map<String, Rectangle> frameBounds, boolean debug) {
        SwingPlacement placement = config.swingPlacement();
        if (placement == SwingPlacement.NESTED) {
            return; // handled inside the main-group flow (Task 9)
        }
        int scx = swingCentreX();
        int scy = swingCentreY();
        Orientation orientation = placement == SwingPlacement.TOP ? Orientation.TOP : Orientation.BOTTOM;

        Composite oldComposite = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, clamp01(config.baseOpacity() / 100f)));

        accum = null;
        Geometry geo = new ArcGeometry(scx, scy, config.size(), config.thickness(), config.gap(),
            config.barSpacing(), config.curve(), 0, orientation, config.flatEnds());
        addBounds(geo.body().getBounds());
        drawSwingArc(g, geo, dtMillis, debug);

        g.setComposite(oldComposite);
        if (accum != null) {
            frameBounds.put("swing", accum);
        }
    }

    // Draws the swing timer as an extra bar nested after the vitals on its side, sharing the main
    // centre and the "main" drag (no separate DragTarget). Left/right orientation, its own index.
    private void drawSwingNested(Graphics2D g, int cx, int cy, boolean leftSide, int index, long dtMillis,
                                 boolean debug) {
        Composite oldComposite = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, clamp01(config.baseOpacity() / 100f)));

        Orientation orientation = leftSide ? Orientation.LEFT : Orientation.RIGHT;
        Geometry geo = new ArcGeometry(cx, cy, config.size(), config.thickness(), config.gap(),
            config.barSpacing(), config.curve(), index, orientation, config.flatEnds());
        addBounds(geo.body().getBounds()); // folds into the "main" bounds accumulator for this frame
        drawSwingArc(g, geo, dtMillis, debug);

        g.setComposite(oldComposite);
    }

    // Eases the swing fill toward the real fraction; snaps on first sight or when smooth motion is off.
    private double animatedSwingFraction(double target, long dtMillis) {
        Double prev = displayedSwingFraction;
        double shown;
        if (prev == null || !config.smoothMotion()) {
            shown = target;
        } else {
            int glide = target < prev ? config.drainGlideMs() : config.restoreGlideMs();
            shown = BarAnimator.step(prev, target, dtMillis, glide);
        }
        displayedSwingFraction = shown;
        return shown;
    }

    // Draws the fill, per-tick pips, the bright leading edge at the current fill front, and the ready
    // glow. Callers own the composite, centre, and bounds; this only paints into the given geometry.
    // In debug (no real swing to preview), the fill sources from a free-running 4-tick loop instead of
    // the tracker, so the timer is visible without combat.
    private void drawSwingArc(Graphics2D g, Geometry geo, long dtMillis, boolean debug) {
        long now = System.nanoTime();
        double target;
        boolean ready;
        if (debug) {
            long period = SWING_DEBUG_TICKS * 600L;
            long loopMs = (now / 1_000_000L) % period;
            target = SwingState.fraction(loopMs, SWING_DEBUG_TICKS);
            ready = loopMs >= period - 16L; // brief READY flash at the wrap
        } else {
            target = swingTracker.fraction(now);
            ready = swingTracker.ready(now);
        }
        double shown = animatedSwingFraction(target, dtMillis);

        Color fill = config.swingColor();
        Color outline = config.showOutline() ? config.outlineColor() : null;
        Paint basePaint = patternPaints.resolve(config.barPattern(), fill);
        BarRenderer.draw(g, geo, config.fillStyle(), FillDirection.BOTTOM_UP, shown, basePaint, fill,
            config.segments(), config.trackColor(), outline, config.outlineWidth(), 0.0, null, 0.0, null, null);

        if (config.showSwingTicks()) {
            drawSwingTicks(g, geo, debug ? SWING_DEBUG_TICKS : swingTracker.cooldownTicks());
        }
        if (!ready && shown > 0.01) {
            drawSwingLeadingEdge(g, geo, shown);
        }
        if (ready) {
            drawSwingReadyGlow(g, geo);
        }
    }

    // A bright marker across the bar at the current fill front, so "now" is unmistakable.
    private void drawSwingLeadingEdge(Graphics2D g, Geometry geo, double f) {
        double[] p = geo.pointAt(f);
        double[] n = geo.normalAt(f);
        int half = geo.thickness() / 2 + 1;
        java.awt.Stroke oldStroke = g.getStroke();
        Paint oldPaint = g.getPaint();
        g.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(new Color(255, 255, 255, 220));
        g.draw(new java.awt.geom.Line2D.Double(
            p[0] - n[0] * half, p[1] - n[1] * half,
            p[0] + n[0] * half, p[1] + n[1] * half));
        g.setStroke(oldStroke);
        g.setPaint(oldPaint);
    }

    // A notch across the bar at each tick boundary, reusing pointAt/normalAt like FillStyle.NOTCHED.
    private void drawSwingTicks(Graphics2D g, Geometry geo, int ticks) {
        if (ticks < 2) {
            return;
        }
        int half = geo.thickness() / 2 + 1;
        java.awt.Stroke oldStroke = g.getStroke();
        Paint oldPaint = g.getPaint();
        g.setColor(new Color(0, 0, 0, 160));
        g.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
        for (int i = 1; i < ticks; i++) {
            double f = (double) i / ticks;
            double[] p = geo.pointAt(f);
            double[] n = geo.normalAt(f);
            g.draw(new java.awt.geom.Line2D.Double(
                p[0] - n[0] * half, p[1] - n[1] * half,
                p[0] + n[0] * half, p[1] + n[1] * half));
        }
        g.setStroke(oldStroke);
        g.setPaint(oldPaint);
    }

    // A soft halo at the bar's belly when the next attack is available, tinted to match the swing colour.
    private void drawSwingReadyGlow(Graphics2D g, Geometry geo) {
        double[] belly = geo.pointAt(0.5);
        float r = config.size() * 0.4f;
        Color base = config.swingColor();
        Color inner = new Color(base.getRed(), base.getGreen(), base.getBlue(), 110);
        Color outer = new Color(base.getRed(), base.getGreen(), base.getBlue(), 0);
        RadialGradientPaint halo = new RadialGradientPaint(
            new java.awt.geom.Point2D.Double(belly[0], belly[1]), r,
            new float[]{0f, 1f},
            new Color[]{inner, outer});
        Paint old = g.getPaint();
        g.setPaint(halo);
        g.fill(new java.awt.geom.Ellipse2D.Double(belly[0] - r, belly[1] - r, r * 2, r * 2));
        g.setPaint(old);
    }

    private void drawVital(Graphics2D g, Vital v, BarState self, boolean leftSide, boolean anyLow,
                           int gap, int cx, int cy, int index, StatsChanges hovered, HpStatus hpStatus,
                           long dtMillis, boolean cached) {
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

        double overBand = config.overhealEnabled() ? OverhealState.overBand(current, max) : 0.0;
        Color overColor = overBand > 0 ? config.overhealColor() : null;
        Color overTick = (overBand > 0 && config.showOverhealTick())
            ? OverhealState.tickColor(config.overhealColor()) : null;

        Color outline = config.showOutline() ? config.outlineColor() : null;
        BarShape shape = v.shape(config);
        Geometry geo;
        if (cached) {
            long cacheKey = ((long) shape.ordinal() << 8) | ((long) index << 1) | (leftSide ? 1 : 0);
            geo = geometryCache.computeIfAbsent(cacheKey,
                k -> shape.build(cx, cy, config.size(), config.thickness(), config.gap(), config.barSpacing(),
                    config.curve(), index, leftSide, config.flatEnds()));
        } else {
            // Detached bars sit at their own centre; the single-centre cache cannot key them, so build
            // fresh each frame. They are few and lone, so this is cheap.
            geo = shape.build(cx, cy, config.size(), config.thickness(), config.gap(), config.barSpacing(),
                config.curve(), index, leftSide, config.flatEnds());
        }
        addBounds(geo.body().getBounds());
        Paint basePaint = patternPaints.resolve(v.pattern(config), fill);
        double shown = animatedFraction(v, self.fraction, dtMillis);
        BarRenderer.draw(g, geo, v.fillStyle(config), config.fillDirection(), shown, basePaint, fill,
            config.segments(), config.trackColor(), outline, config.outlineWidth(), previewFraction, previewColor,
            overBand, overColor, overTick);

        drawLabel(g, ValueText.format(current, max, config.valueDisplay()), shape, cx, cy, gap, index, leftSide, fill);

        g.setComposite(oldComposite);
    }

    // Draws a bar's value text centred on its label anchor, with a one-pixel black drop shadow.
    // A blank string draws nothing. Shared by the vitals, detached bars, and the target bar.
    private void drawLabel(Graphics2D g, String txt, BarShape shape, int cx, int cy, int gap,
                           int index, boolean leftSide, Color fill) {
        if (txt.isEmpty()) {
            return;
        }
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

    private int viewportCentreX() {
        int vpW = client.getViewportWidth();
        int vpX = client.getViewportXOffset();
        if (vpW <= 0) {
            vpW = client.getCanvasWidth();
            vpX = 0;
        }
        return vpX + vpW / 2;
    }

    private int viewportCentreY() {
        int vpH = client.getViewportHeight();
        int vpY = client.getViewportYOffset();
        if (vpH <= 0) {
            vpH = client.getCanvasHeight();
            vpY = 0;
        }
        return vpY + vpH / 2;
    }

    private int centreX() {
        int offset = dragController.isDragging("main") ? dragController.liveOffsetX() : config.offsetX();
        return viewportCentreX() + offset;
    }

    private int centreY() {
        int offset = dragController.isDragging("main") ? dragController.liveOffsetY() : config.offsetY();
        return viewportCentreY() + offset;
    }

    // Centre for a detached bar: viewport centre plus its own offset (or the live drag offset while
    // this bar is the one being dragged).
    private int detachCentreX(Vital v) {
        int offset = dragController.isDragging(v.name()) ? dragController.liveOffsetX() : v.detachX(config);
        return viewportCentreX() + offset;
    }

    private int detachCentreY(Vital v) {
        int offset = dragController.isDragging(v.name()) ? dragController.liveOffsetY() : v.detachY(config);
        return viewportCentreY() + offset;
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
