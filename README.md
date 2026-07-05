# Mind Your Bubbles

[English](README.md) | [简体中文](README.zh-CN.md)

Mind Your Bubbles is a client-side Fabric mod for Minecraft that turns the vanilla air bar into a more consistent survival indicator.

## Why this mod?

Minecraft stores the player's remaining breath as the entity `Air` value. The vanilla HUD only shows that value in a few situations. Many players only notice it underwater, even though it is part of the survival rules.

Mind Your Bubbles makes that value easier to notice and gives it a more consistent display. The entity `Air` value can support more than an underwater warning, and mods, plugins, maps, and gameplay experiments can treat it as a resource worth designing around.

## Features

- Vanilla air bar visibility modes: `VANILLA` by default, `WHEN_NOT_FULL`, and `ALWAYS`
- Smooth air bar animation for large entity `Air` value changes, enabled by default
- JSON configuration
- Optional in-game config screen through Mod Menu and Cloth Config

## Display modes

Choose how the vanilla air bar appears:

| Mode | Behavior |
| --- | --- |
| `VANILLA` | Uses Minecraft's original air bar behavior. |
| `WHEN_NOT_FULL` | Shows the air bar when the player's `Air` value is below its maximum. |
| `ALWAYS` | Shows the air bar at all times. |

Note: A Water Breathing potion, or a similar effect, can keep `Air` full underwater. In that case, `VANILLA` still shows the air bar because the player is in water, while `WHEN_NOT_FULL` hides it because `Air` is full.

## Visual smoothing

When a server-side plugin or datapack changes `Air` in larger steps, vanilla bubble transitions can be skipped or delayed. The smooth animation setting fills in those visual steps so bubble popping, brief blank slots, and empty slots appear in a more natural order.

Large `Air` value changes:

<table>
  <tr>
    <th>Vanilla</th>
    <th>Smoothed</th>
  </tr>
  <tr>
    <td><img src="https://raw.githubusercontent.com/IsQuyet/mind-your-bubbles/main/docs/images/large-air-step-vanilla.gif" alt="Vanilla air bar with a large Air value step" width="400"></td>
    <td><img src="https://raw.githubusercontent.com/IsQuyet/mind-your-bubbles/main/docs/images/large-air-step-smoothed.gif" alt="Smoothed air bar with a large Air value step" width="400"></td>
  </tr>
</table>

Small `Air` value changes:

<table>
  <tr>
    <th>Vanilla</th>
    <th>Smoothed</th>
  </tr>
  <tr>
    <td><img src="https://raw.githubusercontent.com/IsQuyet/mind-your-bubbles/main/docs/images/small-air-step-vanilla.gif" alt="Vanilla air bar with a small Air value step" width="400"></td>
    <td><img src="https://raw.githubusercontent.com/IsQuyet/mind-your-bubbles/main/docs/images/small-air-step-smoothed.gif" alt="Smoothed air bar with a small Air value step" width="400"></td>
  </tr>
</table>

## Configuration

If Mod Menu and Cloth Config are installed, you can change options from the Mod Menu config screen. Changes apply after saving.

For manual configuration, edit this file after the game creates it:

```text
.minecraft/config/mind-your-bubbles.json
```

Example config:

```json
{
  "visibilityMode": "VANILLA",
  "smoothAirBarAnimation": true
}
```

Restart the game after editing the config file manually.

## Requirements

- Minecraft 1.21.11
- Fabric Loader 0.19.3 or newer
- Java 21 or newer

Optional for the in-game config screen:

- Mod Menu
- Cloth Config API

## Compatibility

Tested compatible with the following Minecraft 1.21.11 Fabric mods:

- AppleSkin `3.0.8+mc1.21.11`
- Leave My Bars Alone `21.11.0`
- Raised `5.1.2`

## License

This project is licensed under CC0-1.0.
