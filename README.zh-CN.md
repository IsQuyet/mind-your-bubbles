# Mind Your Bubbles

[English](README.md) | [简体中文](README.zh-CN.md)

Mind Your Bubbles（管好你的气泡条）是一个 Minecraft Fabric 客户端模组，用来控制原版空气条何时显示。

## 为什么做这个模组？

Minecraft 会把玩家剩余呼吸量记录为实体的 `Air` 值。原版 HUD 只在少数场景显示这个值。很多玩家只有下水后才会注意到它，尽管它本来就是生存规则的一部分。

Mind Your Bubbles 让这个值更容易被注意到。你可以决定原版空气条何时显示。

实体 `Air` 值不只适合作为水下警告。模组、插件、地图和玩法实验都可以把它当作一个值得设计的资源。

## 功能

- 仅客户端
- 不需要安装在服务器上
- 支持 JSON 配置
- 可在实体 `Air` 值大幅跳变时平滑空气条动画
- 安装 Mod Menu 和 Cloth Config 时提供游戏内配置界面

## 显示模式

选择一种可见性模式：

| 模式 | 行为 |
| --- | --- |
| `VANILLA` | 使用 Minecraft 原版空气条行为。这是默认模式。 |
| `WHEN_NOT_FULL` | 当玩家的 `Air` 值低于最大值时显示原版空气条。 |
| `ALWAYS` | 始终显示空气条。 |

## 视觉平滑

可选的平滑空气条动画设置可以在服务端插件或数据包以较大的步长修改玩家的 `Air` 值时，尽量保留原版气泡破裂动画。

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
  "smoothAirBarAnimation": false
}
```

手动编辑配置文件后，请重启游戏。

如果安装了 Mod Menu 和 Cloth Config，你也可以在 Mod Menu 的配置界面中修改这些选项。游戏内配置界面会立即保存更改。

## 运行要求

- Minecraft 1.21.11
- Fabric Loader 0.19.3 或更新版本
- Java 21 或更新版本
- Fabric API

如需启用游戏内配置界面，还需要：

- Mod Menu
- Cloth Config API

## 兼容性

Mind Your Bubbles 只控制原版空气条的可见性和可选的视觉动画平滑。它不会修改呼吸机制、空气条贴图、HUD 布局，也不会修改其他 HUD 条，例如：

- 饥饿条
- 生命条
- 坐骑生命条
- 经验条

因为空气条仍然由 Minecraft 渲染，其他 HUD 模组可以继续调整位置和布局。

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
