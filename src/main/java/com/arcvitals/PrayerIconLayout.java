package com.arcvitals;

// Pure geometry for the active-prayer icon row: `count` square icons of `iconSize` px, `spacing` px
// apart, centred on cx with the row's top edge at anchorY. Also yields the padded background-chip rect.
// No drawing and no Client, so it is unit-tested in isolation like BarLayout. count <= 0 is a valid
// empty layout: no icon positions and a zero-area background at the anchor.
final class PrayerIconLayout {

    private final int[] iconXs;
    private final int y;
    private final int iconSize;
    private final int bgX;
    private final int bgY;
    private final int bgW;
    private final int bgH;

    private PrayerIconLayout(int[] iconXs, int y, int iconSize, int bgX, int bgY, int bgW, int bgH) {
        this.iconXs = iconXs;
        this.y = y;
        this.iconSize = iconSize;
        this.bgX = bgX;
        this.bgY = bgY;
        this.bgW = bgW;
        this.bgH = bgH;
    }

    static PrayerIconLayout of(int count, int iconSize, int spacing, int cx, int anchorY, int pad) {
        if (count <= 0) {
            return new PrayerIconLayout(new int[0], anchorY, iconSize, cx, anchorY, 0, 0);
        }
        int totalWidth = count * iconSize + (count - 1) * spacing;
        int startX = cx - totalWidth / 2;
        int[] xs = new int[count];
        for (int i = 0; i < count; i++) {
            xs[i] = startX + i * (iconSize + spacing);
        }
        int bgX = startX - pad;
        int bgY = anchorY - pad;
        int bgW = totalWidth + pad * 2;
        int bgH = iconSize + pad * 2;
        return new PrayerIconLayout(xs, anchorY, iconSize, bgX, bgY, bgW, bgH);
    }

    int count() {
        return iconXs.length;
    }

    int iconX(int i) {
        return iconXs[i];
    }

    int y() {
        return y;
    }

    int iconSize() {
        return iconSize;
    }

    int backgroundX() {
        return bgX;
    }

    int backgroundY() {
        return bgY;
    }

    int backgroundWidth() {
        return bgW;
    }

    int backgroundHeight() {
        return bgH;
    }
}
