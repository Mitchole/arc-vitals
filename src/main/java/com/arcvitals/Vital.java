package com.arcvitals;

import net.runelite.api.Skill;

enum Vital {
    HITPOINTS(Skill.HITPOINTS, "HP"),
    PRAYER(Skill.PRAYER, "Prayer");

    private final Skill skill;
    private final String label;

    Vital(Skill skill, String label) {
        this.skill = skill;
        this.label = label;
    }

    Skill skill() {
        return skill;
    }

    String label() {
        return label;
    }
}
