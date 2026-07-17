package com.arcvitals;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;
import net.runelite.client.config.Units;

@ConfigGroup("arcvitals")
public interface ArcVitalsConfig extends Config {

    @ConfigSection(name = "Layout", description = "Position, size and shape of the bars", position = 0)
    String layoutSection = "layout";

    @ConfigSection(name = "Hitpoints bar", description = "Hitpoints bar", position = 1)
    String hpSection = "hpBar";

    @ConfigSection(name = "Prayer bar", description = "Prayer bar", position = 2)
    String prayerSection = "prayerBar";

    @ConfigSection(name = "Special attack bar", description = "Special attack bar", position = 3)
    String specSection = "specBar";

    @ConfigSection(name = "Run energy bar", description = "Run energy bar", position = 4)
    String runSection = "runBar";

    @ConfigSection(name = "Appearance", description = "Colours and value text", position = 5)
    String appearanceSection = "appearance";

    @ConfigSection(name = "Alerts", description = "Opacity and low-stat alerts", position = 6)
    String alertsSection = "alerts";

    @ConfigSection(name = "Visibility", description = "When the HUD is shown", position = 7)
    String visibilitySection = "visibility";

    @ConfigSection(name = "Debug", description = "Preview the bars in chosen states", position = 8, closedByDefault = true)
    String debugSection = "debug";

    @ConfigItem(keyName = "offsetX", name = "Horizontal offset", description = "Move the whole HUD left or right from screen centre.", section = layoutSection, position = 0)
    @Range(min = -500, max = 500)
    @Units(Units.PIXELS)
    default int offsetX() {
        return 0;
    }

    @ConfigItem(keyName = "offsetY", name = "Vertical offset", description = "Move the whole HUD up or down from screen centre.", section = layoutSection, position = 1)
    @Range(min = -500, max = 500)
    @Units(Units.PIXELS)
    default int offsetY() {
        return 0;
    }

    @ConfigItem(keyName = "size", name = "Bar height", description = "Height of each bar.", section = layoutSection, position = 2)
    @Range(min = 40, max = 400)
    @Units(Units.PIXELS)
    default int size() {
        return 150;
    }

    @ConfigItem(keyName = "thickness", name = "Bar thickness", description = "Stroke width of each bar.", section = layoutSection, position = 3)
    @Range(min = 2, max = 40)
    @Units(Units.PIXELS)
    default int thickness() {
        return 12;
    }

    @ConfigItem(keyName = "gap", name = "Gap from centre", description = "Spacing between the character and the innermost bar on each side.", section = layoutSection, position = 4)
    @Range(min = 0, max = 400)
    @Units(Units.PIXELS)
    default int gap() {
        return 88;
    }

    @ConfigItem(keyName = "barSpacing", name = "Bar spacing", description = "Gap between bars nested on the same side.", section = layoutSection, position = 5)
    @Range(min = 0, max = 40)
    @Units(Units.PIXELS)
    default int barSpacing() {
        return 7;
    }

    @ConfigItem(keyName = "curve", name = "Curve", description = "How much each bar bows outward, in degrees.", section = layoutSection, position = 6)
    @Range(min = 20, max = 180)
    default int curve() {
        return 120;
    }

    @ConfigItem(keyName = "fillDirection", name = "Fill direction", description = "Which way each bar drains.", section = layoutSection, position = 7)
    default FillDirection fillDirection() {
        return FillDirection.BOTTOM_UP;
    }

    @ConfigItem(keyName = "barShape", name = "Bar shape", description = "Overall shape of every bar.", section = layoutSection, position = 8)
    default BarShape barShape() {
        return BarShape.ARC;
    }

    @ConfigItem(keyName = "hpEnabled", name = "Show Hitpoints", description = "Show the Hitpoints bar.", section = hpSection, position = 0)
    default boolean hpEnabled() {
        return true;
    }

    @Alpha
    @ConfigItem(keyName = "hpColor", name = "Hitpoints colour", description = "Fill colour of the Hitpoints bar.", section = hpSection, position = 1)
    default Color hpColor() {
        return new Color(0, 200, 0);
    }

    @ConfigItem(keyName = "hpThreshold", name = "Hitpoints threshold", description = "Alert when Hitpoints fall below this percent of maximum.", section = hpSection, position = 2)
    @Range(min = 0, max = 100)
    @Units(Units.PERCENT)
    default int hpThreshold() {
        return 30;
    }

    @ConfigItem(keyName = "hpSide", name = "Hitpoints side", description = "Which side the Hitpoints bar is on.", section = hpSection, position = 3)
    default Side hpSide() {
        return Side.LEFT;
    }

    @ConfigItem(keyName = "hpPoisonRecolor", name = "Poison recolour", description = "Recolour the Hitpoints bar while poisoned or envenomed.", section = hpSection, position = 4)
    default boolean hpPoisonRecolor() {
        return true;
    }

    @Alpha
    @ConfigItem(keyName = "hpPoisonColor", name = "Poisoned colour", description = "Fill colour of the Hitpoints bar while poisoned.", section = hpSection, position = 5)
    default Color hpPoisonColor() {
        return new Color(0, 145, 0);
    }

    @Alpha
    @ConfigItem(keyName = "hpVenomColor", name = "Venomed colour", description = "Fill colour of the Hitpoints bar while envenomed.", section = hpSection, position = 6)
    default Color hpVenomColor() {
        return new Color(0, 65, 0);
    }

    @ConfigItem(keyName = "hpShapeOverride", name = "Hitpoints shape", description = "Shape for the Hitpoints bar. Inherit uses the global bar shape.", section = hpSection, position = 7)
    default ShapeOverride hpShapeOverride() {
        return ShapeOverride.INHERIT;
    }

    @ConfigItem(keyName = "hpFillOverride", name = "Hitpoints fill style", description = "Fill style for the Hitpoints bar. Inherit uses the global fill style.", section = hpSection, position = 8)
    default FillStyleOverride hpFillOverride() {
        return FillStyleOverride.INHERIT;
    }

    @ConfigItem(keyName = "prayerEnabled", name = "Show Prayer", description = "Show the Prayer bar.", section = prayerSection, position = 0)
    default boolean prayerEnabled() {
        return true;
    }

    @Alpha
    @ConfigItem(keyName = "prayerColor", name = "Prayer colour", description = "Fill colour of the Prayer bar.", section = prayerSection, position = 1)
    default Color prayerColor() {
        return new Color(0, 190, 255);
    }

    @ConfigItem(keyName = "prayerThreshold", name = "Prayer threshold", description = "Alert when Prayer falls below this percent of maximum.", section = prayerSection, position = 2)
    @Range(min = 0, max = 100)
    @Units(Units.PERCENT)
    default int prayerThreshold() {
        return 20;
    }

    @ConfigItem(keyName = "prayerSide", name = "Prayer side", description = "Which side the Prayer bar is on.", section = prayerSection, position = 3)
    default Side prayerSide() {
        return Side.RIGHT;
    }

    @ConfigItem(keyName = "prayerShapeOverride", name = "Prayer shape", description = "Shape for the Prayer bar. Inherit uses the global bar shape.", section = prayerSection, position = 4)
    default ShapeOverride prayerShapeOverride() {
        return ShapeOverride.INHERIT;
    }

    @ConfigItem(keyName = "prayerFillOverride", name = "Prayer fill style", description = "Fill style for the Prayer bar. Inherit uses the global fill style.", section = prayerSection, position = 5)
    default FillStyleOverride prayerFillOverride() {
        return FillStyleOverride.INHERIT;
    }

    @ConfigItem(keyName = "specEnabled", name = "Show Special attack", description = "Show the Special attack bar.", section = specSection, position = 0)
    default boolean specEnabled() {
        return false;
    }

    @Alpha
    @ConfigItem(keyName = "specColor", name = "Special attack colour", description = "Fill colour of the Special attack bar.", section = specSection, position = 1)
    default Color specColor() {
        return new Color(230, 180, 0);
    }

    @ConfigItem(keyName = "specThreshold", name = "Special attack threshold", description = "Alert when Special attack falls below this percent (0 = never).", section = specSection, position = 2)
    @Range(min = 0, max = 100)
    @Units(Units.PERCENT)
    default int specThreshold() {
        return 0;
    }

    @ConfigItem(keyName = "specSide", name = "Special attack side", description = "Which side the Special attack bar is on.", section = specSection, position = 3)
    default Side specSide() {
        return Side.LEFT;
    }

    @ConfigItem(keyName = "specShapeOverride", name = "Special attack shape", description = "Shape for the Special attack bar. Inherit uses the global bar shape.", section = specSection, position = 4)
    default ShapeOverride specShapeOverride() {
        return ShapeOverride.INHERIT;
    }

    @ConfigItem(keyName = "specFillOverride", name = "Special attack fill style", description = "Fill style for the Special attack bar. Inherit uses the global fill style.", section = specSection, position = 5)
    default FillStyleOverride specFillOverride() {
        return FillStyleOverride.INHERIT;
    }

    @ConfigItem(keyName = "runEnabled", name = "Show Run energy", description = "Show the Run energy bar.", section = runSection, position = 0)
    default boolean runEnabled() {
        return false;
    }

    @Alpha
    @ConfigItem(keyName = "runColor", name = "Run energy colour", description = "Fill colour of the Run energy bar.", section = runSection, position = 1)
    default Color runColor() {
        return new Color(230, 120, 30);
    }

    @ConfigItem(keyName = "runThreshold", name = "Run energy threshold", description = "Alert when Run energy falls below this percent (0 = never).", section = runSection, position = 2)
    @Range(min = 0, max = 100)
    @Units(Units.PERCENT)
    default int runThreshold() {
        return 0;
    }

    @ConfigItem(keyName = "runSide", name = "Run energy side", description = "Which side the Run energy bar is on.", section = runSection, position = 3)
    default Side runSide() {
        return Side.RIGHT;
    }

    @ConfigItem(keyName = "runShapeOverride", name = "Run energy shape", description = "Shape for the Run energy bar. Inherit uses the global bar shape.", section = runSection, position = 4)
    default ShapeOverride runShapeOverride() {
        return ShapeOverride.INHERIT;
    }

    @ConfigItem(keyName = "runFillOverride", name = "Run energy fill style", description = "Fill style for the Run energy bar. Inherit uses the global fill style.", section = runSection, position = 5)
    default FillStyleOverride runFillOverride() {
        return FillStyleOverride.INHERIT;
    }

    @Alpha
    @ConfigItem(keyName = "trackColor", name = "Track colour", description = "Colour of the empty part of each bar.", section = appearanceSection, position = 0)
    default Color trackColor() {
        return new Color(0, 0, 0, 130);
    }

    @ConfigItem(keyName = "valueDisplay", name = "Value text", description = "What to show under each bar.", section = appearanceSection, position = 1)
    default ValueDisplay valueDisplay() {
        return ValueDisplay.CURRENT_MAX;
    }

    @ConfigItem(keyName = "showRestorePreview", name = "Restore preview", description = "Show how much hovered food or potions would restore, as a lighter segment.", section = appearanceSection, position = 2)
    default boolean showRestorePreview() {
        return true;
    }

    @ConfigItem(keyName = "flatEnds", name = "Flat bar ends", description = "Cut the bar ends flat (IceHUD style) instead of rounded.", section = appearanceSection, position = 3)
    default boolean flatEnds() {
        return true;
    }

    @ConfigItem(keyName = "showOutline", name = "Bar outline", description = "Draw a border around each bar.", section = appearanceSection, position = 4)
    default boolean showOutline() {
        return true;
    }

    @ConfigItem(keyName = "outlineWidth", name = "Outline width", description = "Thickness of the bar outline.", section = appearanceSection, position = 5)
    @Range(min = 1, max = 5)
    @Units(Units.PIXELS)
    default int outlineWidth() {
        return 1;
    }

    @Alpha
    @ConfigItem(keyName = "outlineColor", name = "Outline colour", description = "Colour of the bar outline.", section = appearanceSection, position = 6)
    default Color outlineColor() {
        return new Color(0, 0, 0, 180);
    }

    @ConfigItem(keyName = "fillStyle", name = "Fill style", description = "How the filled part of each bar is painted.", section = appearanceSection, position = 7)
    default FillStyle fillStyle() {
        return FillStyle.SMOOTH;
    }

    @ConfigItem(keyName = "baseOpacity", name = "Base opacity", description = "Resting opacity of the HUD.", section = alertsSection, position = 0)
    @Range(min = 0, max = 100)
    @Units(Units.PERCENT)
    default int baseOpacity() {
        return 60;
    }

    @ConfigItem(keyName = "alertMode", name = "Alert mode", description = "What brightens when a stat drops low.", section = alertsSection, position = 1)
    default AlertMode alertMode() {
        return AlertMode.PER_BAR;
    }

    @ConfigItem(keyName = "alertOpacity", name = "Alert opacity", description = "Opacity a bar ramps to when alerting.", section = alertsSection, position = 2)
    @Range(min = 0, max = 100)
    @Units(Units.PERCENT)
    default int alertOpacity() {
        return 100;
    }

    @ConfigItem(keyName = "warnColorEnabled", name = "Warning colour", description = "Recolour a bar while it is alerting.", section = alertsSection, position = 3)
    default boolean warnColorEnabled() {
        return true;
    }

    @Alpha
    @ConfigItem(keyName = "warnColor", name = "Warning colour value", description = "Colour used while a bar is alerting.", section = alertsSection, position = 4)
    default Color warnColor() {
        return new Color(255, 60, 60);
    }

    @ConfigItem(keyName = "hideOutOfCombat", name = "Hide out of combat", description = "Hide the HUD when you have been out of combat for a while.", section = visibilitySection, position = 0)
    default boolean hideOutOfCombat() {
        return false;
    }

    @ConfigItem(keyName = "hideOutOfCombatDelay", name = "Hide after", description = "Seconds out of combat before the HUD hides (needs Hide out of combat).", section = visibilitySection, position = 1)
    @Range(min = 0, max = 120)
    @Units(Units.SECONDS)
    default int hideOutOfCombatDelay() {
        return 5;
    }

    @ConfigItem(keyName = "showWhilePraying", name = "Show while praying", description = "When hidden out of combat, keep the Prayer bar or the whole HUD visible while any prayer is active.", section = visibilitySection, position = 2)
    default PrayerVisibility showWhilePraying() {
        return PrayerVisibility.PRAYER_BAR;
    }

    @ConfigItem(keyName = "debugEnabled", name = "Enable debug preview", description = "Preview the bars using the values below instead of live stats.", section = debugSection, position = 0)
    default boolean debugEnabled() {
        return false;
    }

    @ConfigItem(keyName = "debugHpPercent", name = "Hitpoints %", description = "Previewed Hitpoints, as a percent of maximum.", section = debugSection, position = 1)
    @Range(min = 0, max = 100)
    @Units(Units.PERCENT)
    default int debugHpPercent() {
        return 50;
    }

    @ConfigItem(keyName = "debugPrayerPercent", name = "Prayer %", description = "Previewed Prayer, as a percent of maximum.", section = debugSection, position = 2)
    @Range(min = 0, max = 100)
    @Units(Units.PERCENT)
    default int debugPrayerPercent() {
        return 50;
    }

    @ConfigItem(keyName = "debugSpecPercent", name = "Special attack %", description = "Previewed Special attack, as a percent of maximum.", section = debugSection, position = 3)
    @Range(min = 0, max = 100)
    @Units(Units.PERCENT)
    default int debugSpecPercent() {
        return 50;
    }

    @ConfigItem(keyName = "debugRunPercent", name = "Run energy %", description = "Previewed Run energy, as a percent of maximum.", section = debugSection, position = 4)
    @Range(min = 0, max = 100)
    @Units(Units.PERCENT)
    default int debugRunPercent() {
        return 50;
    }

    @ConfigItem(keyName = "debugPoisonState", name = "Poison state", description = "Previewed poison state for the Hitpoints bar. The low-stat warning colour still takes priority.", section = debugSection, position = 5)
    default HpStatus debugPoisonState() {
        return HpStatus.NONE;
    }
}
