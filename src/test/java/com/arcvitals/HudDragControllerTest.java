package com.arcvitals;

import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class HudDragControllerTest {

    private static DragTarget target(String id, int x, int y, int w, int h, int offX, int offY,
                                     int min, int max, String keyX, String keyY) {
        return new DragTarget(id, new Rectangle(x, y, w, h), offX, offY, min, max, keyX, keyY);
    }

    private HudDragController withMain() {
        HudDragController c = new HudDragController();
        c.setTargets(Collections.singletonList(
            target("main", 100, 100, 40, 60, 0, 0, -500, 500, "offsetX", "offsetY")));
        return c;
    }

    @Test
    public void beginGrabsWhenThePointHitsATarget() {
        HudDragController c = withMain();
        assertTrue(c.begin(110, 110));
        assertTrue(c.isDragging());
        assertTrue(c.isDragging("main"));
    }

    @Test
    public void beginIgnoresAPointOutsideEveryTarget() {
        HudDragController c = withMain();
        assertFalse(c.begin(1000, 1000));
        assertFalse(c.isDragging());
    }

    @Test
    public void beginWithNoTargetsReturnsFalse() {
        HudDragController c = new HudDragController();
        assertFalse(c.begin(110, 110));
        assertFalse(c.isDragging());
    }

    @Test
    public void updateMovesTheLiveOffsetByTheDrag() {
        HudDragController c = new HudDragController();
        c.setTargets(Collections.singletonList(
            target("main", 100, 100, 40, 60, 20, 0, -500, 500, "offsetX", "offsetY")));
        c.begin(110, 110);
        c.update(140, 130); // +30 x, +20 y
        assertEquals(50, c.liveOffsetX());
        assertEquals(20, c.liveOffsetY());
    }

    @Test
    public void topmostTargetWinsAnOverlap() {
        HudDragController c = new HudDragController();
        // "main" first, detached "RUN_ENERGY" published after and overlapping the same point.
        c.setTargets(Arrays.asList(
            target("main", 100, 100, 80, 80, 0, 0, -500, 500, "offsetX", "offsetY"),
            target("RUN_ENERGY", 120, 120, 40, 40, 0, 0, -2000, 2000, "runDetachX", "runDetachY")));
        assertTrue(c.begin(130, 130)); // inside both
        assertTrue(c.isDragging("RUN_ENERGY"));
        assertFalse(c.isDragging("main"));
    }

    @Test
    public void beginSelectsTheHitTargetsKeysAndClampRange() {
        HudDragController c = new HudDragController();
        c.setTargets(Collections.singletonList(
            target("RUN_ENERGY", 100, 100, 40, 40, 1500, 0, -2000, 2000, "runDetachX", "runDetachY")));
        c.begin(120, 120);
        c.update(1000, 120); // +880 from grab -> 2380, clamped to 2000 (detached range, not 500)
        assertEquals(2000, c.liveOffsetX());
        assertEquals("runDetachX", c.activeKeyX());
        assertEquals("runDetachY", c.activeKeyY());
    }

    @Test
    public void updateIsANoOpWhenNotDragging() {
        HudDragController c = withMain();
        c.update(200, 200);
        assertEquals(0, c.liveOffsetX());
        assertEquals(0, c.liveOffsetY());
    }

    @Test
    public void endClearsDraggingAndActiveId() {
        HudDragController c = withMain();
        c.begin(110, 110);
        c.end();
        assertFalse(c.isDragging());
        assertFalse(c.isDragging("main"));
    }

    @Test
    public void outlinedIdIsTheArmedTargetWhenNotDragging() {
        HudDragController c = withMain();
        assertNull(c.outlinedId());
        c.updateArmed(110, 110);
        assertEquals("main", c.outlinedId());
        c.clearArmed();
        assertNull(c.outlinedId());
    }

    @Test
    public void armedClearsWhenTheCursorLeavesEveryTarget() {
        HudDragController c = withMain();
        c.updateArmed(110, 110);
        assertEquals("main", c.outlinedId());
        c.updateArmed(500, 500);
        assertNull(c.outlinedId());
    }

    @Test
    public void outlinedIdPrefersTheActiveTargetWhileDragging() {
        HudDragController c = new HudDragController();
        c.setTargets(Arrays.asList(
            target("main", 100, 100, 80, 80, 0, 0, -500, 500, "offsetX", "offsetY"),
            target("RUN_ENERGY", 300, 300, 40, 40, 0, 0, -2000, 2000, "runDetachX", "runDetachY")));
        c.updateArmed(110, 110);  // armed over main
        c.begin(320, 320);        // but drag RUN_ENERGY
        assertEquals("RUN_ENERGY", c.outlinedId());
    }
}
