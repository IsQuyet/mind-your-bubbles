# Mind Your Bubbles

[English](README.md) | [简体中文](README.zh-CN.md)

Mind Your Bubbles is a client-side Fabric mod for Minecraft that turns the vanilla air bar into a more consistent survival indicator instead of a warning that only appears in limited situations.

## Why this mod?

Minecraft tracks a player's remaining breath as the entity `Air` value, but the vanilla HUD only draws attention to it in limited situations. Mind Your Bubbles keeps the vanilla air bar while making `Air` easier to notice, read, and design around.

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

This setting only affects what the air bar shows. Minecraft or the server still controls `Air` and breathing rules.

## Configuration

Mind Your Bubbles creates this config file after the game starts:

```text
.minecraft/config/mind-your-bubbles.json
```

In a development environment, the file is created under:

```text
run/config/mind-your-bubbles.json
```

Example config:

```json
{
  "visibilityMode": "VANILLA",
  "smoothAirBarAnimation": true
}
```

Restart the game after editing the config file manually. If Mod Menu and Cloth Config are installed, the same options are available in-game and are saved immediately.

## Requirements

- Minecraft 1.21.11
- Fabric Loader 0.19.3 or newer
- Java 21 or newer
- Fabric API

Optional for the in-game config screen:

- Mod Menu
- Cloth Config API

## Compatibility

Mind Your Bubbles only changes vanilla air bar rendering. Other HUD mods can still control layout and other bars.

Tested compatible with the following Minecraft 1.21.11 Fabric mods:

- AppleSkin `3.0.8+mc1.21.11`
- Leave My Bars Alone `21.11.0`
- Raised `5.1.2`

## Building

Run the Gradle wrapper:

```powershell
.\gradlew.bat build
```

Gradle writes the built jars to:

```text
build/libs/
```

## License

This project is licensed under CC0-1.0.
