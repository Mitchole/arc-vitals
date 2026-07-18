package com.arcvitals;

import java.awt.Rectangle;
import java.util.Collections;
import java.util.List;
import javax.inject.Singleton;

// Shared state between the render thread (which publishes the draggable HUD units each frame and
// reads the live drag offset) and the input thread (which runs the Alt + left-drag). Cross-thread
// fields are volatile; the published target list is a fresh snapshot each frame and is never mutated
// after publication, and the grab anchors are touched only on the input thread during a drag, so no
// locking is needed. Pure logic - no Graphics, no Client.
//
// Generalises the single-target drag: the main group and every detached bar are DragTargets. One
// target is dragged at a time (activeId); hit testing runs topmost-first so an overlapping detached
// bar wins over the group.
@Singleton
public class HudDragController {

    // Extra pixels around a target's drawn bounds that still count as a grab, and that the outline traces.
    static final int GRAB_PAD = 6;

    private volatile List<DragTarget> targets = Collections.emptyList();
    private volatile boolean dragging;
    private volatile String armedId;
    private volatile String activeId;
    private volatile int liveOffsetX;
    private volatile int liveOffsetY;

    // Grab anchors and the active target's write keys / clamp range: written and read only on the
    // input thread while a drag is in progress.
    private int grabMouseX;
    private int grabMouseY;
    private int grabOffsetX;
    private int grabOffsetY;
    private int activeMin;
    private int activeMax;
    private String activeKeyX;
    private String activeKeyY;

    public void setTargets(List<DragTarget> targets) {
        this.targets = targets;
    }

    // The topmost target whose padded bounds contain the point, or null. Iterates last-to-first so
    // the most recently published target (a detached bar, published after the main group) wins an overlap.
    private DragTarget hit(int px, int py) {
        List<DragTarget> ts = targets;
        for (int i = ts.size() - 1; i >= 0; i--) {
            DragTarget t = ts.get(i);
            Rectangle b = t.bounds();
            if (HudDragMath.hits(b.x, b.y, b.width, b.height, GRAB_PAD, px, py)) {
                return t;
            }
        }
        return null;
    }

    public boolean begin(int px, int py) {
        DragTarget t = hit(px, py);
        if (t == null) {
            return false;
        }
        activeId = t.id();
        activeKeyX = t.keyX();
        activeKeyY = t.keyY();
        activeMin = t.min();
        activeMax = t.max();
        grabMouseX = px;
        grabMouseY = py;
        grabOffsetX = t.offsetX();
        grabOffsetY = t.offsetY();
        liveOffsetX = t.offsetX();
        liveOffsetY = t.offsetY();
        dragging = true;
        return true;
    }

    public void update(int px, int py) {
        if (!dragging) {
            return;
        }
        liveOffsetX = HudDragMath.axisOffset(grabOffsetX, grabMouseX, px, activeMin, activeMax);
        liveOffsetY = HudDragMath.axisOffset(grabOffsetY, grabMouseY, py, activeMin, activeMax);
    }

    public void end() {
        dragging = false;
        activeId = null;
    }

    public void updateArmed(int px, int py) {
        DragTarget t = hit(px, py);
        armedId = (t == null) ? null : t.id();
    }

    public void clearArmed() {
        armedId = null;
    }

    public boolean isDragging() {
        return dragging;
    }

    public boolean isDragging(String id) {
        return dragging && id.equals(activeId);
    }

    public int liveOffsetX() {
        return liveOffsetX;
    }

    public int liveOffsetY() {
        return liveOffsetY;
    }

    public String activeKeyX() {
        return activeKeyX;
    }

    public String activeKeyY() {
        return activeKeyY;
    }

    // The id of the target to outline this frame: the one being dragged, else the one the Alt-cursor
    // is over, else null.
    public String outlinedId() {
        return dragging ? activeId : armedId;
    }
}
