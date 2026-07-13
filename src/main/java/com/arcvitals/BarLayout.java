package com.arcvitals;

final class BarLayout {

    private BarLayout() {
    }

    static int gapForIndex(int baseGap, int thickness, int spacing, int index) {
        return baseGap + index * (thickness + spacing);
    }
}
