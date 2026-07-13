package com.arcvitals;

import net.runelite.client.plugins.itemstats.StatChange;
import net.runelite.client.plugins.itemstats.StatsChanges;

final class RestorePreview {

    private RestorePreview() {
    }

    // The theoretical restore a hovered item applies to the named stat, or 0 if there is
    // no matching non-zero change. Null-safe so callers can pass an unresolved hover.
    static int forStat(StatsChanges changes, String statName) {
        if (changes == null || statName == null) {
            return 0;
        }
        for (StatChange c : changes.getStatChanges()) {
            if (c.getTheoretical() != 0 && c.getStat().getName().equals(statName)) {
                return c.getTheoretical();
            }
        }
        return 0;
    }
}
