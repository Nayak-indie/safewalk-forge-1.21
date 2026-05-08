# SafeWalk Forge 1.21.1

SafeWalk is a client-side Minecraft Forge mod for 1.21.1. It keeps the classic SafeWalk behavior from the 1.8.9 era while using modern Forge APIs, Mojang mappings, and vanilla client interactions.

The mod helps prevent walking off block edges and can optionally assist bridge placement when the player is holding blocks. It does not add server-side content, custom packets, or a required server mod.

## Documentation

All markdown files in this repository:

| File | Purpose |
| --- | --- |
| [README.md](README.md) | Main project overview, usage, build, and release notes. |
| [docs/original-mod-analysis.md](docs/original-mod-analysis.md) | Notes from inspecting the original SafeWalk 1.8.9 jar and its behavior. |
| [docs/porting-map.md](docs/porting-map.md) | Mapping reference from old 1.8.9 Forge concepts to the 1.21.1 Forge rewrite. |

## Features

- Toggleable SafeWalk with the default keybind `V`.
- Client commands: `/safewalk`, `/sw`, `/sb`, and `/speedbridge`.
- Three movement modes:
  - `SNEAK`: holds sneak when the player reaches an edge.
  - `BREEZILY`: slows horizontal movement near edges and can place at the crosshair.
  - `SCAFFOLD`: attempts vanilla block placement against the nearest neighbor block below the player.
- Optional auto-placement using the normal client `useItemOn` interaction.
- Optional local chat status messages.
- Optional disable-on-jump and disable-on-fall safety toggles.
- Client-only Forge setup with `displayTest="IGNORE_SERVER_VERSION"` for multiplayer clients.

## Commands

| Command | Action |
| --- | --- |
| `/sw help` | Show command help in chat. |
| `/sw toggle` | Enable or disable SafeWalk. |
| `/sw mode` | Cycle between `SNEAK`, `BREEZILY`, and `SCAFFOLD`. |
| `/sw mode sneak` | Switch to sneak mode. |
| `/sw mode breezily` | Switch to breezily mode. |
| `/sw mode scaffold` | Switch to scaffold mode. |
| `/sw chat on/off` | Toggle local chat messages. |
| `/sw click on/off` | Toggle auto-placement assistance. |
| `/sw fall on/off` | Toggle disable-on-fall behavior. |
| `/sw jump on/off` | Toggle disable-on-jump behavior. |

## Requirements

- Minecraft `1.21.1`
- Minecraft Forge `52.x`
- Java `21`
- Gradle through the ForgeGradle setup in this repository

## Build

From the repository root:

```powershell
.\gradlew build
```

The built jar is written to:

```text
build/libs/
```

## Project Layout

```text
src/main/java/club/notben/safewalk/
  ClientSafeWalk.java     Client tick logic, keybind, commands, and placement behavior
  SafeWalkConfig.java     Forge client config values and mode enum
  SafeWalkMod.java        Mod entry point and client-only registration

src/main/resources/
  META-INF/mods.toml      Forge mod metadata
  assets/safewalk/lang/   Keybind localization

docs/
  original-mod-analysis.md
  porting-map.md
```

## Notes

This is a modern Forge rewrite inspired by the original 1.8.9 SafeWalk behavior. The repository owner is Nayak Indie.

Use this mod responsibly and check server rules before using movement or placement assistance on multiplayer servers.

## License

MIT License. See [LICENSE](LICENSE).
