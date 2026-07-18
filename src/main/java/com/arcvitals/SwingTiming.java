package com.arcvitals;

// Pure attack-cooldown maths for the swing timer. aspeed is the equipped weapon's base attack speed
// in game ticks (unarmed = 4). Ranged "Rapid" is one tick faster; melee styles do not change speed,
// so the caller passes rapid = false for them. No AWT, no Client.
final class SwingTiming {

    private SwingTiming() {
    }

    static int cooldownTicks(int aspeed, boolean rapid) {
        int ticks = rapid ? aspeed - 1 : aspeed;
        return ticks < 1 ? 1 : ticks;
    }
}
