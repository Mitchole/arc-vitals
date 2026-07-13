package com.arcvitals;

import net.runelite.client.plugins.itemstats.StatChange;
import net.runelite.client.plugins.itemstats.StatsChanges;
import net.runelite.client.plugins.itemstats.stats.Stats;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class RestorePreviewTest {
    private static StatChange change(net.runelite.client.plugins.itemstats.stats.Stat stat, int theoretical) {
        StatChange c = new StatChange();
        c.setStat(stat);
        c.setTheoretical(theoretical);
        return c;
    }

    private static StatsChanges changesOf(StatChange... items) {
        StatsChanges changes = new StatsChanges(items.length);
        changes.setStatChanges(items);
        return changes;
    }

    @Test
    public void returnsTheoreticalOfTheMatchingStat() {
        StatsChanges changes = changesOf(
            change(Stats.PRAYER, 12),
            change(Stats.HITPOINTS, 20));
        assertEquals(20, RestorePreview.forStat(changes, "Hitpoints"));
        assertEquals(12, RestorePreview.forStat(changes, "Prayer"));
    }

    @Test
    public void returnsZeroWhenNoStatMatches() {
        StatsChanges changes = changesOf(change(Stats.HITPOINTS, 20));
        assertEquals(0, RestorePreview.forStat(changes, "Run Energy"));
    }

    @Test
    public void returnsZeroWhenMatchingChangeIsZero() {
        StatsChanges changes = changesOf(change(Stats.HITPOINTS, 0));
        assertEquals(0, RestorePreview.forStat(changes, "Hitpoints"));
    }

    @Test
    public void returnsZeroForNullChanges() {
        assertEquals(0, RestorePreview.forStat(null, "Hitpoints"));
    }
}
