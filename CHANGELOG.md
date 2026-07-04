[ReleaseTag]() is automatically replaced with the release tag, for example mc1.21.11-1.2.1.
[MCVersion]() is automatically replaced with the Minecraft version, for example 1.21.11.
[ModVersion]() is automatically replaced with the mod version, for example 1.2.1.

Everything above the line is ignored. Everything below the line is used as the release notes for GitHub and Modrinth.

----------

Mind Your Bubbles [ModVersion]() for Minecraft [MCVersion]() is a maintenance update that improves optional dependency handling, configuration recovery, and future version maintenance.

## Changes

- Removed Fabric API as a required dependency. Fabric Loader is still required.
- Kept Mod Menu and Cloth Config optional by delaying config screen class loading until Cloth Config is installed.
- Improved recovery for oversized, malformed, or missing-field JSON config files, including timestamped backups.
- Moved air bar policy, counting, and animation state out of the HUD mixin to make future Minecraft version ports easier.
- Added Mod Menu name and description translations for every bundled language.
- Added a manual GitHub Actions release workflow for GitHub and Modrinth publishing.

## Compatibility

- Minecraft [MCVersion]()
- Fabric Loader 0.19.3 or newer
- Client-side only
- Fabric API is not required
