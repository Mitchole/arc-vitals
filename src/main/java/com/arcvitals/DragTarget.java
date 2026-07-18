package com.arcvitals;

import java.awt.Rectangle;

// One draggable HUD unit for a single frame: the main group (id "main") or a detached bar (id =
// the vital's name). Immutable; the controller publishes a fresh list each frame and never mutates a
// target after publication, so it is safe to read from the input thread.
final class DragTarget {

    private final String id;
    private final Rectangle bounds;
    private final int offsetX;
    private final int offsetY;
    private final int min;
    private final int max;
    private final String keyX;
    private final String keyY;

    DragTarget(String id, Rectangle bounds, int offsetX, int offsetY, int min, int max,
               String keyX, String keyY) {
        this.id = id;
        this.bounds = bounds;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.min = min;
        this.max = max;
        this.keyX = keyX;
        this.keyY = keyY;
    }

    String id() {
        return id;
    }

    Rectangle bounds() {
        return bounds;
    }

    int offsetX() {
        return offsetX;
    }

    int offsetY() {
        return offsetY;
    }

    int min() {
        return min;
    }

    int max() {
        return max;
    }

    String keyX() {
        return keyX;
    }

    String keyY() {
        return keyY;
    }
}
