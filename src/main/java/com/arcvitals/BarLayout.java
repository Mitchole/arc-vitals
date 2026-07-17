package com.arcvitals;

final class BarLayout {

    private BarLayout() {
    }

    static int gapForIndex(int baseGap, int thickness, int spacing, int index) {
        return baseGap + index * (thickness + spacing);
    }

    static int labelBaselineY(int cy, int size, int fontHeight, int index) {
        return cy + size / 2 + fontHeight * (index + 1);
    }

    // The value-text anchor for one bar: {centerX, baselineY}. The overlay centres the text on
    // centerX and draws its baseline at baselineY. Non-ring shapes keep the historic placement
    // (belly column, stacked below the bar). Rings place the label just beyond each half-ring's
    // belly on its bulge side, staggered down by nesting index so concentric labels never collide.
    static int[] labelAnchor(BarShape shape, int cx, int cy, int size, int gap, int thickness,
                             int index, int fontHeight, boolean leftSide) {
        if (shape == BarShape.RING) {
            int pad = 12;
            int centerX = leftSide ? (cx - gap - thickness / 2 - pad) : (cx + gap + thickness / 2 + pad);
            int baselineY = cy + fontHeight / 3 + index * (fontHeight + 2);
            return new int[]{centerX, baselineY};
        }
        int centerX = leftSide ? (cx - gap) : (cx + gap);
        int baselineY = labelBaselineY(cy, size, fontHeight, index);
        return new int[]{centerX, baselineY};
    }
}
