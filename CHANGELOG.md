[ReleaseTag]() is automatically replaced with the release tag, for example mc1.21.11-1.2.1.
[MCVersion]() is automatically replaced with the Minecraft version, for example 1.21.11.
[ModVersion]() is automatically replaced with the mod version, for example 1.2.1.

Everything above the line is ignored. Everything below the line is used as the release notes for GitHub and Modrinth.

----------

## Fixes

- Fixed air bar flickering in custom air-drain scenarios, especially outside vanilla underwater behavior and in bubble columns.
- Fixed client-side air recovery prediction affecting non-vanilla air bar visibility modes by rendering from the server-synced Air value.
- Fixed cases where air bubbles could skip or replay stale bursting frames.

## Compatibility

- Improved compatibility with other HUD mods by replacing the air bubble sprite redirect with a WrapOperation.
- Improved compatibility with vanilla HUD layout changes by reusing the observed vanilla air bar position.
