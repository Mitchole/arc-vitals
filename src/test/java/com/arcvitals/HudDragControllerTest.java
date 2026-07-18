package com.arcvitals;

import java.awt.Rectangle;
import org.junit.Test;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class HudDragControllerTest {

    private HudDragController controller() {
        HudDragController c = new HudDragController();
        c.setBounds(new Rectangle(100, 100, 40, 60));
        return c;
    }

    @Test
    public void nullBoundsNeverHit() {
        HudDragController c = new HudDragController();
        c.setBounds(null);
        assertFalse(c.hitsBounds(110, 110));
    }

    @Test
    public void beginGrabsWhenThePointHitsTheBounds() {
        HudDragController c = controller();
        assertTrue(c.begin(110, 110, 0, 0, -500, 500));
        assertTrue(c.isDragging());
    }

    @Test
    public void beginIgnoresAPointOutsideTheBounds() {
        HudDragController c = controller();
        assertFalse(c.begin(1000, 1000, 0, 0, -500, 500));
        assertFalse(c.isDragging());
    }

    @Test
    public void updateMovesTheLiveOffsetByTheDrag() {
        HudDragController c = controller();
        c.begin(110, 110, 20, 0, -500, 500);
        c.update(140, 130, -500, 500); // +30 x, +20 y
        assertEquals(50, c.liveOffsetX());
        assertEquals(20, c.liveOffsetY());
    }

    @Test
    public void endReturnsTheFinalOffsetAndStopsDragging() {
        HudDragController c = controller();
        c.begin(110, 110, 0, 0, -500, 500);
        c.update(160, 110, -500, 500); // +50 x
        int[] fin = c.end();
        assertArrayEquals(new int[] { 50, 0 }, fin);
        assertFalse(c.isDragging());
    }

    @Test
    public void endReturnsNullWhenNotDragging() {
        assertNull(controller().end());
    }

    @Test
    public void showOutlineWhenArmed() {
        HudDragController c = controller();
        assertFalse(c.showOutline());
        c.setArmed(true);
        assertTrue(c.showOutline());
    }

    @Test
    public void showOutlineWhileDragging() {
        HudDragController c = controller();
        c.begin(110, 110, 0, 0, -500, 500);
        assertTrue(c.showOutline());
    }
}
