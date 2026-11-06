# Version 1.0.8 hotfix validation

Core SHA-256: `cc98faaf0214f85c5c251c0c7edd63539d1d02f8c7048c3eace180e9da119ebb`

The hotfix disables only Distant Horizons' separate block-cloud pass once. Skybox clouds, Dramatic Skys, ForgeSkyboxes, shaders, distant terrain, beacons and all other settings remain. The same marker covers automatic Prism, the manual updater and fresh installs; a later deliberate cloud choice is preserved.

Independent comparison with published 1.0.7 verified the client changes and exercised the actual release migration class against a copied 1.0.7 config. The server changes only its outgoing optional-mod status metadata for Distant Horizons and ForgeSkyboxes; login/gameplay classes and channel data are unchanged. Actual-class standalone fixtures passed. No new Minecraft gameplay test or measured RAM/FPS claim is made for this bounded hotfix.

Six real-payload manual updater fixtures from 1.0.5, 1.0.6 and 1.0.7 passed, including repeats, later cloud choices, quoted TOML keys/tables, custom shaders/graphics, completed memory choices, local generated textures and save sentinels. All publisher pins, 1.0.7 content and world files remain unchanged. Only the documented cloud key, UI/source files, server status/source files and update minimum change.
