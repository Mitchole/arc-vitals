package com.arcvitals;

import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.api.gameval.VarPlayerID;

enum Vital {
    HITPOINTS("Hitpoints", "Hitpoints"),
    PRAYER("Prayer", "Prayer"),
    SPECIAL_ATTACK("Special attack", null),
    RUN_ENERGY("Run energy", "Run Energy");

    private final String label;
    private final String restoreStatName;

    Vital(String label, String restoreStatName) {
        this.label = label;
        this.restoreStatName = restoreStatName;
    }

    String label() {
        return label;
    }

    String restoreStatName() {
        return restoreStatName;
    }

    int current(Client client) {
        switch (this) {
            case HITPOINTS:
                return client.getBoostedSkillLevel(Skill.HITPOINTS);
            case PRAYER:
                return client.getBoostedSkillLevel(Skill.PRAYER);
            case SPECIAL_ATTACK:
                return client.getVarpValue(VarPlayerID.SA_ENERGY) / 10;
            case RUN_ENERGY:
                return client.getEnergy() / 100;
            default:
                return 0;
        }
    }

    int max(Client client) {
        switch (this) {
            case HITPOINTS:
                return client.getRealSkillLevel(Skill.HITPOINTS);
            case PRAYER:
                return client.getRealSkillLevel(Skill.PRAYER);
            default:
                return 100;
        }
    }
}
