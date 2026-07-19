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

    @ConfigSection(name = "Position & size", description = "Where the HUD sits and how big the bars are.", position = 0)
    String layoutSection = "layout";

    @ConfigSection(name = "Appearance", description = "Shape, fill, outline, colours and value text.", position = 1)
    String appearanceSection = "appearance";

    @ConfigSection(name = "Animation", description = "Smooth motion when a bar changes level", position = 2)
    String animationSection = "animation";

    @ConfigSection(name = "Alerts", description = "Brightening and recolouring when a stat runs low.", position = 3)
    String alertsSection = "alerts";

    @ConfigSection(name = "Visibility", description = "When the HUD is shown", position = 4)
    String visibilitySection = "visibility";

    @ConfigSection(name = "Hitpoints bar", description = "Show, colour, threshold, poison and overheal for the Hitpoints bar.", position = 5)
    String hpSection = "hpBar";

    @ConfigSection(name = "Prayer bar", description = "Show, colour, threshold and style for the Prayer bar.", position = 6)
    String prayerSection = "prayerBar";

    @ConfigSection(name = "Special attack bar", description = "Show, colour, threshold and style for the Special attack bar.", position = 7)
    String specSection = "specBar";

    @ConfigSection(name = "Run energy bar", description = "Show, colour, threshold and style for the Run energy bar.", position = 8)
    String runSection = "runBar";

    @ConfigSection(name = "Prayer icons", description = "Icons for the prayers you have active", position = 9)
    String prayerIconsSection = "prayerIcons";

    @ConfigSection(name = "Target bar", description = "A bar for your current combat target", position = 10)
    String targetBarSection = "targetBar";

    @ConfigSection(name = "Swing timer", description = "A bar for your attack cooldown", position = 11)
    String swingSection = "swingTimer";

    @ConfigSection(name = "Debug", description = "Preview the bars in chosen states", position = 12, closedByDefault = true)
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

    @ConfigItem(keyName = "curve", name = "Curve", description = "How much each bar bows outward, in degrees. Curved shapes only (ignored by Straight and Ring).", section = appearanceSection, position = 1)
    @Range(min = 20, max = 180)
    default int curve() {
        return 120;
    }

    @ConfigItem(keyName = "fillDirection", name = "Fill direction", description = "Which way each bar drains.", section = appearanceSection, position = 2)
    default FillDirection fillDirection() {
        return FillDirection.BOTTOM_UP;
    }

    @ConfigItem(keyName = "barShape", name = "Bar shape", description = "Overall shape of every bar.", section = appearanceSection, position = 0)
    default BarShape barShape() {
        return BarShape.ARC;
    }

    @ConfigItem(keyName = "fillStyle", name = "Fill style", description = "How the filled part of each bar is painted.", section = appearanceSection, position = 3)
    default FillStyle fillStyle() {
        return FillStyle.SMOOTH;
    }

    @ConfigItem(keyName = "segments", name = "Segments", description = "Number of pips for the Segmented fill style.", section = appearanceSection, position = 4)
    @Range(min = 4, max = 30)
    default int segments() {
        return 14;
    }

    @ConfigItem(keyName = "barPattern", name = "Bar pattern", description = "Material texture painted under each bar's fill. Not shown under the Gradient fill style.", section = appearanceSection, position = 5)
    default BarPattern barPattern() {
        return BarPattern.NONE;
    }

    @ConfigItem(keyName = "dragToMove", name = "Drag to move (hold Alt)", description = "Hold Alt and drag the HUD with the left mouse button to reposition it.", section = layoutSection, position = 6)
    default boolean dragToMove() {
        return true;
    }

    // The four bar sections below (Hitpoints, Prayer, Special attack, Run energy) repeat the same ~10
    // items each. This is not reducible: RuneLite builds config from a JDK proxy over this interface,
    // so every setting needs its own distinctly-named default method with a literal keyName. A loop or
    // shared helper cannot generate them, so the repetition stays.
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

    @ConfigItem(keyName = "hpShapeOverride", name = "Hitpoints shape", description = "Shape for the Hitpoints bar. Inherit uses the global bar shape.", section = hpSection, position = 10)
    default ShapeOverride hpShapeOverride() {
        return ShapeOverride.INHERIT;
    }

    @ConfigItem(keyName = "hpFillOverride", name = "Hitpoints fill style", description = "Fill style for the Hitpoints bar. Inherit uses the global fill style.", section = hpSection, position = 11)
    default FillStyleOverride hpFillOverride() {
        return FillStyleOverride.INHERIT;
    }

    @ConfigItem(keyName = "hpPatternOverride", name = "Hitpoints pattern", description = "Pattern for the Hitpoints bar. Inherit uses the global bar pattern.", section = hpSection, position = 12)
    default PatternOverride hpPatternOverride() {
        return PatternOverride.INHERIT;
    }

    @ConfigItem(keyName = "hpDetached", name = "Detach this bar", description = "Float the Hitpoints bar free of the central group so it can sit on its own. Move it with Alt and the left mouse button (when Drag to move is on), or with the positions below.", section = hpSection, position = 13)
    default boolean hpDetached() {
        return false;
    }

    @ConfigItem(keyName = "hpDetachX", name = "Detached X", description = "Horizontal position of the detached Hitpoints bar, measured from screen centre. Used when Detach this bar is on.", section = hpSection, position = 14)
    @Range(min = -2000, max = 2000)
    @Units(Units.PIXELS)
    default int hpDetachX() {
        return 0;
    }

    @ConfigItem(keyName = "hpDetachY", name = "Detached Y", description = "Vertical position of the detached Hitpoints bar, measured from screen centre. Used when Detach this bar is on.", section = hpSection, position = 15)
    @Range(min = -2000, max = 2000)
    @Units(Units.PIXELS)
    default int hpDetachY() {
        return 0;
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

    @ConfigItem(keyName = "prayerPatternOverride", name = "Prayer pattern", description = "Pattern for the Prayer bar. Inherit uses the global bar pattern.", section = prayerSection, position = 6)
    default PatternOverride prayerPatternOverride() {
        return PatternOverride.INHERIT;
    }

    @ConfigItem(keyName = "prayerDetached", name = "Detach this bar", description = "Float the Prayer bar free of the central group so it can sit on its own. Move it with Alt and the left mouse button (when Drag to move is on), or with the positions below.", section = prayerSection, position = 7)
    default boolean prayerDetached() {
        return false;
    }

    @ConfigItem(keyName = "prayerDetachX", name = "Detached X", description = "Horizontal position of the detached Prayer bar, measured from screen centre. Used when Detach this bar is on.", section = prayerSection, position = 8)
    @Range(min = -2000, max = 2000)
    @Units(Units.PIXELS)
    default int prayerDetachX() {
        return 0;
    }

    @ConfigItem(keyName = "prayerDetachY", name = "Detached Y", description = "Vertical position of the detached Prayer bar, measured from screen centre. Used when Detach this bar is on.", section = prayerSection, position = 9)
    @Range(min = -2000, max = 2000)
    @Units(Units.PIXELS)
    default int prayerDetachY() {
        return 0;
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

    @ConfigItem(keyName = "specPatternOverride", name = "Special attack pattern", description = "Pattern for the Special attack bar. Inherit uses the global bar pattern.", section = specSection, position = 6)
    default PatternOverride specPatternOverride() {
        return PatternOverride.INHERIT;
    }

    @ConfigItem(keyName = "specDetached", name = "Detach this bar", description = "Float the Special attack bar free of the central group so it can sit on its own. Move it with Alt and the left mouse button (when Drag to move is on), or with the positions below.", section = specSection, position = 7)
    default boolean specDetached() {
        return false;
    }

    @ConfigItem(keyName = "specDetachX", name = "Detached X", description = "Horizontal position of the detached Special attack bar, measured from screen centre. Used when Detach this bar is on.", section = specSection, position = 8)
    @Range(min = -2000, max = 2000)
    @Units(Units.PIXELS)
    default int specDetachX() {
        return 0;
    }

    @ConfigItem(keyName = "specDetachY", name = "Detached Y", description = "Vertical position of the detached Special attack bar, measured from screen centre. Used when Detach this bar is on.", section = specSection, position = 9)
    @Range(min = -2000, max = 2000)
    @Units(Units.PIXELS)
    default int specDetachY() {
        return 0;
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

    @ConfigItem(keyName = "runPatternOverride", name = "Run energy pattern", description = "Pattern for the Run energy bar. Inherit uses the global bar pattern.", section = runSection, position = 6)
    default PatternOverride runPatternOverride() {
        return PatternOverride.INHERIT;
    }

    @ConfigItem(keyName = "runDetached", name = "Detach this bar", description = "Float the Run energy bar free of the central group so it can sit on its own. Move it with Alt and the left mouse button (when Drag to move is on), or with the positions below.", section = runSection, position = 7)
    default boolean runDetached() {
        return false;
    }

    @ConfigItem(keyName = "runDetachX", name = "Detached X", description = "Horizontal position of the detached Run energy bar, measured from screen centre. Used when Detach this bar is on.", section = runSection, position = 8)
    @Range(min = -2000, max = 2000)
    @Units(Units.PIXELS)
    default int runDetachX() {
        return 0;
    }

    @ConfigItem(keyName = "runDetachY", name = "Detached Y", description = "Vertical position of the detached Run energy bar, measured from screen centre. Used when Detach this bar is on.", section = runSection, position = 9)
    @Range(min = -2000, max = 2000)
    @Units(Units.PIXELS)
    default int runDetachY() {
        return 0;
    }

    @ConfigItem(keyName = "showPrayerIcons", name = "Show prayer icons", description = "Show icons for the prayers you currently have active, centred under the HUD.", section = prayerIconsSection, position = 0)
    default boolean showPrayerIcons() {
        return true;
    }

    @ConfigItem(keyName = "prayerIconSize", name = "Icon size", description = "Width and height of each prayer icon.", section = prayerIconsSection, position = 1)
    @Range(min = 12, max = 48)
    @Units(Units.PIXELS)
    default int prayerIconSize() {
        return 24;
    }

    @ConfigItem(keyName = "prayerIconOffset", name = "Vertical offset", description = "Move the icon row up or down from the bottom of the bars.", section = prayerIconsSection, position = 2)
    @Range(min = -100, max = 300)
    @Units(Units.PIXELS)
    default int prayerIconOffset() {
        return 6;
    }

    @ConfigItem(keyName = "prayerIconSpacing", name = "Icon spacing", description = "Gap between the prayer icons.", section = prayerIconsSection, position = 3)
    @Range(min = 0, max = 20)
    @Units(Units.PIXELS)
    default int prayerIconSpacing() {
        return 2;
    }

    @ConfigItem(keyName = "prayerIconBackground", name = "Icon background", description = "Draw a background chip behind the icon row.", section = prayerIconsSection, position = 4)
    default boolean prayerIconBackground() {
        return false;
    }

    @Alpha
    @ConfigItem(keyName = "prayerIconBackgroundColor", name = "Icon background colour", description = "Colour of the chip drawn behind the icon row.", section = prayerIconsSection, position = 5)
    default Color prayerIconBackgroundColor() {
        return new Color(0, 0, 0, 130);
    }

    @ConfigItem(keyName = "targetBarEnabled", name = "Show target bar", description = "Show a bar for the hitpoints of your current combat target.", section = targetBarSection, position = 0)
    default boolean targetBarEnabled() {
        return false;
    }

    @Alpha
    @ConfigItem(keyName = "targetBarColor", name = "Target bar colour", description = "Fill colour of the target bar.", section = targetBarSection, position = 1)
    default Color targetBarColor() {
        return new Color(200, 40, 40);
    }

    @ConfigItem(keyName = "targetBarLabel", name = "Label", description = "What the target bar's label shows.", section = targetBarSection, position = 2)
    default TargetLabel targetBarLabel() {
        return TargetLabel.NAME_AND_PERCENT;
    }

    @ConfigItem(keyName = "targetBarSide", name = "Bow direction", description = "Which way a curved target bar bows.", section = targetBarSection, position = 3)
    default Side targetBarSide() {
        return Side.RIGHT;
    }

    @ConfigItem(keyName = "targetBarShapeOverride", name = "Shape", description = "Shape for the target bar. Inherit uses the global bar shape.", section = targetBarSection, position = 4)
    default ShapeOverride targetBarShapeOverride() {
        return ShapeOverride.INHERIT;
    }

    @ConfigItem(keyName = "targetBarOffsetX", name = "Position X", description = "Horizontal position of the target bar, measured from screen centre.", section = targetBarSection, position = 5)
    @Range(min = -2000, max = 2000)
    @Units(Units.PIXELS)
    default int targetBarOffsetX() {
        return 0;
    }

    @ConfigItem(keyName = "targetBarOffsetY", name = "Position Y", description = "Vertical position of the target bar, measured from screen centre.", section = targetBarSection, position = 6)
    @Range(min = -2000, max = 2000)
    @Units(Units.PIXELS)
    default int targetBarOffsetY() {
        return -220;
    }

    @ConfigItem(keyName = "swingEnabled", name = "Show swing timer", description = "Show a bar that fills over your attack cooldown.", section = swingSection, position = 0)
    default boolean swingEnabled() {
        return false;
    }

    @Alpha
    @ConfigItem(keyName = "swingColor", name = "Swing timer colour", description = "Fill colour of the swing timer.", section = swingSection, position = 1)
    default Color swingColor() {
        return new Color(210, 235, 248);
    }

    @ConfigItem(keyName = "swingPlacement", name = "Placement", description = "Where the swing timer sits. Top and Bottom float above/below you and can be dragged with Alt; Nested tucks it into the group.", section = swingSection, position = 2)
    default SwingPlacement swingPlacement() {
        return SwingPlacement.TOP;
    }

    @ConfigItem(keyName = "swingSide", name = "Nesting side", description = "Which side the swing timer nests on, when Placement is Nested.", section = swingSection, position = 3)
    default Side swingSide() {
        return Side.LEFT;
    }

    @ConfigItem(keyName = "showSwingTicks", name = "Show tick marks", description = "Draw a notch at each attack tick along the bar.", section = swingSection, position = 4)
    default boolean showSwingTicks() {
        return true;
    }

    @ConfigItem(keyName = "swingOffsetX", name = "Position X", description = "Horizontal position of the swing timer, measured from screen centre (Top/Bottom placement).", section = swingSection, position = 5)
    @Range(min = -2000, max = 2000)
    @Units(Units.PIXELS)
    default int swingOffsetX() {
        return 0;
    }

    @ConfigItem(keyName = "swingOffsetY", name = "Position Y", description = "Vertical position of the swing timer, measured from screen centre (Top/Bottom placement).", section = swingSection, position = 6)
    @Range(min = -2000, max = 2000)
    @Units(Units.PIXELS)
    default int swingOffsetY() {
        return 0;
    }

    @ConfigItem(keyName = "overhealEnabled", name = "Show overheal", description = "Show hitpoints boosted past your real level as a coloured band past a real-max tick.", section = hpSection, position = 7)
    default boolean overhealEnabled() {
        return false;
    }

    @Alpha
    @ConfigItem(keyName = "overhealColor", name = "Overheal colour", description = "Colour of the over-max band.", section = hpSection, position = 8)
    default Color overhealColor() {
        return new Color(120, 240, 255, 235);
    }

    @ConfigItem(keyName = "showOverhealTick", name = "Show real-max tick", description = "Draw a tick where your real maximum sits.", section = hpSection, position = 9)
    default boolean showOverhealTick() {
        return true;
    }

    @Alpha
    @ConfigItem(keyName = "trackColor", name = "Track colour", description = "Colour of the empty part of each bar.", section = appearanceSection, position = 10)
    default Color trackColor() {
        return new Color(0, 0, 0, 130);
    }

    @ConfigItem(keyName = "valueDisplay", name = "Value text", description = "What to show under each bar.", section = appearanceSection, position = 11)
    default ValueDisplay valueDisplay() {
        return ValueDisplay.CURRENT_MAX;
    }

    @ConfigItem(keyName = "showRestorePreview", name = "Restore preview", description = "Show how much hovered food or potions would restore, as a lighter segment.", section = appearanceSection, position = 12)
    default boolean showRestorePreview() {
        return true;
    }

    @ConfigItem(keyName = "flatEnds", name = "Flat bar ends", description = "Cut the bar ends flat (IceHUD style) instead of rounded.", section = appearanceSection, position = 6)
    default boolean flatEnds() {
        return true;
    }

    @ConfigItem(keyName = "showOutline", name = "Bar outline", description = "Draw a border around each bar.", section = appearanceSection, position = 7)
    default boolean showOutline() {
        return true;
    }

    @ConfigItem(keyName = "outlineWidth", name = "Outline width", description = "Thickness of the bar outline.", section = appearanceSection, position = 8)
    @Range(min = 1, max = 5)
    @Units(Units.PIXELS)
    default int outlineWidth() {
        return 1;
    }

    @Alpha
    @ConfigItem(keyName = "outlineColor", name = "Outline colour", description = "Colour of the bar outline.", section = appearanceSection, position = 9)
    default Color outlineColor() {
        return new Color(0, 0, 0, 180);
    }

    @ConfigItem(keyName = "smoothMotion", name = "Smooth bar motion", description = "Ease each bar to its new level instead of snapping. Drops glide slower than gains.", section = animationSection, position = 0)
    default boolean smoothMotion() {
        return true;
    }

    @ConfigItem(keyName = "drainGlideMs", name = "Drain glide", description = "How long a bar takes to fall to a lower level.", section = animationSection, position = 1)
    @Range(min = 50, max = 1000)
    @Units(Units.MILLISECONDS)
    default int drainGlideMs() {
        return 350;
    }

    @ConfigItem(keyName = "restoreGlideMs", name = "Restore glide", description = "How long a bar takes to rise to a higher level.", section = animationSection, position = 2)
    @Range(min = 50, max = 1000)
    @Units(Units.MILLISECONDS)
    default int restoreGlideMs() {
        return 120;
    }

    @ConfigItem(keyName = "baseOpacity", name = "Base opacity", description = "Resting opacity of the HUD.", section = appearanceSection, position = 13)
    @Range(min = 0, max = 100)
    @Units(Units.PERCENT)
    default int baseOpacity() {
        return 60;
    }

    @ConfigItem(keyName = "alertMode", name = "Alert mode", description = "What brightens when a stat drops low.", section = alertsSection, position = 0)
    default AlertMode alertMode() {
        return AlertMode.PER_BAR;
    }

    @ConfigItem(keyName = "alertOpacity", name = "Alert opacity", description = "Opacity a bar ramps to when alerting.", section = alertsSection, position = 1)
    @Range(min = 0, max = 100)
    @Units(Units.PERCENT)
    default int alertOpacity() {
        return 100;
    }

    @ConfigItem(keyName = "warnColorEnabled", name = "Recolour when alerting", description = "Recolour a bar while it is alerting.", section = alertsSection, position = 2)
    default boolean warnColorEnabled() {
        return true;
    }

    @Alpha
    @ConfigItem(keyName = "warnColor", name = "Warning colour", description = "Colour used while a bar is alerting.", section = alertsSection, position = 3)
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
