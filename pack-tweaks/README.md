# Michael's Pack Tweaks and Zombie Tactics backport

Minecraft 1.20.1 / Forge 47.4.28. Build with Java 17: `./gradlew build` (Windows: `gradlew.bat build`).
The matching binary is `michaels-pack-tweaks-1.0.0.jar`.

Zombie Tactics is by n643064: https://github.com/n643064/ZombieTactics
Original source revision: 6f62bf477fe15c2310517443cba5d7acba5e9fbd. Original declared license: GNU GPL 3.0.
This modified Forge backport retains vanilla AI, fixes marker placement/server spawning, and reduces marker range and mining aggressiveness.
Other pack additions fix loading, paired chest loot, pause configuration refresh, wild Tremorzilla targeting and configurable Dragon health (default 400 HP).
Validation probes are operator-only and extra test behavior requires `-Dmichaelspack.validation=true`; that flag is absent from release launch settings.

The combined Java mod is distributed under GPL version 3; see LICENSE.txt. Third-party Minecraft/Forge APIs retain their own licenses.
Resource overrides adapted from other mods remain governed by their respective original licenses; see the release mod list for authors and project links.
