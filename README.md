# michael's modpack

For Windows 10/11, Minecraft Java Edition, and our six-player friends' server.

## Install and play

1. Download **[michaels-modpack-1.0.2-windows.zip](https://github.com/mwin96/michaels-modpack/releases/download/v1.0.2/michaels-modpack-1.0.2-windows.zip)**.
2. Right-click the ZIP → **Extract All**. Keep the extracted folder together.
3. Open **PLAY.cmd**. Sign into your own Microsoft account when Prism asks; it must own Minecraft Java Edition.

Prism downloads Java, Minecraft, Forge and the pinned modpack, then joins the server. If the first sign-in leaves you at the launcher, double-click **michael's modpack** once. Later, PLAY.cmd launches and joins automatically.

The first launch downloads roughly 866 MB of pack files, plus Minecraft and Java. Give it a few minutes. Your thumbnail is already the launcher's pack icon, and the game menu is branded.

**Server: `51.81.148.186:25590`** — six players, no whitelist, Minecraft account authentication enabled.

The profile allocates up to 6 GB of RAM; 16 GB total system RAM is recommended. Keep the folder in a normal writable location such as Documents. The folder contains an unmodified official Prism Launcher and the verified pack configuration; your Microsoft sign-in stays in Prism.

## Playing

- You start in the natural wilderness with **no starter items or compulsory guidebooks**. Collect wood, stone and food as usual. Rats and the other added systems are optional.
- Ordinary Minecraft movement, inventory, hearts/hunger and automatic item pickup.
- Vanilla progression: blaze rods and pearls → Eyes of Ender → stronghold portal → Dragon. Added bosses are optional encounters.
- **The Ender Dragon has 400 HP, twice vanilla health**, with normal fight mechanics.
- **R reloads guns**, G changes fire mode, H inspects, Z opens attachments, V melees, B zooms and C crawls. Use **O** to interact with chests/doors while holding a gun, or switch to another item for normal right-click. Curios equipment: J. World map: M. New waypoint: N. See **CONTROLS.md**.
- Backrooms is an **optional trip**: craft two **Backrooms Portals** using eight paper around one Ender Pearl. Place one to enter and carry the spare to leave; exits return near the entry point and preserve your bed. Portals have a brief travel cooldown.
- Regular zombie mining can break dirt/glass, while solid wood and stone walls remain useful shelter. The Man appears at night; doors and glass can still be breached. Other enemies and disasters retain their threats.
- Craft guns at the **Gun-Smith Table** and ammunition at the separate **Ammo Workbench**, using vanilla materials. Keep iron, copper and gunpowder. Guns and matching ammo can also appear in dungeon/structure loot; Lootr gives each player personal chest contents. JEI shows recipes and uses.
- The minimap starts small, with entity radar, tracked players and coordinate/status rows hidden. **M** opens the full map. The advanced geology overlay starts off; **F4** toggles it.
- **MakeUp Ultra Fast is ON by default**, using the minimal No Effects profile: shadows, reflections, volumetric clouds, bloom, motion blur and depth of field are off. Shader hotkeys are unbound. Change it through Options → Video Settings → Shader Packs.
- Physics Mod affects ocean waves only. Blocks, dirt, mobs, items and cloth keep their ordinary visuals.
- Rendering defaults to six chunks and a 120 FPS cap. If a weaker PC needs more performance, reduce resolution/render distance or turn the shader off.
- Game master volume starts at 100%. If sound is muted in Windows, unmute the game there.
- Server world simulation pauses when empty and resumes when someone joins. Monthly hosting remains a monthly charge.

The Backrooms mod's first-run photosensitivity notice is retained. Read it on first launch.

## Validation

The core pack passed an independent Astra high review of 1.0.0, followed by a focused review of the 1.0.1 gameplay fixes: real client/server join, normal portal progression, Dragon death/400 HP respawn, paired gun loot through actual Lootr menus, automatic dirt pickup, default-on shader/ocean/gun/model rendering, custom menu and effective key bindings. The exact 1.0.2 client also passed fresh-player empty-inventory, clean-map, overlay-toggle and normal-default checks. See **VALIDATION.md** for coverage and limits.

A matched 1080p shader ON/OFF/ON test on an RTX 5070 Ti measured 118 FPS median in each phase, capped at 120. This showed no noticeable FPS drop from the minimal shader in that test. The weakest friend's hardware is unknown; **60–100+ FPS on that PC has not been measured**. Full four-player survival, overnight stability and some multipart boss encounters are not claimed tested. See **VALIDATION.md** for the exact coverage and limits.

The hosted server booted successfully with all 76 expected server mod files, answered an external Minecraft status request with six slots, and held game time at zero across samples 95 seconds apart while empty.

## Other files

- `michaels-modpack-1.0.2.mrpack`: standard import for an existing Prism installation (Add Instance → Import). Use the Windows ZIP above for the prepared launcher.
- `michaels-modpack-1.0.2-server-setup.zip`: pinned publisher downloader and production defaults.
- `michaels-pack-tweaks-1.0.2-source.zip`: corresponding GPL source for the compatibility mod/Zombie Tactics backport.
- `MOD-LIST.md`, `CONTROLS.md`, `VALIDATION.md`, and `SHA256SUMS.txt`: contents, controls, evidence and checksums.

The original review checkboxes were preserved. Compatibility substitutions are recorded in MOD-LIST.md. Third-party mods download from their publishers; the starter includes official Prism and installer components with licenses/source information, plus this pack's own files. No account credentials or login sessions are supplied.
