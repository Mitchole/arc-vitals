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
}
