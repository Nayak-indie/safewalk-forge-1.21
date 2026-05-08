# SafeWalk 1.8.9 Jar Analysis

Inspected jar: `1-8-9-safewalk-e2080.jar`

SHA-256: `1C35B1E20879C5825032CC2618AD18BEBCFCA92D6A2ACEC8AB20FB536573423E`

## Plain Logic

The original is a Forge client mod for Minecraft 1.8.9. It registers a toggle key and client commands (`/safewalk`, `/sw`, `/sb`, `/speedbridge`). When enabled, it checks the block below the local player. If the player is standing at an edge, it either holds sneak, slows movement and attempts timed block placement, or automatically chooses a neighboring block face and places a block in a scaffold-style mode.

The original does not add content or change rendering. Its behavior is local player movement/input automation:

- Reads the local player position, movement, held item, crosshair hit result, and world block states.
- Presses/releases the client sneak keybinding.
- Optionally calls the normal client player-controller block interaction method.
- In scaffold mode, temporarily changes local yaw/pitch to face a block, places, swings, and restores view.
- Optionally disables itself when a jump or fall is detected.
- Runs an update check against a Dropbox JSON URL and displays a clickable update link.

## Timing and Events

- Forge initialization: registers keybinding, event handlers, and client command.
- Client key input: toggles SafeWalk and tracks whether the user is manually holding sneak.
- Player tick: applies edge detection and mode behavior.
- Living jump event: optionally disables the mod when the local player jumps.
- Client connected event: displays update/toggle-sneak compatibility messages.

## Old Classes Accessed

- `net.minecraft.client.Minecraft`
- `net.minecraft.client.entity.EntityPlayerSP`
- `net.minecraft.client.multiplayer.WorldClient`
- `net.minecraft.client.multiplayer.PlayerControllerMP`
- `net.minecraft.client.settings.KeyBinding`
- `net.minecraft.util.BlockPos`
- `net.minecraft.util.EnumFacing`
- `net.minecraft.util.MovingObjectPosition`
- `net.minecraft.item.ItemBlock`
- Forge events under `net.minecraftforge.fml.common.gameevent`, `net.minecraftforge.event.entity.living`, and `net.minecraftforge.fml.common.network`

## Client Side

The mod is effectively client-side. It uses client-only Minecraft classes and registers client commands/keybinds. Auto placement still causes normal vanilla interaction packets because block placement cannot happen purely locally in multiplayer, but there is no custom networking protocol in the original jar.

## Porting Intent

The rewrite preserves the high-level behavior while avoiding direct view spoofing and position manipulation. It uses modern Forge key mappings, client tick handling, client commands, Mojang mappings, and vanilla `gameMode.useItemOn` for optional placement.
