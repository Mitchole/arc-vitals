package com.arcvitals;

import java.util.ArrayList;
import java.util.List;
import net.runelite.api.Client;
import net.runelite.api.Prayer;
import net.runelite.api.gameval.SpriteID;
import net.runelite.api.gameval.VarbitID;

// Mirrors RuneLite's package-private PrayerType: one entry per Prayer, paired with the "prayer on"
// sprite the prayer tab shows. isActive is client.isPrayerActive(prayer) gated by isEnabled, so
// Deadeye/Mystic Vigour suppress the Eagle Eye/Mystic Might they share a base prayer varbit with, and
// both upgrades require their unlock varbit while outside Last Man Standing. Kept in prayer-book order
// so activeSpriteIds returns icons the way the tab lists them. PrayerType is package-private upstream
// and cannot be imported, so the table is copied here; a new prayer Jagex adds just renders no icon
// until this table is updated.
enum PrayerIcon {
    THICK_SKIN(Prayer.THICK_SKIN, SpriteID.Prayeron.THICK_SKIN),
    BURST_OF_STRENGTH(Prayer.BURST_OF_STRENGTH, SpriteID.Prayeron.BURST_OF_STRENGTH),
    CLARITY_OF_THOUGHT(Prayer.CLARITY_OF_THOUGHT, SpriteID.Prayeron.CLARITY_OF_THOUGHT),
    SHARP_EYE(Prayer.SHARP_EYE, SpriteID.Prayeron.SHARP_EYE),
    MYSTIC_WILL(Prayer.MYSTIC_WILL, SpriteID.Prayeron.MYSTIC_WILL),
    ROCK_SKIN(Prayer.ROCK_SKIN, SpriteID.Prayeron.ROCK_SKIN),
    SUPERHUMAN_STRENGTH(Prayer.SUPERHUMAN_STRENGTH, SpriteID.Prayeron.SUPERHUMAN_STRENGTH),
    IMPROVED_REFLEXES(Prayer.IMPROVED_REFLEXES, SpriteID.Prayeron.IMPROVED_REFLEXES),
    RAPID_RESTORE(Prayer.RAPID_RESTORE, SpriteID.Prayeron.RAPID_RESTORE),
    RAPID_HEAL(Prayer.RAPID_HEAL, SpriteID.Prayeron.RAPID_HEAL),
    PROTECT_ITEM(Prayer.PROTECT_ITEM, SpriteID.Prayeron.PROTECT_ITEM),
    HAWK_EYE(Prayer.HAWK_EYE, SpriteID.Prayeron.HAWK_EYE),
    MYSTIC_LORE(Prayer.MYSTIC_LORE, SpriteID.Prayeron.MYSTIC_LORE),
    STEEL_SKIN(Prayer.STEEL_SKIN, SpriteID.Prayeron.STEEL_SKIN),
    ULTIMATE_STRENGTH(Prayer.ULTIMATE_STRENGTH, SpriteID.Prayeron.ULTIMATE_STRENGTH),
    INCREDIBLE_REFLEXES(Prayer.INCREDIBLE_REFLEXES, SpriteID.Prayeron.INCREDIBLE_REFLEXES),
    PROTECT_FROM_MAGIC(Prayer.PROTECT_FROM_MAGIC, SpriteID.Prayeron.PROTECT_FROM_MAGIC),
    PROTECT_FROM_MISSILES(Prayer.PROTECT_FROM_MISSILES, SpriteID.Prayeron.PROTECT_FROM_MISSILES),
    PROTECT_FROM_MELEE(Prayer.PROTECT_FROM_MELEE, SpriteID.Prayeron.PROTECT_FROM_MELEE),
    EAGLE_EYE(Prayer.EAGLE_EYE, SpriteID.Prayeron.EAGLE_EYE) {
        @Override
        boolean isEnabled(Client client) {
            return !DEADEYE.isEnabled(client);
        }
    },
    MYSTIC_MIGHT(Prayer.MYSTIC_MIGHT, SpriteID.Prayeron.MYSTIC_MIGHT) {
        @Override
        boolean isEnabled(Client client) {
            return !MYSTIC_VIGOUR.isEnabled(client);
        }
    },
    RETRIBUTION(Prayer.RETRIBUTION, SpriteID.Prayeron.RETRIBUTION),
    REDEMPTION(Prayer.REDEMPTION, SpriteID.Prayeron.REDEMPTION),
    SMITE(Prayer.SMITE, SpriteID.Prayeron.SMITE),
    PRESERVE(Prayer.PRESERVE, SpriteID.Prayeron.PRESERVE),
    CHIVALRY(Prayer.CHIVALRY, SpriteID.Prayeron.CHIVALRY),
    DEADEYE(Prayer.DEADEYE, SpriteID.Prayeron.DEADEYE) {
        @Override
        boolean isEnabled(Client client) {
            boolean inLms = client.getVarbitValue(VarbitID.BR_INGAME) != 0;
            boolean deadeye = client.getVarbitValue(VarbitID.PRAYER_DEADEYE_UNLOCKED) != 0;
            return deadeye && !inLms;
        }
    },
    MYSTIC_VIGOUR(Prayer.MYSTIC_VIGOUR, SpriteID.Prayeron.MYSTIC_VIGOUR) {
        @Override
        boolean isEnabled(Client client) {
            boolean inLms = client.getVarbitValue(VarbitID.BR_INGAME) != 0;
            boolean vigour = client.getVarbitValue(VarbitID.PRAYER_MYSTIC_VIGOUR_UNLOCKED) != 0;
            return vigour && !inLms;
        }
    },
    PIETY(Prayer.PIETY, SpriteID.Prayeron.PIETY),
    RIGOUR(Prayer.RIGOUR, SpriteID.Prayeron.RIGOUR),
    AUGURY(Prayer.AUGURY, SpriteID.Prayeron.AUGURY),
    RP_REJUVENATION(Prayer.RP_REJUVENATION, SpriteID.IconPrayerZaros01_30x30.REJUVENATION),
    RP_ANCIENT_STRENGTH(Prayer.RP_ANCIENT_STRENGTH, SpriteID.IconPrayerZaros01_30x30.ANCIENT_STRENGTH),
    RP_ANCIENT_SIGHT(Prayer.RP_ANCIENT_SIGHT, SpriteID.IconPrayerZaros01_30x30.ANCIENT_SIGHT),
    RP_ANCIENT_WILL(Prayer.RP_ANCIENT_WILL, SpriteID.IconPrayerZaros01_30x30.ANCIENT_WILL),
    RP_PROTECT_ITEM(Prayer.RP_PROTECT_ITEM, SpriteID.IconPrayerZaros01_30x30.PROTECT_ITEM),
    RP_RUINOUS_GRACE(Prayer.RP_RUINOUS_GRACE, SpriteID.IconPrayerZaros01_30x30.RUINOUS_GRACE),
    RP_DAMPEN_MAGIC(Prayer.RP_DAMPEN_MAGIC, SpriteID.IconPrayerZaros01_30x30.DAMPEN_MAGIC),
    RP_DAMPEN_RANGED(Prayer.RP_DAMPEN_RANGED, SpriteID.IconPrayerZaros01_30x30.DAMPEN_RANGED),
    RP_DAMPEN_MELEE(Prayer.RP_DAMPEN_MELEE, SpriteID.IconPrayerZaros01_30x30.DAMPEN_MELEE),
    RP_TRINITAS(Prayer.RP_TRINITAS, SpriteID.IconPrayerZaros01_30x30.TRINITAS),
    RP_BERSERKER(Prayer.RP_BERSERKER, SpriteID.IconPrayerZaros01_30x30.BERSERKER),
    RP_PURGE(Prayer.RP_PURGE, SpriteID.IconPrayerZaros01_30x30.PURGE),
    RP_METABOLISE(Prayer.RP_METABOLISE, SpriteID.IconPrayerZaros01_30x30.METABOLISE),
    RP_REBUKE(Prayer.RP_REBUKE, SpriteID.IconPrayerZaros01_30x30.REBUKE),
    RP_VINDICATION(Prayer.RP_VINDICATION, SpriteID.IconPrayerZaros01_30x30.VINDICATION),
    RP_DECIMATE(Prayer.RP_DECIMATE, SpriteID.IconPrayerZaros01_30x30.DECIMATE),
    RP_ANNIHILATE(Prayer.RP_ANNIHILATE, SpriteID.IconPrayerZaros01_30x30.ANNIHILATE),
    RP_VAPORISE(Prayer.RP_VAPORISE, SpriteID.IconPrayerZaros01_30x30.VAPORISE),
    RP_FUMUS_VOW(Prayer.RP_FUMUS_VOW, SpriteID.IconPrayerZaros01_30x30.FUMUS_VOW),
    RP_UMBRAS_VOW(Prayer.RP_UMBRA_VOW, SpriteID.IconPrayerZaros01_30x30.UMBRAS_VOW),
    RP_CRUORS_VOW(Prayer.RP_CRUORS_VOW, SpriteID.IconPrayerZaros01_30x30.CRUORS_VOW),
    RP_GLACIES_VOW(Prayer.RP_GLACIES_VOW, SpriteID.IconPrayerZaros01_30x30.GLACIES_VOW),
    RP_WRATH(Prayer.RP_WRATH, SpriteID.IconPrayerZaros01_30x30.WRATH),
    RP_INTENSIFY(Prayer.RP_INTENSIFY, SpriteID.IconPrayerZaros01_30x30.INTENSIFY);

    private final Prayer prayer;
    private final int spriteId;

    PrayerIcon(Prayer prayer, int spriteId) {
        this.prayer = prayer;
        this.spriteId = spriteId;
    }

    boolean isEnabled(Client client) {
        return true;
    }

    // isPrayerActive(Prayer) is deprecated because it shares varbits across deadeye/eagle eye and
    // mystic vigour/might; isEnabled resolves that overlap, so the deprecation is handled, not ignored.
    @SuppressWarnings("deprecation")
    boolean isActive(Client client) {
        return client.isPrayerActive(prayer) && isEnabled(client);
    }

    static List<Integer> activeSpriteIds(Client client) {
        List<Integer> ids = new ArrayList<>();
        for (PrayerIcon icon : values()) {
            if (icon.isActive(client)) {
                ids.add(icon.spriteId);
            }
        }
        return ids;
    }
}
