<div align="center">

# SafeWalk Forge 1.21.1

![SafeWalk animated header](https://readme-typing-svg.demolab.com?font=JetBrains+Mono&weight=600&size=22&duration=2800&pause=900&color=54C6EB&center=true&vCenter=true&width=720&lines=Modern+Forge+1.21.1+SafeWalk+rewrite;Client-side+edge+safety+for+bridging;Classic+1.8.9+behavior%2C+modern+APIs)

[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-62B47A?style=for-the-badge&logo=codementor&logoColor=white)](#requirements)
[![Forge](https://img.shields.io/badge/Forge-52.x-F16436?style=for-the-badge&logo=curseforge&logoColor=white)](#requirements)
[![Java](https://img.shields.io/badge/Java-21-007396?style=for-the-badge&logo=openjdk&logoColor=white)](#requirements)
[![License](https://img.shields.io/badge/License-MIT-9D7CD8?style=for-the-badge)](LICENSE)

**A clean, client-side SafeWalk rewrite for modern Forge.**  
Built for edge control, optional bridge placement assistance, and compatibility with normal vanilla client interactions.

</div>

---

## Overview

SafeWalk helps prevent walking off block edges in Minecraft `1.21.1`. It ports the core feel of the classic `1.8.9` SafeWalk mod into a modern Forge codebase using Mojang mappings, Forge client events, key mappings, and vanilla `useItemOn` placement.

It is designed as a **client-only** mod:

- No server-side installation required.
- No custom networking protocol.
- No extra blocks, items, or rendering systems.
- Placement assistance uses normal vanilla right-click interaction behavior.

> Use movement or placement assistance responsibly and check server rules before using it on multiplayer servers.

## Documentation Hub

| Status | File | What it covers |
| --- | --- | --- |
| Main | [README.md](README.md) | Project overview, features, commands, build steps, and repo layout. |
| Research | [docs/original-mod-analysis.md](docs/original-mod-analysis.md) | Behavior notes from inspecting the original SafeWalk `1.8.9` jar. |
| Porting | [docs/porting-map.md](docs/porting-map.md) | Mapping reference from legacy Forge/Minecraft APIs to the `1.21.1` rewrite. |

## Feature Set

| Area | Details |
| --- | --- |
| Toggle | Default keybind is `V`. |
| Commands | `/safewalk`, `/sw`, `/sb`, `/speedbridge`. |
| Modes | `SNEAK`, `BREEZILY`, and `SCAFFOLD`. |
| Placement | Optional block placement assistance with vanilla client interaction. |
| Safety toggles | Optional disable-on-jump and disable-on-fall behavior. |
| Chat | Optional local status messages. |
| Multiplayer fit | `displayTest="IGNORE_SERVER_VERSION"` for client-only use. |

## Modes

| Mode | Behavior | Best fit |
| --- | --- | --- |
| `SNEAK` | Holds sneak when the player reaches an edge. | Conservative edge safety. |
| `BREEZILY` | Slows horizontal movement near edges and can place at the crosshair. | Controlled bridge movement. |
| `SCAFFOLD` | Attempts to place against the nearest neighbor block below the player. | Faster placement assistance while preserving vanilla interaction flow. |

<details>
<summary><strong>Command Reference</strong></summary>

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

</details>

<details>
<summary><strong>Requirements</strong></summary>

| Requirement | Version |
| --- | --- |
| Minecraft | `1.21.1` |
| Forge | `52.x` |
| Java | `21` |
| Build system | Gradle / ForgeGradle |

</details>

## Build

From the repository root:

```powershell
.\gradlew build
```

Built jars are written to:

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

## Technical Notes

- The mod checks whether the local player is over an edge by inspecting the block below the player.
- Forced sneak is released when the player leaves the edge state, opens a screen, disables the mod, or manually sneaks.
- Auto-place only runs when the held main-hand item is a `BlockItem`.
- Scaffold placement looks for a nearby neighbor block and uses a constructed `BlockHitResult` for normal placement.
- Config is stored through a Forge client config spec.

## Ownership

Maintained by **Nayak Indie**.  
This is a modern Forge rewrite inspired by the original `1.8.9` SafeWalk behavior.

## License

Released under the [MIT License](LICENSE).
