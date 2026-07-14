<p align="center">
  <img src="logo.png" alt="Arc Vitals" width="260">
</p>

# Arc Vitals

An IceHUD inspired vitals HUD for RuneLite. Curved bars sit either side of the
screen centre and fill and drain along their curve as your stats change, so you
can keep an eye on your hitpoints and prayer without looking down at the orbs.

![Arc Vitals in game](screenshot.png)

## Features

- Up to four curved bars flanking the centre of the screen: Hitpoints, Prayer,
  Special attack and Run energy. Each has its own toggle, colour, low threshold
  and side, and bars on the same side nest outward. Special attack and Run
  energy are off by default.
- Flat or rounded bar ends, with an optional outline.
- Adjustable position, size, thickness, gap and curve.
- Value text under each bar: current/max, a percentage, both, or nothing.
- Restore preview: hover food or a potion and the bar shows how much it would
  restore as a lighter segment.
- Base opacity, so the HUD can sit quietly in the background.
- Low-stat alert: a bar brightens (and can turn red) once it drops below the
  threshold you set. Alert per bar, for the whole HUD, or off.
- Option to hide the HUD once you have been out of combat for a set number of
  seconds.

## Getting started

Install Arc Vitals from the Plugin Hub and enable it. Out of the box you get two
bars: hitpoints on the left, prayer on the right. Everything else is optional and
stays off until you switch it on.

## Configuration

The settings panel is grouped so you can find things quickly:

- **Layout** sets the position, size, thickness, gap and curve of the bars.
- A **section per bar** (Hitpoints, Prayer, Special attack, Run energy) holds
  that bar's toggle, colour, side and low threshold.
- **Appearance** covers flat or rounded ends, the outline, value text and base
  opacity.
- **Alerts** controls the low-stat warning and its colour.
- **Visibility** holds the out-of-combat hide option and its delay.

## Notes

The bars are anchored to the centre of the game view rather than to your
character, since the game does not keep your character centred on screen. If you
play in Stretched Mode the bars are upscaled with the rest of the interface and
can look a little soft; turning Stretched Mode off keeps them crisp.
