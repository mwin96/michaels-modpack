# Validation — 1.0.3

Exact MRpack SHA256: `601ab4bc543daedd224aafe0ad053ee6bd7f24d97858ee27744c62fc4ca82abf`.

The reviewed server utility passed 27 consecutive melee hits with normal timing on all three statues, nine death/removal checks, and all 26 spider scaling/reload checks (native 1HP floor retained). Empty-tag stacking, meaningful gun NBT, Forge capabilities and status compatibility regressions passed. The additional pause hooks passed a real empty/join/resume/disconnect/pause test; all prior gameplay classes are byte-identical. Matching GPL sources accompany the custom utilities and equipment.

All 28 equipment recipes and models, sixteen native weapon attacks, armor/paxel behavior, real chest/Lootr rarity rolls and actual TaCZ bullet bonuses passed. The normal-animal resource variant preserves the other retained assets and includes reproducible GPL source.

The final utility retains the previously tested Siren, combat and pause gameplay classes byte-for-byte; its outgoing status-only helper additionally identifies five proven server-only structure mods. Login/channel checks remain unchanged. Native Siren tests prove normal weapons and kills on both 180HP forms, 300 protected blocks, 30-second departure and one-player-kill cleanup. The former 500-damage attack deals 2.24 damage through diamond armor. Native multiplayer tests prove friend distances nearby/beyond entity tracking, cross-dimension labels and logout cleanup, plus old-death cleanup with manual/current markers retained.

The client updater passed isolated Windows PowerShell tests for installation, preference/key preservation, idempotence, running-game refusal, download integrity and invalid-config rejection. New publisher downloads are hash-pinned. Existing 1.0.2 assets are unchanged.

The final combination joined a writable clone of the preserved real world, including all server-only terrain/structure mods without installing them on clients. The preservation audit proves 838 inhabited chunks plus a 256-block buffer, complete neighboring structures and all retained payloads/timestamps unchanged; 15,069 zero-inhabited chunks are available for regeneration. User/player files remain unchanged; only the unused Wither Storm dimension descriptor and its removed-mod datapack entry change in level.dat. Native traversal verifies new terrain and structures.

The first Dragon fight uses Ender Trigon at exactly 400HP, with twelve native parts and working gun impacts in all three head/neck regions. Its normal death animation opens the portal and no bonus boss spawns. Heart upgrades pass 20HP to 40HP, limit handling and persistence through death.

Native wildlife/models, compact Jade, shaders, loot and the custom title pass. Testing stays muted: audio checks cover resolved gains and warning attenuation rather than listening. Existing graphics, chunk distances and every existing heap cap are preserved, including the old 6144MB default. Fresh installs use the separately tested 4096MB profile. The weakest friend's actual PC has not been benchmarked.
