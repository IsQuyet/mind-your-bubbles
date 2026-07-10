<div align="center">

# Mind Your Bubbles

[English](README.md) | [简体中文](README.zh-CN.md)

[![Modrinth 下载量](https://img.shields.io/modrinth/dt/mind-your-bubbles?logo=modrinth&label=Modrinth&color=00AF5C)](https://modrinth.com/mod/mind-your-bubbles)
[![GitHub Releases](https://img.shields.io/badge/GitHub-Releases-181717?logo=github&logoColor=white)](https://github.com/IsQuyet/mind-your-bubbles/releases)
[![许可证](https://img.shields.io/github/license/IsQuyet/mind-your-bubbles?label=License&color=blue)](LICENSE)

</div>

Mind Your Bubbles（管好你的气泡条）是一个 Minecraft Fabric 客户端模组，让原版空气条更容易被看见，也能在水下警告之外发挥作用。

## 为什么做这个模组？

Minecraft 已经把玩家呼吸量记录为实体的 `Air` 值，但原版 HUD 只在少数场景显示它。Mind Your Bubbles 存在的原因是：`Air` 不只适合作为水下警告，也可以成为模组、插件、地图和玩法实验围绕设计的资源。

## 功能

- 选择原版空气条何时显示：默认为 `VANILLA`，也可改为 `WHEN_NOT_FULL` 或 `ALWAYS`。
- 平滑较大或较小的 `Air` 变化，让气泡过渡更易读。默认开启。
- 在非原版掉氧场景中保持自定义显示稳定。

## 行为

### 显示模式

| 模式 | 行为 |
| --- | --- |
| `VANILLA` | 使用 Minecraft 原版空气条行为。 |
| `WHEN_NOT_FULL` | 当玩家的 `Air` 值低于最大值时显示原版空气条。 |
| `ALWAYS` | 始终显示空气条。 |

水肺药水或类似效果可以让玩家在水下保持满 `Air`。这种情况下，`VANILLA` 会显示空气条，`WHEN_NOT_FULL` 会隐藏它。

### 视觉平滑

当 `Air` 以非原版步长变化时，平滑动画会补上缺失的气泡过渡。

<table>
  <tr>
    <th>变化</th>
    <th>原版</th>
    <th>平滑后</th>
  </tr>
  <tr>
    <th>`Air` 大幅变化</th>
    <td><img src="https://raw.githubusercontent.com/IsQuyet/mind-your-bubbles/main/docs/images/large-air-step-vanilla.gif" alt="原版空气条在 Air 值大幅变化时的表现" width="360"></td>
    <td><img src="https://raw.githubusercontent.com/IsQuyet/mind-your-bubbles/main/docs/images/large-air-step-smoothed.gif" alt="平滑后的空气条在 Air 值大幅变化时的表现" width="360"></td>
  </tr>
  <tr>
    <th>`Air` 小幅变化</th>
    <td><img src="https://raw.githubusercontent.com/IsQuyet/mind-your-bubbles/main/docs/images/small-air-step-vanilla.gif" alt="原版空气条在 Air 值小幅变化时的表现" width="360"></td>
    <td><img src="https://raw.githubusercontent.com/IsQuyet/mind-your-bubbles/main/docs/images/small-air-step-smoothed.gif" alt="平滑后的空气条在 Air 值小幅变化时的表现" width="360"></td>
  </tr>
</table>

## 配置

生成的配置文件位于 Minecraft 实例的配置目录：

```text
<实例文件夹>/config/mind-your-bubbles.json
```

## 运行要求

- Fabric Loader
- Java 21 或更新版本
- 与下载文件匹配的 Minecraft 版本

可选：

- Mod Menu
- Cloth Config API

## 许可证

本项目使用 CC0-1.0 许可证。
