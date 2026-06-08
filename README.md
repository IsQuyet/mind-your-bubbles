# Air Bar Tweaks

Air Bar Tweaks is a client-side Minecraft Fabric mod that lets you configure when the vanilla air bar is displayed.

It only changes the render timing of the vanilla air bar. It does not change breathing mechanics, HUD position, or air bar textures.

## Features

- Client-side only
- Keeps the vanilla air bar style and position
- Supports simple JSON configuration
- Does not need to be installed on servers

## Display modes

The mod supports three air bar visibility modes:

| Mode | Behavior |
| --- | --- |
| `VANILLA` | Keeps Minecraft's original air bar behavior. This is the default mode. |
| `WHEN_NOT_FULL` | Shows the air bar when the player's internal air supply is not full. |
| `ALWAYS` | Always renders the vanilla air bar. |

## Configuration

The config file is created automatically after the game starts:

```text
.minecraft/config/air-bar-tweaks.json
```

In a development environment, it is created under:

```text
run/config/air-bar-tweaks.json
```

Example config:

```json
{
  "visibilityMode": "VANILLA"
}
```

After editing the config file, restart the game for the change to take effect.

## Requirements

- Minecraft 1.21.11
- Fabric Loader 0.19.3 or newer
- Java 21 or newer
- Fabric API

## Building

Use the Gradle wrapper:

```powershell
.\gradlew.bat build
```

The built jars will be generated under:

```text
build/libs/
```

## License

This project is licensed under CC0-1.0.
