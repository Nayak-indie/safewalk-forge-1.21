# 1.8.9 to 1.21.1 Forge Mapping

| 1.8.9 concept | 1.21.1 Forge rewrite |
| --- | --- |
| `Minecraft.getMinecraft()` | `Minecraft.getInstance()` |
| `EntityPlayerSP` | `LocalPlayer` |
| `WorldClient` | `ClientLevel` / `Level` from `Minecraft.level` |
| `PlayerControllerMP#onPlayerRightClick` | `Minecraft.gameMode.useItemOn(...)` |
| `KeyBinding` | `KeyMapping` |
| `KeyBinding#setKeyBindState` | `KeyMapping#setDown` on `Minecraft.options.keyShift` |
| `InputEvent.KeyInputEvent` | `ClientTickEvent` plus `KeyMapping#consumeClick` |
| `TickEvent.PlayerTickEvent` | `TickEvent.ClientTickEvent` guarded to `Phase.END` |
| `LivingJumpEvent` | Client tick transition on `Minecraft.options.keyJump.isDown()` |
| `BlockPos`, `EnumFacing` | `BlockPos`, `Direction` |
| `MovingObjectPosition` | `HitResult` / `BlockHitResult` |
| `ItemBlock` | `BlockItem` |
| `Configuration` | `ForgeConfigSpec` client config |

The port is still Forge-only and client-only. `mods.toml` uses `displayTest="IGNORE_SERVER_VERSION"` and client-side dependencies so the mod can be present on a multiplayer client without requiring installation on the server.
