# Mind Your Bubbles

[English](README.md) | [简体中文](README.zh-CN.md)

Mind Your Bubbles is a client-side Fabric mod for Minecraft that controls when the vanilla air bar appears.

## Why this mod?

Minecraft stores the player's remaining breath as the entity `Air` value. The vanilla HUD only shows that value in a few situations. Many players only notice it underwater, even though it is part of the survival rules.

Mind Your Bubbles makes that value easier to notice. You decide when the vanilla air bar appears.

The entity `Air` value can support more than an underwater warning. Mods, plugins, maps, and gameplay experiments can treat it as a resource worth designing around.

## Features

- Client-side only
- Does not need to be installed on servers
- Supports JSON configuration
- Can smooth the air bar when the entity `Air` value changes in large steps
- Adds an in-game config screen when Mod Menu and Cloth Config are installed

## Display modes

Choose one visibility mode:

| Mode | Behavior |
| --- | --- |
| `VANILLA` | Uses Minecraft's original air bar behavior. This is the default mode. |
| `WHEN_NOT_FULL` | Shows the air bar when the player's `Air` value is below its maximum. |
| `ALWAYS` | Shows the air bar at all times. |

## Visual smoothing

The optional smooth air bar animation setting helps preserve the vanilla bubble pop animation when a server-side plugin or datapack changes the player's `Air` value in larger steps.

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
  "smoothAirBarAnimation": false
}
```

Restart the game after editing the config file.

If Mod Menu and Cloth Config are installed, you can also change these options from the Mod Menu config screen. The in-game screen saves changes immediately.

## Requirements

- Minecraft 1.21.11
- Fabric Loader 0.19.3 or newer
- Java 21 or newer
- Fabric API

For the in-game config screen:

- Mod Menu
- Cloth Config API

## Compatibility

Mind Your Bubbles only controls vanilla air bar visibility and optional visual animation smoothing. It does not modify breathing mechanics, air bar textures, HUD layout, or other HUD bars such as:

- The food bar
- The health bar
- The mount health bar
- The experience bar

Because Minecraft still renders the air bar, other HUD mods can continue to adjust position and layout.

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
