package com.arcvitals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.runelite.api.Client;
import net.runelite.api.Prayer;
import net.runelite.api.gameval.SpriteID;
import net.runelite.api.gameval.VarbitID;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

// isPrayerActive(Prayer) is deprecated (shares base-prayer varbits); the enum under test handles that,
// and the tests must stub it, so the whole class suppresses the deprecation warning.
@SuppressWarnings("deprecation")
public class PrayerIconTest {

    @Test
    public void noPrayersActiveGivesEmptyList() {
        Client client = mock(Client.class);
        assertEquals(Collections.emptyList(), PrayerIcon.activeSpriteIds(client));
    }

    @Test
    public void activePrayersAreReturnedInBookOrder() {
        Client client = mock(Client.class);
        // PROTECT_FROM_MELEE sits before PIETY in the prayer book, so it comes first regardless of
        // the order they are stubbed here.
        when(client.isPrayerActive(Prayer.PIETY)).thenReturn(true);
        when(client.isPrayerActive(Prayer.PROTECT_FROM_MELEE)).thenReturn(true);
        assertEquals(
            Arrays.asList(SpriteID.Prayeron.PROTECT_FROM_MELEE, SpriteID.Prayeron.PIETY),
            PrayerIcon.activeSpriteIds(client));
    }

    @Test
    public void deadeyeSuppressesEagleEye() {
        Client client = mock(Client.class);
        when(client.isPrayerActive(Prayer.EAGLE_EYE)).thenReturn(true);
        when(client.isPrayerActive(Prayer.DEADEYE)).thenReturn(true);
        when(client.getVarbitValue(VarbitID.PRAYER_DEADEYE_UNLOCKED)).thenReturn(1);
        // BR_INGAME defaults to 0 (not in Last Man Standing).
        assertEquals(
            Collections.singletonList(SpriteID.Prayeron.DEADEYE),
            PrayerIcon.activeSpriteIds(client));
    }

    @Test
    public void mysticVigourSuppressesMysticMight() {
        Client client = mock(Client.class);
        when(client.isPrayerActive(Prayer.MYSTIC_MIGHT)).thenReturn(true);
        when(client.isPrayerActive(Prayer.MYSTIC_VIGOUR)).thenReturn(true);
        when(client.getVarbitValue(VarbitID.PRAYER_MYSTIC_VIGOUR_UNLOCKED)).thenReturn(1);
        assertEquals(
            Collections.singletonList(SpriteID.Prayeron.MYSTIC_VIGOUR),
            PrayerIcon.activeSpriteIds(client));
    }

    @Test
    public void deadeyeNotUnlockedShowsEagleEye() {
        Client client = mock(Client.class);
        when(client.isPrayerActive(Prayer.EAGLE_EYE)).thenReturn(true);
        when(client.isPrayerActive(Prayer.DEADEYE)).thenReturn(true);
        // PRAYER_DEADEYE_UNLOCKED defaults to 0 -> Deadeye inactive, Eagle Eye shown.
        assertEquals(
            Collections.singletonList(SpriteID.Prayeron.EAGLE_EYE),
            PrayerIcon.activeSpriteIds(client));
    }

    @Test
    public void deadeyeInLastManStandingShowsEagleEye() {
        Client client = mock(Client.class);
        when(client.isPrayerActive(Prayer.EAGLE_EYE)).thenReturn(true);
        when(client.isPrayerActive(Prayer.DEADEYE)).thenReturn(true);
        when(client.getVarbitValue(VarbitID.PRAYER_DEADEYE_UNLOCKED)).thenReturn(1);
        when(client.getVarbitValue(VarbitID.BR_INGAME)).thenReturn(1);
        assertEquals(
            Collections.singletonList(SpriteID.Prayeron.EAGLE_EYE),
            PrayerIcon.activeSpriteIds(client));
    }
}
