# Mind Your Bubbles

[English](README.md) | [简体中文](README.zh-CN.md)

Mind Your Bubbles（管好你的气泡条）是一个 Minecraft Fabric 客户端模组，让原版空气条成为更稳定的生存提示，而不是只在少数场景出现的水下警告。

## 为什么做这个模组？

Minecraft 会把玩家剩余呼吸量记录为实体的 `Air` 值。原版 HUD 只在少数场景显示这个值。很多玩家只有下水后才会注意到它，尽管它本来就是生存规则的一部分。

Mind Your Bubbles 让这个值更容易被注意到，并获得更稳定的显示。实体 `Air` 值不只适合作为水下警告，模组、插件、地图和玩法实验也可以把它当作一个值得设计的资源。

## 功能

- 原版空气条可见性模式：默认 `VANILLA`，也可选择 `WHEN_NOT_FULL` 或 `ALWAYS`
- 实体 `Air` 值大幅变化时的空气条平滑动画，默认启用
- JSON 配置
- 通过 Mod Menu 和 Cloth Config 提供可选的游戏内配置界面

## 显示模式

选择原版空气条的显示方式：

| 模式 | 行为 |
| --- | --- |
| `VANILLA` | 使用 Minecraft 原版空气条行为。 |
| `WHEN_NOT_FULL` | 当玩家的 `Air` 值低于最大值时显示原版空气条。 |
| `ALWAYS` | 始终显示空气条。 |

注：水肺药水或类似效果可以让玩家在水下保持满 `Air`。这种情况下，`VANILLA` 会因为玩家在水里而显示空气条，`WHEN_NOT_FULL` 会因为 `Air` 仍是满值而隐藏它。

## 视觉平滑

当服务端插件或数据包以较大的步长修改 `Air` 时，原版气泡过渡可能会被跳过或延迟。平滑动画设置会补上这些视觉步骤，让气泡破裂、短暂空槽和空槽以更自然的顺序出现。

这个设置只影响空气条的显示效果。实际 `Air` 值和呼吸规则仍由 Minecraft 或服务器控制。

## 配置

Mind Your Bubbles 会在游戏启动后创建这个配置文件：

```text
.minecraft/config/mind-your-bubbles.json
```

在开发环境中，配置文件会创建在：

```text
run/config/mind-your-bubbles.json
```

配置示例：

```json
{
  "visibilityMode": "VANILLA",
  "smoothAirBarAnimation": true
}
```

手动编辑配置文件后，请重启游戏。如果安装了 Mod Menu 和 Cloth Config，你也可以在游戏内修改相同选项，并且更改会立即保存。

## 运行要求

- Minecraft 1.21.11
- Fabric Loader 0.19.3 或更新版本
- Java 21 或更新版本

游戏内配置界面的可选依赖：

- Mod Menu
- Cloth Config API

## 兼容性

Mind Your Bubbles 只改变原版空气条的渲染。其他 HUD 模组仍可控制布局和其他 HUD 条。

已测试兼容以下 Minecraft 1.21.11 Fabric 模组：

- AppleSkin `3.0.8+mc1.21.11`
- Leave My Bars Alone `21.11.0`
- Raised `5.1.2`

## 构建

运行 Gradle wrapper：

```powershell
.\gradlew.bat build
```

Gradle 会将构建出的 jar 写入：

```text
build/libs/
```

## 许可证

本项目使用 CC0-1.0 许可证。
