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
        return 140;
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
        return 70;
    }

    @ConfigItem(keyName = "barSpacing", name = "Bar spacing", description = "Gap between bars nested on the same side.", section = layoutSection, position = 5)
    @Range(min = 0, max = 40)
    @Units(Units.PIXELS)
    default int barSpacing() {
        return 4;
    }

    @ConfigItem(keyName = "curve", name = "Curve", description = "How much each bar bows outward, in degrees.", section = layoutSection, position = 6)
    @Range(min = 20, max = 180)
    default int curve() {
        return 110;
    }

    @ConfigItem(keyName = "fillDirection", name = "Fill direction", description = "Which way each bar drains.", section = layoutSection, position = 7)
    default FillDirection fillDirection() {
        return FillDirection.BOTTOM_UP;
    }

    @ConfigItem(keyName = "swapSides", name = "Swap sides", description = "Put Prayer on the left and Hitpoints on the right.", section = layoutSection, position = 8)
    default boolean swapSides() {
        return false;
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
}
