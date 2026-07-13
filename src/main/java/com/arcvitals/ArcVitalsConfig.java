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

    @ConfigSection(
        name = "Layout",
        description = "Position, size and shape of the bars",
        position = 0
    )
    String layoutSection = "layout";

    @ConfigSection(
        name = "Appearance",
        description = "Colours and value text",
        position = 1
    )
    String appearanceSection = "appearance";

    @ConfigSection(
        name = "Alerts",
        description = "Opacity and low-stat alerts",
        position = 2
    )
    String alertsSection = "alerts";

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

    @ConfigItem(keyName = "gap", name = "Gap from centre", description = "Spacing between the character and each bar.", section = layoutSection, position = 4)
    @Range(min = 0, max = 400)
    @Units(Units.PIXELS)
    default int gap() {
        return 70;
    }

    @ConfigItem(keyName = "curve", name = "Curve", description = "How much each bar bows outward, in degrees.", section = layoutSection, position = 5)
    @Range(min = 20, max = 180)
    default int curve() {
        return 110;
    }

    @ConfigItem(keyName = "fillDirection", name = "Fill direction", description = "Which way each bar drains.", section = layoutSection, position = 6)
    default FillDirection fillDirection() {
        return FillDirection.BOTTOM_UP;
    }

    @ConfigItem(keyName = "swapSides", name = "Swap sides", description = "Put Prayer on the left and Hitpoints on the right.", section = layoutSection, position = 7)
    default boolean swapSides() {
        return false;
    }

    @Alpha
    @ConfigItem(keyName = "hpColor", name = "Hitpoints colour", description = "Fill colour of the Hitpoints bar.", section = appearanceSection, position = 0)
    default Color hpColor() {
        return new Color(0, 200, 0);
    }

    @Alpha
    @ConfigItem(keyName = "prayerColor", name = "Prayer colour", description = "Fill colour of the Prayer bar.", section = appearanceSection, position = 1)
    default Color prayerColor() {
        return new Color(0, 190, 255);
    }

    @Alpha
    @ConfigItem(keyName = "trackColor", name = "Track colour", description = "Colour of the empty part of each bar.", section = appearanceSection, position = 2)
    default Color trackColor() {
        return new Color(0, 0, 0, 130);
    }

    @ConfigItem(keyName = "valueDisplay", name = "Value text", description = "What to show under each bar.", section = appearanceSection, position = 3)
    default ValueDisplay valueDisplay() {
        return ValueDisplay.CURRENT_MAX;
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

    @ConfigItem(keyName = "hpThreshold", name = "Hitpoints threshold", description = "Alert when Hitpoints fall below this percent of maximum.", section = alertsSection, position = 3)
    @Range(min = 0, max = 100)
    @Units(Units.PERCENT)
    default int hpThreshold() {
        return 30;
    }

    @ConfigItem(keyName = "prayerThreshold", name = "Prayer threshold", description = "Alert when Prayer falls below this percent of maximum.", section = alertsSection, position = 4)
    @Range(min = 0, max = 100)
    @Units(Units.PERCENT)
    default int prayerThreshold() {
        return 20;
    }

    @ConfigItem(keyName = "warnColorEnabled", name = "Warning colour", description = "Recolour a bar while it is alerting.", section = alertsSection, position = 5)
    default boolean warnColorEnabled() {
        return true;
    }

    @Alpha
    @ConfigItem(keyName = "warnColor", name = "Warning colour value", description = "Colour used while a bar is alerting.", section = alertsSection, position = 6)
    default Color warnColor() {
        return new Color(255, 60, 60);
    }
}
