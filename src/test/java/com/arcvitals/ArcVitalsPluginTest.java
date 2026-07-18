package com.arcvitals;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import net.runelite.api.Player;
import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;
import org.junit.Test;

public class ArcVitalsPluginTest {
    public static void main(String[] args) throws Exception {
        ExternalPluginManager.loadBuiltin(ArcVitalsPlugin.class);
        RuneLite.main(args);
    }

    @Test
    public void isAttackTargetTrueForNpc() {
        Player local = mock(Player.class);
        net.runelite.api.NPC npc = mock(net.runelite.api.NPC.class);
        assertTrue(ArcVitalsPlugin.isAttackTarget(npc, local));
    }

    @Test
    public void isAttackTargetFalseForSelfOrNull() {
        Player local = mock(Player.class);
        assertFalse(ArcVitalsPlugin.isAttackTarget(null, local));
        assertFalse(ArcVitalsPlugin.isAttackTarget(local, local));
    }

    @Test
    public void isAttackTargetTrueForOtherPlayer() {
        Player local = mock(Player.class);
        Player other = mock(Player.class);
        assertTrue(ArcVitalsPlugin.isAttackTarget(other, local));
    }
}
