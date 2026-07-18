package com.arcvitals;

import java.awt.Rectangle;
import javax.inject.Singleton;

// Shared state between the render thread (which publishes the HUD's on-screen bounds and reads the
// live drag offset) and the input thread (which runs the Alt + left-drag). The cross-thread fields
// are volatile; the published Rectangle is a fresh snapshot each frame and is never mutated after
// publication, so no locking is needed. Pure logic - no Graphics, no Client.
@Singleton
public class HudDragController {

    // Extra pixels around the drawn HUD that still count as a grab, and that the outline traces.
    static final int GRAB_PAD = 6;

    private volatile Rectangle bounds;
    private volatile boolean dragging;
    private volatile boolean armed;
    private volatile int liveOffsetX;
    private volatile int liveOffsetY;

    // Grab anchors: written and read only on the input thread while a drag is in progress.
    private int grabMouseX;
    private int grabMouseY;
    private int grabOffsetX;
    private int grabOffsetY;

    public void setBounds(Rectangle r) {
        this.bounds = r;
    }

    public boolean hitsBounds(int px, int py) {
        Rectangle b = bounds;
        if (b == null) {
            return false;
        }
        return HudDragMath.hits(b.x, b.y, b.width, b.height, GRAB_PAD, px, py);
    }

    public boolean begin(int px, int py, int curOffX, int curOffY, int min, int max) {
        if (!hitsBounds(px, py)) {
            return false;
        }
        grabMouseX = px;
        grabMouseY = py;
        grabOffsetX = curOffX;
        grabOffsetY = curOffY;
        liveOffsetX = curOffX;
        liveOffsetY = curOffY;
        dragging = true;
        return true;
    }

    public void update(int px, int py, int min, int max) {
        if (!dragging) {
            return;
        }
        liveOffsetX = HudDragMath.axisOffset(grabOffsetX, grabMouseX, px, min, max);
        liveOffsetY = HudDragMath.axisOffset(grabOffsetY, grabMouseY, py, min, max);
    }

    public int[] end() {
        if (!dragging) {
            return null;
        }
        dragging = false;
        return new int[] { liveOffsetX, liveOffsetY };
    }

    public void setArmed(boolean a) {
        this.armed = a;
    }

    public boolean isDragging() {
        return dragging;
    }

    public int liveOffsetX() {
        return liveOffsetX;
    }

    public int liveOffsetY() {
        return liveOffsetY;
    }

    public boolean showOutline() {
        return armed || dragging;
    }
}
