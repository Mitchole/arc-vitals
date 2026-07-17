package com.arcvitals;

import java.awt.Color;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.api.gameval.VarPlayerID;

enum Vital {
    HITPOINTS(
        c -> c.getBoostedSkillLevel(Skill.HITPOINTS),
        c -> c.getRealSkillLevel(Skill.HITPOINTS),
        ArcVitalsConfig::hpEnabled, ArcVitalsConfig::hpColor,
        ArcVitalsConfig::hpThreshold, ArcVitalsConfig::hpSide,
        ArcVitalsConfig::hpShapeOverride, ArcVitalsConfig::hpFillOverride,
        ArcVitalsConfig::hpPatternOverride,
        ArcVitalsConfig::debugHpPercent,
        "Hitpoints"),
    PRAYER(
        c -> c.getBoostedSkillLevel(Skill.PRAYER),
        c -> c.getRealSkillLevel(Skill.PRAYER),
        ArcVitalsConfig::prayerEnabled, ArcVitalsConfig::prayerColor,
        ArcVitalsConfig::prayerThreshold, ArcVitalsConfig::prayerSide,
        ArcVitalsConfig::prayerShapeOverride, ArcVitalsConfig::prayerFillOverride,
        ArcVitalsConfig::prayerPatternOverride,
        ArcVitalsConfig::debugPrayerPercent,
        "Prayer"),
    SPECIAL_ATTACK(
        c -> c.getVarpValue(VarPlayerID.SA_ENERGY) / 10,
        c -> 100,
        ArcVitalsConfig::specEnabled, ArcVitalsConfig::specColor,
        ArcVitalsConfig::specThreshold, ArcVitalsConfig::specSide,
        ArcVitalsConfig::specShapeOverride, ArcVitalsConfig::specFillOverride,
        ArcVitalsConfig::specPatternOverride,
        ArcVitalsConfig::debugSpecPercent,
        null),
    RUN_ENERGY(
        c -> c.getEnergy() / 100,
        c -> 100,
        ArcVitalsConfig::runEnabled, ArcVitalsConfig::runColor,
        ArcVitalsConfig::runThreshold, ArcVitalsConfig::runSide,
        ArcVitalsConfig::runShapeOverride, ArcVitalsConfig::runFillOverride,
        ArcVitalsConfig::runPatternOverride,
        ArcVitalsConfig::debugRunPercent,
        "Run Energy");

    private final ToIntFunction<Client> current;
    private final ToIntFunction<Client> max;
    private final Predicate<ArcVitalsConfig> enabled;
    private final Function<ArcVitalsConfig, Color> color;
    private final ToIntFunction<ArcVitalsConfig> threshold;
    private final Function<ArcVitalsConfig, Side> side;
    private final Function<ArcVitalsConfig, ShapeOverride> shapeOverride;
    private final Function<ArcVitalsConfig, FillStyleOverride> fillOverride;
    private final Function<ArcVitalsConfig, PatternOverride> patternOverride;
    private final ToIntFunction<ArcVitalsConfig> debugPercent;
    private final String restoreStatName;

    Vital(ToIntFunction<Client> current, ToIntFunction<Client> max,
          Predicate<ArcVitalsConfig> enabled, Function<ArcVitalsConfig, Color> color,
          ToIntFunction<ArcVitalsConfig> threshold, Function<ArcVitalsConfig, Side> side,
          Function<ArcVitalsConfig, ShapeOverride> shapeOverride,
          Function<ArcVitalsConfig, FillStyleOverride> fillOverride,
          Function<ArcVitalsConfig, PatternOverride> patternOverride,
          ToIntFunction<ArcVitalsConfig> debugPercent, String restoreStatName) {
        this.current = current;
        this.max = max;
        this.enabled = enabled;
        this.color = color;
        this.threshold = threshold;
        this.side = side;
        this.shapeOverride = shapeOverride;
        this.fillOverride = fillOverride;
        this.patternOverride = patternOverride;
        this.debugPercent = debugPercent;
        this.restoreStatName = restoreStatName;
    }

    int current(Client client) {
        return current.applyAsInt(client);
    }

    int max(Client client) {
        return max.applyAsInt(client);
    }

    boolean enabled(ArcVitalsConfig config) {
        return enabled.test(config);
    }

    Color color(ArcVitalsConfig config) {
        return color.apply(config);
    }

    int threshold(ArcVitalsConfig config) {
        return threshold.applyAsInt(config);
    }

    Side side(ArcVitalsConfig config) {
        return side.apply(config);
    }

    BarShape shape(ArcVitalsConfig config) {
        return shapeOverride.apply(config).resolve(config.barShape());
    }

    FillStyle fillStyle(ArcVitalsConfig config) {
        return fillOverride.apply(config).resolve(config.fillStyle());
    }

    BarPattern pattern(ArcVitalsConfig config) {
        return patternOverride.apply(config).resolve(config.barPattern());
    }

    int debugPercent(ArcVitalsConfig config) {
        return debugPercent.applyAsInt(config);
    }

    String restoreStatName() {
        return restoreStatName;
    }
}
