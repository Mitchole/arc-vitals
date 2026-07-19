<p align="center">
  <img src="logo.png" alt="Arc Vitals" width="260">
</p>

# Arc Vitals

A vitals HUD for RuneLite, inspired by IceHUD. Curved bars sit either side of
your character and follow the curve as they fill and drain, so you can watch your
hitpoints and prayer without dragging your eyes down to the orbs. From there it
opens up: more bars, different shapes and textures, a bar for whatever you're
fighting, and a handful of extras you can switch on if you want them.

![Arc Vitals in game](screenshot.png)

## Features

- Up to four bars: hitpoints, prayer, special attack and run energy. Each one
  gets its own colour, side and low threshold, and you can turn each on or off.
  Put two on the same side and they nest one outside the other. Special attack
  and run energy start switched off.
- A value readout under each bar. Show the current and max, a percentage, both,
  or nothing at all.
- Hover a bit of food or a potion and the bar shows how much it would heal you,
  drawn as a lighter segment above the current fill.
- Overheal. A brew that takes your hitpoints past your normal maximum shows the
  extra as a separate band, with a tick marking where your real max sits.
- Low-stat alerts. A bar brightens once it drops past the threshold you set, and
  it can flip to a warning colour too. Have the alert on each bar, across the
  whole HUD, or off.
- The hitpoints bar darkens while you're poisoned and darkens further under
  venom. Both colours can be changed.
- Smooth motion. Bars glide to their new level instead of snapping, and a drop
  eases slower than a top-up so a hit is easy to catch. Retime both or turn it
  off.
- A row showing the prayers you currently have active, centred under the HUD.
- A target bar for whatever you're fighting, showing its health with a name or
  percentage label.
- A swing timer (experimental) that fills over your attack cooldown, for a read
  on when your next hit is ready.
- Move the whole HUD by holding Alt and dragging with the left mouse button, or
  pull a single bar out of the group and place it on its own.
- Flat or rounded bar ends, an optional outline, and a resting opacity so the
  whole thing can fade back when you're not looking at it.
- If you'd rather only see it mid-fight, there's an option to hide the HUD once
  you've been out of combat for a few seconds. Keep a prayer running and it can
  hold the prayer bar (or the whole HUD) on screen so the drain doesn't go
  unnoticed.

## Getting started

Install Arc Vitals from the Plugin Hub and turn it on. You start with two bars,
hitpoints on the left and prayer on the right. Everything past that stays off
until you want it.

## Bar styles

Three settings decide how the bars look, and each has a global choice plus a
per-bar override that's set to Inherit by default.

- Shape. The default curved arc, a plain straight vertical bar, a leaf, a horn,
  or a ring. Set a left and a right bar to Ring and the two halves close into a
  full circle around your character.
- Fill. A flat colour, a glossy tube, a bright-to-dark gradient, segmented pips
  (you pick how many), a glowing core, or notches at the 25/50/75% marks.
- Pattern. An optional material texture under the fill, tinted to the bar's
  colour: brushed metal, carbon weave, riveted plate, dragon scale, rune etch or
  tech mesh.

There's also a fill direction if you'd rather a bar drained from the other end.

## Configuration

Open the plugin settings to change any of this. The shared options come first:

- Position & size. Where the HUD sits, the height and thickness of the bars, the
  gap from your character, and whether Alt-drag is on.
- Appearance. The shape, fill and pattern above, plus bar ends, outline, track
  colour, the value text, and the resting opacity.
- Animation. The smooth motion and how fast bars drop and recover.
- Alerts. The low-stat brightening and the optional warning colour.
- Visibility. The out-of-combat hide, how long it waits, and whether an active
  prayer keeps the prayer bar or the whole HUD up.

After that comes a section for each bar: its colour, side, threshold, its own
shape, fill and pattern, and the option to detach it and set its own position.
The hitpoints section also holds the poison and venom colours and the overheal
band. Prayer icons, the target bar and the swing timer each get a section too.

A Debug section at the bottom fakes the whole HUD from sliders, so you can style
any of it without being in a fight. It stands in for the bars, the target bar,
the swing timer, the prayer icons and the overheal band, and it can animate the
values to show the motion running.

## Notes

The HUD is pinned to the middle of the game view rather than to your character,
because the game doesn't keep your character dead centre on screen. One thing to
watch for: in Stretched Mode the bars get scaled up along with the rest of the
interface and go a bit soft. Switch Stretched Mode off and they're sharp again.

The swing timer is still experimental. It sizes itself from your weapon speed and
reads your attack animation, so an unusual weapon or an action mid-fight can
throw the timing out. It stays off unless you turn it on.

## Changelog

### 1.2

- Bar shapes: straight, leaf, horn and ring alongside the original curve.
- Six fill styles and six material textures, each with a per-bar override.
- Smooth motion when a bar changes level, with separate drop and recovery timing.
- A row of your active prayer icons under the HUD.
- A target bar for your current opponent's health.
- An overheal band on the hitpoints bar for brew boosts past your max.
- Move the HUD with Alt-drag, or detach a single bar and place it anywhere.
- An experimental swing timer that fills over your attack cooldown.
- Reorganised settings: the shared options sit up top and every bar has its own
  section.
- The debug preview now covers the target bar, swing timer, prayer icons,
  overheal and the drain animation.

### 1.1

- Poison and venom darken the hitpoints bar.
- An active prayer can keep the prayer bar, or the whole HUD, on screen while
  you're hidden out of combat.
- Added the debug preview section.

### 1.0

- First Plugin Hub release. Up to four curved bars for hitpoints, prayer,
  special attack and run energy, each with its own colour, side and threshold.
- Value text, restore preview on hovered food and potions, low-stat alerts, flat
  or rounded ends, an outline, resting opacity, and hide-out-of-combat.
