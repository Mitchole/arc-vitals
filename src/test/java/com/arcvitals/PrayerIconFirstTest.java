package com.arcvitals;

import java.util.List;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PrayerIconFirstTest {

    @Test
    public void zeroGivesEmpty() {
        assertTrue(PrayerIcon.firstSpriteIds(0).isEmpty());
        assertTrue(PrayerIcon.firstSpriteIds(-3).isEmpty());
    }

    @Test
    public void returnsRequestedCount() {
        assertEquals(3, PrayerIcon.firstSpriteIds(3).size());
        assertEquals(1, PrayerIcon.firstSpriteIds(1).size());
    }

    @Test
    public void clampsToTableSize() {
        int all = PrayerIcon.values().length;
        assertEquals(all, PrayerIcon.firstSpriteIds(1000).size());
    }

    @Test
    public void isAStablePrefix() {
        List<Integer> three = PrayerIcon.firstSpriteIds(3);
        List<Integer> five = PrayerIcon.firstSpriteIds(5);
        assertEquals(three, five.subList(0, 3));
    }
}
