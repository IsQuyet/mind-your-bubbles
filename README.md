<div align="center">

# Mind Your Bubbles

[English](README.md) | [简体中文](README.zh-CN.md)

[![Modrinth Downloads](https://img.shields.io/modrinth/dt/mind-your-bubbles?logo=modrinth&label=Modrinth&color=00AF5C)](https://modrinth.com/mod/mind-your-bubbles)
[![GitHub Releases](https://img.shields.io/badge/GitHub-Releases-181717?logo=github&logoColor=white)](https://github.com/IsQuyet/mind-your-bubbles/releases)
[![License](https://img.shields.io/github/license/IsQuyet/mind-your-bubbles?label=License&color=blue)](LICENSE)

</div>

Mind Your Bubbles is a client-side Fabric mod that makes Minecraft's vanilla air bar easier to see and useful beyond the usual underwater warning.

## Why this mod?

Minecraft already stores breath as the entity `Air` value, but the vanilla HUD only shows it in a few situations. Mind Your Bubbles exists because `Air` can be more than an underwater warning: it can be a gameplay resource for mods, plugins, maps, and experiments to design around.

## Features

- Choose when the vanilla air bar appears: `VANILLA` by default, with `WHEN_NOT_FULL` and `ALWAYS` available.
- Smooth large or small `Air` changes so bubble transitions stay readable. Enabled by default.
- Keep custom Air-drain displays stable in non-vanilla scenarios.

## Behavior

### Display modes

| Mode | Behavior |
| --- | --- |
| `VANILLA` | Uses Minecraft's original air bar behavior. |
| `WHEN_NOT_FULL` | Shows the air bar when the player's `Air` value is below its maximum. |
| `ALWAYS` | Shows the air bar at all times. |

Water Breathing, or similar effects, can keep `Air` full underwater. In that case, `VANILLA` shows the bar, while `WHEN_NOT_FULL` hides it.

### Visual smoothing

When `Air` changes in non-vanilla steps, smooth animation fills in the missing bubble transitions.

<table>
  <tr>
    <th>Change</th>
    <th>Vanilla</th>
    <th>Smoothed</th>
  </tr>
  <tr>
    <th>Large `Air` changes</th>
    <td><img src="https://raw.githubusercontent.com/IsQuyet/mind-your-bubbles/main/docs/images/large-air-step-vanilla.gif" alt="Vanilla air bar with a large Air value step" width="360"></td>
    <td><img src="https://raw.githubusercontent.com/IsQuyet/mind-your-bubbles/main/docs/images/large-air-step-smoothed.gif" alt="Smoothed air bar with a large Air value step" width="360"></td>
  </tr>
  <tr>
    <th>Small `Air` changes</th>
    <td><img src="https://raw.githubusercontent.com/IsQuyet/mind-your-bubbles/main/docs/images/small-air-step-vanilla.gif" alt="Vanilla air bar with a small Air value step" width="360"></td>
    <td><img src="https://raw.githubusercontent.com/IsQuyet/mind-your-bubbles/main/docs/images/small-air-step-smoothed.gif" alt="Smoothed air bar with a small Air value step" width="360"></td>
  </tr>
</table>

## Configuration

Generated config file is located in the Minecraft instance config directory:

```text
<instance folder>/config/mind-your-bubbles.json
```

## Requirements

- Fabric Loader
- Java 21 or newer
- A Minecraft version matching the downloaded file

Optional:

- Mod Menu
- Cloth Config API

## License

This project is licensed under CC0-1.0.
