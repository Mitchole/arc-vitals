package com.arcvitals;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class PrayerIconLayoutTest {

    @Test
    public void zeroCountIsEmpty() {
        PrayerIconLayout layout = PrayerIconLayout.of(0, 24, 2, 200, 300, 3);
        assertEquals(0, layout.count());
        assertEquals(0, layout.backgroundWidth());
        assertEquals(0, layout.backgroundHeight());
    }

    @Test
    public void oneIconIsCentredOnCx() {
        PrayerIconLayout layout = PrayerIconLayout.of(1, 24, 2, 200, 300, 3);
        assertEquals(1, layout.count());
        // icon left edge + half its width lands exactly on cx.
        assertEquals(200, layout.iconX(0) + layout.iconSize() / 2);
        assertEquals(300, layout.y());
    }

    @Test
    public void threeIconsAreSymmetricAndEvenlySpaced() {
        PrayerIconLayout layout = PrayerIconLayout.of(3, 24, 2, 200, 300, 3);
        // total width = 3*24 + 2*2 = 76; startX = 200 - 38 = 162.
        assertEquals(162, layout.iconX(0));
        assertEquals(188, layout.iconX(1)); // + (24 + 2)
        assertEquals(214, layout.iconX(2));
        // the row's midpoint is cx.
        assertEquals(200, (layout.iconX(0) + layout.iconX(2) + layout.iconSize()) / 2);
    }

    @Test
    public void backgroundEnclosesTheRowPlusPad() {
        PrayerIconLayout layout = PrayerIconLayout.of(3, 24, 2, 200, 300, 3);
        assertEquals(162 - 3, layout.backgroundX());
        assertEquals(300 - 3, layout.backgroundY());
        // right edge = last icon right edge + pad; bottom edge = anchorY + iconSize + pad.
        assertEquals(214 + 24 + 3, layout.backgroundX() + layout.backgroundWidth());
        assertEquals(300 + 24 + 3, layout.backgroundY() + layout.backgroundHeight());
    }
}
