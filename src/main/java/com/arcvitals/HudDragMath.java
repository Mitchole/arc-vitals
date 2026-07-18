package com.arcvitals;

// Pure geometry for the Alt + drag HUD move: point-in-padded-rect hit testing and per-axis offset
// with clamping. No AWT drawing, no Client, no state.
final class HudDragMath {

    private HudDragMath() {
    }

    // True when (px, py) lies inside the rectangle grown by pad on every side. A zero-area rectangle
    // (nothing drawn this frame) never registers a hit.
    static boolean hits(int x, int y, int w, int h, int pad, int px, int py) {
        if (w <= 0 || h <= 0) {
            return false;
        }
        return px >= x - pad && px <= x + w + pad && py >= y - pad && py <= y + h + pad;
    }

    // New offset for one axis: the grab-start offset plus how far the cursor has moved since the
    // grab, clamped to [min, max].
    static int axisOffset(int startOffset, int startCoord, int curCoord, int min, int max) {
        int v = startOffset + (curCoord - startCoord);
        if (v < min) {
            return min;
        }
        return v > max ? max : v;
    }
}
